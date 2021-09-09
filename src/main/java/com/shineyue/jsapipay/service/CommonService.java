package com.shineyue.jsapipay.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shineyue.jsapipay.config.JsapiConfig;
import com.shineyue.jsapipay.utils.CacheManager;
import com.shineyue.jsapipay.utils.CommonUtil;
import com.shineyue.jsapipay.utils.RedisUtil;
import com.shineyue.jsapipay.utils.SignUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;

@Service
public class CommonService {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private RedisUtil redisUtil;

	private final static Logger log = LoggerFactory.getLogger(CommonService.class);

	public static void main(String[] args) {
		// log.info(JsapiConfig.getPrivateKey().toString());
		// getCertificateOfPlatform();
		log.info("\r\n" + getCodeUrl());
		// log.info(("\r\n" + getAccess_tokenUrl("001FZEGa18hKAB0v5rGa1kbZ9v1FZEGQ")));
		// log.info(CommonUtil.getRandomString(28));
		// getAccessToken();
		// getJsapiTiket();
	}

	/**
	 * 初次获取平台证书
	 * 
	 * @return
	 */
	public static String getCertificateOfPlatform() {

		CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
				.withMerchant(JsapiConfig.MERCHANTID, JsapiConfig.MERCHANTSERIALNUMBER, JsapiConfig.getPrivateKey())
				.withValidator(response -> true).build();

		URIBuilder uriBuilder = null;
		String bodyAsString = "";
		try {
			uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/certificates");
			HttpGet httpGet = new HttpGet(uriBuilder.build());
			httpGet.addHeader("Accept", "application/json");
			try {
				CloseableHttpResponse response = httpClient.execute(httpGet);
				bodyAsString = EntityUtils.toString(response.getEntity());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(bodyAsString);
		return bodyAsString;
	}

	public List<X509Certificate> getX509CertificateList(String apiV3Key, String body) {
		List<X509Certificate> newCertList = new ArrayList<>();

		// 将apiV3Key 转为 byte 数组

		try {
			newCertList = deserializeToCerts(null, null);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newCertList;
	}

	/**
	 * JSAPI 下单
	 * 
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public Map<String, String> placeOrder() throws IOException, GeneralSecurityException {

		String bodyAsString = "";
		String generateSignature = "";
		// 反序列化平台证书并解密
		List<X509Certificate> wechatpayCertificates = deserializeToCerts(JsapiConfig.getAPIv3Key(),
				JsapiConfig.CERTIFICATEBODY);

		CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
				.withMerchant(JsapiConfig.MERCHANTID, JsapiConfig.MERCHANTSERIALNUMBER, JsapiConfig.getPrivateKey())
				.withWechatpay(wechatpayCertificates).build();
		HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
		httpPost.addHeader("Accept", "application/json");
		httpPost.addHeader("Content-type", "application/json; charset=utf-8");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectMapper objectMapper = new ObjectMapper();
		String outTradeNo = CommonUtil.getRandomString(28);
		log.info("订单号为" + outTradeNo);

		ObjectNode rootNode = objectMapper.createObjectNode();
		rootNode.put("mchid", JsapiConfig.MCHID).put("appid", JsapiConfig.GZH_APPID)
				.put("description", "Image形象店-深圳腾大-QQ公仔").put("notify_url", JsapiConfig.NOTIFY_URL)
				.put("out_trade_no", outTradeNo);
		rootNode.putObject("amount").put("total", 1);
		rootNode.putObject("payer").put("openid", "onriNxBISVmRjChACUme-xhbu3GU");

		try {
			objectMapper.writeValue(bos, rootNode);
			httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
			CloseableHttpResponse response = httpClient.execute(httpPost);
			bodyAsString = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(bodyAsString);

		String timestamp = SignUtil.create_timestamp();

		String nonceStr = SignUtil.create_nonce_str();

		Map<String, String> waitToSign = new HashMap<String, String>();

		JSONObject bodyObj = (JSONObject) JSONObject.parse(bodyAsString);
		if (bodyAsString.contains("prepay_id")) {
			waitToSign.put("package", "prepay_id=" + bodyObj.getString("prepay_id"));
		} else {
			waitToSign.put("package", bodyAsString);
		}
		waitToSign.put("timestamp", timestamp);
		waitToSign.put("nonceStr", nonceStr);
		waitToSign.put("appId", JsapiConfig.GZH_APPID);
		// 获取签名
		// 第一步 构建签名串儿
		String waitToSignStr = JsapiConfig.GZH_APPID + "\n" + timestamp + "\n" + nonceStr + "\nprepay_id="
				+ bodyObj.getString("prepay_id") + "\n";
		// 第二步 计算签名值
		byte[] sign256 = SignUtil.sign256(waitToSignStr, JsapiConfig.getPrivateKey());

		// 第三步 Base64编码

		generateSignature = SignUtil.encodeBase64(sign256);

		waitToSign.put("paySign", generateSignature);
		waitToSign.remove("appId");

		return waitToSign;
	}

	/**
	 * 反序列化证书并解密
	 * 
	 * 暂时从AutoUpdateCertificatesVerifier中方法中拷过来，由于不知AutoUpdateCertificatesVerifier
	 * 中构造方法 Credentials 如何设置
	 */
	private List<X509Certificate> deserializeToCerts(byte[] apiV3Key, String body)
			throws GeneralSecurityException, IOException {
		AesUtil decryptor = new AesUtil(apiV3Key);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataNode = mapper.readTree(body).get("data");
		List<X509Certificate> newCertList = new ArrayList<>();
		if (dataNode != null) {
			for (int i = 0, count = dataNode.size(); i < count; i++) {
				JsonNode encryptCertificateNode = dataNode.get(i).get("encrypt_certificate");
				// 解密
				String cert = decryptor.decryptToString(
						encryptCertificateNode.get("associated_data").toString().replaceAll("\"", "").getBytes("utf-8"),
						encryptCertificateNode.get("nonce").toString().replaceAll("\"", "").getBytes("utf-8"),
						encryptCertificateNode.get("ciphertext").toString().replaceAll("\"", ""));

				CertificateFactory cf = CertificateFactory.getInstance("X509");
				X509Certificate x509Cert = (X509Certificate) cf
						.generateCertificate(new ByteArrayInputStream(cert.getBytes("utf-8")));
				try {
					x509Cert.checkValidity();
				} catch (CertificateExpiredException | CertificateNotYetValidException e) {
					continue;
				}
				newCertList.add(x509Cert);
			}
		}
		return newCertList;
	}

	/**
	 * 拼接获取code地址
	 * 
	 * @return
	 */
	public static String getCodeUrl() {
		return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + JsapiConfig.GZH_APPID + "&redirect_uri="
				+ JsapiConfig.REDIRECT_URI + "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
	}

	/**
	 * 拼接获取access_token地址
	 * 
	 * @param code
	 * @return
	 */
	public static String getAccess_tokenUrl(String code) {
		return "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + JsapiConfig.GZH_APPID + "&secret="
				+ JsapiConfig.GZH_SECRET + "&code=" + code + "&grant_type=authorization_code";
	}

	/**
	 * 查询订单
	 * 
	 * @param transaction_id 微信支付订单号 微信支付系统生成的订单号
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String queryOrder(String transaction_id) throws GeneralSecurityException, IOException, URISyntaxException {
		// 反序列化平台证书并解密
		List<X509Certificate> wechatpayCertificates = deserializeToCerts(JsapiConfig.getAPIv3Key(),
				JsapiConfig.CERTIFICATEBODY);

		CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
				.withMerchant(JsapiConfig.MERCHANTID, JsapiConfig.MERCHANTSERIALNUMBER, JsapiConfig.getPrivateKey())
				.withWechatpay(wechatpayCertificates).build();
		URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/pay/transactions/id/" + transaction_id
				+ "?mchid=" + JsapiConfig.MCHID);
		HttpGet httpGet = new HttpGet(uriBuilder.build());
		httpGet.addHeader("Accept", "application/json");

		CloseableHttpResponse response = httpClient.execute(httpGet);

		String bodyAsString = EntityUtils.toString(response.getEntity());
		log.info(bodyAsString);
		return bodyAsString;
	}

	/**
	 * 关闭订单
	 * 
	 * @param outtradeno 商户系统内部订单号
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public String closeOrder(String outtradeno) throws GeneralSecurityException, IOException {

		// 反序列化平台证书并解密
		List<X509Certificate> wechatpayCertificates = deserializeToCerts(JsapiConfig.getAPIv3Key(),
				JsapiConfig.CERTIFICATEBODY);

		CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
				.withMerchant(JsapiConfig.MERCHANTID, JsapiConfig.MERCHANTSERIALNUMBER, JsapiConfig.getPrivateKey())
				.withWechatpay(wechatpayCertificates).build();
		HttpPost httpPost = new HttpPost(
				"https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/" + outtradeno + "/close");
		httpPost.addHeader("Accept", "application/json");
		httpPost.addHeader("Content-type", "application/json; charset=utf-8");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectMapper objectMapper = new ObjectMapper();

		ObjectNode rootNode = objectMapper.createObjectNode();
		rootNode.put("mchid", JsapiConfig.MCHID);

		objectMapper.writeValue(bos, rootNode);

		httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
		CloseableHttpResponse response = httpClient.execute(httpPost);

		String bodyAsString = EntityUtils.toString(response.getEntity());
		log.info(bodyAsString);
		return bodyAsString;
	}

	/**
	 * 申请退款
	 * 
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@SuppressWarnings("deprecation")
	public String refund(JSONObject refund) throws GeneralSecurityException, IOException {
		// 反序列化平台证书并解密
		List<X509Certificate> wechatpayCertificates = deserializeToCerts(JsapiConfig.getAPIv3Key(),
				JsapiConfig.CERTIFICATEBODY);

		CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
				.withMerchant(JsapiConfig.MERCHANTID, JsapiConfig.MERCHANTSERIALNUMBER, JsapiConfig.getPrivateKey())
				.withWechatpay(wechatpayCertificates).build();
		HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds");
		httpPost.addHeader("Accept", "application/json");
		httpPost.addHeader("Content-type", "application/json; charset=utf-8");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectMapper objectMapper = new ObjectMapper();

		ObjectNode rootNode = objectMapper.createObjectNode();
		ObjectNode amountNode = objectMapper.createObjectNode();
		// 商户订单号
		rootNode.put("out_trade_no", refund.getString("out_trade_no"));
		// 退款单号
		rootNode.put("out_refund_no", refund.getString("out_refund_no"));

		amountNode.put("refund", Integer.parseInt(refund.getString("refund")));
		amountNode.put("total", Integer.parseInt(refund.getString("total")));
		// 币种
		amountNode.put("currency", refund.getString("currency"));
		rootNode.put("amount", amountNode);

		objectMapper.writeValue(bos, rootNode);

		httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
		CloseableHttpResponse response = httpClient.execute(httpPost);

		String bodyAsString = EntityUtils.toString(response.getEntity());
		log.info(bodyAsString);
		return bodyAsString;

	}

	/**
	 * 获取jsapiTiket方法封装
	 * 
	 * @return
	 */
//	public String getJsapiTiket() {
//		// 判断缓存中是否有jsapiTiket值
//		String jsapiTiketHc = cacheManager.getData("jsapiTiket");
//		String jsapiTiket = "";
//		if (jsapiTiketHc == null) {
//			String accessToken = "";
//			String accessTokenHc = cacheManager.getData("accessToken");
//			// 判断缓存中是否有accesstoken值
//			if (accessTokenHc == null) {
//				accessToken = getAccessToken();
//				JSONObject accessTokenObj = JSON.parseObject(accessToken);
//				String access_token = accessTokenObj.getString("access_token");
//				int expires_in = accessTokenObj.getInteger("expires_in");
//				accessToken = access_token;
//
//				// 缓存数据
//				// 提前一分钟刷新
//				cacheManager.setData("accessToken", access_token, expires_in - 60);
//			} else {
//				accessToken = accessTokenHc;
//			}
//
//			jsapiTiket = getJsapiTiket(accessToken);
//			JSONObject jsapiTiketObj = JSON.parseObject(jsapiTiket);
//
//			String ticket = jsapiTiketObj.getString("ticket");
//			int expires_in = jsapiTiketObj.getInteger("expires_in");
//
//			jsapiTiket = ticket;
//			// 缓存数据
//			// 提前一分钟刷新
//			cacheManager.setData("jsapiTiket", jsapiTiket, expires_in - 60);
//		} else {
//			jsapiTiket = jsapiTiketHc;
//		}
//
//		return jsapiTiket;
//
//	}

	/**
	 * map存 改爲 redis存
	 * 
	 * @return
	 */
	public String getJsapiTiket() {
		// 判断缓存中是否有jsapiTiket值
		boolean haskey = redisUtil.hasKey("jsapiTiket");
		String jsapiTiket = "";
		if (haskey) {
			log.info("緩存中取jsapiTiket");
			jsapiTiket = (String) redisUtil.get("jsapiTiket");

		} else {
			String accessToken = "";
			boolean hasToken = redisUtil.hasKey("accessToken");
			// 判断缓存中是否有accesstoken值
			if (hasToken) {
				log.info("緩存中取accessToken");
				accessToken = (String) redisUtil.get("accessToken");

			} else {
				accessToken = getAccessToken();
				JSONObject accessTokenObj = JSON.parseObject(accessToken);
				String access_token = accessTokenObj.getString("access_token");
				int expires_in = accessTokenObj.getInteger("expires_in");
				accessToken = access_token;

				// 缓存数据
				// 提前一分钟刷新
				redisUtil.set("accessToken", access_token, expires_in - 60);
			}

			jsapiTiket = getJsapiTiket(accessToken);
			JSONObject jsapiTiketObj = JSON.parseObject(jsapiTiket);

			String ticket = jsapiTiketObj.getString("ticket");
			int expires_in = jsapiTiketObj.getInteger("expires_in");

			jsapiTiket = ticket;
			// 缓存数据
			// 提前一分钟刷新
			redisUtil.set("jsapiTiket", jsapiTiket, expires_in - 60);

		}

		return jsapiTiket;

	}

	/**
	 * access_token是公众号的全局唯一接口调用凭据 -->获取access_token
	 */
	public static String getAccessToken() {

		String bodyAsString = "";
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
				+ JsapiConfig.GZH_APPID + "&secret=" + JsapiConfig.GZH_SECRET;
		String accept = "application/json";

		bodyAsString = CommonUtil.dogetone(url, accept);

		return bodyAsString;
	}

	/**
	 * jsapi_ticket是公众号用于调用微信JS接口的临时票据 -->获取jsapi_ticket 从微信
	 */
	public static String getJsapiTiket(String accessToken) {

		String bodyAsString = "";
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
		String accept = "application/json";

		bodyAsString = CommonUtil.dogetone(url, accept);
		log.info(bodyAsString);

		return bodyAsString;
	}

	/**
	 * 发放红包接口 V2
	 * 
	 * @throws Exception
	 * 
	 */

	public String sendredpack(String ip) throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		Map<String, String> header = new HashMap<String, String>();

		// 随机字符串
		String nonce_str = CommonUtil.getRandomString(32);
		paramMap.put("nonce_str", nonce_str);
		// 商户订单号
		String mch_billno = CommonUtil.getRandomString(28);
		paramMap.put("mch_billno", mch_billno);
		// 商户号
		String mch_id = JsapiConfig.MCHID;
		paramMap.put("mch_id", mch_id);
		// 公众号 appid
		String wxappid = JsapiConfig.GZH_APPID;
		paramMap.put("wxappid", wxappid);
		// 商户名称
		String send_name = "神玥伟奥互联网";
		paramMap.put("send_name", send_name);
		// 用户openid 先写死
		String re_openid = "onriNxBISVmRjChACUme-xhbu3GU";
		paramMap.put("re_openid", re_openid);
		// 金额 1元
		String total_amount = "100";
		paramMap.put("total_amount", total_amount);
		// 红包发放总人数
		String total_num = "1";
		paramMap.put("total_num", total_num);
		// 红包祝福语
		String wishing = "祝您中秋节快乐！";
		paramMap.put("wishing", wishing);

		// ip地址
		String client_ip = ip;
		paramMap.put("client_ip", client_ip);

		// 活动名称
		String act_name = "三行情诗活动";
		paramMap.put("act_name", act_name);

		String remark = "猜越多得越多，快来抢！";
		paramMap.put("remark", remark);

		String sign = SignUtil.generateSignature(paramMap, JsapiConfig.APIKEYSTR, "MD5");
		paramMap.put("sign", sign);

		// 拼装 xml 参数
		String mapToXml = CommonUtil.mapToXml(paramMap);

		// 请求地址
		String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";

		header.put("Content-Type", "application/xml;charset=utf8");

		// String result = CommonUtil.doPostOne(url, mapToXml, header);
		String result = CommonUtil.requestOnce(url, mapToXml, 2000, 2000, true);

		return result;
	}

}

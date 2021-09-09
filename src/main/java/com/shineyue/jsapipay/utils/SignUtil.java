package com.shineyue.jsapipay.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;

public class SignUtil {

	private static final String ENCODING = "UTF-8";
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

	public static Map<String, String> sign(String jsapi_ticket, JSONObject signDTO) {
		Map<String, String> ret = new HashMap<String, String>();
		String string1;
		String signature = "";
		String nonceStr = signDTO.getString("nonceStr");
		String timestamp = signDTO.getString("timestamp");
		String url = signDTO.getString("url");

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
		System.out.println(string1);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("jsapi_ticket", jsapi_ticket);
		ret.put("nonceStr", nonceStr);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String create_nonce_str() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	/**
	 * SHA256WithRSA签名
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] sign256(String data, PrivateKey privateKey) throws NoSuchAlgorithmException,
			InvalidKeySpecException, InvalidKeyException, SignatureException, UnsupportedEncodingException {

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		signature.initSign(privateKey);

		signature.update(data.getBytes(ENCODING));

		return signature.sign();
	}

	public static boolean verify256(String data, byte[] sign, PublicKey publicKey) {
		if (data == null || sign == null || publicKey == null) {
			return false;
		}

		try {
			Signature signetcheck = Signature.getInstance(SIGNATURE_ALGORITHM);
			signetcheck.initVerify(publicKey);
			signetcheck.update(data.getBytes("UTF-8"));
			return signetcheck.verify(sign);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 二进制数据编码为BASE64字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String encodeBase64(byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}

	/**
	 * BASE64解码
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] decodeBase64(byte[] bytes) {
		byte[] result = null;
		try {
			result = Base64.decodeBase64(bytes);
		} catch (Exception e) {
			return null;
		}
		return result;
	}

	/**
	 * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
	 *
	 * @param data     待签名数据
	 * @param key      API密钥
	 * @param signType 签名方式
	 * @return 签名
	 */
	public static String generateSignature(final Map<String, String> data, String key, String signType)
			throws Exception {
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			if (k.equals("sign")) {
				continue;
			}
			if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
				sb.append(k).append("=").append(data.get(k).trim()).append("&");
		}
		sb.append("key=").append(key);
		if ("MD5".equals(signType)) {
			return MD5(sb.toString()).toUpperCase();
		} else if ("HMACSHA256".equals(signType)) {
			return HMACSHA256(sb.toString(), key);
		} else {
			throw new Exception(String.format("Invalid sign_type: %s", signType));
		}
	}

	/**
	 * 生成 MD5
	 *
	 * @param data 待处理数据
	 * @return MD5结果
	 */
	public static String MD5(String data) throws Exception {
		java.security.MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 生成 HMACSHA256
	 * 
	 * @param data 待处理数据
	 * @param key  密钥
	 * @return 加密结果
	 * @throws Exception
	 */
	public static String HMACSHA256(String data, String key) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

}

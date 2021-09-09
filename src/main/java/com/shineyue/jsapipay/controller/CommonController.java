package com.shineyue.jsapipay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shineyue.jsapipay.entity.WxPayNotifyVO;
import com.shineyue.jsapipay.service.CommonService;
import com.shineyue.jsapipay.utils.SignUtil;

@RestController
@RequestMapping(path = "/jsapi")
public class CommonController {

	@Autowired
	private CommonService service;

	private final static Logger log = LoggerFactory.getLogger(CommonController.class);

	@RequestMapping(value = "/getTime")
	public long getTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 预支付
	 */
	@RequestMapping(value = "/preparePay", method = RequestMethod.POST)
	public Map<String, String> placeOrder() {
		Map<String, String> result = new HashMap<String, String>();
		try {
			result = service.placeOrder();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 退款
	 * 
	 * @return
	 */
	@RequestMapping(value = "/refunds", method = RequestMethod.POST)
	public String refunds(String Data) {
		JSONObject params = (JSONObject) JSONObject.parse(Data);

		String result = "";
		try {
			result = service.refund(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 发红包接口
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sendredpack", method = RequestMethod.POST)
	public String sendredpack(HttpServletRequest request) {
		String sendredpackResult = "";

		// 获取请求的ip地址
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.indexOf(",") != -1) {
			String[] ips = ip.split(",");
			ip = ips[0].trim();
		}
		log.info("ip:  " + ip);

		try {
			sendredpackResult = service.sendredpack(ip);
			log.info("发送红包返回：", sendredpackResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sendredpackResult;
	}

	/**
	 * 查询订单
	 * 
	 * @param transaction_id
	 * @return
	 */
	@RequestMapping(value = "/order_query", method = RequestMethod.POST)
	public String queryOrder(@RequestParam(value = "transaction_id", required = true) String transaction_id) {
		String result = "";

		try {
			result = service.queryOrder(transaction_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 支付结果通知 回调
	 * 
	 * @return
	 */
	@RequestMapping(value = "/callback", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject notify(HttpServletRequest request, @RequestBody WxPayNotifyVO param) {
		JSONObject result = new JSONObject();
		log.info(param.toString());

		// 如果支付成功
		result.put("code", "SUCCESS");
		result.put("message", "成功");

		return result;
	}

	/**
	 * 
	 * @param url 当前网页地址
	 * @return
	 */
	@RequestMapping(value = "/getSign", method = RequestMethod.POST)
	public Map getSign(HttpServletRequest request, HttpServletResponse response) {
		JSONObject params = JSONObject.parseObject(request.getParameter("params"));
		String jsapiTiket = "";
		// 调试阶段先注释 避免频繁调用 正式使用需要放开注释
		jsapiTiket = service.getJsapiTiket();
		log.info("参数：" + params.toJSONString());

		Map<String, String> ret = SignUtil.sign(jsapiTiket, params);
		for (Map.Entry entry : ret.entrySet()) {
			log.info(entry.getKey() + ", " + entry.getValue());
		}
		return ret;
	}

	/**
	 * 第二种方式 因getSign 报502错误
	 * 
	 * @param Data
	 * @return
	 */
	@RequestMapping(value = "/getSign2", method = RequestMethod.POST)
	public Map getSign2(String Data) {
		JSONObject params = (JSONObject) JSONObject.parse(Data);
		String jsapiTiket = "";
		// 调试阶段先注释 避免频繁调用 正式使用需要放开注释
		jsapiTiket = service.getJsapiTiket();
		log.info("参数：" + params.toJSONString());

		Map<String, String> ret = SignUtil.sign(jsapiTiket, params);
		for (Map.Entry entry : ret.entrySet()) {
			log.info(entry.getKey() + ", " + entry.getValue());
		}
		return ret;
	}

}

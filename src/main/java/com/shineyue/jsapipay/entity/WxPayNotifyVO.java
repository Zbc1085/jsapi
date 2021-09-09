package com.shineyue.jsapipay.entity;

import lombok.Data;

@Data
public class WxPayNotifyVO {

	private String id;
	private String create_time;
	private String resource_type;
	private String event_type;
	private Resource resource;
	private String summary;

	@Data
	static class Resource {
		private String algorithm;
		private String ciphertext;
		private String nonce;
		private String original_type;
		private String associated_data;
	}

}

package com.shineyue.jsapipay.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;

import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;

public class JsapiConfig {

	/**
	 * 商户号
	 */
	public static String MERCHANTID = "1482120652";

	/**
	 * 商户API证书的证书序列号
	 */
	public static String MERCHANTSERIALNUMBER = "441782149F1BC18121C033491CD9D9F547D8CFA1";

	/**
	 * 商户API私钥字符串
	 */
	public static String MERCHANTPRIVATEKEYSTR = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDiUFvf/t7eOc6w"
			+ "53JX6/u7hTDCrJ8miZmGMsFZJ/mcJPkj3aH99+QY0AHlag8URWRUd4XmSFoXkb0U"
			+ "MHui/4Th75f7b2e9HNDo5CwzXZr+rWxz0sGsWy9nKW/iBScU5DdMyMzC8272WkAn"
			+ "iMwdb9zQWYtM3siAoCLNUMrTS41zdeP1hh/8Y+GZm/DOqD8rBzdypcLOGhlib+Ww"
			+ "ChhNG7Y8nIP5j9OF/sV3JNv2ME2RoVmBNlBrmrvG1IlRrv34eOnq7HJFtF+ozB2/"
			+ "27I8CDPdRaOQ2NZZz8KOdbFS72VXSVx9vYtbswM91lgMJO5Rb4oK/LLykQS/wU94"
			+ "IaZO2mxbAgMBAAECggEAU7aJ5ZdvdSBI9mMGhsNBwKGJ4djcnfK0GVHY91O0HzAG"
			+ "AGBRNZJmi08WBHhwz3zDmgMEQqRsvt8n47nzYd3Gl5R60YTqCKgKFrugJffwB1i7"
			+ "DzTccxbft/c57Y8eX7nzQrHcHBjT6MiFhpoxdooUUfVtc6fM3tddSDkkbXg7AIPf"
			+ "FmiI0ryWFEjbS9oPVafWLENEETYkLR1WnvrGutdnPmCKOh4/ilyYkHDt+wjXydxx"
			+ "xQgNrwDpj+TkS8eCbs4PBYp6tqMKYUvC/sUWoiFgTyVZK1Eq/t/UeeKcgFd7M14j"
			+ "gVYx3kJmeQFx0TwKY4X6eQop0rD5p5Zbzx7nH3DzAQKBgQDxNzMyz4DKUtuWsvmQ"
			+ "AfudixgCzJdSIezMkQinuseUEqcJYOrhl0jfYQk7kWJqj/dyzi8Rs1t1gGWTyXLV"
			+ "JCMB8ZXoYKQXRdRHWA3jHt+Sbl6NAEO55PEGAIhjrE87FYmgenG0fukKRN6zUF90"
			+ "QXBjTzotFvUqMBlvm2TlV8txGwKBgQDwL1fQOh1aeOGZkT7BPgw3Km0+Bq7iVg9c"
			+ "feJ694PShXRigxi1mzL8JWzvfWwBmjwZqxbCy5u2X6AP3LfxmKpTextbUAUOnmuj"
			+ "Ltdna8+VHVF9Prdi3xNgF6mbw9s+Wr3IOdpz0wHkPRTYIh9I20wn/WqW9NFutvCD"
			+ "h40lWLflwQKBgCM5Lk7DIklpJAuzguHWiEmPICh4e7hQn+WXYC4uBirHNoEWF/Zt"
			+ "QwTrhI+gQUDSWQ34scLh7/8Oisaz6gLZzgrgnkVap1uGpOLuJsj9nyQg3cm1Oi6R"
			+ "EekQdtonNUKpeHlop+X1ik4nlGWW3Pl6/NBjp1kiOxa4A17a0Db1RQ31AoGBAMfr"
			+ "QDaMCN7De1SttwR2Nwi8Ac39FhhC9epQxfEAkPLsLmHJKQoQsrF6HWCCjyIOVTQw"
			+ "WnOtsmBs2/gYCevI3Q07jRvGUU9HweREGvt0m4ZMrwWG7HIZqDD1wTFsJtOSCCHf"
			+ "NdvuqMZnqS/1xwCHuK4Ym14nTSPQlgWFEJEjdX6BAoGAIZh3ocus3I5n+WDQU45/"
			+ "v2UkUpoeZ1KMUjxP8zXmMCwQ1Q0HZefj6PvSE+6Dn6kI8/rSbIWu5oFRO9yARcDl"
			+ "488E70SooQIDTWwdgK4CjpZn3O5dB9G39KLKZ/THeRzoVj8uaVp6hqVW/+PwhkTe" + "PFca3OkwjZmjQaQNrGyDvJY=";

	/**
	 * 商户API私钥
	 * 
	 * @return
	 */
	public static PrivateKey getPrivateKey() {
		System.out.println("api密钥字符串：" + MERCHANTPRIVATEKEYSTR);
		PrivateKey privateKey = null;
		try {
			privateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(MERCHANTPRIVATEKEYSTR.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(privateKey.toString());
		return privateKey;
	}

	public static String APIV3KEYSTR = "Ifwinngsikjhgtyuhj4588658f8hkr7h";

	public static String APIKEYSTR = "Ifwinngsikjhgtyuhj4567658f8hkr7h";

	/**
	 * APIv3密钥
	 * 
	 * @return
	 */
	public static byte[] getAPIv3Key() {
		byte[] bytes = APIV3KEYSTR.getBytes();
		return bytes;
	}

	/**
	 * 接口返回的平台证书body串
	 */
	public static String CERTIFICATEBODY = "{\"data\":[{\"effective_time\":\"2021-04-01T15:26:48+08:00\",\"encrypt_certificate\":{\"algorithm\":\"AEAD_AES_256_GCM\",\"associated_data\":\"certificate\",\"ciphertext\":\"Q9lZlnrGjnZQXHDQfciLekfgeIGVcgk3YlTKhm+w2DPBvkODCRSCagQGoWwMgzIhxWslKVZ0WAnZIK0EnuTu1FYDQTDuzO8CDmT8oFKsJNFm4A86LsR+uPGtzCRDdAsXN6jYzFrid18rXAaenS/SKw9KuJcn8bjSfTGxSZYrM7ZtwRA59XHiuxcBbOhY9sCFaxo9vWFty3eVMX5vtg5oqMDb9Aa//cApjui6Ie8DErnlb0ZHT2uFceE/2Mi67H+xJeTTcJ3h9m2M3GDua0RJ22r4mfaScSRUCQSlX+U5+hjOV8Dx/6U/aFymlzXjEGtkrhYBS+hD2zUuyFH9yaeSttw3mNsv3CtMyiBNVjjM/Gxzb/AsCvwSX5ibZMN0aAdzzC8xq5Jg+3f7mqs+waIyAXYJ+rDZ5WDpRs6zGmhGRlxtlDi/8Xjx9iFAIJ7ppBimlW5qJXrMudSmM2Ejp6WV37WChYjSRp7I5yFX4+HiGSnEf7mGghZOm78jHc2UTwFo8OcS1oSUmOikTnteKe4G5xhbJh04DZEMJPtoP9RTV4IIvO2XCxHlUIgURA/ei3dMWmqsOOne/ODi0GjNeldkYkoavBCah9N9p4UU54Md+//D6DhgQUfc6MD9lFWVNgfjwIRcg8aXWWHQbB8zmkS/W8zHcvDx2j386o0i97Kl4pfNZkzce4EqZd6b+ushJJSV+v57Q1RHOhhSWp9PXRF7Cn/gpU9tcZqWjkRGHjoirgXx7jUEcuwEsPIBMeMqHPadHY9YnKYNZooC8cBujqUseG0pnJ6NH1xIrhqVjSmhwGPiXZ1smAL1LBAjVqE1JpG4Atzr9+nI3J4Xtixkl4n5qspZgjV5a1uEqicWjG3pRWePxTACNcaHRXwHKzZSigqvwSSIGjRUUNgnN5aI17YqYECAbinxF50fr4260NAbr9FwbJkqwEHOJNcjLYbq3IZHgLx2b+sMDp3siasieX5vB3JH/FaOPZsCRiILIyQiAwPSj5Moai+5eHkzmHs7NWwcUfiYxg0gHhbsPKxV98wnvLJJDf6SM35Dek+mmcFvVQ1u/fcTPyHjDBC/LcE+Ikj8IhrDdv+4Y1MuGEv1IM9MabgEkkXPG7+RVA+TebNvUHo1KJ2FivSo2UcliOLtSWzzJK+TSOSKBAzDtqxI4jOsiMjnUSfK+XgA3655WHcCEqSAtjDUN3HJMwv70bqHSjbhM4RREQdEV9gGzaiko8rMwAnjj2Lt1nfD7L6NOs7Mey/s3u1ujaE9CPn7ofANRkQ3vOPydZhxaQ2k0+oACOTM7HXOn5O+PuFBYNkc1ND6QZGDAFvR3o/yQqVOJtdFaI048MMC7dlGrOTbX0+6EXFa2oVuO5cUIfkq/F7zYTwXWwg+v++8SPVLVsyU7fwvYgr476bM9wUvNx9PX2Pr8S81uXSEfohljxjFzgVa/JPsovSh++bGZZkkyPhHErgA2Hrtg+E1T+O1PheDO+0nyjISy5InWJKSAfMQh8/2ynhWiiUZM7meNrSz9qcow5o8vIi7Tpvtg9BGf9bEybxAfBCn3kEfXa1tmd9cy5uJPEdtREWy3pjx/WRQAAUexFCYv5uFxujVV19KNO+2zZhrZLxjirtV7+LfRes+3s7qx8AJ/GoHIbo+5iLrkAvq0b3fWPYktNn0nEDUX9iTiApXdNLicWnddkLeYT/kc/VHxkqKEQ1LTQr5dufvs1slbEykWKp2BUAiiHT//aF8nXybVkuXi+NASqD7VAN/Rd+N2XAe/g4MnVAT5i4tbbg0lRc3FkgzOExQSF/072v2+TxrbQIb7D3o8Q6PR/sKQW73sbe+ijo8Q6A2Huko5mop9jEtFi5wa8MlnXRXXKcpMdSfvr4dh16tLjYTpQ==\",\"nonce\":\"5ab6d2be5d1f\"},\"expire_time\":\"2026-03-31T15:26:48+08:00\",\"serial_no\":\"34087E4737BBEC253442909208D1C5C8855CC8BF\"}]}";

	/**
	 * appid
	 */
	public static final String APPID = "wx3b81bd5fce929ffc";

	/**
	 * 商户号
	 */
	public static final String MCHID = "1482120652";

	/**
	 * 异步回调方法
	 */
	public static final String NOTIFY_URL = "https://appcs.sjgjj.cn/yuexiang/jsapi/callback";

	/****************
	 * 以下为公众号配置
	 * 
	 ****************/

	/**
	 * 公众号appId
	 */
	public static String GZH_APPID = "wx24aedcf6475f34a0";

	/**
	 * 公众号的secret
	 */
	public static String GZH_SECRET = "96b7282734624ea0c78caeaf4db93ec4";

	/**
	 * redirect地址
	 */
	public static String REDIRECT_URI = "https%3A%2F%2Fappcs.sjgjj.cn%2Fyuexiang%2FNewFile.html";

	/**
	 * 获取证书
	 * 
	 * @return
	 */
	public static InputStream getCertStream() {
		// TODO Auto-generated method stub
		String certPath = "D:\\Program Files\\Tencent\\zhengshugongju\\WXCertUtil\\cert\\apiclient_cert.p12";
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(certPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fis;

	}

}

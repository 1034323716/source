package richinfo.attendance.SMS;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.MessageDigest;
import java.util.*;
import java.util.Map.Entry;

/**
 * 类名称:SignatureUtil
 * 类描述:签名工具类
 * 创建人:hetianhuai
 * 创建时间:2019/6/27 16:20
 * Version 1.0
 */
public class SignatureUtil {

	public static final String TOKEN = "9adkcieiq907a2pmli09";//用于生成数字签名

	/**
	 * 创建SHA1签名
	 * @param params
	 * @return SHA1签名
	 */
	public static String createSignature(SortedMap<String, String> params) {
		String s = sha1Encrypt(sortParams(params));
		//System.out.println("签名是: "+s);
		return s;
        // return sha1Encrypt(sortParams(params));
	}

	/**
	 * 创建SHA1签名
	 * @param timeStamp
	 * @param nonce
	 * @param
	 * @return
	 */
	public static String createSignature(String timeStamp, String nonce) {
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("token", TOKEN);
		signParams.put("timeStamp", timeStamp);
		signParams.put("nonce", nonce);
		return createSignature(signParams);
	}

	public static String createSignature(String timeStamp, String nonce, String secretId) {
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("token", TOKEN);
		signParams.put("timeStamp", timeStamp);
		signParams.put("nonce", nonce);
		signParams.put("secretId", secretId);
		return createSignature(signParams);
	}

	/**
	 * 使用SHA1算法对字符串进行加密
	 * @param str
	 * @return
	 */
	public static String sha1Encrypt(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes("UTF-8"));
			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 生成时间戳
	 * @return
	 */
	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

	/**
	 * 生成随机字符串
	 * @return
	 */
	public static String getRandomStr() {
		return RandomStringUtils.randomAlphanumeric(6);
	}

    public static String getRandomStr(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

	/**
	 * 根据参数名称对参数进行字典排序
	 * @param params
	 * @return
	 */
	private static String sortParams(SortedMap<String, String> params) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> es = params.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = entry.getKey();
			String v = entry.getValue();
			sb.append(k + "=" + v + "&");
		}
		return sb.substring(0, sb.lastIndexOf("&"));
	}
}

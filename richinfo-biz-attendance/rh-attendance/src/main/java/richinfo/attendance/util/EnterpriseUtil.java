package richinfo.attendance.util;

import com.google.gson.Gson;
import com.google.gson.JsonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class EnterpriseUtil {
	private final static Logger logger = LoggerFactory
	        .getLogger(EnterpriseUtil.class);
	private static Gson gson=new Gson(); 
	
	/*APPID相当于通讯录的APPKEY*/
	private static String appkey = AttendanceConfig.getInstance().getProperty("attend.qytxl.appkey", "9fdcd721d954456b8c7ea53f80635456");
	private static String appsecret = AttendanceConfig.getInstance().getProperty("attend.qytxl.appsecret", "6af15ca383ee45dd959bf0e84d8eadac");
	private static String url = AttendanceConfig.getInstance().getProperty("attend.qytxl.callbackUrl", "http://121.15.167.235:10721/atdc/sync/getRegisterCallBackInfo");
	private static String postUrl = AttendanceConfig.getInstance().getProperty("qytxl.register.url", "https://open.cytxl.com.cn/enterprise/registerCallBack.json");
	private static String aeskey = AttendanceConfig.getInstance().getProperty("attend.qytxl.aes_key", "6af15ca383ee45dd");
	/**
	* 需要的长度
	* @param length
	*/
	public static String getNum(int length){
		//获取一个随机数
		double rand = Math.random();
		//将随机数转换为字符串
		String str = String.valueOf(rand).replace("0.", "");
		//截取字符串
		String newStr = str.substring(0, length);
		return newStr;
	}
    
    /**
	 * 发送https请求共用体 
	 */
	public  static com.alibaba.fastjson.JSONObject sendPost(String url, String parame, Map<String,Object> pmap) throws IOException, KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException{
		// 请求结果
		//JSONObject json = new JSONObject();
		JSONObject json = new JSONObject();
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		URL realUrl;
		HttpsURLConnection conn;
		String method = "POST";
		//查询地址
		String queryString = url;
		//请求参数获取
		String postpar = "";
		//字符串请求参数
		if(parame!=null){
			postpar = parame;
		}
		// map格式的请求参数
		if(pmap!=null){
			StringBuffer mstr = new StringBuffer();
			for(String str:pmap.keySet()){
				String val = (String) pmap.get(str);
				try {
					val=URLEncoder.encode(val,"UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
					mstr.append(str+"="+val+"&");
			}
			// 最终参数
			postpar = mstr.toString();
			logger.info("postpar={}",postpar);
			int lasts=postpar.lastIndexOf("&");
			postpar=postpar.substring(0, lasts);
		}
		if(method.toUpperCase().equals("GET")){
			queryString+="?"+postpar;
		}
		SSLSocketFactory  ssf= HttpsClientUtils.getSSFactory();
		try {
			realUrl = new URL(queryString);
			conn = (HttpsURLConnection)realUrl.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if(method.toUpperCase().equals("POST")){
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				out = new PrintWriter(conn.getOutputStream());
				out.print(postpar);
				out.flush();
			}else{
				conn.connect();
			}
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(),"utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			logger.info("sendPost result={}",result);
//			json = JSONObject.fromObject(result);
			json = JSONObject.parseObject(result);
		}finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
			}
		}
		return json;
	}
	
	public static String getNornmalSignature(HashMap<String, String> params, String appscret) {
		String signature = null;
		Map<String, String> sortedParams = new TreeMap<String, String>(params);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
		basestring.append(param.getKey())
		.append("=")
		.append(param.getValue() == null ? "" : param.getValue());
		}
		basestring.append(appscret);
		try {
		signature = DigestUtils.shaHex(basestring.toString());
		} catch (Exception e) {
		e.printStackTrace();
		}
		return signature;
	} 
	
	public static String getRregisterCallBack(String enterpriseId){
    	/*获取企业ID、请求方法、回调接口类型  默认调用*/
    	String method = "create";
    	String eventTypeString = "event_user_remove";
    	if(null == method){
    		method="create";
    	}
    	if(null == eventTypeString){
    		eventTypeString="event_user_remove";
    	}
    	logger.debug("enterpriseId:"+enterpriseId+",method:"+method+",eventType:"+eventTypeString);
    	String app_key= appkey;
    	String once = EnterpriseUtil.getNum(12);
    	String version = "2.0";
    	String channel = appkey;
    	String sdk_from = "java";
		JSONArray eventType = new JSONArray();
		eventType.add(eventTypeString);
		String departmentId = "";
		String token ="3ad8id23i907o2kmli03";
		String aes_key = null;
		try {
			aes_key = AESEncryptUtil.aesEncrypt(aeskey, "PKCS5Padding");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		HashMap<String,String> Map = new HashMap<String,String>();
		Map.put("app_key", app_key);
		Map.put("once", once);
		Map.put("version", version);
		Map.put("channel", channel);
		Map.put("sdk_from", sdk_from);
		Map.put("eventType", eventType.toString());
		Map.put("enterpriseId", enterpriseId);
		Map.put("departmentId", departmentId);
		Map.put("url", url);
		if(method.equals("create")||method.equals("update")){
			Map.put("token", token);
			Map.put("aes_key", aes_key);	
		}
		String signature = EnterpriseUtil.getNornmalSignature(Map, appsecret);
		
		StringBuffer params = new StringBuffer();
		params.append("app_key="+app_key);
		params.append("&once="+once);
		params.append("&version="+version);
		params.append("&channel="+channel);
		params.append("&sdk_from="+sdk_from);
		params.append("&signature="+signature);
		params.append("&eventType="+eventType);
		params.append("&enterpriseId="+enterpriseId);
		params.append("&departmentId="+departmentId);
		params.append("&url="+url);
		if(method.equals("create")||method.equals("update")){
			params.append("&token="+token);
			params.append("&aes_key="+aes_key);
		}
		String parame = params.toString();
		
    	try {
    		logger.debug("request params:"+parame);
			JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
			logger.debug("response result:"+responseJson);
			return responseJson.toString();
		} catch (Exception e) {
			logger.error("registerCallBack  is error!",e);
			return null;
		} 
	}
	
	/** 
     * @MethodName : toJson 
     * @Description : 将对象转为JSON串，此方法能够满足大部分需求 
     * @param src 
     *            :将要被转化的对象 
     * @return :转化后的JSON串 
     */  
    public static String toJson(Object src) {  
        if (src == null) {  
            return gson.toJson(JsonNull.INSTANCE);  
        }  
        return gson.toJson(src);  
    }
}

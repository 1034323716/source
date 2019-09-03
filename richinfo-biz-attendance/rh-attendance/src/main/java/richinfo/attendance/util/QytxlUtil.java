/**
 * 文件名：QytxlUtil.java
 * 创建日期： 2018年6月9日
 * 作者：     黄学振
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved
 */
package richinfo.attendance.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.RegisterCallBackResBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;

/**
 * 功能描述： 统一认证接口操作辅助类
 */
public class QytxlUtil
{
    private static Logger log = LoggerFactory.getLogger(QytxlUtil.class);
    private static volatile QytxlUtil instance;

    /**
     * 企业通讯录URL
     * 测试环境：https://api1.cytxl.com.cn/
     * 生产环境：https://open.cytxl.com.cn/
     * APPID相当于通讯录的APPKEY
     */
    
    /**
     * 企业通讯录注册回调方法
     */
    private String QYTXL_REGISTER_URL = AttendanceConfig.getInstance().getProperty(
        "qytxl.register.url", "https://open.cytxl.com.cn/enterprise/registerCallBack.json");

    /**
     * 获取部门的子部门及其直属联系人department_staff
     */
    private String QYTXL_DEPARTMENT_STAFF_URL = AttendanceConfig.getInstance().getProperty(
        "qytxl.department.staff.url", "https://open.cytxl.com.cn/enterprise/getDetail.json");
	/*获取用户所属的部门id*/
	private String QYTXL_EUSER_DEPARTMENT_ID_URL = AttendanceConfig.getInstance().getProperty(
			"qytxl.euser.department.id.url", "https://open.cytxl.com.cn/enterprise/getDeptIdsByEuserId.json");

	/*获取用户所属的部门id*/
	private String QYTXL_EUSER_ITEM_URL = AttendanceConfig.getInstance().getProperty(
			"qytxl.euser.department.id.url", "https://open.cytxl.com.cn/enterprise/getItem.json");
    /**
     * 企业通讯录删除回调方法
     */
    private String QYTXL_DELETE_URL = AttendanceConfig.getInstance().getProperty(
            "qytxl.deleter.url", "https://open.cytxl.com.cn/enterprise/deleteCallBack.json");
    
    private String QYTXL_UPDATE_URL = AttendanceConfig.getInstance().getProperty(
            "qytxl.update.url", "https://open.cytxl.com.cn/enterprise/updateCallBack.json");

    private String QYTXL_CALL_BACK_MSG_URL = AttendanceConfig.getInstance().getProperty(
        "qytxl.call.back.msg.url", "https://open.cytxl.com.cn/enterprise/getCallBack.json");

    private String QYTXL_ENTER_MSG_URL = AttendanceConfig.getInstance().getProperty(
        "qytxl.enter.msg.url", "https://open.cytxl.com.cn/enterprise/getEntInfo.json");

    private String QYTXL_CHECK_ENTER_IS_EXIST_URL = AttendanceConfig.getInstance().getProperty(
        "qytxl.enter.isexist.url", "https://open.cytxl.com.cn/enterprise/getEntInfo.json");

    /*
    * 根据uid查询contactId
    * */
    private String QYTXL_QUERY_CONTACT_ID_BY_UID_URL = AttendanceConfig.getInstance().getProperty(
        "qytxl.query.contactId.url", "https://open.cytxl.com.cn/enterprise/getContactIdByEuserId.json");

    /*
    * 根据enterid查询employees
    * */
    private String QYTXL_QUERY_EMPLOEES_BY_ENTERID_URL = AttendanceConfig.getInstance().getProperty(
        "qytxl.query.getQuery.url", "https://open.cytxl.com.cn/enterprise/getQuery.json");

    /**
     * 企业通讯录注册-AES_KEY加密  encryptKey
     */                       
    private String QYTXL_AES_KEY_ENCRYPTKEY = AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.aes_key.encryptKey",
            "PKCS5Padding");
    
    /**
     * 企业通讯录注册-AES_KEY
     */                       
    private static  String QYTXL_AES_KEY = AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.aes_key",
            "6af15ca383ee45dd");
    
    /**
     * 企业通讯录注册-appid
     */                       
    private String QYTXL_APPKEY = AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.appkey",
            "9fdcd721d954456b8c7ea53f80635456");
    /**
     * 企业通讯录注册-app_key
     */                       
    private String QYTXL_APPSECRET = AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.appsecret",
            "6af15ca383ee45dd959bf0e84d8eadac");
   
    
    /**
     * 企业通讯录注册-版本号
     */
    private String QYTXL_VERSION= AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.version",
            "2.0");

    /**
     * 企业通讯录注册-channel渠道 编号
     */
    private String QYTXL_CHANNEL = AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.channel",
            "9fdcd721d954456b8c7ea53f80635456");

    /**
     * 企业通讯录注册-调用接口语言类别
     */                       
    private String QYTXL_SDK_FROM = AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.sdk_from",
            "java");
    
    /**
     * 企业通讯录注册-eventType
     */                       
    private String QYTXL_EVENTTYPE= AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.eventType",
            "event_user_remove");
    
    /**
     * 企业通讯录注册-CALLBACK_URL
     */                       
    private String QYTXL_CALLBACK_URL= AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.callbackUrl",
            "http://120.197.235.114/atdc/sync/getRegisterCallBackInfo");
    
    /**
     * 企业通讯录注册-TOEKN
     */                       
    private String QYTXL_TOKEN= AttendanceConfig.getInstance()
        .getProperty("attend.qytxl.token",
            "3ad8id23i907o2kmli03");

    public static final String ENCODING_AES_KEY ="16e40d28631333dcaff078aa7c13a91c8762fca34ik";
    
    

    /**
     * 单例模式
     */
    public static QytxlUtil getInstance()
    {
        if (null == instance)
        {
            synchronized (QytxlUtil.class)
            {
                if (null == instance)
                {
                    instance = new QytxlUtil();
                    return instance;
                }
            }
        }
        return instance;
    }

    /**
     * 注册企业通讯录回调接口
     * @param
    * @param
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public RegisterCallBackResBean registerCallBack(String enterpriseId,
    		String method,String eventType) throws NoSuchAlgorithmException
    {
    	RegisterCallBackResBean resBean = new RegisterCallBackResBean();
   
        
        /*获取企业ID、请求方法、回调接口类型  默认调用*/
    
    	if(null == method){
    		method="create";
    	}
    	if(null == eventType){
    		eventType="event_user_remove";
    	}
    	
   	    //请求注册回调接口
    	String postUrl = "";
		if(method.equals("create")){
			postUrl = QYTXL_REGISTER_URL;
		}else if(method.equals("delete")){
			postUrl = QYTXL_DELETE_URL;
		}else if(method.equals("update")){
			postUrl = QYTXL_UPDATE_URL;
		}

		String parame = getRegisterParam(eventType,enterpriseId,method);
    	try
    	{

            log.info("Register url={},params={}",postUrl,parame);
			JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
			log.info("Register res={}",responseJson.toString());
			resBean = JsonUtil.jsonToBean(responseJson.toString(), RegisterCallBackResBean.class);
			resBean.setEventType(eventType);
			
		} 
    	catch (Exception e) {
			log.error("registerCallBack is error!",e);
			log.error(
	                "registerCallBack is error|enterId={}|req={}",
	                enterpriseId, resBean.toString(), e);
			resBean = null;
		} 

        return resBean;
    }
    /**
     * 注册企业通讯录回调接口
     * @param
     * @param
     * @return
     * @throws NoSuchAlgorithmException
     */
    public RegisterCallBackResBean deleteCallBackByUrl(String enterpriseId,
                                                    String url,String eventType) throws NoSuchAlgorithmException
    {
        RegisterCallBackResBean resBean = new RegisterCallBackResBean();

        /*获取企业ID、请求方法、回调接口类型  默认调用*/

        //请求注册回调接口
        String postUrl = QYTXL_DELETE_URL;

        String parame = getDelByUrlParam(enterpriseId,url,eventType);
        try
        {
            log.info("Register url={},params={}",postUrl,parame);
            JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
            log.info("Register res={}",responseJson.toString());
            resBean = JsonUtil.jsonToBean(responseJson.toString(), RegisterCallBackResBean.class);
            resBean.setEventType(eventType);

        }
        catch (Exception e) {
            log.error("registerCallBack is error!",e);
            log.error(
                "registerCallBack is error|enterId={}|req={}",
                enterpriseId, resBean.toString(), e);
            resBean = null;
        }

        return resBean;
    }
	/**
	 * 查询联系人
	 * @param enterpriseId
	 * @param contactId
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getItem(String enterpriseId , String contactId )throws Exception{
		String parame = getItemParam(enterpriseId,contactId);
		String postUrl = QYTXL_EUSER_ITEM_URL;
		log.debug("getItem url={},params={}",postUrl,parame);
		JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
		log.debug("Register res={}",responseJson.toString());
		Map<String,Object> map = JsonUtil.jsonToBean(responseJson.toString(), Map.class);
		return map;
	}

	/**
	 *
	 * @param enterpriseId
	 * @param contactId
	 * @return
	 */
	private String getItemParam(String enterpriseId, String contactId) {
        String app_key = QYTXL_APPKEY;
        String once = EnterpriseUtil.getNum(12);
        String version = QYTXL_VERSION;
        String channel = QYTXL_APPKEY;
        String sdk_from = QYTXL_SDK_FROM;
        HashMap<String, String> Map = new HashMap<String, String>();
        Map.put("app_key", app_key);
        Map.put("once", once);
        Map.put("version", version);
        Map.put("channel", channel);
        Map.put("sdk_from", sdk_from);
        Map.put("enterpriseId", enterpriseId);
        Map.put("contactId", contactId);
        String signature = EnterpriseUtil.getNornmalSignature(Map, QYTXL_APPSECRET);
        StringBuffer params = new StringBuffer();
        params.append("app_key=" + app_key);
        params.append("&once=" + once);
        params.append("&version=" + version);
        params.append("&channel=" + channel);
        params.append("&sdk_from=" + sdk_from);
        params.append("&signature=" + signature);
        params.append("&enterpriseId=" + enterpriseId);
        params.append("&contactId=" + contactId);

        return params.toString();
    }

	/**
	 * 根据企业ID和euserId查询并返回用户所在全量部门ID
	 * @param enterpriseId
	 * @param euserId
	 */
	public  Map<String,Object> getDeptIdsByEuserId(String enterpriseId,String euserId) throws Exception {
		String parame = getDeptIdsByEuserIdParam(enterpriseId,euserId);
		String postUrl = QYTXL_EUSER_DEPARTMENT_ID_URL;
		log.info("gainDepartmentStaff url={},params={}",postUrl,parame);
		JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
		log.info("Register res={}",responseJson.toString());
		Map<String,Object> map = responseJson;
		return map;
	}
	/**
	 * 根据企业ID和euserId查询并返回用户所在全量部门ID封装参数
	 * @param enterpriseId
	 * @param euserId
	 */
	private String getDeptIdsByEuserIdParam(String enterpriseId, String euserId) {
		String app_key = QYTXL_APPKEY;
		String once = EnterpriseUtil.getNum(12);
		String version = QYTXL_VERSION;
		String channel = QYTXL_APPKEY;
		String sdk_from = QYTXL_SDK_FROM;
		HashMap<String,String> Map = new HashMap<String,String>();
		Map.put("app_key",app_key);
		Map.put("once",once);
		Map.put("version",version);
		Map.put("channel",channel);
		Map.put("sdk_from",sdk_from);
		Map.put("enterpriseId",enterpriseId);
		Map.put("euserId",euserId);
		String signature = EnterpriseUtil.getNornmalSignature(Map, QYTXL_APPSECRET);
		StringBuffer params = new StringBuffer();
		params.append("app_key="+app_key);
		params.append("&once="+once);
		params.append("&version="+version);
		params.append("&channel="+channel);
		params.append("&sdk_from="+sdk_from);
		params.append("&signature="+signature);
		params.append("&enterpriseId="+enterpriseId);
		params.append("&euserId="+euserId);

		return params.toString();
	}

	/**
	 * enterprise/getDetail  获取部门的子部门及其直属联系人
	 * @param
	 * @return
	 */
	public Map<String,Object> gainDepartmentStaff(String departmentId , String enterpriseId )throws Exception {
		//封装请求参数
		String parame = gainDepartmentStaffParam(departmentId,enterpriseId);
		String postUrl = QYTXL_DEPARTMENT_STAFF_URL;
		log.info("gainDepartmentStaff url={},params={}",postUrl,parame);
		JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
		//log.info("Register res={}",responseJson.toString());
		Map<String,Object> map = responseJson;
		return map;
	}

	/**
	 * 获取部门的子部门及其直属联系人 封装请求参数
	 * @param
	 * @return
	 */
	private String gainDepartmentStaffParam(String departmentId , String enterpriseId) {
		String app_key = QYTXL_APPKEY;
		String once = EnterpriseUtil.getNum(12);
		String version = QYTXL_VERSION;
		String channel = QYTXL_APPKEY;
		String sdk_from = QYTXL_SDK_FROM;
		HashMap<String,String> Map = new HashMap<String,String>();
		Map.put("app_key",app_key);
		Map.put("once",once);
		Map.put("version",version);
		Map.put("channel",channel);
		Map.put("sdk_from",sdk_from);
		Map.put("enterpriseId",enterpriseId);
		Map.put("departmentId",departmentId);
		String signature = EnterpriseUtil.getNornmalSignature(Map, QYTXL_APPSECRET);
		StringBuffer params = new StringBuffer();
		params.append("app_key="+app_key);
		params.append("&once="+once);
		params.append("&version="+version);
		params.append("&channel="+channel);
		params.append("&sdk_from="+sdk_from);
		params.append("&signature="+signature);
		params.append("&enterpriseId="+enterpriseId);
		params.append("&departmentId="+departmentId);

		return params.toString();
	}

	/**
     * 通讯录注册回调请求参数组装
     * @param
     * @return
     * @throws NoSuchAlgorithmException 
     */
    private String getRegisterParam(String eventTypeString,String enterpriseId,String method){
    	
    	String app_key= QYTXL_APPKEY;
    	String once = EnterpriseUtil.getNum(12);
    	String version = QYTXL_VERSION;
    	String channel = QYTXL_APPKEY;
    	String sdk_from = QYTXL_SDK_FROM;
		JSONArray eventType = new JSONArray();
		eventType.add(eventTypeString);
		String departmentId = "";
		String url = QYTXL_CALLBACK_URL;
		String token =QYTXL_TOKEN;
		String aes_key = null;
		try {
			aes_key = AESEncryptUtil.aesEncrypt(QYTXL_AES_KEY, QYTXL_AES_KEY_ENCRYPTKEY).replaceAll("\\+","");
		} catch (Exception e) {
			log.error("getRegisterParam is error!");
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
		if(method.equals("create")){
			Map.put("token", token);
			Map.put("aes_key", aes_key);	
		}else if(method.equals("delete")){
		}else if(method.equals("update")){
			Map.put("token", token);
			Map.put("aes_key", aes_key);
		}
		String signature = EnterpriseUtil.getNornmalSignature(Map, QYTXL_APPSECRET);
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
    	return parame;
    }
    /**
     * 通讯录根据url删除回调请求参数组装
     * @param
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String getDelByUrlParam(String enterpriseId,String url,String eventTypeString){

        String app_key= QYTXL_APPKEY;
        String once = EnterpriseUtil.getNum(12);
        String version = QYTXL_VERSION;
        String channel = QYTXL_APPKEY;
        String sdk_from = QYTXL_SDK_FROM;
        JSONArray eventType = new JSONArray();
        eventType.add(eventTypeString);
        String departmentId = "";
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
        String signature = EnterpriseUtil.getNornmalSignature(Map, QYTXL_APPSECRET);
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
        String parame = params.toString();
        return parame;
    }

	/**
	 * 获取注册回调信息
     * 参数 ： 企业id
	 * @return
	 */
	public Map<String,Object> getCallBack(String enterpriseId) throws Exception{
		//封装请求参数
		String parame = getCallBackMsgParam( enterpriseId);
		String postUrl = QYTXL_CALL_BACK_MSG_URL;
		log.info("getCallBackMsg url={},params={}",postUrl,parame);
		JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
		log.info("Register res={}",responseJson.toString());
		Map<String,Object> map = responseJson;
		return map;
	}

	/**
	 * 获取企业信息
     * 参数 ： 企业id
	 * @return
	 */
	public Map<String,Object> getEntInfo(String enterpriseId) throws Exception{
		//封装请求参数
		String parame = getCallBackMsgParam( enterpriseId);
		String postUrl = QYTXL_ENTER_MSG_URL;
		log.info("getEntInfo url={},params={}",postUrl,parame);
		JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
		log.info("Register res={}",responseJson.toString());
		Map<String,Object> map = responseJson;
		return map;
	}



    /**
     * 获取注册回调信息参数封装
     * @param enterpriseId
     * @return
     */
    private String getCallBackMsgParam(String enterpriseId) {
        String app_key= QYTXL_APPKEY;
        String once = EnterpriseUtil.getNum(12);
        String version = QYTXL_VERSION;
        String channel = QYTXL_APPKEY;
        String sdk_from = QYTXL_SDK_FROM;
        HashMap<String,String> Map = new HashMap<String,String>();
        Map.put("app_key", app_key);
        Map.put("once", once);
        Map.put("version", version);
        Map.put("channel", channel);
        Map.put("sdk_from", sdk_from);
        Map.put("enterpriseId", enterpriseId);

        String signature = EnterpriseUtil.getNornmalSignature(Map, QYTXL_APPSECRET);
        StringBuffer params = new StringBuffer();
        params.append("app_key="+app_key);
        params.append("&once="+once);
        params.append("&version="+version);
        params.append("&channel="+channel);
        params.append("&sdk_from="+sdk_from);
        params.append("&signature="+signature);
        params.append("&enterpriseId="+enterpriseId);

        String parame = params.toString();
        return parame;
    }

    /**
     * 通过企业ID+应用链接查询企业是否存在该应用接口
     * 参数 ： 企业id
     * @return
     */
    public Map<String,Object> checkEnterIsExist(String enterpriseId,String appUrl) throws Exception{
        //封装请求参数
        String parame = getCheckEnterIsExistParams(enterpriseId,appUrl);
        String postUrl = QYTXL_CHECK_ENTER_IS_EXIST_URL;
        log.info("checkEnterIsExist url={},params={}",postUrl,parame);
        JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
        log.info("Register res={}",responseJson.toString());
        Map<String,Object> map = responseJson;
        System.out.println(map);
        return map;
    }

    /**
     * 获取通过企业ID+应用链接查询企业是否存在该应用接口参数封装
     * @param enterpriseId
     * @return
     */
    private String getCheckEnterIsExistParams(String enterpriseId,String appUrl) {
        HashMap<String,String> Map = new HashMap<String,String>();
        Map.put("appUrl", appUrl);
        Map.put("enterpriseId", enterpriseId);

        StringBuffer params = new StringBuffer();
        params.append("appUrl="+appUrl);;
        params.append("&enterpriseId="+enterpriseId);

        String parame = params.toString();
        return parame;
    }

    /**
     * 通过企业ID+应用链接查询企业是否存在该应用接口
     * 参数 ： 企业id
     * @return
     */
    public Map<String,Object> getContactIdByEuserId(String enterpriseId,String euserId) throws Exception{
        //封装请求参数
        String parame = getContactIdByEuserIdParams(enterpriseId,euserId);
       // String postUrl = "https://open1.cytxl.com.cn/enterprise/getContactIdByEuserId.json";
        String postUrl = QYTXL_QUERY_CONTACT_ID_BY_UID_URL;
        log.info("getContactIdByEuserId url={},params={}",postUrl,parame);
        JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
        //log.info("Register res={}",responseJson.toString());
        Map<String,Object> map = responseJson;
        System.out.println(map);
        return map;
    }
    /**
     * 通过企业ID+应用链接查询企业是否存在该应用接口
     * 参数 ： 企业id
     * @return
     */
    private String getContactIdByEuserIdParams(String enterpriseId, String euserId) {
        String app_key= QYTXL_APPKEY;
//        String app_key= "2b5aa5bbace54212b5036640db943db0";
        String once = EnterpriseUtil.getNum(12);
        String version = QYTXL_VERSION;
        String channel = QYTXL_APPKEY;
        //String channel = "2b5aa5bbace54212b5036640db943db0";
        String sdk_from = QYTXL_SDK_FROM;
        HashMap<String,String> Map = new HashMap<String,String>();
        Map.put("app_key", app_key);
        Map.put("once", once);
        Map.put("version", version);
        Map.put("channel", channel);
        Map.put("sdk_from", sdk_from);
        Map.put("enterpriseId", enterpriseId);
        Map.put("euserId", euserId);

        String signature = EnterpriseUtil.getNornmalSignature(Map, QYTXL_APPSECRET);
//        String signature = EnterpriseUtil.getNornmalSignature(Map, "258e5059518c4f56a56658d7172d29c5");
        StringBuffer params = new StringBuffer();
        params.append("app_key="+app_key);
        params.append("&once="+once);
        params.append("&version="+version);
        params.append("&channel="+channel);
        params.append("&sdk_from="+sdk_from);
        params.append("&signature="+signature);
        params.append("&enterpriseId="+enterpriseId);
        params.append("&euserId="+euserId);

        String parame = params.toString();
        return parame;

    }


    /**
     * 通过企业ID查询该企业下所有联系人
     * 参数 ： 企业id
     * @return
     */
    public String getEmployeesByEnterId(String enterpriseId,String mobile,String matchHint) throws Exception{
        //封装请求参数
        String parame = getEmployeesByEnterIdParams(enterpriseId,mobile,matchHint);
        String postUrl = QYTXL_QUERY_EMPLOEES_BY_ENTERID_URL;
        log.info("getEmployeesByEnterId url={},params={}",postUrl,parame);
        JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
        Map<String,Object> map = JsonUtil.jsonToBean(responseJson.toString(), Map.class);
        log.info("responseJson={}",responseJson);
        log.info("map={}",map);
        String items = (String) responseJson.get("items");
//        String result = JsonUtil.jsonToBean(responseJson.toString(), String.class);
        return items;
    }

    /**
     * 通过企业ID查询该企业下所有联系人参数封装
     * 参数 ： 企业id
     * @return
     */
    private String getEmployeesByEnterIdParams(String enterpriseId,String phone,String matchHint) {
        String app_key= QYTXL_APPKEY;
//        String app_key= "d7eec29775ca42a894ab3ce432667e70";
        String mobile = "";
        try {
            mobile = AesUtils.encrypt(phone, "ca7dc22b57fa45a7");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String once = EnterpriseUtil.getNum(12);
        String version = QYTXL_VERSION;
        String channel = QYTXL_APPKEY;
//        String channel= "d7eec29775ca42a894ab3ce432667e70";
        String sdk_from = QYTXL_SDK_FROM;
        HashMap<String,String> Map = new HashMap<String,String>();
        Map.put("app_key", app_key);
        Map.put("mobile", mobile);
        Map.put("once", once);
        Map.put("version", version);
        Map.put("channel", channel);
        Map.put("sdk_from", sdk_from);
        Map.put("enterpriseId", enterpriseId);
        Map.put("matchHint", matchHint);
        String signature = EnterpriseUtil.getNornmalSignature(Map, "ca7dc22b57fa45a7a6a8eb89a3dc7b49");
//        String signature = EnterpriseUtil.getNornmalSignature(Map, "ca7dc22b57fa45a7");
        StringBuffer params = new StringBuffer();
        try {
            app_key = URLEncoder.encode(app_key,"UTF-8");
            mobile = URLEncoder.encode(mobile,"UTF-8");
            once = URLEncoder.encode(once,"UTF-8");
            version = URLEncoder.encode(version,"UTF-8");
            channel = URLEncoder.encode(channel,"UTF-8");
            sdk_from = URLEncoder.encode(sdk_from,"UTF-8");
            enterpriseId = URLEncoder.encode(enterpriseId,"UTF-8");
            matchHint = URLEncoder.encode(matchHint,"UTF-8");
            signature = URLEncoder.encode(signature,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.append("app_key="+app_key);
        params.append("&mobile="+mobile);
        params.append("&once="+once);
        params.append("&version="+version);
        params.append("&channel="+channel);
        params.append("&sdk_from="+sdk_from);
        params.append("&signature="+signature);
        params.append("&enterpriseId="+enterpriseId);
        params.append("&matchHint="+matchHint);

        String parame = params.toString();
        return parame;

    }

    public static void main(String[] args) {
//        String app_key= "d7eec29775ca42a894ab3ce432667e70";
//        String once = EnterpriseUtil.getNum(12);
//        String version = "1.0";
//        String channel= "d7eec29775ca42a894ab3ce432667e70";
//        String sdk_from = "java";
//        String attendanceId= "815";
//        String attendanceDate = "2019-03-28";
//        String type = "未打卡";
//        HashMap<String,String> Map = new HashMap<String,String>();
//        Map.put("app_key", app_key);
//        Map.put("once", once);
//        Map.put("version", version);
//        Map.put("channel", channel);
//        Map.put("sdk", sdk_from);
//        Map.put("attendanceId", attendanceId);
//        Map.put("attendanceDate", attendanceDate);
//        Map.put("type", type);
//        String signature = EnterpriseUtil.getNornmalSignature(Map, "ca7dc22b57fa45a7a6a8eb89a3dc7b49");
//        Map.put("signature", signature);
//        System.out.println(once);
//        System.out.println(signature);
//        try {
//            String s = AESEncryptUtil.aesDecrypt("n1nVS+a8dyhbqQzdUs5NjQ==", "ca7dc22b57fa45a7");
//            System.out.println(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

}

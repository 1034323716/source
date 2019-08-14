package richinfo.attendance.action;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import richinfo.attendance.asyn.TeamMonthReportAsynTask;
import richinfo.attendance.bean.RegisterCallBackResBean;
import richinfo.attendance.dao.AttendDao;
import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.dao.EnterDao;
import richinfo.attendance.entity.AttendDepartmentChooser;
import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.entity.TeamMonthReportEntity;
import richinfo.attendance.entity.vo.DetailVO;
import richinfo.attendance.service.AttendEnterService;
import richinfo.attendance.service.AttendGroupService;
import richinfo.attendance.service.impl.AttendEnterServiceImpl;
import richinfo.attendance.service.impl.AttendGroupServiceImpl;
import richinfo.attendance.task.EmployeeMonthDetailTask;
import richinfo.attendance.task.TeamMonthReportTask;
import richinfo.attendance.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 功能描述： 考勤同步接口类
 */
@Controller
@RequestMapping("/sync")
public class AttendanceSyncAction {

	private EnterDao enterDao = new EnterDao();
	private AttendEnterService attendEnterService = new AttendEnterServiceImpl();
	private AttendDao attendDao = new AttendDao();
	private AttendGroupService attendGroupService =new AttendGroupServiceImpl();
    private AttendReportDao reportDao = new AttendReportDao();
    private final static Logger logger = LoggerFactory.getLogger(AttendanceSyncAction.class);

    //请求身份密钥 AES request_key  加密
    private final  String REQUEST_KEY = "REQUEST_KEY_q9PLS5V8y3qxbiMj9y8Xgw==";
    
    /*APPID相当于通讯录的APPKEY*/
    private static String appid = AttendanceConfig.getInstance().getProperty("attend.qytxl.appid", "9fdcd721d954456b8c7ea53f80635456");
    private static String appkey = AttendanceConfig.getInstance().getProperty("attend.qytxl.appkey", "6af15ca383ee45dd959bf0e84d8eadac");
    private static String url = AttendanceConfig.getInstance().getProperty("attend.qytxl.callbackUrl", "http://121.15.167.235:10721/atdc/sync/getRegisterCallBackInfo");
//	String postUrl = AttendanceConfig.getInstance().getProperty("qytxl.register.url", "https://open.cytxl.com.cn/enterprise/registerCallBack.json");
    private static  String postUrl = AttendanceConfig.getInstance().getProperty("qytxl.register.url", "https://open.cytxl.com.cn/enterprise/getCallBack.json");
//    	String postUrl = AttendanceConfig.getInstance().getProperty("attend.enterprise.postUrl", "https://api1.cytxl.com.cn/enterprise/deleteCallBack.json");
    private static String aeskey = AttendanceConfig.getInstance().getProperty("attend.qytxl.aes_key", "6af15ca383ee45dd");


    /**
     * 监控接口
     * @param request
     * @param response
     */
    @RequestMapping(value = "/monitoring", method = RequestMethod.GET)
    @ResponseBody
    public String monitoring(HttpServletRequest request, HttpServletResponse response) {
        String requestIp = getRequestIp(request);
        logger.info("运维监控请求 requestIp={}",requestIp);
        return "succeed";
    }


    /**
     * 注册回调接口
     * @param request
     * @param response
     */
    @RequestMapping(value = "/registerCallBack", method = RequestMethod.GET)
    public void registerCallBack(HttpServletRequest request, HttpServletResponse response) {
    	InputStream input = null;
    	/*获取企业ID、请求方法、回调接口类型  默认调用*/
    	String enterpriseId = request.getParameter("enterpriseId");
    	String method = request.getParameter("method");
    	String eventTypeString = request.getParameter("eventType");
    	if(null == method){
    		method="create";
    	}
    	if(null == eventTypeString){
    		eventTypeString="event_user_remove";
    	}
    	logger.debug("注册回调---enterpriseId:"+enterpriseId+",method:"+method+",eventType:"+eventTypeString);
    	String app_key= appid;
    	String once = EnterpriseUtil.getNum(12);
    	String version = "2.0";
    	String channel = appid;
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
		String signature = EnterpriseUtil.getNornmalSignature(Map, appkey);
		
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
		if(method.equals("delete")){
			postUrl="https://open.cytxl.com.cn/enterprise/deleteCallBack.json";
		}
    	try {
    		logger.debug("注册回调 request params:"+parame);
			JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
			logger.debug("注册回调 response result:"+responseJson);
        }
        catch (Exception e) {
            logger.error(
                "registerCallBack error enterpriseId={}|method={}|eventType={}",
                enterpriseId, method, eventTypeString, e);
        }
    }
    
    /**
     * 接收回调地址
//     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/getRegisterCallBackInfo", method = RequestMethod.POST)
    public String getRegisterCallBackInfo(@RequestParam String msg_signature
    		,@RequestParam String timeStamp
    		,@RequestParam String nonce
    		,@RequestParam String encrypt, HttpServletResponse response) {
			Map map = new HashMap();
			map.put("msg_signature",msg_signature);
			map.put("timeStamp",timeStamp);
			map.put("nonce",nonce);
			try {
				logger.info("getRegisterCallBackInfo request msg_signature={}|timeStamp={}| nonce={}|encrypt={}|aeskey={}",msg_signature,timeStamp,nonce,encrypt,aeskey);
				String aesDecrypt = AESEncryptUtil.aesDecrypt(encrypt, AESEncryptUtil.getAESKey(AESEncryptUtil.aesEncrypt(aeskey, "PKCS5Padding").replaceAll("\\+","")));
				//String aesDecrypt = AesUtils.decrypt(encrypt, aeskey);
				logger.info("aesDecrypt:"+aesDecrypt);
				JSONObject msg = JSONObject.parseObject(aesDecrypt);
				//回调接口类型
				Object EventType = msg.get("EventType");
				//企业ID
				String CorpId = msg.get("CorpId").toString();
				//删除人员回调
				if(EventType.equals("event_user_remove")){
					List<AttendEmployee> decreaseMember = new ArrayList<AttendEmployee>();
					//用户ID
					JSONArray UserIds = msg.getJSONArray("UserId");
					for(int i=0;i<UserIds.size();i++){
						String userid = UserIds.get(i).toString();
						AttendEmployee attendEmployee = new AttendEmployee();
						attendEmployee.setEnterId(CorpId);
						attendEmployee.setContactId(userid);
						attendEmployee.setStatus(1);
						decreaseMember.add(attendEmployee);
					}

					enterDao.deleteUser(decreaseMember);

                 //删除部门回调
				}else if (EventType.equals("event_dept_remove")){
					List<AttendDepartmentChooser>attendDepartmentChoosers = new ArrayList<>();
					JSONArray deptIdS = msg.getJSONArray("DepId");
					for(int i=0;i<deptIdS.size();i++){
						String deptId = deptIdS.get(i).toString();
						AttendDepartmentChooser attendDepartmentChooser = new AttendDepartmentChooser();
						attendDepartmentChooser.setEnterpriseId(CorpId);
						attendDepartmentChooser.setDepartmentId(deptId);
						attendDepartmentChoosers.add(attendDepartmentChooser);
					}
					enterDao.deleteDepartment(attendDepartmentChoosers);
                 //删除企业回调
				}else if (EventType.equals("event_corp_remove")){
                    enterDao.delEnterGroup(Arrays.asList(CorpId));
                    //用户信息更新回调
                }else if (EventType.equals("event_user_modify")){
                    List<AttendEmployee> decreaseMember = new ArrayList<AttendEmployee>();
                    //用户ID
                    JSONArray UserIds = msg.getJSONArray("UserId");
                    for(int i=0;i<UserIds.size();i++){
                        String userid = UserIds.get(i).toString();
                        AttendEmployee attendEmployee = new AttendEmployee();
                        attendEmployee.setContactId(userid);
                        attendEmployee.setEnterId(CorpId);
                        attendEmployee.setStatus(1);
                        decreaseMember.add(attendEmployee);
                    }
                    enterDao.userModify(decreaseMember);
                }
			} catch (Exception e1) {
			    logger.error("getRegisterCallBackInfo error msg_signature={}|encrypt={}", msg_signature, encrypt, e1);
			}
			logger.debug("recept message:msg_signature"+msg_signature+",timeStamp:"+timeStamp+",nonce:"+nonce+",encrypt:"+encrypt);
			String success = null;
			try {
				success = AESEncryptUtil.aesEncrypt("success",AESEncryptUtil.getAESKey(AESEncryptUtil.aesEncrypt(aeskey, "PKCS5Padding").replaceAll("\\+","")));
			} catch (Exception e) {
			    logger.error("getRegisterCallBackInfo error msg_signature={}|encrypt={}", msg_signature, encrypt, e);
			}
			map.put("encrypt", success);
			logger.debug("return message:"+success);
			return EnterpriseUtil.toJson(map);
    }

	/**
	 * 查询企业注册回调信息
	 * @param map
	 * @return
     * 已经完成
	 */
	/*@RequestMapping(value = "/getCallBack" , method = RequestMethod.POST)
	@ResponseBody*/
    public List getCallBack(@RequestBody Map<String,String> map ,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2018-12-28")){
            return null;
        }
        String requestIp = getRequestIp(request);
		logger.info("getCallBack request map={}|ip",map,request);
		String enter = map.get("enterId");
		String pageNo = map.get("pageNo");
		String pageCount = map.get("pageCount");
		String eventType = map.get("eventType");
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return  Arrays.asList("非法访问 ip："+requestIp);
        }
    	return attendEnterService.getCallBack(enter,pageNo,pageCount,eventType);
	}

	/**
	 * 注册  删除  企业注册回调信息
	 * @param map
	 * @return
     * 已完成
	 */
	///@RequestMapping(value = "/updateCallBack" , method = RequestMethod.POST)
	//@ResponseBody
	public RegisterCallBackResBean updateCallBack(@RequestBody Map<String,String> map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2018-11-01")){
            return null;
        }
        String requestIp = getRequestIp(request);
		logger.info("updateCallBack request map={}|ip={}",map,requestIp);
		String enter = map.get("enterId");
        String method = map.get("method");
        String eventType = map.get("eventType");
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            RegisterCallBackResBean registerCallBackResBean = new RegisterCallBackResBean();
            registerCallBackResBean.setError_msg( "非法访问 ip：" + requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return registerCallBackResBean;
        }
		return attendEnterService.updateCallBack(enter, method, eventType);
	}

	/**
	 * 删除登记注册回调接口
     * 已完成
	 * @return
	 */
  //  @RequestMapping(value = "/delRegister" , method = RequestMethod.POST)
   // @ResponseBody
	public Map<String,Object> delRegister (@RequestBody Map<String,String> paramMap,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2018-11-01")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("delRegister request map={}|ip={}",paramMap,requestIp);
        String key = paramMap.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            Map<String,Object> errorMap = new HashMap();
            errorMap.put("error", "非法访问 ip：" + requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return errorMap;
        }
		return attendEnterService.delRegister();
	}

    /***
     * 重新注册回调接口
     * 已完成
     */
    /*@RequestMapping(value = "/anewRegister" , method = RequestMethod.POST)
    @ResponseBody*/
    public Map<String,Object> anewRegister(@RequestBody Map<String,String> paramMap,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2018-12-28")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("anewRegister request map={}|ip={}",paramMap,requestIp);
        String key = paramMap.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            Map<String,Object> errorMap = new HashMap();
            errorMap.put("error", "非法访问 ip：" + requestIp);
            return errorMap;
        }
        return attendEnterService.anewRegister();
    }

    /***
     * 原生态sql查询
     */
    @RequestMapping(value = "/originalSqlQuery" , method = RequestMethod.POST)
    @ResponseBody
    public List  originalSqlQuery(@RequestBody Map<String,String> paramMap,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2019-01-28")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("originalSqlQuery request param paramMap= {}|ip={}",paramMap,requestIp);
        String sql = paramMap.get("sql");
        String key = paramMap.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return  Arrays.asList("非法访问 ip："+requestIp);
        }
        if (StringUtils.isBlank(sql) || !(sql.trim().startsWith("select")|sql.trim().startsWith("SELECT"))){
           return  Arrays.asList("参数错误");
        }
        return attendDao.originalSqlQuery(paramMap);
    }
    private  String getRequestIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 指定时间统计个人月报
     */
    @RequestMapping(value = "/assignStatistics" , method = RequestMethod.POST)
    @ResponseBody
    public List  assignStatistics(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!(currentDateTime.equals("2019-05-16") || currentDateTime.equals("2019-05-17"))){
//            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("assignStatistics request date map={}|ip={}",map,requestIp);
        String date = map.get("date");
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return  Arrays.asList("非法访问 ip："+requestIp);
        }
        if (StringUtils.isBlank(date)){
            return  Arrays.asList("参数错误");
        }
        date= date.trim();
        try {
            Date assignDate = TimeUtil.string2Date(date);
            EmployeeMonthDetailTask employeeMonthDetailTask = new EmployeeMonthDetailTask();
            employeeMonthDetailTask.statistics(assignDate);
            return  Arrays.asList("assignDate 完成");
        } catch (ParseException e) {
            return  Arrays.asList("失败  转换异常");
        }
    }

    /**
     * 指定时间统计团队月报
     */
    @RequestMapping(value = "/assignTeamStatistics" , method = RequestMethod.POST)
    @ResponseBody
    public List  assignTeamStatistics(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!(currentDateTime.equals("2019-05-16") || currentDateTime.equals("2019-05-17"))){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("assignStatistics request date map={}|ip={}",map,requestIp);
        String date = map.get("date");
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return  Arrays.asList("非法访问 ip："+requestIp);
        }
        if (StringUtils.isBlank(date)){
            return  Arrays.asList("参数错误");
        }
        date= date.trim();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            Date assignDate = simpleDateFormat.parse(date);
            TeamMonthReportTask employeeMonthDetailTask = new TeamMonthReportTask();
            employeeMonthDetailTask.statistics(assignDate);
            return  Arrays.asList("assignDate 完成");
        } catch (ParseException e) {
            return  Arrays.asList("失败  转换异常");
        }
    }

    /**
     * 同步检查通讯录同步删除人员
     * @return
     *  已完成
     */
    //@RequestMapping(value = "/attendanceSyncVerify" , method = RequestMethod.POST)
    //@ResponseBody
    public List  attendanceSyncVerify(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2018-11-01")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("attendanceSyncVerify request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return  Arrays.asList("非法访问 ip："+requestIp);
        }
        return attendGroupService.attendanceSyncVerify();
    }

    @RequestMapping(value = "/testGetItem" , method = RequestMethod.POST)
    @ResponseBody
    public Map testGetItem(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        String requestIp = getRequestIp(request);
        logger.info("getEntInfo request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            Map<String,Object> errorMap = new HashMap();
            errorMap.put("error", "非法访问 ip：" + requestIp);
            return errorMap;
        }
        String contactId = map.get("contactId");
        String enterId = map.get("enterId");
        try {
            return QytxlUtil.getInstance().getItem(enterId, contactId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    return null;
    }

    /**
     * 获取企业信息
     * @param map
     * @param request
     * @return
     * 已经完成
     */
   /* @RequestMapping(value = "/getEntInfo" , method = RequestMethod.POST)
    @ResponseBody*/
    public Map getEntInfo(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2018-11-29")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("getEntInfo request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            Map<String,Object> errorMap = new HashMap();
            errorMap.put("error", "非法访问 ip：" + requestIp);
            return errorMap;
        }
        String enterId = map.get("enterId");
        try {
            return QytxlUtil.getInstance().getEntInfo(enterId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验删除企业
     * @param map
     * @param request
     * @return
     */
    /*@RequestMapping(value = "/checkEnter" , method = RequestMethod.POST)
    @ResponseBody*/
    public List checkEnter (@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2018-11-29")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("checkEnter request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return Arrays.asList("非法访问 ip：" + requestIp);
        }
       // logger.info("校验完成====================");
        return attendEnterService.checkEnter();
    }

    /**
     * 注册企业回调
     * 由于新增注册回调，需要根据所记录注册的企业加上注册企业删除回调
     * @param map
     * @param request
     * @return
     * 已经完成
     */
  /*  @RequestMapping(value = "/registerEnterCallBack",method = RequestMethod.POST)
    @ResponseBody*/
    public List registerEnterCallBack(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        /*if (!currentDateTime.equals("2018-11-29")){
            return null;
        }*/
        String requestIp = getRequestIp(request);
        logger.info("checkEnter request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return Arrays.asList("非法访问 ip：" + requestIp);
        }
        return attendEnterService.registerEnterCallBack();
    }

    /**
     * 通过企业ID+应用链接查询企业是否存在该应用接口
     * @param map
     * @param request
     * @return
     */
    /*@RequestMapping(value = "/checkEnterIsExist" , method = RequestMethod.POST)
    @ResponseBody*/
    public Map checkEnterIsExist (@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2018-11-29")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("checkEnterIsExist request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            Map errorMap = new HashMap();
            errorMap.put("errorIp",requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return errorMap;
        }
        String enterpriseId	 = (String)map.get("enterpriseId");
        String appUrl = (String)map.get("appUrl");
        // logger.info("校验完成====================");
        try {
            return QytxlUtil.getInstance().checkEnterIsExist(enterpriseId,appUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   /* @RequestMapping(value = "/getContactIdByUid" , method = RequestMethod.POST)
    @ResponseBody*/
    public Map getContactIdByUid (@RequestBody Map<String ,String>map,HttpServletRequest request) {
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2019-01-28")){
            return null;
        }
        String requestIp = getRequestIp(request);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            Map errorMap = new HashMap();
            errorMap.put("errorIp",requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return errorMap;
        }
        try {
            Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(String.valueOf(map.get("enterpriseId")), String.valueOf(map.get("euserId")));
            if(0 == (int)repMap.get("error_code")){
                List<Map<String,String>> userList= (List<Map<String,String>>)repMap.get("contactInfo");
                for (Map<String,String>  userMap : userList){
                    String adminName  = AesUtils.decrypt(userMap.get("name"), AttendanceConfig.getInstance()
                        .getProperty("attend.qytxl.aes_key",
                            "6af15ca383ee45dd"));
                    userMap.put("name",adminName);
                }
            }
            return repMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 补充考勤组contactId
     * @param map
     * @param request
     * @return
     *//*
    @RequestMapping(value = "/checkoutGroupContactId" , method = RequestMethod.POST)
    @ResponseBody*/
    public List checkoutGroupContactId(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2019-01-28")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("checkoutGroupContactId request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            Map errorMap = new HashMap();
            errorMap.put("errorIp",requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return Arrays.asList("非法访问 ip：" + requestIp);
        }
        attendGroupService.checkoutGroupContactId();
        return Arrays.asList("succeed");
    }

    /**
     *  补充审批人contactId
     * @param map
     * @param request
     * @return
     *//*
    @RequestMapping(value = "/checkoutExamineContactId" , method = RequestMethod.POST)
    @ResponseBody*/
    public List checkoutExamineContactId(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2019-01-28")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("checkoutExamineContactId request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            Map errorMap = new HashMap();
            errorMap.put("errorIp",requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return Arrays.asList("非法访问 ip：" + requestIp);
        }
        attendGroupService.checkoutExamineContactId();
        return Arrays.asList("succeed");
    }

  /*  *//**
     * 补充考勤人员contactId
     * @param map
     * @param request
     * @return
     *//*
    @RequestMapping(value = "/checkoutEmployeeContactId" , method = RequestMethod.POST)
    @ResponseBody*/
    public List checkoutEmployeeContactId(@RequestBody Map<String ,String>map,HttpServletRequest request){
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2019-01-28")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("checkoutExamineContactId request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            Map errorMap = new HashMap();
            errorMap.put("errorIp",requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return Arrays.asList("非法访问 ip：" + requestIp);
        }
        attendGroupService.checkoutEmployeeContactId();
        return Arrays.asList("succeed");
    }

    /**
     * 查询企业联系人
     * @param map
     * @param request
     * @return
     */
//    @RequestMapping(value = "/getEmployeesByEnterId" , method = RequestMethod.POST)
//    @ResponseBody
    public String getEmployeesByEnterId(@RequestBody Map<String ,String>map,HttpServletRequest request){
        Map<String,Object> resultMap = new HashMap<>();
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2019-01-29")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("getEmployeesByEnterId request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            Map errorMap = new HashMap();
            errorMap.put("errorIp",requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return "非法访问 ip：" + requestIp;
        }
        String enterId = map.get("enterId");
        String mobile = map.get("mobile");
        String matchHint = map.get("matchHint");
//        List<AttendEmployee> employees = attendGroupService.getEmployeesByEnterId(enterId,mobile);
        try {
            String items = QytxlUtil.getInstance().getEmployeesByEnterId(enterId,mobile,matchHint);
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return  null;
    }

    /**
     *指定更换企业考勤组审批人与转移审批单
     * @param map
     * @param request
     * @return
     *//*
    @RequestMapping(value = "/updateApprover" , method = RequestMethod.POST)
    @ResponseBody*/
    public String updateApprover(@RequestBody Map<String ,String>map,HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2019-03-13")) {
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("updateApprover request date map={}|ip={}", map, requestIp);
        String key = map.get("key");
        if (StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)) {
            Map errorMap = new HashMap();
            errorMap.put("errorIp", requestIp);
            logger.error("========非法访问=====key={}======ip={}", key, requestIp);
            return "非法访问 ip：" + requestIp;
        }
        return attendGroupService.updateApprover(map);
    }


    @RequestMapping(value = "/repairTeamMonthReport" , method = RequestMethod.POST)
    @ResponseBody
    public List repairTeamMonthReport(@RequestBody Map<String,String> map,HttpServletRequest request){

        String currentDateTime = TimeUtil.getCurrentDateTime(TimeUtil.BASE_DATE_FORMAT);
        if (!currentDateTime.equals("2019-04-01")){
            return null;
        }
        String requestIp = getRequestIp(request);
        logger.info("repairTeamMonthReport request date map={}|ip={}",map,requestIp);
        String key = map.get("key");
        if ( StringUtils.isBlank(key) || !REQUEST_KEY.equals(key)){
            Map errorMap = new HashMap();
            errorMap.put("errorIp",requestIp);
            logger.error("========非法访问=====key={}======ip={}",key,requestIp);
            return Arrays.asList("非法访问 ip：" + requestIp);
        }

        List<String> enterIds = reportDao.queryAllEnterId();
        if (AssertUtil.isEmpty(enterIds)) {
            logger.info("TeamMonthReportTask can't find enterIds");
            return Arrays.asList("TeamMonthReportTask can't find enterIds");
        }
        String firstDate = map.get("firstDate");
        String attendanceDate = map.get("attendanceDate");
        //创建一个线程数默认为20的线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(AttendanceConfig.getInstance().getMultiThreadedPool());
        try {

            for (String enterId : enterIds) {
                List<TeamMonthReportEntity> monthes = reportDao.teamMonthReportInfo(firstDate, attendanceDate, enterId);
                if (AssertUtil.isEmpty(monthes)) {
                    logger
                        .info(
                            "TeamMonthReportTask can't find report data,enterId={}|attendanceDate={}",
                            enterId, attendanceDate);
                    continue;
                }

                // 异步将各企业数据入库
//            AsynTaskProcess.asynExecTask(new TeamMonthReportAsynTask(monthes));
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        new TeamMonthReportAsynTask(monthes);
                    }
                });
            }
            //线程任务执行完毕后 关闭多线程
            fixedThreadPool.shutdown();
            logger.info("end TeamMonthReportTask ");

        } catch (Exception e) {
            //发生异常直接回收关闭连接池
            fixedThreadPool.shutdownNow();
        }
        return Arrays.asList("succeed");
    }


    /**
     * 获取当天的考勤数据
     * 139邮箱方需求
     * @param map
     * @return
     */
    @RequestMapping(value = "/getCurrentDayDate" , method = RequestMethod.POST)
    @ResponseBody
    public Map getCurrentDayDate(@RequestBody Map<String ,String>map) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();
        logger.info("map={}",map);

        //校验参数
        if (!validParams(map,resultMap)) {
            return resultMap;
        }
        //校验参数签名
        if (!validSignature(map,resultMap)) {
            return resultMap;
        }

        //校验是否是139邮箱企业考勤组 封闭其他考勤组查看权限
        if (!validAttendanceId(map,resultMap)) {
            return resultMap;
        }

        List<DetailVO> employeeMonthDetails = attendGroupService.getCurrentDayDate(map);
        if (AssertUtil.isNotEmpty(employeeMonthDetails)) {
//            int totalCount = attendGroupService.getCurrentDayDateCount(map);
            for (DetailVO e : employeeMonthDetails) {
                String phone = "";
                String name = "";
                if (AssertUtil.isNotEmpty(e.getPhone())) {
                    phone = AESEncryptUtil.aesEncrypt(e.getPhone(),"ca7dc22b57fa45a7");
                }
                if (AssertUtil.isNotEmpty(e.getEmployeeName())) {
                    name = AESEncryptUtil.aesEncrypt(e.getEmployeeName(),"ca7dc22b57fa45a7");
                }
                e.setEmployeeName(name);
                e.setPhone(phone);
            }
//            resultMap.put("totalCount",totalCount);
            resultMap.put("result",employeeMonthDetails);
            resultMap.put("totalCount",employeeMonthDetails.size());
        } else {
            resultMap.put("totalCount",0);
            resultMap.put("result",null);
        }
        resultMap.put("code",0);
        resultMap.put("msg","成功");
        return resultMap;
    }

    /**
     * 根据enterId获取企业下所有有效考勤组
     * 139邮箱方需求
     * @param map
     * @return
     */
    @RequestMapping(value = "/getAttendanceIdByEnterId" , method = RequestMethod.POST)
    @ResponseBody
    public Map getAttendanceIdByEnterId(@RequestBody Map<String ,String>map,HttpServletRequest request) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();
        logger.info("map={}",map);

        //校验参数
        if (!validParamsEnterId(map,resultMap)) {
            return resultMap;
        }
        //参数签名校验
        if (!validSignatureEnterId(map,resultMap)) {
            return resultMap;
        }

        //校验是否是139邮箱企业考勤组 封闭其他考勤组查看权限
        if (!validAttendanceId(map,resultMap)) {
            return resultMap;
        }

//        int totalCount = attendGroupService.getAttendanceIdByEnterIdCount(map.get("enterId"));
        List<String> attendanceIds = attendGroupService.getAttendanceIdByEnterId(map.get("enterId"));
        resultMap.put("code",0);
        resultMap.put("msg","成功");
//        resultMap.put("totalCount",totalCount);
        resultMap.put("result",attendanceIds);
        resultMap.put("totalCount",attendanceIds.size());
        return resultMap;
    }

    private boolean validParams(Map<String ,String>map,Map<String ,Object>resultMap) throws ParseException {
        if (AssertUtil.isEmpty(map.get("attendanceId"))) {
            resultMap.put("code",100);
            resultMap.put("msg","考勤组id参数不得为空");
            return false;
        } else if (AssertUtil.isEmpty(map.get("attendanceDate"))) {
            resultMap.put("code",100);
            resultMap.put("msg","考勤日期参数不得为空");
            return false;
        } else if (!TimeUtil.checkDateFormat(map.get("attendanceDate"), TimeUtil.BASE_DATE_FORMAT)) {
            resultMap.put("code",203);
            resultMap.put("msg","考勤日期格式有误");
            return false;
        } else if (TimeUtil.dateDiff(
            TimeUtil.string2Date(map.get("attendanceDate")),
            TimeUtil.string2Date(TimeUtil.getCurrentDate())) < 0 ) {
            resultMap.put("code",204);
            resultMap.put("msg","查询日期非法：超过当前日期");
            return false;
        } else if (AssertUtil.isEmpty(map.get("type"))) {
            resultMap.put("code",100);
            resultMap.put("msg","查询类型不得为空");
            return false;
        }
        return true;
    }

    private boolean validParamsEnterId(Map<String ,String>map,Map<String ,Object>resultMap) throws ParseException {
        if (AssertUtil.isEmpty(map.get("enterId"))) {
            resultMap.put("code",100);
            resultMap.put("msg","enterId参数不得为空");
            return false;
        }
        return true;
    }

    private boolean validSignature(Map<String ,String>map,Map<String ,Object>resultMap) {
        if (AssertUtil.isEmpty(map.get("once"))) {
            resultMap.put("code",107);
            resultMap.put("msg","once不得为空");
            return false;
        }
        String app_key = "d7eec29775ca42a894ab3ce432667e70";
        String once = map.get("once");
        String version = "1.0";
        String channel = "d7eec29775ca42a894ab3ce432667e70";
        String sdk = "java";
        String QYTXL_APPKEY= "ca7dc22b57fa45a7a6a8eb89a3dc7b49";
        HashMap<String, String> Map = new HashMap<String, String>();
        Map.put("app_key", app_key);
        Map.put("once", once);
        Map.put("version", version);
        Map.put("channel", channel);
        Map.put("sdk", sdk);
        Map.put("attendanceId", map.get("attendanceId"));
        Map.put("attendanceDate", map.get("attendanceDate"));
        Map.put("type", map.get("type"));
        Map.put("enterId", map.get("enterId"));
        String signature = EnterpriseUtil.getNornmalSignature(Map,QYTXL_APPKEY);
        if (!signature.equals(map.get("signature"))) {
            resultMap.put("code",104);
            resultMap.put("msg","参数签名有误");
            logger.error("app_key={}|once={}|version={}|channel={}|sdk={}|attendanceId={}|attendanceDate={}|signature={}|QYTXL_APPKEY={}",
                app_key,once,version,channel,sdk,map.get("attendanceId"),map.get("attendanceDate"),signature,QYTXL_APPKEY);
            return false;
        }
        return true;
    }

    private boolean validAttendanceId(Map<String ,String>map,Map<String ,Object>resultMap) {
        if (AssertUtil.isEmpty(map.get("enterId")) && "123420282".equals(map.get("enterId"))) {
            resultMap.put("code",110);
            resultMap.put("msg","查询企业不存在或不合法");
            return false;
        }
        String attendanceId = map.get("attendanceId");
        List<String> attendanceIds = attendGroupService.getAttendanceIdByEnterId(map.get("enterId"));
        if (AssertUtil.isEmpty(attendanceIds)) {
            resultMap.put("code",108);
            resultMap.put("msg","该企业无可用考勤组");
            return false;
        }

        if (!attendanceIds.contains(attendanceId)) {
            resultMap.put("code",109);
            resultMap.put("msg","查询的考勤组不属于该企业，不允许查询");
            return false;
        }
        return true;
    }

    private boolean validSignatureEnterId(Map<String ,String>map,Map<String ,Object>resultMap) {
        if (AssertUtil.isEmpty(map.get("once"))) {
            resultMap.put("code",107);
            resultMap.put("msg","once不得为空");
            return false;
        }
        String app_key = "d7eec29775ca42a894ab3ce432667e70";
        String once = map.get("once");
        String version = "1.0";
        String channel = "d7eec29775ca42a894ab3ce432667e70";
        String sdk = "java";
        String QYTXL_APPKEY= "ca7dc22b57fa45a7a6a8eb89a3dc7b49";
        HashMap<String, String> Map = new HashMap<String, String>();
        Map.put("app_key", app_key);
        Map.put("once", once);
        Map.put("version", version);
        Map.put("channel", channel);
        Map.put("sdk", sdk);
        Map.put("enterId", map.get("enterId"));
        String signature = EnterpriseUtil.getNornmalSignature(Map,QYTXL_APPKEY);
        if (!signature.equals(map.get("signature"))) {
            resultMap.put("code",104);
            resultMap.put("msg","参数签名有误");
            logger.error("app_key={}|once={}|version={}|channel={}|sdk={}|signature={}|QYTXL_APPKEY={}",
                app_key,once,version,channel,sdk,signature,QYTXL_APPKEY);
            return false;
        }
        return true;
    }

    private static String ListToString(List<?> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
//                if (list.get(i) instanceof List) {
//                    sb.append(ListToString((List<?>) list.get(i)));
//                    sb.append(SEP1);
//                } else if (list.get(i) instanceof Map) {
//                    sb.append(MapToString((Map<?, ?>) list.get(i)));
//
//                }
                sb.append(list.get(i));
                sb.append(",");
            }
        }
        return sb.toString();
    }
}

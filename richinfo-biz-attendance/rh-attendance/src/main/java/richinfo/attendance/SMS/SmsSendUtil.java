package richinfo.attendance.SMS;

import cn.hutool.http.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.util.AttendanceConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名称:SmsSendUtil
 * 类描述:短信发送工具类
 * 创建人:hetianhuai
 * 创建时间:2019/6/27 16:20
 * Version 1.0
 */
public class SmsSendUtil {

    private static final Logger logger = LoggerFactory.getLogger(SmsSendUtil.class);

    //    private static final String BASE_URL = "http://221.176.34.113:8080/demo/a2p-app/";
    private static final String BASE_URL = "https://rcsoa-my.zone139.com:10285/a2p-app/";

    private static final String SMS_SEND_WITHIN_ADDRESSBOOK_URL = BASE_URL + "sms/sendSms";
    private static final String SMS_SEND_WITHOUT_ADDRESSBOOK_URL = BASE_URL + "sms/sendSmsWithoutAddressBook";
    private static final String TEMPLATE_CREATE_URL = BASE_URL + "template/addTemplate";
    private static final String TEMPLATE_SELECT_URL = BASE_URL + "template/selectByCreateIdTemplate";
    private static final String GET_SEND_RESULT_URL = BASE_URL + "sms/getSendResult";

    //    private static final String SECRET_ID = "zgyd1";
//    private static final String SECRET_KEY = "88assadsfsdfsffsf6dsfsdfd";
//    private static final String TOKEN = "9adkcieiq907a2pmli09";
//    private static final String SECRET_ID = "KBvMyuuljsRY6";
//    private static final String SECRET_KEY = "3r3J8Sfi5j5Y1DUMeFlPljsRY6";
    private static final String SECRET_ID = AttendanceConfig.getInstance().getProperty("attend.qytxl.secretId","7g7lXzVS55nJv");
    private static final String SECRET_KEY = AttendanceConfig.getInstance().getProperty("attend.qytxl.secretKey","EPRpv8IWpIPQSj1V24ZVS55nJv");
    private static final String TOKEN = "9adkcieiq907a2pmli09";

    private static final String TEMPLATE_CONTENT = "【和飞信-考勤打卡】小和提醒您 ：准备开始新一天的工作啦，不要忘记打卡哦~{[placeholder:url]}{[placeholder:remark]}";
    private static final String GET_OFF_WORK_CONTENT= "【和飞信-考勤打卡】小和提醒您 ：下班啦，快好好放松吧，不要忘记打卡哦~{[placeholder:url]}{[placeholder:remark]}";
    private static final String TEMPLATE_HAS_PLACE_HOLDER = "1";

//    private static int TEMPLATE_ID = 0x201901;
//    private static int GET_OF_WORK_TEMPLATE_ID = 0x201902;
    private static int TEMPLATE_ID = AttendanceConfig.getInstance().getPropertyInt("attend.qytxl.templateId");
    private static int GET_OF_WORK_TEMPLATE_ID = AttendanceConfig.getInstance().getPropertyInt("attend.qytxl.getOfWorkTemplateId");
    private static final int CODE_SEND_SUCCESS = 0;
    private static final int CODE_ID_NOT_EXISTS = -114;

    /**
     * 发送短信(接收人是通讯录成员)
     * @param enterDeptId 企业id
     * @param contactIds 联系人id（若多个以“,”分割） 暂时逐个发送
     * @param appKey 创建企业时传入的appKey
     * @param placeHolderContent 占位符的替换内容
     * @param retryNum 重试次数
     * @param msgType 消息类型 1：签到  2：签退
     */
    public static boolean sendSmsWithInAddressBook(String enterDeptId, String contactIds, String appKey,
                                                   String placeHolderContent, int retryNum, boolean isRetry, int msgType){
        logger.info("短信发送开始！");
        boolean sendResult = false;
        logger.info("TemplateId:{},getTemplateId:{}",TEMPLATE_ID,GET_OF_WORK_TEMPLATE_ID);
        try {
            //关闭创建模板功能 由于已经配置默认模板暂时关闭线上创建模板功能
            // 检查短信模板是否存在，模板不存在，创建模板
//            if(!isRetry){
//                boolean existTemplate = false;
//                if(msgType == 1) {
//                    existTemplate = SmsSendUtil.checkTemplate(TEMPLATE_ID);
//                } else if(msgType == 2) {
//                    existTemplate = SmsSendUtil.checkTemplate(GET_OF_WORK_TEMPLATE_ID);
//                }
//                if (!existTemplate) {
//                    System.out.println("模板不存在! ");
//                    Object templateId = SmsSendUtil.createTemplate(msgType);
//                    if (templateId != null && templateId instanceof Integer){
//                        System.out.println("重置模板Id! templateId : " + templateId);
//                        if(msgType == 1) {
//                            TEMPLATE_ID = ((Integer) templateId).intValue();
//                        } else if(msgType == 2) {
//                            GET_OF_WORK_TEMPLATE_ID = ((Integer) templateId).intValue();
//                        }
//                    } else{
//                        return sendResult;
//                    }
//                }
//            }

            // 发送短信消息
            String requestBody = null;
            if(msgType == 1) {
                requestBody = SmsSendUtil.getSmsSendMsg(enterDeptId, contactIds, appKey, TEMPLATE_ID, placeHolderContent);
            } else if(msgType == 2) {
                requestBody = SmsSendUtil.getSmsSendMsg(enterDeptId, contactIds, appKey, GET_OF_WORK_TEMPLATE_ID, placeHolderContent);
            }
            JSONObject outPutJson = SmsSendUtil.httpRequest(requestBody, SMS_SEND_WITHIN_ADDRESSBOOK_URL);
            -- retryNum;
            if (outPutJson == null){
                logger.info("短信发送失败，请求路径出错！");
                return sendResult;
            }
            JSONObject resStatus = outPutJson.getJSONObject("resStatus");
            int resCode = Integer.parseInt(String.valueOf(resStatus.get("resCode")));
            if (resCode == CODE_SEND_SUCCESS){
                String result = String.valueOf(outPutJson.get("result"));
                String decrypt = AESUtil.aesDecrypt(result, AESUtil.getAESKey(SECRET_KEY));
                logger.debug(decrypt);
                if (SmsSendUtil.isJSONObjectStr(decrypt)){
                    JSONObject resultJson = JSONObject.parseObject(decrypt);
                    String taskId = resultJson.getString("taskId");
                    // 获取短信发送结果
                    sendResult = true;
                    /*SendResultInfo sendResultInfo = SmsSendUtil.getSendResult(taskId, contactIds, 1);
                    sendResult = sendResultInfo.getResult();
                    // 发送失败，重新发送
                    if (!sendResult && retryNum > 0){
                        //contactIds = sendResultInfo.getRetryContacts();
                        System.out.println("短信发送失败，重新发送! retryNum : " + retryNum + ", retryContactIds" + contactIds);
                        sendResult = SmsSendUtil.sendSmsWithInAddressBook(enterDeptId, contactIds, appKey, placeHolderContent, retryNum, true);
                    }*/
                }
            } else if (resCode == CODE_ID_NOT_EXISTS) {
                // 模板不存在，创建模板
                //关闭创建模板功能 由于已经配置默认模板暂时关闭线上创建模板功能
//                Object templateId = SmsSendUtil.createTemplate(msgType);
//                if (templateId != null && templateId instanceof Integer){
//                    // 重置模板Id，递归发送
//                    System.out.println("重置模板Id，递归发送! templateId : " + templateId);
//                    if(msgType == 1) {
//                        TEMPLATE_ID = ((Integer) templateId).intValue();
//                    } else if(msgType == 2) {
//                        GET_OF_WORK_TEMPLATE_ID = ((Integer) templateId).intValue();
//                    }
//
//                    sendResult = SmsSendUtil.sendSmsWithInAddressBook(enterDeptId, contactIds, appKey, placeHolderContent, retryNum, true, msgType);
//                }
                logger.info("短信发送失败! 模板不存在");
               sendResult = false;
            } else {
                logger.info("短信发送失败! ResCode : " + resCode + ", ResMsg : " + resStatus.get("resMsg"));
            }
        } catch (Exception e) {
            logger.error("短信发送失败: " + e);
        }
        return sendResult;
    }

    /**
     * 发送短信(接收人非通讯录成员)
     * @param mobiles 手机号（若多个以“,”分割） 暂时逐个发送
     * @param contactNames 人员名称（若多个以“,”分割） 注意：名称顺序和数量contactIds保持一致
     * @param enterDeptName 团队或组织名称
     * @param placeHolderContent 占位符的替换内容
     * @param retryNum 重试次数
     * @param msgType 消息类型 1：签到  2：签退
     */
    public static boolean sendSmsWithOutAddressBook(String mobiles, String contactNames, String enterDeptName,
                                                    String placeHolderContent, int retryNum, boolean isRetry, int msgType){
        logger.info("send sms begin");
        boolean sendResult = false;
        -- retryNum;
        try {
            // 检查短信模板是否存在，模板不存在，创建模板
            if (!isRetry){
                boolean existTemplate = false;
                if(msgType == 1) {
                    existTemplate = SmsSendUtil.checkTemplate(TEMPLATE_ID);
                } else if(msgType == 2) {
                    existTemplate = SmsSendUtil.checkTemplate(GET_OF_WORK_TEMPLATE_ID);
                }
                if (!existTemplate) {
                    logger.info("短信发送模板不存在，需要创建模板!");
                    Object templateId = SmsSendUtil.createTemplate(msgType);
                    if (templateId != null && templateId instanceof Integer){
                        logger.info("重置模板Id! templateId : {}", templateId);
                        if(msgType == 1) {
                            TEMPLATE_ID = ((Integer) templateId).intValue();
                        } else if(msgType == 2) {
                            GET_OF_WORK_TEMPLATE_ID = ((Integer) templateId).intValue();
                        }
                    }
                }
            }

            // 发送短信消息
            String requestBody = null;
            if(msgType == 1) {
                requestBody = SmsSendUtil.getSmsWithOutAddressBookMsg(mobiles, contactNames, enterDeptName, TEMPLATE_ID, placeHolderContent);
            } else if(msgType == 2) {
                requestBody = SmsSendUtil.getSmsWithOutAddressBookMsg(mobiles, contactNames, enterDeptName, GET_OF_WORK_TEMPLATE_ID, placeHolderContent);
            }
            JSONObject outPutJson = SmsSendUtil.httpRequest(requestBody, SMS_SEND_WITHOUT_ADDRESSBOOK_URL);

            if (outPutJson == null){
                logger.info("短信发送失败，请求路径出错！");
                return sendResult;
            }
            JSONObject resStatus = outPutJson.getJSONObject("resStatus");
            int resCode = Integer.parseInt(String.valueOf(resStatus.get("resCode")));
            logger.info("短信发送返回结果：{}", outPutJson);
            if (resCode == CODE_SEND_SUCCESS){
                String result = String.valueOf(outPutJson.get("result"));
                String decrypt = AESUtil.aesDecrypt(result, AESUtil.getAESKey(SECRET_KEY));
                if (SmsSendUtil.isJSONObjectStr(decrypt)){
                    JSONObject resultJson = JSONObject.parseObject(decrypt);
                    String taskId = resultJson.getString("taskId");
                    // 获取短信发送结果
                    sendResult = true;
                    /*SendResultInfo sendResultInfo = SmsSendUtil.getSendResult(taskId, mobiles, 2);
                    sendResult = sendResultInfo.getResult();
                    // 发送失败，重新发送
                    if (!sendResult && retryNum > 0){
                        //mobiles = sendResultInfo.getRetryContacts();
                        System.out.println("短信发送失败，重新发送! retryNum : " + retryNum + ", retryMobiles: " + mobiles);
                        sendResult = SmsSendUtil.sendSmsWithOutAddressBook(mobiles, contactNames, enterDeptName, placeHolderContent, retryNum, true);
                    }*/
                }
            } else {
                logger.info("短信发送失败! ResCode :{}, ResMsg : {}", resCode, resStatus.get("resMsg"));
                //重试
                if (retryNum > 0){
                    sendResult = SmsSendUtil.sendSmsWithOutAddressBook(mobiles, contactNames,
                        enterDeptName, placeHolderContent, retryNum, true, msgType);
                }
            }
        } catch (Exception e) {
            logger.info("短信发送失败：{}", e.getMessage());
            if (retryNum > 0){
                sendResult = SmsSendUtil.sendSmsWithOutAddressBook(mobiles, contactNames,
                    enterDeptName, placeHolderContent, retryNum, true, msgType);
            }
        }
        return sendResult;
    }

    /**
     * 获取短信发送结果
     * @param taskId 任务id
     * @param contactsOrMobiles 联系人id或手机号（若多个以“,”分割）
     * @param type 1 通讯录成员 2 非通讯录成员
     * @return
     */
    public static SendResultInfo getSendResult(String taskId, String contactsOrMobiles, int type){
        System.out.println("获取短信发送结果开始! taskId: " + taskId);
        try {
            String requestBody = SmsSendUtil.getSendResultMsg(taskId);
            JSONObject outPutJson = SmsSendUtil.httpRequest(requestBody, GET_SEND_RESULT_URL);
            if (outPutJson == null)
                return new SendResultInfo(false, contactsOrMobiles);
            JSONObject resStatus = outPutJson.getJSONObject("resStatus");
            int resCode = Integer.parseInt(String.valueOf(resStatus.get("resCode")));
            if (resCode == CODE_SEND_SUCCESS) {
                String result = String.valueOf(outPutJson.get("result"));
                String decrypt = AESUtil.aesDecrypt(result, AESUtil.getAESKey(SECRET_KEY));
                System.out.println(decrypt);
                if (SmsSendUtil.isJSONObjectStr(decrypt)) {
                    JSONObject resultJson = JSONObject.parseObject(decrypt);
                    //String smsSendResult = resultJson.getString("smsSendResult");
                    int failureTotalNum = resultJson.getInteger("failureTotalNum");
                    if (failureTotalNum == 0){
                        return new SendResultInfo(true, null);
                    } else {
                        String failureList = resultJson.getString("failureList");
                        if (SmsSendUtil.isJSONArrayStr(failureList)){
                            JSONArray jsonArray = JSONArray.parseArray(failureList);
                            String retryContactIds = "";
                            String retryMobiles = "";
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject failure = jsonArray.getJSONObject(i);
                                System.out.println(failure.toJSONString());
                                String contactId = failure.getString("contactId");
                                String mobile = failure.getString("mobile");
                                if (i != jsonArray.size() - 1) {
                                    retryContactIds += contactId + ",";
                                    retryMobiles += mobile + ",";
                                } else {
                                    retryContactIds += contactId;
                                    retryMobiles += mobile;
                                }
                            }
                            if (type == 1 && !retryContactIds.equals(""))
                                return new SendResultInfo(false, retryContactIds);
                            if (type == 2 && !retryMobiles.equals(""))
                                return new SendResultInfo(false, retryMobiles);
                        }
                    }
                }
            } else {
                System.out.println("获取发送短信结果失败! ResCode : " + resCode + ", ResMsg : " + resStatus.get("resMsg"));
            }
        } catch (Exception e) {
            System.out.println("获取发送短信结果失败: " + e.getMessage());
        }
        return new SendResultInfo(false, contactsOrMobiles);
    }

    /**
     * 查询短信模板是否存在
     * @param templateId
     * @return
     */
    public static boolean checkTemplate(int templateId){
        logger.info("查询短信模板开始! ");
        int retryNum = 0;
        while (true) {
            if(retryNum > 2) {
                break;
            }
            retryNum++;

            try {
                String requestBody = SmsSendUtil.getTemplateSelectMsg(templateId);
                JSONObject outPutJson = SmsSendUtil.httpRequest(requestBody, TEMPLATE_SELECT_URL);
                if (outPutJson == null)
                    return false;
                int resCode = Integer.parseInt(String.valueOf(outPutJson.get("resultCode")));
                if (resCode == CODE_SEND_SUCCESS){
                    String resultData = String.valueOf(outPutJson.get("resultData"));
                    String decrypt = AESUtil.aesDecrypt(resultData, AESUtil.getAESKey(SECRET_KEY));
                    if (SmsSendUtil.isJSONArrayStr(decrypt)){
                        JSONArray jsonArray = JSONArray.parseArray(decrypt);
                        if (jsonArray.size() > 0){
                            logger.info("查询短信模板成功!");
                            return true;
                        }
                    }

                } else {
                    logger.info("查询短信模板失败! ResCode : {}, ResMsg :{}", resCode, outPutJson.get("resultMsg"));
                }

                break;
            } catch (Exception e) {
                logger.info("查询短信模板失败: {}", e.toString());
            }
        }

        return false;
    }

    /**
     * 创建短信模板
     * @return
     */
    public static Object createTemplate(int msgType){
        logger.info("创建模板开始! ");
        int retryNum = 0;
        while (true) {
            if (retryNum > 2) {
                break;
            }
            retryNum++;

            try {
                String requestBody = SmsSendUtil.getTemplateCreateMsg(msgType);
                JSONObject outPutJson = SmsSendUtil.httpRequest(requestBody, TEMPLATE_CREATE_URL);
                if (outPutJson == null)
                    return null;

                String resultMsg = String.valueOf(outPutJson.get("resultMsg"));
                int resCode = Integer.parseInt(String.valueOf(outPutJson.get("resultCode")));
                if (resCode == CODE_SEND_SUCCESS){
                    logger.info("创建短信模板成功!");
                    return outPutJson.get("resultData");
                } else {
                    logger.info("创建短信模板失败! ResCode : {}, ResMsg : {}", resCode, resultMsg);
                }

                break;
            } catch (Exception e) {
                logger.info("创建短信模板失败 : {}", e.toString());
            }
        }

        return null;
    }

    /**
     * HTTP请求
     * @param requestBody 请求消息体
     * @param url 请求路径
     * @return
     * @throws IOException
     */
    private static JSONObject httpRequest(String requestBody, String url) throws IOException {
        logger.debug("Request Begin! \n  url: " + url + ",\n  body: " + requestBody);
        HttpResponse httpResponse = HttpRequest.post(url).body(requestBody).header(Header.CONTENT_TYPE, ContentType.JSON.toString()).execute();
        if (httpResponse.getStatus() == HttpStatus.HTTP_OK){
            String outPut = SmsSendUtil.copyToString(httpResponse.bodyStream());
            if (SmsSendUtil.isJSONObjectStr(outPut)){
                logger.debug("Request Success!");
                return JSONObject.parseObject(outPut);
            }
        }
        logger.info("短信请求url:" + url);
        logger.info("短信请求body:" + requestBody);
        logger.info("Request Fail! Response Status : " + httpResponse.getStatus());
        return null;
    }

    /**
     * 短信发送请求消息体
     * @param enterDeptId
     * @param contactIds
     * @param appKey
     * @param templateId
     * @param placeHolderContent
     * @return
     * @throws Exception
     */
    private static String getSmsSendMsg(String enterDeptId, String contactIds, String appKey, Integer templateId, String placeHolderContent) throws Exception {
        String encrypt = SmsSendUtil.getSmsEncryptMsg(enterDeptId, contactIds, appKey, templateId, placeHolderContent);
        return SmsSendUtil.getRequestBody(encrypt);
    }

    /**
     * 短信发送请求消息体
     * @param mobiles
     * @param contactNames
     * @param enterDeptName
     * @param templateId
     * @param placeHolderContent
     * @return
     * @throws Exception , , ,
     */
    private static String getSmsWithOutAddressBookMsg(String mobiles, String contactNames, String enterDeptName, Integer templateId, String placeHolderContent) throws Exception {
        String encrypt = SmsSendUtil.getSmsWithOutAddressBookEncryptMsg(mobiles, contactNames, enterDeptName, templateId,  placeHolderContent);
        return SmsSendUtil.getRequestBody(encrypt);
    }

    /**
     * 模板查询请求消息体
     * @return
     * @throws Exception
     */
    private static String getTemplateSelectMsg(int templateId) throws Exception {
        String encrypt = SmsSendUtil.getTemplateSelectEncryptMsg(templateId);
        return SmsSendUtil.getRequestBody(encrypt);
    }

    /**
     * 模板创建请求消息体
     * @return
     * @throws Exception
     */
    private static String getTemplateCreateMsg(int msgType) throws Exception {
        String encrypt = SmsSendUtil.getTemplateEncryptMsg(msgType);
        return SmsSendUtil.getRequestBody(encrypt);
    }

    /**
     * 获取短信发送结果请求消息体
     * @param taskId
     * @return
     * @throws Exception
     */
    private static String getSendResultMsg(String taskId) throws Exception {
        String encrypt = SmsSendUtil.getSendResultEncryptMsg(taskId);
        return SmsSendUtil.getRequestBody(encrypt);
    }

    /**
     * 通用请求消息体创建
     * @param encrypt
     * @return
     */
    private static String getRequestBody(String encrypt){
        String timeStamp = SignatureUtil.getTimeStamp();//时间戳
        String nonce = SignatureUtil.getRandomStr(6);//随机字符串
        Map requestBody = new HashMap();
        requestBody.put("timeStamp", timeStamp);
        requestBody.put("nonce", nonce);
        requestBody.put("secretId", SECRET_ID);
        requestBody.put("signature", SignatureUtil.createSignature(timeStamp, nonce, SECRET_ID));//消息体签名
        requestBody.put("encrypt",encrypt);//加密字符串
        return JSONObject.toJSONString(requestBody);
    }

    /**
     * 短信发送加密信息
     * @param enterDeptId
     * @param contactIds
     * @param appKey
     * @param templateId
     * @param placeHolderContent
     * @return
     * @throws Exception
     */
    private static String getSmsEncryptMsg(String enterDeptId, String contactIds, String appKey, Integer templateId, String placeHolderContent) throws Exception {
        Map encryptMap = new HashMap();
        encryptMap.put("enterDeptId", enterDeptId);
        encryptMap.put("contactIds", contactIds);
        encryptMap.put("appKey", appKey);
        encryptMap.put("templateId", templateId);
        if (placeHolderContent != null){
            encryptMap.put("placeHolderContent", placeHolderContent);
        }
        return AESUtil.aesEncrypt(JSONObject.toJSONString(encryptMap), AESUtil.getAESKey(SECRET_KEY));
    }

    /**
     * 短信发送加密信息
     * @param mobiles
     * @param contactNames
     * @param enterDeptName
     * @param templateId
     * @param placeHolderContent
     * @return
     * @throws Exception
     */
    private static String getSmsWithOutAddressBookEncryptMsg(String mobiles, String contactNames, String enterDeptName, Integer templateId, String placeHolderContent) throws Exception {
        Map encryptMap = new HashMap();
        encryptMap.put("mobiles", mobiles);
        encryptMap.put("contactNames", contactNames);
        encryptMap.put("enterDeptName", enterDeptName);
        encryptMap.put("templateId", templateId);
        if (placeHolderContent != null){
            encryptMap.put("placeHolderContent", placeHolderContent);
        }
        return AESUtil.aesEncrypt(JSONObject.toJSONString(encryptMap), AESUtil.getAESKey(SECRET_KEY));
    }

    /**
     * 模板查询加密信息
     * @return
     * @throws Exception
     */
    private static String getTemplateSelectEncryptMsg(int templateId) throws Exception {
        Map encryptMap = new HashMap();
        encryptMap.put("secretId", SECRET_ID);
        encryptMap.put("pageSize", 10);
        encryptMap.put("pageNum", 1);
        encryptMap.put("templateId", String.valueOf(templateId));
        return AESUtil.aesEncrypt(JSONObject.toJSONString(encryptMap), AESUtil.getAESKey(SECRET_KEY));
    }

    /**
     * 模板创建加密信息
     * @return
     * @throws Exception
     */
    private static String getTemplateEncryptMsg(int msgType) throws Exception {
        Map encryptMap = new HashMap();
        encryptMap.put("secretId", SECRET_ID);
        if(msgType == 1) {
            encryptMap.put("content", TEMPLATE_CONTENT);
        } else if(msgType == 2) {
            encryptMap.put("content", GET_OFF_WORK_CONTENT);
        }
        encryptMap.put("hasPlaceholder", TEMPLATE_HAS_PLACE_HOLDER);
        return AESUtil.aesEncrypt(JSONObject.toJSONString(encryptMap), AESUtil.getAESKey(SECRET_KEY));
    }

    /**
     * 获取短信发送结果加密信息
     * @param taskId
     * @return
     * @throws Exception
     */
    private static String getSendResultEncryptMsg(String taskId) throws Exception {
        Map encryptMap = new HashMap();
        encryptMap.put("taskId", taskId);
        return AESUtil.aesEncrypt(JSONObject.toJSONString(encryptMap), AESUtil.getAESKey(SECRET_KEY));
    }

    /**
     * 输入流转换为字符串
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String copyToString(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null){
            buffer.append(line);
        }
        return buffer.toString();
    }

    /**
     * 是否是JSONObject格式字符串
     * @param str
     * @return
     */
    public static boolean isJSONObjectStr(String str){
        if (str != null && str.startsWith("{") && str.endsWith("}")){
            return true;
        }
        return false;
    }

    /**
     * 是否是JSONArray格式字符串
     * @param str
     * @return
     */
    public static boolean isJSONArrayStr(String str){
        if (str != null && str.startsWith("[") && str.endsWith("]")){
            return true;
        }
        return false;
    }
}
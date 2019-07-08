/**
 * 文件名：UMCUtil.java
 * 创建日期： 2017年6月5日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AttendLoginReq;
import richinfo.attendance.bean.RcsCheckAdminRes;
import richinfo.attendance.bean.UMCReqBean;
import richinfo.attendance.bean.UMCReqBody;
import richinfo.attendance.bean.UMCReqHeader;
import richinfo.attendance.bean.UMCResBean;
import richinfo.attendance.bean.UmcArtifactValidRes;
import richinfo.attendance.bean.UmcGetArtifactRes;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.common.ResultCode;
import richinfo.attendance.entity.UserInfo;
import richinfo.components.invoke.client.CallService;
import richinfo.components.invoke.client.connector.HttpParamVo;

/**
 * 功能描述： 统一认证接口操作辅助类
 */
public class UMCUtil
{
    private static Logger log = LoggerFactory.getLogger(UMCUtil.class);
    private static volatile UMCUtil instance;

    /**
     * 统一认证接口地址 内部地址：https://10.55.0.129:8443/api/tokenValidate
     * (内部配置最终走这个IP：10.200.1.22)
     */
    private String UMC_URL = AttendanceConfig.getInstance().getProperty(
        "attend.umc.url", "https://wap.cmpassport.com:8443/api/tokenValidate");
    /**
     * PC端单点登录token校验
     */
    private String UMC_VALIED_ARTICFACT = AttendanceConfig.getInstance()
        .getProperty("attend.validArtifact.url",
            "http://121.15.167.251:10010/UmcWeb/s?func=ArtifactCheckRequest");
    /**
     * PC端获取凭证
     */                       
    private String UMC_GET_ARTICFACT = AttendanceConfig.getInstance()
        .getProperty("attend.getArtifact.url",
            "http://auth.cmpassport.com:6500/UmcWeb/s?func=GetArtifactRequest");
    /**
     * 统一认证sourceID
     */
    private String UMC_SOURCEID = AttendanceConfig.getInstance().getProperty(
        "attend.umc.sourceid", "001069");

    /**
     * RCS管理员权限校验
     */
    private String RCS_CHECKADMIN_URL = AttendanceConfig.getInstance()
        .getProperty("attend.rcs.checkadmin.url",
            "http://117.136.240.59/rcsoa_receive/validate/isAdmin.do");

    /**
     * RCS管理员权限校验
     */
    private String RCS_CHECKADMIN_SUCCESS = "success";

    /**
     * 单例模式
     */
    public static UMCUtil getInstance()
    {
        if (null == instance)
        {
            synchronized (UMCUtil.class)
            {
                if (null == instance)
                {
                    instance = new UMCUtil();
                    return instance;
                }
            }
        }
        return instance;
    }

    /**
     * 通过统一认证平台认证token的有效性
     * @param token
     * @return
     */
    public UMCResBean checkTokenByUMC(String token, String uid, String enterId)
    {
        UMCResBean resBean = new UMCResBean();

        String resultJson = null;
        HttpParamVo reqVo = umcParam(token);
        try
        {
            // 使用通讯组件调用UMC token校验接口
            resultJson = new String((byte[]) CallService.callService(UMC_URL,
                reqVo), "utf-8");
            log.info(
                "checkTokenByUMC uid={}|enterId={}|reqJson={}|resultJson={}",
                uid, enterId, reqVo.getParam(), resultJson);

            // 校验结果解析
            resBean = JsonUtil.jsonToBean(resultJson, UMCResBean.class);

        }
        catch (Exception e)
        {
            log.error(
                "checkTokenByUMC call socket server failed! uid={}|enterId={}|reqJson={}",
                uid, enterId, reqVo.getParam(), e);
            // UMC接口稳定性不佳，失败进行一次重试
            try
            {
                // 有网络问题？，先睡眠10ms
                Thread.sleep(10);
                // 使用通讯组件调用UMC token校验接口
                resultJson = new String((byte[]) CallService.callService(
                    UMC_URL, reqVo), "utf-8");
                log.info(
                    "second checkTokenByUMC  uid={}|enterId={}|reqJson={}|resultJson={}",
                    uid, enterId, reqVo.getParam(), resultJson);

                // 校验结果解析
                resBean = JsonUtil.jsonToBean(resultJson, UMCResBean.class);
            }
            catch (Exception e2)
            {
                log.error(
                    "second checkTokenByUMC call socket server,  still failed! uid={}|enterId={}|token={}",
                    uid, enterId, token, e2);
            }

        }

        return resBean;
    }

    /**
     * 统一认证请求参数组装
     * {"header":{"apptype":"2","msgid":"1443434372620","sourceid":"100000",
     * "systemtime":"20170317185000123","version":"1.0"},"body":{"token":
     * "8484xxxx"}}
     * @param token
     * @return
     */
    private HttpParamVo umcParam(String token)
    {
        HttpParamVo reqVo = new HttpParamVo();
        UMCReqBean reqBean = new UMCReqBean();

        // 请求头head
        UMCReqHeader head = new UMCReqHeader();
        head.setVersion("1.0");
        head.setMsgid(UUID.randomUUID().toString());
        head.setSystemtime(TimeUtil
            .getCurrentDateTime(TimeUtil.DATETIME_FORMAT_yyyyMMddHHmmssSSS));
        head.setSourceid(UMC_SOURCEID);
        // 业务平台编码定义 1BOSS、2web 、3wap 、4pc客户端 、5手机客户端
        head.setApptype("3");
        reqBean.setHeader(head);

        // 请求body
        UMCReqBody body = new UMCReqBody();
        body.setToken(token);
        reqBean.setBody(body);

        // UMC接口指定contentType
        String contentType = "application/json;charset=utf-8";
        reqVo.setContentType(contentType);
        reqVo.setParam(JsonUtil.beanToJson(reqBean));

        return reqVo;
    }

    /**
     * 通过RCS检验确认管理员省份，原则上只有管理员登录的时候校验即可
     * @param enterDeptId
     * @param isAdmin
     * @param phone
     * @return
     */
    public boolean checkAdminByRCS(String enterDeptId, String isAdmin,
        String phone, String uid) throws ClientProtocolException, IOException
    {
        boolean checkResult = false;
        // 按接口要求，组装内容体
        String content = createContent(enterDeptId, isAdmin, phone);
        checkResult = baseCheckAdminByRCS(enterDeptId, isAdmin, phone, uid,
            content);
        return checkResult;

    }

    /**
     * 通过RCS检验确认管理员省份，底层调用接口处理操作
     * @param enterId
     * @param isAdmin
     * @param phone
     * @param uid
     * @param content
     * @return
     */
    private boolean baseCheckAdminByRCS(String enterId, String isAdmin,
        String phone, String uid, String content)
    {
        boolean checkResult = false;

        HttpClient httpClient = null;
        HttpResponse response = null;
        String result = "";
        log.info(
            "baseCheckAdminByRCS start enterId={}|isAdmin={}|phone={}|uid={}",
            enterId, isAdmin, phone, uid);
        try
        {
            // 初始化连接
            httpClient = new DefaultHttpClient();
            // 请求超时 3s
            httpClient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            // 读取超时 3s
            httpClient.getParams().setParameter(
                CoreConnectionPNames.SO_TIMEOUT, 3000);
            HttpPost httpPost = new HttpPost(RCS_CHECKADMIN_URL);

            // 组装参数
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String timeStamp = SignatureUtil.getTimeStamp();
            params.add(new BasicNameValuePair("timeStamp", timeStamp));
            String nonce = SignatureUtil.getRandomStr();
            params.add(new BasicNameValuePair("nonce", nonce));
            String msg_signature = SignatureUtil.createSignature(timeStamp,
                nonce);
            params.add(new BasicNameValuePair("msg_signature", msg_signature));
            String encrypt = AESEncryptUtil.aesEncrypt(content,
                AESEncryptUtil.getAESKey(AESEncryptUtil.ENCODING_AES_KEY));
            params.add(new BasicNameValuePair("encrypt", encrypt));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            // 执行请求 处理返回的结果
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                String reContent = EntityUtils.toString(entity, "UTF-8");
                // json解析 获取加密串字段来解密
                RcsCheckAdminRes res = JsonUtil.jsonToBean(reContent,
                    RcsCheckAdminRes.class);
                if (AssertUtil.isNotEmpty(res))
                {
                    result = AESEncryptUtil.aesDecrypt(res.getEncrypt(),
                        AESEncryptUtil
                            .getAESKey(AESEncryptUtil.ENCODING_AES_KEY));// 返回的结果
                    log.info(
                        "baseCheckAdminByRCS result={}|enterId={}|isAdmin={}|phone={}|uid={}",
                        result, enterId, isAdmin, phone, uid);
                    // 如果解密后的字段是"success"则表示鉴权成功
                    if (RCS_CHECKADMIN_SUCCESS.equals(result))
                    {
                        checkResult = true;
                    }
                }
                EntityUtils.consume(entity);
            }
            else
            {
                // 如果接口异常，默认给过，防止接口异常导致管理员无法操作
                // 后续可根据实际情况调整true或者false
                checkResult = true;
                log.error(
                    "baseCheckAdminByRCS failure!phone={}|uid={}|timeStamp={}|nonce={}|msg_signature={}|content={}|encrypt={}|resCode={}|reqUrl={}",
                    phone, uid, timeStamp, nonce, msg_signature, content,
                    encrypt, response.getStatusLine().getStatusCode(),
                    RCS_CHECKADMIN_URL);
            }
        }
        catch (Exception e)
        {
            // 如果接口异常，默认给过，防止接口异常导致管理员无法操作
            // 后续可根据实际情况调整true或者false
            checkResult = true;
            log.error(
                "baseCheckAdminByRCS error! enterId={}|phone={}|uid={}|rcsurl={}",
                enterId, phone, uid, RCS_CHECKADMIN_URL, e);
        }
        finally
        {
            httpClient.getConnectionManager().shutdown();
        }
        return checkResult;
    }

    /**
     * 通过RCS检验确认管理员省份，原则上只有管理员登录的时候校验即可 建议RCS给的isAdmin为1时调用
     * @param enterId
     * @param isAdmin
     * @param phone
     * @return
     */
    public boolean checkAdminByRCS(String enterId, String firstDeptId,
        String isAdmin, String phone, String uid)
        throws ClientProtocolException, IOException
    {
        boolean checkResult = false;
        log.info(
            "checkAdminByRCS start enterId={}|firstDeptId={}|isAdmin={}|phone={}|uid={}",
            enterId, firstDeptId, isAdmin, phone, uid);

        // 先判断处理集团企业enterDeptId模式
        if (enterId.indexOf("-") > -1)
        {
            // 负一屏管理员校验接口 enterDeptId校验兼容性暂不可行，还需用旧有校验接口
            /*
             * checkResult = checkAdminByRCS(enterId, isAdmin, phone, uid);
             * return checkResult;
             */
            enterId = enterId.substring(0, enterId.indexOf("-"));
        }

        // 再处理普通enterId模式
        if (AssertUtil.isEmpty(firstDeptId))
        {
            // 大部分企业没有这个参数，默认为0
            firstDeptId = "0";
        }
        // 按接口要求，组装内容体
        String content = createContent(enterId, firstDeptId, isAdmin, phone);
        checkResult = baseCheckAdminByRCS(enterId, isAdmin, phone, uid, content);

        return checkResult;

    }

    public UmcArtifactValidRes checkArtifactByUmc(AttendLoginReq req)
    {
        UmcArtifactValidRes res = new UmcArtifactValidRes();
        String reqParam = buildArtifactParam(req.getPassid(),
                req.getSourceid(), req.getArtifact());
        String result = null;
        try
        {
            // 使用通讯组件调用UMC token校验接口
            result = new String((byte[]) CallService.callService(
                    UMC_VALIED_ARTICFACT, reqParam), "utf-8");
            log.info(
                    "checkArtifactByUmc uid={}|enterId={}|url={}|req={}|result={}",
                    req.getUid(), req.getEnterId(), UMC_VALIED_ARTICFACT, reqParam,
                    result);
            // 结果解析
            return parseRespResult(req, res, result);
        }
        catch (Exception e)
        {
            log.error(
                    "checkArtifactByUmc call server failed,uid={}|enterId={}|url={}|req={}",
                    req.getUid(), req.getEnterId(), UMC_VALIED_ARTICFACT, reqParam,
                    e);
            // UMC接口稳定性不佳，失败进行一次重试
            /*try
            {
                // 有网络问题？，先睡眠10ms
                Thread.sleep(10);
                // 使用通讯组件调用UMC token校验接口
                result = new String((byte[]) CallService.callService(
                    UMC_VALIED_ARTICFACT, reqParam), "utf-8");
                log.info(
                    "second checkArtifactByUmc uid={}|enterId={}|url={}|req={}|result={}",
                    req.getUid(), req.getEnterId(), UMC_VALIED_ARTICFACT,
                    reqParam, result);
                // 校验结果解析
                return parseRespResult(req, res, result);
            }
            catch (Exception e2)
            {
                log.error(
                    "second checkArtifactByUmc call server still failed,uid={}|enterId={}|url={}|req={}",
                    req.getUid(), req.getEnterId(), UMC_VALIED_ARTICFACT,
                    reqParam, e2);
            }*/

        }
        res.setCode(AtdcResultCode.ATDC108.ARTIFACT_VALID_FAIL);
        res.setSummary(AtdcResultSummary.ATDC108.ARTIFACT_VALID_FAIL);
        return res;
    }
    
    
    
    public UmcGetArtifactRes getArtifactByUmc(UserInfo user)
    {
    	UmcGetArtifactRes res = new UmcGetArtifactRes();
        String reqParam = buildGetArtifactParam(user);
        String result = null;
        try
        {
            // 使用通讯组件调用UMC token校验接口
            result = new String((byte[]) CallService.callService(
            		UMC_GET_ARTICFACT, reqParam), "utf-8");
            log.info(
                "getArtifactByUmc uid={}|enterId={}|url={}|req={}|result={}",
                user.getUid(), user.getEnterId(), UMC_GET_ARTICFACT, reqParam,
                result);
            // 结果解析
            return parseRespResult(user,res, result);
        }
        catch (Exception e)
        {
            log.error(
                "getArtifactByUmc call server failed,uid={}|enterId={}|url={}|req={}",
                user.getUid(), user.getEnterId(), UMC_GET_ARTICFACT, reqParam,
                e);

        }
        res.setCode(AtdcResultCode.ATDC108.ARTIFACT_VALID_FAIL);
        res.setSummary(AtdcResultSummary.ATDC108.ARTIFACT_VALID_FAIL);
        return res;
    }

    /**
     * 解析凭证校验结果
     * @param req
     * @param res
     * @param result
     * @return
     */
    private UmcArtifactValidRes parseRespResult(AttendLoginReq req,
        UmcArtifactValidRes res, String result)
    {
        try
        {
            Document document = DocumentHelper.parseText(result);
            Element rootEle = document.getRootElement();
            Element ele = rootEle.element("Body").element(
                "ArtifactCheckResponse");
            String resultCode = ele.elementTextTrim("ResultCode");
            // 成功才进行用户节点信息解析
            if ("0".equals(resultCode))
            {
                Element userEle = ele.element("UserInfo");
                res.setUid(userEle.elementTextTrim("UID"));
                res.setPassId(userEle.elementTextTrim("PassID"));
                res.setUserId(userEle.elementTextTrim("UserID"));
                res.setMobileNumber(userEle.elementTextTrim("MobileNumber"));
                res.setEmailAddress(userEle.elementTextTrim("EmailAddress"));
                res.setNickName(userEle.elementTextTrim("NickName"));
                res.setLastChangeTime(userEle.elementTextTrim("LastChangeTime"));
                res.setExpandParams(userEle.elementTextTrim("ExpandParams"));
                return res;
            }
        }
        catch (Exception e)
        {
            log.error(
                "checkArtifactByUmc parseRespResult failed,uid={}|enterId={}|result={}",
                req.getUid(), req.getEnterId(), result, e);
        }
        res.setCode(AtdcResultCode.ATDC108.ARTIFACT_VALID_FAIL);
        res.setSummary(AtdcResultSummary.ATDC108.ARTIFACT_VALID_FAIL);
        return res;
    }
    
    
    /**
     * 解析凭证校验结果
     * @param
     * @param res
     * @param result
     * @return
     */
    private UmcGetArtifactRes parseRespResult(UserInfo user,
    		UmcGetArtifactRes res, String result)
    {
        try
        {
            Document document = DocumentHelper.parseText(result);
            Element rootEle = document.getRootElement();
            Element ele = rootEle.element("Body").element(
                "GetArtifactResponse");
            String resultCode = ele.elementTextTrim("ResultCode");
            res.setResultCode(ConverUtil.string2Int(resultCode));
            // 成功才进行用户节点信息解析
            if ("0".equals(resultCode))
            {
                res.setInResponseTo(ele.elementTextTrim("InResponseTo"));
                res.setSystemTime(ele.elementTextTrim("SystemTime"));
                res.setArtifact(ele.elementTextTrim("Artifact"));
                res.setCode(ResultCode.S_OK);
                return res;
            }
        }
        catch (Exception e)
        {
            log.error(
                "checkArtifactByUmc parseRespResult failed,uid={}|enterId={}|result={}",
                user.getUid(), user.getEnterId(), result, e);
        }
        res.setCode(AtdcResultCode.ATDC108.ARTIFACT_VALID_FAIL);
        res.setSummary(AtdcResultSummary.ATDC108.ARTIFACT_VALID_FAIL);
        return res;
    }

    /**
     * 构建请求参数
     * @param passId
     * @param sourceId
     * @param artifactId
     */
    private String buildArtifactParam(String passId, String sourceId,
        String artifactId)
    {
        if (AssertUtil.isEmpty(sourceId))
        {
            sourceId = UMC_SOURCEID;
        }
        String head = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?> <soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
        StringBuilder sb = new StringBuilder(head);
        String uuid = UUID.randomUUID().toString();
        String systemTIme = TimeUtil.getCurrentDateTime("yyyyMMddHHmmssSSS");
        sb.append("<soap:Body>");
        sb.append("<ArtifactCheckRequest>");
        sb.append("<MSGID>").append(uuid).append("</MSGID>");
        sb.append("<SystemTime>").append(systemTIme).append("</SystemTime>");
        sb.append("<Vesion>").append("1.0").append("</Vesion>");
        sb.append("<SourceID>").append(sourceId).append("</SourceID>");
        sb.append("<AppId>").append("2").append("</AppId>");
        sb.append("<ClientVersion>").append("1.0").append("</ClientVersion>");
        sb.append("<PassID>").append(passId).append("</PassID>");
        sb.append("<Artifact>").append(artifactId).append("</Artifact>");
        sb.append("<UID>").append("").append("</UID>");
        sb.append("<ExpandParams>").append("").append("</ExpandParams>");
        sb.append("</ArtifactCheckRequest>");
        sb.append("</soap:Body>");
        sb.append("</soap:Envelope>");
        return sb.toString();
    }
    
    private String buildGetArtifactParam(UserInfo userInfo)
        {
            
            String head = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?> <soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
            StringBuilder sb = new StringBuilder(head);
            String uuid = UUID.randomUUID().toString();
            String systemTIme = TimeUtil.getCurrentDateTime("yyyyMMddHHmmssSSS");
            sb.append("<soap:Body>");
            sb.append("<GetArtifactRequest>");
            sb.append("<MSGID>").append(uuid).append("</MSGID>");
            sb.append("<SystemTime>").append(systemTIme).append("</SystemTime>");
            sb.append("<Vesion>").append("1.0").append("</Vesion>");
            sb.append("<SourceID>").append(UMC_SOURCEID).append("</SourceID>");
            sb.append("<AppId>").append("2").append("</AppId>");
            sb.append("<ClientVersion>").append("1.0").append("</ClientVersion>");
            sb.append("<PassID>").append(userInfo.getUmcArtifactValidRes().getPassId()).append("</PassID>");
            sb.append("<UID>").append(userInfo.getUmcArtifactValidRes().getUid()).append("</UID>");
            sb.append("<ExpandParams>").append("").append("</ExpandParams>");
            sb.append("</ArtifactCheckRequest>");
            sb.append("</soap:Body>");
            sb.append("</soap:Envelope>");
            return sb.toString();
        }

    // public String methodPost(String url,NameValuePair[] data){
    //
    // String response= "";//要返回的response信息
    // HttpClient httpClient = new HttpClient();
    // PostMethod postMethod = new PostMethod(url);
    // // 将表单的值放入postMethod中
    // postMethod.setRequestBody(data);
    // // 执行postMethod
    // int statusCode = 0;
    // try {
    // statusCode = httpClient.executeMethod(postMethod);
    // } catch (HttpException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
    // // 301或者302
    // if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
    // || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
    // // 从头中取出转向的地址
    // Header locationHeader = postMethod.getResponseHeader("location");
    // String location = null;
    // if (locationHeader != null) {
    // location = locationHeader.getValue();
    // System.out.println("The page was redirected to:" + location);
    // response= methodPost(location,data);//用跳转后的页面重新请求。
    // } else {
    // System.err.println("Location field value is null.");
    // }
    // } else {
    // System.out.println(postMethod.getStatusLine());
    //
    // try {
    // response= postMethod.getResponseBodyAsString();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // postMethod.releaseConnection();
    // }
    // return response;
    // }

    /**
     * 组装请求内容体 enterId 和 firstDeptId 格式 firstDeptId无值调用方需保证默认为0
     * @param enterId
     * @param firstDeptId
     * @param isAdmin
     * @param phone
     * @return
     */
    private String createContent(String enterId, String firstDeptId,
        String isAdmin, String phone)
    {
        StringBuilder str = new StringBuilder();

        str.append("{\"enterId\":\"");
        str.append(enterId);
        str.append("\", \"firstDeptId\":\"");
        str.append(firstDeptId);
        str.append("\", \"isAdmin\":\"");
        str.append(isAdmin);
        str.append("\", \"mobile\":\"");
        str.append(phone);
        str.append("\"}");

        return str.toString();
    }

    /**
     * 组装请求内容体 enterDeptId格式 36101-130280
     * @param enterDeptId
     * @param isAdmin
     * @param phone
     * @return
     */
    private String createContent(String enterDeptId, String isAdmin,
        String phone)
    {
        StringBuilder str = new StringBuilder();

        str.append("{\"enterDeptId\":\"");
        str.append(enterDeptId);
        str.append("\", \"isAdmin\":\"");
        str.append(isAdmin);
        str.append("\", \"mobile\":\"");
        str.append(phone);
        str.append("\"}");

        return str.toString();
    }

    public static void main(String[] args)
    {
        // System.out.println(createContent("1111","1","135678973849"));
    }
}

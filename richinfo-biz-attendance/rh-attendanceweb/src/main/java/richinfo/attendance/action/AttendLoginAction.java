/**
 * 文件名：AttendLoginAction.java
 * 创建日期： 2017年6月1日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月1日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.action;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import richinfo.attendance.SMS.SmsSendUtil;
import richinfo.attendance.bean.AttendGroupWithEmpRes;
import richinfo.attendance.bean.AttendLoginReq;
import richinfo.attendance.bean.AttendLoginRes;
import richinfo.attendance.bean.UmcGetArtifactRes;
import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.dao.AttendGroupDao;
import richinfo.attendance.dao.MessageDao;
import richinfo.attendance.entity.*;
import richinfo.attendance.service.AttendGroupService;
import richinfo.attendance.service.AttendLoginService;
import richinfo.attendance.service.MessageService;
import richinfo.attendance.service.impl.AttendGroupServiceImpl;
import richinfo.attendance.service.impl.AttendLoginServiceImpl;
import richinfo.attendance.service.impl.MessageServiceImpl;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.Base64Coder;
import richinfo.bcomponet.util.BUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述： 登录接口类 --RCS单点登录考勤系统
 */
@Controller
@RequestMapping("/login")
public class AttendLoginAction extends BaseAttendanceAction {
    private static Logger log = LoggerFactory.getLogger(AttendLoginAction.class);

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();

    private AttendGroupDao groupDao = new AttendGroupDao();

    private AttendGroupService groupService = new AttendGroupServiceImpl();
    private AttendLoginService attendLoginService = new AttendLoginServiceImpl();

    /**
     * 单点登录请求
     * @param request
     * @param response
     * @throws ServletException
     */
    @RequestMapping(value = "/ssoAttendance", method = RequestMethod.GET)
    public void ssoAttendance(HttpServletRequest request, HttpServletResponse response){
        try {
            Map<String, String> reqMap = parserReqParam(request);
            String isAdmin = (String) reqMap.get("isAdmin");
            String comeFrom = (String) reqMap.get("isComeFrom");
            String appealId = (String) reqMap.get("appealId");
            String dailyDate = (String) reqMap.get("dailyDate");
            String enterDeptId = (String) reqMap.get("enterDeptId");
            log.info(
                "ssoAttendance token={}|contactId={}|euserId={}|enterId={}|enterName={}|firstDeptId={}|enterDeptId={}|isAdmin={}|comeFrom={}|clinetIP={}",
                reqMap.get("token"), reqMap.get("contactId"),
                reqMap.get("EUserID"), reqMap.get("enterId"),
                reqMap.get("enterName"), reqMap.get("firstDeptId"), enterDeptId,
                isAdmin, comeFrom, BUtils.getIpAddr(request));

            // 参数设置
            AttendLoginReq req = new AttendLoginReq();
            req.setToken(reqMap.get("token"));
            req.setIsAdmin(isAdmin);
            String uid = reqMap.get("EUserID");
            req.setUid(uid);
            req.setEnterId(reqMap.get("enterId"));
            req.setEnterDeptId(enterDeptId);
            req.setContactId(reqMap.get("contactId"));
            req.setFirstDeptId(reqMap.get("firstDeptId"));

            String enterName = reqMap.get("enterName");
            log.debug("enterName before base64:" + enterName);
            // RCS通过url传过来的base64编码有问题，要替换
            try {
                enterName = base64Dcode(enterName);
                log.debug("enterName after base64:" + enterName);
                req.setEnterName(enterName);
            } catch (Exception e) {}

            // 登录操作，需要调用统一认证接口验证token
            AttendLoginRes res = loginService.ssoAttendance(req);

            // 写会话cookies
            saveCookie("token", res.getUsessionid(), response);
            // 用户uid写cookies
            saveCookie("uid", uid, response);

            // 企业名称写cookies
            if (AssertUtil.isNotEmpty(res.getEnterName())){
                saveCookie("enterName", gbEncoding(res.getEnterName()), response);
            }
            // 用户enterId写cookies
            // 优先使用enterDeptId(集团企业的模式："企业id"-"一级部门id",36101-130280)
            if (AssertUtil.isNotEmpty(req.getEnterDeptId())) {
                saveCookie("enterId", req.getEnterDeptId(), response);
            } else {
                saveCookie("enterId", req.getEnterId(), response);
            }

            // 用户手机号码写cookies
            saveCookie("phone", res.getPhone(), response);
            // 用户首次登录标识写cookies 0否1是
            saveCookie("firstLogin", res.getFirstLogin(), response);
            //用户角色类型写入
            saveCookie("roleType", String.valueOf(res.getRoleType()), response);
            //设备功能写入
            saveCookie("useEquipmentClock", String.valueOf(res.getUseEquipmentClock()), response);

            // 写会话cookies HTTPOLNY
            saveHTTPOlnyCookie("loginToken", res.getUsessionid(), response);
            // 审批员标识写cookie 0否1是
            saveCookie("examinerState", res.getExaminerState() + "", response);
            // 白名单标识写cookie 0否1是
            saveCookie("whitelistStatus", String.valueOf(res.getWhitelistStatus()), response);

            saveCookie("visitViaMessage", "0", response);

            // TODO 先设置全部是管理员 便于测试 上线要修改
            // 配置管理员列表，便于测试管理员和非管理员，临时设置，上线要去掉，2017-06-27
            // 对管理员和考勤组负责人开放权限
            if ("1".equals(res.getStatus())) {
                saveCookie("status", "1", response);
            } else {
                saveCookie("status", "0", response);
            }

            //认证错误存储错误信息
            //获取状态码
            String code = res.getCode();

            // 登录
            // 结果跳转
            String jumpToUrl = res.getJumpUrl();
            if(!"S_OK".equals(code)){
                //状态码
                saveCookie("code", code, response);
                saveCookie("summary", res.getSummary(), response);
            }else {
                //跳转异常申诉
                if("1".equals(comeFrom) && StringUtils.isNotBlank(appealId)){
                    saveCookie("visitViaMessage", "1", response);
                    if (1 == res.getExaminerState()){
                        jumpToUrl = AttendanceConfig.getInstance().getProperty(
                            "attend.login.approveUrl", "")+"/"+appealId+"?role=admin";
                    }else {
                        jumpToUrl = AttendanceConfig.getInstance().getProperty(
                                "attend.login.approveUrl", "")+"/"+appealId+"?role=user";
                    }
                    //跳转日报统计
                }else if (StringUtils.isNotBlank(comeFrom) &&"1".equals(comeFrom) && StringUtils.isNotBlank(dailyDate)){
                    saveCookie("visitViaMessage", "1", response);
                    saveCookie("dailyDate", dailyDate, response);
                    jumpToUrl = AttendanceConfig.getInstance().getProperty("attend.login.dailyUrl", "");
                }else{
                    //认证成功 首次进入 判断用户是否是首次登录 0否1是 并且是管理员
                    List<AttendGroupWithEmpRes> list = groupDao.checkoutGroup(req.getEnterId()==null?
                        req.getEnterDeptId():req.getEnterId(), 0);
                   // logger.info("进来企业考勤组  =============list={} ",list);
                    saveCookie("firstLogStatus",list.isEmpty()?"1":"0",response);
                    if ("1".equals(res.getFirstLogin()) ) {
                        logger.info("用户第一次登陆");
                        //判断是管理员，没有考勤组，则定义不是让前端跳转创建考勤组页面
                        if (list.isEmpty() &&  "1".equals(res.getStatus())) {
                            //首次进入状态 1为无考勤组 0为有考勤组
                            saveCookie("firstLogStatus","1",response);
                            //存在考勤组  判断用户是否已经加入考勤组，是否白名单，然后回去
                        } else if (AssertUtil.isNotEmpty(list)){
                            saveCookie("firstLogStatus","0",response);
                            AttendEmployee employee = new AttendEmployee();
                            employee.setEnterId(req.getEnterId());
                            employee.setStatus(AttendEmployee.EmployeeStatus.Normal.getValue());
                            employee.setContactId(reqMap.get("contactId"));
                            employee.setEnterName(enterName);
                            employee.setPhone(res.getPhone());
                            employee.setUid(uid);
                            //判断部门选择器就行自动/手动加入考勤组
                            //String token = BUtils.getCookie(request, "token");
                            UserInfo userInfo = userInfoCache.get(res.getUsessionid());
                            logger.info("userInfoCache-------userInfo={}",userInfo);
                            List<AttendDepartmentChooser> attendDepartmentChoosers = groupService.detectionJoinGroup(employee,userInfo);

                            if (AssertUtil.isNotEmpty(attendDepartmentChoosers)){
                                String departmentJson = JSON.toJSONString(attendDepartmentChoosers);
                                logger.info("response attendDepartmentChoosers = {}",departmentJson);
                                departmentJson = gbEncoding(departmentJson);
                                logger.info("response 编码后 attendDepartmentChoosers = {}", departmentJson);
                                //saveCookie("employeeStatus","0",response);
                                saveCookie("departments",departmentJson,response);
                                //跳转地址 选择地址
                               // jumpToUrl=("http://120.196.212.78:8080/satdc/rcs/index.html#/Chosedepartment");
                            }
                        }
                    }else {
                        //用户不是第一次登陆
                        // 用户只有一个考勤组条件下:未加入考勤组则添加进考勤组,已加入考勤组如果再次匹配成功则更新考勤组
                        logger.info("用户不是第一次登陆");
                        AttendEmployee employee = new AttendEmployee();
                        employee.setEnterId(req.getEnterId());
                        employee.setStatus(AttendEmployee.EmployeeStatus.Normal.getValue());
                        employee.setContactId(reqMap.get("contactId"));
                        employee.setEnterName(enterName);
                        employee.setPhone(res.getPhone());
                        employee.setUid(uid);

                        //先从缓存中通过Usessionid获取缓存的用户信息
                        UserInfo userInfo = userInfoCache.get(res.getUsessionid());

                        List<AttendDepartmentChooser> attendDepartmentChoosers = groupService.detectionJoinGroup(employee, userInfo);
                        if (AssertUtil.isNotEmpty(attendDepartmentChoosers)) {
                            String departmentJson = JSON.toJSONString(attendDepartmentChoosers);
                            logger.info("response attendDepartmentChoosers = {}", departmentJson);
                            departmentJson = gbEncoding(departmentJson);
                            logger.info("response 编码后 attendDepartmentChoosers = {}", departmentJson);
                            saveCookie("departments", departmentJson, response);
                        }
                    }
                }
            }

            log.info(
                "ssoAttend,uid={}|contactId={}|enterId={}|phone={}|jumpToUrl={}",
                uid, reqMap.get("contactId"), reqMap.get("enterId"),
                res.getPhone(), jumpToUrl);
            redirect(request, response, jumpToUrl);
        }catch (Exception e){
            logger.error("全局异常  认证失败 e{}",e);
            redirect(request,response,AttendanceConfig.getInstance().getProperty("attend.login.failUrl", ""));
        }
    }

    /**
     * PC端管理后台单点登录请求(虽然PC端跟APP端单点登录功能基本一致，为避免后续APP跟PC端个性化差异太多，将接口分离，)
     * @param request
     * @param response
     * @throws ServletException
     */
    @RequestMapping(value = "/ssoAttendancePc", method = RequestMethod.GET)
    public void ssoAttendancePc(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> reqMap = parserReqParam(request);
        String enterName = reqMap.get("eN");
        // RCS通过url传过来的base64编码有问题，要替换
        try {
            enterName = base64Dcode(enterName);
        } catch (Exception e) {}
        String isAdmin = (String) reqMap.get("aM");
        String enterDeptId = (String) reqMap.get("enterDeptId");
        String artifact = reqMap.get("artifact");
        String sourceId = reqMap.get("sourceid");
        String passId = reqMap.get("passid");
        String optype = reqMap.get("optype");
        String reqtime = reqMap.get("reqtime");
        String check = reqMap.get("check");
        String uid = reqMap.get("uI");
        String enterId = reqMap.get("eI");
        String firstDeptId = reqMap.get("fD");
        String aM = reqMap.get("isAdmin");
        log.info(
            "ssoAttendancePc artifact={}|sourceid={}|passId={}|optype={}|reqtime={}|check={}|euserId={}|enterId={}|enterName={}|firstDeptId={}|enterDeptId={}|isAdmin={}|clinetIP={}|aM={}",
            artifact, sourceId, passId, optype, reqtime, check, uid, enterId,
            enterName, firstDeptId, enterDeptId, isAdmin,
            BUtils.getIpAddr(request),aM);

        // 参数设置
        AttendLoginReq req = new AttendLoginReq();
        req.setEnterName(enterName);
        req.setIsAdmin(isAdmin);
        req.setUid(uid);
        req.setEnterId(enterId);
        req.setEnterDeptId(enterDeptId);
        req.setFirstDeptId(firstDeptId);
        req.setArtifact(artifact);
        req.setSourceid(sourceId);
        req.setOptype(optype);
        req.setReqtime(reqtime);
        req.setCheck(check);
        req.setPassid(passId);
        req.setaM(aM);

        // 登录操作，需要调用统一认证接口验证token
        AttendLoginRes res = loginService.ssoAttendancePc(req);

        // 写会话cookies
        saveCookie("token", res.getUsessionid(), response);
        // 用户uid写cookies
        saveCookie("uid", uid, response);
        // 企业名称写cookies
        saveCookie("enterName", gbEncoding(enterName), response);
        // 用户enterId写cookies
        // 优先使用enterDeptId(集团企业的模式："企业id"-"一级部门id",36101-130280)
        if (AssertUtil.isNotEmpty(req.getEnterDeptId())) {
            saveCookie("enterId", req.getEnterDeptId(), response);
        } else if (AssertUtil.isNotEmpty(firstDeptId) && !"0".equals(firstDeptId)) {
            saveCookie("enterId", enterId + "-" + firstDeptId, response);
        } else {
            saveCookie("enterId", enterId, response);
        }
        // 用户手机号码写cookies
        saveCookie("phone", res.getPhone(), response);

        // 用户首次登录标识写cookies 0否1是
        saveCookie("firstLogin", res.getFirstLogin(), response);
        //用户角色类型写入
        saveCookie("roleType", String.valueOf(res.getRoleType()), response);
        // 写会话cookies HTTPOLNY
        saveHTTPOlnyCookie("loginToken", res.getUsessionid(), response);
        // 审批员标识写cookie 0否1是
        saveCookie("examinerState", res.getExaminerState() + "", response);

        // TODO 先设置全部是管理员 便于测试 上线要修改
        // 配置管理员列表，便于测试管理员和非管理员，临时设置，上线要去掉，2017-06-27
        if ( "1".equals(res.getStatus())) {
            saveCookie("status", "1", response);
        } else {
            saveCookie("status", "0", response);
        }

        //认证错误存储错误信息
        //获取状态码
        String code = res.getCode();
        if(!"S_OK".equals(code)){
            //状态码
            saveCookie("code", code, response);
            saveCookie("summary", res.getSummary(), response);
        }

        // 登录结果跳转
        String jumpToUrl = res.getJumpUrl();
        log.info("ssoAttendPc,uid={}|enterId={}|phone={}|jumpToUrl={}", uid,
            reqMap.get("enterId"), res.getPhone(), jumpToUrl);
        // return "redirect:" + jumpToUrl;
        redirect(request, response, jumpToUrl);
    }
    
    
    /**
     * PC端管理后台单点登录获取凭证
     * @param request
     * @param response
     * @throws ServletException
     */
    @RequestMapping(value = "/ssoGetArtifactPc", method = RequestMethod.GET)
    public void getArtifactPc(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	UserInfo userInfo = this.checkLogin(request);
    	UmcGetArtifactRes getArtifacRes = loginService.getArtifactPc(userInfo);
    	 // 需要返回考勤人员处理失败的列表
        processJsonTemplate(response, "attendance/getArtifact_json.ftl", getArtifacRes);
    }

    /**
     * base64解码：先转换并补齐位数，url传输后+号会被替换成空格，=号会丢失。 base64编码url直接传输有弊端
     * @param enterName
     * @return
     */
    private String base64Dcode(String enterName) {
        enterName = enterName.trim().replaceAll(" ", "+");
        int i = enterName.length() % 4;
        if (i != 0) {
            int j = 4 - i;
            for (int k = 0; k < j; k++) {
                enterName = enterName + "=";
            }
        }
        enterName = Base64Coder.decodeString(enterName);
        return enterName;
    }

    /**
     * 写一个cookie
     * @param cookieName Cookie名称
     * @param cookieValue Cookie值
     * @param resp
     */
    protected void saveCookie(String cookieName, String cookieValue, HttpServletResponse resp) {
        resp.addHeader("Set-Cookie", cookieName + "=" + cookieValue + ";Path=/");
    }

    /**
     * 写一个会话cookie httpolny
     * @param cookieName Cookie名称
     * @param cookieValue Cookie值
     * @param resp
     */
    protected void saveHTTPOlnyCookie(String cookieName, String cookieValue, HttpServletResponse resp) {
        resp.addHeader("Set-Cookie", cookieName + "=" + cookieValue
            + ";Path=/;HTTPOnly");
    }

    /*
     * 中文转unicode编码
     */
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }


    /**
     * SMS单点登录
     */
    @RequestMapping(value = "/ssoSMSAttendance", method = RequestMethod.POST)
    @ResponseBody
    public Map ssoSMSAttendance(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> map = parserReqJsonParam(request);
        String token = (String) map.get("token");
        Map<String,Object> responseObj = new HashMap<>();
        if(StringUtils.isEmpty(token)) {
            responseObj.put("code","S_ERROR");
            responseObj.put("summary","token入参不能为空");
            return responseObj;
        }

        // 参数设置
        AttendLoginReq req = new AttendLoginReq();
        req.setToken(token);
        req.setUid((String) map.get("uid"));
        SMSAttendancInfo attendancInfo = attendLoginService.ssoAttendanceSMS(req);


        if(null == attendancInfo) {
            responseObj.put("code","S_ERROR");
            responseObj.put("summary","登录失败，请重新登录");
            return responseObj;
        } else if(attendancInfo.getSsoStatus() == -1) {
            responseObj.put("code","S_ERROR");
            responseObj.put("summary","查询不到该用户");
            return responseObj;
        } else if(attendancInfo.getSsoStatus() == -2) {
            responseObj.put("code","S_ERROR");
            responseObj.put("summary","不能代人打卡哦");
            return responseObj;
        } else {
            saveCookie("token", attendancInfo.getToken(), response);
            saveCookie("loginToken", attendancInfo.getToken(), response);
            UserGroupEntity groupEntity = attendancInfo.getUserGroup();
            if(null != groupEntity) {
                saveCookie("enterId", groupEntity.getEnterId(), response);
                saveCookie("enterName", groupEntity.getEnterName(), response);
                saveCookie("phone", groupEntity.getPhone(), response);
                saveCookie("uid", groupEntity.getUid(), response);
            }

            Map<String,Object> SMSStatus = new HashMap<>();
            SMSStatus.put("attendRecord",attendancInfo.getAttendRecord());
            SMSStatus.put("attendClockVos",attendancInfo.getAttendClockVos());
            SMSStatus.put("userGroup",attendancInfo.getUserGroup());
            SMSStatus.put("allowOutRangeClock",attendancInfo.getAllowOutRangeClock());
            SMSStatus.put("charge",attendancInfo.getCharge());
            SMSStatus.put("token",attendancInfo.getToken());
            responseObj.put("code","S_OK");
            responseObj.put("summary","登录成功");
            responseObj.put("var", SMSStatus);
        }

        return responseObj;
    }

    /**
     * 取号回调
     */
    @RequestMapping(value = "/SMSVerificationCallback", method = RequestMethod.POST)
    public void SMSVerificationCallback(HttpServletRequest request){

    }


    /**
     * SMS单点登录
     */
    @RequestMapping(value = "/takeNumLoginAttendance", method = RequestMethod.POST)
    @ResponseBody
    public Map takeNumLoginAttendance(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> map = parserReqJsonParam(request);
        Map<String,Object> responseObj = new HashMap<>();
        String uid = (String) map.get("uid");
        if(StringUtils.isEmpty(uid)) {
            responseObj.put("code","S_ERROR");
            responseObj.put("summary","uid入参不能为空");
            return responseObj;
        }
        // 参数设置
        AttendLoginReq req = new AttendLoginReq();
        req.setUid(uid);
        SMSAttendancInfo attendancInfo = attendLoginService.ssoAttendanceTakeNum(req);

        if(null != attendancInfo) {
            saveCookie("token", attendancInfo.getToken(), response);
            saveCookie("loginToken", attendancInfo.getToken(), response);
            UserGroupEntity groupEntity = attendancInfo.getUserGroup();
            if(null != groupEntity) {
                saveCookie("enterId", groupEntity.getEnterId(), response);
                saveCookie("enterName", groupEntity.getEnterName(), response);
                saveCookie("phone", groupEntity.getPhone(), response);
                saveCookie("uid", groupEntity.getUid(), response);
            }
        }
        Map<String,Object> SMSStatus = new HashMap<>();
        SMSStatus.put("attendRecord",attendancInfo.getAttendRecord());
        SMSStatus.put("attendClockVos",attendancInfo.getAttendClockVos());
        SMSStatus.put("userGroup",attendancInfo.getUserGroup());
        SMSStatus.put("allowOutRangeClock",attendancInfo.getAllowOutRangeClock());
        SMSStatus.put("charge",attendancInfo.getCharge());
        SMSStatus.put("token",attendancInfo.getToken());
        responseObj.put("code","S_OK");
        responseObj.put("summary","登录成功");
        responseObj.put("var", SMSStatus);

        return responseObj;
    }

}

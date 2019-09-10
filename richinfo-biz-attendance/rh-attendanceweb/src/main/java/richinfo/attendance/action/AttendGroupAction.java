/**
 * 文件名：AttendGroupAction.java
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
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import richinfo.attendance.asyn.MessageUpdateAsynTask;
import richinfo.attendance.bean.*;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.common.ResBean;
import richinfo.attendance.common.ResultCode;
import richinfo.attendance.entity.*;
import richinfo.attendance.service.AttendGroupService;
import richinfo.attendance.service.impl.AttendGroupServiceImpl;
import richinfo.attendance.util.*;
import richinfo.bcomponet.asyn.AsynTaskProcess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 功能描述： 考勤组管理和操作接口类
 */
@Controller
@RequestMapping("/group")
public class AttendGroupAction extends BaseAttendanceAction {
    private AttendGroupService groupService = new AttendGroupServiceImpl();

    private Logger logger = LoggerFactory.getLogger(AttendGroupAction.class);

    /** 考勤组 */
    private AttendGroup attendGroup;
    /** 考勤日历，包含统计日期等信息 */
    private AttendCalendar attendCalendar;

    /**
     * 判断考勤人员是否已经在其他考勤组，返回考勤人员-考勤组的关系信息
     * @param request
     * @param response
     */
    @RequestMapping(value = "/checkEmployee", method = RequestMethod.POST)
    public void checkEmployee(HttpServletRequest request, HttpServletResponse response) {
        // 获取参数
        Map<String, Object> reqMap = parserReqJsonParam(request);
       // logger.info("判断考勤人员是否已经在其他考勤组请求参数===================reqMap={}",reqMap);
        // 区分创建跟编辑考勤组，创建不会传值，编辑传考勤组的id信息
        long attendanceId = ConverUtil.string2Long(
            request.getParameter("attendanceId"), -1);
        // 创建考勤组用true表示，编辑考勤组用false标识
        boolean flag = -1 == attendanceId ? true : false;

        AttendGroupReq groupReq = new AttendGroupReq();
        // 获取会话中的数据信息
        setReqBean(groupReq, request);
        if (!flag) {
            groupReq.setAttendanceId(attendanceId);
        }
        //获取部门
        List<AttendDepartmentChooser> attendDepartmentChoosers = parserDepartment(reqMap);
        logger.info("====获取部门======attendDepartmentChoosers={}",attendDepartmentChoosers);
        //调用和通讯录获取部门下的员工
        if (AssertUtil.isNotEmpty(attendDepartmentChoosers)){
            List<Map<String, Object>> userMapList = new ArrayList<>();
            boolean result = true;
             gainDepartmentStaff(userMapList,attendDepartmentChoosers,result);
             if (!result){
                 AttendGroupRes groupRes = new AttendGroupRes();
                 groupRes.setCode(ResultCode.S_ERROR);
                 groupRes.setSummary(AtdcResultSummary.ATDC102.QYTXL_SESSION_ERROR);
                 // 模板内容返回值
                 processJsonTemplate(response, "attendance/checkEmployee_json.ftl",
                         groupRes);
                 return;
             }
            logger.debug("====获取部门人员======userMapList={}",userMapList);
            if (AssertUtil.isNotEmpty(userMapList)){
                reqMap.put("userMapList",userMapList);
            }
        }
        //部门选择器
        groupReq.setAttendDepartmentChoosers(attendDepartmentChoosers);
        // 获取用户列表
        groupReq.setEmployees(parserEmployees(reqMap, groupReq));
        // 进行判断
        AttendGroupRes groupRes = groupService.checkEmployee(groupReq, flag);
        // 模板内容返回值
        processJsonTemplate(response, "attendance/checkEmployee_json.ftl",
            groupRes);
    }

    /**
     * 创建考勤组
     * @param request
     * @param response
     */
    @RequestMapping(value = "/createGroup", method = RequestMethod.POST)
    public void createGroup(HttpServletRequest request, HttpServletResponse response) {
        // 获取参数
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendGroupReq groupReq = new AttendGroupReq();
        // 获取会话中的数据信息
        setReqBean(groupReq, request);
        logger.debug("createGroupReq={}|OperateUid={}|phone={}", AtdcStringUtil
            .getRequestString(reqMap), groupReq.getUserInfo().getUid(),
            groupReq.getUserInfo().getPhone());
        //获取部门
        List<AttendDepartmentChooser>attendDepartmentChoosers = parserDepartment(reqMap);
        //调用和通讯录获取部门下的员工
        if (AssertUtil.isNotEmpty(attendDepartmentChoosers)){
            List<Map<String, Object>> userMapList = new ArrayList<>();
            boolean result =true;
            gainDepartmentStaff(userMapList,attendDepartmentChoosers,result);
            if (!result){
                AttendGroupRes groupRes = new AttendGroupRes();
                groupRes.setCode(ResultCode.S_ERROR);
                groupRes.setSummary(AtdcResultSummary.ATDC102.QYTXL_SESSION_ERROR);
                // 模板内容返回值
                processJsonTemplate(response, "attendance/updateGroup_json.ftl", groupRes);
                return;
            }
            if (AssertUtil.isNotEmpty(userMapList)){
                reqMap.put("userMapList",userMapList);
            }
        }
        groupReq.setAttendDepartmentChoosers(attendDepartmentChoosers);
        // 组装请求对象
        parserReqMap(reqMap, groupReq);
        if (groupReq.getAttendType()==1) {
            if (AssertUtil.isNotEmpty(groupReq.getUseFlexibleRule())) {
                if (AssertUtil.isNotEmpty(groupReq.getFlexitime())) {
                    String fixedAttendRule = groupReq.getFixedAttendRule();
                    Map jsonMap = JSON.parseObject(fixedAttendRule);
                    logger.debug("jsonMap ={}",jsonMap);
                    List dayNum = new ArrayList(jsonMap.keySet());
                    for (int i=0;i<dayNum.size()-1;i++) {
                        logger.info("day={}",dayNum.get(i));
                        int day = Integer.parseInt(dayNum.get(i).toString());
                        String attendRule = jsonMap.get(day).toString();
                        logger.debug("attendRule ={}",attendRule);
//                15:00-17:00
                        String afternoonTime = attendRule.substring(attendRule.indexOf("-")+1);
                        String morningTime = attendRule.substring(0,attendRule.indexOf("-"));
                        String[] split = afternoonTime.split(":");
                        String[] splitMorningTime = morningTime.split(":");
                        double f1 = new BigDecimal((float)Integer.parseInt(split[1])/60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        double f2 = new BigDecimal((float)Integer.parseInt(splitMorningTime[1])/60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        double pmTime = (double)(Integer.parseInt(split[0])+f1) ;
                        double amTime = (double)(Integer.parseInt(splitMorningTime[0])+f2) ;
                        if ((pmTime+groupReq.getFlexitime())>=24) {
                            AttendGroupRes groupRes = new AttendGroupRes();
                            groupRes.setCode(AtdcResultCode.ATDC104.NOT_ALLOWED_OVER_DAY);
                            groupRes.setSummary(AtdcResultSummary.ATDC104.NOT_ALLOWED_OVER_DAY);
                            processJsonTemplate(response, "attendance/common_json.ftl", groupRes);
                            return;
                        }
                        if ((amTime+groupReq.getFlexitime()+ new BigDecimal((float)groupReq.getAllowLateTime()/60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue())>=pmTime) {
                            AttendGroupRes groupRes = new AttendGroupRes();
                            groupRes.setCode(AtdcResultCode.ATDC104.NOT_ALLOWED_COVER_AFTERNOON);
                            groupRes.setSummary(AtdcResultSummary.ATDC104.NOT_ALLOWED_COVER_AFTERNOON);
                            processJsonTemplate(response, "attendance/common_json.ftl", groupRes);
                            return;
                        }
                    }
                }
            }
        }
            // 创建考勤组
            AttendGroupRes groupRes = groupService.createGroup(groupReq);
            // 需要返回考勤人员处理失败的列表
            processJsonTemplate(response, "attendance/updateGroup_json.ftl", groupRes);

    }

    /**
    * 编辑考勤组
    * @param request
    * @param response
    */
    @RequestMapping(value = "/updateGroup", method = RequestMethod.POST)
    public void updateGroup(HttpServletRequest request, HttpServletResponse response) {
        AttendGroupReq reqParam = new AttendGroupReq();
        setReqBean(reqParam, request);
        Map<String, Object> map = parserReqJsonParam(request);
        logger.debug("updateGroupReq={}|operateUid={}|phone={}", AtdcStringUtil.getRequestString(map),
            reqParam.getUserInfo().getUid(), reqParam.getUserInfo().getPhone());
        //获取部门
        List<AttendDepartmentChooser>attendDepartmentChoosers = parserDepartment(map);
        //调用和通讯录获取部门下的员工
        if (AssertUtil.isNotEmpty(attendDepartmentChoosers)){
            List<Map<String, Object>> userMapList = new ArrayList<>();
            boolean result = true;
            gainDepartmentStaff(userMapList,attendDepartmentChoosers,result);
            if (!result){
                AttendGroupRes groupRes = new AttendGroupRes();
                groupRes.setCode(ResultCode.S_ERROR);
                groupRes.setSummary(AtdcResultSummary.ATDC102.QYTXL_SESSION_ERROR);
                // 模板内容返回值
                processJsonTemplate(response, "attendance/updateGroup_json.ftl", groupRes);
                return;
            }
            if (AssertUtil.isNotEmpty(userMapList)){
                map.put("userMapList",userMapList);
            }
        }
        reqParam.setAttendDepartmentChoosers(attendDepartmentChoosers);
        // 对请求数据进行解析
        parseUpdateReqParam(map, reqParam);
        if (reqParam.getAttendType()==1) {
            if (AssertUtil.isNotEmpty(reqParam.getUseFlexibleRule())) {
                if (AssertUtil.isNotEmpty(reqParam.getFlexitime())) {
                    String fixedAttendRule = reqParam.getFixedAttendRule();
                    Map jsonMap = JSON.parseObject(fixedAttendRule);
                    logger.debug("jsonMap ={}",jsonMap);
                    List dayNum = new ArrayList(jsonMap.keySet());
                    for (int i=0;i<dayNum.size()-1;i++) {
                        logger.info("day={}",dayNum.get(i));
                        int day = Integer.parseInt(dayNum.get(i).toString());
                        String attendRule = jsonMap.get(day).toString();
                        logger.debug("attendRule ={}",attendRule);
//                15:00-17:00
                        String afternoonTime = attendRule.substring(attendRule.indexOf("-")+1);
                        String morningTime = attendRule.substring(0,attendRule.indexOf("-"));
                        String[] split = afternoonTime.split(":");
                        String[] splitMorningTime = morningTime.split(":");
                        double f1 = new BigDecimal((float)Integer.parseInt(split[1])/60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        double f2 = new BigDecimal((float)Integer.parseInt(splitMorningTime[1])/60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        double pmTime = (double)(Integer.parseInt(split[0])+f1) ;
                        double amTime = (double)(Integer.parseInt(splitMorningTime[0])+f2) ;
                        if ((pmTime+reqParam.getFlexitime())>=24) {
                            AttendGroupRes groupRes = new AttendGroupRes();
                            groupRes.setCode(AtdcResultCode.ATDC104.NOT_ALLOWED_OVER_DAY);
                            groupRes.setSummary(AtdcResultSummary.ATDC104.NOT_ALLOWED_OVER_DAY);
                            processJsonTemplate(response, "attendance/common_json.ftl", groupRes);
                            return;
                        }
                        if ((amTime+reqParam.getFlexitime()+ new BigDecimal((float)reqParam.getAllowLateTime()/60).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue())>=pmTime) {
                            AttendGroupRes groupRes = new AttendGroupRes();
                            groupRes.setCode(AtdcResultCode.ATDC104.NOT_ALLOWED_COVER_AFTERNOON);
                            groupRes.setSummary(AtdcResultSummary.ATDC104.NOT_ALLOWED_COVER_AFTERNOON);
                            processJsonTemplate(response, "attendance/common_json.ftl", groupRes);
                            return;
                        }
                    }
                }
            }
        }
            AttendGroupRes groupRes = groupService.updateGroup(reqParam);
            processJsonTemplate(response, "attendance/updateGroup_json.ftl", groupRes);
    }

    /**
     * 删除考勤组
     * @param request
     * @param response
     */
    @RequestMapping(value = "/deleteGroup", method = RequestMethod.POST)
    public void deleteGroup(HttpServletRequest request, HttpServletResponse response) {
        AttendGroupReq reqParam = new AttendGroupReq();
        setReqBean(reqParam, request);
        Map<String, Object> map = parserReqJsonParam(request);
        parseDelGroupReqParam(map, reqParam);
        AttendGroupRes result = groupService.deleteGroup(reqParam);
        processJsonTemplate(response, "attendance/common_json.ftl", result);
    }

    /**
     * 查询员工所在的考勤组以及主要信息
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryOwnGroup", method = RequestMethod.POST)
    public void queryOwnGroup(HttpServletRequest request, HttpServletResponse response) {
        AttendGroupReq reqBean = new AttendGroupReq();
        setReqBean(reqBean, request);
        AttendUserInGroupRes res = groupService.queryOwnGroup(reqBean);
//        logger.info("queryOwnGroup userGroup={}", res.getUserGroup());
//        logger.info("getWorkdayStatus={}",res.getUserGroup().getWorkdayStatus());
        processJsonTemplate(response, "attendance/queryOwnGroup_json.ftl", res);
    }

    /**
     * 管理员查询企业考勤组列表
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryGroupList", method = RequestMethod.POST)
    public void queryGroupList(HttpServletRequest request, HttpServletResponse response) {
        AttendGroupReq req = new AttendGroupReq();
        Map<String, Object> reqMap = parserReqJsonParam(request);
        setReqBean(req, request);
        req.setEnterId((String) reqMap.get("enterId"));
        AttendGroupListRes res = groupService.queryGroupList(req);
        processJsonTemplate(response, "attendance/queryGroupList_json.ftl", res);
    }

    /**
     * 管理员考勤组详情查询
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryGroupDetail", method = RequestMethod.POST)
    public void queryGroupDetail(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendGroupReq req = new AttendGroupReq();
        setReqBean(req, request);
        req.setAttendanceId(getAsLong(reqMap, "attendanceId"));
        AttendGroupRes res = groupService.queryGroupDetail(req);
        processJsonTemplate(response, "attendance/queryGroupDetail_json.ftl", res);
    }
    
    /**
     * 编辑考勤组排班
     * @param request
     * @param response
     */
    @RequestMapping(value = "/updateSchedule", method = RequestMethod.POST)
    public void updateSchedule(HttpServletRequest request, HttpServletResponse response) {
        AttendScheduleReq reqParam = new AttendScheduleReq();
        setReqBean(reqParam, request);
        Map<String, Object> map = parserReqJsonParam(request);
        logger.debug("updateGroupReq={}|operateUid={}|phone={}", AtdcStringUtil
            .getRequestString(map), reqParam.getUserInfo().getUid(), reqParam
            .getUserInfo().getPhone());
        // 对请求数据进行解析
        reqParam.parserEmployeesParam(map);
        AttendScheduleRsp rsp = groupService.updateAttendSchedule(reqParam);
        processJsonTemplate(response, "attendance/updateSchedule_json.ftl", rsp);
    }

    /**
     * 获取请求参数，组装为实体类
     * @param reqMap
     * @return
     */
    private AttendGroupReq parserReqMap(Map<String, Object> reqMap, AttendGroupReq groupReq) {
        groupReq.setAttendanceName((String) reqMap.get("attendanceName"));
        groupReq.setEnterId((String) reqMap.get("enterId"));
        groupReq.setEnterName((String) reqMap.get("enterName"));
        groupReq.setAmTime((String) reqMap.get("amTime"));
        groupReq.setPmTime((String) reqMap.get("pmTime"));
        groupReq.setLocation((String) reqMap.get("location"));
        groupReq.setDetailAddr((String) reqMap.get("detailAddr"));
        // 获取经纬度参数，判断非空、转化由工具类实现
        //获取考勤地址
        groupReq.setAttendClockSites(parserClockSites(reqMap));

        groupReq.setOutRange(ConverUtil.string2Int((String) reqMap.get("outRange")));
        groupReq.setExamineName((String) reqMap.get("examineName"));
        groupReq.setExamineUid((String) reqMap.get("examineUid"));
        groupReq.setExamineContactId((String) reqMap.get("examineContactId"));
        // 获取员工列表
        groupReq.setEmployees(parserEmployees(reqMap, groupReq));
        // 获取考勤组负责人列表
        if (AssertUtil.isNotEmpty(reqMap.get("chargemanList"))){
            groupReq.setChargemanList(parserChargemanList(reqMap, groupReq));
        }
        // 考勤组类型 1：固定班 2：排班 3：自由班
        groupReq.setAttendType(ConverUtil.string2Int((String) reqMap.get("attendType")));
        /** 固定班排班规则 */
        groupReq.setFixedAttendRule(new Gson().toJson((LinkedTreeMap) reqMap.get("fixedAttendRule")));
        /** 自由班排班规则 */
        groupReq.setFreeAttendRule(new Gson().toJson((LinkedTreeMap) reqMap.get("freeAttendRule")));
        /** 允许迟到时长，单位：分钟 （针对固定班和排班） */
        groupReq.setAllowLateTime(ConverUtil.string2Int((String) reqMap.get("allowLateTime")));
        /** 允许迟到时长，单位：分钟 （针对固定班和排班） */
        groupReq.setRelyHoliday(ConverUtil.string2Int((String) reqMap.get("relyHoliday")));
        /** 班次列表 （考勤类型 为2时，该字段有值） */
        groupReq.setScheduleShifts(parserScheduleShifts(reqMap, groupReq));
        /** 是否允许外勤打卡标识，0允许1不允许 */
        groupReq.setIsAllowedOutRangeClock(ConverUtil.string2Int((String) reqMap.get("isAllowedOutRangeClock")));
        /** 是否开启弹性班制标识，0开启1不开启 */
        if (AssertUtil.isNotEmpty(ConverUtil.string2Int((String) reqMap.get("useFlexibleRule")))) {
            groupReq.setUseFlexibleRule(ConverUtil.string2Int((String) reqMap.get("useFlexibleRule")));
        }
        /** 当开启弹性班制时，设置弹性时长，单位为小时 */
        if (AssertUtil.isNotEmpty(groupReq.getUseFlexibleRule())) {
            if (groupReq.getUseFlexibleRule()==0) {
                groupReq.setFlexitime(Double.parseDouble(String.valueOf(reqMap.get("flexitime")==null?"0":String.valueOf(reqMap.get("flexitime")))));
            }
        }
        return groupReq;
    }

    /**
     * 解析获取地点
     * @param reqMap
     * @return
     */
    private List<AttendClockSite> parserClockSites(Map<String, Object> reqMap) {
        List<AttendClockSite> attendClockSites = new ArrayList<>();
        List<Map<String,Object>> mapList = (List<Map<String,Object>> )reqMap.get("attendClockSites");
        for (Map<String,Object> map : mapList){
            AttendClockSite attendClockSite = new AttendClockSite();
            attendClockSite.setLongitude(ConverUtil.string2Double((String) map.get("longitude")));
            attendClockSite.setLatitude(ConverUtil.string2Double((String) map.get("latitude")));
            attendClockSite.setAttendanceRange( ConverUtil.string2Int((String) map.get("range")));
            attendClockSite.setDetailAddr((String) map.get("detailAddr"));
            attendClockSite.setLocation((String) map.get("location"));
            attendClockSites.add(attendClockSite);
        }

         return attendClockSites;
    }

    /**
     * 获取部门下的员工
     * @param attendDepartmentChoosers
     * @return
     */
    private void gainDepartmentStaff(List<Map<String, Object>> userMapList ,List<AttendDepartmentChooser> attendDepartmentChoosers,boolean result) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        Map<String,Object> objectMap = new HashMap<>();
        for (AttendDepartmentChooser attendDepartmentChooser : attendDepartmentChoosers){
            try {
                objectMap = QytxlUtil.getInstance().gainDepartmentStaff(attendDepartmentChooser.getDepartmentId(),attendDepartmentChooser.getEnterpriseId());
                //调用失败重试
            } catch (Exception e) {
                logger.info("调用企业通讯录获取直属联系人一次失败 e={}",e);
                try {
                    objectMap = QytxlUtil.getInstance().gainDepartmentStaff(attendDepartmentChooser.getDepartmentId(),attendDepartmentChooser.getEnterpriseId());
                } catch (Exception e1) {
                    result = false;
                    logger.info("调用企业通讯录获取直属联系人二次失败 e={}",e);
                    return;
                }
            }
            if (0 != (int)objectMap.get("error_code")){
                result = false;
                logger.error(" gainDepartmentStaff Qytxl error objectMapJson = {}",objectMap);
                return ;
            }
           // logger.info("==================objectMap={}",objectMap);
            objectMap.put("departmentName",attendDepartmentChooser.getDepartmentName());
            userMapList.add(objectMap);
        }
    }
    /**
     * 解析获取部门
     * @param reqMap
     * @return
     */
    private List<AttendDepartmentChooser> parserDepartment(Map<String, Object> reqMap) {
        List<AttendDepartmentChooser> attendDepartmentChoosers = new ArrayList<>();
        if (AssertUtil.isEmpty(reqMap.get("departments"))) {
            logger.error("departments parameter is empty.");
            return attendDepartmentChoosers;
        }
        List<Object> departments = (List<Object>) reqMap.get("departments");
        for (int i = 0; i < departments.size(); i++) {
            LinkedTreeMap<String, String> department = (LinkedTreeMap<String, String>) departments.get(i);
            AttendDepartmentChooser attendDepartmentChooser = new AttendDepartmentChooser();
            attendDepartmentChooser.setDepartmentId(department.get("departmentId"));
            attendDepartmentChooser.setDepartmentName(department.get("departmentName"));
            attendDepartmentChooser.setEnterpriseId(department.get("enterpriseId"));
            attendDepartmentChoosers.add(attendDepartmentChooser);
        }
        return attendDepartmentChoosers;

    }

    /**
     * 解析员工列表
     * @param reqMap
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<UserInfo> parserEmployees(Map<String, Object> reqMap, AttendGroupReq req) {
        List<UserInfo> users = new ArrayList<UserInfo>();
        if (AssertUtil.isEmpty(reqMap.get("employees"))&& AssertUtil.isEmpty(reqMap.get("userMapList"))) {
            logger.error("employees && userMapList parameter is empty.");
            return users;
        }
        // RCS提供的新选择器不能提供企业名称、用户号码等基本信息，员工企业名称信息直接使用考勤管理员企业名称2017-11-09
        UserInfo userInfo = req.getUserInfo();
        String enterName = (String) reqMap.get("enterName");
        String enterId = userInfo.getEnterId();
        List<Object> employees = (List<Object>) reqMap.get("employees");
        int count = employees.size();
        // uidSet用于uid去重，防止前端重复提交用户信息
        Set<String> uidSet = new HashSet<String>();
        if (AssertUtil.isNotEmpty(reqMap.get("userMapList"))){
            List<Map<String,Object>> userMapList = (List<Map<String,Object>>)reqMap.get("userMapList");
            for (Map<String,Object> objectMap : userMapList){
                //部门名称
                String departmentName = (String) objectMap.get("departmentName");
//                logger.info("获取部门下的人员封装 ====objectMap={}",objectMap);
                List<Map<String,Object>> items =(List<Map<String,Object>>) objectMap.get("items");
                if (AssertUtil.isNotEmpty(items)){
                    for (Map<String,Object> employeeMap : items){
                        UserInfo user = new UserInfo();
                        user.setUid((String) employeeMap.get("euserId"));
                        // 添加失败，即uid重复，则忽略
                        if (!uidSet.add(user.getUid())) {
                            logger.info("uid repeated,ignore it.uid={}", user.getUid());
                            continue;
                        }
                        String employeeName = (String) employeeMap.get("name");
                        if (AssertUtil.isNotEmpty(employeeName)) {
                             logger.info("解密前employeeName={}",employeeName);
                            try {
                                employeeName = AesUtils.decrypt(employeeName, AttendanceConfig.getInstance()
                                    .getProperty("attend.qytxl.aes_key",
                                        "6af15ca383ee45dd"));
                            } catch (Exception e)
                            {}
                        }
                        logger.info("解密后employeeName={}",employeeName);
                        user.setEmployeeName(employeeName);
                        user.setContactId((String) employeeMap.get("contactId"));
                        String phone = (String) employeeMap.get("regMobile");
                        try {
                            phone = AesUtils.decrypt(phone, AttendanceConfig.getInstance()
                                .getProperty("attend.qytxl.aes_key",
                                    "6af15ca383ee45dd"));
                        }catch (Exception e) {}
                        user.setPhone(phone);
                        user.setDeptId((String) employeeMap.get("departmentId"));
                        user.setDeptName(departmentName);
                        //user.setEmail(String.valueOf(employeeMap.get("email")));
                        user.setEnterId(enterId);
                        user.setEnterName(enterName);
                        users.add(user);
                    }
                }
            }
        }
        for (int i = 0; i < count; i++) {
            LinkedTreeMap<String, String> employee = (LinkedTreeMap<String, String>) employees
                .get(i);
            UserInfo user = new UserInfo();
            user.setUid(employee.get("euserId"));
//            user.setUid(employee.get("userId"));
            // 添加失败，即uid重复，则忽略
            if (!uidSet.add(user.getUid())) {
                logger.info("uid repeated,ignore it.uid={}", user.getUid());
                continue;
            }
            user.setEmployeeName(employee.get("name"));
            user.setContactId(employee.get("contactId"));
            user.setPhone(employee.get("phone"));
            user.setDeptId(employee.get("deptId"));
            user.setDeptName(employee.get("department"));
            user.setEmail(employee.get("email"));
            user.setPosition(employee.get("position"));
            user.setEnterId(employee.get("enterpriseId"));
            user.setEnterName(enterName);
            users.add(user);
        }
        // uidSet强制回收
        uidSet = null;
        return users;
    }

    /**
     * 解析考勤组负责人列表
     * @param reqMap
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<UserInfo> parserChargemanList(Map<String, Object> reqMap, AttendGroupReq req) {
        List<UserInfo> users = new ArrayList<UserInfo>();
        // RCS提供的新选择器不能提供企业名称、用户号码等基本信息，员工企业名称信息直接使用考勤管理员企业名称2017-11-09
        UserInfo userInfo = req.getUserInfo();
        String enterName = userInfo.getEnterName();
        String enterId = userInfo.getEnterId();
        List<Object> employees = (List<Object>) reqMap.get("chargemanList");
        int count = employees.size();
        // uidSet用于uid去重，防止前端重复提交用户信息
        Set<String> uidSet = new HashSet<String>();
        for (int i = 0; i < count; i++) {
            LinkedTreeMap<String, String> employee = (LinkedTreeMap<String, String>) employees
                .get(i);
            UserInfo user = new UserInfo();
            user.setUid(employee.get("euserId"));
            // 添加失败，即uid重复，则忽略
            if (!uidSet.add(user.getUid())) {
                logger.info("uid repeated,ignore it.uid={}", user.getUid());
                continue;
            }
            user.setEmployeeName(employee.get("name"));
            user.setContactId(employee.get("contactId"));
            user.setPhone(employee.get("phone"));
            user.setDeptId(employee.get("deptId"));
            user.setDeptName(employee.get("department"));
            user.setEmail(employee.get("email"));
            user.setPosition(employee.get("position"));
            user.setEnterId(employee.get("enterpriseId"));
            user.setEnterName(enterName);
            users.add(user);
        }
        // uidSet强制回收
        uidSet = null;
        return users;
    }


    /**
     * 解析员工列表
     * @param reqMap
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<UserInfo> parserEmployeesToWhitelist(Map<String, Object> reqMap, AttendGroupReq req) {
        List<UserInfo> users = new ArrayList<UserInfo>();
        if (AssertUtil.isEmpty(reqMap.get("employees"))) {
            logger.error("employees parameter is empty.");
            return users;
        }
        // RCS提供的新选择器不能提供企业名称、用户号码等基本信息，员工企业名称信息直接使用考勤管理员企业名称2017-11-09
        UserInfo userInfo = req.getUserInfo();
        String enterName = userInfo.getEnterName();
        String enterId = userInfo.getEnterId();
        List<Object> employees = (List<Object>) reqMap.get("employees");
        int count = employees.size();
        // uidSet用于uid去重，防止前端重复提交用户信息
        Set<String> uidSet = new HashSet<String>();
        for (int i = 0; i < count; i++) {
            LinkedTreeMap<String, String> employee = (LinkedTreeMap<String, String>) employees.get(i);
            UserInfo user = new UserInfo();
            user.setUid(employee.get("euserId"));
//            user.setUid(employee.get("userId"));
            // 添加失败，即uid重复，则忽略
            if (!uidSet.add(user.getUid())) {
                logger.info("uid repeated,ignore it.uid={}", user.getUid());
                continue;
            }
            user.setEmployeeName(employee.get("name"));
            user.setContactId(employee.get("contactId"));
            user.setPhone(employee.get("phone"));
            user.setDeptId(employee.get("deptId"));
            user.setDeptName(employee.get("department"));
            user.setEmail(employee.get("email"));
            user.setPosition(employee.get("position"));
            user.setEnterId(employee.get("enterpriseId"));
            user.setEnterName(enterName);
            users.add(user);
        }
        // uidSet强制回收
        uidSet = null;
        return users;
    }

    /**
     * 解析编辑考勤组请求参数
     * @param map
     * @return
     */
    private void parseUpdateReqParam(Map<String, Object> map, AttendGroupReq req) {
        req.setAttendanceId(getAsLong(map, "attendanceId"));
        req.setAttendanceName((String) map.get("attendanceName"));
        req.setEnterId((String) map.get("enterId"));
        req.setEnterName((String) map.get("enterName"));
        req.setAmTime((String) map.get("amTime"));
        req.setPmTime((String) map.get("pmTime"));
        req.setAttendClockSites(parserClockSites(map));
        req.setOutRange(getAsInt(map, "outRange"));
        req.setExamineName((String) map.get("examineName"));
        req.setExamineUid((String) map.get("examineUid"));
        req.setExamineContactId((String) map.get("examineContactId"));
        req.setEmployees(parserEmployees(map, req));
        // 获取考勤组负责人列表
        if (AssertUtil.isNotEmpty(map.get("chargemanList"))) {
            req.setChargemanList(parserChargemanList(map, req));
        }
        // 考勤组类型 1：固定班 2：排班 3：自由班
        req.setAttendType(ConverUtil.string2Int((String) map.get("attendType")));
        /** 固定班排班规则 */
        req.setFixedAttendRule(new Gson().toJson((LinkedTreeMap) map.get("fixedAttendRule")));
        /** 自由班排班规则 */
        req.setFreeAttendRule(new Gson().toJson((LinkedTreeMap) map.get("freeAttendRule")));
        /** 允许迟到时长，单位：分钟 （针对固定班和排班） */
        req.setAllowLateTime(ConverUtil.string2Int((String) map.get("allowLateTime")));
        /** 允许迟到时长，单位：分钟 （针对固定班和排班） */
        req.setRelyHoliday(ConverUtil.string2Int((String) map.get("relyHoliday")));
        req.setScheduleShifts(parserScheduleShifts(map, req));
        /** 是否允许外勤打卡标识，0允许1不允许 */
        req.setIsAllowedOutRangeClock(ConverUtil.string2Int((String) map.get("isAllowedOutRangeClock")));
        /** 是否开启弹性班制标识，0开启1不开启 */
        if (AssertUtil.isNotEmpty(map.get("useFlexibleRule"))) {
            req.setUseFlexibleRule(ConverUtil.string2Int((String) map.get("useFlexibleRule")));
        }
        /** 当开启弹性班制时，设置弹性时长，单位为小时 */
        if (AssertUtil.isNotEmpty(req.getUseFlexibleRule())) {
            if (req.getUseFlexibleRule()==0) {
                req.setFlexitime(Double.parseDouble(String.valueOf(map.get("flexitime")==null?"0":String.valueOf(map.get("flexitime")))));
            }
        }

    }

    /**
     * 解析删除考勤组请求参数
     * @param map
     * @return
     */
    private void parseDelGroupReqParam(Map<String, Object> map, AttendGroupReq req) {
        req.setAttendanceId(getAsLong(map, "attendanceId"));
        req.setEnterId((String) map.get("enterId"));
    }

    private int getAsInt(Map<String, Object> map, String key) {
        if (AssertUtil.isEmpty(map)) {
            return -1;
        }

        String result = (String) map.get(key);
        if (AssertUtil.isEmpty(result)) {
            return -1;
        }
        return ConverUtil.string2Int(result, -1);
    }

    private double getAsDouble(Map<String, Object> map, String key) {
        if (AssertUtil.isEmpty(map)) {
            return -1;
        }
        String result = (String) map.get(key);
        if (AssertUtil.isEmpty(result)) {
            return -1;
        }
        return ConverUtil.string2Double(result, -1);
    }

    private long getAsLong(Map<String, Object> map, String key) {
        if (AssertUtil.isEmpty(map))
        {
            return -1;
        }

        String result = (String) map.get(key);
        if (AssertUtil.isEmpty(result)) {
            return -1;
        }
        return ConverUtil.string2Long(result, -1);
    }

    /**
     * 测试编辑考勤组时，更新待发消息
     * @param request
     * @param response
     */
    @RequestMapping(value = "/testUpdateGroupMsg", method = RequestMethod.POST)
    public void testUpdateGroupMsg(HttpServletRequest request, HttpServletResponse response) {
        AttendGroupReq reqParam = new AttendGroupReq();
        Map<String, Object> map = parserReqJsonParam(request);
        logger.debug("req={}", AtdcStringUtil.getRequestString(map));
        // 对请求数据进行解析
        reqParam.setAttendanceId(getAsLong(map, "attendanceId"));
        reqParam.setAmTime((String) map.get("amTime"));
        reqParam.setPmTime((String) map.get("pmTime"));
        reqParam.setEnterId((String) map.get("enterId"));

        AttendGroup attendGroup = new AttendGroup();
        attendGroup.setAttendanceId(reqParam.getAttendanceId());
        attendGroup.setAmTime(reqParam.getAmTime());
        attendGroup.setPmTime(reqParam.getPmTime());
        attendGroup.setEnterId(reqParam.getEnterId());

        AttendGroup oldAttendGroup = groupService.getAttendGroupInfoFromCache(reqParam.getAttendanceId(), reqParam.getEnterId());
        logger.debug("oldAttendGroup={}", oldAttendGroup);
        logger.debug("attendGroup={}", attendGroup);

        // 编辑考勤组信息时，更新消息待发表中的推送目标数据
        MessageUpdateInfo info = new MessageUpdateInfo(oldAttendGroup,
            attendGroup, true, AttendanceUtil.getCguid(), null, null, null, false);
        AsynTaskProcess.asynExecTask(new MessageUpdateAsynTask(info));
        logger.info(
                "testUpdateGroupMsg put it to MessageUpdateAsynTask. cguid={}|isUpdate={}|{}",
                info.getCguid(), info.isUpdate(), info.getNewAttendGroup());

        processJsonTemplate(response, "attendance/common_json.ftl", new ResBean());
    }
    
    /**
     * 解析考勤组排班班次表
     * @param reqMap
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<AttendanceScheduleShift> parserScheduleShifts(
        Map<String, Object> reqMap, AttendGroupReq req) {
        List<AttendanceScheduleShift> scheduleShiftsList = new ArrayList<AttendanceScheduleShift>(
            4);
        if (AssertUtil.isEmpty(reqMap.get("scheduleShifts"))) {
            logger.info("AttendGroupAction[parserScheduleShifts] scheduleShifts parameter is empty.");
            return scheduleShiftsList;
        }
        List<Object> scheduleShifts = (List<Object>) reqMap.get("scheduleShifts");
        int count = scheduleShifts.size();
        for (int i = 0; i < count; i++) {
            LinkedTreeMap<String, String> attendanceScheduleShiftMap = (LinkedTreeMap<String, String>) scheduleShifts.get(i);
            AttendanceScheduleShift attendanceScheduleShift = new AttendanceScheduleShift();
            attendanceScheduleShift.setScheduleShiftId(
                attendanceScheduleShiftMap.get("scheduleShiftId") == null ?
                    null : Long.valueOf(attendanceScheduleShiftMap.get("scheduleShiftId")));
            attendanceScheduleShift.setScheduleShiftName(attendanceScheduleShiftMap.get("scheduleShiftName"));
            attendanceScheduleShift.setWorkTime(attendanceScheduleShiftMap.get("workTime"));
            scheduleShiftsList.add(attendanceScheduleShift);
        }
        return scheduleShiftsList;
    }
    
    /**
     * 管理员查询企业考勤组列表（PC端）
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryGroupFromPc", method = RequestMethod.POST)
    public void queryGroupFromPc(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);

        AttendGroupReq req = new AttendGroupReq();
        setReqBean(req, request);
        req.setEnterId((String) reqMap.get("enterId"));

        AttendGroupListRes res = groupService.queryGroupFromPc(req);
        processJsonTemplate(response, "attendance/queryGroupFromPc_json.ftl", res);
    }

    /**
     * 用户通过选择部门加入考勤组
     * @param request
     * @param response
     */
    @RequestMapping(value = "/joinGroup",method = RequestMethod.POST)
    public void  joinGroup(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendDepartmentReq req = new AttendDepartmentReq();
        setReqBean(req, request);
        req.setAttendanceId(ConverUtil.string2Long((String) reqMap.get("attendanceId"), -1) );
        req.setDepartmentId((String)reqMap.get("departmentId"));
        req.setUid((String)reqMap.get("uid"));
        logger.info("用户通过选择部门加入考勤组 req={}",req);
        AttendDepartmentRes res = groupService.joinGroup(req);
        processJsonTemplate(response, "attendance/common_json.ftl", res);
    }



    /**
     *  H5 管理员首次进入判断是否存在考勤组
     */
    @RequestMapping(value = "/checkoutGroup", method = RequestMethod.POST)
    public void checkoutGroup(HttpServletRequest request, HttpServletResponse response) {
        AttendGroupListRes res = new AttendGroupListRes();
        AttendGroupReq req = new AttendGroupReq();
        Map<String, Object> reqMap = parserReqJsonParam(request);
        setReqBean(req, request);
        req.setEnterId((String) reqMap.get("enterId"));
        res = groupService.checkoutGroup(req);

        processJsonTemplate(response, "attendance/checkoutGroup_json.ftl", res);
    }

    /**
     * 设置白名单 审批限制
     * @param request
     * @param response
     */
    @RequestMapping(value = "/setWhiteList", method = RequestMethod.POST)
    public void setWhiteList(HttpServletRequest request, HttpServletResponse response) {

        AttendGroupReq req = new AttendGroupReq();
        Map<String, Object> reqMap = parserReqJsonParam(request);
        setReqBean(req, request);
        logger.info("setWhiteList reqMap= {}",reqMap);
        //获取用户列表
        req.setEmployees(parserEmployeesToWhitelist(reqMap,req));
        //获取审批限制数据
        Object approvalRestrict = reqMap.get("approvalRestrict");
        //不为空说明是PC端口
        if (approvalRestrict != null){
            AttendApprovalRestrict attendApprovalRestrict = new AttendApprovalRestrict();
            LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String,String>)approvalRestrict;
            attendApprovalRestrict.setRestrictNumber(linkedTreeMap.get("restrictNumber")==null?0:Integer.parseInt(linkedTreeMap.get("restrictNumber")));
            attendApprovalRestrict.setRestrictStatus(linkedTreeMap.get("restrictStatus")==null?0:Integer.parseInt(linkedTreeMap.get("restrictStatus")));
            req.setAttendApprovalRestrict(attendApprovalRestrict);
        }
        //设置白名单
        AttendGroupRes res = groupService.setWhiteList(req);

        processJsonTemplate(response, "attendance/setWhiteList.ftl", res);
    }

    /**
     * h5 独立设置审批
     * @param request
     * @param response
     * {"":0,"":1}
     */
    @RequestMapping(value = "/setApprovalRestrict", method = RequestMethod.POST)
    public void setApprovalRestrict(HttpServletRequest request,HttpServletResponse response){
        AttendGroupReq req = new AttendGroupReq();
        Map<String, Object> reqMap = parserReqJsonParam(request);
        setReqBean(req, request);
        AttendApprovalRestrict attendApprovalRestrict = new AttendApprovalRestrict();
        attendApprovalRestrict.setRestrictStatus(Integer.parseInt(String.valueOf(reqMap.get("restrictStatus"))));
        attendApprovalRestrict.setRestrictNumber(Integer.parseInt(String.valueOf(reqMap.get("restrictNumber"))));
        req.setAttendApprovalRestrict(attendApprovalRestrict);
        //设置审批限制参数
        AttendGroupRes res = groupService.setApprovalRestrict(req);

        processJsonTemplate(response, "attendance/common_json.ftl", res);

    }


    /**
     * 查询白名单
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryWhiteList", method = RequestMethod.POST)
    public void queryWhiteList(HttpServletRequest request, HttpServletResponse response){
        AttendGroupReq req = new AttendGroupReq();

        setReqBean(req, request);

        //查询白名单
        AttendGroupRes res = groupService.queryWhiteList(req);

        processJsonTemplate(response,"attendance/queryWhiteList.ftl", res);
    }

    /**
     * 移除白名单人员
     * @param request
     * @param response
     */
    @RequestMapping(value = "/removeWhiteListItem", method = RequestMethod.POST)
    public void removeWhiteListItem(HttpServletRequest request, HttpServletResponse response){
        AttendGroupReq req = new AttendGroupReq();

        Map<String, Object> reqMap = parserReqJsonParam(request);
        
        setReqBean(req, request);

        //获取用户列表
        req.setEmployees(parserEmployeesToWhitelist(reqMap,req));

        //移除白名单
        AttendGroupRes res = groupService.removeWhiteListItem(req);

        processJsonTemplate(response,"attendance/common_json.ftl", res);
    }

    /**
     * 移除考勤组负责人
     * @param request
     * @param response
     */
    @RequestMapping(value = "/removeChargeMan", method = RequestMethod.POST)
    public void removeChargeMan(HttpServletRequest request, HttpServletResponse response){
        AttendGroupReq req = new AttendGroupReq();

        Map<String, Object> reqMap = parserReqJsonParam(request);

        setReqBean(req, request);

        //获取用户列表
        req.setEmployees(parserEmployeesToWhitelist(reqMap,req));

        //移除白名单
        AttendGroupRes res = groupService.removeChargeMan(req);

        processJsonTemplate(response,"attendance/common_json.ftl", res);
    }

    /**
     * 检测企业内有无在用考勤组
     * @param request
     * @param response
     */
    @RequestMapping(value = "/checkEnterGroup", method = RequestMethod.POST)
    public void checkEnterGroup(HttpServletRequest request, HttpServletResponse response){
        AttendGroupReq req = new AttendGroupReq();

        Map<String, Object> reqMap = parserReqJsonParam(request);

        setReqBean(req, request);
        AttendGroupRes res = groupService.checkEnterGroup(req,request);

        processJsonTemplate(response,"attendance/checkEnterGroup_json.ftl", res);
    }

    /**
     * 查询设备列表
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryEquipmentList", method = RequestMethod.POST)
    public void queryEquipmentList(HttpServletRequest request, HttpServletResponse response){
        AttendGroupReq attendGroupReq = new AttendGroupReq();

        Map<String, Object> reqMap = parserReqJsonParam(request);

        setReqBean(attendGroupReq, request);

        parseQueryEquipmentListParam(reqMap,attendGroupReq);

        AttendGroupRes res = groupService.queryEquipmentList(attendGroupReq,request);

        processJsonTemplate(response,"attendance/queryEquipmentList_json.ftl", res);
    }

    /**
     * 解析查询设备列表请求参数
     */
    private void parseQueryEquipmentListParam(Map<String, Object> map, AttendGroupReq attendGroupReq){
        attendGroupReq.setEnterId((String) map.get("enterId"));
        if (AssertUtil.isNotEmpty(map.get("attendanceId"))) {
            attendGroupReq.setAttendId((String) map.get("attendanceId"));
        }
        if (AssertUtil.isNotEmpty(map.get("employeeName"))) {
            attendGroupReq.setEmployeeName((String) map.get("employeeName"));
        }
    }

    /**
     * 设备后台管理系统删除设备
     * @param request
     * @param response
     */
    @RequestMapping(value = "/removeEquipment", method = RequestMethod.POST)
    public void removeEquipment(HttpServletRequest request, HttpServletResponse response){
        AttendGroupReq attendGroupReq = new AttendGroupReq();

        Map<String, Object> reqMap = parserReqJsonParam(request);

        setReqBean(attendGroupReq, request);

        parsRemoveEquipmentParam(reqMap,attendGroupReq);

        AttendGroupRes res = groupService.removeEquipment(attendGroupReq,request);

        processJsonTemplate(response,"attendance/common_json.ftl", res);
    }

    /**
     * 解析设备后台管理系统删除设备请求参数
     */
    private void parsRemoveEquipmentParam(Map<String, Object> map, AttendGroupReq attendGroupReq){
        attendGroupReq.setEnterId((String) map.get("enterId"));
        attendGroupReq.setUid((String) map.get("uid"));
        if (AssertUtil.isNotEmpty(map.get("equipmentSerial"))) {
            attendGroupReq.setEquipmentSerial((String) map.get("equipmentSerial"));
        }
    }

    /**
     * 设备后台管理系统设置设备数量限制
     * @param request
     * @param response
     */
    @RequestMapping(value = "/setEquipmentLimit", method = RequestMethod.POST)
    public void setEquipmentLimit(HttpServletRequest request, HttpServletResponse response){
        AttendGroupReq attendGroupReq = new AttendGroupReq();

        Map<String, Object> reqMap = parserReqJsonParam(request);

        setReqBean(attendGroupReq, request);

        parseSetEquipmentLimitParam(reqMap,attendGroupReq);

        AttendGroupRes res = groupService.setEquipmentLimit(attendGroupReq,request);

        processJsonTemplate(response,"attendance/common_json.ftl", res);
    }

    /**
     * 解析设备后台管理系统设置设备数量限制请求参数
     */
    private void parseSetEquipmentLimitParam(Map<String, Object> map, AttendGroupReq attendGroupReq){
        attendGroupReq.setEnterId((String) map.get("enterId"));
        for (Map.Entry s:map.entrySet()) {

        }
        attendGroupReq.setEquipmentLimit((String) map.get("equipmentLimit"));
        attendGroupReq.setEquipmentStatus((String) map.get("equipmentUseStatus"));
    }

    /**
     * 设备打卡入库
     * @param request
     * @param response
     */
    @RequestMapping(value = "/setEquipment", method = RequestMethod.POST)
    public void insertEquipment(HttpServletRequest request, HttpServletResponse response){
        AttendGroupReq attendGroupReq = new AttendGroupReq();

        Map<String, Object> reqMap = parserReqJsonParam(request);

        setReqBean(attendGroupReq, request);

        parsSetEquipmentParam(reqMap,attendGroupReq);

        AttendGroupRes res = groupService.insertEquipment(attendGroupReq,request);

        processJsonTemplate(response,"attendance/common_json.ftl", res);
    }

    /**
     * 解析设备打卡入库请求参数
     */
    private void parsSetEquipmentParam(Map<String, Object> map, AttendGroupReq attendGroupReq){
        attendGroupReq.setEquipmentSerial((String) map.get("equipmentSerial")==null?"":(String) map.get("equipmentSerial"));
        attendGroupReq.setAttendanceName((String) map.get("attendanceName"));
        attendGroupReq.setEquipmentDeviceType((String) map.get("equipmentDeviceType")==null?"":(String) map.get("equipmentDeviceType"));
    }

}

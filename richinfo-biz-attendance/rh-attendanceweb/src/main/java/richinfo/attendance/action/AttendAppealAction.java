/**
 * 文件名：AttendAppealAction.java
 * 创建日期： 2017年10月11日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月11日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import richinfo.attendance.bean.AbnormalAppealRes;
import richinfo.attendance.bean.AttendAppealReq;
import richinfo.attendance.bean.AttendAppealRes;
import richinfo.attendance.bean.AttendExamineRes;
import richinfo.attendance.common.ResBean;
import richinfo.attendance.service.AttendAppealService;
import richinfo.attendance.service.AttendService;
import richinfo.attendance.service.impl.AttendAppealServiceImpl;
import richinfo.attendance.service.impl.AttendServiceImpl;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.ConverUtil;
import richinfo.attendance.util.TimeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * 功能描述：考勤异常申诉
 *
 */
@Controller
@RequestMapping("/appeal")
public class AttendAppealAction extends BaseAttendanceAction {
    private AttendAppealService appealService = new AttendAppealServiceImpl();

    private AttendService attednService = new AttendServiceImpl();

    /**
     * 考勤异常申诉
     * @param request
     * @param response
     */
    @RequestMapping(value = "/abnormalAppeal", method = RequestMethod.POST)
    public void abnormalAppeal(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendAppealReq reqBean = parseAbnormalAppeal(reqMap);
        setReqBean(reqBean, request);
        AbnormalAppealRes resBean = appealService.abnormalAppeal(reqBean);
        processJsonTemplate(response, "attendance/abnormalAppeal_json.ftl", resBean);
    }

    /**
     * 考勤异常申诉参数校验
     * @param reqMap
     * @return
     */
    private AttendAppealReq parseAbnormalAppeal(Map<String, Object> reqMap) {
        AttendAppealReq reqBean = new AttendAppealReq();
        String goWork = (String) reqMap.get("goWork");
        String leaveWork = (String) reqMap.get("leaveWork");
        Date goWorkDate = null;
        if (AssertUtil.isNotEmpty(goWork)) {
            try {
                goWorkDate = TimeUtil.long2Date(ConverUtil.string2Long(goWork, 0L), "HH:mm:ss");
            } catch (ParseException e) {}
        }

        Date leaveWorkDate = null;
        if (AssertUtil.isNotEmpty(leaveWork)) {
            try {
                leaveWorkDate = TimeUtil.long2Date(ConverUtil.string2Long(leaveWork, 0L), "HH:mm:ss");
            } catch (ParseException e) {}
        }
        reqBean.setReason((String) reqMap.get("reason"));
        reqBean.setGoWork(goWorkDate);
        reqBean.setGoWorkDesc((String) reqMap.get("goWorkDesc"));
        reqBean.setLeaveWork(leaveWorkDate);
        reqBean.setLeaveWorkDesc((String) reqMap.get("leaveWorkDesc"));
        reqBean.setAttendanceDate(TimeUtil.string2Date((String) reqMap.get("attendanceDate"), TimeUtil.BASE_DATE_FORMAT));
        reqBean.setRemark((String) reqMap.get("remark"));
        reqBean.setAttendanceId(ConverUtil.string2Long((String) reqMap.get("attendanceId"), -1));
        reqBean.setAppealRecord(ConverUtil.string2Int((String) reqMap.get("appealRecord"), -1));
        reqBean.setMonthRcdId(ConverUtil.string2Long((String) reqMap.get("monthRcdId"), -1));
        return reqBean;
    }

    /**
     * 查询单个申诉单详情
     * @param request
     */
    @RequestMapping(value = "/querySingleAppealInfo", method = RequestMethod.POST)
    public void querySingleAppealInfo(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendAppealReq reqBean = parseQuerySingleAppealInfo(reqMap);
        setReqBean(reqBean, request);
        logger.info("------------------------query AppealId={}",reqBean.getAppealId());
        AttendAppealRes resBean = appealService.querySingleAppealInfo(reqBean);
        processJsonTemplate(response, "attendance/querySingleAppealInfo_json.ftl", resBean);
    }

    /**
     * 查询单个申诉单详情参数解析
     * @param reqMap
     * @return
     */
    private AttendAppealReq parseQuerySingleAppealInfo(Map<String, Object> reqMap) {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setAppealId(ConverUtil.string2Long((String) reqMap.get("appealId"), -1));
        return reqBean;
    }

    /**
     * 查询用户已申诉考勤单信息
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryUserAppealList", method = RequestMethod.POST)
    public void queryUserAppealList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendAppealReq reqBean = parseQueryUserAppealList(reqMap);
        setReqBean(reqBean, request);
        AttendAppealRes resBean = appealService.queryUserAppealList(reqBean);
        processJsonTemplate(response, "attendance/queryUserAppealList_json.ftl", resBean);
    }

    /**
     * 查询用户已申诉考勤单信息参数解析
     * @param reqMap
     * @return
     */
    private AttendAppealReq parseQueryUserAppealList(Map<String, Object> reqMap) {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setPageNo(ConverUtil.string2Int((String) reqMap.get("pageNo"),
            1));
        reqBean.setPageSize(ConverUtil.string2Int(
            (String) reqMap.get("pageSize"), 20));
        return reqBean;
    }

    /**
     * 查询审批员待审批信息
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryManageAppealList", method = RequestMethod.POST)
    public void queryManageAppealList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendAppealReq reqBean = parseQueryManageAppealList(reqMap);
        setReqBean(reqBean, request);
        AttendAppealRes resBean = appealService.queryManageAppealList(reqBean);
        processJsonTemplate(response, "attendance/queryManageAppealList_json.ftl", resBean);
    }

    /**
     * 查询审批员待审批信息参数解析
     * @param reqMap
     * @return
     */
    private AttendAppealReq parseQueryManageAppealList(Map<String, Object> reqMap) {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setPageNo(ConverUtil.string2Int((String) reqMap.get("pageNo"), 1));
        reqBean.setPageSize(ConverUtil.string2Int((String) reqMap.get("pageSize"), 20));
        return reqBean;
    }

    /**
     * 查询考勤组对应的审批员
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryExamineUidList", method = RequestMethod.POST)
    public void queryExamineUidList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendAppealReq reqBean = parseQueryExamineUidList(reqMap);
        setReqBean(reqBean, request);
        AttendExamineRes resBean = appealService.queryExamineUid(reqBean);
        processJsonTemplate(response, "attendance/queryExamineUidList_json.ftl", resBean);
    }

    /**
     * 查询考勤组对应的审批员参数解析
     * @param reqMap
     * @return
     */
    private AttendAppealReq parseQueryExamineUidList(Map<String, Object> reqMap) {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setEnterId((String) reqMap.get("enterId"));
        reqBean.setAttendanceId(ConverUtil.string2Long((String) reqMap.get("attendanceId"), -1));
        return reqBean;
    }

    /**
     * 撤销考勤异常申诉单
     * @param request
     * @param response
     */
    @RequestMapping(value = "/cancelAppeal", method = RequestMethod.POST)
    public void cancelAppeal(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendAppealReq reqBean = parseCancelAppeal(reqMap);
        setReqBean(reqBean, request);
        ResBean resBean = appealService.cancelAppeal(reqBean);
        processJsonTemplate(response, "attendance/common_json.ftl", resBean);
    }

    /**
     * 撤销考勤异常申诉单参数解析
     * @param reqMap
     * @return
     */
    private AttendAppealReq parseCancelAppeal(Map<String, Object> reqMap) {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setAppealId(ConverUtil.string2Long((String) reqMap.get("appealId"), -1));
        return reqBean;
    }

    /**
     * 审批员审批异常申诉单
     * @param request
     * @param response
     */
    @RequestMapping(value = "/dealAppeal", method = RequestMethod.POST)
    public void dealAppeal(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendAppealReq reqBean = parseDealAppeal(reqMap);
        setReqBean(reqBean, request);
        ResBean resBean = appealService.dealAppeal(reqBean);
        processJsonTemplate(response, "attendance/common_json.ftl", resBean);
    }

    /**
     * 审批员审批异常申诉单参数解析
     * @param reqMap
     * @return
     */
    private AttendAppealReq parseDealAppeal(Map<String, Object> reqMap) {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setAppealId(ConverUtil.string2Long((String) reqMap.get("appealId"), -1));
        reqBean.setExamineResult(ConverUtil.string2Int((String) reqMap.get("examineResult"), -1));
        return reqBean;
    }
}

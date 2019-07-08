/**
 * 文件名：AttendReportAction.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import richinfo.attendance.bean.*;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.service.AttendReportService;
import richinfo.attendance.service.impl.AttendReportServiceImpl;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.ConverUtil;
import richinfo.attendance.util.TimeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static richinfo.attendance.util.TimeUtil.BASE_DATE_FORMAT;
import static richinfo.attendance.util.TimeUtil.BASE_DATE_FORMAT_YYYY_MM;

/**
 * 功能描述： 考勤统计报表接口类
 */
@Controller
@RequestMapping("/report")
public class AttendReportAction extends BaseAttendanceAction {
    private AttendReportService reportService = new AttendReportServiceImpl();

    private Logger logger = LoggerFactory.getLogger(AttendReportAction.class);

    /**
     * 查看员工月报，支持员工查询本人，管理员查询其他人
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryPersonalMonthlyReport", method = RequestMethod.POST)
    public void queryPersonalMonthlyReport(HttpServletRequest request,
        HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 解析请求信息
        AttendReportReq reportReq = parserPersonalMonthlyReportReq(reqMap);
        // 获取会话中的数据信息
        setReqBean(reportReq, request);
        // 查询个人月报
        AttendReportRes reportRes = reportService.queryPersonalMonthlyReport(reportReq);
        logger.debug("queryPersonalMonthlyReport.res={}|useTime={}", reportRes, reportReq.getUseTime());
        // 模板内容返回固定的值
        processJsonTemplate(response, "attendance/personalMonthlyReport_json.ftl", reportRes);
    }

    /**
     * 查看团队日报统计
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamDailyReport", method = RequestMethod.POST)
    public void queryTeamDailyReport(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 解析请求信息
        AttendReportReq reportReq = parserTeamDailyReportReq(reqMap);
        // 获取会话中的数据信息
        setReqBean(reportReq, request);
        // 查询团队日报统计
        AttendReportRes reportRes = reportService.queryTeamDailyReport(reportReq);
        logger.debug("queryTeamDailyReport.res={}|useTime={}", reportRes, reportReq.getUseTime());
        // 模板内容返回固定的值
        processJsonTemplate(response, "attendance/teamDailyReport_json.ftl", reportRes);
    }

    /**
     * 查看团队日报详情
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamDailyInfo", method = RequestMethod.POST)
    public void queryTeamDailyInfo(HttpServletRequest request, HttpServletResponse response) {
        AttendReportReq reportReq = new AttendReportReq();
        setReqBean(reportReq, request);
        Map<String, Object> map = parserReqJsonParam(request);
        paserTeamDailyReqParam(map, reportReq);
        TeamDailyRes respBean = reportService.queryTeamDailyInfo(reportReq);
        processJsonTemplate(response, "attendance/queryTeamDailyInfo_json.ftl", respBean);
    }

    /**
     * 组装团队日报详情请求参数
     * @param map
     * @return
     */
    private void paserTeamDailyReqParam(Map<String, Object> map, AttendReportReq reportReq) {
        reportReq.setPageNo(getAsInt(map, "pageNo"));
        reportReq.setPageSize(getAsInt(map, "pageSize"));
        reportReq.setAttendanceDate((String) map.get("date"));
        reportReq.setEnterId((String) map.get("enterId"));
        if (AssertUtil.isNotEmpty((String) map.get("recvEmail"))) {
            reportReq.setRecvEmail(((String) map.get("recvEmail")).replaceAll("，", ","));
        }
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

    /**
     * 查询团队月报统计
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamMonthlyReport", method = RequestMethod.POST)
    public void queryTeamMonthlyReport(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = parserReqJsonParam(request);

        AttendReportReq req = new AttendReportReq();
        // 获取会话中的数据信息
        setReqBean(req, request);
        req.setEnterId((String) map.get("enterId"));
        req.setAttendanceMonth((String) map.get("queryMonth"));
        req.setPageNo(getAsInt(map, "pageNo"));
        req.setPageSize(getAsInt(map, "pageSize"));

        TeamMonthRes res = reportService.queryTeamMonthlyReport(req);
        processJsonTemplate(response, "attendance/queryTeamMonthReport_json.ftl", res);
    }

    /**
     * 发送团队月报统计
     * @param request
     * @param response
     */
    @RequestMapping(value = "/sendTeamMonthlyReport", method = RequestMethod.POST)
    public void sendTeamMonthlyReport(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = parserReqJsonParam(request);

        AttendReportReq req = new AttendReportReq();
        // 获取会话中的数据信息
        setReqBean(req, request);
        req.setEnterId((String) map.get("enterId"));
        req.setAttendanceMonth((String) map.get("queryMonth"));
        if (AssertUtil.isNotEmpty((String) map.get("recvEmail"))) {
            req.setRecvEmail(((String) map.get("recvEmail")).replaceAll("，", ","));
        }

        TeamMonthRes res = reportService.sendTeamMonthlyReport(req);

        processJsonTemplate(response, "attendance/common_json.ftl", res);
    }

    /**
     * 发送员工月报，支持员工查询本人，管理员查询其他人
     * @param request
     * @param response
     */
    @RequestMapping(value = "/sendPersonalMonthlyReport", method = RequestMethod.POST)
    public void sendPersonalMonthlyReport(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 解析请求信息
        AttendReportReq reportReq = parserReportReq(reqMap);
        // 获取会话中的数据信息
        setReqBean(reportReq, request);
        // 查询个人月报
        AttendReportRes reportRes = new AttendReportRes();
        reportRes = reportService.sendPersonalMonthlyReport(reportReq);
        logger.debug("sendPersonalMonthlyReport.res={}|useTime={}", reportRes, reportReq.getUseTime());
        // 模板内容返回固定的值
        processJsonTemplate(response, "attendance/common_json.ftl", reportRes);
    }

    /**
     * 发送团队日报详情
     * @param request
     * @param response
     */
    @RequestMapping(value = "/sendTeamDailyInfo", method = RequestMethod.POST)
    public void sendTeamDailyInfo(HttpServletRequest request, HttpServletResponse response) {
        AttendReportReq reportReq = new AttendReportReq();
        setReqBean(reportReq, request);
        Map<String, Object> map = parserReqJsonParam(request);
        paserTeamDailyReqParam(map, reportReq);
        TeamDailyRes respBean = reportService.sendTeamDailyInfo(reportReq);
        processJsonTemplate(response, "attendance/common_json.ftl", respBean);
    }

    /**
     * 解析查询员工个人月报请求信息
     * @param reqMap
     * @return
     */
    private AttendReportReq parserPersonalMonthlyReportReq(
        Map<String, Object> reqMap) {
        AttendReportReq reportReq = new AttendReportReq();
        reportReq.setEnterId((String) reqMap.get("enterId"));
        reportReq.setUid((String) reqMap.get("uid"));
        reportReq.setAttendanceMonth((String) reqMap.get("queryMonth"));
        reportReq.setAttendanceId(ConverUtil.string2Long((String)reqMap.get("attendanceId")));
        if (AssertUtil.isNotEmpty((String) reqMap.get("recvEmail"))) {
            reportReq.setRecvEmail(((String) reqMap.get("recvEmail")).replaceAll("，", ","));
        }
        return reportReq;
    }

    /**
     * 解析查询团队日报统计请求信息
     * @param reqMap
     * @return
     */
    private AttendReportReq parserTeamDailyReportReq(Map<String, Object> reqMap) {
        AttendReportReq reportReq = new AttendReportReq();
        reportReq.setEnterId((String) reqMap.get("enterId"));
        reportReq.setAttendanceDate((String) reqMap.get("queryDay"));
        return reportReq;
    }

    /**
     * PC端查询团队月报统计数据
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamMonthPc", method = RequestMethod.POST)
    public void queryTeamMonthPc(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = parserReqJsonParam(request);

        AttendReportReq req = parseQueryTeamMonthPc(map);
        // 获取会话中的数据信息
        setReqBean(req, request);
        req.setEnterId(req.getUserInfo().getEnterId());
        TeamMonthRes res = reportService.queryTeamMonthPc(req);
        String attendanceMonth = req.getAttendanceMonth();
        if (TimeUtil.convert2long(attendanceMonth, BASE_DATE_FORMAT_YYYY_MM)>=
            TimeUtil.convert2long("2018-11", BASE_DATE_FORMAT_YYYY_MM)){
            processJsonTemplate(response, "attendance/queryTeamMonthPc201811_json.ftl", res);
        }else {
            processJsonTemplate(response, "attendance/queryTeamMonthPc_json.ftl", res);
        }
    }

    /**
     * 解析PC端查询团队月报统计请求参数
     * @param map
     * @return
     */
    private AttendReportReq parseQueryTeamMonthPc(Map<String, Object> map) {
        AttendReportReq req = new AttendReportReq();
        req.setAttendanceMonth((String) map.get("attendanceMonth"));
        req.setAttendanceId(ConverUtil.string2Long((String) map.get("attendanceId"), -1));
        req.setEmployeeName((String) map.get("employeeName"));
        req.setPageNo(getAsInt(map, "pageNo"));
        req.setPageSize(getAsInt(map, "pageSize"));
        return req;
    }

    /**
     * PC端查询员工月报明细
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryEmpMonthPc", method = RequestMethod.POST)
    public void queryEmpMonthPc(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = parserReqJsonParam(request);

        AttendReportReq req = parseQueryEmpMonthPc(map);
        // 获取会话中的数据信息
        setReqBean(req, request);
        req.setEnterId(req.getUserInfo().getEnterId());
        AttendReportRes res = reportService.queryEmpMonthPc(req);
        processJsonTemplate(response, "attendance/queryEmpMonthPc_json.ftl", res);
    }

    /**
     * PC端查询员工月报明细参数注解
     * @param map
     * @return
     */
    private AttendReportReq parseQueryEmpMonthPc(Map<String, Object> map) {
        AttendReportReq req = new AttendReportReq();
        req.setStartDate((String) map.get("startDate"));
        req.setEndDate((String) map.get(("endDate")));
        req.setAttendanceId(ConverUtil.string2Long((String) map.get("attendanceId"), -1));
        req.setEmployeeName((String) map.get("employeeName"));
        req.setPageNo(getAsInt(map, "pageNo"));
        req.setPageSize(getAsInt(map, "pageSize"));
        return req;
    }

    /**
     * 导出PC端团队月报统计数据
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportTeamMonthPc", method = RequestMethod.POST)
    public void exportTeamMonthPc(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = parserReqJsonParam(request);

        AttendReportReq req = parseExportTeamMonthInfoPc(map);
        // 获取会话中的数据信息
        setReqBean(req, request);
        req.setEnterId(req.getUserInfo().getEnterId());
        AttendExportReptRes res = reportService.exportTeamMonthInfoPc(req);
        UserInfo user = req.getUserInfo();
        boolean result = res.isSuccess() ? true : false;
        logger.info(
                "exportTeamMonthPc operate,result={}|enterId={}|uid={}|phone={}|req={}|code={}|summary={}",
                result, user.getEnterId(), user.getUid(), user.getPhone(), req,
                res.getCode(), res.getSummary());
        processJsonTemplate(response, "attendance/exportReptInfo_json.ftl", res);
    }

    /**
     * 注值PC端团队月报统计数据请求体
     * @param map
     * @return
     */
    private AttendReportReq parseExportTeamMonthInfoPc(Map<String, Object> map) {
        AttendReportReq req = new AttendReportReq();
        req.setAttendanceId(ConverUtil.string2Long((String) map.get("attendanceId"), -1));
        req.setEmployeeName((String) map.get("employeeName"));
        req.setAttendanceMonth((String) map.get("attendanceMonth"));
        return req;
    }

    /**
     * 导出PC端员工月报明细数据
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportEmpMonthPc", method = RequestMethod.POST)
    public void exportEmpMonthPc(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = parserReqJsonParam(request);
        AttendReportReq req = parseExportEmpMonthPc(map);
        // 获取会话中的数据信息
        setReqBean(req, request);
        req.setEnterId(req.getUserInfo().getEnterId());
        AttendExportReptRes res = reportService.exportEmpMonthPc(req);
        UserInfo user = req.getUserInfo();
        boolean result = res.isSuccess() ? true : false;
        logger.info(
                "exportEmpMonthPc operate,result={}|enterId={}|uid={}|phone={}|req={}|code={}|summary={}",
                result, user.getEnterId(), user.getUid(), user.getPhone(), req,
                res.getCode(), res.getSummary());
        processJsonTemplate(response, "attendance/exportReptInfo_json.ftl", res);
    }

    /**
     * 注值PC端员工月报明细数据请求体
     * @param map
     * @return
     */
    private AttendReportReq parseExportEmpMonthPc(Map<String, Object> map) {
        AttendReportReq req = new AttendReportReq();
        req.setAttendanceId(ConverUtil.string2Long((String) map.get("attendanceId"), -1));
        req.setEmployeeName((String) map.get("employeeName"));
        req.setStartDate((String) map.get("startDate"));
        req.setEndDate((String) map.get("endDate"));
        return req;
    }

    /**
     * 查询团队日报分项数据统计(APP端)
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamDailyItem", method = RequestMethod.POST)
    public void queryTeamDaylyItem(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = parserReqJsonParam(request);
        AttendReportReq req = parseQueryTeamDaylyItem(map);
        // 获取会话中的数据信息
        setReqBean(req, request);
        req.setEnterId(req.getUserInfo().getEnterId());
        AttendReptItemRes res = reportService.queryTeamDailyItem(req);
        processJsonTemplate(response, "attendance/queryTeamDailyItem_json.ftl", res);
    }

    /**
     * 查询团队日报分项数据统计注值请求实体
     * @param map
     * @return
     */
    private AttendReportReq parseQueryTeamDaylyItem(Map<String, Object> map) {
        AttendReportReq req = new AttendReportReq();
        req.setAttendanceDate((String) map.get("attendanceDate"));
        req.setItemId(ConverUtil.string2Int((String) map.get("itemId"), -1));
        req.setPageNo(ConverUtil.string2Int((String) map.get("pageNo"), -1));
        req.setPageSize(ConverUtil.string2Int((String) map.get("pageSize"), -1));
        return req;
    }

    /**
     * 查询团队日报个人月列表考勤数据统计(APP端)
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryEmpMonthList", method = RequestMethod.POST)
    public void queryEmpMonthList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = parserReqJsonParam(request);
        AttendReportReq req = parseQueryEmpMonthList(map);
        // 获取会话中的数据信息
        setReqBean(req, request);
        req.setEnterId(req.getUserInfo().getEnterId());
        AttendReptEmpMonthRes res = reportService.queryEmpMonthList(req);
        processJsonTemplate(response, "attendance/queryEmpMonthList_json.ftl", res);
    }

    /**
     * 团队日报个人月列表考勤数据统计注值请求实体
     * @param map
     * @return
     */
    private AttendReportReq parseQueryEmpMonthList(Map<String, Object> map) {
        AttendReportReq req = new AttendReportReq();
        req.setAttendanceMonth((String) map.get("attendanceMonth"));
        req.setUid((String) map.get("uid"));
        return req;
    }




    /**
     * H5统计个人月报统计
     * 2018.11 报表优化需求
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryEmpMonthStatistics", method = RequestMethod.POST)
    public void queryEmpMonthStatistics(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 解析请求信息
        AttendReportReq reportReq = parserReportReq(reqMap);
        // 获取会话中的数据信息
        setReqBean(reportReq, request);
        AttendReportRes reportRes = reportService.queryEmpMonthStatistics(reportReq);
       // logger.debug("queryEmpMonthStatistics.res={}|useTime={}", reportRes, reportReq.getUseTime());
        processJsonTemplate(response, "attendance/personalMonthlyReportNew_json.ftl", reportRes);
    }

    /**
     * H5统计个人月报明细
     * 2018.11 报表优化需求
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryEmpMonthDetail", method = RequestMethod.POST)
    public void queryEmpMonthDetail(HttpServletRequest request,HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 解析请求信息
        AttendReportReq reportReq = parserReportReq(reqMap);
        // 获取会话中的数据信息
        setReqBean(reportReq, request);
        String attendanceMonth = reportReq.getAttendanceMonth();
        // 模板内容返回固定的值
        if (TimeUtil.convert2long(attendanceMonth,BASE_DATE_FORMAT_YYYY_MM) >=
            TimeUtil.convert2long("2018-11",BASE_DATE_FORMAT_YYYY_MM)) {
            AttendReportRes reportRes = reportService.queryEmpMonthDetail(reportReq);
           // logger.debug("queryEmpMonthDetail.res={}|useTime={}", reportRes, reportReq.getUseTime());
            processJsonTemplate(response, "attendance/queryEmpMonthDetail_json.ftl", reportRes);
        } else {
            AttendReportRes reportRes = reportService.queryPersonalMonthlyReport(reportReq);
            //logger.debug("queryPersonalMonthlyReport.res={}|useTime={}", reportRes, reportReq.getUseTime());
            processJsonTemplate(response, "attendance/personalMonthlyReport_json.ftl", reportRes);
        }
    }

    /**
     * H5统计团队月报统计
     * 2018.11 报表优化需求
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamMonthStatistics", method = RequestMethod.POST)
    public void queryTeamMonthStatistics(HttpServletRequest request,HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 解析请求信息
        AttendReportReq reportReq = parserReportReq(reqMap);
        // 获取会话中的数据信息
        setReqBean(reportReq, request);
        String attendanceMonth = reportReq.getAttendanceMonth();
        TeamMonthRes teamMonthRes = reportService.queryTeamMonthStatistics(reportReq);
        // 模板内容返回固定的值
        if (TimeUtil.convert2long(attendanceMonth,BASE_DATE_FORMAT_YYYY_MM) >=
            TimeUtil.convert2long("2018-11",BASE_DATE_FORMAT_YYYY_MM)) {
            processJsonTemplate(response, "attendance/queryTeamMonthReportNew_json.ftl", teamMonthRes);
        } else {
            processJsonTemplate(response, "attendance/queryTeamMonthReportOld_json.ftl", teamMonthRes);
        }
    }

    /**
     * h5查询团队月报分项详情
     * 2018.11 报表优化需求
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamMonthDetails", method = RequestMethod.POST)
    public void queryTeamMonthDetails(HttpServletRequest request,HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 解析请求信息
        AttendReportReq reportReq = parserReportReq(reqMap);
        // 获取会话中的数据信息
        setReqBean(reportReq, request);
        AttendReportRes reportRes = reportService.queryTeamMonthDetails(reportReq);
        logger.debug("queryTeamMonthDetails.res={}| data={} |useTime={}", reportRes ,reportRes.getEmployeeMonthDetailVO(), reportReq.getUseTime());
        // 模板内容返回固定的值
        processJsonTemplate(response, "attendance/queryTeamMonthDetails_json.ftl", reportRes);
    }

    /**
     * H5统计团队日报统计
     * 2018.11 报表优化需求
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamDailyStatistics", method = RequestMethod.POST)
    public void queryTeamDailyStatistics(HttpServletRequest request,
                                         HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 解析请求信息
        AttendReportReq reportReq = parserReportReq(reqMap);
        // 获取会话中的数据信息
        setReqBean(reportReq, request);
        // 查询个人月报
        AttendReportRes reportRes = reportService.queryTeamDailyStatistics(reportReq);
        logger.debug("queryTeamDailyStatistics.res={}|useTime={}", reportRes, reportReq.getUseTime());
        String attendanceDate = reportReq.getAttendanceDate();
        // 模板内容返回固定的值
        if (TimeUtil.convert2long(attendanceDate,BASE_DATE_FORMAT) >=
            TimeUtil.convert2long("2018-11-29",BASE_DATE_FORMAT)) {
            processJsonTemplate(response, "attendance/teamDailyReportNew_json.ftl", reportRes);
        } else {
            processJsonTemplate(response, "attendance/teamDailyReport_json.ftl", reportRes);
        }
    }

    /**
     * H5统计团队日报明细  这个跟以前的一样不改了
     * 2018.11 报表优化需求
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamDailyDetail", method = RequestMethod.POST)
    public void queryTeamDailyDetail(HttpServletRequest request,HttpServletResponse response) {
//        Map<String, Object> reqMap = parserReqJsonParam(request);
//        // 解析请求信息
//        AttendReportReq reportReq = parserPersonalMonthlyReportReq(reqMap);
//        // 获取会话中的数据信息
//        setReqBean(reportReq, request);
//        // 查询个人月报
//        AttendReportRes reportRes = reportService.queryTeamDailyDetail(reportReq);
//        logger.debug("queryTeamDailyDetail.res={}|useTime={}", reportRes, reportReq.getUseTime());
//        String attendanceDate = reportReq.getAttendanceDate();
//        // 模板内容返回固定的值
//        if (TimeUtil.convert2long(attendanceDate,BASE_DATE_FORMAT_YYYY_MM) >=
//            TimeUtil.convert2long("2018-11",BASE_DATE_FORMAT_YYYY_MM)) {
//            processJsonTemplate(response, "attendance/personalMonthlyReportNew_json.ftl", reportRes);
//        } else {
//            processJsonTemplate(response, "attendance/personalMonthlyReport_json.ftl", reportRes);
//        }
    }

    /**
     * h5查询团队日报分项详情
     * 2018.11 报表优化需求
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryTeamDailyItemDetails", method = RequestMethod.POST)
    public void queryTeamDailyItemDetails(HttpServletRequest request,HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendReportReq reportReq = parserReportReq(reqMap);
        setReqBean(reportReq, request);
        AttendReportRes reportRes = reportService.queryTeamDailyItemDetails(reportReq);
        processJsonTemplate(response, "attendance/queryTeamDailyItemNew_json.ftl", reportRes);

        /*String attendanceDate = reportReq.getAttendanceDate();
        if (TimeUtil.convert2long(attendanceDate,BASE_DATE_FORMAT) >=
            TimeUtil.convert2long("2018-11-29",BASE_DATE_FORMAT)) {
            AttendReportRes reportRes = reportService.queryTeamDailyItemDetails(reportReq);
            processJsonTemplate(response, "attendance/queryTeamDailyItemNew_json.ftl", reportRes);
        } else {
            AttendReportRes reportRes = reportService.queryTeamDailyItemDetails(reportReq);
            processJsonTemplate(response, "attendance/queryTeamDailyItemNew_json.ftl", reportRes);
           // AttendReptItemRes reportRes = reportService.queryTeamDailyItem(reportReq);
           // processJsonTemplate(response, "attendance/queryTeamDailyItem_json.ftl", reportRes);
        }*/
    }

    private AttendReportReq parserReportReq(Map<String, Object> reqMap) {
        AttendReportReq reportReq = new AttendReportReq();
        reportReq.setEnterId((String) reqMap.get("enterId"));
        reportReq.setUid((String) reqMap.get("uid"));
        reportReq.setAttendanceMonth((String) reqMap.get("attendanceMonth"));
        if (AssertUtil.isNotEmpty((String) reqMap.get("queryMonth"))) {
            reportReq.setAttendanceMonth((String) reqMap.get("queryMonth"));
        }
        reportReq.setAttendanceDate((String) reqMap.get("attendanceDate"));
        if (AssertUtil.isNotEmpty((String) reqMap.get("queryDay"))) {
            reportReq.setAttendanceDate((String) reqMap.get("queryDay"));
        }
        reportReq.setItemId(ConverUtil.string2Int((String) reqMap.get("itemId"), -1));
        reportReq.setPageNo(ConverUtil.string2Int((String) reqMap.get("pageNo"), -1));
        reportReq.setPageSize(ConverUtil.string2Int((String) reqMap.get("pageSize"), -1));
        reportReq.setAttendanceId(ConverUtil.string2Long((String)reqMap.get("attendanceId")));
        if (AssertUtil.isNotEmpty((String) reqMap.get("recvEmail"))) {
            reportReq.setRecvEmail(((String) reqMap.get("recvEmail")).replaceAll("，", ","));
        }
        return reportReq;
    }

    /**
     *PC查出原始打卡数据
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryOriginalClockDataPc",method = RequestMethod.POST)
    public void  queryOriginalClockDataPc(HttpServletRequest request,HttpServletResponse response){
        Map<String, Object> reqMap = parserReqJsonParam(request);
        logger.info("queryOriginalClockDataPc request param reqMap={}",reqMap);
        AttendReportReq reportReq = parserQueryOriginal(reqMap);
        setReqBean(reportReq, request);
        // 查询个人月报
        AttendRes attendRes = reportService.queryOriginalClockDataPc(reportReq);
        logger.debug("queryOriginalClockDataPc.res={}|useTime={}", attendRes, reportReq.getUseTime());
        // 模板内容返回固定的值
        processJsonTemplate(response, "attendance/queryOriginalClockDataPc_json.ftl", attendRes);
    }

    /**
     * 获取请求参数
     * @param
     * @return
     */
    private AttendReportReq parserQueryOriginal( Map<String, Object> reqMap) {
        AttendReportReq reportReq = new AttendReportReq();
        reportReq.setEnterId((String) reqMap.get("enterId"));
        reportReq.setStartDate(String.valueOf(reqMap.get("startDate")));
        reportReq.setEndDate(String.valueOf(reqMap.get("endDate")));
        reportReq.setAttendanceId(ConverUtil.string2Long((String)reqMap.get("attendanceId"),-1));
        reportReq.setEmployeeName(String.valueOf(reqMap.get("employeeName")));
        reportReq.setLegworkStatus(ConverUtil.string2Int((String) reqMap.get("legworkStatus"), -1));
        reportReq.setPageNo(ConverUtil.string2Int((String) reqMap.get("pageNo"), -1));
        reportReq.setPageSize(ConverUtil.string2Int((String) reqMap.get("pageSize"), -1));
        return  reportReq;
    }

    /**
     * pc导出考勤原始数据
     * @param request
     * @param response
     */
    @RequestMapping(value = "exportOriginalClockDataPc" ,method = RequestMethod.POST)
    public void exportOriginalClockDataPc(HttpServletRequest request,HttpServletResponse response){
        Map<String, Object> reqMap = parserReqJsonParam(request);
        AttendReportReq reportReq = parserQueryOriginal(reqMap);
        setReqBean(reportReq, request);
        AttendExportReptRes res = reportService.exportOriginalClockDataPc(reportReq);
        logger.debug("queryOriginalClockDataPc.res={}|useTime={}", res, reportReq.getUseTime());
        processJsonTemplate(response, "attendance/exportReptInfo_json.ftl", res);
    }
}

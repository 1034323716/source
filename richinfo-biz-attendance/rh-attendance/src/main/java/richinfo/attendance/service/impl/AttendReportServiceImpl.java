/**
 * 文件名：AttendReportServiceImpl.java
 * 创建日期： 2017年6月8日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.*;
import richinfo.attendance.common.AtdcConsts;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.common.ResBean;
import richinfo.attendance.dao.AttendAppealDao;
import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.entity.*;
import richinfo.attendance.service.AttendReportService;
import richinfo.attendance.util.*;
import richinfo.tools.file.FileUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static richinfo.attendance.util.TimeUtil.BASE_DATE_FORMAT_YYYY_MM;

/**
 * 功能描述：报表模块接口实现类
 * 
 */
public class AttendReportServiceImpl implements AttendReportService
{

    private Logger logger = LoggerFactory
        .getLogger(AttendReportServiceImpl.class);

    private AttendReportDao reportDao = new AttendReportDao();

    private AttendanceConfig config = AttendanceConfig.getInstance();

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();

    private AttendAppealDao attendAppealDao = new AttendAppealDao();

    /**
     * 查询团队日报详情
     */
    @Override
    public TeamDailyRes queryTeamDailyInfo(AttendReportReq reqParam)
    {
        TeamDailyRes respBean = new TeamDailyRes();

        UserInfo userInfo = reqParam.getUserInfo();
        // 校验请求参数
        checkQueryTeamDailyParam(reqParam, respBean, userInfo);
        if (!respBean.isSuccess())
        {
            logger
                .info(
                    "queryTeamDailyInfo checkParam failed,phone={}|uid={}|resultCode={}|summary={}|reqParam={}",
                    userInfo.getPhone(), userInfo.getUid(), respBean.getCode(),
                    respBean.getSummary(), reqParam);
            return respBean;
        }
        logger
            .info(
                "queryTeamDailyInfo checkParam success,phone={}|uid={}|reqParam={}",
                userInfo.getPhone(), userInfo.getUid(), reqParam);

        // 实体bean注入
        TeamDailyEntity teamReq = dealQueryTeamDailyInfoBeanParam(reqParam);
        // 查询消息总量
        long totalCount = reportDao.queryTeamDailyCount(teamReq, userInfo);

        // 处理分页数据
        dealQueryTeamDailyInfoPages(teamReq, totalCount);

        if (totalCount <= 0 || teamReq.getPageNo() > teamReq.getTotalSize())
        {
            logger
                .info(
                    "queryTeamDailyInfo can't find data,phone={}|uid={}|reqParam={}",
                    userInfo.getPhone(), userInfo.getUid(), teamReq);
            respBean.setPageNo(teamReq.getPageNo());
            respBean.setTotalCount(teamReq.getTotalCount());
            respBean.setPageSize(teamReq.getPageSize());
            respBean.setData(null);
            return respBean;
        }

        List<TeamDailyEntity> list = reportDao.queryTeamDailyInfo(teamReq,
            userInfo);
        respBean.setPageNo(teamReq.getPageNo());
        respBean.setTotalCount(totalCount);
        respBean.setPageSize(teamReq.getPageSize());
        respBean.setData(list);
        return respBean;
    }

    /**
     * 处理团队日报详情查询分页数据
     * @param teamReq
     * @param totalCount
     */
    private void dealQueryTeamDailyInfoPages(TeamDailyEntity teamReq,
        long totalCount)
    {
        if (totalCount > 0)
        {
            int pageSize = teamReq.getPageSize();

            if (pageSize > AttendanceConfig.getInstance().getPropertyInt(
                "attend.page.maxSize", 200)
                || pageSize < 1)
            {
                pageSize = 20;
                teamReq.setPageSize(20);
            }
            int totalSize = (int) ((totalCount % pageSize == 0) ? totalCount
                / pageSize : (totalCount / pageSize) + 1);
            teamReq.setTotalSize(totalSize);

            teamReq.setOffSet((teamReq.getPageNo() - 1) * pageSize);

            if (teamReq.getPageNo() <= 0)
            {
                teamReq.setPageNo(1);
                teamReq.setOffSet(0);
            }
        }
    }

    /**
     * 处理团队日报详情查询bean注入
     * @param reqParam
     * @return
     */
    private TeamDailyEntity dealQueryTeamDailyInfoBeanParam(
        AttendReportReq reqParam)
    {
        TeamDailyEntity teamReq = new TeamDailyEntity();
        teamReq.setAttendanceDate(reqParam.getAttendanceDate());
        teamReq.setEnterId(reqParam.getEnterId());
        teamReq.setPageNo(reqParam.getPageNo());
        teamReq.setPageSize(reqParam.getPageSize());
        return teamReq;
    }

    /**
     * 校验团队日报详情查看参数
     * @param reqParam
     * @param respBean
     * @return
     */
    private void checkQueryTeamDailyParam(AttendReportReq reqParam,
        TeamDailyRes respBean, UserInfo userInfo)
    {
        if (AssertUtil.isEmpty(reqParam.getEnterId())) {
            // 企业Id为空
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }

        if (userInfo.getIsAdmin() != 1 && userInfo.getRoleType() != 1) {
            // 是否为管理员
            respBean.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            respBean.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }

        if (!reqParam.getEnterId().equals(userInfo.getEnterId())) {
            // 判断企业Id与会话信息中的企业Id一致(即表示操作的是其他企业)
            respBean.setCode(AtdcResultCode.ATDC106.ENTERID_NOT_MATCHED);
            respBean.setSummary(AtdcResultSummary.ATDC106.ENTERID_NOT_MATCHED);
            return;
        }

        if (AssertUtil.isEmpty(reqParam.getAttendanceDate()))
        {
            // 查询日期为空
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }

        if (!TimeUtil.checkDateFormat(reqParam.getAttendanceDate(),
            TimeUtil.BASE_DATE_FORMAT))
        {
            // 查询日期时间格式错误
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return;
        }
        int dateCount = 0;
        try
        {
            dateCount = TimeUtil.dateDiff(
                TimeUtil.string2Date(reqParam.getAttendanceDate()),
                TimeUtil.string2Date(TimeUtil.getCurrentDate()));
        }
        catch (Exception e)
        {
            logger.error(
                "queryTeamDailyInfo dateDiff format failed,reqParam={}",
                reqParam, e);
        }
        if (dateCount < 0)
        {
            // 查询日期非法：超过当前日期
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_ILLEGAL_DATE);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_ILLEGAL_DATE);
            return;
        }
        // 如果未传递pageSize或传递数据非法，则使用默认值20
        if (reqParam.getPageSize() <= 0 || reqParam.getPageSize() > 50)
        {
            reqParam.setPageNo(20);
        }
    }

    @Override
    public AttendReportRes queryPersonalMonthlyReport(AttendReportReq reportReq)
    {
        // 参数校验
        AttendReportRes reportRes = checkMonthReportReq(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkPersonalMonthlyReportReq failed.reportReq={}|userInfo={}",
                    reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 权限校验，支持员工查询本人，管理员查询其他人
        reportRes = checkAuthorityOfPMR(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkAuthorityOfPMR failed.reportReq={}|userInfo={}",
                reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 查询员工个人月报
        List<EmployeeMonthDetail> employeeMonth = reportDao.queryPersonalMonthlyReport(reportReq.getUid(),
                reportReq.getAttendanceMonth(), reportReq.getEnterId());

        //查询企业是否开启
        try {
            AttendApprovalRestrict attendApprovalRestrict = attendAppealDao.queryApprovalRestrictByEnterId(reportReq.getUserInfo().getEnterId());
            //说明没有开启
            if (attendApprovalRestrict == null || attendApprovalRestrict.getRestrictStatus() == 0){
                reportRes.setRestrictStatus(0);
                //设置了上限
            }else {
                //查询月份的提交审批数
                int approvalCount = attendAppealDao.queryApprovalCount(reportReq.getUid(), reportReq.getAttendanceMonth(), reportReq.getEnterId());
                reportRes.setRestrictStatus(approvalCount >= attendApprovalRestrict.getRestrictNumber() ? 1 : 0);
            }

        }catch (Exception e){
            e.printStackTrace();
            UserInfo userInfo = reportReq.getUserInfo();
            logger.error("queryApprovalRestrictByEnterId  error uid={}|enterId={}|errorMsg={} ",userInfo.getUid(),userInfo.getEnterId(),e);
            reportRes.setCode(AtdcResultCode.ATDC107.BASE);
            reportRes.setSummary(AtdcResultSummary.ATDC107.S_ERROR);
        }

        // 返回查询结果，即使结果为空，仍然返回成功
        reportRes.setEmployeeMonth(employeeMonth);

        return reportRes;
    }

    @Override
    public AttendReportRes queryTeamDailyReport(AttendReportReq reportReq) {
        // 参数校验
        AttendReportRes reportRes = checkDailyReportReq(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkTeamDailyReportReq failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 权限校验
        reportRes = checkAuthorityOfTDR(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkAuthorityOfTDR failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            return reportRes;
        }

        // 统计企业ID的团队日报
        TeamDailyReportEntity teamDailyReport = reportDao.sumTeamDailyReport(reportReq.getEnterId(), reportReq.getAttendanceDate());

        logger.debug("sum result.reportReq={}|teamDailyReport={}", reportReq, teamDailyReport);

        reportRes.setTeamDailyReport(teamDailyReport);

        return reportRes;
    }

    /**
     * 查询个人月报权限校验，支持员工查本人、管理员查所有人
     * @param reportReq
     * @return
     */
    private AttendReportRes checkAuthorityOfPMR(AttendReportReq reportReq) {
        AttendReportRes reportRes = new AttendReportRes();

        String uid = reportReq.getUid();
        // 获取回话信息中的uid
        String uidInSeesion = reportReq.getUserInfo().getUid();
        // 是否查本人的个人月报
        if (uid.equals(uidInSeesion)) {
            return reportRes;
        }
        // 若不是查本人月报，进一步判断是否为管理员
        reportRes = checkIsAdmin(reportReq);
        if (!reportRes.isSuccess()) {
            return reportRes;
        }

        String enterId = reportReq.getEnterId();
        // 获取回话信息中的enterId
        String enterIdInSession = reportReq.getUserInfo().getEnterId();
        // 校验管理员是否跨企业操作
        if (AssertUtil.isEmpty(enterId) || !enterId.equals(enterIdInSession)) {
            logger.warn("enterId not matched.reqEnterId={}|SessionEnterId={}", enterId, enterIdInSession);
            reportRes.setCode(AtdcResultCode.ATDC106.ENTERID_NOT_MATCHED);
            reportRes.setSummary(AtdcResultSummary.ATDC106.ENTERID_NOT_MATCHED);
        }
        return reportRes;
    }

    /**
     * 判断是否为管理员身份
     * @param
     * @return
     */
    private AttendReportRes checkIsAdmin(AttendReportReq reportReq)
    {
        AttendReportRes reportRes = new AttendReportRes();
        // 判断是否为管理员身份
        if (reportReq.getUserInfo().getIsAdmin() != 1 && reportReq.getUserInfo().getRoleType() != 1)
        {
            logger.warn("user is not admin.user={}", reportReq.getUserInfo());
            reportRes.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            reportRes.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return reportRes;
        }

        return reportRes;
    }



    /**
     * 校验查询团队日报统计的请求权限
     * @param reportReq
     * @return
     */
    private AttendReportRes checkAuthorityOfTDR(AttendReportReq reportReq)
    {
        // 校验是否为管理员权限
        AttendReportRes reportRes = checkIsAdmin(reportReq);
        if (!reportRes.isSuccess())
        {
            return reportRes;
        }
        // 校验查询的企业ID是否为自己管理的企业
        String enterId = reportReq.getEnterId();
        String enterIdInSession = reportReq.getUserInfo().getEnterId();
        if (!enterId.equals(enterIdInSession))
        {
            logger.warn("admin not belong to the enter.enterId={}|userInfo={}",
                enterId, reportReq.getUserInfo());
            reportRes.setCode(AtdcResultCode.ATDC106.NOT_BELONGTO_ADMIN);
            reportRes.setSummary(AtdcResultSummary.ATDC106.NOT_BELONGTO_ADMIN);
            return reportRes;
        }

        return reportRes;
    }

    /**
     *  校验日报的请求参数
     * @param reportReq
     * @return
     */
    private AttendReportRes checkDailyReportReq(AttendReportReq reportReq)
    {
        AttendReportRes reportRes = new AttendReportRes();
        // 校验企业ID
        if (AssertUtil.isEmpty(reportReq.getEnterId())) {
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return reportRes;
        }
        // 查询日期校验
        if (!validDate(reportReq)) {
            // 日期参数校验失败
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return reportRes;
        }
        // 判断是否为管理员身份
        if (reportReq.getUserInfo().getIsAdmin() != 1 && reportReq.getUserInfo().getRoleType() != 1) {
            reportRes.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            reportRes.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return reportRes;
        }
//        if (AssertUtil.isEmpty(reportReq.getAttendanceDate())) {
//            String queryDay = AtdcTimeUtil
//                .getYesterday(TimeUtil.BASE_DATE_FORMAT);
//            // 设置查询日期为昨天
//            reportReq.setAttendanceDate(queryDay);
//            logger.warn("queryDay is empty,use yesterday.queryDay={}|userInfo={}",
//                queryDay, reportReq.getUserInfo());
//        } else {
//            if (!TimeUtil.checkDateFormat(reportReq.getAttendanceDate(),
//                TimeUtil.BASE_DATE_FORMAT)) {
//                reportRes.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
//                reportRes.setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
//                return reportRes;
//            }
//        }

        return reportRes;
    }

    @Override
    public TeamMonthRes queryTeamMonthlyReport(AttendReportReq reqParam) {
        TeamMonthRes res = new TeamMonthRes();
        logger.info("queryTeamMonthlyReport enterId={}|month={}|userInfo={}",
            reqParam.getAttendanceId(), reqParam.getAttendanceMonth(),
            reqParam.getUserInfo());

        // 管理员权限检测
        if (reqParam.getUserInfo().getIsAdmin() != 1)
        {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return res;
        }

        int totalCount = 0;
        List<TeamMonthEntity> list = new ArrayList<TeamMonthEntity>();
        try
        {
            totalCount = reportDao.queryTeamMonthCount(reqParam);
            if (totalCount > 0)
            {
                // 分页参数检查处理
                int currentPage = reqParam.getPageNo();
                if (currentPage < 1)
                {
                    reqParam.setPageNo(1);
                }
                int pageSize = reqParam.getPageSize();
                if (pageSize < 1
                    || pageSize > AttendanceConfig.getInstance()
                        .getPropertyInt("attend.page.maxSize", 200))
                {
                    reqParam.setPageSize(20);
                }
                list = reportDao.queryTeamMonthReport(reqParam);
            }
            logger
                .info(
                    "queryTeamMonthlyReport enterId={}|month={}|totalCount={}|phone={}",
                    reqParam.getAttendanceId(), reqParam.getAttendanceMonth(),
                    totalCount, reqParam.getUserInfo().getPhone());
        }
        catch (Exception e)
        {
            logger
                .error(
                    "queryTeamMonthReport failed,enterId={}|queryDate={}|pageNo={}|pageSize={}",
                    reqParam.getEnterId(), reqParam.getAttendanceMonth(),
                    reqParam.getPageNo(), reqParam.getPageSize(), e);
        }

        res.setPageNo(reqParam.getPageNo());
        res.setTotalCount(totalCount);
        res.setPageSize(reqParam.getPageSize());
        res.setData(list);

        return res;
    }

    @Override
    public TeamMonthRes sendTeamMonthlyReport(AttendReportReq reqParam) {
        TeamMonthRes res = new TeamMonthRes();
        logger.info("sendTeamMonthlyReport enterId={}|month={}|recvEmail={}|userInfo={}",
                reqParam.getEnterId(),reqParam.getAttendanceMonth(),
                reqParam.getRecvEmail(),reqParam.getUserInfo());

        // 管理员权限检测
        if (reqParam.getUserInfo().getIsAdmin() != 1 && reqParam.getUserInfo().getRoleType() != 1) {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return res;
        }

        // 接收人邮件地址校验
        boolean isEmail = MailUtil.getInstance().checkEmail(reqParam.getRecvEmail());
        if (!isEmail) {
            logger.warn("checkEmail failed.reportReq={}|userInfo={}", reqParam, reqParam.getUserInfo());
            res.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return res;
        }

        List<TeamMonthEntity> list = new ArrayList<TeamMonthEntity>();
        try {
            // 先查所有list数据
            list = reportDao.sendTeamMonthStatistics(reqParam);

//            TeamMonthEntity entity1 = new  TeamMonthEntity();
//            entity1.setPhone("");
//            entity1.setUid("ZXC");
//            entity1.setEmployeeName("CCCC");
//            list.add(entity1);


            int count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger.info("sendTeamMonthStatistics enterId={}|month={}|recvEmail={}|phone={}|listSize={}",
                    reqParam.getEnterId(), reqParam.getAttendanceMonth(),
                    reqParam.getRecvEmail(), reqParam.getUserInfo().getPhone(), count);
            if (AssertUtil.isEmpty(list)) {
                res.setCode(AtdcResultCode.ATDC108.NO_EXPORT_DATA);
                res.setSummary(AtdcResultSummary.ATDC108.NO_EXPORT_DATA);
                return res;
            } else {
                // 生成execl 根据月份使用不同导出模板
                String excelPath = null;
                if (TimeUtil.convert2long(reqParam.getAttendanceMonth(),BASE_DATE_FORMAT_YYYY_MM) >
                    TimeUtil.convert2long("2018-10",BASE_DATE_FORMAT_YYYY_MM)) {
                    excelPath = exportTeamMonthlyExeclNew(reqParam.getUserInfo().getEnterName(),
                        reqParam.getAttendanceMonth(), list);
                } else {
                    excelPath = exportTeamMonthlyExecl(reqParam.getUserInfo().getEnterName(),
                        reqParam.getAttendanceMonth(), list);
                }

                String subject = "【" + reqParam.getUserInfo().getEnterName()
                    + "】 打卡统计团队月报-" + reqParam.getAttendanceMonth();
                // 组装并发送邮件
                String content = "您好，您导出的报表见附件。<br /><br />---------<br />和飞信负一屏考勤打卡团队";
                boolean send = MailUtil.getInstance().send(subject, content,
                        reqParam.getRecvEmail(), excelPath);

                if (!send){
                    res.setCode(AtdcResultCode.ATDC108.PARAM_PARSE_FAILED);
                    res.setSummary(AtdcResultSummary.ATDC108.EXCEL_FAILURE);
                }
                // 发送完成后删除报表文件
                FileUtil.delete(excelPath);
            }
        } catch (Exception e) {
            logger.error("sendTeamMonthlyReport failed,enterId={}|queryDate={}|recvEmail={}|phone={}",
                    reqParam.getEnterId(), reqParam.getAttendanceMonth(),
                    reqParam.getRecvEmail(), reqParam.getUserInfo().getPhone(), e);
            res.setCode(AtdcResultCode.S_ERROR);
            res.setSummary(AtdcResultSummary.ATDC107.S_ERROR);
        }

        return res;
    }

    /**
     * 组装数据，生成execl：打卡统计团队月报
     * @param list
     */
    private String exportTeamMonthlyExecl(String enterName, String month, List<TeamMonthEntity> list) {
        String title = "【" + enterName + "】 打卡统计团队月报-" + month;
        String[] rowsName = new String[] { "部门", "员工","contractId" ,"手机号码","正常", "外勤", "迟到", "早退",
            "未打卡","已申诉" };

        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        // 循环组装单元格数据
        for (TeamMonthEntity entity : list) {
            objs = new Object[rowsName.length];
            objs[0] = entity.getAttendanceName();
            objs[1] = entity.getEmployeeName();
            objs[2] = (entity.getContactId());
            objs[3] = entity.getPhone()==null || entity.getPhone().equals("") ?"-":entity.getPhone();
            objs[4] = entity.getNormalDays();
            objs[5] = entity.getOutsideDays();
            objs[6] = entity.getLateDays();
            objs[7] = entity.getEarlyDays();
            objs[8] = entity.getNotClockedDays();
            objs[9] = entity.getAppealDays();
            dataList.add(objs);
        }

        // 生成文件的路径
        String path = AttendanceConfig.getInstance().getProperty(
            "attend.export.file.dir", "/data/exportTempFile/")
            + title + ".xls";
        // 调用公共组件方法生成execl
        ExeclUtil.getInstance().export(title, rowsName, dataList, path, true);

        return path;
    }

    /**
     * 组装数据，生成execl：打卡统计团队月报 新版
     * @param list
     */
    private String exportTeamMonthlyExeclNew(String enterName, String month, List<TeamMonthEntity> list) {
        String title = "【" + enterName + "】 打卡统计团队月报-" + month;
        String[] rowsName = new String[] { "部门", "员工" ,"手机号码", "正常", "外勤", "迟到", "早退",
            "上午未打卡","下午未打卡","contractId" };

        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        // 循环组装单元格数据
        for (TeamMonthEntity entity : list) {
            objs = new Object[rowsName.length];
            objs[0] = entity.getAttendanceName();
            objs[1] = entity.getEmployeeName();
            objs[2] = entity.getPhone()==null || entity.getPhone().equals("") ? "-" : entity.getPhone();
            objs[3] = entity.getNormalDays();
            objs[4] = entity.getOutsideDays();
            objs[5] = entity.getLateDays();
            objs[6] = entity.getEarlyDays();
            objs[7] = entity.getGoNotClockedDays();
            objs[8] = entity.getLeaveNotClockedDays();
            objs[9] = entity.getContactId();
            dataList.add(objs);
        }

        // 生成文件的路径
        String path = AttendanceConfig.getInstance().getProperty(
            "attend.export.file.dir", "/data/exportTempFile/")
            + title + ".xls";
        // 调用公共组件方法生成execl
        ExeclUtil.getInstance().export(title, rowsName, dataList, path, true);

        return path;
    }

    @Override
    public AttendReportRes sendPersonalMonthlyReport(AttendReportReq reportReq)
    {
        // 参数校验
        AttendReportRes reportRes = checkMonthReportReq(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkPersonalMonthlyReportReq failed.reportReq={}|userInfo={}",
                    reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 权限校验，支持员工查询本人，管理员查询其他人
        reportRes = checkAuthorityOfPMR(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkAuthorityOfPMR failed.reportReq={}|userInfo={}",
                reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 接收人邮件地址校验
        boolean isEmail = MailUtil.getInstance().checkEmail(
            reportReq.getRecvEmail());
        if (!isEmail) {
            logger.warn("checkEmail failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return reportRes;
        }
        // 查询员工个人月报
        List<EmployeeMonthDetail> employeeMonth = reportDao
            .queryPersonalMonthlyReport(reportReq.getUid(),
                reportReq.getAttendanceMonth(), reportReq.getEnterId());

        if (AssertUtil.isEmpty(employeeMonth)) {
            logger.info("sendPersonalMonthlyReport data is null,reportReq={}|userInfo={}",
                    reportReq, reportReq.getUserInfo());
            reportRes.setCode(AtdcResultCode.ATDC108.NO_EXPORT_DATA);
            reportRes.setSummary(AtdcResultSummary.ATDC108.NO_EXPORT_DATA);
            return reportRes;
        }
        String employeeName = employeeMonth.get(0).getEmployeeName();
        // 生成execl
        String excelPath = exportPersonalMonthlyExecl(reportReq.getUserInfo()
            .getEnterName(), employeeName, reportReq.getAttendanceMonth(),
            employeeMonth);

        String subject = "【" + reportReq.getUserInfo().getEnterName()
            + "】 打卡统计个人月报（" + employeeName + "）-"
            + reportReq.getAttendanceMonth();
        // 组装并发送邮件
        String content = "您好，您导出的报表见附件。<br /><br />---------<br />和飞信负一屏考勤打卡团队";
        boolean send = MailUtil.getInstance().send(subject, content, reportReq.getRecvEmail(),
                excelPath);
        if (!send){
            reportRes.setCode(AtdcResultCode.ATDC108.PARAM_PARSE_FAILED);
            reportRes.setSummary(AtdcResultSummary.ATDC108.EXCEL_FAILURE);
        }

        // 发送完成后删除报表文件
        FileUtil.delete(excelPath);

        return reportRes;

    }

    /**
     * 导出execl：打卡统计个人月报
     * @param enterName
     * @param attendanceMonth
     * @param list
     * @return
     */
    private String exportPersonalMonthlyExecl(String enterName,
        String employeename, String attendanceMonth,
        List<EmployeeMonthDetail> list)
    {

        String title = "【" + enterName + "】 打卡统计个人月报（" + employeename + "）-"
            + attendanceMonth;
        String[] rowsName = new String[] {"contractId", "日期", "上班打卡时间", "描述", "下班打卡时间",
            "描述", "备注" };

        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        // 循环组装单元格数据
        for (EmployeeMonthDetail entity : list)
        {
            objs = new Object[rowsName.length];
            objs[0] = entity.getContactId();
            objs[1] = TimeUtil.date2String(entity.getAttendanceDate(),
                TimeUtil.BASE_DATE_FORMAT);
            objs[2] = (entity.getGoWork() == null ? "-" : TimeUtil.date2String(
                entity.getGoWork(), TimeUtil.BASE_TIME_FORMAT));
            objs[3] = (entity.getGoWorkDesc() == null ? " " : entity
                .getGoWorkDesc());
            objs[4] = (entity.getLeaveWork() == null ? "-" : TimeUtil
                .date2String(entity.getLeaveWork(), TimeUtil.BASE_TIME_FORMAT));
            objs[5] = (entity.getLeaveWorkDesc() == null ? " " : entity
                .getLeaveWorkDesc());
            objs[6] = entity.getRemark();
            dataList.add(objs);
        }

        // 生成文件的路径
        String path = AttendanceConfig.getInstance().getProperty(
            "attend.export.file.dir", "/data/exportTempFile/")
            + title + ".xls";
        // 调用公共组件方法生成execl
        ExeclUtil.getInstance().export(title, rowsName, dataList, path, false);

        return path;

    }

    @Override
    public TeamDailyRes sendTeamDailyInfo(AttendReportReq reqParam)
    {
        TeamDailyRes respBean = new TeamDailyRes();

        UserInfo userInfo = reqParam.getUserInfo();
        // 校验请求参数
        checkQueryTeamDailyParam(reqParam, respBean, userInfo);
        if (!respBean.isSuccess())
        {
            logger
                .info(
                    "sendTeamDailyInfo checkParam failed,phone={}|uid={}|resultCode={}|summary={}|reqParam={}",
                    userInfo.getPhone(), userInfo.getUid(), respBean.getCode(),
                    respBean.getSummary(), reqParam);
            return respBean;
        }
        // 接收人邮件地址校验
        boolean isEmail = MailUtil.getInstance().checkEmail(
            reqParam.getRecvEmail());
        if (!isEmail)
        {
            logger.warn("checkEmail failed.reportReq={}|userInfo={}", reqParam,
                reqParam.getUserInfo());
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return respBean;
        }
        logger.info(
            "sendTeamDailyInfo checkParam success,phone={}|uid={}|reqParam={}",
            userInfo.getPhone(), userInfo.getUid(), reqParam);

        // 实体bean注入
        TeamDailyEntity teamReq = dealSendTeamDailyInfoBeanParam(reqParam);

        List<TeamDailyEntity> list = reportDao.queryTeamDailyInfo(teamReq, userInfo);


        if (AssertUtil.isEmpty(list))
        {
            logger.info(
                "sendTeamDailyInfo data is null,phone={}|uid={}|reqParam={}",
                userInfo.getPhone(), userInfo.getUid(), reqParam);
            respBean.setCode(AtdcResultCode.ATDC108.NO_EXPORT_DATA);
            respBean.setSummary(AtdcResultSummary.ATDC108.NO_EXPORT_DATA);
            return respBean;
        }
        // 生成execl
        String excelPath = exportTeamDailyInfoExecl(reqParam.getUserInfo()
            .getEnterName(), reqParam.getAttendanceDate(), list);

        String subject = "【" + reqParam.getUserInfo().getEnterName()
            + "】 团队打卡明细日报-" + reqParam.getAttendanceDate();
        // 组装并发送邮件
        String content = "您好，您导出的报表见附件。<br /><br />---------<br />和飞信负一屏考勤打卡团队";
        boolean send = MailUtil.getInstance().send(subject, content, reqParam.getRecvEmail(),
                excelPath);
        if (!send){
            respBean.setCode(AtdcResultCode.ATDC108.PARAM_PARSE_FAILED);
            respBean.setSummary(AtdcResultSummary.ATDC108.EXCEL_FAILURE);
        }
        // 发送完成后删除报表文件
        FileUtil.delete(excelPath);

        return respBean;
    }

    /**
     * 查询参数组装：发送团队日报详情（所有员工）
     * @param reqParam
     * @return
     */
    private TeamDailyEntity dealSendTeamDailyInfoBeanParam(
        AttendReportReq reqParam)
    {
        TeamDailyEntity teamReq = new TeamDailyEntity();
        teamReq.setAttendanceDate(reqParam.getAttendanceDate());
        teamReq.setEnterId(reqParam.getEnterId());
        teamReq.setPageNo(-1);
        teamReq.setPageSize(-1);
        return teamReq;

    }

    /**
     * 导出execl：团队打卡明细日报
     * @param enterName
//     * @param attendanceMonth
     * @param list
     * @return
     */
    private String exportTeamDailyInfoExecl(String enterName,
        String attendanceDate, List<TeamDailyEntity> list)
    {
        String title = "【" + enterName + "】 团队打卡明细日报-" + attendanceDate;
        String[] rowsName = new String[] { "部门", "员工","contractId" ,"手机号码","最早打卡时间", "打卡状态描述",
            "打卡地点", "最晚打卡时间", "打卡状态描述", "打卡地点", "是否外勤打卡" };

        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        // 循环组装单元格数据
        for (TeamDailyEntity entity : list)
        {
            objs = new Object[rowsName.length];
            objs[0] = entity.getAttendanceName();
            objs[1] = entity.getEmployeeName();
            objs[2] = (entity.getContactId());
            objs[3] =  entity.getPhone()==null || entity.getPhone().equals("") ? "-" : entity.getPhone();
            objs[4] = (entity.getEarlyTime() == null ? "-" : TimeUtil.date2String(entity.getEarlyTime(), TimeUtil.BASE_TIME_FORMAT));
            objs[5] = (entity.getEarlyTimeDesc() == null? entity.getRemark(): entity.getEarlyTimeDesc());
            objs[6] = (entity.getEarlyTimeLocation() == null ? " " : entity.getEarlyTimeLocation());
            objs[7] = (entity.getLastTime() == null ? "-" : TimeUtil.date2String(entity.getLastTime(), TimeUtil.BASE_TIME_FORMAT));
            objs[8] = (entity.getLastTimeDesc() == null ? entity.getRemark() : entity.getLastTimeDesc());
            objs[9] = (entity.getLastTimeLocation() == null ? " " : entity.getLastTimeLocation());
            objs[10] = (entity.getRegionStatus() == 0 ? "否" : "是");
            dataList.add(objs);
        }

        // 生成文件的路径
        String path = AttendanceConfig.getInstance().getProperty(
            "attend.export.file.dir", "/data/exportTempFile/")
            + title + ".xls";
        // 调用公共组件方法生成execl
        ExeclUtil.getInstance().export(title, rowsName, dataList, path, true);

        return path;

    }

    @Override
    public TeamMonthRes queryTeamMonthPc(AttendReportReq req)
    {
        TeamMonthRes res = new TeamMonthRes();
        checkQueryTeamMonthPcParam(req, res);
        if (!res.isSuccess())
        {
            logger
                .info(
                    "queryTeamMonthPc checkParam failed,phone={}|reqParam={}|code={}|summary={}",
                    req.getUserInfo().getPhone(), req, res.getCode(),
                    res.getSummary());
            return res;
        }
        logger.info("queryTeamMonthPc checkParam success,phone={}|reqParam={}",
            req.getUserInfo().getPhone(), req);
        int totalCount = reportDao.queryTeamMonthPcCount(req);
        if (totalCount <= 0)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return res;
        }
        // 构建分页数据
        buidlPageQuery(req);
        int totalSize = totalCount % req.getPageSize() == 0 ? totalCount
            / req.getPageSize() : totalCount / req.getPageSize() + 1;
        res.setPageNo(req.getPageNo());
        res.setPageSize(req.getPageSize());
        if (req.getPageNo() > totalSize)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return res;
        }
        int offset = (req.getPageNo() - 1) * req.getPageSize();
        req.setOffset(offset);
        List<TeamMonthEntity> list = reportDao.queryTeamMonthPc(req);
        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return res;
        }
        res.setData(list);
        res.setTotalCount(totalCount);
        return res;
    }

    /**
     * PC端查询团队月报统计数据参数校验
     * @param req
     * @param res
     */
    private void checkQueryTeamMonthPcParam(AttendReportReq req,
        TeamMonthRes res)
    {
        if (!validMonth(req))
        {
            // 日期参数错误
            res.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return;
        }
        // 判断是否为管理员身份
        if (req.getUserInfo().getIsAdmin() != 1 && req.getUserInfo().getRoleType() != 1)
        {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }

    }

    /**
     * 校验月份信息
     * @param req
     * @return
     */
    private boolean validMonth(AttendReportReq req) {
        if (AssertUtil.isEmpty(req.getAttendanceMonth())) {
            return false;
        }
        if (!isValidDate(req.getAttendanceMonth(), "yyyy-MM")) {
            // 日期格式不符合条件
            return false;
        }
       /* // 不能超过当前月份
        long reqTime = TimeUtil.string2Date(req.getAttendanceMonth(), "yyyy-MM").getTime();
        String tempStr = TimeUtil.date2String(new Date(), "yyyy-MM");
        long currentTime = TimeUtil.string2Date(tempStr, "yyyy-MM").getTime();
        if (reqTime > currentTime) {
            return false;
        }
        // 不能超过当前年份
        long reqYear = TimeUtil.string2Date(req.getAttendanceMonth(), "yyyy").getTime();
        String temp = TimeUtil.date2String(new Date(), "yyyy");
        long currentYear = TimeUtil.string2Date(temp, "yyyy").getTime();
        if (reqYear > currentYear) {
            return false;
        }*/
        return true;
    }

    /**
     * 校验日期格式
     * @param str
     * @return
     */
    private boolean isValidDate(String str, String formater) {
        if (AssertUtil.isEmpty(str)) {
            return false;
        }
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(formater);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            Date dateStr = format.parse(str);
            return str.equals(format.format(dateStr));
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 获取日期间的月份差
     * @param req
     * @return
     */
    private int getDiffMonth(AttendReportReq req, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        try {
            bef.setTime(sdf.parse(req.getStartDate()));
            aft.setTime(sdf.parse(req.getEndDate()));
        } catch (ParseException e) {

        }
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH) + 1;
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        return result + month;
    }

    /**
     * 比较日期大小，前面大为1，前面小为-1，相等为0
     * @param DATE1
     * @param DATE2
     * @return
     */
    private int compare_date(String DATE1, String DATE2, String fromat) {
        DateFormat df = new SimpleDateFormat(fromat);
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {

        }
        return 0;
    }

    @Override
    public AttendReportRes queryEmpMonthPc(AttendReportReq req)
    {
        AttendReportRes res = new AttendReportRes();
        checkQueryEmpMonthPc(req, res);
        if (!res.isSuccess()) {
            logger.info("queryEmpMonthPc checkParam failed,phone={}|uid={}|reqParam={}|code={}|summary={}",
                    req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                    req, res.getCode(), res.getSummary());
            return res;
        }
        logger.info("queryEmpMonthPc checkParam success,phone={}|uid={}|reqParam={}",
            req.getUserInfo().getPhone(), req.getUserInfo().getUid(), req);
        // 查询满足条件的消息总量
        int totalCount = reportDao.queryEmpMonthPcCount(req);
        if (totalCount <= 0) {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return res;
        }
        // 构建分页数据
        buidlPageQuery(req);
        int totalSize = totalCount % req.getPageSize() == 0 ? totalCount
            / req.getPageSize() : totalCount / req.getPageSize() + 1;
        res.setPageNo(req.getPageNo());
        res.setPageSize(req.getPageSize());
        if (req.getPageNo() > totalSize) {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return res;
        }
        int offset = (req.getPageNo() - 1) * req.getPageSize();
        req.setOffset(offset);
        // 如果有数据则分页查询具体数据
        List<EmployeeMonthDetail> list = reportDao.queryEmpMonthPc(req);
        if (AssertUtil.isEmpty(list)) {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return res;
        }
        res.setEmployeeMonth(list);
        res.setTotalCount(totalCount);
        return res;
    }

    private void buidlPageQuery(AttendReportReq req)
    {
        if (-1 == req.getPageNo())
        {
            req.setPageNo(1);
        }
        if (-1 == req.getPageSize() || req.getPageSize() > 100)
        {
            req.setPageSize(20);
        }

    }

    /**
     * PC端查询员工月报明细参数校验
     * @param req
     * @param res
     */
    private void checkQueryEmpMonthPc(AttendReportReq req, AttendReportRes res)
    {
        if (!validReqDate(req))
        {
            // 日期参数校验失败
            res.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return;
        }
        // 判断是否为管理员身份
        if (req.getUserInfo().getIsAdmin() != 1)
        {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }

    }

    /**
     * 校验PC端查询员工月报明细日期请求参数
     * @param req
     * @return
     */
    private boolean validReqDate(AttendReportReq req)
    {
        if (AssertUtil.isEmpty(req.getStartDate())
            || AssertUtil.isEmpty(req.getEndDate()))
        {
            return false;
        }
        if (!isValidDate(req.getStartDate(), "yyyy-MM-dd")
            || !isValidDate(req.getEndDate(), "yyyy-MM-dd"))
        {
            // 日期格式不符合条件
            return false;
        }
        if (compare_date(req.getStartDate(), req.getEndDate(), "yyyy-MM-dd") > 0)
        {
            // 开始日期不能大于截止日期
            return false;
        }
        String currentTimeStr = TimeUtil.getCurrentDate();
        if (compare_date(req.getStartDate(), currentTimeStr, "yyyy-MM-dd") > 0)
        {
            // 开始日期不能大于或等于当前日期
            return false;
        }
        if (compare_date(req.getEndDate(), currentTimeStr, "yyyy-MM-dd") > 0)
        {
            // 截止日期不能大于或等于当前日期
            return false;
        }
        int spaceMonth = getDiffMonth(req, "yyyy-MM-dd");
        if (spaceMonth > 3)
        {
            // 开始与截止日期跨度不超过3个月
//            return false;
        }
        return true;
    }

    @Override
    public AttendExportReptRes exportTeamMonthInfoPc(AttendReportReq req)
    {
        AttendExportReptRes res = new AttendExportReptRes();
        checkExportTeamMonthInfoPc(req, res);
        if (!res.isSuccess())
        {
            logger
                .info(
                    "exportTeamMonthInfoPc checkParam failed,phone={}|req={}|code={}|summary={}",
                    req.getUserInfo().getPhone(), req, res.getCode(),
                    res.getSummary());
            return res;
        }
        logger.info(
            "exportTeamMonthInfoPc checkParam success,phone={}|uid={}|req={}",
            req.getUserInfo().getPhone(), req.getUserInfo().getUid(), req);
        int totalCount = reportDao.queryExportTeamMonthPcCount(req);
        if (totalCount == 0)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            return res;
        }
        if (totalCount > config.getMaxExportCount())
        {
            res.setCode(AtdcResultCode.ATDC108.SO_MANY_EXPORT_DATA);
            res.setSummary(AtdcResultSummary.ATDC108.SO_MANY_EXPORT_DATA);
            return res;
        }
        // TODO 目前数据量较小，直接查整个企业的数据导出即可，后续量增多时可调整成分页查询
        List<TeamMonthEntity> list = reportDao.queryExportTeamMonthPc(req);

        if (AssertUtil.isEmpty(list)) {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            return res;
        }
        buildTeamMonthPcExcel(req, list, res);
        return res;
    }

    /**
     * 生成PC端团队月报Excel数据
     * @param req
     * @param list
     * @return
     */
    private void buildTeamMonthPcExcel(AttendReportReq req, List<TeamMonthEntity> list, AttendExportReptRes res) {
        String title = "【" + req.getUserInfo().getEnterName() + "】考勤统计 统计时间 " + req.getAttendanceMonth();
        String[] rowsName;
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;

        if (TimeUtil.convert2long(req.getAttendanceMonth(),TimeUtil.BASE_DATE_FORMAT_YYYY_MM) <
            TimeUtil.convert2long("2018-11",TimeUtil.BASE_DATE_FORMAT_YYYY_MM)){
             rowsName = new String[] { "名称", "contactId","手机号码","部门","考勤组", "工作总时长(分钟)", "正常",
                "外勤", "迟到", "早退", "未打卡", "已申诉" };
            for (TeamMonthEntity entity : list) {
                objs = new Object[rowsName.length];
                objs[0] = entity.getEmployeeName();
                objs[1] = entity.getContactId();
                objs[2] = entity.getPhone()==null || entity.getPhone().equals("")  ? "-" : entity.getPhone();
                objs[3] = entity.getDeptName() == null ? " " : entity.getDeptName();
                objs[4] = entity.getAttendanceName();
                objs[5] = entity.getTotalWorkTime();
                objs[6] = entity.getNormalDays();
                objs[7] = entity.getOutsideDays();
                objs[8] = entity.getLateDays();
                objs[9] = entity.getEarlyDays();
                objs[10] = entity.getNotClockedDays();
                objs[11] = entity.getAppealDays();
                dataList.add(objs);
            }
        }else {
            rowsName = new String[] { "名称", "contactId","手机号码", "部门","考勤组", "工作总时长(分钟)", "正常",
                "外勤", "迟到","迟到时长(分钟)", "早退","早退时长(分钟)", "上班未打卡", "下班未打卡", "已申诉" };
            // 循环组装单元格数据
            for (TeamMonthEntity entity : list) {
                objs = new Object[rowsName.length];
                objs[0] = entity.getEmployeeName();
                objs[1] = entity.getContactId();
                objs[2] = entity.getPhone()==null || entity.getPhone().equals("") ?"-":entity.getPhone();
                objs[3] = entity.getDeptName() == null ? " " : entity.getDeptName();
                objs[4] = entity.getAttendanceName();
                objs[5] = entity.getTotalWorkTime();
                objs[6] = entity.getNormalDays();
                objs[7] = entity.getOutsideDays();
                objs[8] = entity.getLateDays();
                objs[9] = entity.getLateMinutes();
                objs[10] = entity.getEarlyDays();
                objs[11] = entity.getEarlyMinutes();
                objs[12] = entity.getGoNotClockedDays();
                objs[13] = entity.getLeaveNotClockedDays();
                objs[14] = entity.getAppealDays();
                dataList.add(objs);
            }
        }

        // 生成文件的路径目录
        String baseDir = config.getDownLoadTempBaseDir();
        // 由于此时生成的文件是临时的，为防止调用了接口生成了文件但不进行下载触发不了删除，导致文件堆积，按时间日期进行隔层，采用用定时任务进行删除，
        // 生成的目录如:20180211/10/,比如现在是10点，定时任务将删除8点和之前的所有数据(即20180211/08/和20180211/07/目录下的都会被删除)
        String dailyDate = TimeUtil.getCurrentDateTime("yyyyMMdd");
        String currentHour = TimeUtil.getCurrentHour();
        String fileTempDir = baseDir + dailyDate + File.separator + currentHour;
        // 创建临时文件生成的目录
        FileUtil.createDir(fileTempDir);
        // 为防止生成的文件名重复,对title进行拼接uuid处理，下载时再裁剪文件名
        String randomId = UUID.randomUUID().toString();
        String title2 = randomId
            + AtdcConsts.ATTEND_BIZ.DOWNLOAD_FILENAME_SEPARATOR + title;
        String path = fileTempDir + File.separator + title2 + ".xls";
        // 调用公共组件方法生成execl
        ExeclUtil.getInstance().export(title, rowsName, dataList, path, false);
        // 返回内容均采用base64加密
        String base64Dir = Base64Coder.encodeString(dailyDate + File.separator
            + currentHour);
        String base64Title = Base64Coder.encodeString(title2 + ".xls");
        String serverUrl = config.getDownLoadServerUrl();
        String url = serverUrl + "contendDirId=" + base64Dir + "&fileName="
            + base64Title;
        res.setUrl(url);
    }

    /**
     * PC端团队月报导出参数校验
     * @param req
     * @param res
     */
    private void checkExportTeamMonthInfoPc(AttendReportReq req, ResBean res)
    {
        if (!validMonth(req))
        {
            // 日期格式错误或为空
            res.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return;
        }
        // 判断是否为管理员身份
        if (req.getUserInfo().getIsAdmin() != 1)
        {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }

    }

    @Override
    public AttendExportReptRes exportEmpMonthPc(AttendReportReq req)
    {
        AttendExportReptRes res = new AttendExportReptRes();
        checkExportEmpMonthPc(req, res);
        if (!res.isSuccess())
        {
            logger
                .info(
                    "exportEmpMonthPc checkParam failed,phone={}|req={}|code={}|summary={}",
                    req.getUserInfo().getPhone(), req, res.getCode(),
                    res.getSummary());
            return res;
        }
        logger.info(
            "exportEmpMonthPc checkParam success,phone={}|uid={}|req={}", req
                .getUserInfo().getPhone(), req.getUserInfo().getUid(), req);
        int totalCount = reportDao.queryExportEmpMonthPcCount(req);
        if (totalCount == 0)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            return res;
        }
        if (totalCount > config.getMaxExportCount())
        {
            res.setCode(AtdcResultCode.ATDC108.SO_MANY_EXPORT_DATA);
            res.setSummary(AtdcResultSummary.ATDC108.SO_MANY_EXPORT_DATA);
            return res;
        }
        // TODO 目前数据量较小，直接查整个企业的数据导出即可，后续量增多时可调整成分页查询
        List<EmployeeMonthDetail> list = reportDao.queryExportEmpMonthPc(req);


        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            return res;
        }
        buildEmpMonthPcExcel(req, list, res);
        return res;
    }

    private void buildEmpMonthPcExcel(AttendReportReq req,
        List<EmployeeMonthDetail> list, AttendExportReptRes res)
    {
        String title = "【" + req.getUserInfo().getEnterName() + "】考勤明细 统计时间 "
            + req.getStartDate() + "至" + req.getEndDate();
        String[] rowsName = new String[] { "名称", "contactId","手机号码","日期","部门", "考勤组", "最早打卡时间", "打卡状态描述",
            "打卡地点", "最晚打卡时间", "打卡状态描述", "打卡地点", "工作时长(分钟)" ,"是否外勤"};

        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        // 循环组装单元格数据
        for (EmployeeMonthDetail entity : list)
        {
            objs = new Object[rowsName.length];
            objs[0] = entity.getEmployeeName();
            objs[1] = entity.getContactId();
            objs[2] = entity.getPhone()==null || entity.getPhone().equals("") ? "-" : entity.getPhone();
            objs[3] = TimeUtil.date2String(entity.getAttendanceDate(), "yyyy-MM-dd");
            objs[4] = entity.getDeptName() == null ? " " : entity.getDeptName();
            objs[5] = entity.getAttendanceName();
            objs[6] = entity.getGoWorkTime() == null ? " " : entity
                .getGoWorkTime();
            objs[7] = entity.getGoWorkDesc() == null ? entity.getRemark() : entity
                .getGoWorkDesc();
            objs[8] = entity.getGoLocation() == null ? " " : entity
                .getGoLocation();
            objs[9] = entity.getLeaveWorkTime() == null ? " " : entity
                .getLeaveWorkTime();
            objs[10] = entity.getLeaveWorkDesc() == null ? entity.getRemark() : entity
                .getLeaveWorkDesc();
            objs[11] = entity.getLeaveLocation() == null ? " " : entity
                .getLeaveLocation();
            objs[12] = entity.getWorkMinutes();
            objs[13] = entity.getRegionStatus() == 0 ? "否" : "是";
            dataList.add(objs);
        }

        // 生成文件的路径目录
        String baseDir = config.getDownLoadTempBaseDir();
        // 由于此时生成的文件是临时的，为防止调用了接口生成了文件但不进行下载触发不了删除，导致文件堆积，按时间日期进行隔层，采用用定时任务进行删除，
        // 生成的目录如:20180211/10/,比如现在是10点，定时任务将删除8点和之前的所有数据(即20180211/08/和20180211/07/目录下的都会被删除)
        String dailyDate = TimeUtil.getCurrentDateTime("yyyyMMdd");
        String currentHour = TimeUtil.getCurrentHour();
        String fileTempDir = baseDir + dailyDate + File.separator + currentHour;
        // 创建临时文件生成的目录
        FileUtil.createDir(fileTempDir);
        // 为防止生成的文件名重复,对title进行拼接uuid处理，下载时再裁剪文件名
        String randomId = UUID.randomUUID().toString();
        String title2 = randomId
            + AtdcConsts.ATTEND_BIZ.DOWNLOAD_FILENAME_SEPARATOR + title;
        String path = fileTempDir + File.separator + title2 + ".xls";
        // 调用公共组件方法生成execl
        ExeclUtil.getInstance().export(title, rowsName, dataList, path, false);
        // 返回内容均采用base64加密
        String base64Dir = Base64Coder.encodeString(dailyDate + File.separator
            + currentHour);
        String base64Title = Base64Coder.encodeString(title2 + ".xls");
        String serverUrl = config.getDownLoadServerUrl();
        String url = serverUrl + "contendDirId=" + base64Dir + "&fileName="
            + base64Title;
        res.setUrl(url);
    }

    /**
     * PC端导出员工月报明细数据参数校验
     * @param req
     * @param res
     */
    private void checkExportEmpMonthPc(AttendReportReq req, ResBean res)
    {
        if (!validReqDate(req))
        {
            // 日期参数校验失败
            res.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return;
        }
        // 判断是否为管理员身份
        if (req.getUserInfo().getIsAdmin() != 1)
        {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }

    }

    @Override
    public AttendReptItemRes queryTeamDailyItem(AttendReportReq req) {
        AttendReptItemRes res = new AttendReptItemRes();
        checkQueryTeamDailyItem(req, res);
        if (!res.isSuccess()) {
            logger.info("queryTeamDailyItem checkParam failed,phone={}|uid={}|enterId={}|code={}|summary={}|req={}",
                    req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                    req.getEnterId(), res.getCode(), res.getSummary(), req);
            return res;
        }
        logger.info("queryTeamDailyItem checkParam success,phone={}|uid={}|enterId={}|itemId={}|reqDate={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                req.getEnterId(), req.getItemId(), req.getAttendanceDate());
        if (1 == req.getItemId()) {
            // 迟到
            handleLateItem(req, res);
        }
        else if (2 == req.getItemId()) {
            // 早退
            handleEarlyItem(req, res);
        }
        else if (3 == req.getItemId()) {
            // 未打卡
            handleNotClockedItem(req, res);
        }

        res.setIsNewData(0);
        return res;
    }

    /**
     * 处理未打卡数据
     * @param req
     * @param res
     */
    private void handleNotClockedItem(AttendReportReq req, AttendReptItemRes res)
    {
        int totalCount = reportDao.queryNotClockedItemCount(req);
        if (totalCount <= 0)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        // 构建分页数据
        buidlPageQuery(req);
        int totalSize = totalCount % req.getPageSize() == 0 ? totalCount
            / req.getPageSize() : totalCount / req.getPageSize() + 1;
        res.setPageNo(req.getPageNo());
        res.setPageSize(req.getPageSize());
        if (req.getPageNo() > totalSize)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        int offset = (req.getPageNo() - 1) * req.getPageSize();
        req.setOffset(offset);
        List<EmployeeMonthDetail> list = reportDao.queryNotClockedItem(req);
        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        res.setData(list);
        res.setTotalCount(totalCount);
    }

    /**
     * 处理早退数据
     * @param req
     * @param res
     */
    private void handleEarlyItem(AttendReportReq req, AttendReptItemRes res)
    {
        int totalCount = reportDao.queryEarlyItemCount(req);
        if (totalCount <= 0)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        // 构建分页数据
        buidlPageQuery(req);
        int totalSize = totalCount % req.getPageSize() == 0 ? totalCount
            / req.getPageSize() : totalCount / req.getPageSize() + 1;
        res.setPageNo(req.getPageNo());
        res.setPageSize(req.getPageSize());
        if (req.getPageNo() > totalSize)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        int offset = (req.getPageNo() - 1) * req.getPageSize();
        req.setOffset(offset);
        List<EmployeeMonthDetail> list = reportDao.queryEarlyItem(req);
        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        res.setData(list);
        res.setTotalCount(totalCount);
    }

    /**
     * 处理迟到数据
     * @param req
     * @param res
     */
    private void handleLateItem(AttendReportReq req, AttendReptItemRes res)
    {
        int totalCount = reportDao.queryLateItemCount(req);
        if (totalCount <= 0)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        // 构建分页数据
        buidlPageQuery(req);
        int totalSize = totalCount % req.getPageSize() == 0 ? totalCount
            / req.getPageSize() : totalCount / req.getPageSize() + 1;
        res.setPageNo(req.getPageNo());
        res.setPageSize(req.getPageSize());
        if (req.getPageNo() > totalSize)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        int offset = (req.getPageNo() - 1) * req.getPageSize();
        req.setOffset(offset);
        List<EmployeeMonthDetail> list = reportDao.queryLateItem(req);
        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            res.setTotalCount(0);
            return;
        }
        res.setData(list);
        res.setTotalCount(totalCount);
    }

    /**
     * 报表分项统计参数校验
     * @param req
     * @param res
     */
    private void checkQueryTeamDailyItem(AttendReportReq req,
        AttendReptItemRes res) {
        if (!validDate(req)) {
            // 日期参数校验失败
            res.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return;
        }
        // 判断是否为管理员身份
        if (req.getUserInfo().getIsAdmin() != 1) {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }

//        if (1 != req.getItemId() && 2 != req.getItemId()
//            && 3 != req.getItemId())
//        {
//            res.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
//            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
//            return;
//        }
    }

    /**
     * 校验日期信息
     * @param req
     * @return
     */
    private boolean validDate(AttendReportReq req) {
        if (AssertUtil.isEmpty(req.getAttendanceDate())) {
            return false;
        }
        if (!isValidDate(req.getAttendanceDate(), "yyyy-MM-dd")) {
            // 日期格式不符合条件
            return false;
        }
        // 不能超过当前日期
       /* long reqTime = TimeUtil.string2Date(req.getAttendanceDate(),
            "yyyy-MM-dd").getTime();
        String tempStr = TimeUtil.date2String(new Date(), "yyyy-MM-dd");
        long currentTime = TimeUtil.string2Date(tempStr, "yyyy-MM-dd").getTime();
        if (reqTime > currentTime) {
            return false;
        }*/
        // 不能超过当前年份
        /*long reqYear = TimeUtil
            .string2Date(req.getAttendanceDate(), "yyyy").getTime();
        String temp = TimeUtil.date2String(new Date(), "yyyy");
        long currentYear = TimeUtil.string2Date(temp, "yyyy").getTime();
        if (reqYear > currentYear) {
            return false;
        }*/
        return true;
    }

    @Override
    public AttendReptEmpMonthRes queryEmpMonthList(AttendReportReq req)
    {
        AttendReptEmpMonthRes res = new AttendReptEmpMonthRes();
        checkQueryEmpMonthListPc(req, res);
        if (!res.isSuccess())
        {
            logger
                .info(
                    "queryEmpMonthList checkParam failed,phone={}|uid={}|enterId={}|code={}|summary={}|req={}",
                    req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                    req.getEnterId(), res.getCode(), res.getSummary(), req);
            return res;
        }
        logger
            .info(
                "queryEmpMonthList checkParam success,phone={}|uid={}|enterId={}|reqUid={}|reqDate={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                req.getEnterId(), req.getUid(), req.getAttendanceMonth());

        // 查询当前用户目前的考勤组信息
        UserGroupEntity userGroup = null;
        try
        {
            userGroup = employeeDao.queryOwnGroup(req.getUid());
        }
        catch (Exception e)
        {
            logger.error("queryEmpMonthList queryOwnGroup failed,uid={}",
                req.getUid(), e);
            res.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            res.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return res;
        }

        List<TeamMonthEntity> list = reportDao.queryEmpMonthList(req);
        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            return res;
        }
        TeamMonthEntity data = buildDataBean(list);
        if (data.getLateDays() > 0)
        {
            // 查询迟到详细信息
            handleLateDetailInfo(req, res);
        }
        else
        {
            res.setLateCount(0);
            res.setLateList(null);
            res.setLateTotalMinutes(0);
        }

        if (data.getEarlyDays() > 0)
        {
            // 查询早退详细信息
            handleEarlyDetailInfo(req, res);
        }
        else
        {
            res.setEarlyCount(0);
            res.setEarlyList(null);
            res.setEarlyTotalMinutes(0);
        }

        if (data.getNotClockedDays() > 0)
        {
            // 查询未打卡详细信息
            handleNotClockedDetailInfo(req, res);
        }
        else
        {
            res.setNotClockedCount(0);
            res.setNotClockedList(null);
        }
        res.setOutSideCount(data.getOutsideDays());
        res.setNormalCount(data.getNormalDays());
        res.setAttendanceName(userGroup.getAttendanceName());
        return res;
    }

    /**
     * 由于团队月报中存在用户在企业中换考勤组的情况，即查询团队月报数据时会出现同一个用户一个月中出现多条数据情况
     * @param list
     * @return
     */
    private TeamMonthEntity buildDataBean(List<TeamMonthEntity> list)
    {
        TeamMonthEntity data = new TeamMonthEntity();
        int normalDays = 0;
        int outsideDays = 0;
        int lateDays = 0;
        int earlyDays = 0;
        int notClockedDays = 0;
        for (TeamMonthEntity t : list)
        {
            normalDays = normalDays + t.getNormalDays();
            outsideDays = outsideDays + t.getOutsideDays();
            lateDays = lateDays + t.getLateDays();
            earlyDays = earlyDays + t.getEarlyDays();
            notClockedDays = notClockedDays + t.getNotClockedDays();
        }
        data.setNormalDays(normalDays);
        data.setOutsideDays(outsideDays);
        data.setLateDays(lateDays);
        data.setEarlyDays(earlyDays);
        data.setNotClockedDays(notClockedDays);
        return data;
    }

    /**
     * 查询未打卡详细信息
     * @param req
     * @param res
     */
    private void handleNotClockedDetailInfo(AttendReportReq req,
        AttendReptEmpMonthRes res)
    {
        List<EmployeeMonthDetail> list = reportDao
            .queryNotClockedDetailInfo(req);
        if (AssertUtil.isEmpty(list))
        {
            res.setNotClockedCount(0);
            res.setNotClockedList(null);
            return;
        }
        int notClockedCount = 0;
        for (EmployeeMonthDetail e : list)
        {
            if ("未打卡".equals(e.getGoWorkDesc())
                && "未打卡".equals(e.getLeaveWorkDesc()))
            {
                notClockedCount = notClockedCount + 2;
            }
            else if ("未打卡".equals(e.getGoWorkDesc()))
            {
                notClockedCount = notClockedCount + 1;
            }
            else if ("未打卡".equals(e.getLeaveWorkDesc()))
            {
                notClockedCount = notClockedCount + 1;
            }
        }
        res.setNotClockedCount(notClockedCount);
        res.setNotClockedList(list);
    }

    /**
     * 查询早退详细信息
     * @param req
     * @param res
     */
    private void handleEarlyDetailInfo(AttendReportReq req,
        AttendReptEmpMonthRes res)
    {
        List<EmployeeMonthDetail> list = reportDao.queryEarlyDetailInfo(req);
        if (AssertUtil.isEmpty(list))
        {
            res.setEarlyCount(0);
            res.setEarlyList(null);
            res.setEarlyTotalMinutes(0);
            return;
        }
        int totalEarlyMinutes = 0;
        for (EmployeeMonthDetail e : list)
        {
            totalEarlyMinutes = totalEarlyMinutes + e.getEarlyMinutes();
        }
        res.setEarlyCount(list.size());
        res.setEarlyList(list);
        res.setEarlyTotalMinutes(totalEarlyMinutes);
    }

    /**
     * 查询迟到详细信息
     * @param req
     * @param res
     */
    private void handleLateDetailInfo(AttendReportReq req,
        AttendReptEmpMonthRes res)
    {
        List<EmployeeMonthDetail> list = reportDao.queryLateDetailInfo(req);
        if (AssertUtil.isEmpty(list))
        {
            res.setLateCount(0);
            res.setLateList(null);
            res.setLateTotalMinutes(0);
            return;
        }
        int totalLateMinutes = 0;
        for (EmployeeMonthDetail e : list)
        {
            totalLateMinutes = totalLateMinutes + e.getLateMinutes();
        }
        res.setLateTotalMinutes(totalLateMinutes);
        res.setLateCount(list.size());
        res.setLateList(list);

    }

    /**
     * 团队日报个人月列表考勤数据统计参数校验
     * @param req
     * @param res
     */
    private void checkQueryEmpMonthListPc(AttendReportReq req,
        AttendReptEmpMonthRes res)
    {
        if (AssertUtil.isEmpty(req.getUid()))
        {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }
        if (!validMonth(req))
        {
            // 日期参数错误
            res.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return;
        }
        // 判断是否为管理员身份
        if (req.getUserInfo().getIsAdmin() != 1)
        {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }
    }

    @Override
    public AttendReportRes queryEmpMonthStatistics(AttendReportReq reportReq) {
        // 参数校验
        AttendReportRes reportRes = checkMonthReportReq(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkQueryEmpMonthStatisticsReq failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 权限校验，支持员工查询本人，管理员查询其他人
        reportRes = checkAuthorityOfPMR(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkAuthorityOfPMR failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 查询员工个人月报
        List<EmployeeMonthDetailVO> employeeMonth = reportDao.queryEmpMonthStatistics(
            reportReq.getUid(), reportReq.getAttendanceMonth(), reportReq.getEnterId());

        // 返回查询结果，即使结果为空，仍然返回成功
        reportRes.setEmployeeMonthDetailVO(employeeMonth);

        return reportRes;
    }

    /**
     * 校验月报的请求参数
     * @param reportReq
     * @return
     */
    private AttendReportRes checkMonthReportReq(AttendReportReq reportReq) {
        AttendReportRes reportRes = new AttendReportRes();

        // 用户参数非空校验
        if (AssertUtil.isEmpty(reportReq.getEnterId())
                && AssertUtil.isEmpty(reportReq.getUid())) {
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return reportRes;
        }
        // 查询月份校验
        if (!validMonth(reportReq)) {
            // 日期参数校验失败
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return reportRes;
        }

        return reportRes;
    }

    @Override
    public AttendReportRes queryEmpMonthDetail(AttendReportReq reportReq) {
        // 参数校验
        AttendReportRes reportRes = checkMonthReportReq(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkPersonalMonthlyReportReq failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 权限校验，支持员工查询本人，管理员查询其他人
        reportRes = checkAuthorityOfPMR(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkAuthorityOfPMR failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            return reportRes;
        }

        // 查询员工个人月报
        List<EmployeeMonthDetailVO> employeeMonth = reportDao.queryEmpMonthDetail(reportReq.getUid(), reportReq.getAttendanceMonth(), reportReq.getEnterId());
        //查询企业是否开启
        try {
            AttendApprovalRestrict attendApprovalRestrict = attendAppealDao.queryApprovalRestrictByEnterId(reportReq.getUserInfo().getEnterId());
            //说明没有开启
            if (attendApprovalRestrict == null || attendApprovalRestrict.getRestrictStatus() == 0){
                reportRes.setRestrictStatus(0);
                //设置了上限
            }else {
                //查询月份的提交审批数
               int approvalCount = attendAppealDao.queryApprovalCount(reportReq.getUid(), reportReq.getAttendanceMonth(), reportReq.getEnterId());
                reportRes.setRestrictStatus(approvalCount >= attendApprovalRestrict.getRestrictNumber() ? 1 : 0);
            }

        }catch (Exception e){
            e.printStackTrace();
            UserInfo userInfo = reportReq.getUserInfo();
            logger.error("queryApprovalRestrictByEnterId  error uid={}|enterId={}|errorMsg={} ",userInfo.getUid(),userInfo.getEnterId(),e);
            reportRes.setCode(AtdcResultCode.ATDC107.BASE);
            reportRes.setSummary(AtdcResultSummary.ATDC107.S_ERROR);
        }

        // 返回查询结果，即使结果为空，仍然返回成功
        reportRes.setEmployeeMonthDetailVO(employeeMonth);

        return reportRes;
    }

    @Override
    public TeamMonthRes queryTeamMonthStatistics(AttendReportReq reqParam) {
        // 参数校验
        TeamMonthRes res = checkQueryTeamMonthReq(reqParam);
        if (!res.isSuccess()) {
            logger.warn("checkPersonalMonthlyReportReq failed.reportReq={}|userInfo={}", reqParam, reqParam.getUserInfo());
            return res;
        }

        // 管理员权限检测
        if (reqParam.getUserInfo().getIsAdmin() != 1 && reqParam.getUserInfo().getRoleType() != 1) {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return res;
        }
        logger.info("queryTeamMonthStatistics enterId={}|month={}|userInfo={}", reqParam.getEnterId(), reqParam.getAttendanceMonth(), reqParam.getUserInfo());

        List<TeamMonthEntity> list = new ArrayList<>();
        try {
            list = reportDao.queryTeamMonthStatistics(reqParam);
            logger.info("queryTeamMonthStatistics enterId={}|month={}|phone={}",
                    reqParam.getEnterId(), reqParam.getAttendanceMonth(),  reqParam.getUserInfo().getPhone());
        } catch (Exception e) {
            logger.error("queryTeamMonthStatistics failed,enterId={}|month={}",
                    reqParam.getEnterId(), reqParam.getAttendanceMonth(), e);
        }
        res.setData(list);
        logger.info("data={}",list);
        return  res;
    }

    @Override
    public AttendReportRes queryTeamMonthDetails(AttendReportReq reportReq) {
        // 参数校验
        AttendReportRes reportRes = checkQueryTeamMonthDetailsReq(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkPersonalMonthlyReportReq failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 权限校验，支持员工查询本人，管理员查询其他人
//        reportRes = checkAuthorityOfPMR(reportReq);
//        if (!reportRes.isSuccess()) {
//            logger.warn("checkAuthorityOfPMR failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
//            return reportRes;
//        }
        List<EmployeeMonthDetailVO> employeeMonthDetailVOList = reportDao.queryTeamMonthDetails(
            reportReq.getAttendanceMonth(),reportReq.getEnterId(),reportReq.getItemId());
        // 返回查询结果，即使结果为空，仍然返回成功
        reportRes.setEmployeeMonthDetailVO(employeeMonthDetailVOList);
        reportRes.setItemId(reportReq.getItemId());

        return reportRes;
    }

    @Override
    public AttendReportRes queryTeamDailyStatistics(AttendReportReq reportReq) {
        // 参数校验
        AttendReportRes reportRes = checkDailyReportReq(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkTeamDailyReportReq failed.reportReq={}|userInfo={}",
                reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 权限校验
        reportRes = checkAuthorityOfTDR(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkAuthorityOfTDR failed.reportReq={}|userInfo={}",
                reportReq, reportReq.getUserInfo());
            return reportRes;
        }

        // 统计企业ID的团队日报
        TeamDailyReportEntity teamDailyReport = reportDao.queryTeamDailyStatistics(reportReq.getEnterId(), reportReq.getAttendanceDate());

        logger.debug("sum result.reportReq={}|teamDailyReport={}", reportReq, teamDailyReport);

        reportRes.setTeamDailyReport(teamDailyReport);

        return reportRes;
    }

    @Override
    public AttendReportRes queryTeamDailyDetail(AttendReportReq reportReq) {
        return null;
    }

    @Override
    public AttendReportRes queryTeamDailyItemDetails(AttendReportReq reportReq) {
        // 参数校验
        AttendReportRes reportRes = checkDailyReportReq(reportReq);
        if (!reportRes.isSuccess()) {
            logger.warn("checkQueryTeamDailyItemDetailsReq failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
            return reportRes;
        }
        // 权限校验，支持员工查询本人，管理员查询其他人
//        reportRes = checkAuthorityOfPMR(reportReq);
//        if (!reportRes.isSuccess()) {
//            logger.warn("checkAuthorityOfPMR failed.reportReq={}|userInfo={}", reportReq, reportReq.getUserInfo());
//            return reportRes;
//        }
        List<EmployeeMonthDetail> employeeMonth = reportDao.queryTeamDailyItemDetails(
            reportReq.getAttendanceDate(),reportReq.getEnterId(),reportReq.getItemId());

        reportRes.setEmployeeMonth(employeeMonth);

        reportRes.setIsNewData(1);

        return reportRes;
    }

    /**
     * 校验查询团队月报统计
     * @param reportReq
     * @return
     */
    private TeamMonthRes checkQueryTeamMonthReq(AttendReportReq reportReq) {
        TeamMonthRes reportRes = new TeamMonthRes();

        // 用户参数非空校验
        if (AssertUtil.isEmpty(reportReq.getEnterId())) {
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return reportRes;
        }
        // 查询月份校验
        if (!validMonth(reportReq)) {
            // 日期参数校验失败
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return reportRes;
        }
        // 判断是否为管理员身份
        if (reportReq.getUserInfo().getIsAdmin() != 1 && reportReq.getUserInfo().getRoleType() != 1) {
            reportRes.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            reportRes.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return reportRes;
        }

        return reportRes;
    }

    /**
     * 校验查询团队月报分项详情参数
     * @param reportReq
     * @return
     */
    private AttendReportRes checkQueryTeamMonthDetailsReq(AttendReportReq reportReq) {
        AttendReportRes reportRes = new AttendReportRes();

        // 用户参数非空校验
        if (AssertUtil.isEmpty(reportReq.getEnterId())) {
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return reportRes;
        }
        // 查询月份校验
        if (!validMonth(reportReq)) {
            // 日期参数校验失败
            reportRes.setCode(AtdcResultCode.ATDC104.PARAMS_DATEFORMAT_ERROR);
            reportRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_DATEFORMAT_ERROR);
            return reportRes;
        }
        // 判断是否为管理员身份
        if (reportReq.getUserInfo().getIsAdmin() != 1 && reportReq.getUserInfo().getRoleType() != 1) {
            reportRes.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            reportRes.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return reportRes;
        }

        return reportRes;
    }

    /**
     * pc 查询考勤原始数据
     * @param reportReq
     * @return
     */
    @Override
    public AttendRes queryOriginalClockDataPc(AttendReportReq reportReq) {
        AttendRes attendRes = checkQueryOriginalParam(reportReq);
        if (!attendRes.isSuccess()) {
            logger.warn("checkAuthorityOfPMR failed.reportReq={}|userInfo={}",
                reportReq, reportReq.getUserInfo());
            return attendRes;
        }
        // 查询满足条件的消息总量
        int totalCount = reportDao.queryOriginalClockDataPcCount(reportReq);
        if (totalCount <= 0) {
            attendRes.setCode(AtdcResultCode.S_OK);
            attendRes.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            attendRes.setTotalCount(0);
            return attendRes;
        }
        // 构建分页数据
        buidlPageQuery(reportReq);
        int totalSize = totalCount % reportReq.getPageSize() == 0 ? totalCount
            / reportReq.getPageSize() : totalCount / reportReq.getPageSize() + 1;
        reportReq.setPageNo(reportReq.getPageNo());
        reportReq.setPageSize(reportReq.getPageSize());
        if (reportReq.getPageNo() > totalSize) {
            attendRes.setCode(AtdcResultCode.S_OK);
            attendRes.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            attendRes.setTotalCount(0);
            return attendRes;
        }
        int offset = (reportReq.getPageNo() - 1) * reportReq.getPageSize();
        reportReq.setOffset(offset);
        // 如果有数据则分页查询具体数据
        List<AttendEntity> list = reportDao.queryOriginalClockDataPc(reportReq);
        if (AssertUtil.isEmpty(list)) {
            attendRes.setCode(AtdcResultCode.S_OK);
            attendRes.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            attendRes.setTotalCount(0);
            return attendRes;
        }
        attendRes.setAttendRecord(list);
        attendRes.setTotalCount(totalCount);
        return attendRes;

    }

    /**
     * 校验参数与身份校验
     * @param reportReq
     * @return
     */
    private AttendRes checkQueryOriginalParam(AttendReportReq reportReq) {
        AttendRes attendRes = new AttendRes();
        UserInfo userInfo = reportReq.getUserInfo();
        if (userInfo.getIsAdmin() != 1){
            attendRes.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            attendRes.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return attendRes;
        }
        if (reportReq.getEnterId().isEmpty() ){
            attendRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            attendRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return attendRes;
        }
        if (!reportReq.getEnterId().equals(userInfo.getEnterId())){
            attendRes.setCode(AtdcResultCode.ATDC106.ENTERID_NOT_MATCHED);
            attendRes.setSummary(AtdcResultSummary.ATDC106.ENTERID_NOT_MATCHED);
            return attendRes;
        }
        //查询时间没有
        if (StringUtils.isBlank(reportReq.getStartDate()) || StringUtils.isBlank(reportReq.getEndDate())){

            attendRes.setCode(AtdcResultCode.ATDC104.PARAMS_ILLEGAL_DATE);
            attendRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_ILLEGAL_DATE);
            return attendRes;
        }else {
            // 月份格式校验
            if (!TimeUtil.checkDateFormat(reportReq.getStartDate(), TimeUtil.BASE_DATE_FORMAT) ||
                    !TimeUtil.checkDateFormat(reportReq.getEndDate(), TimeUtil.BASE_DATE_FORMAT) )
                {
                    attendRes.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
                    attendRes.setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
                    return attendRes;
                }
            }
        return attendRes;
    }

    /**
     * pc导出考勤原始数据
     * @param
     * @param
     */
    @Override
    public AttendExportReptRes exportOriginalClockDataPc(AttendReportReq reportReq) {
        AttendRes attendRes = checkQueryOriginalParam(reportReq);
        AttendExportReptRes res = new AttendExportReptRes();
        if (!attendRes.isSuccess())
        {
            logger.warn("checkAuthorityOfPMR failed.reportReq={}|userInfo={}",
                reportReq, reportReq.getUserInfo());
            res.setCode(attendRes.getCode());
            res.setSummary(attendRes.getSummary());
            return res;
        }
        // 查询满足条件的消息总量
        int totalCount = reportDao.queryOriginalClockDataPcCount(reportReq);
        if (totalCount == 0)
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            return res;
        }
        if (totalCount > config.getMaxExportCount())
        {
            res.setCode(AtdcResultCode.ATDC108.SO_MANY_EXPORT_DATA);
            res.setSummary(AtdcResultSummary.ATDC108.SO_MANY_EXPORT_DATA);
            return res;
        }
        // TODO 目前数据量较小，直接查整个企业的数据导出即可，后续量增多时可调整成分页查询
        List<AttendEntity> list = reportDao.exportOriginalClockDataPc(reportReq);

        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            return res;
        }
        buildOriginalClockDataPcExcel(reportReq, list, res);
        return res;
    }

    /**
     * 构建导出数据
     * @param
     * @param list
     * @param res
     */
    private void buildOriginalClockDataPcExcel(AttendReportReq req, List<AttendEntity> list, AttendExportReptRes res) {
        String title = "【" + req.getUserInfo().getEnterName() + "】考勤原始数据 统计时间 "
            + req.getStartDate() + "至" + req.getEndDate();
        String[] rowsName = new String[] { "名称", "contactId","手机号码","部门", "考勤组", "日期", "打卡时间",
            "打卡地点","详细位置", "是否外勤","外勤打卡备注"};

        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        // 循环组装单元格数据
        for (AttendEntity entity : list)
        {
            objs = new Object[rowsName.length];
            objs[0] = entity.getEmployeeName();
            objs[1] = entity.getContactId();
            objs[2] = entity.getPhone() ==null || entity.getPhone().equals("") ? "-":entity.getPhone();
            objs[3] = entity.getDeptName() == null ? " " : entity.getDeptName();
            objs[4] = entity.getAttendanceName();
            objs[5] = TimeUtil.date2String(entity.getAttendanceDate(), "yyyy-MM-dd");
            objs[6] = TimeUtil.date2String(entity.getAttendanceTime(), TimeUtil.BASE_TIME_FORMAT);
            objs[7] =entity.getLocation();
            objs[8] =entity.getDetailAddr();
            objs[9] =entity.getStatus()==0 ? "否": "是";
            objs[10] =entity.getOutWorkRemark()==null?" ":entity.getOutWorkRemark();
            dataList.add(objs);
        }

        // 生成文件的路径目录
        String baseDir = config.getDownLoadTempBaseDir();
        // 由于此时生成的文件是临时的，为防止调用了接口生成了文件但不进行下载触发不了删除，导致文件堆积，按时间日期进行隔层，采用用定时任务进行删除，
        // 生成的目录如:20180211/10/,比如现在是10点，定时任务将删除8点和之前的所有数据(即20180211/08/和20180211/07/目录下的都会被删除)
        String dailyDate = TimeUtil.getCurrentDateTime("yyyyMMdd");
        String currentHour = TimeUtil.getCurrentHour();
        String fileTempDir = baseDir + dailyDate + File.separator + currentHour;
        // 创建临时文件生成的目录
        FileUtil.createDir(fileTempDir);
        // 为防止生成的文件名重复,对title进行拼接uuid处理，下载时再裁剪文件名
        String randomId = UUID.randomUUID().toString();
        String title2 = randomId
            + AtdcConsts.ATTEND_BIZ.DOWNLOAD_FILENAME_SEPARATOR + title;
        String path = fileTempDir + File.separator + title2 + ".xls";
        // 调用公共组件方法生成execl
        ExeclUtil.getInstance().export(title, rowsName, dataList, path, false);
        // 返回内容均采用base64加密
        String base64Dir = Base64Coder.encodeString(dailyDate + File.separator
            + currentHour);
        String base64Title = Base64Coder.encodeString(title2 + ".xls");
        String serverUrl = config.getDownLoadServerUrl();
        String url = serverUrl + "contendDirId=" + base64Dir + "&fileName="
            + base64Title;
        res.setUrl(url);
    }
}

/**
 * 文件名：AttendReportService.java
 * 创建日期： 2017年6月8日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.service;

import richinfo.attendance.bean.*;

/**
 * 功能描述：报表模块接口
 * 
 */
public interface AttendReportService
{
    /**
     * 查询团队日报详情（所有员工）
     * @param reqParam
     * @return
     */
    public TeamDailyRes queryTeamDailyInfo(AttendReportReq reqParam);

    /**
     * 查看员工个人月报,支持员工查询本人，管理员查询其他人 （某个员工）
     * @param reportReq
     * @return
     */
    AttendReportRes queryPersonalMonthlyReport(AttendReportReq reportReq);

    /**
     * 
     * 查询团队日报统计（所有员工）
     * @param reportReq
     * @return
     */
    AttendReportRes queryTeamDailyReport(AttendReportReq reportReq);

    /**
     * 查询团队月报统计（所有员工）
     * @param reqParam
     * @return
     */
    public TeamMonthRes queryTeamMonthlyReport(AttendReportReq reqParam);

    /**
     * 发送团队月报统计（所有员工）
     * @param reqParam
     * @return
     */
    public TeamMonthRes sendTeamMonthlyReport(AttendReportReq req);

    /**
     * 发送团队的员工月报信息（某个员工）
     * @param reqParam
     * @return
     */
    public AttendReportRes sendPersonalMonthlyReport(AttendReportReq reportReq);

    /**
     * 发送团队日报详情（所有员工）
     * @param reqParam
     * @return
     */
    public TeamDailyRes sendTeamDailyInfo(AttendReportReq reportReq);

    /**
     * PC端查询团队月报统计数据
     * @param req
     * @return
     */
    public TeamMonthRes queryTeamMonthPc(AttendReportReq req);

    /**
     * PC端查询员工月报明细
     * @param req
     * @return
     */
    public AttendReportRes queryEmpMonthPc(AttendReportReq req);

    /**
     * 导出PC端团队月报统计数据
     * @param req
     * @return
     */
    public AttendExportReptRes exportTeamMonthInfoPc(AttendReportReq req);

    /**
     * 导出PC端员工月报明细数据
     * @param req
     * @param res
     * @return
     */
    public AttendExportReptRes exportEmpMonthPc(AttendReportReq req);

    /**
     * 查询团队日报分项数据统计接口
     * @param req
     * @return
     */
    public AttendReptItemRes queryTeamDailyItem(AttendReportReq req);

    /**
     * 查询团队日报个人月列表考勤数据统计
     * @param req
     * @return
     */
    public AttendReptEmpMonthRes queryEmpMonthList(AttendReportReq req);

    /**
     * H5统计个人月报统计
     * 2018.11 报表优化需求
     * @param reportReq
     * @return
     */
    AttendReportRes queryEmpMonthStatistics(AttendReportReq reportReq);

    /**
     * H5统计个人月报
     * 2018.11 报表优化需求
     * @param reportReq
     * @return
     */
    AttendReportRes queryEmpMonthDetail(AttendReportReq reportReq);

    /**
     * H5统计团队月报统计
     * 2018.11 报表优化需求
     * @param reportReq
     * @return
     */
    TeamMonthRes queryTeamMonthStatistics(AttendReportReq reportReq);

    /**
     * h5查询团队月报分项详情
     * 2018.11 报表优化需求
     * @param reportReq
     * @return
     */
    AttendReportRes queryTeamMonthDetails(AttendReportReq reportReq);

    /**
     * H5统计团队日报统计
     * 2018.11 报表优化需求
     * @param reportReq
     * @return
     */
    AttendReportRes queryTeamDailyStatistics(AttendReportReq reportReq);

    /**
     * H5统计团队日报明细
     * 2018.11 报表优化需求
     * @param reportReq
     * @return
     */
    AttendReportRes queryTeamDailyDetail(AttendReportReq reportReq);

    /**
     * h5查询团队日报分项详情
     * 2018.11 报表优化需求
     * @param reportReq
     * @return
     */
    AttendReportRes queryTeamDailyItemDetails(AttendReportReq reportReq);

    /**
     * pc 查询考勤原始数据
     * @param reportReq
     * @return
     */
    AttendRes queryOriginalClockDataPc(AttendReportReq reportReq);
    /**
     * pc导出考勤原始数据
     *
     */
    AttendExportReptRes exportOriginalClockDataPc(AttendReportReq reportReq);
}

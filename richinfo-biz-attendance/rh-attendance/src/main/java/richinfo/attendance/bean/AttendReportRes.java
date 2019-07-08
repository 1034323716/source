/**
 * 文件名：AttendReportRes.java
 * 创建日期： 2017年6月9日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月9日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;
import richinfo.attendance.entity.EmployeeMonthDetail;
import richinfo.attendance.entity.EmployeeMonthDetailVO;
import richinfo.attendance.entity.TeamDailyReportEntity;

import java.util.List;

/**
 * 功能描述：考勤报表模块响应实体类
 * 
 */
public class AttendReportRes extends ResBean
{

    private static final Long serialVersionUID = -893367148650028173L;

    /** 当前页 */
    private Integer pageNo;
    /** 每页显示数据大小 */
    private Integer pageSize;
    /** 总数据量 */
    private Integer totalCount;
    /** 员工个人月报明细列表 */
    private List<EmployeeMonthDetail> employeeMonth;

    /** 员工个人月报明细列表 新版需求 */
    private List<EmployeeMonthDetailVO> employeeMonthDetailVO;

    /** 新旧数据标识*/
    private Integer isNewData;

    /**团队月报分项id*/
    private Integer itemId;

    /*审批上限标识 0没有上线或者没有约束，1上限*/
    private int restrictStatus;

    public int getRestrictStatus() {
        return restrictStatus;
    }

    public void setRestrictStatus(int restrictStatus) {
        this.restrictStatus = restrictStatus;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public List<EmployeeMonthDetailVO> getEmployeeMonthDetailVO() {
        return employeeMonthDetailVO;
    }

    public void setEmployeeMonthDetailVO(List<EmployeeMonthDetailVO> employeeMonthDetailVO) {
        this.employeeMonthDetailVO = employeeMonthDetailVO;
    }

    public Integer getIsNewData() {
        return isNewData;
    }

    public void setIsNewData(Integer isNewData) {
        this.isNewData = isNewData;
    }

    /** 团队日报统计结果 */
    private TeamDailyReportEntity teamDailyReport;

    public Integer getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(Integer pageNo)
    {
        this.pageNo = pageNo;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<EmployeeMonthDetail> getEmployeeMonth()
    {
        return employeeMonth;
    }

    public void setEmployeeMonth(List<EmployeeMonthDetail> employeeMonth)
    {
        this.employeeMonth = employeeMonth;
    }

    public TeamDailyReportEntity getTeamDailyReport()
    {
        return teamDailyReport;
    }

    public void setTeamDailyReport(TeamDailyReportEntity teamDailyReport)
    {
        this.teamDailyReport = teamDailyReport;
    }

    @Override
    public String toString()
    {
        return "AttendReportRes [pageNo=" + pageNo + ", pageSize=" + pageSize
            + ", totalCount=" + totalCount + ", employeeMonth=" + employeeMonth
            + ", teamDailyReport=" + teamDailyReport + "]";
    }
}

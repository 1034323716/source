/**
 * 文件名：TeamMonthEntity.java
 * 创建日期： 2017年6月9日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月9日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.entity;

import java.io.Serializable;

/**
 * 功能描述： 团队月报数据实体类
 */
public class TeamMonthEntity implements Serializable
{

    private static final Long serialVersionUID = -3663053662555213196L;

    /** 分页显示，默认第一页 */
    private Integer pageNo = 1;

    /** 分页每页显示数据量，默认20条 */
    private Integer pageSize = 20;

    /** 数据总量 */
    private Long totalCount;

    /** 查询月份 */
    private String attendanceMonth;

    /** 考勤人员Id */
    private String uid;

    /** 企业通讯录Id */
    private String contactId;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    /** 企业Id */
    private String enterId;

    /**部门名称*/
    private String deptName;

    /** 考勤组Id */
    private String attendanceId;

    /** 考勤组名称 */
    private String attendanceName;

    /** 考勤人员名称 */
    private String employeeName;

    /** 正常出勤天数 */
    private Integer normalDays;

    /** 外勤天数 */
    private Integer outsideDays;

    /** 迟到天数 */
    private Integer lateDays;

    /** 早退天数 */
    private Integer earlyDays;

    /** 未打卡天数 */
    private Integer notClockedDays;

    /** 已申诉天数 */
    private Integer appealDays;

    /** 用户手机号 */
    private String phone;

    /** 工作总时长 */
    private Integer totalWorkTime;
    /*2018-11-06 新增迟到分钟，早退分钟，上班未打卡，下班未打卡*/
    /*迟到分钟数*/
    private Integer lateMinutes;

    /*早退分钟数*/
    private Integer earlyMinutes;

    /*上班未打卡*/
    private Integer goNotClockedDays;

    /*下班未打卡*/
    private Integer leaveNotClockedDays;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public Integer getEarlyMinutes() {
        return earlyMinutes;
    }

    public void setEarlyMinutes(Integer earlyMinutes) {
        this.earlyMinutes = earlyMinutes;
    }

    public Integer getGoNotClockedDays() {
        return goNotClockedDays;
    }

    public void setGoNotClockedDays(Integer goNotClockedDays) {
        this.goNotClockedDays = goNotClockedDays;
    }

    public Integer getLeaveNotClockedDays() {
        return leaveNotClockedDays;
    }

    public void setLeaveNotClockedDays(Integer leaveNotClockedDays) {
        this.leaveNotClockedDays = leaveNotClockedDays;
    }

    public Integer getTotalWorkTime()
    {
        return totalWorkTime;
    }

    public void setTotalWorkTime(Integer totalWorkTime)
    {
        this.totalWorkTime = totalWorkTime;
    }

    public Integer getAppealDays()
    {
        return appealDays;
    }

    public void setAppealDays(Integer appealDays)
    {
        this.appealDays = appealDays;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

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

    public Long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }

    public String getAttendanceMonth()
    {
        return attendanceMonth;
    }

    public void setAttendanceMonth(String attendanceMonth)
    {
        this.attendanceMonth = attendanceMonth;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public String getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getAttendanceName()
    {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName)
    {
        this.attendanceName = attendanceName;
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    public void setEmployeeName(String employeeName)
    {
        this.employeeName = employeeName;
    }

    public Integer getNormalDays()
    {
        return normalDays;
    }

    public void setNormalDays(Integer normalDays)
    {
        this.normalDays = normalDays;
    }

    public Integer getOutsideDays()
    {
        return outsideDays;
    }

    public void setOutsideDays(Integer outsideDays)
    {
        this.outsideDays = outsideDays;
    }

    public Integer getLateDays()
    {
        return lateDays;
    }

    public void setLateDays(Integer lateDays)
    {
        this.lateDays = lateDays;
    }

    public Integer getEarlyDays()
    {
        return earlyDays;
    }

    public void setEarlyDays(Integer earlyDays)
    {
        this.earlyDays = earlyDays;
    }

    public Integer getNotClockedDays()
    {
        return notClockedDays;
    }

    public void setNotClockedDays(Integer notClockedDays)
    {
        this.notClockedDays = notClockedDays;
    }

    @Override
    public String toString() {
        return "TeamMonthEntity{" +
            "pageNo=" + pageNo +
            ", pageSize=" + pageSize +
            ", totalCount=" + totalCount +
            ", attendanceMonth='" + attendanceMonth + '\'' +
            ", uid='" + uid + '\'' +
            ", enterId='" + enterId + '\'' +
            ", attendanceId='" + attendanceId + '\'' +
            ", attendanceName='" + attendanceName + '\'' +
            ", employeeName='" + employeeName + '\'' +
            ", normalDays=" + normalDays +
            ", outsideDays=" + outsideDays +
            ", lateDays=" + lateDays +
            ", earlyDays=" + earlyDays +
            ", notClockedDays=" + notClockedDays +
            ", appealDays=" + appealDays +
            ", phone='" + phone + '\'' +
            ", totalWorkTime=" + totalWorkTime +
            ", lateMinutes=" + lateMinutes +
            ", earlyMinutes=" + earlyMinutes +
            ", goNotClockedDays=" + goNotClockedDays +
            ", leaveNotClockedDays=" + leaveNotClockedDays +
            '}';
    }
}

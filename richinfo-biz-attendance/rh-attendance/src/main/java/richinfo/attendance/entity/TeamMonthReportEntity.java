/**
 * 文件名：TeamMonthReportEntity.java
 * 创建日期： 2017年6月9日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月9日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：
 *
 */
public class TeamMonthReportEntity implements Serializable
{

    private static final long serialVersionUID = 2123329311393117716L;

    /** 考勤组Id */
    private long attendanceId;

    /** 企业Id */
    private String enterId;

    /** 考勤月 */
    private String attendanceMonth;

    /** 考勤日期 ：查询出统计数据时需要进行映射 */
    private Date attendanceDate;

    /** 考勤用户uid */
    private String uid;

    /** 考勤人员姓名 */
    private String employeeName;

    /** 正常出勤天数 */
    private int normalDays;

    /** 外勤天数 */
    private int outsideDays;

    /** 迟到天数 */
    private int lateDays;

    /** 早退天数 */
    private int earlyDays;

    /** 未打卡天数 */
    private int notClockedDays;

    /** 已申诉天数 */
    private int appealDays;

    /** 新增phone字段 */
    private String phone;

    /** 创建时间 */
    private Date createTime;

    /** 工作时长 */
    private int workMinute;
    /*2018-11-06 新增迟到分钟，早退分钟，上班未打卡，下班未打卡*/
    /*迟到分钟数*/
    private int lateMinutes;

    /*早退分钟数*/
    private int earlyMinutes;

    /*上班未打卡*/
    private int goNotClockedDays;

    /*下班未打卡*/
    private int leaveNotClockedDays;

    public int getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(int lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public int getEarlyMinutes() {
        return earlyMinutes;
    }

    public void setEarlyMinutes(int earlyMinutes) {
        this.earlyMinutes = earlyMinutes;
    }

    public int getGoNotClockedDays() {
        return goNotClockedDays;
    }

    public void setGoNotClockedDays(int goNotClockedDays) {
        this.goNotClockedDays = goNotClockedDays;
    }

    public int getLeaveNotClockedDays() {
        return leaveNotClockedDays;
    }

    public void setLeaveNotClockedDays(int leaveNotClockedDays) {
        this.leaveNotClockedDays = leaveNotClockedDays;
    }

    public int getWorkMinute()
    {
        return workMinute;
    }

    public void setWorkMinute(int workMinute)
    {
        this.workMinute = workMinute;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public int getAppealDays()
    {
        return appealDays;
    }

    public void setAppealDays(int appealDays)
    {
        this.appealDays = appealDays;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public String getAttendanceMonth()
    {
        return attendanceMonth;
    }

    public void setAttendanceMonth(String attendanceMonth)
    {
        this.attendanceMonth = attendanceMonth;
    }

    public Date getAttendanceDate()
    {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate)
    {
        this.attendanceDate = attendanceDate;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    public void setEmployeeName(String employeeName)
    {
        this.employeeName = employeeName;
    }

    public int getNormalDays()
    {
        return normalDays;
    }

    public void setNormalDays(int normalDays)
    {
        this.normalDays = normalDays;
    }

    public int getOutsideDays()
    {
        return outsideDays;
    }

    public void setOutsideDays(int outsideDays)
    {
        this.outsideDays = outsideDays;
    }

    public int getLateDays()
    {
        return lateDays;
    }

    public void setLateDays(int lateDays)
    {
        this.lateDays = lateDays;
    }

    public int getEarlyDays()
    {
        return earlyDays;
    }

    public void setEarlyDays(int earlyDays)
    {
        this.earlyDays = earlyDays;
    }

    public int getNotClockedDays()
    {
        return notClockedDays;
    }

    public void setNotClockedDays(int notClockedDays)
    {
        this.notClockedDays = notClockedDays;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TeamMonthReportEntity{" +
            "attendanceId=" + attendanceId +
            ", enterId='" + enterId + '\'' +
            ", attendanceMonth='" + attendanceMonth + '\'' +
            ", attendanceDate=" + attendanceDate +
            ", uid='" + uid + '\'' +
            ", employeeName='" + employeeName + '\'' +
            ", normalDays=" + normalDays +
            ", outsideDays=" + outsideDays +
            ", lateDays=" + lateDays +
            ", earlyDays=" + earlyDays +
            ", notClockedDays=" + notClockedDays +
            ", appealDays=" + appealDays +
            ", phone='" + phone + '\'' +
            ", createTime=" + createTime +
            ", workMinute=" + workMinute +
            ", lateMinutes=" + lateMinutes +
            ", earlyMinutes=" + earlyMinutes +
            ", goNotClockedDays=" + goNotClockedDays +
            ", leaveNotClockedDays=" + leaveNotClockedDays +
            '}';
    }
}

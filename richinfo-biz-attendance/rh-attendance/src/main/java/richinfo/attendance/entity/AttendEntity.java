/**
 * 文件名：AttendRes.java
 * 创建日期： 2017年6月5日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

import richinfo.attendance.util.AtdcTimeUtil;

/**
 * 功能描述：考勤模块实体类
 * 
 */
public class AttendEntity implements Serializable
{

    private static final long serialVersionUID = -8260571737042181899L;

    /** 考勤记录ID */
    private long recordId;

    /** 企业联系人ID */
    private String uid;
    /*考勤人员名称*/
    private  String employeeName;

    /** 手机号 */
    private String phone;

    /**部门名称*/
    private String deptName;

    /** 考勤组ID */
    private long attendanceId;

    /** 考勤日期，YYYY-MM-DD */
    private Date attendanceDate;

    /** 打卡时间 */
    private Date attendanceTime;

    /** 打卡地点 */
    private String location;

    /** 详细地址 */
    private String detailAddr;

    /** 经度 */
    private double longitude;

    /** 纬度 */
    private double latitude;

    /** 是否外勤打卡 0：公司打卡 1：外勤打卡 */
    private int status = ClockStatus.InClock.getValue();

    //外勤打卡备注
    private String outWorkRemark;


    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getOutWorkRemark() {
        return outWorkRemark;
    }

    public void setOutWorkRemark(String outWorkRemark) {
        this.outWorkRemark = outWorkRemark;
    }

    /** 通讯录联系人ID */
    private String contactId;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    /*考勤组名*/
    private String attendanceName;

    public String getAttendanceName() {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName) {
        this.attendanceName = attendanceName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Date getAttendanceTime()
    {
        return attendanceTime;
    }


    /** 将打卡时间以毫秒数返回给前端 */
    public long getTime()
    {
        return AtdcTimeUtil.togetherDateAndTime(attendanceDate, attendanceTime,null);
    }

    public void setAttendanceTime(Date attendanceTime)
    {
        this.attendanceTime = attendanceTime;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(long recordId)
    {
        this.recordId = recordId;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public Date getAttendanceDate()
    {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate)
    {
        this.attendanceDate = attendanceDate;
    }

    public String getDetailAddr()
    {
        return detailAddr;
    }

    public void setDetailAddr(String detailAddr)
    {
        this.detailAddr = detailAddr;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public enum ClockStatus {
        // 正常 //异常 //未知
        InClock(0), OutClock(1), Unknow(-1);

        private int value;

        private ClockStatus(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static ClockStatus parse(int value)
        {
            switch (value)
            {
            case 0:
                return InClock;
            case 1:
                return OutClock;
            default:
                return Unknow;
            }
        }
    }

    @Override
    public String toString() {
        return "AttendEntity{" +
            "recordId=" + recordId +
            ", uid='" + uid + '\'' +
            ", employeeName='" + employeeName + '\'' +
            ", phone='" + phone + '\'' +
            ", attendanceId=" + attendanceId +
            ", attendanceDate=" + attendanceDate +
            ", attendanceTime=" + attendanceTime +
            ", location='" + location + '\'' +
            ", detailAddr='" + detailAddr + '\'' +
            ", longitude=" + longitude +
            ", latitude=" + latitude +
            ", status=" + status +
            ", outWorkRemark='" + outWorkRemark + '\'' +
            ", contactId='" + contactId + '\'' +
            ", attendanceName='" + attendanceName + '\'' +
            '}';
    }
}

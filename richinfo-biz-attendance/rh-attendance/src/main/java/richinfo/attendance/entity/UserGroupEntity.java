/**
 * 文件名：UserGroupEntity.java
 * 创建日期： 2017年6月12日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月12日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.entity;

import richinfo.attendance.common.BeanObject;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述： 用户与考勤组关系实体类
 */
public class UserGroupEntity extends BeanObject implements Serializable
{
    private static final long serialVersionUID = -1796306181257117686L;

    /** 考勤人员ID */
    private long employeeId;

    /** 考勤组ID */
    private long attendanceId;

    /** 企业联系人ID */
    private String uid;

    /** 考勤员工姓名 */
    private String employeeName;

    /** 手机号 */
    private String phone;

    /** 管理员标识 1是0否 */
    private int isAdmin;

    /** 企业ID */
    private String enterId;

    /** 企业名称 */
    private String enterName;

    /** 考勤组名称 */
    private String attendanceName;

    /** 考勤地点 */
    private String location;

    /** 考勤详细地点 */
    private String detailAddr;

    /** 经度 */
    private double longitude;

    /** 纬度 */
    private double latitude;

    /** 有效范围（公司内打卡） */
    private int attendanceRange;

    /** 有效范围（公司外打卡） */
    private int attendanceOutRange;

    /** 审批员姓名 */
    private String examineName;

    /** 审批员uid */
    private String examineUid;

    /*0工作日/1节假日*/
    private  int workdayStatus;

    /** 考勤组类型 1：固定班 2：排班制 3：自由班 */
    private int attendType;
    
    /** 该规则只有固定班有值，其他考勤组类型的考勤组此值为空 规则具体采用json格式存储*/
    private String fixedAttendRule;
    
    /** 该规则只有自由班有值，其他考勤组类型的考勤组此值为空  规则具体采用json格式存储*/
    private String freeAttendRule;
    
    /** 该规则只有自由班有值，其他考勤组类型的考勤组此值为空  规则具体采用json格式存储*/
    private int allowLateTime;
    
    /** 是否按法定节假日处理打卡：0按法定节假日处理打卡：1不按法定节假日处理 此规则只针对固定班跟自由班*/
    private int relyHoliday;
    //考勤地址
    private List<AttendClockSite>attendClockSites;

    /**
     * 角色类型 1为考勤组负责
     */
    private Integer roleType;

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    //是否允许外勤打卡标志，0允许1不允许
    private int isAllowedOutRangeClock;

    public int getIsAllowedOutRangeClock() {
        return isAllowedOutRangeClock;
    }

    public void setIsAllowedOutRangeClock(int isAllowedOutRangeClock) {
        this.isAllowedOutRangeClock = isAllowedOutRangeClock;
    }

    //    private int roleType;
//
//    public int getRoleType() {
//        return roleType;
//    }
//
//    public void setRoleType(int roleType) {
//        this.roleType = roleType;
//    }

    public List<AttendClockSite> getAttendClockSites() {
        return attendClockSites;
    }

    public void setAttendClockSites(List<AttendClockSite> attendClockSites) {
        this.attendClockSites = attendClockSites;
    }

    public int getWorkdayStatus() {
        return workdayStatus;
    }

    public void setWorkdayStatus(int workdayStatus) {
        this.workdayStatus = workdayStatus;
    }

    public int getAttendanceOutRange()
    {
        return attendanceOutRange;
    }

    public void setAttendanceOutRange(int attendanceOutRange)
    {
        this.attendanceOutRange = attendanceOutRange;
    }

    public String getExamineName()
    {
        return examineName;
    }

    public void setExamineName(String examineName)
    {
        this.examineName = examineName;
    }

    public String getExamineUid()
    {
        return examineUid;
    }

    public void setExamineUid(String examineUid)
    {
        this.examineUid = examineUid;
    }

    public long getEmployeeId()
    {
        return employeeId;
    }

    public void setEmployeeId(long employeeId)
    {
        this.employeeId = employeeId;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
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

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public int getIsAdmin()
    {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin)
    {
        this.isAdmin = isAdmin;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public String getEnterName()
    {
        return enterName;
    }

    public void setEnterName(String enterName)
    {
        this.enterName = enterName;
    }

    public String getAttendanceName()
    {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName)
    {
        this.attendanceName = attendanceName;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
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

    public int getAttendanceRange()
    {
        return attendanceRange;
    }

    public void setAttendanceRange(int attendanceRange)
    {
        this.attendanceRange = attendanceRange;
    }
    

    public int getAttendType()
    {
        return attendType;
    }

    public void setAttendType(int attendType)
    {
        this.attendType = attendType;
    }

    public String getFixedAttendRule()
    {
        return fixedAttendRule;
    }

    public void setFixedAttendRule(String fixedAttendRule)
    {
        this.fixedAttendRule = fixedAttendRule;
    }

    public String getFreeAttendRule()
    {
        return freeAttendRule;
    }

    public void setFreeAttendRule(String freeAttendRule)
    {
        this.freeAttendRule = freeAttendRule;
    }

    public int getAllowLateTime()
    {
        return allowLateTime;
    }

    public void setAllowLateTime(int allowLateTime)
    {
        this.allowLateTime = allowLateTime;
    }

    public int getRelyHoliday()
    {
        return relyHoliday;
    }

    public void setRelyHoliday(int relyHoliday)
    {
        this.relyHoliday = relyHoliday;
    }

    @Override
    public String toString() {
        return "UserGroupEntity{" +
                "employeeId=" + employeeId +
                ", attendanceId=" + attendanceId +
                ", uid='" + uid + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", phone='" + phone + '\'' +
                ", isAdmin=" + isAdmin +
                ", enterId='" + enterId + '\'' +
                ", enterName='" + enterName + '\'' +
                ", attendanceName='" + attendanceName + '\'' +
                ", location='" + location + '\'' +
                ", detailAddr='" + detailAddr + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", attendanceRange=" + attendanceRange +
                ", attendanceOutRange=" + attendanceOutRange +
                ", examineName='" + examineName + '\'' +
                ", examineUid='" + examineUid + '\'' +
                ", workdayStatus=" + workdayStatus +
                ", attendType=" + attendType +
                ", fixedAttendRule='" + fixedAttendRule + '\'' +
                ", freeAttendRule='" + freeAttendRule + '\'' +
                ", allowLateTime=" + allowLateTime +
                ", relyHoliday=" + relyHoliday +
                ", attendClockSites=" + attendClockSites +
                '}';
    }
}

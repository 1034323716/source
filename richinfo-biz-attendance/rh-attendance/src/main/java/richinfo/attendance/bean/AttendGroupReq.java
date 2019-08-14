/**
 * 文件名：AttendGroupReq.java
 * 创建日期： 2017年6月2日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月2日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.entity.*;

import java.util.List;

/**
 * 功能描述：考勤组管理模块的请求实体类
 * 
 */
public class AttendGroupReq extends AttendReqBean
{

    private static final long serialVersionUID = 4527786030737057924L;

    /** 考勤组名称 */
    private String attendanceName;

    /** 企业名称 */
    private String enterName;

    /** 上午工作时间 */
    private String amTime;

    /** 下午工作时间 */
    private String pmTime;

    /** 考勤地点 */
    private String location;

    /** 详细地址 */
    private String detailAddr;

    /** 经度 */
    private double longitude;

    /** 纬度 */
    private double latitude;

    /** 考勤有效范围 */
    private int range;

    /** 审批员姓名 */
    private String examineName;

    /** 审批员uid */
    private String examineUid;

    /** 审批员contactId*/
    private String examineContactId;

    /** 考勤人员列表 */
    private List<UserInfo> employees;

    /** 不需要考勤组人员列表 */
    private List<UserInfo> noAttendEmployees;

    /** 外勤打卡选择范围 */
    private int outRange;
    
    /** 考勤组类型 1：固定班 2：排班 3：自由班 */
    private int attendType;

    /** 固定班排班规则 */
    private String fixedAttendRule;

    /** 自由班排班规则 */
    private String freeAttendRule;

    /** 允许迟到时长，单位：分钟 （针对固定班和排班） */
    private int allowLateTime;
    
    /**是否按法定节假日统计处理考勤打卡数据*/
    private int relyHoliday;
    
    /**班次列表 （考勤类型 为2时，该字段有值）*/
    private List<AttendanceScheduleShift> scheduleShifts;

    /*考勤地点*/
    private  List<AttendClockSite> attendClockSites;

    /*部门选择器*/
    private List<AttendDepartmentChooser> attendDepartmentChoosers;

    /** 白名单创建人*/
    private String creator;

    /** 白名单创建人id*/
    private String creatorId;

    private String equipmentUseStatus;

    public String getEquipmentUseStatus() {
        return equipmentUseStatus;
    }

    public void setEquipmentUseStatus(String equipmentUseStatus) {
        this.equipmentUseStatus = equipmentUseStatus;
    }

    /** 考勤人员列表 */
    private List<UserInfo> chargemanList;
    /*审批限制实体*/
    private AttendApprovalRestrict attendApprovalRestrict;

    public AttendApprovalRestrict getAttendApprovalRestrict() {
        return attendApprovalRestrict;
    }

    public void setAttendApprovalRestrict(AttendApprovalRestrict attendApprovalRestrict) {
        this.attendApprovalRestrict = attendApprovalRestrict;
    }

    //是否允许外勤打卡标志，0允许1不允许
    private int isAllowedOutRangeClock;

    public int getIsAllowedOutRangeClock() {
        return isAllowedOutRangeClock;
    }

    public void setIsAllowedOutRangeClock(int isAllowedOutRangeClock) {
        this.isAllowedOutRangeClock = isAllowedOutRangeClock;
    }

    //是否使用弹性班制 0使用1不使用
    private  int useFlexibleRule;

    public int getUseFlexibleRule() {
        return useFlexibleRule;
    }

    public void setUseFlexibleRule(int useFlexibleRule) {
        this.useFlexibleRule = useFlexibleRule;
    }

    //弹性时间时长
    private double flexitime;

    public double getFlexitime() {
        return flexitime;
    }

    public void setFlexitime(double flexitime) {
        this.flexitime = flexitime;
    }

    private String employeeName;

    private String attendId;

    public String getAttendId() {
        return attendId;
    }

    public void setAttendId(String attendId) {
        this.attendId = attendId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    private String equipmentSerial;

    private String equipmentDeviceType;

    private String equipmentStatus;

    public String getEquipmentSerial() {
        return equipmentSerial;
    }

    public void setEquipmentSerial(String equipmentSerial) {
        this.equipmentSerial = equipmentSerial;
    }

    public String getEquipmentDeviceType() {
        return equipmentDeviceType;
    }

    public void setEquipmentDeviceType(String equipmentDeviceType) {
        this.equipmentDeviceType = equipmentDeviceType;
    }

    public String getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(String equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    private String equipmentLimit;

    public String getEquipmentLimit() {
        return equipmentLimit;
    }

    public void setEquipmentLimit(String equipmentLimit) {
        this.equipmentLimit = equipmentLimit;
    }

    public String getExamineContactId() {
        return examineContactId;
    }

    public void setExamineContactId(String examineContactId) {
        this.examineContactId = examineContactId;
    }

    public List<UserInfo> getChargemanList() {
        return chargemanList;
    }

    public void setChargemanList(List<UserInfo> chargemanList) {
        this.chargemanList = chargemanList;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<AttendDepartmentChooser> getAttendDepartmentChoosers() {
        return attendDepartmentChoosers;
    }

    public void setAttendDepartmentChoosers(List<AttendDepartmentChooser> attendDepartmentChoosers) {
        this.attendDepartmentChoosers = attendDepartmentChoosers;
    }

    public List<UserInfo> getNoAttendEmployees() {
        return noAttendEmployees;
    }

    public void setNoAttendEmployees(List<UserInfo> noAttendEmployees) {
        this.noAttendEmployees = noAttendEmployees;
    }

    public List<AttendClockSite> getAttendClockSites() {
        return attendClockSites;
    }

    public void setAttendClockSites(List<AttendClockSite> attendClockSites) {
        this.attendClockSites = attendClockSites;
    }

    public int getOutRange()
    {
        return outRange;
    }

    public void setOutRange(int outRange)
    {
        this.outRange = outRange;
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

    public String getAttendanceName()
    {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName)
    {
        this.attendanceName = attendanceName;
    }

    public String getEnterName()
    {
        return enterName;
    }

    public void setEnterName(String enterName)
    {
        this.enterName = enterName;
    }

    public String getAmTime()
    {
        return amTime;
    }

    public void setAmTime(String amTime)
    {
        this.amTime = amTime;
    }

    public String getPmTime()
    {
        return pmTime;
    }

    public void setPmTime(String pmTime)
    {
        this.pmTime = pmTime;
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

    public int getRange()
    {
        return range;
    }

    public void setRange(int range)
    {
        this.range = range;
    }

    public List<UserInfo> getEmployees()
    {
        return employees;
    }

    public void setEmployees(List<UserInfo> employees)
    {
        this.employees = employees;
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

    public List<AttendanceScheduleShift> getScheduleShifts()
    {
        return scheduleShifts;
    }

    public void setScheduleShifts(List<AttendanceScheduleShift> scheduleShifts)
    {
        this.scheduleShifts = scheduleShifts;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendGroupReq [attendanceName=")
            .append(attendanceName).append(", enterName=").append(enterName)
            .append(", amTime=").append(amTime).append(", pmTime=")
            .append(pmTime).append(", location=").append(location)
            .append(", detailAddr=").append(detailAddr).append(", longitude=")
            .append(longitude).append(", latitude=").append(latitude)
            .append(", range=").append(range).append(", examineName=")
            .append(examineName).append(", examineUid=").append(examineUid)
            .append(", employees=").append(employees).append(", outRange=")
            .append(outRange).append(", attendType=").append(attendType)
            .append(", fixedAttendRule=").append(fixedAttendRule)
            .append(", freeAttendRule=").append(freeAttendRule)
            .append(", allowLateTime=").append(allowLateTime)
            .append(", relyHoliday=").append(relyHoliday)
            .append(", scheduleShifts=").append(scheduleShifts).append("]");
        return builder.toString();
    }
 
}

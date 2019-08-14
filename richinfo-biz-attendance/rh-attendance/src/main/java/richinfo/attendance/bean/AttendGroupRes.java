/**
 * 文件名：AttendGroupRes.java
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

import richinfo.attendance.common.ResBean;
import richinfo.attendance.entity.*;
import richinfo.attendance.entity.AttendGroup.GroupStatus;
import richinfo.attendance.entity.vo.AttendanceEquipmentVO;

import java.util.Date;
import java.util.List;

/**
 * 功能描述： 考勤组管理模块的响应实体类
 */
public class AttendGroupRes extends ResBean
{

    private static final long serialVersionUID = 1077933511158732276L;

    /** 考勤组ID */
    private long attendanceId;

    /** 考勤组名称 */
    private String attendanceName;

    /** 企业ID */
    private String enterId;

    /** 企业名称 */
    private String enterName;

    /** 上午上班时间 */
    private String amTime;

    /** 下午上班时间 */
    private String pmTime;

    /** 考勤地点 */
    private String location;

    /** 详细考勤地址 */
    private String detailAddr;

    /** 经度 */
    private double longitude;

    /** 纬度 */
    private double latitude;

    /** 有效范围(内勤) */
    private int attendanceRange;

    /** 有效范围(外勤) */
    private int attendanceOutRange;

    /** 考勤组状态 */
    private int status = GroupStatus.Normal.getValue();

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date modifyTime;

    /** 审批员姓名 */
    private String examineName;

    /** 审批员uid */
    private String examineUid;

    /** 审批员contactId*/
    private String examineContactId;

    /** 创建人名字*/
    private String adminName;

    /** 创建人contactId*/
    private String adminContactId;

    /** 考勤组人员列表 */
    private List<UserInfo> employees;

    /** 考勤组人员白名单列表 */
    private List<AttendWhitelistEntity> whitelistEntities;

    /** 考勤组人员列表 */
    private List<AttendEmployee> chargemanList;

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

    // 企业内是否是空考勤组标识
    private int isEmptyAttendance;

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

    //审批限制实体
    private AttendApprovalRestrict attendApprovalRestrict;

    public AttendApprovalRestrict getAttendApprovalRestrict() {
        return attendApprovalRestrict;
    }

    public void setAttendApprovalRestrict(AttendApprovalRestrict attendApprovalRestrict) {
        this.attendApprovalRestrict = attendApprovalRestrict;
    }

    /** 设备限制数量 */
    private String equipmentLimit;

    public String getEquipmentLimit() {
        return equipmentLimit;
    }

    public void setEquipmentLimit(String equipmentLimit) {
        this.equipmentLimit = equipmentLimit;
    }

    //打卡设备
//    private  List<AttendanceEquipment> equipmentList;
//
//    public List<AttendanceEquipment> getEquipmentList() {
//        return equipmentList;
//    }
//
//    public void setEquipmentList(List<AttendanceEquipment> equipmentList) {
//        this.equipmentList = equipmentList;
//    }

    private  List<AttendanceEquipmentVO> equipmentList;

    public List<AttendanceEquipmentVO> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<AttendanceEquipmentVO> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminContactId() {
        return adminContactId;
    }

    public void setAdminContactId(String adminContactId) {
        this.adminContactId = adminContactId;
    }

    public int getIsEmptyAttendance() {
        return isEmptyAttendance;
    }

    public void setIsEmptyAttendance(int isEmptyAttendance) {
        this.isEmptyAttendance = isEmptyAttendance;
    }

    public String getExamineContactId() {
        return examineContactId;
    }

    public void setExamineContactId(String examineContactId) {
        this.examineContactId = examineContactId;
    }

    /*考勤地点*/
    private  List<AttendClockSite> attendClockSites;
    /*部门选择器*/
    private  List<AttendDepartmentChooser> attendDepartmentChoosers;

    public List<AttendEmployee> getChargemanList() {
        return chargemanList;
    }

    public void setChargemanList(List<AttendEmployee> chargemanList) {
        this.chargemanList = chargemanList;
    }

    public List<AttendDepartmentChooser> getAttendDepartmentChoosers() {
        return attendDepartmentChoosers;
    }

    public void setAttendDepartmentChoosers(List<AttendDepartmentChooser> attendDepartmentChoosers) {
        this.attendDepartmentChoosers = attendDepartmentChoosers;
    }

    public List<AttendClockSite> getAttendClockSites() {
        return attendClockSites;
    }

    public void setAttendClockSites(List<AttendClockSite> attendClockSites) {
        this.attendClockSites = attendClockSites;
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

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
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

    public int getAttendanceRange()
    {
        return attendanceRange;
    }

    public void setAttendanceRange(int attendanceRange)
    {
        this.attendanceRange = attendanceRange;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getModifyTime()
    {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime)
    {
        this.modifyTime = modifyTime;
    }

    public List<AttendWhitelistEntity> getWhitelistEntities() {
        return whitelistEntities;
    }

    public void setWhitelistEntities(List<AttendWhitelistEntity> whitelistEntities) {
        this.whitelistEntities = whitelistEntities;
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

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendGroupRes [attendanceId=").append(attendanceId)
            .append(", attendanceName=").append(attendanceName)
            .append(", enterId=").append(enterId).append(", enterName=")
            .append(enterName).append(", amTime=").append(amTime)
            .append(", pmTime=").append(pmTime).append(", location=")
            .append(location).append(", detailAddr=").append(detailAddr)
            .append(", longitude=").append(longitude).append(", latitude=")
            .append(latitude).append(", attendanceRange=")
            .append(attendanceRange).append(", attendanceOutRange=")
            .append(attendanceOutRange).append(", status=").append(status)
            .append(", createTime=").append(createTime).append(", modifyTime=")
            .append(modifyTime).append(", examineName=").append(examineName)
            .append(", examineUid=").append(examineUid).append(", employees=")
            .append(employees).append(", attendType=").append(attendType)
            .append(", fixedAttendRule=").append(fixedAttendRule)
            .append(", freeAttendRule=").append(freeAttendRule)
            .append(", allowLateTime=").append(allowLateTime)
            .append(", relyHoliday=").append(relyHoliday).append("]");
        return builder.toString();
    }

}

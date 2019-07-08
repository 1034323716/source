/**
 * 文件名：EmployeeMonthDetail.java
 * 创建日期： 2017年6月9日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月9日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.entity;

import richinfo.attendance.common.BeanObject;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AtdcTimeUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：员工个人月报明细实体类对象
 * 
 */
public class EmployeeMonthDetailVO extends BeanObject implements Serializable
{


    private static final Long serialVersionUID = 1810055394137127650L;
    /** 考勤组ID */
    private Long attendanceId;

    /** 企业ID */
    private String enterId;

    /** 企业联系人ID */
    private String uid;

    /** 员工姓名 */
    private String employeeName;

    /** 考勤日期 */
    private Date attendanceDate;

    /** 上班打卡时间 */
    private Date goWork;
    
    /** 上班打卡日期 */
    private Date goWorkDate;

    /** 上班打卡地点 */
    private String goLocation;

    /** 上班考勤描述 */
    private String goWorkDesc;

    /** 下班打卡时间 */
    private Date leaveWork;
    
    /** 下班打卡日期 */
    private Date leaveWorkDate;

    /** 下班打卡地点 */
    private String leaveLocation;

    /** 下班考勤描述 */
    private String leaveWorkDesc;

    /** 考勤区域状态，公司打卡 or 外勤打卡, 0：公司打卡 1：外勤打卡 */
    private Integer regionStatus;

    /** 打卡日期备注，工作日 or 节假日 */
    private String remark;

    /** 创建时间 */
    private Date createTime;

    /** 员工月报明细表主键Id */
    private Long monthRcdId;

    /** 考勤记录是否正常 1:正常 2：异常 3:待审核 4：审核通过 5：审核拒绝 6：已撤销 */
    private Integer recordState;

    /** 新增phone字段，便于统计报表需求 */
    private String phone;

    /** 异常申诉ID编号 */
    private Long appealId;

    /** 迟到分钟数 */
    private Integer lateMinutes;

    /** 早退分钟数 */
    private Integer earlyMinutes;

    /** 工作时长 */
    private Integer workMinutes;


    /** 查询报表分项数据时所需，表示用户未打卡次数 */
    private Integer count;

    /** 未打卡状态标识 1：上午未打卡 2：下午未打卡 3：上、下午未打卡 */
    private Integer attendanceState;

    
    /** 班次ID */
    private Long scheduleShiftId;
    
    /** 班次名称 */
    private String scheduleShiftName;
    
    /** 班次上下班时间 */
    private String scheduleShiftWorkTime;
    /** 是否排班 0:休息，1：是*/
    private Integer isSchedule;

    /**
     *
     *增加每一个打卡的外勤情况
     */
    /** 上班0打卡公司打卡  1外勤打卡 */
    private Integer goRegionStatus;
    /** 下班0打卡公司打卡  1外勤打卡 */
    private Integer leaveRegionStatus;
    /** 上下班打卡不是0 上班 1 下班 */
    private Integer amPmStatue;

    /** 考勤组名称 */
    private String attendanceName;

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

    /** 有效范围 (内勤) */
    private Integer attendanceRange;

    /** 考勤组状态 */
    private Integer status = AttendGroup.GroupStatus.Normal.getValue();

    /** 考勤组创建者uid */
    private String adminUid;

    /** 修改时间 */
    private Date modifyTime;

    /** 审批员姓名 */
    private String examineName;

    /** 上午未打卡次数 */
    private Integer goNotClockedDays;

    /** 下午未打卡次数 */
    private Integer leaveNotClockedDays;

    /** 迟到次数 */
    private Integer lateDays;

    /** 早退次数 */
    private Integer earlyDays;

    /** 申诉次数 */
    private Integer appealDays;

    /** 外勤天数 */
    private Integer outsideDays;

    public Integer getOutsideDays() {
        return outsideDays;
    }

    public void setOutsideDays(Integer outsideDays) {
        this.outsideDays = outsideDays;
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

    public Integer getLateDays() {
        return lateDays;
    }

    public void setLateDays(Integer lateDays) {
        this.lateDays = lateDays;
    }

    public Integer getEarlyDays() {
        return earlyDays;
    }

    public void setEarlyDays(Integer earlyDays) {
        this.earlyDays = earlyDays;
    }

    public Integer getAppealDays() {
        return appealDays;
    }

    public void setAppealDays(Integer appealDays) {
        this.appealDays = appealDays;
    }

    public String getAppealRecord() {
        return appealRecord;
    }

    public void setAppealRecord(String appealRecord) {
        this.appealRecord = appealRecord;
    }

    /** 审批记录
     * 1：申诉上午 2：申诉下午 3：申诉上下午 */
    private String appealRecord;

    /** 审批员uid */
    private String examineUid;


    /** 考勤组人员列表 */
    private List<String> employees;

    /** 有效范围(外勤) */
    private Integer attendanceOutRange;
    /**
     * 考勤组类型 1：固定班 2：排班制 3：自由班
     */
    private Integer attendType;

    /** 是否按法定节假日处理打卡 */
    private Integer relyHoliday;

    /** 考勤组用户允许迟到时长，单位:分钟 */
    private Integer allowLateTime;

    /**
     * 固定班规则
     * 该规则只有固定班有值，其他考勤组类型的考勤组此值为空
     * 规则具体采用json格式存储，取出来需进行解析：
     * 采用{"1":{"amTime":"08:00-11:30","pmTime":"13:00-17:30"}}
     * 则表示周一，以此类推周二、三等，
     *  */
    private String fixedAttendRule;

    /**
     * 自由班规则
     * 该规则只有自由班有值，其他考勤组类型的考勤组此值为空
     * 规则具体采用json格式存储，取出来需进行解析：采用{"1":"0","2":"1","3":"0","4":"1","5":"0","6":"1","7":"1"}
     * 表示周一需要不打卡，周二需要打卡 */
    private String freeAttendRule;

    public String getFixedAttendRule() {
        return fixedAttendRule;
    }

    public void setFixedAttendRule(String fixedAttendRule) {
        this.fixedAttendRule = fixedAttendRule;
    }

    public String getFreeAttendRule() {
        return freeAttendRule;
    }

    public void setFreeAttendRule(String freeAttendRule) {
        this.freeAttendRule = freeAttendRule;
    }

    public Integer getAmPmStatue() {
        return amPmStatue;
    }

    public void setAmPmStatue(Integer amPmStatue) {
        this.amPmStatue = amPmStatue;
    }

    public Integer getGoRegionStatus() {
        return goRegionStatus;
    }

    public void setGoRegionStatus(Integer goRegionStatus) {
        this.goRegionStatus = goRegionStatus;
    }

    public Integer getLeaveRegionStatus() {
        return leaveRegionStatus;
    }

    public void setLeaveRegionStatus(Integer leaveRegionStatus) {
        this.leaveRegionStatus = leaveRegionStatus;
    }

    public Date getGoWorkDate() {
		return goWorkDate;
	}

	public void setGoWorkDate(Date goWorkDate) {
		this.goWorkDate = goWorkDate;
	}

	public Date getLeaveWorkDate() {
		return leaveWorkDate;
	}

	public void setLeaveWorkDate(Date leaveWorkDate) {
		this.leaveWorkDate = leaveWorkDate;
	}

	public Integer getIsSchedule() {
		return isSchedule;
	}

	public void setIsSchedule(Integer isSchedule) {
		this.isSchedule = isSchedule;
	}

	public Integer getAttendType() {
		return attendType;
	}

	public void setAttendType(Integer attendType) {
		this.attendType = attendType;
	}

	public Long getScheduleShiftId() {
		return scheduleShiftId;
	}

	public void setScheduleShiftId(Long scheduleShiftId) {
		this.scheduleShiftId = scheduleShiftId;
	}

	public String getScheduleShiftName() {
		return scheduleShiftName;
	}

	public void setScheduleShiftName(String scheduleShiftName) {
		this.scheduleShiftName = scheduleShiftName;
	}

	public String getScheduleShiftWorkTime() {
		return scheduleShiftWorkTime;
	}

	public void setScheduleShiftWorkTime(String scheduleShiftWorkTime) {
		this.scheduleShiftWorkTime = scheduleShiftWorkTime;
	}

	public Integer getAttendanceState()
    {
        return attendanceState;
    }

    public void setAttendanceState(Integer attendanceState)
    {
        this.attendanceState = attendanceState;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }

    public String getAttendanceName()
    {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName)
    {
        this.attendanceName = attendanceName;
    }

    public Integer getLateMinutes()
    {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes)
    {
        this.lateMinutes = lateMinutes;
    }

    public Integer getEarlyMinutes()
    {
        return earlyMinutes;
    }

    public void setEarlyMinutes(Integer earlyMinutes)
    {
        this.earlyMinutes = earlyMinutes;
    }

    public Integer getWorkMinutes()
    {
        return workMinutes;
    }

    public void setWorkMinutes(Integer workMinutes)
    {
        this.workMinutes = workMinutes;
    }

    public Long getAppealId()
    {
        return appealId;
    }

    public void setAppealId(Long appealId)
    {
        this.appealId = appealId;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Integer getRecordState()
    {
        return recordState;
    }

    public void setRecordState(Integer recordState)
    {
        this.recordState = recordState;
    }

    public Long getMonthRcdId()
    {
        return monthRcdId;
    }

    public void setMonthRcdId(Long monthRcdId)
    {
        this.monthRcdId = monthRcdId;
    }

    public Long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId)
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

    public Date getAttendanceDate()
    {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate)
    {
        this.attendanceDate = attendanceDate;
    }

    public Date getGoWork()
    {
        return goWork;
    }

    public void setGoWork(Date goWork)
    {
        this.goWork = goWork;
    }

    public String getGoLocation()
    {
        return goLocation;
    }

    public void setGoLocation(String goLocation)
    {
        this.goLocation = goLocation;
    }

    public String getGoWorkDesc()
    {
        return goWorkDesc;
    }

    public void setGoWorkDesc(String goWorkDesc)
    {
        this.goWorkDesc = goWorkDesc;
    }

    public Date getLeaveWork()
    {
        return leaveWork;
    }

    public void setLeaveWork(Date leaveWork)
    {
        this.leaveWork = leaveWork;
    }

    public String getLeaveLocation()
    {
        return leaveLocation;
    }

    public void setLeaveLocation(String leaveLocation)
    {
        this.leaveLocation = leaveLocation;
    }

    public String getLeaveWorkDesc()
    {
        return leaveWorkDesc;
    }

    public void setLeaveWorkDesc(String leaveWorkDesc)
    {
        this.leaveWorkDesc = leaveWorkDesc;
    }

    public Integer getRegionStatus()
    {
        return regionStatus;
    }

    public void setRegionStatus(Integer regionStatus)
    {
        this.regionStatus = regionStatus;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    /**
     * 报表导出需要
     * @return
     */
    public String getGoWorkTime()
    {
        return AtdcTimeUtil.mergeDateAndTime(attendanceDate, goWork, goWorkDate);
    }

    /**
     * 报表导出需要
     * @return
     */
    public String getLeaveWorkTime()
    {
        return AtdcTimeUtil.mergeDateAndTime(attendanceDate, leaveWork,leaveWorkDate);
    }

    /**
     * 应前端要求，时间以毫秒数形式传递。返回日期
     * @return
     */
    public Long getDate()
    {
        if (AssertUtil.isEmpty(attendanceDate))
        {
            return 0L;
        }
        return attendanceDate.getTime();
    }

    /**
     * 以毫秒数返回上班时间
     * @return
     */
    public Long getGoTime()
    {
        return AtdcTimeUtil.togetherDateAndTime(attendanceDate, goWork, goWorkDate);
    }

    /**
     * 以毫秒数返回下班时间
     * @return
     */
    public Long getLeaveTime()
    {
        return AtdcTimeUtil.togetherDateAndTime(attendanceDate, leaveWork, leaveWorkDate);
    }



    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public String getAmTime() {
        return amTime;
    }

    public void setAmTime(String amTime) {
        this.amTime = amTime;
    }

    public String getPmTime() {
        return pmTime;
    }

    public void setPmTime(String pmTime) {
        this.pmTime = pmTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetailAddr() {
        return detailAddr;
    }

    public void setDetailAddr(String detailAddr) {
        this.detailAddr = detailAddr;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Integer getAttendanceRange() {
        return attendanceRange;
    }

    public void setAttendanceRange(Integer attendanceRange) {
        this.attendanceRange = attendanceRange;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getExamineName() {
        return examineName;
    }

    public void setExamineName(String examineName) {
        this.examineName = examineName;
    }

    public String getExamineUid() {
        return examineUid;
    }

    public void setExamineUid(String examineUid) {
        this.examineUid = examineUid;
    }

    public List<String> getEmployees() {
        return employees;
    }

    public void setEmployees(List<String> employees) {
        this.employees = employees;
    }

    public Integer getAttendanceOutRange() {
        return attendanceOutRange;
    }

    public void setAttendanceOutRange(Integer attendanceOutRange) {
        this.attendanceOutRange = attendanceOutRange;
    }

    public Integer getRelyHoliday() {
        return relyHoliday;
    }

    public void setRelyHoliday(Integer relyHoliday) {
        this.relyHoliday = relyHoliday;
    }

    public Integer getAllowLateTime() {
        return allowLateTime;
    }

    public void setAllowLateTime(Integer allowLateTime) {
        this.allowLateTime = allowLateTime;
    }

    public static Long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "EmployeeMonthDetail{" +
                "attendanceId=" + attendanceId +
                ", enterId='" + enterId + '\'' +
                ", uid='" + uid + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", attendanceDate=" + attendanceDate +
                ", goWork=" + goWork +
                ", goWorkDate=" + goWorkDate +
                ", goLocation='" + goLocation + '\'' +
                ", goWorkDesc='" + goWorkDesc + '\'' +
                ", leaveWork=" + leaveWork +
                ", leaveWorkDate=" + leaveWorkDate +
                ", leaveLocation='" + leaveLocation + '\'' +
                ", leaveWorkDesc='" + leaveWorkDesc + '\'' +
                ", regionStatus=" + regionStatus +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", monthRcdId=" + monthRcdId +
                ", recordState=" + recordState +
                ", phone='" + phone + '\'' +
                ", appealId=" + appealId +
                ", lateMinutes=" + lateMinutes +
                ", earlyMinutes=" + earlyMinutes +
                ", workMinutes=" + workMinutes +
                ", attendanceName='" + attendanceName + '\'' +
                ", count=" + count +
                ", attendanceState=" + attendanceState +
                ", attendType=" + attendType +
                ", scheduleShiftId=" + scheduleShiftId +
                ", scheduleShiftName='" + scheduleShiftName + '\'' +
                ", scheduleShiftWorkTime='" + scheduleShiftWorkTime + '\'' +
                ", isSchedule=" + isSchedule +
                ", goRegionStatus=" + goRegionStatus +
                ", leaveRegionStatus=" + leaveRegionStatus +
                ", amPmStatue=" + amPmStatue +
                '}';
    }
}

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

/**
 * 功能描述：员工个人月报明细实体类对象
 * 
 */
public class EmployeeMonthDetail extends BeanObject implements Serializable
{

    private static final Long serialVersionUID = 2957370342152267792L;

    /** 考勤组ID */
    private Long attendanceId;

    /** 企业ID */
    private String enterId;

    /** 企业联系人ID */
    private String uid;

    /** 企业通讯录ID */
    private String contactId;

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

    /** 考勤组名称 */
    private String attendanceName;

    /** 查询报表分项数据时所需，表示用户未打卡次数 */
    private Integer count;

    /** 未打卡状态标识 1：上午未打卡 2：下午未打卡 3：上、下午未打卡 */
    private Integer attendanceState;
    
    /** 考勤组类型 1：固定班 2：排班制 3：自由班 */
    private Integer attendType;
    
    /** 班次ID */
    private Long scheduleShiftId;
    
    /** 班次名称 */
    private String scheduleShiftName;
    
    /** 班次上下班时间 */
    private String scheduleShiftWorkTime;
    /** 是否排班 0:休息，1：是*/
    private Integer isSchedule;

    /**
     *增加每一个打卡的外勤情况
     */
    /** 上班0打卡公司打卡  1外勤打卡 */
    private Integer goRegionStatus;
    /** 下班0打卡公司打卡  1外勤打卡 */
    private Integer leaveRegionStatus;
    /** 上下班打卡不是0 上班 1 下班 */
    private Integer amPmStatue;

    private String outWorkRemark;
    /**部门名称*/
    private String deptName;

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

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
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

    /*@Override
    public String toString()
    {
        return "EmployeeMonthDetail [attendanceId=" + attendanceId
            + ", enterId=" + enterId + ", uid=" + uid + ", employeeName="
            + employeeName + ", attendanceDate=" + attendanceDate + ", goWork="
            + goWork + ", goLocation=" + goLocation + ", goWorkDesc="
            + goWorkDesc + ", leaveWork=" + leaveWork + ", leaveLocation="
            + leaveLocation + ", leaveWorkDesc=" + leaveWorkDesc
            + ", regionStatus=" + regionStatus + ", remark=" + remark
            + ", createTime=" + createTime + ", monthRcdId=" + monthRcdId
            + ", recordState=" + recordState + ", phone=" + phone
            + ", appealId=" + appealId + ", lateMinutes=" + lateMinutes
            + ", earlyMinutes=" + earlyMinutes + ", workMinutes=" + workMinutes
            + ", attendanceName=" + attendanceName + ", count=" + count
            + ", attendanceState=" + attendanceState + "]";
    }*/

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

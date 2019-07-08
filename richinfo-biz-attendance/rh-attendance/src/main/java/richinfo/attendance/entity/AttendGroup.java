/**
 * 文件名：AttendGroup.java
 * 创建日期： 2017年6月5日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.entity;

import richinfo.attendance.common.BeanObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：考勤组实体类 attendance_group_info
 * 
 */
public class AttendGroup extends BeanObject implements Serializable
{

    private static final long serialVersionUID = 7083207089334938832L;

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

    /** 有效范围 (内勤) */
    private int attendanceRange;

    /** 考勤组状态 */
    private int status = GroupStatus.Normal.getValue();

    /** 考勤组创建者uid */
    private String adminUid;

    /*创建者contactId*/
    private String adminContactId;

    /*创建者名称*/
    private String adminName;

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

    /** 考勤组人员列表 */
    private List<String> employees;

    /** 有效范围(外勤) */
    private int attendanceOutRange;
    /**
     * 考勤组类型 1：固定班 2：排班制 3：自由班
     */
    private int attendType;
    
    /** 是否按法定节假日处理打卡 */
    private int relyHoliday;
    
    /** 考勤组用户允许迟到时长，单位:分钟 */
    private int allowLateTime;
    
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

    /** 考勤组负责人列表 */
    private List<UserInfo> chargemanList;

    public List<UserInfo> getChargemanList() {
        return chargemanList;
    }

    public void setChargemanList(List<UserInfo> chargemanList) {
        this.chargemanList = chargemanList;
    }

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

    public String getExamineContactId() {
        return examineContactId;
    }

    public void setExamineContactId(String examineContactId) {
        this.examineContactId = examineContactId;
    }

    public String getAdminContactId() {
        return adminContactId;
    }

    public void setAdminContactId(String adminContactId) {
        this.adminContactId = adminContactId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public int getAttendType() {
		return attendType;
	}

	public void setAttendType(int attendType) {
		this.attendType = attendType;
	}

	public int getRelyHoliday() {
		return relyHoliday;
	}

	public void setRelyHoliday(int relyHoliday) {
		this.relyHoliday = relyHoliday;
	}

	public int getAllowLateTime() {
		return allowLateTime;
	}

	public void setAllowLateTime(int allowLateTime) {
		this.allowLateTime = allowLateTime;
	}

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

    public String getAdminUid()
    {
        return adminUid;
    }

    public void setAdminUid(String adminUid)
    {
        this.adminUid = adminUid;
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

    public List<String> getEmployees()
    {
        return employees;
    }

    public void setEmployees(List<String> employees)
    {
        this.employees = employees;
    }

    public enum GroupStatus {
        // 正常 //异常/白名单 //未知
        Normal(0), Abnormal(1),Unknow(-1);

        private int value;

        private GroupStatus(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static GroupStatus parse(int value)
        {
            switch (value)
            {
            case 0:
                return Normal;
            case 1:
                return Abnormal;
            default:
                return Unknow;
            }
        }
    }

    public enum AttendType {
        // 固定班 排班 自由班
        Fix(1), Schedule(2), Free(3);

        private int value;

        private AttendType(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static AttendType parse(int value)
        {
            switch (value)
            {
            case 1:
                return Fix;
            case 2:
                return Schedule;
            default:
                return Free;
            }
        }
    }
    
    
    public enum RelyHoliday {
        //节假日不需打卡 //节假日需打卡
    	NotRely(0), Rely(1);

        private int value;

        private RelyHoliday(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static RelyHoliday parse(int value)
        {
            switch (value)
            {
            case 0:
                return NotRely;
            case 1:
                return Rely;
            default:
                return Rely;
            }
        }
    }

    @Override
    public String toString() {
        return "AttendGroup{" +
            "attendanceId=" + attendanceId +
            ", attendanceName='" + attendanceName + '\'' +
            ", enterId='" + enterId + '\'' +
            ", enterName='" + enterName + '\'' +
            ", amTime='" + amTime + '\'' +
            ", pmTime='" + pmTime + '\'' +
            ", location='" + location + '\'' +
            ", detailAddr='" + detailAddr + '\'' +
            ", longitude=" + longitude +
            ", latitude=" + latitude +
            ", attendanceRange=" + attendanceRange +
            ", status=" + status +
            ", adminUid='" + adminUid + '\'' +
            ", adminContactId='" + adminContactId + '\'' +
            ", adminName='" + adminName + '\'' +
            ", createTime=" + createTime +
            ", modifyTime=" + modifyTime +
            ", examineName='" + examineName + '\'' +
            ", examineUid='" + examineUid + '\'' +
            ", examineContactId='" + examineContactId + '\'' +
            ", employees=" + employees +
            ", attendanceOutRange=" + attendanceOutRange +
            ", attendType=" + attendType +
            ", relyHoliday=" + relyHoliday +
            ", allowLateTime=" + allowLateTime +
            ", fixedAttendRule='" + fixedAttendRule + '\'' +
            ", freeAttendRule='" + freeAttendRule + '\'' +
            ", chargemanList=" + chargemanList +
            ", roleType=" + roleType +
            ", isAllowedOutRangeClock=" + isAllowedOutRangeClock +
            ", useFlexibleRule=" + useFlexibleRule +
            ", flexitime=" + flexitime +
            '}';
    }

    public String toSimpleString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendGroup [attendanceId=").append(attendanceId)
            .append(", attendanceName=").append(attendanceName)
            .append(", attendType=").append(attendType)
            .append(", fixedAttendRule=").append(fixedAttendRule)
            .append(", freeAttendRule=").append(freeAttendRule)
            .append(", allowLateTime=").append(allowLateTime)
            .append(", relyHoliday=").append(relyHoliday).append("]");
        return builder.toString();
    }

}

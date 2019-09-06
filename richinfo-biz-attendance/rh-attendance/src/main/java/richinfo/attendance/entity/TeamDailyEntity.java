/**
 * 文件名：TeamDailyEntity.java
 * 创建日期： 2017年6月8日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.entity;

import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.TimeUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：团队日报详情实体类
 *
 */
public class TeamDailyEntity implements Serializable
{
    private static final long serialVersionUID = -8293338015064069418L;

    /** 分页显示，默认第一页 */
    private int pageNo = 1;
    /** 分页每页显示数据量，默认20条 */
    private int pageSize = 20;
    /** 分页查询偏移量 */
    private int offSet;
    /** 数据总量 */
    private long totalCount;
    /** 数据总页数 */
    private int totalSize;
    /** 日报查询日期 */
    private String attendanceDate;
    /** 企业Id */
    private String enterId;
    /** 考勤组名称 */
    private String attendanceName;
    /** 考勤组id */
    private long attendanceId;
    /** 考勤人员名称 */
    private String employeeName;
    /** 最早打卡时间 */
    private Date earlyTime;
    /** 最早打卡日期 */
    private Date earlyDate;
    /** 最早打卡时间描述 */
    private String earlyTimeDesc;
    /** 最晚打卡时间 */
    private Date lastTime;
    /** 最晚打卡日期 */
    private Date lastDate;
    /** 最晚打卡时间描述 */
    private String lastTimeDesc;
    /** 最早打卡时间考勤地点 */
    private String earlyTimeLocation;
    /** 最晚打卡时间考勤地点 */
    private String lastTimeLocation;
    /** 用户手机号 */
    private String phone;
    /**节假日描述*/
    private String remark;
    /**外勤描述*/
    private int regionStatus;
    /**通讯录id*/
    private String contactId;
    /** 企业联系人ID */
    private String uid;
    /** 部门名 */
    private String deptName;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public int getRegionStatus() {
        return regionStatus;
    }

    public void setRegionStatus(int regionStatus) {
        this.regionStatus = regionStatus;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getEarlyDate() {
		return earlyDate;
	}

	public void setEarlyDate(Date earlyDate) {
		this.earlyDate = earlyDate;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getEarlyTimeDesc()
    {
        return earlyTimeDesc;
    }

    public void setEarlyTimeDesc(String earlyTimeDesc)
    {
        this.earlyTimeDesc = earlyTimeDesc;
    }

    public String getLastTimeDesc()
    {
        return lastTimeDesc;
    }

    public void setLastTimeDesc(String lastTimeDesc)
    {
        this.lastTimeDesc = lastTimeDesc;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public void setTotalSize(int totalSize)
    {
        this.totalSize = totalSize;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public long getEarlyTimes()
    {
        if (AssertUtil.isEmpty(earlyTime))
        {
            return 0L;
        }

        String attendanceTimeStr = TimeUtil.date2String(earlyTime, "HH:mm:ss");
        String times = null;
        if(AssertUtil.isNotEmpty(earlyDate)){
        	String earlyDateStr = TimeUtil.date2String(earlyDate, "yyyy-MM-dd");
        	times = earlyDateStr + " " + attendanceTimeStr;
        }else{
        	times = attendanceDate + " " + attendanceTimeStr;
        }
        long tt = TimeUtil.string2Date(times, TimeUtil.BASE_DATETIME_FORMAT)
                .getTime();
        return tt;
    }

    public long getLastTimes()
    {
        if (AssertUtil.isEmpty(lastTime))
        {
            return 0L;
        }
        String attendanceTimeStr = TimeUtil.date2String(lastTime, "HH:mm:ss");
        String times = null;
        if(AssertUtil.isNotEmpty(lastDate)){
        	String lastDateStr = TimeUtil.date2String(lastDate, "yyyy-MM-dd");
        	times = lastDateStr + " " + attendanceTimeStr;
        }else{
        	times = attendanceDate + " " + attendanceTimeStr;
        }
       
        long tt = TimeUtil.string2Date(times, TimeUtil.BASE_DATETIME_FORMAT)
            .getTime();

        return tt;
    }

    public int getOffSet()
    {
        return offSet;
    }

    public void setOffSet(int offSet)
    {
        this.offSet = offSet;
    }

    public int getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    public String getAttendanceDate()
    {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate)
    {
        this.attendanceDate = attendanceDate;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
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

    public Date getEarlyTime()
    {
        return earlyTime;
    }

    public void setEarlyTime(Date earlyTime)
    {
        this.earlyTime = earlyTime;
    }

    public Date getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(Date lastTime)
    {
        this.lastTime = lastTime;
    }

    public String getEarlyTimeLocation()
    {
        return earlyTimeLocation;
    }

    public void setEarlyTimeLocation(String earlyTimeLocation)
    {
        this.earlyTimeLocation = earlyTimeLocation;
    }

    public String getLastTimeLocation()
    {
        return lastTimeLocation;
    }

    public void setLastTimeLocation(String lastTimeLocation)
    {
        this.lastTimeLocation = lastTimeLocation;
    }

    @Override
    public String toString()
    {
        return "TeamDailyEntity [pageNo=" + pageNo + ", pageSize=" + pageSize
            + ", offSet=" + offSet + ", totalCount=" + totalCount
            + ", totalSize=" + totalSize + ", attendanceDate=" + attendanceDate
            + ", enterId=" + enterId + ", attendanceName=" + attendanceName
            + ", attendanceId=" + attendanceId + ", employeeName="
            + employeeName + ", earlyTime=" + earlyTime + ", earlyTimeDesc="
            + earlyTimeDesc + ", lastTime=" + lastTime + ", lastTimeDesc="
            + lastTimeDesc + ", earlyTimeLocation=" + earlyTimeLocation
            + ", lastTimeLocation=" + lastTimeLocation + ", phone=" + phone
            + "]";
    }
}

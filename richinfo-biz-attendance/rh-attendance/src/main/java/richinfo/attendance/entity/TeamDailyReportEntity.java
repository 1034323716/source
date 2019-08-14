/**
 * 文件名：TeamDailyReportEntity.java
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

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：团队日报统计实体类
 *
 */
public class TeamDailyReportEntity implements Serializable
{
    private static final Long serialVersionUID = -2285752830260503155L;

    /** 企业Id */
    private String enterId;

    /** 考勤组Id */
    private Long attendanceId;

    /** 考勤日期 */
    private Date attendanceDate;

    /** 正常考勤人数 */
    private Integer normalCount;

    /** 外勤人数 */
    private Integer outsideCount;

    /** 迟到人数 */
    private Integer lateCount;

    /** 早退人数 */
    private Integer earlyCount;

    /** 未打卡人数 */
    private Integer notClockedCount;

    /*上班未打卡人数*/
    private Integer goNotClockedCount;

    /*下班未打卡人数*/
    private Integer leaveNotClockedCount;

    /** 已申诉人数 */
    private Integer appealCount;

    /** 创建时间 */
    private Date createTime;

    public Integer getGoNotClockedCount() {
        return goNotClockedCount;
    }

    public void setGoNotClockedCount(Integer goNotClockedCount) {
        this.goNotClockedCount = goNotClockedCount;
    }

    public Integer getLeaveNotClockedCount() {
        return leaveNotClockedCount;
    }

    public void setLeaveNotClockedCount(Integer leaveNotClockedCount) {
        this.leaveNotClockedCount = leaveNotClockedCount;
    }

    public Integer getAppealCount()
    {
        return appealCount;
    }

    public void setAppealCount(Integer appealCount)
    {
        this.appealCount = appealCount;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public Long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId)
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

    public Integer getNormalCount()
    {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount)
    {
        this.normalCount = normalCount;
    }

    public Integer getOutsideCount()
    {
        return outsideCount;
    }

    public void setOutsideCount(Integer outsideCount)
    {
        this.outsideCount = outsideCount;
    }

    public Integer getLateCount()
    {
        return lateCount;
    }

    public void setLateCount(Integer lateCount)
    {
        this.lateCount = lateCount;
    }

    public Integer getEarlyCount()
    {
        return earlyCount;
    }

    public void setEarlyCount(Integer earlyCount)
    {
        this.earlyCount = earlyCount;
    }

    public Integer getNotClockedCount()
    {
        return notClockedCount;
    }

    public void setNotClockedCount(Integer notClockedCount)
    {
        this.notClockedCount = notClockedCount;
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
        return "TeamDailyReportEntity{" +
            "enterId='" + enterId + '\'' +
            ", attendanceId=" + attendanceId +
            ", attendanceDate=" + attendanceDate +
            ", normalCount=" + normalCount +
            ", outsideCount=" + outsideCount +
            ", lateCount=" + lateCount +
            ", earlyCount=" + earlyCount +
            ", notClockedCount=" + notClockedCount +
            ", goNotClockedCount=" + goNotClockedCount +
            ", leaveNotClockedCount=" + leaveNotClockedCount +
            ", appealCount=" + appealCount +
            ", createTime=" + createTime +
            '}';
    }
}

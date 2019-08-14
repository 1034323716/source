/**
 * 文件名：AttendCalendar.java
 * 创建日期： 2017年6月8日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

import richinfo.attendance.common.BeanObject;

/**
 * 功能描述：考勤公共日历实体类
 * 
 */
public class AttendCalendar extends BeanObject implements Serializable
{

    private static final long serialVersionUID = -5126977909134695453L;

    /** 日历记录ID */
    private long calendarId;

    /** 日历日期 */
    private Date calendarDate;

    /** 星期 */
    private String week;

    /** 日期备注，如：工作日、节假日 */
    private String remark;

    /** 工作日标识，0：工作日,1:非工作日，默认工作日。 */
    private int status = CalendarStatus.Weekday.getValue();

    /** 创建时间，即，日历数据的录入时间 */
    private Date createTime;

    public long getCalendarId()
    {
        return calendarId;
    }

    public void setCalendarId(long calendarId)
    {
        this.calendarId = calendarId;
    }

    public Date getCalendarDate()
    {
        return calendarDate;
    }

    public void setCalendarDate(Date calendarDate)
    {
        this.calendarDate = calendarDate;
    }

    public String getWeek()
    {
        return week;
    }

    public void setWeek(String week)
    {
        this.week = week;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    /**
     * 是否为工作日
     * @return
     */
    public boolean isWeekDay()
    {
        return this.status == CalendarStatus.Weekday.getValue();
    }

    /**
     * 
     * 功能描述：工作日/节假日状态
     * 
     */
    public enum CalendarStatus {
        // 工作日 // 非工作日 // 未知
        Weekday(0), Holiday(1), Unknow(-1);

        private int value;

        private CalendarStatus(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static CalendarStatus parse(int value)
        {
            switch (value)
            {
            case 0:
                return Weekday;
            case 1:
                return Holiday;
            default:
                return Unknow;
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("AttendCalendar[id=").append(calendarId).append(",date=")
            .append(calendarDate).append(",week=").append(week)
            .append(",remark=").append(remark).append(",status=")
            .append(status).append(",createTime=").append(createTime)
            .append("]");
        return sb.toString();
    }
}

/**
 * 文件名：AttendanceScheduleShift.java
 * 创建日期： 2018年3月29日
 * 作者：     jiangshengsheng
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 *  1.修改时间：2018年3月29日
 *   修改人：jiangshengsheng
 *   修改内容：
 */
package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

import richinfo.attendance.common.BeanObject;

/**
 * 
 * 功能描述：考勤组排班班次实体类 attendance_schedule_shift
 *
 */
public class AttendanceScheduleShift extends BeanObject implements Serializable
{

    private static final long serialVersionUID = 3833453071772015321L;
    /** 班次ID */
    private Long scheduleShiftId;
    /** 考勤组ID */
    private Long attendanceId;
    /** 班次名称 */
    private String scheduleShiftName;
    /** 班次上下班时间 */
    private String workTime;
    /** 班次状态：0：正常 */
    private Integer status;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date modifyTime;

    public Long getScheduleShiftId()
    {
        return scheduleShiftId;
    }

    public void setScheduleShiftId(Long scheduleShiftId)
    {
        this.scheduleShiftId = scheduleShiftId;
    }

    public Long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getScheduleShiftName()
    {
        return scheduleShiftName;
    }

    public void setScheduleShiftName(String scheduleShiftName)
    {
       this.scheduleShiftName = scheduleShiftName == null ? null
            : scheduleShiftName.trim();
    }

    public String getWorkTime()
    {
        return workTime;
    }

    public void setWorkTime(String workTime)
    {
        this.workTime = workTime == null ? null : workTime.trim();
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
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
    
    public enum ShiftStatus {
        // 正常 //异常 //未知
        Normal(0), Abnormal(1), Unknow(-1);

        private int value;

        private ShiftStatus(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static ShiftStatus parse(int value)
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

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendanceScheduleShift [scheduleShiftId=")
            .append(scheduleShiftId).append(", attendanceId=")
            .append(attendanceId).append(", scheduleShiftName=")
            .append(scheduleShiftName).append(", workTime=").append(workTime)
            .append(", status=").append(status).append(", createTime=")
            .append(createTime).append(", modifyTime=").append(modifyTime)
            .append("]");
        return builder.toString();
    }
    

}
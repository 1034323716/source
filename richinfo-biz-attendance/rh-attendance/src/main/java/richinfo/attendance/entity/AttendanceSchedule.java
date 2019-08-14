/**
 * 文件名：AttendanceSchedule.java
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

import richinfo.attendance.entity.AttendanceScheduleShift.ShiftStatus;
import richinfo.attendance.common.BeanObject;

/**
 * 
 * 功能描述：考勤组排班实体类 attendance_schedule
 *
 */
public class AttendanceSchedule extends BeanObject implements Serializable
{

    private static final long serialVersionUID = 7813039409730754910L;
    /** 排班ID */
    private Long scheduleId;
    /** 考勤组ID */
    private Long attendanceId;
    /** 考勤打卡规则月份 */
    private String attendMonth;
    /** 用户唯一标识uid */
    private String uid;
    /** 员工名字 */
    private String employeeName;
    /** 1号的班次ID（为0，则表示休息） */
    private Long day1;
    /** 2号的班次ID */
    private Long day2;
    /** 3号考勤组ID */
    private Long day3;
    /** 4号考勤组ID */
    private Long day4;
    /** 5号考勤组ID */
    private Long day5;
    /** 6号考勤组ID */
    private Long day6;
    /** 7号考勤组ID */
    private Long day7;
    /** 8号考勤组ID */
    private Long day8;
    /** 9号考勤组ID */
    private Long day9;
    /** 10号考勤组ID */
    private Long day10;
    /** 11号考勤组ID */
    private Long day11;
    /** 12号考勤组ID */
    private Long day12;
    /** 13号考勤组ID */
    private Long day13;
    /** 14号考勤组ID */
    private Long day14;
    /** 15号考勤组ID */
    private Long day15;
    /** 16号考勤组ID */
    private Long day16;
    /** 17号考勤组ID */
    private Long day17;
    /** 18号考勤组ID */
    private Long day18;
    /** 19号考勤组ID */
    private Long day19;
    /** 20号考勤组ID */
    private Long day20;
    /** 21号考勤组ID */
    private Long day21;
    /** 22号考勤组ID */
    private Long day22;
    /** 23号考勤组ID */
    private Long day23;
    /** 24号考勤组ID */
    private Long day24;
    /** 25号考勤组ID */
    private Long day25;
    /** 26号考勤组ID */
    private Long day26;
    /** 27号考勤组ID */
    private Long day27;
    /** 28号考勤组ID */
    private Long day28;
    /** 29号考勤组ID */
    private Long day29;
    /** 30号考勤组ID */
    private Long day30;
    /** 31号考勤组ID */
    private Long day31;
    /** 排班状态 0：正常 */
    private Integer status;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date modifyTime;

    public Long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public Long getScheduleId()
    {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public String getAttendMonth()
    {
        return attendMonth;
    }

    public void setAttendMonth(String attendMonth)
    {
        this.attendMonth = attendMonth == null ? null : attendMonth.trim();
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    public void setEmployeeName(String employeeName)
    {
        this.employeeName = employeeName == null ? null : employeeName.trim();
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Long getDay1()
    {
        return day1;
    }

    public void setDay1(Long day1)
    {
        this.day1 = day1;
    }

    public Long getDay2()
    {
        return day2;
    }

    public void setDay2(Long day2)
    {
        this.day2 = day2;
    }

    public Long getDay3()
    {
        return day3;
    }

    public void setDay3(Long day3)
    {
        this.day3 = day3;
    }

    public Long getDay4()
    {
        return day4;
    }

    public void setDay4(Long day4)
    {
        this.day4 = day4;
    }

    public Long getDay5()
    {
        return day5;
    }

    public void setDay5(Long day5)
    {
        this.day5 = day5;
    }

    public Long getDay6()
    {
        return day6;
    }

    public void setDay6(Long day6)
    {
        this.day6 = day6;
    }

    public Long getDay7()
    {
        return day7;
    }

    public void setDay7(Long day7)
    {
        this.day7 = day7;
    }

    public Long getDay8()
    {
        return day8;
    }

    public void setDay8(Long day8)
    {
        this.day8 = day8;
    }

    public Long getDay9()
    {
        return day9;
    }

    public void setDay9(Long day9)
    {
        this.day9 = day9;
    }

    public Long getDay10()
    {
        return day10;
    }

    public void setDay10(Long day10)
    {
        this.day10 = day10;
    }

    public Long getDay11()
    {
        return day11;
    }

    public void setDay11(Long day11)
    {
        this.day11 = day11;
    }

    public Long getDay12()
    {
        return day12;
    }

    public void setDay12(Long day12)
    {
        this.day12 = day12;
    }

    public Long getDay13()
    {
        return day13;
    }

    public void setDay13(Long day13)
    {
        this.day13 = day13;
    }

    public Long getDay14()
    {
        return day14;
    }

    public void setDay14(Long day14)
    {
        this.day14 = day14;
    }

    public Long getDay15()
    {
        return day15;
    }

    public void setDay15(Long day15)
    {
        this.day15 = day15;
    }

    public Long getDay16()
    {
        return day16;
    }

    public void setDay16(Long day16)
    {
        this.day16 = day16;
    }

    public Long getDay17()
    {
        return day17;
    }

    public void setDay17(Long day17)
    {
        this.day17 = day17;
    }

    public Long getDay18()
    {
        return day18;
    }

    public void setDay18(Long day18)
    {
        this.day18 = day18;
    }

    public Long getDay19()
    {
        return day19;
    }

    public void setDay19(Long day19)
    {
        this.day19 = day19;
    }

    public Long getDay20()
    {
        return day20;
    }

    public void setDay20(Long day20)
    {
        this.day20 = day20;
    }

    public Long getDay21()
    {
        return day21;
    }

    public void setDay21(Long day21)
    {
        this.day21 = day21;
    }

    public Long getDay22()
    {
        return day22;
    }

    public void setDay22(Long day22)
    {
        this.day22 = day22;
    }

    public Long getDay23()
    {
        return day23;
    }

    public void setDay23(Long day23)
    {
        this.day23 = day23;
    }

    public Long getDay24()
    {
        return day24;
    }

    public void setDay24(Long day24)
    {
        this.day24 = day24;
    }

    public Long getDay25()
    {
        return day25;
    }

    public void setDay25(Long day25)
    {
        this.day25 = day25;
    }

    public Long getDay26()
    {
        return day26;
    }

    public void setDay26(Long day26)
    {
        this.day26 = day26;
    }

    public Long getDay27()
    {
        return day27;
    }

    public void setDay27(Long day27)
    {
        this.day27 = day27;
    }

    public Long getDay28()
    {
        return day28;
    }

    public void setDay28(Long day28)
    {
        this.day28 = day28;
    }

    public Long getDay29()
    {
        return day29;
    }

    public void setDay29(Long day29)
    {
        this.day29 = day29;
    }

    public Long getDay30()
    {
        return day30;
    }

    public void setDay30(Long day30)
    {
        this.day30 = day30;
    }

    public Long getDay31()
    {
        return day31;
    }

    public void setDay31(Long day31)
    {
        this.day31 = day31;
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
    
    public Long getDay(int dayNum)
    {
        switch (dayNum)
        {
        case 1:
            return day1;
        case 2:
            return day2;
        case 3:
            return day3;
        case 4:
            return day4;
        case 5:
            return day5;
        case 6:
            return day6;
        case 7:
            return day7;
        case 8:
            return day8;
        case 9:
            return day9;
        case 10:
            return day10;
        case 11:
            return day11;
        case 12:
            return day12;
        case 13:
            return day13;
        case 14:
            return day14;
        case 15:
            return day15;
        case 16:
            return day16;
        case 17:
            return day17;
        case 18:
            return day18;
        case 19:
            return day19;
        case 20:
            return day20;
        case 21:
            return day21;
        case 22:
            return day22;
        case 23:
            return day23;
        case 24:
            return day24;
        case 25:
            return day25;
        case 26:
            return day26;
        case 27:
            return day27;
        case 28:
            return day28;
        case 29:
            return day29;
        case 30:
            return day30;
        case 31:
            return day31;
        default:
            return 0L;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendanceSchedule [scheduleId=").append(scheduleId)
            .append(", attendanceId=").append(attendanceId)
            .append(", attendMonth=").append(attendMonth).append(", uid=")
            .append(uid).append(", employeeName=").append(employeeName)
            .append(", day1=").append(day1).append(", day2=").append(day2)
            .append(", day3=").append(day3).append(", day4=").append(day4)
            .append(", day5=").append(day5).append(", day6=").append(day6)
            .append(", day7=").append(day7).append(", day8=").append(day8)
            .append(", day9=").append(day9).append(", day10=").append(day10)
            .append(", day11=").append(day11).append(", day12=").append(day12)
            .append(", day13=").append(day13).append(", day14=").append(day14)
            .append(", day15=").append(day15).append(", day16=").append(day16)
            .append(", day17=").append(day17).append(", day18=").append(day18)
            .append(", day19=").append(day19).append(", day20=").append(day20)
            .append(", day21=").append(day21).append(", day22=").append(day22)
            .append(", day23=").append(day23).append(", day24=").append(day24)
            .append(", day25=").append(day25).append(", day26=").append(day26)
            .append(", day27=").append(day27).append(", day28=").append(day28)
            .append(", day29=").append(day29).append(", day30=").append(day30)
            .append(", day31=").append(day31).append(", status=")
            .append(status).append(", createTime=").append(createTime)
            .append(", modifyTime=").append(modifyTime).append("]");
        return builder.toString();
    }

    public enum Status {
        // 正常 //无效 //未知
        Normal(0), Deleted(1), Unknow(-1);

        private int value;

        private Status(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static Status parse(int value)
        {
            switch (value)
            {
            case 0:
                return Normal;
            case 1:
                return Deleted;
            default:
                return Unknow;
            }
        }
    }
}
/**
 * 文件名：AttendReptEmpMonth.java
 * 创建日期： 2018年1月25日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月25日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import java.util.List;

import richinfo.attendance.entity.EmployeeMonthDetail;
import richinfo.attendance.common.ResBean;

/**
 * 功能描述：团队日报个人月列表考勤数据统计
 *
 */
public class AttendReptEmpMonthRes extends ResBean
{
    private static final long serialVersionUID = -4548297879031747580L;

    /** 正常次数 */
    private int normalCount;
    /** 早退次数 */
    private int earlyCount;
    /** 早退总分钟数 */
    private int earlyTotalMinutes;
    /** 迟到次数 */
    private int lateCount;
    /** 迟到总分钟数 */
    private int lateTotalMinutes;
    /** 外勤次数 */
    private int outSideCount;
    /** 未打卡次数 */
    private int notClockedCount;
    /** 考勤组名称 */
    private String attendanceName;

    /** 早退详细信息 */
    private List<EmployeeMonthDetail> earlyList;

    /** 迟到详细信息 */
    private List<EmployeeMonthDetail> lateList;

    /** 未打卡详细信息 */
    private List<EmployeeMonthDetail> notClockedList;

    public String getAttendanceName()
    {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName)
    {
        this.attendanceName = attendanceName;
    }

    public int getNormalCount()
    {
        return normalCount;
    }

    public void setNormalCount(int normalCount)
    {
        this.normalCount = normalCount;
    }

    public int getEarlyCount()
    {
        return earlyCount;
    }

    public void setEarlyCount(int earlyCount)
    {
        this.earlyCount = earlyCount;
    }

    public int getEarlyTotalMinutes()
    {
        return earlyTotalMinutes;
    }

    public void setEarlyTotalMinutes(int earlyTotalMinutes)
    {
        this.earlyTotalMinutes = earlyTotalMinutes;
    }

    public int getLateCount()
    {
        return lateCount;
    }

    public void setLateCount(int lateCount)
    {
        this.lateCount = lateCount;
    }

    public int getLateTotalMinutes()
    {
        return lateTotalMinutes;
    }

    public void setLateTotalMinutes(int lateTotalMinutes)
    {
        this.lateTotalMinutes = lateTotalMinutes;
    }

    public int getOutSideCount()
    {
        return outSideCount;
    }

    public void setOutSideCount(int outSideCount)
    {
        this.outSideCount = outSideCount;
    }

    public int getNotClockedCount()
    {
        return notClockedCount;
    }

    public void setNotClockedCount(int notClockedCount)
    {
        this.notClockedCount = notClockedCount;
    }

    public List<EmployeeMonthDetail> getEarlyList()
    {
        return earlyList;
    }

    public void setEarlyList(List<EmployeeMonthDetail> earlyList)
    {
        this.earlyList = earlyList;
    }

    public List<EmployeeMonthDetail> getLateList()
    {
        return lateList;
    }

    public void setLateList(List<EmployeeMonthDetail> lateList)
    {
        this.lateList = lateList;
    }

    public List<EmployeeMonthDetail> getNotClockedList()
    {
        return notClockedList;
    }

    public void setNotClockedList(List<EmployeeMonthDetail> notClockedList)
    {
        this.notClockedList = notClockedList;
    }

    @Override
    public String toString()
    {
        return "AttendReptEmpMonthRes [normalCount=" + normalCount
            + ", earlyCount=" + earlyCount + ", earlyTotalMinutes="
            + earlyTotalMinutes + ", lateCount=" + lateCount
            + ", lateTotalMinutes=" + lateTotalMinutes + ", outSideCount="
            + outSideCount + ", notClockedCount=" + notClockedCount
            + ", attendanceName=" + attendanceName + ", earlyList=" + earlyList
            + ", lateList=" + lateList + ", notClockedList=" + notClockedList
            + "]";
    }
}

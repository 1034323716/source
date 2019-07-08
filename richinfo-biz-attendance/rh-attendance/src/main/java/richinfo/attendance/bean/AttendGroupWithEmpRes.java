/**
 * 文件名：AttendGroupWithEmpRes.java
 * 创建日期： 2018年4月17日
 * 作者：     JaceJiang
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年4月17日
 *   修改人：JaceJiang
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.util.AssertUtil;

import java.util.List;

/**
 * 功能描述： 考勤组列表返回bean个体 包含若干个成员名称（前三个）
 */
public class AttendGroupWithEmpRes extends AttendGroup
{

    private static final long serialVersionUID = 8670655588021067983L;

    private String employeesName;

    private String adminName;

    private String adminContactId;

    @Override
    public String getAdminName() {
        return adminName;
    }

    @Override
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    @Override
    public String getAdminContactId() {
        return adminContactId;
    }

    @Override
    public void setAdminContactId(String adminContactId) {
        this.adminContactId = adminContactId;
    }

    /*多地点集合*/
    private List<String> locations;

    private int adminned;

    public int getAdminned() {
        return adminned;
    }

    public void setAdminned(int adminned) {
        this.adminned = adminned;
    }

    /*负责人集合*/
    private List<String> chargeMans;

    public List<String> getChargeMans() {
        return chargeMans;
    }

    public void setChargeMans(List<String> chargeMans) {
        this.chargeMans = chargeMans;
    }

    public String getEmployeesName()
    {// 截取前三个
        if (AssertUtil.isNotEmpty(employeesName))
        {
            StringBuilder sb = new StringBuilder();
            String[] employeeNameArry = employeesName.split(",");
            int size = employeeNameArry.length;
            for (int i = 0; i < 4 && i < size; i++)
            {
                sb.append(employeeNameArry[i]).append(",");
            }
            employeesName = sb.subSequence(0, sb.lastIndexOf(",")).toString();
        }
        return employeesName;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public void setEmployeesName(String employeesName)
    {
        this.employeesName = employeesName;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendGroupListWithEmpRes [employeesName=")
            .append(employeesName).append(", getAttendanceOutRange()=")
            .append(getAttendanceOutRange()).append(", getExamineName()=")
            .append(getExamineName()).append(", getExamineUid()=")
            .append(getExamineUid()).append(", getAttendanceId()=")
            .append(getAttendanceId()).append(", getAttendanceName()=")
            .append(getAttendanceName()).append(", getEnterId()=")
            .append(getEnterId()).append(", getEnterName()=")
            .append(getEnterName()).append(", getAmTime()=")
            .append(getAmTime()).append(", getPmTime()=").append(getPmTime())
            .append(", getLocation()=").append(getLocation())
            .append(", getDetailAddr()=").append(getDetailAddr())
            .append(", getLongitude()=").append(getLongitude())
            .append(", getLatitude()=").append(getLatitude())
            .append(", getAttendanceRange()=").append(getAttendanceRange())
            .append(", getStatus()=").append(getStatus())
            .append(", getAdminUid()=").append(getAdminUid())
            .append(", getCreateTime()=").append(getCreateTime())
            .append(", getModifyTime()=").append(getModifyTime())
            .append(", getEmployees()=").append(getEmployees())
            .append(", getAttendType()=").append(getAttendType())
            .append(", getFixedAttendRule()=").append(getFixedAttendRule())
            .append(", getFreeAttendRule()=").append(getFreeAttendRule())
            .append(", getAllowLateTime()=").append(getAllowLateTime())
            .append(", getRelyHoliday()=").append(getRelyHoliday())
            .append(", toString()=").append(super.toString())
            .append(", getClass()=").append(getClass()).append(", hashCode()=")
            .append(hashCode()).append("]");
        return builder.toString();
    }
    // public static void main(String[] agrs){
    // String test1 = "111";
    // String test2 = "111,222,333,444,555,666";
    // AttendGroupWithEmpRes attendGroupWithEmpRes = new
    // AttendGroupWithEmpRes();
    // attendGroupWithEmpRes.setEmployeesName(test1);
    // System.out.println(attendGroupWithEmpRes.getEmployeesName());;
    // attendGroupWithEmpRes.setEmployeesName(test2);
    // System.out.println(attendGroupWithEmpRes.getEmployeesName());;
    // }
}

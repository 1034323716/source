/**
 * 文件名：AttendEmployee.java
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

/**
 * 功能描述：考勤人员实体类 attendance_employee_info
 * 
 */
public class AttendEmployee extends BeanObject implements Serializable
{

    private static final long serialVersionUID = 5101710114616031358L;

    /** 考勤人员ID */
    private long employeeId;

    /** 考勤组ID */
    private long attendanceId;

    /** 企业联系人ID */
    private String uid;
    
    /** 用户联系人contactId，对应和通讯录企业联系人contactId */
    private String contactId;

    /** 考勤员工姓名 */
    private String employeeName;

    /** 手机号 */
    private String phone;

    /** 企业ID */
    private String enterId;

    /** 企业名称 */
    private String enterName;

    /** 部门ID */
    private String deptId;

    /** 部门名称 */
    private String deptName;

    /** 邮件 */
    private String email;

    /** 职位 */
    private String position;

    /** 考勤人员标识 */
    private int status = EmployeeStatus.Normal.getValue();

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date modifyTime;

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

//    private int roleType;
//
//    public int getRoleType() {
//        return roleType;
//    }
//
//    public void setRoleType(int roleType) {
//        this.roleType = roleType;
//    }

    public long getEmployeeId()
    {
        return employeeId;
    }

    public void setEmployeeId(long employeeId)
    {
        this.employeeId = employeeId;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }
    
    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    public void setEmployeeName(String employeeName)
    {
        this.employeeName = employeeName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
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

    public String getDeptId()
    {
        return deptId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
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

    public Date getModifyTime()
    {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime)
    {
        this.modifyTime = modifyTime;
    }

    public enum EmployeeStatus {
        // 正常 //异常 //未知
        Normal(0), Abnormal(1),Whitelist(2), Unknow(-1);

        private int value;

        private EmployeeStatus(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static EmployeeStatus parse(int value)
        {
            switch (value)
            {
            case 0:
                return Normal;
            case 1:
                return Abnormal;
             case 2:
                    return Whitelist;
            default:
                return Unknow;
            }
        }
    }

    public enum EmployeeType {
        // 普通员工0 考勤组负责人1 未知-1
        NormalEmp(0), ChargeMan(1), Unknow(-1);

        private Integer value;

        private EmployeeType(Integer value)
        {
            this.value = value;
        }

        public Integer getValue()
        {
            return value;
        }

        public static EmployeeType parse(Integer value) {
            switch (value) {
                case 0:
                    return NormalEmp;
                case 1:
                    return ChargeMan;
                default:
                    return Unknow;
            }
        }
    }

    public enum EmployeeChargemanStatus {
        // 0 考勤组负责人1 未知-1
        InUse(0), NoneUse(1), Unknow(-1);

        private int value;

        private EmployeeChargemanStatus(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static EmployeeChargemanStatus parse(int value) {
            switch (value) {
                case 0:
                    return InUse;
                case 1:
                    return NoneUse;
                default:
                    return Unknow;
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("AttendEmployee[employeeId=").append(employeeId)
            .append(",attendanceId=").append(attendanceId).append(",uid=")
            .append(uid).append(",contactId=").append(contactId)
            .append(",employeeName=").append(employeeName).append(",phone=")
            .append(phone).append(",enterId=").append(enterId)
            .append(",deptName=").append(deptName).append(",status=")
            .append(status).append(",modifyTime=").append(modifyTime)
            .append("]");
        return sb.toString();
    }

}

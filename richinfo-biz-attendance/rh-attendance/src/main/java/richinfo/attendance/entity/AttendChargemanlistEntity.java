package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by qiang on 2018/9/7.
 * 白名单
 */
public class AttendChargemanlistEntity implements Serializable{
    private static final long serialVersionUID = 5101710114616031358L;

    /** 负责人ID */
    private long chargemanId ;

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

    /** 负责人状态,在用0/不用1 */
    private int status = AttendChargemanlistEntity.EmployeeStatus.Normal.getValue();

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date modifyTime;

    /** 考勤组ID*/
    private long attendanceId;

    public long getChargemanId() {
        return chargemanId;
    }

    public void setChargemanId(long chargemanId) {
        this.chargemanId = chargemanId;
    }

    public long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public enum EmployeeStatus {
        // 正常 //异常 //未知
        Normal(0), Abnormal(1), Unknow(-1);

        private int value;

        private EmployeeStatus(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static AttendChargemanlistEntity.EmployeeStatus parse(int value)
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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEnterId() {
        return enterId;
    }

    public void setEnterId(String enterId) {
        this.enterId = enterId;
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "AttendChargemanlistEntity{" +
            "chargemanId=" + chargemanId +
            ", uid='" + uid + '\'' +
            ", contactId='" + contactId + '\'' +
            ", employeeName='" + employeeName + '\'' +
            ", phone='" + phone + '\'' +
            ", enterId='" + enterId + '\'' +
            ", enterName='" + enterName + '\'' +
            ", deptId='" + deptId + '\'' +
            ", deptName='" + deptName + '\'' +
            ", email='" + email + '\'' +
            ", position='" + position + '\'' +
            ", status=" + status +
            ", createTime=" + createTime +
            ", modifyTime=" + modifyTime +
            ", attendanceId=" + attendanceId +
            '}';
    }
}

package richinfo.attendance.entity.vo;

import java.util.Date;

/**
 * Created by Daniel on 2019/3/28.
 */
public class DetailVO {
    /** 企业ID */
    private String enterId;

    /** 企业联系人ID */
    private String uid;

    /** 企业ID */
    private String contactId;

    /** 员工姓名 */
    private String employeeName;

    /** 考勤日期 */
    private Date attendanceDate;

    /** 上班打卡时间 */
    private Date goWork;

    /** 上班打卡日期 */
    private Date goWorkDate;

    /** 上班打卡地点 */
    private String goLocation;

    /** 上班考勤描述 */
    private String goWorkDesc;

    /** 下班打卡时间 */
    private Date leaveWork;

    /** 下班打卡日期 */
    private Date leaveWorkDate;

    /** 下班打卡地点 */
    private String leaveLocation;

    /** 下班考勤描述 */
    private String leaveWorkDesc;

    /** 考勤区域状态，公司打卡 or 外勤打卡, 0：公司打卡 1：外勤打卡 */
    private Integer regionStatus;

    /** 新增phone字段，便于统计报表需求 */
    private String outWorkRemark;

    /** 新增phone字段，便于统计报表需求 */
    private String phone;

    /** 创建时间 */
    private Date createTime;

    /** 企业名称 */
    private String enterName;

    /** 打卡日期备注，工作日 or 节假日 */
    private String remark;

    /** 迟到分钟数 */
    private Integer lateMinutes;

    /** 早退分钟数 */
    private Integer earlyMinutes;

    /** 工作时长 */
    private Integer workMinutes;

    /** 考勤组名称 */
    private String attendanceName;

    private Integer attendType;

    public String getEnterId() {
        return enterId;
    }

    public void setEnterId(String enterId) {
        this.enterId = enterId;
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

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Date getGoWork() {
        return goWork;
    }

    public void setGoWork(Date goWork) {
        this.goWork = goWork;
    }

    public Date getGoWorkDate() {
        return goWorkDate;
    }

    public void setGoWorkDate(Date goWorkDate) {
        this.goWorkDate = goWorkDate;
    }

    public String getGoLocation() {
        return goLocation;
    }

    public void setGoLocation(String goLocation) {
        this.goLocation = goLocation;
    }

    public String getGoWorkDesc() {
        return goWorkDesc;
    }

    public void setGoWorkDesc(String goWorkDesc) {
        this.goWorkDesc = goWorkDesc;
    }

    public Date getLeaveWork() {
        return leaveWork;
    }

    public void setLeaveWork(Date leaveWork) {
        this.leaveWork = leaveWork;
    }

    public Date getLeaveWorkDate() {
        return leaveWorkDate;
    }

    public void setLeaveWorkDate(Date leaveWorkDate) {
        this.leaveWorkDate = leaveWorkDate;
    }

    public String getLeaveLocation() {
        return leaveLocation;
    }

    public void setLeaveLocation(String leaveLocation) {
        this.leaveLocation = leaveLocation;
    }

    public String getLeaveWorkDesc() {
        return leaveWorkDesc;
    }

    public void setLeaveWorkDesc(String leaveWorkDesc) {
        this.leaveWorkDesc = leaveWorkDesc;
    }

    public Integer getRegionStatus() {
        return regionStatus;
    }

    public void setRegionStatus(Integer regionStatus) {
        this.regionStatus = regionStatus;
    }

    public String getOutWorkRemark() {
        return outWorkRemark;
    }

    public void setOutWorkRemark(String outWorkRemark) {
        this.outWorkRemark = outWorkRemark;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public Integer getEarlyMinutes() {
        return earlyMinutes;
    }

    public void setEarlyMinutes(Integer earlyMinutes) {
        this.earlyMinutes = earlyMinutes;
    }

    public Integer getWorkMinutes() {
        return workMinutes;
    }

    public void setWorkMinutes(Integer workMinutes) {
        this.workMinutes = workMinutes;
    }

    public String getAttendanceName() {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName) {
        this.attendanceName = attendanceName;
    }

    public Integer getAttendType() {
        return attendType;
    }

    public void setAttendType(Integer attendType) {
        this.attendType = attendType;
    }

    @Override
    public String toString() {
        return "EmployeeMonthDetailVO{" +
            "enterId='" + enterId + '\'' +
            ", uid='" + uid + '\'' +
            ", contactId='" + contactId + '\'' +
            ", employeeName='" + employeeName + '\'' +
            ", attendanceDate=" + attendanceDate +
            ", goWork=" + goWork +
            ", goWorkDate=" + goWorkDate +
            ", goLocation='" + goLocation + '\'' +
            ", goWorkDesc='" + goWorkDesc + '\'' +
            ", leaveWork=" + leaveWork +
            ", leaveWorkDate=" + leaveWorkDate +
            ", leaveLocation='" + leaveLocation + '\'' +
            ", leaveWorkDesc='" + leaveWorkDesc + '\'' +
            ", regionStatus=" + regionStatus +
            ", outWorkRemark='" + outWorkRemark + '\'' +
            ", phone='" + phone + '\'' +
            ", createTime=" + createTime +
            ", enterName='" + enterName + '\'' +
            ", remark='" + remark + '\'' +
            ", lateMinutes=" + lateMinutes +
            ", earlyMinutes=" + earlyMinutes +
            ", workMinutes=" + workMinutes +
            ", attendanceName='" + attendanceName + '\'' +
            ", attendType=" + attendType +
            '}';
    }
}

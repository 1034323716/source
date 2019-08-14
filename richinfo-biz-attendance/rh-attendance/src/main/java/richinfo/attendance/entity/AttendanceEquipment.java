package richinfo.attendance.entity;

/**
 * Created by Daniel on 2019/4/24.
 */
public class AttendanceEquipment {
    private String equipmentId;

    private String enterId;

    private String uid;

    private String employeeName;

    private String contractId;

    private String attendanceId;

    private String attendanceName;

    private String equipmentSerial;

    private String equipmentDeviceType;

    private String equipmentStatus;

    private String createTime;

    private String updateTime;

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getAttendanceName() {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName) {
        this.attendanceName = attendanceName;
    }

    public String getEquipmentSerial() {
        return equipmentSerial;
    }

    public void setEquipmentSerial(String equipmentSerial) {
        this.equipmentSerial = equipmentSerial;
    }

    public String getEquipmentDeviceType() {
        return equipmentDeviceType;
    }

    public void setEquipmentDeviceType(String equipmentDeviceType) {
        this.equipmentDeviceType = equipmentDeviceType;
    }

    public String getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(String equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AttendanceEquipment{" +
            "equipmentId='" + equipmentId + '\'' +
            ", enterId='" + enterId + '\'' +
            ", uid='" + uid + '\'' +
            ", employeeName='" + employeeName + '\'' +
            ", contractId='" + contractId + '\'' +
            ", attendanceId='" + attendanceId + '\'' +
            ", attendanceName='" + attendanceName + '\'' +
            ", equipmentSerial='" + equipmentSerial + '\'' +
            ", equipmentDeviceType='" + equipmentDeviceType + '\'' +
            ", equipmentStatus='" + equipmentStatus + '\'' +
            ", createTime='" + createTime + '\'' +
            ", updateTime='" + updateTime + '\'' +
            '}';
    }
}

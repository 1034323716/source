package richinfo.attendance.entity;

/**
 * Created by Daniel on 2019/4/24.
 */
public class AttendanceEquipmentControl {
    private String pId;

    private String enterId;

    private String equipmentUseStatus;

    private String equipmentLimit;

    private String createTime;

    private String updateTime;

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getEnterId() {
        return enterId;
    }

    public void setEnterId(String enterId) {
        this.enterId = enterId;
    }

    public String getEquipmentUseStatus() {
        return equipmentUseStatus;
    }

    public void setEquipmentUseStatus(String equipmentUseStatus) {
        this.equipmentUseStatus = equipmentUseStatus;
    }

    public String getEquipmentLimit() {
        return equipmentLimit;
    }

    public void setEquipmentLimit(String equipmentLimit) {
        this.equipmentLimit = equipmentLimit;
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
        return "AttendanceEquipmentControl{" +
            "pId='" + pId + '\'' +
            ", enterId='" + enterId + '\'' +
            ", equipmentUseStatus='" + equipmentUseStatus + '\'' +
            ", equipmentLimit='" + equipmentLimit + '\'' +
            ", createTime='" + createTime + '\'' +
            ", updateTime='" + updateTime + '\'' +
            '}';
    }
}

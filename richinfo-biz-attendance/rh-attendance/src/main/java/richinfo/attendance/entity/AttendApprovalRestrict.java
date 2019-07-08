package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by qiang on 2019/2/15.
 */
public class AttendApprovalRestrict implements Serializable {


    //主键
    private int approvalId;
    //企业id
    private String enterId;
    //限制状态，0不开启 1开启
    private int restrictStatus ;
    //限制次数
    private int restrictNumber = 0;
    //创建时间
    private Date createTime;
    //更新时间
    private Date modifyTime;

    public int getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(int approvalId) {
        this.approvalId = approvalId;
    }

    public String getEnterId() {
        return enterId;
    }

    public void setEnterId(String enterId) {
        this.enterId = enterId;
    }

    public int getRestrictStatus() {
        return restrictStatus;
    }

    public void setRestrictStatus(int restrictStatus) {
        this.restrictStatus = restrictStatus;
    }

    public int getRestrictNumber() {
        return restrictNumber;
    }

    public void setRestrictNumber(int restrictNumber) {
        this.restrictNumber = restrictNumber;
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
        return "AttendApprovalRestrict{" +
            "approvalId=" + approvalId +
            ", enterId='" + enterId + '\'' +
            ", restrictStatus=" + restrictStatus +
            ", restrictNumber=" + restrictNumber +
            ", createTime=" + createTime +
            ", modifyTime=" + modifyTime +
            '}';
    }
}

package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by qiang on 2018/9/7.
 * 部门选择器
 */
public class AttendDepartmentChooser implements Serializable{
    private long chooserId=0;
    /*考勤组id*/
    private long attendanceId;
    /*企业id*/
    private String enterpriseId;
    /*部门id*/
    private String departmentId;
    /*部门名称*/
    private String departmentName;
    /*创建时间*/
    private Date createTime;
    /*更新时间*/
    private Date updateTime;

    public long getChooserId() {
        return chooserId;
    }

    public void setChooserId(long chooserId) {
        this.chooserId = chooserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId) {
        this.attendanceId = attendanceId;
    }


    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "AttendDepartmentChooser{" +
                "chooserId=" + chooserId +
                ", attendanceId=" + attendanceId +
                ", enterpriseId='" + enterpriseId + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

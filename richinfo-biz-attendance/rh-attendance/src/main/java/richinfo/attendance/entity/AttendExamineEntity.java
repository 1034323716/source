/**
 * 文件名：AttendAppealEntity.java
 * 创建日期： 2017年10月17日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月17日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：考勤组、审批员关联实体类
 *
 */
public class AttendExamineEntity implements Serializable
{
    private static final long serialVersionUID = 4965015393004534828L;

    /** 主键id */
    private long attExamineId;

    /** 企业Id */
    private String enterId;

    /** 考勤组ID */
    private long attendanceId;

    /** 审批者uid */
    private String examineUid;

    /** 审批者姓名 */
    private String examineName;

    /*审批者contactId*/
    private String examineContactId;

    /** 审批者状态 1:正常 2:非正常(不是审批者) */
    private int examinerState = ExaminerState.Normal.getValue();

    /** 记录是否被删除 1：否 2：是 */
    private int isDelete;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    public String getExamineContactId() {
        return examineContactId;
    }

    public void setExamineContactId(String examineContactId) {
        this.examineContactId = examineContactId;
    }

    public int getIsDelete()
    {
        return isDelete;
    }

    public void setIsDelete(int isDelete)
    {
        this.isDelete = isDelete;
    }

    public String getExamineName()
    {
        return examineName;
    }

    public void setExamineName(String examineName)
    {
        this.examineName = examineName;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public long getAttExamineId()
    {
        return attExamineId;
    }

    public void setAttExamineId(long attExamineId)
    {
        this.attExamineId = attExamineId;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getExamineUid()
    {
        return examineUid;
    }

    public void setExamineUid(String examineUid)
    {
        this.examineUid = examineUid;
    }

    public int getExaminerState()
    {
        return examinerState;
    }

    public void setExaminerState(int examinerState)
    {
        this.examinerState = examinerState;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public enum ExaminerState {
        // 正常 //异常
        Normal(1), Abnormal(2);

        private int value;

        private ExaminerState(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static ExaminerState parse(int value)
        {
            switch (value)
            {
                case 1:
                    return Normal;
                case 2:
                    return Abnormal;
                default:
                    return Abnormal;
            }
        }
    }


    @Override
    public String toString()
    {
        return "AttendExamineEntity [attExamineId=" + attExamineId
            + ", enterId=" + enterId + ", attendanceId=" + attendanceId
            + ", examineUid=" + examineUid + ", examineName=" + examineName
            + ", examinerState=" + examinerState + ", isDelete=" + isDelete
            + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
    }
}

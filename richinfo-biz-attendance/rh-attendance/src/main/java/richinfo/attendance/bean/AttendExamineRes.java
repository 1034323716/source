/**
 * 文件名：AttendExamineRes.java
 * 创建日期： 2017年10月17日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月17日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;

/**
 * 功能描述：
 *
 */
public class AttendExamineRes extends ResBean
{
    private static final long serialVersionUID = 3988097762293648979L;

    /** 考勤组id */
    private long attendanceId;

    /** 企业ID */
    private String enterId;

    /** 审批者uid */
    private String examineUid;

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public String getExamineUid()
    {
        return examineUid;
    }

    public void setExamineUid(String examineUid)
    {
        this.examineUid = examineUid;
    }
}

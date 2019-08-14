/**
 * 文件名：AttendAppealReq.java
 * 创建日期： 2017年10月12日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月12日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：申诉异常请求实体类
 *
 */
public class AttendAppealReq extends AttendReqBean implements Serializable
{
    private static final long serialVersionUID = -1302042355643250350L;

    /** 查询页 */
    private int pageNo;

    /** 每页显示大小,默认20 */
    private int pageSize;

    /** 偏移量 */
    private int offset;

    /** 申诉单Id */
    private long appealId;

    /** 用户姓名 */
    private String name;

    /** 申诉原因 */
    private String reason;

    /** 上午班次 */
    private Date goWork;

    /** 上午班次描述 */
    private String goWorkDesc;

    /** 下午班次 */
    private Date leaveWork;

    /** 下午班次描述 */
    private String leaveWorkDesc;

    /** 审核者uid */
    private String examineUid;

    /** 审核人姓名 */
    private String examineName;

    /** 审核单状态 */
    private int examineState;

    /** 审核结果    1：同意     2：拒绝 */
    private int examineResult;

    /** 考勤异常申诉原始数据中的记录编号Id */
    private long monthRcdId;

    /** 考勤异常申诉原始数据中的考勤日期 yyyy-MM-dd */
    private Date attendanceDate;

    /** 用户上班工作性质 ：工作日、节假日 */
    private String remark;

    /** 申诉记录： 1：申诉上午 2：申诉下午 3：申诉上、下午 */
    private int appealRecord;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public int getAppealRecord()
    {
        return appealRecord;
    }

    public void setAppealRecord(int appealRecord)
    {
        this.appealRecord = appealRecord;
    }

    public Date getGoWork()
    {
        return goWork;
    }

    public void setGoWork(Date goWork)
    {
        this.goWork = goWork;
    }

    public Date getLeaveWork()
    {
        return leaveWork;
    }

    public void setLeaveWork(Date leaveWork)
    {
        this.leaveWork = leaveWork;
    }

    public Date getAttendanceDate()
    {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate)
    {
        this.attendanceDate = attendanceDate;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public int getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public long getAppealId()
    {
        return appealId;
    }

    public void setAppealId(long appealId)
    {
        this.appealId = appealId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getGoWorkDesc()
    {
        return goWorkDesc;
    }

    public void setGoWorkDesc(String goWorkDesc)
    {
        this.goWorkDesc = goWorkDesc;
    }

    public String getLeaveWorkDesc()
    {
        return leaveWorkDesc;
    }

    public void setLeaveWorkDesc(String leaveWorkDesc)
    {
        this.leaveWorkDesc = leaveWorkDesc;
    }

    public String getExamineUid()
    {
        return examineUid;
    }

    public void setExamineUid(String examineUid)
    {
        this.examineUid = examineUid;
    }

    public String getExamineName()
    {
        return examineName;
    }

    public void setExamineName(String examineName)
    {
        this.examineName = examineName;
    }

    public int getExamineState()
    {
        return examineState;
    }

    public void setExamineState(int examineState)
    {
        this.examineState = examineState;
    }

    public int getExamineResult()
    {
        return examineResult;
    }

    public void setExamineResult(int examineResult)
    {
        this.examineResult = examineResult;
    }

    public long getMonthRcdId()
    {
        return monthRcdId;
    }

    public void setMonthRcdId(long monthRcdId)
    {
        this.monthRcdId = monthRcdId;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    @Override
    public String toString()
    {
        return "AttendAppealReq [pageNo=" + pageNo + ", pageSize=" + pageSize
            + ", appealId=" + appealId + ", name=" + name + ", reason="
            + reason + ", goWork=" + goWork + ", goWorkDesc=" + goWorkDesc
            + ", leaveWork=" + leaveWork + ", leaveWorkDesc=" + leaveWorkDesc
            + ", examineUid=" + examineUid + ", examineName=" + examineName
            + ", examineState=" + examineState + ", examineResult="
            + examineResult + ", monthRcdId=" + monthRcdId
            + ", attendanceDate=" + attendanceDate + ", remark=" + remark
            + ", appealRecord=" + appealRecord + ", createTime=" + createTime
            + ", updateTime=" + updateTime + "]";
    }
}

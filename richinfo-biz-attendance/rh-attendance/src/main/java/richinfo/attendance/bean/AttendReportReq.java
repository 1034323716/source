/**
 * 文件名：AttendReportReq.java
 * 创建日期： 2017年6月9日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月9日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.bean;

/**
 * 功能描述： 考勤统计请求bean
 */
public class AttendReportReq extends AttendReqBean
{

    private static final long serialVersionUID = -4914763649927106348L;

    /** 查询日期 */
    private String attendanceDate;

    /** 查询月份 */
    private String attendanceMonth;

    /** 分页显示，默认第一页 */
    private int pageNo = 1;

    /** 分页每页显示数据量，默认20条 */
    private int pageSize = 20;

    /** 分页偏移量 */
    private int offset;

    // 考勤PC端管理后台新增请求参数
    /** 报表接收人邮箱 */
    private String recvEmail;

    /** 查询开始日期 */
    private String startDate;

    /** 查询截止日期 */
    private String endDate;

    /** 查询员工姓名 */
    private String employeeName;

    /** 查询考勤组ID */
    private long attendanceId;

    /** 报表查询时分项标识：1：迟到；2：早退；3：未打卡 */
    private int itemId;

    /** 用户uid */
    private String uid;

    public String getEnter() {
        return enter;
    }

    public void setEnter(String enter) {
        this.enter = enter;
    }

    /** 用户uid */
    private String enter;

    /*是否外勤  0否 1是*/
    private int legworkStatus;

    public int getLegworkStatus() {
        return legworkStatus;
    }

    public void setLegworkStatus(int legworkStatus) {
        this.legworkStatus = legworkStatus;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public int getItemId()
    {
        return itemId;
    }

    public void setItemId(int itemId)
    {
        this.itemId = itemId;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    public void setEmployeeName(String employeeName)
    {
        this.employeeName = employeeName;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getAttendanceDate()
    {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate)
    {
        this.attendanceDate = attendanceDate;
    }

    public String getAttendanceMonth()
    {
        return attendanceMonth;
    }

    public void setAttendanceMonth(String attendanceMonth)
    {
        this.attendanceMonth = attendanceMonth;
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

    public String getRecvEmail()
    {
        return recvEmail;
    }

    public void setRecvEmail(String recvEmail)
    {
        this.recvEmail = recvEmail;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ReportReq[enterId=").append(super.getEnterId())
            .append(",uid=").append(super.getUid()).append(",queryMonth=")
            .append(attendanceMonth).append(",recvEmail=").append(recvEmail)
            .append(",queryDay=").append(attendanceDate).append(",startDate=")
            .append(startDate).append(",endDate=").append(endDate)
            .append(",attendanceId=").append(attendanceId)
            .append(",employeeName=").append(employeeName).append(",itemId=")
            .append(itemId).append(",uid=").append(uid).append(",legworkStatus=").append(legworkStatus).append("]");
        return sb.toString();
    }
}

/**
 * 文件名：AttendScheduleShiftReq.java
 * 创建日期： 2018年4月17日
 * 创建人：JaceJiang
 */
package richinfo.attendance.bean;

/**
 * 功能描述： 排班列表,排班班次列表查询bean
 */
public class AttendScheduleShiftReq extends AttendReqBean
{

    private static final long serialVersionUID = -4914763649927106348L;

    /** 分页显示，默认第一页 */
    private int pageNo = 1;

    /** 分页每页显示数据量，默认20条 */
    private int pageSize = 20;

    /** 分页偏移量 */
    private int offset;
    
    /** 数据总量 */
    private long totalCount;
    
    /** 数据总页数 */
    private int totalSize;
    
    /** 状态*/
    private int status;
    
    /** 考勤月份*///查询考勤组排班用到此字段
    private String attendMonth;

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

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public void setTotalSize(int totalSize)
    {
        this.totalSize = totalSize;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getAttendMonth()
    {
        return attendMonth;
    }

    public void setAttendMonth(String attendMonth)
    {
        this.attendMonth = attendMonth;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendScheduleShiftReq [pageNo=").append(pageNo)
            .append(", pageSize=").append(pageSize).append(", offset=")
            .append(offset).append(", totalCount=").append(totalCount)
            .append(", totalSize=").append(totalSize).append(", status=")
            .append(status).append(", attendMonth=").append(attendMonth)
            .append("]");
        return builder.toString();
    }

}

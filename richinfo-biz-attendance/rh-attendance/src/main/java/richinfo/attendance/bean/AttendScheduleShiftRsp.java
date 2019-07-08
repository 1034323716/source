/**
 * 文件名：AttendScheduleShiftReq.java
 * 创建日期： 2018年4月17日
 * 创建人：JaceJiang
 */
package richinfo.attendance.bean;

import java.util.List;

import richinfo.attendance.entity.AttendanceSchedule;
import richinfo.attendance.entity.AttendanceScheduleShift;
import richinfo.attendance.common.ResBean;

/**
 * 功能描述：查询排班,排班班次返回实体
 *
 */
public class AttendScheduleShiftRsp extends ResBean
{
    private static final long serialVersionUID = 826794059504387955L;

    private int pageNo;

    private int pageSize;

    private long totalCount;

    private List<AttendanceScheduleShift> data;
    //查询考勤组排班用到此字段
    private List<AttendanceSchedule> attendanceScheduleList;
   //查询考勤组排班用到此字段  月份
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

    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<AttendanceScheduleShift> getData()
    {
        return data;
    }

    public void setData(List<AttendanceScheduleShift> data)
    {
        this.data = data;
    }

    public List<AttendanceSchedule> getAttendanceScheduleList()
    {
        return attendanceScheduleList;
    }

    public void setAttendanceScheduleList(
        List<AttendanceSchedule> attendanceScheduleList)
    {
        this.attendanceScheduleList = attendanceScheduleList;
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
        builder.append("AttendScheduleShiftRsp [pageNo=").append(pageNo)
            .append(", pageSize=").append(pageSize).append(", totalCount=")
            .append(totalCount).append(", data=").append(data)
            .append(", attendanceScheduleList=").append(attendanceScheduleList)
            .append(", attendMonth=").append(attendMonth).append("]");
        return builder.toString();
    }
    
}

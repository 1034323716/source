/**
 * AttendScheduleRsp.java
 * 创建日期： 2018年4月17日
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年4月17日
 *   修改人：JaceJiang
 *   修改内容：
 */
package richinfo.attendance.bean;

import java.util.List;

import richinfo.attendance.entity.AttendanceSchedule;
import richinfo.attendance.common.ResBean;

/**
 * 功能描述：查询排班返回实体
 * 
 */
public class AttendScheduleRsp extends ResBean
{
    private static final long serialVersionUID = 826794059504387955L;

    private List<AttendanceSchedule> attendanceScheduleList;

    public List<AttendanceSchedule> getAttendanceScheduleList()
    {
        return attendanceScheduleList;
    }

    public void setAttendanceScheduleList(
        List<AttendanceSchedule> attendanceScheduleList)
    {
        this.attendanceScheduleList = attendanceScheduleList;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendScheduleRsp [getAttendanceScheduleList()=")
            .append(getAttendanceScheduleList()).append("]");
        return builder.toString();
    }
}

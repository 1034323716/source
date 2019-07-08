/**
 * 文件名：AttendScheduleReq.java
 * 创建日期： 2018年4月17日
 * 作者：     JaceJiang
 * Copyright (c) 2016-2018 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间： 2018年4月17日
 *   修改人：JaceJiang
 *   修改内容：
 */
package richinfo.attendance.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import richinfo.attendance.entity.AttendanceSchedule;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.ConverUtil;

import com.google.gson.internal.LinkedTreeMap;

/**
 * 功能描述：编辑考勤组排班的请求实体类
 * 
 */
public class AttendScheduleReq extends AttendReqBean
{

    private static final long serialVersionUID = 4527786030737057924L;

    private List<AttendanceSchedule> attendanceScheduleList = new ArrayList<AttendanceSchedule>();

    private List<String> allUpdateUid = new ArrayList<String>();
    
    private String scheduleMonth = "";

    @SuppressWarnings("unchecked")
    public void parserEmployeesParam(Map<String, Object> params)
    {
        Date createTime = new Date();
        long attendanceId = getAsLong(params, "attendanceId");
        this.setAttendanceId(attendanceId);
        scheduleMonth = (String) params.get("scheduleMonth");
        List<Object> scheduleList = (List<Object>) params.get("scheduleList");
        int count = scheduleList.size();
        for (int i = 0; i < count; i++)
        {
            LinkedTreeMap<String, Object> attendanceScheduleMap = (LinkedTreeMap<String, Object>) scheduleList
                .get(i);
            String uid = (String) attendanceScheduleMap.get("uid");
            String employeeName = (String) attendanceScheduleMap.get("employeeName");

            if (!AssertUtil.isEmpty(uid) && !AssertUtil.isEmpty(employeeName))
            {
                AttendanceSchedule attendanceSchedule = new AttendanceSchedule();
                attendanceSchedule.setAttendanceId(attendanceId);
                attendanceSchedule.setAttendMonth(scheduleMonth);
                attendanceSchedule.setCreateTime(createTime);
                attendanceSchedule.setModifyTime(createTime);
                attendanceSchedule.setUid(uid);
                attendanceSchedule.setStatus(AttendanceSchedule.Status.Normal.getValue());
                attendanceSchedule.setEmployeeName(employeeName);
                LinkedTreeMap<String, Object> scheduleMap =  (LinkedTreeMap)attendanceScheduleMap.get("schedule");
//                String scheduleListStr = attendanceScheduleMap
//                    .get("schedule");
//                System.out.println(scheduleListStr);
//                Map<String, Object> scheduleListMap = JsonUtil
//                    .jsonToMap(scheduleListStr);
                if(scheduleMap != null && scheduleMap.size()>0){
                    fillAttendanceScheduleDays(attendanceSchedule, scheduleMap);
                }
                attendanceScheduleList.add(attendanceSchedule);
                allUpdateUid.add(uid);
            }
        }
    }

    private static long getAsLong(Map<String, Object> map, String key)
    {
        if (AssertUtil.isEmpty(map))
        {
            return -1;
        }

        String result = (String) map.get(key);
        if (AssertUtil.isEmpty(result))
        {
            return -1;
        }
        return ConverUtil.string2Long(result, -1);
    }

    private static void fillAttendanceScheduleDays(
        AttendanceSchedule attendanceSchedule,
        LinkedTreeMap<String, Object>  scheduleListMap)
    {

        for (Integer i = 1; i < 32; i++)
        {
            String iStr = i.toString();
            if (scheduleListMap.containsKey(iStr)
                && AssertUtil.isNum((String) scheduleListMap.get(iStr)))
                ;
            {
            	Long id = 0L;
            	if(AssertUtil.isNotEmpty(scheduleListMap.get(iStr)))
            	{
            		id = Long.valueOf((String)scheduleListMap.get(iStr));
            	}
                switch (i)
                {
                case 1:
                    attendanceSchedule.setDay1(id);
                    break;
                case 2:
                    attendanceSchedule.setDay2(id);
                    break;
                case 3:
                    attendanceSchedule.setDay3(id);
                    break;
                case 4:
                    attendanceSchedule.setDay4(id);
                    break;
                case 5:
                    attendanceSchedule.setDay5(id);
                    break;
                case 6:
                    attendanceSchedule.setDay6(id);
                    break;
                case 7:
                    attendanceSchedule.setDay7(id);
                    break;
                case 8:
                    attendanceSchedule.setDay8(id);
                    break;
                case 9:
                    attendanceSchedule.setDay9(id);
                    break;
                case 10:
                    attendanceSchedule.setDay10(id);
                    break;
                case 11:
                    attendanceSchedule.setDay11(id);
                    break;
                case 12:
                    attendanceSchedule.setDay12(id);
                    break;
                case 13:
                    attendanceSchedule.setDay13(id);
                    break;
                case 14:
                    attendanceSchedule.setDay14(id);
                    break;
                case 15:
                    attendanceSchedule.setDay15(id);
                    break;
                case 16:
                    attendanceSchedule.setDay16(id);
                    break;
                case 17:
                    attendanceSchedule.setDay17(id);
                    break;
                case 18:
                    attendanceSchedule.setDay18(id);
                    break;
                case 19:
                    attendanceSchedule.setDay19(id);
                    break;
                case 20:
                    attendanceSchedule.setDay20(id);
                    break;
                case 21:
                    attendanceSchedule.setDay21(id);
                    break;
                case 22:
                    attendanceSchedule.setDay22(id);
                    break;
                case 23:
                    attendanceSchedule.setDay23(id);
                    break;
                case 24:
                    attendanceSchedule.setDay24(id);
                    break;
                case 25:
                    attendanceSchedule.setDay25(id);
                    break;
                case 26:
                    attendanceSchedule.setDay26(id);
                    break;
                case 27:
                    attendanceSchedule.setDay27(id);
                    break;
                case 28:
                    attendanceSchedule.setDay28(id);
                    break;
                case 29:
                    attendanceSchedule.setDay29(id);
                    break;
                case 30:
                    attendanceSchedule.setDay30(id);
                    break;
                case 31:
                    attendanceSchedule.setDay31(id);
                    break;
                default:
                    break;
                }
            }
        }

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

    public List<String> getAllUpdateUid()
    {
        return allUpdateUid;
    }

    public void setAllUpdateUid(List<String> allUpdateUid)
    {
        this.allUpdateUid = allUpdateUid;
    }

    public String getScheduleMonth()
    {
        return scheduleMonth;
    }

    public void setScheduleMonth(String scheduleMonth)
    {
        this.scheduleMonth = scheduleMonth;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AttendScheduleReq [attendanceScheduleList=")
            .append(attendanceScheduleList).append(", allUpdateUid=").append(allUpdateUid)
            .append(", scheduleMonth=").append(scheduleMonth).append("]");
        return builder.toString();
    }

    
}

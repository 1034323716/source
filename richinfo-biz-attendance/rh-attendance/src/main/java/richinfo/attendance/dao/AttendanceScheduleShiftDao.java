/**
 * 文件名：AttendanceScheduleShiftDao.java
 * 创建日期： 2018年4月3日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年4月3日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.entity.AttendanceScheduleShift;
import richinfo.attendance.entity.AttendanceScheduleShift.ShiftStatus;

/**
 * 功能描述：考勤组排班班次 DAO层
 *
 */
public class AttendanceScheduleShiftDao extends BaseAttendanceDao
{
    /** table_name="attendance_schedule_shift"; */

    private Logger logger = LoggerFactory
        .getLogger(AttendanceScheduleShiftDao.class);

    /**
     * 根据考勤组ID查询排班班次
     * @param attendanceId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceScheduleShift> queryShiftByAttendanceId(
        long attendanceId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        map.put("status", ShiftStatus.Normal.getValue());
        List<AttendanceScheduleShift> list = null;
        try
        {
            list = attendanceDao.queryForList(
                "attendance.queryShiftByAttendanceId", map);
        }
        catch (Exception e)
        {
            logger.error("queryShiftByAttendanceId error attendanceId={}. {}",
                attendanceId, e);
        }

        return list;
    }
}

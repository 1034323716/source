/**
 * 文件名：AttendCalendarDao.java
 * 创建日期： 2017年6月9日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月9日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.dao;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.entity.AttendCalendar;
import richinfo.dbcomponent.exception.PersistException;

/**
 * 功能描述：公共日历数据库可持久层
 * 
 */
public class AttendCalendarDao extends BaseAttendanceDao
{
    private final Logger logger = LoggerFactory
        .getLogger(AttendCalendarDao.class);

    /**
     * 根据年份查询公共日历数据
     * @param year 年份，格式：yyyy。eg:2017
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendCalendar> queryCalendarByYear(String year)
    {
        try
        {
            logger.debug("queryCalendarBetweenTime.year={}", year);

            return attendanceDao.queryForList("attendance.queryCalendarByYear",
                year);
        }
        catch (PersistException e)
        {
            logger.error("queryCalendarBetweenTime error.year={}", year, e);
            return null;
        }
    }

    /**
     * 根据年份删除公共日历数据
     * @param year 年份，格式：yyyy。eg:2017
     * @return
     */
    public boolean removeCalendarByYear(String year)
    {
        try
        {
            int result = attendanceDao.delete(
                "attendance.removeCalendarByYear", year);
            logger.info("removeCalendarBetweenTime success.year={}", year,
                result);

            return result > 0;
        }
        catch (PersistException e)
        {
            logger.error("removeCalendarBetweenTime error.year={}", year, e);
            return false;
        }
    }

    /**
     * 批量插入公共日历数据
     * @param calendarList
     * @return
     */
    public boolean batchSaveAttendCalendar(List<AttendCalendar> calendarList)
    {
        try
        {
            logger.info("batchSaveAttendCalendar.size={}", calendarList.size());

            return attendanceDao.batchInsert(
                "attendance.batchSaveAttendCalendar", calendarList);
        }
        catch (PersistException e)
        {
            logger.error("batchSaveAttendCalendar error.", e);
            return false;
        }
    }

    /**
     * 根据日期查找公共日历数据，查找失败返回null
     * @param calendarDate
     * @return
     */
    public AttendCalendar queryAttendCalendarByDate(Date calendarDate)
    {
        AttendCalendar result = null;
        try
        {
            result = (AttendCalendar) attendanceDao.queryForObject(
                "attendance.queryAttendCalendarByDate", calendarDate);

            logger.info("queryAttendCalendarByDate.result={}", result);
        }
        catch (PersistException e)
        {
            logger.error("queryAttendCalendarByDate error.date={}",
                calendarDate, e);
        }
        return result;
    }
}

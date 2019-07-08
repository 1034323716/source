/**
 * 文件名：CalenderServiceImpl.java
 * 创建日期： 2017年6月8日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.common.AtdcConsts;
import richinfo.attendance.common.ServiceObject;
import richinfo.attendance.dao.AttendCalendarDao;
import richinfo.attendance.entity.AttendCalendar;
import richinfo.attendance.entity.AttendCalendar.CalendarStatus;
import richinfo.attendance.service.CalendarService;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AtdcTimeUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：公共日历数据逻辑层
 * 
 */
public class CalendarServiceImpl extends ServiceObject implements
    CalendarService
{
    private final Logger logger = LoggerFactory
        .getLogger(CalendarServiceImpl.class);

    private AttendanceConfig config = AttendanceConfig.getInstance();
    private AttendCalendarDao calendarDao = new AttendCalendarDao();

    private final int HolidayType = 0;
    private final int WeekendType = 1;

    /*
     * (non-Javadoc)
     * 
     * @see richinfo.attendance.service.CalenderService#savePublicCalender()
     */
    @Override
    public void savePublicCalendar()
    {
        // 已完成配置的年份
        String configYear = config.getCalenderConfigYear();
        // 需要保存的年份
        String saveYear = config.getCalenderSaveYear();
        // 判断下一年是否完成配置
        if (configYear.indexOf(saveYear) < 0)
        {
            logger.warn(
                "Public Calendar has not configured.yearConfig={}|saveYear={}",
                configYear, saveYear);

            // 告警措施

            return;
        }
        // 获取录入年份的日历数据列表
        List<AttendCalendar> calendarList = getPublicCalendar(saveYear);
        if (AssertUtil.isEmpty(calendarList))
        {
            logger.error("calendarList is empty,end it.");
            return;
        }
        logger.info("getPublicCalendar end.year={}|size={}", saveYear,
            calendarList.size());

        // 检查数据
        if (AssertUtil.isNotEmpty(calendarDao.queryCalendarByYear(saveYear)))
        {
            logger.warn("Calendar has dates,remove it.year={}", saveYear);

            calendarDao.removeCalendarByYear(saveYear);
        }
        // 批量入库
        boolean result = calendarDao.batchSaveAttendCalendar(calendarList);

        logger.info("savePublicCalendar end.year={}|size={}|result={}",
            saveYear, calendarList.size(), result);
    }

    /**
     * 获取年份的日历数据列表
     * @param year 即将录入的年份
     * @return
     */
    private List<AttendCalendar> getPublicCalendar(String year)
    {
        List<AttendCalendar> yearList = new ArrayList<AttendCalendar>();

        // 获取节假日的日历数据
        List<AttendCalendar> holidayList = getCalendarFromConfig(year,
            HolidayType);
        if (AssertUtil.isEmpty(holidayList))
        {
            logger.warn("holidayList is empty,return.");
            return yearList;
        }
        logger.info("get holidayList end.year={}|size={}", year,
            holidayList.size());

        // 获取周末的日历数据
        List<AttendCalendar> weekendList = getCalendarFromConfig(year,
            WeekendType);
        if (AssertUtil.isEmpty(weekendList))
        {
            logger.warn("weekendList is empty,return.");
            return yearList;
        }
        logger.info("get weekendList end.year={}|size={}", year,
            weekendList.size());

        // 节假日数据获取成功，添加到总列表中
        yearList.addAll(holidayList);
//        yearList.addAll(weekendList);

        // 获取工作日的日历数据
        List<AttendCalendar> workdayList = getWorkdayOfYear(year, yearList);
        if (AssertUtil.isEmpty(workdayList))
        {
            logger.warn("workdayList is empty,return.");
            return yearList;
        }
        logger.info("get workdayList end.year={}|size={}", year,
            workdayList.size());

        // 添加工作日数据
        yearList.addAll(workdayList);

        return yearList;
    }

    /**
     * 获取配置的日历数据
     * @param year 年份
     * @param type 类型，节假日HolidayType = 0; 双休日 WeekendType = 1
     * @return
     */
    private List<AttendCalendar> getCalendarFromConfig(String year, int type)
    {
        List<AttendCalendar> holidayList = new ArrayList<AttendCalendar>();

        // 获取配置值
        String configStr = "";
        if (type == HolidayType)
        {
            // 获取假日数据,eg:2017-05-28:端午节,2017-05-29:端午节
            configStr = config.getHoilday(year);
        }
        else if (type == WeekendType)
        {
            // 获取双休日数据,eg:2017-06-10,2017-06-11
            configStr = config.getWeekend(year);
        }
        else
        {
            logger.error("error type.type={}", type);
            return holidayList;
        }
        // 再次校验配置值
        if (AssertUtil.isEmpty(configStr))
        {
            logger.error("holiday of calender is empty.year={}", year);
            return holidayList;
        }

        logger.info("year={}|configStr={}", year, configStr);
        return parserCalendar(configStr);
    }

    /**
     * 解析配置的非工作日的日历数据
     * @param holidayConfig
     * @return
     */
    private List<AttendCalendar> parserCalendar(String holidayConfig)
    {
        List<AttendCalendar> list = new ArrayList<AttendCalendar>();

        // 日期列表之间，用逗号(,)分隔。eg:2017-05-28:端午节,2017-05-29:端午节
        String[] holidays = holidayConfig.split(AtdcConsts.SEPARATOR.DATE_LIST);
        for (String holiday : holidays)
        {
            // 日期-描述之间，用冒号(:)分隔。eg:2017-05-28:端午节
            String[] desc = holiday.split(AtdcConsts.SEPARATOR.DATE_DESC);
            // 校验格式
            if (desc.length > 2)
            {
                logger.error("holiday of calender formart error.date={}",
                    holiday);
                return list;
            }

            AttendCalendar temp = new AttendCalendar();
            // 设置日期
            Date date = TimeUtil
                .string2Date(desc[0], TimeUtil.BASE_DATE_FORMAT);
            temp.setCalendarDate(date);
            // 设置描述
            if (desc.length == 2)
            {
                // 设置配置的描述.eg:2017-05-28:端午节
                temp.setRemark(desc[1]);
            }
            else
            {
                // 双休日，无描述，仅日期。eg:2017-06-10,2017-06-11
                temp.setRemark(AtdcConsts.REMARK.WEEKEND);
            }
            // 非工作日是否使用默认的统一备注描述
            if (config.isUseRestdayDefaultRemark() > 0)
            {
                // 目前节假日、双休日的描述，统一使用“节假日”，无需判断具体的节日,2017-06-08
                temp.setRemark(AtdcConsts.REMARK.HOLIDAY);
            }

            temp.setWeek(AtdcTimeUtil.getWeekDesc(date));
            // 设置状态为非工作日
            temp.setStatus(CalendarStatus.Holiday.getValue());
            temp.setCreateTime(new Date());

            // 添加到列表中
            list.add(temp);
        }

        logger.info("parserCalendar end.size={}", list.size());

        return list;
    }

    /**
     * 获取年份的工作日列表
     * @param year 年份
     * @param restDayList 休息日列表，节假日
     * @return
     */
    private List<AttendCalendar> getWorkdayOfYear(String year,
        List<AttendCalendar> restDayList)
    {
        List<AttendCalendar> workDayList = new ArrayList<AttendCalendar>();

        // 获取年份的日期列表
        List<Date> dateOfYear = AtdcTimeUtil.getDateOfYear(year);
        if (AssertUtil.isEmpty(dateOfYear))
        {
            logger.error("getDateOfYear error.year={}", year);
            return workDayList;
        }
        for (Date date : dateOfYear)
        {
            boolean isRest = false;
            // 判断是否为节假日
            for (AttendCalendar restDay : restDayList)
            {
                // 不能直接使用Date.compareTo(anotherDate)函数，其对比的是毫秒数，仅对比日期即可
                if (TimeUtil.getDaysBetween(date, restDay.getCalendarDate()) == 0L)
                {
                    isRest = true;
                    break;
                }
            }
            // 为工作日
            if (!isRest)
            {
                AttendCalendar temp = new AttendCalendar();
                // 组装实体对象
                temp.setCalendarDate(date);
                temp.setWeek(AtdcTimeUtil.getWeekDesc(date));
                temp.setRemark(AtdcConsts.REMARK.WEEKDAY);
                temp.setStatus(CalendarStatus.Weekday.getValue());
                temp.setCreateTime(new Date());

                // 添加到列表中
                workDayList.add(temp);
            }
        }

        logger.info("getWorkdayOfYear end.year={}|size={}", year,
            workDayList.size());
        return workDayList;
    }
}

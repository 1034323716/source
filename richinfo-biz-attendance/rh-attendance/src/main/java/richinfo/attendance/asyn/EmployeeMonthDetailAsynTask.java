/**
 * 文件名：EmployeeMonthDetailAsynTask.java
 * 创建日期： 2017年6月10日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月10日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.asyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.entity.AttendCalendar;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.service.EmployeeMonthStatisticsService;
import richinfo.attendance.service.impl.EmployeeMonthStatisticsServiceImpl;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.bcomponet.asyn.task.AsynTask;

/**
 * 功能描述：以考勤组为单位统计考勤状态：便于判断考勤状态，避免互相影响
 * 
 */
public class EmployeeMonthDetailAsynTask implements AsynTask<String>
{
    /** 考勤组 */
    private AttendGroup attendGroup;
    /** 考勤日历，包含统计日期等信息 */
    private AttendCalendar attendCalendar;


    private final Logger logger = LoggerFactory
        .getLogger(EmployeeMonthDetailAsynTask.class);

    public EmployeeMonthDetailAsynTask(AttendGroup attendGroup,
        AttendCalendar attendCalendar)
    {
        this.attendGroup = attendGroup;
        this.attendCalendar = attendCalendar;
        this.task();
    }

    @Override
    public String task()
    {
        long start = System.currentTimeMillis();
        logger.info("EmployeeMonthDetailAsynTask start. group={}", attendGroup);

        EmployeeMonthStatisticsService service = new EmployeeMonthStatisticsServiceImpl(
                attendGroup, attendCalendar);

        service.employeeMonthDatailStatistics();

        logger.info("EmployeeMonthDetailAsynTask end. useTime={}|group={}",
            AttendanceUtil.getUseTime(start), attendGroup);

       return null;
    }
}

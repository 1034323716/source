/**
 * 文件名：PubilcCalenderTask.java
 * 创建日期： 2017年6月8日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.task;

import richinfo.attendance.service.CalendarService;
import richinfo.attendance.service.impl.CalendarServiceImpl;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

/**
 * 功能描述：保存公共日历数据
 * 
 */
public class AttendCalendarTask extends Task
{
    private CalendarService calendarService = new CalendarServiceImpl();

    @Override
    public void repeat(TaskContext context) throws TaskException
    {
        // 判断是否为定时任务机器，保证只有一台机器执行定时任务
        if (!AttendanceConfig.getInstance().isTaskServer())
        {
            logger.info("not task server,ignore AttendCalendarTask.");
            return;
        }

        logger.info("Start PubilcCalenderTask.");
        // 保存公共日历数据
        savePublicCalendar();
    }

    /**
     * 保存公共日历数据
     */
    private void savePublicCalendar()
    {
        calendarService.savePublicCalendar();
    }
}

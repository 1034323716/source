/**
 * 文件名：TeamMonthReportTask.java
 * 创建日期： 2017年6月9日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月9日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.asyn.TeamMonthReportAsynTask;
import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.entity.TeamMonthReportEntity;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.attendance.util.TimeUtil;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 功能描述：团队月报统计定时任务
 *
 */
public class TeamMonthReportTask extends Task
{
    private AttendReportDao reportDao = new AttendReportDao();
    //private AttendanceConfig config = AttendanceConfig.getInstance();

    private Logger logger = LoggerFactory.getLogger(TeamMonthReportTask.class);

    @Override
    public void init(TaskContext context) throws TaskException
    {
    }

    @Override
    public void repeat(TaskContext context) throws TaskException {
        // 判断是否为定时任务机器，保证只有一台机器执行定时任务
        if (!AttendanceConfig.getInstance().isTaskServer()) {
            logger.info("not task server,ignore TeamMonthReportTask.");
            return;
        }

        //调用统计方法   参数为null  代表获取当前时间统计
        statistics(null);


    }

    public void  statistics(Date assignDate){
        long start = System.currentTimeMillis();
        logger.info("start execute TeamMonthReportTask");

        // 获取当前系统时间的前一天时间
        Calendar calendarY = Calendar.getInstance();
        calendarY.setTime(new Date());
        if (assignDate != null) {
            calendarY.setTime(assignDate);
        }
        calendarY.add(Calendar.DAY_OF_MONTH, -1);
        String attendanceDate = TimeUtil.date2String(calendarY.getTime(),
            TimeUtil.BASE_DATE_FORMAT);

        // 获取当前系统时间的前一天时间的当前月第一天：
        calendarY.add(Calendar.MONTH, 0);
        calendarY.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        String firstDate = TimeUtil.date2String(calendarY.getTime(),
            TimeUtil.BASE_DATE_FORMAT);

        List<String> enterIds = reportDao.queryAllEnterId();
        if (AssertUtil.isEmpty(enterIds)) {
            logger.info("TeamMonthReportTask can't find enterIds");
            return;
        }
        //多线程执行插入导致部分数据插入失败,暂时更改线程池为1
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        try {

            for (String enterId : enterIds) {
                List<TeamMonthReportEntity> monthes = reportDao.teamMonthReportInfo(firstDate, attendanceDate, enterId);
                if (AssertUtil.isEmpty(monthes)) {
                    logger
                        .info(
                            "TeamMonthReportTask can't find report data,enterId={}|attendanceDate={}",
                            enterId, attendanceDate);
                    continue;
                }

                // 异步将各企业数据入库
//            AsynTaskProcess.asynExecTask(new TeamMonthReportAsynTask(monthes));
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        new TeamMonthReportAsynTask(monthes);
                    }
                });
            }
            //线程任务执行完毕后 关闭多线程
            fixedThreadPool.shutdown();
            logger.info("end TeamMonthReportTask useTime={}", AttendanceUtil.getUseTime(start));

        } catch (Exception e) {
            //发生异常直接回收关闭连接池
            fixedThreadPool.shutdownNow();
        }

    }

}

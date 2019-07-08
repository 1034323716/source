/**
 * 文件名：EmployeeMonthDetailTask.java
 * 创建日期： 2017年6月10日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月10日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.task;

import richinfo.attendance.asyn.EmployeeMonthDetailAsynTask;
import richinfo.attendance.dao.AttendCalendarDao;
import richinfo.attendance.dao.AttendGroupDao;
import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.entity.AttendCalendar;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.entity.EmployeeMonthDetail;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.attendance.util.TimeUtil;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：员工个人月报明细统计，每日统计员工的考勤状态
 * 
 */
public class EmployeeMonthDetailTask extends Task
{
    private AttendGroupDao groupDao = new AttendGroupDao();
    private AttendCalendarDao calendarDao = new AttendCalendarDao();
    private AttendReportDao reportDao = new AttendReportDao();

//    private Lock lock = new ReentrantLock();

    public static void main(String[] args) throws ParseException {
        Calendar calendarY = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String date = "2019-03";
        calendarY.setTime(simpleDateFormat.parse(date));
        System.out.println(calendarY.getTime());
    }

  /*  @Override
    public void repeat(TaskContext context) throws TaskException {
        temporaryData();
    }*/
    @Override
    public void repeat(TaskContext context) throws TaskException {
        // 判断是否为定时任务机器，保证只有一台机器执行定时任务
        if (!AttendanceConfig.getInstance().isTaskServer()) {
            logger.info("not task server,ignore EmployeeMonthDetailTask.");
            return;
        }
        //调用统计方法   参数为null  代表获取当前时间统计
        statistics(null);

    }

    public void  statistics(Date assignDate){
        long start = System.currentTimeMillis();

        List<AttendGroup> allGroups;
        try {
            allGroups = groupDao.queryAllNormalAttendGroup();
        } catch (Exception e) {
            logger.error("EmployeeMonthDetailTask queryEmployRecordByTime error!", e);
            //异常重试一次，防止连接空闲太久被关闭导致的连接异常问题
            allGroups = groupDao.queryAllNormalAttendGroup();
        }
        // 考勤组列表为空，直接返回
        if (AssertUtil.isEmpty(allGroups)) {
            logger.warn("AttendGroup is empty,EmployeeMonthDetailTask end.");
            return;
        }

        Date today = new Date();

        if (assignDate != null){
            today = assignDate;
        }

        Calendar c = Calendar.getInstance();
        int hh = c.get(Calendar.HOUR_OF_DAY);
        //判断是否是凌晨执行
        if (hh==0 ) {
            c.setTime(today);
            //凌晨执行时则统计前一天的数据
            c.add(Calendar.DAY_OF_MONTH, -1);
            today = c.getTime();
        }

        AttendCalendar todayAttendCalendar = calendarDao
            .queryAttendCalendarByDate(today);
        logger.info("统计时间 todayAttendCalendar={}",todayAttendCalendar);

        if (AssertUtil.isEmpty(todayAttendCalendar)) {
            logger.warn("todayAttendCalendar is empty,EmployeeMonthDetailTask end.");
            return;
        }
        //固长线程池 默认线程数为20
        ExecutorService fixedThreadPool  = Executors.newFixedThreadPool(AttendanceConfig.getInstance().getMultiThreadedPool());
        try {
            for (AttendGroup group : allGroups) {
                //加锁
//                lock.lock();
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        new EmployeeMonthDetailAsynTask(group, todayAttendCalendar);
                    }
                });
            }

            logger.info("end EmployeeMonthDetailTask useTime={}", AttendanceUtil.getUseTime(start));
            //多线程任务执行完毕 然后关闭线程池
            fixedThreadPool.shutdown();
            //1秒钟监听一次线程线是否完成任务
            while (true) {
                if (fixedThreadPool.isTerminated()) {
                    logger.info("====================================个人月报执行完毕====================================");
                    //执行团队日报
                    //个人月报处理完直接处理团队日报
                    new TeamDailyTask(TimeUtil.formatDateTime(todayAttendCalendar.getCalendarDate(),TimeUtil.BASE_DATE_FORMAT));
                    break;
                }
                Thread.sleep(1000);
            }
            //设置线程中断超时时间 5S
            if (!fixedThreadPool.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                fixedThreadPool.shutdownNow();
            }

        } catch (Exception e) {
            fixedThreadPool.shutdownNow();
        }

    }

    private void temporaryData() {
        logger.info("============================开始插入数据=====================================");
        List<EmployeeMonthDetail>list = new ArrayList();
        for (int i = 0 ;i<=10000;i++){
            EmployeeMonthDetail employeeMonthDetail = new EmployeeMonthDetail();
            employeeMonthDetail.setAttendanceId(311L);
            employeeMonthDetail.setEnterId("118648502");
            employeeMonthDetail.setUid(UUID.randomUUID().toString().replaceAll("-",""));
            employeeMonthDetail.setEmployeeName(i+"");
            employeeMonthDetail.setAttendanceDate(new Date());
            employeeMonthDetail.setGoWork(new Date());
            employeeMonthDetail.setGoWorkDate(new Date());
            employeeMonthDetail.setGoWorkDesc(i+"");
            employeeMonthDetail.setGoLocation(i+"");
            employeeMonthDetail.setLeaveWork(new Date());
            employeeMonthDetail.setLeaveWorkDate(new Date());
            employeeMonthDetail.setLeaveWorkDesc(i+"");
            employeeMonthDetail.setGoLocation(1+"");
            employeeMonthDetail.setRegionStatus(0);
            employeeMonthDetail.setRemark("工作日");
            employeeMonthDetail.setCreateTime(new Date());
            employeeMonthDetail.setRecordState(1);
            employeeMonthDetail.setPhone("13800138000");
            employeeMonthDetail.setAttendType(1);
            list.add(employeeMonthDetail);
            if (list.size() == 200){
                boolean result = reportDao.batchSaveEmployeeMonthDetail(list);
                list.clear();
            }
        }
        if (!list.isEmpty()) {
            boolean result = reportDao.batchSaveEmployeeMonthDetail(list);
        }
        logger.info("============================数据插入完成=============================");
    }

}

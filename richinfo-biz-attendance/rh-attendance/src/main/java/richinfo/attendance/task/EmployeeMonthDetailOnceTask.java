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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import richinfo.attendance.common.AtdcConsts;
import richinfo.attendance.dao.AttendCalendarDao;
import richinfo.attendance.dao.AttendDao;
import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.entity.AttendCalendar;
import richinfo.attendance.entity.AttendEntity;
import richinfo.attendance.entity.EmployeeMonthDetailVO;
import richinfo.attendance.util.*;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 功能描述：员工个人月报明细统计，每日统计员工的考勤状态
 * 
 */
public class EmployeeMonthDetailOnceTask extends Task {
    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();
    private AttendCalendarDao calendarDao = new AttendCalendarDao();
    private AttendReportDao reportDao = new AttendReportDao();
    private AttendDao attendDao = new AttendDao();

    /** 上午打卡截止时间，用于判断上午、下午班次 */
    private String morningEndTime;
    /** 上午打卡截止时间，用于判断是否迟到 */
    private String morningStartTime;
    /** 上午打卡截止时间，用于判断是否早退 */
    private String afternoonEndTime;
    /** 上午工作时间 */
    private String amTime;
    /** 下午工作时间*/
    private String pmTime;




    @Override
    public void repeat(TaskContext context) throws TaskException {
        // 判断是否为定时任务机器，保证只有一台机器执行定时任务
        if (!AttendanceConfig.getInstance().isTaskServer()) {
            logger.info("not task server,ignore EmployeeMonthDetailOnceTask.");
            return;
        }

        long start = System.currentTimeMillis();

        logger.info("EmployeeMonthDetailOnceTask is begin");

        System.out.println("EmployeeMonthDetailOnceTask is begin");

        ExecutorService fixedThreadPool  = Executors.newFixedThreadPool(AttendanceConfig.getInstance().getMultiThreadedPool());

        List<EmployeeMonthDetailVO> employeeMonthDetailList =  reportDao.queryEmpMonthDetailList() ;

        for (EmployeeMonthDetailVO employeeMonthDetail : employeeMonthDetailList) {
            List<AttendEntity> attendList = attendDao.queryEmployRecord(employeeMonthDetail.getUid(), employeeMonthDetail.getAttendanceDate());
            AttendCalendar todayAttendCalendar = calendarDao.queryAttendCalendarByDate(employeeMonthDetail.getAttendanceDate());
            String remark = employeeMonthDetail.getRemark();
            if (employeeMonthDetail.getAttendType()==1) {//固定班
                boolean workday = isFixedAttendWorkDay(employeeMonthDetail,todayAttendCalendar);
                if ("工作日".equals(remark)) {
                    employeeMonthDetail = statisticsWeekdayAttend(attendList,employeeMonthDetail,todayAttendCalendar);
                } else {
                    employeeMonthDetail = statisticsRestdayAttend(attendList,employeeMonthDetail,todayAttendCalendar);
                    dealRemark(todayAttendCalendar,employeeMonthDetail);
                }

            } else if (employeeMonthDetail.getAttendType()==3) {//自由班
                boolean workday = isFreeAttendWorkDay(employeeMonthDetail,todayAttendCalendar);
                if ("工作日".equals(remark)) {
                    employeeMonthDetail = statisticsWeekdayFreeAttend(attendList,employeeMonthDetail,todayAttendCalendar);
                } else {
                    employeeMonthDetail = statisticsRestdayAttend(attendList,employeeMonthDetail,todayAttendCalendar);
                    dealRemark(todayAttendCalendar,employeeMonthDetail);
                }
            } else {
                employeeMonthDetail = statisticsRestdayAttend(attendList,employeeMonthDetail,todayAttendCalendar);
            }

            employeeDao.updateEmployeeMonthDetailBy(employeeMonthDetail);

            long a = System.currentTimeMillis();
            //个人月报处理完直接处理团队日报
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    new TeamDailyTask(TimeUtil.formatDateTime(todayAttendCalendar.getCalendarDate(),TimeUtil.BASE_DATE_FORMAT));
                }
            });
            long b = System.currentTimeMillis();
            logger.info("deal one teamdailytask time is {}",(b-a));
        }

        long end = System.currentTimeMillis();
        logger.info("this task is finished！and it had dealed {} items",employeeMonthDetailList.size());
        logger.info("using time is {}",(end-start));
    }

    private boolean isFixedAttendWorkDay(EmployeeMonthDetailVO employeeMonthDetail,AttendCalendar todayAttendCalendar){
        boolean workday =  false;
        Map jsonObject = JSON.parseObject(employeeMonthDetail.getFixedAttendRule());
        if(AssertUtil.isNotEmpty(jsonObject)) {
            int week = AtdcTimeUtil.getWeekNum(todayAttendCalendar.getWeek());
            JSONObject workTime = (JSONObject)jsonObject.get(ConverUtil.object2String(week));
            if(AssertUtil.isNotEmpty(workTime)) {
                workday = true;
                String amTime = workTime.get("amTime").toString();
                String pmTime = workTime.get("pmTime").toString();
                setStartAndEndTime(amTime,pmTime);
            }else {
                workday = false;
            }
        }
        return workday;
    }

    private boolean isFreeAttendWorkDay(EmployeeMonthDetailVO employeeMonthDetail,AttendCalendar todayAttendCalendar){
        boolean workday =  false;
        Map jsonObject = JSONObject.parseObject(employeeMonthDetail.getFreeAttendRule());
        if(AssertUtil.isNotEmpty(jsonObject)) {
            int week = AtdcTimeUtil.getWeekNum(todayAttendCalendar.getWeek());
            String iswork = (String) jsonObject.get(ConverUtil.object2String(week));
            if(AssertUtil.isEmpty(iswork)) {
                workday = false;
            }else {
                if(iswork.equals("1")) {
                    workday = true;
                }
            }
        }
        return workday;
    }

    private void setStartAndEndTime(String amTime,String pmTime) {
        this.amTime = amTime;
        this.pmTime = pmTime;
        // 时间以秒数结尾
        String Suffix = ":00";
        // 设置上午班次打卡的截止时间 12:00:00
        this.morningEndTime = AtdcTimeUtil.getEndTime(amTime)
            + Suffix;
        // 设置上午班次打卡的开始时间 09:00:00
        this.morningStartTime = AtdcTimeUtil.getStartTime(amTime) + Suffix;
        // 设置下午班次打卡的截止时间 18:00:00
        this.afternoonEndTime = AtdcTimeUtil
            .getEndTime(pmTime) + Suffix;
    }

    private void dealRemark(AttendCalendar todayAttendCalendar,EmployeeMonthDetailVO employeeMonthDetail) {
        if (todayAttendCalendar.getStatus()==1) {
            employeeMonthDetail.setRemark(todayAttendCalendar.getRemark());
        } else {
            employeeMonthDetail.setRemark(AtdcConsts.REMARK.WEEKEND);
        }
        employeeMonthDetail.setGoWorkDesc(employeeMonthDetail.getRemark());
        employeeMonthDetail.setLeaveWorkDesc(employeeMonthDetail.getRemark());
    }

    private EmployeeMonthDetailVO statisticsRestdayAttend(List<AttendEntity> attendList,EmployeeMonthDetailVO detail,AttendCalendar todayAttendCalendar){
        detail.setRecordState(1);
        if (todayAttendCalendar.getStatus()==1) {
            detail.setRemark(todayAttendCalendar.getRemark());
        } else {
            detail.setRemark(AtdcConsts.REMARK.WEEKEND);
        }
        detail.setGoWorkDesc(detail.getRemark());
        detail.setLeaveWorkDesc(detail.getRemark());
        if (detail.getAttendType()==2) {
            detail.setScheduleShiftName("休息");
            detail.setRemark("休息");
        }
        if (AssertUtil.isEmpty(attendList)) {
            detail.setRecordState(1);
            detail.setRegionStatus(0);
            return detail;
        }
        int size = attendList.size();
        AttendEntity firstAttend = null;
        AttendEntity lastAttend = null;
        if (size == 1) {
            firstAttend = attendList.get(0);
            detail.setGoWork(firstAttend.getAttendanceTime());
            detail.setGoWorkDate(firstAttend.getAttendanceDate());
            detail.setGoLocation(firstAttend.getLocation());
            detail.setRegionStatus(firstAttend.getStatus());
        }
        if (size > 1) {
            firstAttend = attendList.get(0);
            detail.setGoWork(firstAttend.getAttendanceTime());
            detail.setGoWorkDate(firstAttend.getAttendanceDate());
            detail.setGoLocation(firstAttend.getLocation());
            lastAttend = attendList.get(size - 1);
            detail.setLeaveWork(lastAttend.getAttendanceTime());
            detail.setLeaveWorkDate(lastAttend.getAttendanceDate());
            detail.setLeaveLocation(lastAttend.getLocation());
            int workTime = (int) ((detail.getLeaveWork().getTime() - detail
                .getGoWork().getTime()) / (60 * 1000));
            detail.setWorkMinutes(workTime);
            detail.setRegionStatus((firstAttend.getStatus()+lastAttend.getStatus()) == 0 ? 0 : 1);
        }
        return detail;
    }

    private EmployeeMonthDetailVO statisticsWeekdayFreeAttend(List<AttendEntity> attendList,EmployeeMonthDetailVO detail,AttendCalendar todayAttendCalendar){
        if (AssertUtil.isEmpty(attendList)){
            handleNoAttendanceCase(detail);
        }
        int size = attendList.size();
        if (size == 1) {
            handleSingleFreeAttendanceCase(detail, attendList.get(0));
        }
        if (size > 1) {
            handleMultipleFreeAttendanceCase(detail, attendList);
        }
        detail.setRemark(AtdcConsts.REMARK.WEEKDAY);
        return detail;
    }

    private void handleNoAttendanceCase(EmployeeMonthDetailVO detail){
        detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        detail.setWorkMinutes(0);
        if (detail.getAttendType()==1) {
            detail.setRecordState(2);
        } else {
            detail.setRecordState(1);
        }
        detail.setRegionStatus(0);
    }

    private void handleSingleFreeAttendanceCase(EmployeeMonthDetailVO detail, AttendEntity firstAttend) {
        detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        detail.setGoWork(firstAttend.getAttendanceTime());
        detail.setGoWorkDate(firstAttend.getAttendanceDate());
        detail.setGoLocation(firstAttend.getLocation());
        detail.setRegionStatus(firstAttend.getStatus());
        // 设置工作时长 没有合法的考勤记录 工作时长为0
        detail.setWorkMinutes(0);
        detail.setRecordState(2);
    }

    private void handleMultipleFreeAttendanceCase(EmployeeMonthDetailVO detail,List<AttendEntity> attendList) {
        AttendEntity firstAttend = attendList.get(0);
        AttendEntity lastAttend = attendList.get(attendList.size() - 1);
        detail.setRecordState(1);
        detail.setGoWork(firstAttend.getAttendanceTime());
        detail.setGoWorkDate(firstAttend.getAttendanceDate());
        detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        detail.setGoLocation(firstAttend.getLocation());
        detail.setLeaveWork(lastAttend.getAttendanceTime());
        detail.setLeaveWorkDate(lastAttend.getAttendanceDate());
        detail.setLeaveLocation(lastAttend.getLocation());
        detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        int workTime = (int) ((lastAttend.getAttendanceTime().getTime() - firstAttend.getAttendanceTime().getTime()) / (60 * 1000));
        detail.setWorkMinutes(workTime);
        if (firstAttend.getStatus() + lastAttend.getStatus() > 0) {
            detail.setRegionStatus(AttendEntity.ClockStatus.OutClock.getValue());
        } else {
            detail.setRegionStatus(AttendEntity.ClockStatus.InClock.getValue());
        }
    }

    private EmployeeMonthDetailVO statisticsWeekdayAttend(List<AttendEntity> attendList,EmployeeMonthDetailVO detail,AttendCalendar todayAttendCalendar){
        if (AssertUtil.isEmpty(attendList)){
            handleNoAttendanceCase(detail);
        }
        int size = attendList.size();
        if (size == 1) {
            handleSingleAttendanceCase(detail, attendList.get(0));
        }
        if (size > 1) {
            handleMultipleAttendanceCase(detail, attendList);
        }
        detail.setRemark(AtdcConsts.REMARK.WEEKDAY);
        return detail;
    }

    private void handleSingleAttendanceCase(EmployeeMonthDetailVO detail,AttendEntity firstAttend) {
        //判断是否为上午班次
        if (isMorningHours(firstAttend.getAttendanceTime())) {
            //上午打卡情形
            dealMorningCase(firstAttend,detail,false);
        } else {
            // 设置上午未打卡
            detail.setGoWork(null);
            detail.setGoWorkDate(null);
            detail.setGoLocation(null);
            detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
            dealeAfternoonCase(detail,firstAttend,false);
        }
        // 设置工作时长 没有合法的考勤记录 工作时长为0
        detail.setWorkMinutes(0);
        // 记录是否为外勤
        detail.setRegionStatus(firstAttend.getStatus());
        detail.setRecordState(2);
    }

    private boolean isMorningHours(Date attendanceTime) {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        return this.morningEndTime.compareTo(time) >= 0;
    }

    private boolean isLateClocked(Date attendanceTime) {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        return this.morningStartTime.compareTo(time) < 0;
    }

    private boolean dealMorningCase(AttendEntity firstAttend,EmployeeMonthDetailVO detail,boolean flag){
        //设置上班打卡时间
        detail.setGoWork(firstAttend.getAttendanceTime());
        //设置上午打卡日期
        detail.setGoWorkDate(firstAttend.getAttendanceDate());
        //设置上午打卡地点
        detail.setGoLocation(firstAttend.getLocation());
        //设置下午未打卡
        detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        // 判断上午是否迟到
        if (isLateClocked(firstAttend.getAttendanceTime())){
            String amTime = this.amTime.substring(0, 5);
            Date date = TimeUtil.string2Date(amTime, "HH:mm");
            int lateMinutes = (int) ((detail.getGoWork().getTime() - date
                .getTime()) / (60 * 1000));
            //设置迟到时间
            detail.setLateMinutes(lateMinutes);
            if(lateMinutes<=detail.getAllowLateTime()) {
                // 设置上午已经打卡
                detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
//                flag = true;
                return true;
            } else {
                // 设置上午打卡迟到
                detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.LATE);
                return false;
            }
        } else {
            // 设置上午已经打卡
            detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
//            flag = true;
            return true;
        }

    }

    private void dealeAfternoonCase(EmployeeMonthDetailVO detail,AttendEntity lastAttend,boolean flag){
        //设置下午打卡时间
        detail.setLeaveWork(lastAttend.getAttendanceTime());
        //设置下午打卡日期
        detail.setLeaveWorkDate(lastAttend.getAttendanceDate());
        //设置下午打卡地点
        detail.setLeaveLocation(lastAttend.getLocation());
        //判断是否早退
        if (isEarlyClocked(lastAttend.getAttendanceTime())) {
            //设置下午早退
            detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.EARLY);
            //设置下午早退分钟数
            String pmTime = this.pmTime.substring(6);
            Date date = TimeUtil.string2Date(pmTime, "HH:mm");
            int earlyMinutes = (int) ((date.getTime() - detail.getLeaveWork()
                .getTime()) / (60 * 1000));
            detail.setEarlyMinutes(earlyMinutes);
            //设置考勤状态
            detail.setRecordState(2);
        } else {
            //设置下午已打卡
            detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
            if (flag) {
                //一天的考勤状态都正常
                detail.setRecordState(1);
            } else {
                //上午迟到 考勤异常
                detail.setRecordState(2);
            }
        }
    }

    private boolean isEarlyClocked(Date attendanceTime) {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        return this.afternoonEndTime.compareTo(time) > 0;
    }

    private void handleMultipleAttendanceCase(EmployeeMonthDetailVO detail,List<AttendEntity> attendList) {
        AttendEntity firstAttend = attendList.get(0);
        AttendEntity lastAttend = attendList.get(attendList.size() - 1);
        //判断是否为上午班次
        if (isMorningHours(firstAttend.getAttendanceTime())) {
            //判断上午考勤状态 true为正常 false为迟到
            boolean flag = false;
            //处理上午打卡的情况
            flag = dealMorningCase(firstAttend,detail,flag);
            dealeAfternoonCase(detail,lastAttend,flag);
            int workTime = (int) ((detail.getLeaveWork().getTime() - detail
                .getGoWork().getTime()) / (60 * 1000));
            // 设置工作时长分钟
            detail.setWorkMinutes(workTime);
        } else {
            //设置上午未打卡
            detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
            detail.setGoWork(null);
            detail.setGoWorkDate(null);
            detail.setGoLocation(null);
            dealeAfternoonCase(detail,lastAttend,false);
            detail.setWorkMinutes(0);
        }
        // 记录是否为外勤
        detail.setRegionStatus((firstAttend.getStatus()+lastAttend.getStatus()) == 0 ? 0 : 1);
    }
}

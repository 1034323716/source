/**
 * 文件名：AttendCalendarDaoTest.java
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

import org.junit.Before;
import richinfo.attendance.common.AtdcConsts;
import richinfo.attendance.entity.AttendCalendar;
import richinfo.attendance.entity.AttendCalendar.CalendarStatus;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.util.AtdcTimeUtil;
import richinfo.dbcomponent.service.PersistClientBuilder;
import richinfo.dbcomponent.service.PersistService;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * 
 */
public class AttendCalendarDaoTest
{
    private AttendCalendarDao dao = new AttendCalendarDao();

    /** 数据源信息 */
    private String ATTENDANCE_DATASOURCE = "proxool.attendance";

    protected PersistService attendanceDao = PersistClientBuilder
            .createPersistClient(ATTENDANCE_DATASOURCE);

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

    /**
     * Test method for
     * {@link richinfo.attendance.dao.AttendCalendarDao#
     * queryCalendarBetweenTime(java.util.Date, java.util.Date)}
     * .
     */
//    @Test
    public void testCalendarByYear()
    {
        Date[] days = new  Date[5];
        String dayStr = "2018-10-";

        for (int i=0;i<days.length;i++) {
            try {
                days[i] = new SimpleDateFormat("yyyy-MM-dd").parse(dayStr + (12+i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            AttendCalendar todayAttendCalendar = dao.queryAttendCalendarByDate(days[i]);
 //           System.out.println(todayAttendCalendar.getCalendarDate());
        }

//        System.out.println(days.toString());
//        System.out.println(dao.queryCalendarByYear("2017").size());
    }

    /**
     * Test method for
     * {@link richinfo.attendance.dao.AttendCalendarDao#
     * removeCalendarBetweenTime(java.util.Date, java.util.Date)}
     * .
     */
    // @Test
    public void testRemoveCalendarByYear()
    {
        System.out.println(dao.removeCalendarByYear("2018"));
    }

    /**
     * Test method for
     * {@link richinfo.attendance.dao.AttendCalendarDao#batchSaveAttendCalendar(java.util.List)}
     * .
     */
//     @Test
    public void testBatchSaveAttendCalendar()
    {
        List<AttendCalendar> workDayList = new ArrayList<AttendCalendar>();
        Calendar cal = Calendar.getInstance();

        AttendCalendar temp = new AttendCalendar();
        cal.set(2018, 0, 1, 12, 20);
        // 组装实体对象
        temp.setCalendarDate(cal.getTime());
        temp.setWeek(AtdcTimeUtil.getWeekDesc(cal.getTime()));
        temp.setRemark(AtdcConsts.REMARK.WEEKDAY);
        temp.setStatus(CalendarStatus.Weekday.getValue());
        temp.setCreateTime(new Date());
        // 添加到列表中
        workDayList.add(temp);

        AttendCalendar temp2 = new AttendCalendar();
        cal.set(2018, 0, 2, 10, 20);
        // 组装实体对象
        temp2.setCalendarDate(cal.getTime());
        temp2.setWeek(AtdcTimeUtil.getWeekDesc(cal.getTime()));
        temp2.setRemark(AtdcConsts.REMARK.WEEKDAY);
        temp2.setStatus(CalendarStatus.Weekday.getValue());
        temp2.setCreateTime(new Date());
        // 添加到列表中
        workDayList.add(temp2);

        AttendCalendar temp3 = new AttendCalendar();
        cal.set(2018, 0, 31, 8, 20);
        // 组装实体对象
        temp3.setCalendarDate(cal.getTime());
        temp3.setWeek(AtdcTimeUtil.getWeekDesc(cal.getTime()));
        temp3.setRemark(AtdcConsts.REMARK.HOLIDAY);
        temp3.setStatus(CalendarStatus.Weekday.getValue());
        temp3.setCreateTime(new Date());
        // 添加到列表中
        workDayList.add(temp3);

        AttendCalendar temp4 = new AttendCalendar();
        cal.set(2018, 1, 1, 9, 20);
        // 组装实体对象
        temp4.setCalendarDate(cal.getTime());
        temp4.setWeek(AtdcTimeUtil.getWeekDesc(cal.getTime()));
        temp4.setRemark(AtdcConsts.REMARK.HOLIDAY);
        temp4.setStatus(CalendarStatus.Holiday.getValue());
        temp4.setCreateTime(new Date());
        // 添加到列表中
        workDayList.add(temp4);

        AttendCalendar temp5 = new AttendCalendar();
        cal.set(2018, 11, 31, 9, 20);
        // 组装实体对象
        temp5.setCalendarDate(cal.getTime());
        temp5.setWeek(AtdcTimeUtil.getWeekDesc(cal.getTime()));
        temp5.setRemark(AtdcConsts.REMARK.HOLIDAY);
        temp5.setStatus(CalendarStatus.Holiday.getValue());
        temp5.setCreateTime(new Date());
        // 添加到列表中
        workDayList.add(temp5);

        AttendCalendar temp6 = new AttendCalendar();
        cal.set(2018, 0, 1, 9, 20);
        // 组装实体对象
        temp6.setCalendarDate(cal.getTime());
        temp6.setWeek(AtdcTimeUtil.getWeekDesc(cal.getTime()));
        temp6.setRemark(AtdcConsts.REMARK.NEW_YEAR_DAY);
        temp6.setStatus(CalendarStatus.Holiday.getValue());
        temp6.setCreateTime(new Date());
        // 添加到列表中
        workDayList.add(temp6);

        dao.batchSaveAttendCalendar(workDayList);
    }

    private AttendCalendarDao calendarDao = new AttendCalendarDao();

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();

    private AttendGroupDao attendGroupDao = new AttendGroupDao();

    /** 考勤组 */
    private AttendGroup attendGroup;

    private AttendReportDao reportDao = new AttendReportDao();

//    @Test
//    private void test2() throws PersistException, ParseException {
////        String attendRuleJson = attendGroupDao.queryAttendanceRule(
////                100028L,
////                AttendGroup.GroupStatus.Normal.getValue(),
////                1);
////        Map temp = JSON.parseObject(attendRuleJson);
////        System.out.println("temp>>>>>>>>>>>>>>>>"+temp.toString());
////        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
////        System.out.println("day>>>>>>>>>>>>>>>>"+day);
////        Map attendRuleMap = JSON.parseObject(temp.get(day+"").toString());
////        System.out.println("attendRuleMap>>>>>>>>>>>>>>>>"+attendRuleMap.toString());
//
//        List<EmployeeMonthDetail> employeeMonthDetailList = new ArrayList<EmployeeMonthDetail>();
//
//        // 组装日期信息
//        String date = "2018-07-30";
//        String time = "00:00:13";
//        String timeLeave = "09:00:13";
//
//        //测试 冯司欣 无打卡记录 固定班
//        EmployeeMonthDetail employeeMonthDetail1 = new EmployeeMonthDetail();
//        employeeMonthDetail1.setAttendanceId(101630L);
//        employeeMonthDetail1.setEnterId("483453");
//        employeeMonthDetail1.setUid("B9CBE6EA022EAE09390260F4955B7E20");
//        employeeMonthDetail1.setEmployeeName("冯司欣");
//        // 组装日期信息
//        employeeMonthDetail1.setAttendanceDate(new Date());
//        employeeMonthDetail1.setRemark("工作日");
//        employeeMonthDetail1.setCreateTime(new Date());
//        employeeMonthDetail1.setPhone("12345678999");
//
//        employeeMonthDetail1.setAttendType(AttendGroup.AttendType.Fix.getValue());
//        employeeMonthDetail1.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
//        employeeMonthDetail1.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
//        employeeMonthDetail1.setRecordState(2);
//
//        //测试 陈杏 1次打卡记录 固定班
//        EmployeeMonthDetail employeeMonthDetail2 = new EmployeeMonthDetail();
//        employeeMonthDetail2.setAttendanceId(101630L);
//        employeeMonthDetail2.setEnterId("483453");
//        employeeMonthDetail2.setUid("C4B43CB08604612E3D68DF29AAD668C2");
//        employeeMonthDetail2.setEmployeeName("陈杏");
//        // 组装日期信息
//        employeeMonthDetail2.setAttendanceDate(new Date());
//        employeeMonthDetail2.setRemark("工作日");
//        employeeMonthDetail2.setCreateTime(new Date());
//        employeeMonthDetail2.setPhone("12345678998");
//
//        employeeMonthDetail2.setAttendType(AttendGroup.AttendType.Fix.getValue());
//        employeeMonthDetail2.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
//        //设置上午打卡地点
//        employeeMonthDetail2.setGoLocation("广州信息港");
//        //设置上班打卡时间
//        employeeMonthDetail2.setGoWork(new SimpleDateFormat("HH:mm:ss").parse(time));
//        employeeMonthDetail2.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
//        employeeMonthDetail2.setRecordState(2);
//
//        //测试 江丽玲 多次打卡记录 固定班
//        EmployeeMonthDetail employeeMonthDetail3 = new EmployeeMonthDetail();
//        employeeMonthDetail3.setAttendanceId(101762L);
//        employeeMonthDetail3.setEnterId("483453");
//        employeeMonthDetail3.setUid("9F8396A2C2C3D5DE09608D463AA23B36");
//        employeeMonthDetail3.setEmployeeName("江丽玲");
//        // 组装日期信息
//        employeeMonthDetail3.setAttendanceDate(new Date());
//        employeeMonthDetail3.setRemark("工作日");
//        employeeMonthDetail3.setCreateTime(new Date());
//        employeeMonthDetail3.setPhone("12345678997");
//
//        employeeMonthDetail3.setAttendType(AttendGroup.AttendType.Fix.getValue());
//        employeeMonthDetail3.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
//        //设置上午打卡地点
//        employeeMonthDetail3.setGoLocation("广州信息港");
//        //设置上班打卡时间
//        employeeMonthDetail3.setGoWork(new SimpleDateFormat("HH:mm:ss").parse(time));
//        //设置下班打卡时间
//        employeeMonthDetail3.setLeaveWork(new SimpleDateFormat("HH:mm:ss").parse(timeLeave));
//        employeeMonthDetail3.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
//        employeeMonthDetail3.setRecordState(1);
//
//        employeeMonthDetailList.add(employeeMonthDetail1);
//        employeeMonthDetailList.add(employeeMonthDetail2);
//        employeeMonthDetailList.add(employeeMonthDetail3);
//
//        boolean result = attendanceDao.batchInsert(
//                "attendance.batchSaveEmployeeMonthDetail",
//                employeeMonthDetailList);
//
//        if (result) System.out.println("插入成功");
//
////        List<AttendGroup> allGroups = attendanceDao.queryForList(
////                "attendance.queryAllNormalAttendGroup",
////                AttendGroup.GroupStatus.Normal.getValue());
////        System.out.println(Arrays.toString(allGroups.toArray()));
//
//        //统计今天的数据
////        Date today = new Date();
////        Calendar c = Calendar.getInstance();
////        int hh = c.get(Calendar.HOUR_OF_DAY);
////        int mm = c.get(Calendar.MINUTE);
////        int ss = c.get(Calendar.SECOND);
////        //判断是否是凌晨执行
////        if (true) {
////            c.setTime(today);
////            //凌晨执行时则统计前一天的数据
////            c.add(Calendar.DAY_OF_MONTH, -1);
////            today = c.getTime();
////        }
////
////        AttendCalendar todayAttendCalendar = calendarDao
////                .queryAttendCalendarByDate(today);
////
////        System.out.println(todayAttendCalendar);
//
////        attendGroup.setAttendanceId(100028l);
//
////
////        List<UserInfo> userList = employeeDao.queryUserList(
////                100028L, AttendGroup.GroupStatus.Normal.getValue());
////
////        System.out.println(userList);
//
//    }

}

/**
 * 文件名：EmployeeMonthStatisticsServiceImpl.java
 * 创建日期： 2017年6月14日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月14日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.common.AtdcConsts;
import richinfo.attendance.common.AtdcConsts.SEPARATOR;
import richinfo.attendance.common.ServiceObject;
import richinfo.attendance.dao.*;
import richinfo.attendance.entity.*;
import richinfo.attendance.entity.AttendEntity.ClockStatus;
import richinfo.attendance.entity.AttendGroup.AttendType;
import richinfo.attendance.entity.AttendGroup.GroupStatus;
import richinfo.attendance.entity.AttendGroup.RelyHoliday;
import richinfo.attendance.msg.Constants;
import richinfo.attendance.service.EmployeeMonthStatisticsService;
import richinfo.attendance.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述：员工个人月报详情统计逻辑层
 * 
 */
public class EmployeeMonthStatisticsServiceImpl extends ServiceObject implements
    EmployeeMonthStatisticsService
{
    /** 考勤组 */
    private AttendGroup attendGroup;
    /** 考勤日历，包含统计日期等信息 */
    private AttendCalendar attendCalendar;
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
    /** 弹性开关*/
    private boolean isFlexible = false;
    private Map<String,AttendanceSchedule> currentMonthScheduleMap;
    
    private Map<Long,AttendanceScheduleShift> shiftMap;
    
    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();
    private AttendDao attendDao = new AttendDao();
    private AttendReportDao reportDao = new AttendReportDao();
    private AttendanceScheduleShiftDao shiftDao = new AttendanceScheduleShiftDao();
    private AttendanceScheduleDao scheduleDao = new AttendanceScheduleDao();

    private AttendanceConfig config = AttendanceConfig.getInstance();
    private final Logger logger = LoggerFactory
        .getLogger(EmployeeMonthStatisticsServiceImpl.class);

    /**
     * 以考勤组为单位统计考勤状态，考勤组作为属性构造时传递
     * @param attendGroup 考勤组
     */
    public EmployeeMonthStatisticsServiceImpl(AttendGroup attendGroup,
        AttendCalendar attendCalendar)
    {
        this.attendGroup = attendGroup;
        this.attendCalendar = attendCalendar;
        init();
    }

    /**
     * 初始化时间属性
     */
    private void init()
    {
        // 时间以秒数结尾
        String Suffix = ":00";
        String fixedAttendRule = this.attendGroup.getFixedAttendRule();
        //logger.info("=========================={}|{}",fixedAttendRule,attendGroup.getAttendanceName());
        if (AssertUtil.isNotEmpty(fixedAttendRule)){
            Map temp = JSON.parseObject(fixedAttendRule);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            day = day == 0 ? 7 : day;
            Map  attendRuleMap = JSON.parseObject(String.valueOf(temp.get(String.valueOf(day))));
            if (AssertUtil.isNotEmpty(attendRuleMap)){
               String amTime = (String) attendRuleMap.get("amTime");
               String pmTime = (String) attendRuleMap.get("pmTime");
                // 设置上午班次打卡的截止时间
                this.morningEndTime = AtdcTimeUtil.getEndTime(amTime)
                        + Suffix;
                // 设置上午班次打卡的开始时间
                this.morningStartTime = AtdcTimeUtil.getStartTime(amTime) + Suffix;
                // 设置下午班次打卡的截止时间
                this.afternoonEndTime = AtdcTimeUtil.getEndTime(pmTime) + Suffix;
            }
        }/*else {
            // 设置上午班次打卡的截止时间
            this.morningEndTime = AtdcTimeUtil.getEndTime(attendGroup.getAmTime())
                    + Suffix;
            // 设置上午班次打卡的开始时间
            this.morningStartTime = AtdcTimeUtil.getStartTime(attendGroup
                    .getAmTime()) + Suffix;
            // 设置下午班次打卡的截止时间
            this.afternoonEndTime = AtdcTimeUtil
                    .getEndTime(attendGroup.getPmTime()) + Suffix;
        }*/

        if(attendGroup.getAttendType()==AttendType.Schedule.getValue())
        {
        	currentMonthScheduleMap = new HashMap<>();
        	// 考勤组所有用户的本月排班
            List<AttendanceSchedule> currentMonthScheduleList = scheduleDao
                .querySchedule(attendGroup.getAttendanceId(),
                AtdcTimeUtil.getCurrentYearMonth(attendCalendar.getCalendarDate()), null);
            for(AttendanceSchedule schedult : currentMonthScheduleList)
            {
            	currentMonthScheduleMap.put(schedult.getUid(), schedult);
            	logger.debug("schedult uin={}|schedult={}",schedult.getUid(),schedult);
            }
            
         // 根据考勤组ID查询排班班次
            shiftMap = new HashMap<>();
            List<AttendanceScheduleShift> shiftList = shiftDao
                .queryShiftByAttendanceId(attendGroup.getAttendanceId());
            for(AttendanceScheduleShift shift : shiftList){
            	shiftMap.put(shift.getScheduleShiftId(), shift);
            }
        }
    }
    
    /**
     * 初始化时间属性
     */
    private void setStartAndEndTime(String amTime,String pmTime)
    {
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


    /*
     * (non-Javadoc)
     * 
     * @see richinfo.attendance.service.EmployeeMonthStatisticsService#
     * employeeMonthDatailStatistics(richinfo.attendance.entity.AttendGroup)
     */
    @Override
    public void employeeMonthDatailStatistics()
    {
        long start = System.currentTimeMillis();
        logger.info("employeeMonthDatailStatistics start. group={}|attendCalendar={}",
            attendGroup, attendCalendar);

        // 获取考勤组的员工列表，暂时不考虑分页
        List<UserInfo> userList = employeeDao.queryUserList(attendGroup.getEnterId(),attendGroup.getAttendanceId(), GroupStatus.Normal.getValue());

        // 若员工列表为空，直接结束
        if (AssertUtil.isEmpty(userList)) {
            logger.warn("employeeMonthDatailStatistics end,UserList is empty.group={}", attendGroup);
            return;
        }

        List<EmployeeMonthDetail> list = new ArrayList<EmployeeMonthDetail>();

        Map<String, Object> map = new HashMap<String, Object>();

        // 根据员工列表循环统计
        for (UserInfo user : userList) {
            // 统计员工考勤
            EmployeeMonthDetail detail = statisticsUserAttend(user);

            map.put("uid", user.getUid());
            map.put("attendanceDate", attendCalendar.getCalendarDate());

            EmployeeMonthDetail employeeMonthDetail = employeeDao.queryNomMonthly(map);
            //预防从跑过去日子数据
            if (AssertUtil.isNotEmpty(employeeMonthDetail) && AssertUtil.isNotEmpty(employeeMonthDetail.getRecordState())){
                int recordState = employeeMonthDetail.getRecordState();
                if (detail.getAttendType().equals(employeeMonthDetail.getAttendType())) {
                    detail.setRecordState(recordState);
                }

                if (4 == recordState){
                    detail.setGoWorkDesc(employeeMonthDetail.getGoWorkDesc());
                    detail.setLeaveWorkDesc(employeeMonthDetail.getLeaveWorkDesc());
                    detail.setEarlyMinutes(0);
                    detail.setLateMinutes(0);
                }
            }

            if (AssertUtil.isNotEmpty(employeeMonthDetail) && !employeeMonthDetail.getAttendanceId().equals(detail.getAttendanceId()) ) {
                logger.info("不一致employeeMonthDetail.getAttendanceId()={}||||||detail.getAttendanceId()={}",employeeMonthDetail.getAttendanceId() , detail.getAttendanceId());
//                detail.setAttendanceId(employeeMonthDetail.getAttendanceId());
                detail.setMonthRcdId(employeeMonthDetail.getMonthRcdId());
                detail.setAttendType(employeeMonthDetail.getAttendType());
                employeeDao.updateEmployeeMonthDetailById(detail);
//                attendanceDao.update("attendance.updateEmployeeMonthDetailById",detail);
            }else{
                list.add(detail);
            }

//            logger.info("detail.getMonthRcdId() = {}",detail.getMonthRcdId());

        }
        // 批量入库统计结果
        boolean result = reportDao.batchSaveEmployeeMonthDetail(list);

        logger
            .info(
                "employeeMonthDatailStatistics end. useTime={}|group={}|attendCalendar={}|size={}|result={}",
                AttendanceUtil.getUseTime(start), attendGroup, attendCalendar,
                list.size(), result);
    }

    /**
     * 统计单个员工的考勤状态
     * @param user
     */
    private EmployeeMonthDetail statisticsUserAttend(UserInfo user)
    {
        EmployeeMonthDetail detail = null;
        List<AttendEntity> attendList = attendDao.queryEmployRecord(user.getUid(), attendCalendar.getCalendarDate());

        if(attendGroup.getAttendType()==AttendType.Fix.getValue())// 判断是否固定班
             {
        	//是否工作日
        	boolean workday = isFixedAttendWorkDay();
//        	logger.debug("attendCalendar.isWeekDay() = {}",attendCalendar.isWeekDay());
        	//是否是补班日期
            boolean rowday = getRowDays();
//        	logger.debug("判断是不是补班日期 = {}",rowday);
        	//补班日期判断 需要在非节假日和工作日之前
             if (rowday && attendGroup.getRelyHoliday()==RelyHoliday.NotRely.getValue()) {
                 detail = statisticsWeekdayAttend(user, attendList);
             }
             //不是工作日且按国家法定节假日处理考勤 即不需要打卡
             else if ((!attendCalendar.isWeekDay()
                &&attendGroup.getRelyHoliday()==RelyHoliday.NotRely.getValue())) {
        		// 进行非工作日的统计
        		detail = statisticsRestdayAttend(user, attendList);
                if (attendCalendar.getStatus() == AttendCalendar.CalendarStatus.Holiday.getValue()){
                    detail.setRemark(attendCalendar.getRemark());
                }else{
                    detail.setRemark(AtdcConsts.REMARK.WEEKEND);
                }
                detail.setGoWorkDesc(detail.getRemark());
                detail.setLeaveWorkDesc(detail.getRemark());
             }
            //不是节假日，并且不是工作日
        	else if(attendCalendar.isWeekDay()&&!workday) {
        		detail = statisticsRestdayAttend(user, attendList);
                if (attendCalendar.getStatus()== AttendCalendar.CalendarStatus.Holiday.getValue()){
                    detail.setRemark(attendCalendar.getRemark());
                }else{
                    detail.setRemark(AtdcConsts.REMARK.WEEKEND);
                }
                detail.setGoWorkDesc(detail.getRemark());
                detail.setLeaveWorkDesc(detail.getRemark());
            }
        	//节假日，并且节假日需要打卡，并且不是工作日
        	else if(!attendCalendar.isWeekDay()
        			&&attendGroup.getRelyHoliday()==RelyHoliday.Rely.getValue()
        			&&!workday) {
                // 进行非工作日的统计
        		detail = statisticsRestdayAttend(user, attendList);
                if (attendCalendar.getStatus()== AttendCalendar.CalendarStatus.Holiday.getValue()){
                    detail.setRemark(attendCalendar.getRemark());
                }else{
                    detail.setRemark(AtdcConsts.REMARK.WEEKEND);
                }
                detail.setGoWorkDesc(detail.getRemark());
                detail.setLeaveWorkDesc(detail.getRemark());
            }
            else
            {
                detail = statisticsWeekdayAttend(user, attendList);
            }
        }
        // 判断是否自由班
        else if(attendGroup.getAttendType()==AttendType.Free.getValue())
        {
        	//是否工作日
        	boolean workday = isFreeAttendWorkDay();
        	logger.info("Free workday={}",workday);
        	//节假日并且中国节假日不需要打卡
        	if ((!attendCalendar.isWeekDay()
        			&&attendGroup.getRelyHoliday()==RelyHoliday.NotRely.getValue()))
        	{
        		// 进行非工作日的统计
        		detail = statisticsRestdayAttend(user, attendList);
                if (attendCalendar.getStatus()== AttendCalendar.CalendarStatus.Holiday.getValue()){
                    detail.setRemark(attendCalendar.getRemark());
                }else{
                    detail.setRemark(AtdcConsts.REMARK.WEEKEND);
                }
                detail.setGoWorkDesc(detail.getRemark());
                detail.setLeaveWorkDesc(detail.getRemark());
        	}
            //不是节假日，并且不是工作日
        	else if(attendCalendar.isWeekDay()&&!workday)
            {
        		detail = statisticsRestdayAttend(user, attendList);
                if (attendCalendar.getStatus()== AttendCalendar.CalendarStatus.Holiday.getValue()){
                    detail.setRemark(attendCalendar.getRemark());
                }else{
                    detail.setRemark(AtdcConsts.REMARK.WEEKEND);
                }
                detail.setGoWorkDesc(detail.getRemark());
                detail.setLeaveWorkDesc(detail.getRemark());
            }
        	//节假日，并且节假日需要打卡，并且不是工作日
        	else if(!attendCalendar.isWeekDay()
        			&&attendGroup.getRelyHoliday()==RelyHoliday.Rely.getValue()
        			&&!workday)
            {
                // 进行非工作日的统计
        		detail = statisticsRestdayAttend(user, attendList);
                if (attendCalendar.getStatus()== AttendCalendar.CalendarStatus.Holiday.getValue()){
                    detail.setRemark(attendCalendar.getRemark());
                }else{
                    detail.setRemark(AtdcConsts.REMARK.WEEKEND);
                }
                detail.setGoWorkDesc(detail.getRemark());
                detail.setLeaveWorkDesc(detail.getRemark());
            }
            else
            {
                // 进行工作日的统计
                detail = statisticsWeekdayFreeAttend(user, attendList);
            }
        }
//        排班制
        else
        {
            //由于之前判断排班的工作日时会用到scheduleShiftId在下架排班功能之后为避免出现空值异常
            //故不做排班工作日判断 但保留该逻辑给后续的版本
            //排班不区分工作日非工作日 统一处理
            detail = statisticsRestdayAttend(user, attendList);
        }

        //如果用户要手机号码的话 也要记录上
        if (AssertUtil.isNotEmpty(user.getPhone())) detail.setPhone(user.getPhone());

        return detail;
    }
    
    /**
     * 获取（排班制）某天的考勤时间（签到/签退时间）
     * @param date
     * @param workTime 08:00-12:00 或 22:00-04:00
     * @param msgType
     * @return
     */
    private Date getScheduleAttendDate(Date date, String workTime, int msgType)
    {
        if (AssertUtil.isNotEmpty(workTime))
        {
            Date attendDate = null;
            // 签到时间
            if (Constants.MsgType.Sign.getValue() == msgType)
            {
                attendDate = AttendanceUtil.getAttendDate(date, workTime, 0);
            }
            // 签退时间
            else if (Constants.MsgType.SignOut.getValue() == msgType)
            {
                // 如果是跨天的工作时间22:00-04:00,则给date加1天
                if (AtdcTimeUtil.isCrossWorkTime(workTime))
                {
                    date = AtdcTimeUtil.getNextDay(date);
                }
                attendDate = AttendanceUtil.getAttendDate(date, workTime, 1);
            }
            return attendDate;
        }
        else
        {
            return null;
        }
    }

    /**
     * 统计固定班工作日的考勤状态
     * @param user
     * @param attendList
     */
    private EmployeeMonthDetail statisticsWeekdayAttend(UserInfo user,
        List<AttendEntity> attendList)
    {
        // 获取记录的基本信息
        EmployeeMonthDetail detail = assembleEmployeeMonthDetail(user);
        //设置考勤组类型
        detail.setAttendType(AttendType.Fix.getValue());

//        //判断是否使用了弹性班制规则
//        if (AssertUtil.isNotEmpty(attendGroup.getUseFlexibleRule())) {
//            if (attendGroup.getUseFlexibleRule()==0) {
//                setStartAndEndTimeInFlexibleRule();
//            }
//        }

        // attendList需要进行非空判断，不能直接使用size()
        if (AssertUtil.isEmpty(attendList))
        {
            // 无打卡记录
            handleNoAttendanceCase(detail);
        }
        int size = attendList.size();
        if (size == 1)
        {
            // 仅一次打卡记录
            handleSingleAttendanceCase(detail, attendList.get(0));
        }
        if (size > 1)
        {
            // 多次打卡记录
            handleMultipleAttendanceCase(detail, attendList);
        }
        //设置备注 为工作日
        detail.setRemark(AtdcConsts.REMARK.WEEKDAY);
        return detail;
    }
    
    
    /**
     * 统计自由班工作日的考勤状态
     * @param user
     * @param attendList
     */
    private EmployeeMonthDetail statisticsWeekdayFreeAttend(UserInfo user,
        List<AttendEntity> attendList)
    {
        // 获取记录的基本信息
        EmployeeMonthDetail detail = assembleEmployeeMonthDetail(user);
        detail.setAttendType(attendGroup.getAttendType());

        // attendList需要进行非空判断，不能直接使用size()
        if (AssertUtil.isEmpty(attendList))
        {
            // 无打卡记录
            handleNoAttendanceCase(detail);
        }
        int size = attendList.size();
        if (size == 1)
        {
            // 仅一次打卡记录
            handleSingleFreeAttendanceCase(detail, attendList.get(0));
        }
        if (size > 1)
        {
            // 多次打卡记录
            handleMultipleFreeAttendanceCase(detail, attendList);
        }
        //设置备注 为工作日
        detail.setRemark(AtdcConsts.REMARK.WEEKDAY);
        return detail;
    }
    
    /**
     * 统计排班班工作日的考勤状态
     * @param user
     * @param attendList
     */
    private EmployeeMonthDetail statisticsWeekdayScheduleAttend(UserInfo user,
        List<AttendEntity> attendList,AttendanceScheduleShift shift)
    {
    	//TODO 
        // 获取记录的基本信息
        EmployeeMonthDetail detail = assembleEmployeeMonthDetail(user);
        detail.setAttendType(AttendType.Schedule.getValue());
        if(AssertUtil.isNotEmpty(shift))
        {
        	detail.setScheduleShiftId(shift.getScheduleShiftId());
        	detail.setScheduleShiftName(shift.getScheduleShiftName());
        	detail.setScheduleShiftWorkTime(shift.getWorkTime());
        }else{
        	detail.setScheduleShiftId(0L);
        	detail.setScheduleShiftName("休息");
        }

        // attendList需要进行非空判断，不能直接使用size()
        if (AssertUtil.isEmpty(attendList))
        {
            // 无打卡记录
            handleNoAttendanceCase(detail);
        }
        int size = attendList.size();
        if (size == 1)
        {
            // 仅一次打卡记录
            handleSingleScheduleAttendanceCase(detail, attendList.get(0),shift);
        }
        if (size > 1)
        {
            // 多次打卡记录
            handleMultipleScheduleAttendanceCase(detail, attendList,shift);
        }
        //设置备注 为休息日
        detail.setRemark(AtdcConsts.REMARK.HOLIDAY);
        return detail;
    }

    /**
     * 处理固定班有多次考勤记录的情况
     * @param detail
     * @param attendList
     */
    private void handleMultipleAttendanceCase(EmployeeMonthDetail detail,
        List<AttendEntity> attendList)
    {

        AttendEntity firstAttend = attendList.get(0);
        AttendEntity lastAttend = attendList.get(attendList.size() - 1);

        //判断是否为上午班次

        if (isMorningHours(firstAttend.getAttendanceTime())) {
            //判断上午考勤状态 true为正常 false为迟到
            boolean flag = false;
            //处理上午打卡的情况
            flag = dealMorningCase(firstAttend,detail,flag);
            isFlexible = true;
            dealeAfternoonCase(detail,lastAttend,flag);

            int workTime = 0;
            try {
                workTime = (int) (( new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(detail.getLeaveWork(),"HH:mm")).getTime() -
                    new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(detail.getGoWork(),"HH:mm")).getTime()) / (60 * 1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

    /**
     * 处理下午考勤的情况
     * @param detail
     * @param lastAttend
     * @param flag
     */
    private void dealeAfternoonCase(EmployeeMonthDetail detail,AttendEntity lastAttend,boolean flag){
        //设置下午打卡时间
        detail.setLeaveWork(lastAttend.getAttendanceTime());
        //设置下午打卡日期
        detail.setLeaveWorkDate(lastAttend.getAttendanceDate());
        //设置下午打卡地点
        detail.setLeaveLocation(lastAttend.getLocation());
        //判断是否早退
        if (isEarlyClocked(lastAttend.getAttendanceTime(),detail)) {
            //设置下午早退
            detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.EARLY);
            //设置下午早退分钟数
            String pmTime = this.pmTime.substring(6);
            Date date = TimeUtil.string2Date(pmTime, "HH:mm");
            int earlyMinutes = 0;
            try {
                earlyMinutes = (int) ((date.getTime() - new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(detail.getLeaveWork(),"HH:mm")).getTime()) / (60 * 1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
    
    /**
     * 处理自由班有多次考勤记录的情况
     * @param detail
     * @param attendList
     */
    private void handleMultipleFreeAttendanceCase(EmployeeMonthDetail detail,
        List<AttendEntity> attendList)
    {
        // 获取首尾两次考勤记录
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
        //  计算工作时长(单位为分钟)，后续增加排班制时需要进行修改
        int workTime = (int) ((lastAttend.getAttendanceTime().getTime() - firstAttend
            .getAttendanceTime().getTime()) / (60 * 1000));
        detail.setWorkMinutes(workTime);
        // 判断是否为外勤打卡
        if (firstAttend.getStatus() + lastAttend.getStatus() > 0)
        {
            // 上午下午考勤，有一次为外勤，即为外勤
            detail.setRegionStatus(ClockStatus.OutClock.getValue());
        }
        else
        {
            // 上午下午考勤，均为公司打卡，即为公司打卡
            detail.setRegionStatus(ClockStatus.InClock.getValue());
        }
    }


    /**
     * 处理排班有多次考勤记录的情况
     * @param detail
     * @param attendList
     */
    private void handleMultipleScheduleAttendanceCase(EmployeeMonthDetail detail,
        List<AttendEntity> attendList,AttendanceScheduleShift shift)
    {
    	 // 获取首尾两次考勤记录
        AttendEntity firstAttend = attendList.get(0);
        AttendEntity lastAttend = attendList.get(attendList.size() - 1);
        //  计算迟到时长，排班时还需再优化
        String amDate = TimeUtil.date2String(attendCalendar.getCalendarDate(),"yyyy-MM-dd") + 
        		  " " + shift.getWorkTime().substring(0, 5);
        Date date = TimeUtil.string2Date(amDate, "yyyy-MM-dd HH:mm");
        
        Date goWork = TimeUtil.string2Date((TimeUtil.date2String(firstAttend.getAttendanceDate(), "yyyy-MM-dd") +
        		  " " + TimeUtil.date2String(firstAttend.getAttendanceTime(), "HH:mm")),"yyyy-MM-dd HH:mm");
        Date leaveWork = TimeUtil.string2Date((TimeUtil.date2String(lastAttend.getAttendanceDate(), "yyyy-MM-dd") +
      		  " " + TimeUtil.date2String(lastAttend.getAttendanceTime(), "HH:mm")),"yyyy-MM-dd HH:mm");
     
        //  计算工作时长(单位为分钟)，后续增加排班制时需要进行修改
        int workTime = (int) ((leaveWork.getTime() - goWork
            .getTime()) / (60 * 1000));
        detail.setWorkMinutes(workTime);

        // 记录上午考勤状态
        handleMorningScheduleAttend(detail, firstAttend,shift);
        // 记录下午考勤状态
        handleAfternoonScheduleAttend(detail, lastAttend,shift);

        // 判断该天考勤打卡记录是否正常
        if (AtdcConsts.ATTEND_DESC.NORMAL.equals(detail.getGoWorkDesc())
            && AtdcConsts.ATTEND_DESC.NORMAL.equals(detail.getLeaveWorkDesc()))
        {
            detail.setRecordState(1);
        }
        else
        {
            detail.setRecordState(2);
        }

        // 判断是否为外勤打卡
        if (firstAttend.getStatus() + lastAttend.getStatus() > 0)
        {
            // 上午下午考勤，有一次为外勤，即为外勤
            detail.setRegionStatus(ClockStatus.OutClock.getValue());
        }
        else
        {
            // 上午下午考勤，均为公司打卡，即为公司打卡
            detail.setRegionStatus(ClockStatus.InClock.getValue());
        }
    }

    /**
     * 处理固定班只有一次考勤记录的情况
     * @param detail
     * @param firstAttend
     */
    private void handleSingleAttendanceCase(EmployeeMonthDetail detail,
        AttendEntity firstAttend) {
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

    /**
     * 处理上午打卡的情况
     * @param firstAttend
     * @param detail
     */
    private boolean dealMorningCase(AttendEntity firstAttend,EmployeeMonthDetail detail,boolean flag){
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
            int lateMinutes = 0;
            try {
                lateMinutes = (int) (( new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(detail.getGoWork(),"HH:mm")).getTime() -
                    date.getTime()) / (60 * 1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //设置迟到时间
            detail.setLateMinutes(lateMinutes);
            if(lateMinutes<=attendGroup.getAllowLateTime()) {
                // 设置上午已经打卡
                detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
                return true;
            } else {
                // 设置上午打卡迟到
                detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.LATE);
                return false;
            }
        } else {
            // 设置上午已经打卡
            detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
            return true;
        }

    }

    /**
     * 处理自由班只有一次考勤记录的情况
     * @param detail
     * @param firstAttend
     */
    private void handleSingleFreeAttendanceCase(EmployeeMonthDetail detail,
        AttendEntity firstAttend)
    {
            // 下午设置为未打卡
        detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
            // 判断上午的考勤状态
        detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        detail.setGoWork(firstAttend.getAttendanceTime());
        detail.setGoWorkDate(firstAttend.getAttendanceDate());
        detail.setGoLocation(firstAttend.getLocation());
//        handleMorningAttend(detail, firstAttend);
        // 记录是否为外勤
        detail.setRegionStatus(firstAttend.getStatus());
        // 设置工作时长 没有合法的考勤记录 工作时长为0
        detail.setWorkMinutes(0);
        detail.setRecordState(2);
    }
    
    /**
     * 处理排班只有一次考勤记录的情况
     * @param detail
     * @param firstAttend
     */
    private void handleSingleScheduleAttendanceCase(EmployeeMonthDetail detail,
        AttendEntity firstAttend, AttendanceScheduleShift shift)
    {
            // 下午设置为未打卡
        detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
            // 判断上午的考勤状态
        detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        detail.setGoWork(firstAttend.getAttendanceTime());
        detail.setGoWorkDate(firstAttend.getAttendanceDate());
        detail.setGoLocation(firstAttend.getLocation());
        handleMorningScheduleAttend(detail, firstAttend,shift);
        // 记录是否为外勤
        detail.setRegionStatus(firstAttend.getStatus());
        detail.setRecordState(2);
    }

    /**
     * 处理固定班没有考勤记录的情况
     * @param detail
     */
    private void handleNoAttendanceCase(EmployeeMonthDetail detail) {
        // 设置上午未打卡
        detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        detail.setGoWork(null);
        detail.setGoWorkDate(null);
        detail.setGoLocation(null);

        //设置下午未打卡
        detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        detail.setLeaveWork(null);
        detail.setLeaveWorkDate(null);
        detail.setLeaveLocation(null);
        // 设置工作时长 没有合法的考勤记录 工作时长为0
        detail.setWorkMinutes(0);
        //无打卡记录的数据 仅固定班设为异常 排班自由班均正常
        if (attendGroup.getAttendType()==AttendType.Fix.getValue()) {
            detail.setRecordState(2);
        } else {
            detail.setRecordState(1);
        }
        // 设置外勤状态
        detail.setRegionStatus(0);
    }

    /**
     * 统计固定板/自由班   非工作日的考勤状态,仅记录时间，不记录考勤状态
     * @param user
     * @param attendList
     */
    private EmployeeMonthDetail statisticsRestdayAttend(UserInfo user,
        List<AttendEntity> attendList)
    {
        // 获取记录的基本信息
        EmployeeMonthDetail detail = assembleEmployeeMonthDetail(user);
        // 无打卡记录，attendList需要进行非空判断，不能直接使用size()
        detail.setRecordState(1);
        detail.setAttendType(attendGroup.getAttendType());
        //设置备注
        if (attendCalendar.getStatus()== AttendCalendar.CalendarStatus.Holiday.getValue()){
            detail.setRemark(attendCalendar.getRemark());
        }else{
            detail.setRemark(AtdcConsts.REMARK.WEEKEND);
        }
        detail.setGoWorkDesc(detail.getRemark());
        detail.setLeaveWorkDesc(detail.getRemark());

        //处理排班的情况
        if(attendGroup.getAttendType()==AttendType.Schedule.getValue())
        {
            //设置备注和考勤名为休息
            detail.setScheduleShiftName("休息");
            detail.setRemark("休息");
        }
        //无打卡记录
        if (AssertUtil.isEmpty(attendList))
        {
            // 直接返回
            // 设定该条考勤记录状态为正常（非工作日统一是正常的）
            detail.setRecordState(1);
            detail.setRegionStatus(0);
            return detail;
        }
        // 获取打卡次数
        int size = attendList.size();
        // 首尾打卡记录
        AttendEntity firstAttend = null;
        AttendEntity lastAttend = null;
        // 只有一次打卡记录
        if (size == 1)
        {
            firstAttend = attendList.get(0);
            detail.setGoWork(firstAttend.getAttendanceTime());
            detail.setGoLocation(firstAttend.getLocation());
            detail.setRegionStatus(firstAttend.getStatus());
        }
        // 多次打卡记录
        if (size > 1)
        {
            // 首次打卡记录
            firstAttend = attendList.get(0);
            // 记录上午打卡时间、地点
            detail.setGoWork(firstAttend.getAttendanceTime());
            detail.setGoWorkDate(firstAttend.getAttendanceDate());
            detail.setGoLocation(firstAttend.getLocation());

            // 尾次打卡记录
            lastAttend = attendList.get(size - 1);
            // 记录下午打卡时间、地点
            detail.setLeaveWork(lastAttend.getAttendanceTime());
            detail.setLeaveWorkDate(lastAttend.getAttendanceDate());
            detail.setLeaveLocation(lastAttend.getLocation());

            //    计算工作时长(单位为分钟)，后续增加排班制时需要进行修改
            int workTime = 0;
            try {
                workTime = (int) (( new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(detail.getLeaveWork(),"HH:mm")).getTime() -
                    new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(detail.getGoWork(),"HH:mm")).getTime()) / (60 * 1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            detail.setWorkMinutes(workTime);
            detail.setRegionStatus((firstAttend.getStatus()+lastAttend.getStatus()) == 0 ? 0 : 1);
        }

        // 不记录考勤状态,不记录是否为外勤
        return detail;
    }

    /**
     * 处理排班上午的考勤状态，记录时间、地点、状态
     * @param detail
//     * @param attend 工作日上午的打卡记录
     */
    private void handleMorningScheduleAttend(EmployeeMonthDetail detail,
        AttendEntity morningAttend,AttendanceScheduleShift shift)
    {
        // 记录上午打卡时间、地点
        detail.setGoWork(morningAttend.getAttendanceTime());
        detail.setGoWorkDate(morningAttend.getAttendanceDate());
        detail.setGoLocation(morningAttend.getLocation());
        //  计算迟到时长，排班时还需再优化
        String amDate = TimeUtil.date2String(attendCalendar.getCalendarDate(),"yyyy-MM-dd") + 
        		  " " + shift.getWorkTime().substring(0, 5);
        Date date = TimeUtil.string2Date(amDate, "yyyy-MM-dd HH:mm");
        
        Date goWork = TimeUtil.string2Date((TimeUtil.date2String(morningAttend.getAttendanceDate(), "yyyy-MM-dd") +
        		  " " + TimeUtil.date2String(morningAttend.getAttendanceTime(), "HH:mm")),"yyyy-MM-dd HH:mm");
        int lateMinutes = (int) ((goWork.getTime() - date
            .getTime()) / (60 * 1000));
        detail.setLateMinutes(lateMinutes);
        // 上午迟到
        if(lateMinutes<=attendGroup.getAllowLateTime())
        {
        	detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        }else
        {
        	detail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.LATE);
        }
    }
    
    
    /**
     * 处理排班下午的考勤状态，记录时间、地点、状态
     * @param detail
//     * @param attend 工作日下午的打卡记录
     */
    private void handleAfternoonScheduleAttend(EmployeeMonthDetail detail,
        AttendEntity afternoonAttend,AttendanceScheduleShift shift)
    {
        // 记录下午打卡时间、地点
        detail.setLeaveWork(afternoonAttend.getAttendanceTime());
        detail.setLeaveWorkDate(afternoonAttend.getAttendanceDate());
        detail.setLeaveLocation(afternoonAttend.getLocation());
        // 判断下午是否早退
        //  计算迟到时长，排班时还需再优化
        int amTime = ConverUtil.string2Int(shift.getWorkTime().substring(0, 2));
        int pmTime = ConverUtil.string2Int(shift.getWorkTime().substring(6, 8));
        //晚班跨天
        String pmDate = TimeUtil.date2String(attendCalendar.getCalendarDate(),"yyyy-MM-dd") + 
        		  " " + shift.getWorkTime().substring(6);
        if(amTime>pmTime){
        	//下班时间+1
        	pmDate = TimeUtil.plusDays(TimeUtil.date2String(attendCalendar.getCalendarDate(),"yyyy-MM-dd"), 1) + 
          		  " " + shift.getWorkTime().substring(6);
        }
        
        Date date = TimeUtil.string2Date(pmDate, "yyyy-MM-dd HH:mm");
        
        Date leaveWork = TimeUtil.string2Date((TimeUtil.date2String(afternoonAttend.getAttendanceDate(), "yyyy-MM-dd") +
        		  " " + TimeUtil.date2String(afternoonAttend.getAttendanceTime(), "HH:mm")),"yyyy-MM-dd HH:mm");
        //  计算早退时长，排班时还需再优化
        int earlyMinutes = (int) ((date.getTime() - leaveWork
            .getTime()) / (60 * 1000));
        
        if(earlyMinutes>0)
        {
        	detail.setEarlyMinutes(earlyMinutes);
        	// 下午早退
        	detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.EARLY);
        }else
        {
        	// 下午正常
            detail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        }
            
    }

    /**
     * 判断固定班是否为上午班次
     * @param attendanceTime
     * @return
     */
    private boolean isMorningHours(Date attendanceTime)
    {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime, TimeUtil.BASE_TIME_FORMAT);
        setStartAndEndTimeInFlexibleRule();
//        if (this.morningEndTime.compareTo(time) >= 0) {
//            // 设置上午班次打卡的开始时间 09:00:00
//            this.morningStartTime = AtdcTimeUtil.getStartTime(this.amTime) + ":00";
//            // 设置下午班次打卡的截止时间 18:00:00
//            this.afternoonEndTime = AtdcTimeUtil.getEndTime(this.pmTime) + ":00";
//            return true;
//        } else {
//            return false;
//        }

        return this.morningEndTime.compareTo(time) >= 0;
    }

    /**
     * 判断是否迟到
     * @param attendanceTime
     * @return
     */
    private boolean isLateClocked(Date attendanceTime)
    {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        return this.morningStartTime.compareTo(time) < 0;
    }

    /**
     * 判断是否早退
     * @param
     * @return
     */
    private boolean isEarlyClocked(Date attendanceTime,EmployeeMonthDetail monthDetail)
    {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        if (isFlexible && attendGroup.getUseFlexibleRule()==0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtil.BASE_TIME_FORMAT);
            long worktimeDistance = 0;
            long realDistance = 0;
            Date morning = new Date();
            try {
                Date afternoon = simpleDateFormat.parse(this.afternoonEndTime);
                morning = simpleDateFormat.parse(this.morningStartTime);
                attendanceTime = simpleDateFormat.parse(time);
                worktimeDistance = (afternoon.getTime() - morning.getTime())/(1000* 60 * 60);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (AssertUtil.isNotEmpty(monthDetail.getGoWork())) {
                realDistance = (attendanceTime.getTime() - monthDetail.getGoWork().getTime())/(1000* 60 * 60);
            } else {
                realDistance = (long) attendGroup.getFlexitime() + (attendanceTime.getTime() - morning.getTime())/(1000* 60 * 60);
            }
            return realDistance<worktimeDistance;
        } else {
            String fixedAttendRule = attendGroup.getFixedAttendRule();
            Map jsonMap = JSON.parseObject(fixedAttendRule);
            int day = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
            Map attendRuleMap = JSON.parseObject(jsonMap.get(day).toString());
            String pmTime = (String)attendRuleMap.get("pmTime");
            this.pmTime = pmTime;
            this.afternoonEndTime = AtdcTimeUtil.getEndTime(pmTime) + ":00";
            return this.afternoonEndTime.compareTo(time) > 0;
        }
    }
    

    /**
     * 组装员工个人月报明细
     * @param user
     * @return
     */
    private EmployeeMonthDetail assembleEmployeeMonthDetail(UserInfo user)
    {
        EmployeeMonthDetail detail = new EmployeeMonthDetail();
        // 组装用户信息
        detail.setAttendanceId(user.getAttendanceId());
        detail.setEnterId(user.getEnterId());
        detail.setUid(user.getUid());
        detail.setEmployeeName(user.getEmployeeName());
        // 组装日期信息
        detail.setAttendanceDate(attendCalendar.getCalendarDate());
        detail.setCreateTime(new Date());
        detail.setPhone(user.getPhone());
        detail.setRecordState(1);
        detail.setLateMinutes(0);
        detail.setEarlyMinutes(0);
        detail.setWorkMinutes(0);
        detail.setScheduleShiftId(0L);
        return detail;
    }
    
    /**
     * 判断是否固定班工作日
     * @return
     */
    private boolean isFixedAttendWorkDay(){
    	boolean workday =  false;
    	    //将固定班的班点规则解析成JSONOBEJECT
           Map jsonObject = JSON.parseObject(attendGroup.getFixedAttendRule());
   
    		if(AssertUtil.isNotEmpty(jsonObject))
    		{
//    			logger.debug("isFixedAttendWorkDay week={}|jsonObject={}",attendCalendar.getWeek(),jsonObject);
                //根据星期几获取来转换成相应数字
    			int week = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
//    			logger.debug("isFixedAttendWorkDay weekInt={}",week);
    			//解析固定班规则 用数字截取出班次
    			JSONObject workTime = (JSONObject)jsonObject.get(ConverUtil.object2String(week)); 
//    			logger.debug("isFixedAttendWorkDay workTime={}",workTime);
                //若解析出来有结果 则表明是工作日 若无则表示非工作日
        		if(AssertUtil.isNotEmpty(workTime))
        		{
        			workday = true;
        			//解析出上下班的班点 "amTime":"09:30-12:00","pmTime":"14:00-17:30"
        			String amTime = workTime.get("amTime").toString();
        			String pmTime = workTime.get("pmTime").toString();
        			//这里的封装方法 不能兼容到允许迟到分钟的情况
        			setStartAndEndTime(amTime,pmTime);
        		}else
        		{
        			workday = false;
        		}
        		
    		}
    		

    	return workday;
    }
    
    /**
     * 判断是否自由班工作日
     * @return
     */
    private boolean isFreeAttendWorkDay(){    	
    	boolean workday =  false;
      //  logger.info("判断判断是否自由班工作日===================");

    		Map jsonObject = JSONObject.parseObject(attendGroup.getFreeAttendRule());
    		if(AssertUtil.isNotEmpty(jsonObject))
    		{
    			logger.info("isFreeAttendWorkDay jsonObject={}",jsonObject);
    			int week = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
    			logger.info("isFreeAttendWorkDay week={}",week);
    			String iswork = (String) jsonObject.get(ConverUtil.object2String(week));
    			
    			logger.info("isFreeAttendWorkDay week={}",iswork);
    			if(AssertUtil.isEmpty(iswork))
    			{
    				workday = false;
    			}else
    			{
    				if(iswork.equals("1"))
    				{
    					workday = true;
    				}
    			}
    		}

    	return workday;
    }
    
    /**
     * 获取排班id 如果id>=0 则不是排班工作日
     * @return
     */
    private long getScheduleShiftId(UserInfo user){    	
    	long scheduleShiftId =  0L;
    	try
    	{
    		AttendanceSchedule schedule = currentMonthScheduleMap.get(user.getUid());
    		if(AssertUtil.isNotEmpty(schedule)){
    			int dayNum = ConverUtil.string2Int(TimeUtil.getDateTimeOfDay(attendCalendar.getCalendarDate()));
        		//班次ID
        		scheduleShiftId = schedule.getDay(dayNum);
    		}
    		
    	}catch(Exception e)
    	{
    		
    	}
    	return scheduleShiftId;
    }

    /**
     * 获取补班日期列表
     */
    private boolean getRowDays() {
        String[] rowDays = config.getRowDays(TimeUtil.getCurrentYear()).split(SEPARATOR.DATE_LIST);
        if (AssertUtil.isNotEmpty(rowDays)) {
            List<String> strings = Arrays.asList(rowDays);
            if (strings.contains(TimeUtil.date2String(attendCalendar.getCalendarDate(),"yyyy-MM-dd"))) {
                Map jsonObject = JSON.parseObject(attendGroup.getFixedAttendRule());
                if (AssertUtil.isNotEmpty(jsonObject)) {
                    List dayNum = new ArrayList(jsonObject.keySet());
                    int week = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
                    JSONObject workTime;
                    //周六按固定班最后一个工作日计算 周日按第一个工作日计算
                    if (week == 6) {
                        week = Integer.parseInt(dayNum.get(dayNum.size()-1).toString());
                    } else if (week == 7){
                        week = Integer.parseInt(dayNum.get(0).toString());
                    }
                    workTime = (JSONObject)jsonObject.get(ConverUtil.object2String(week));
                    if (AssertUtil.isNotEmpty(workTime)) {
                        String amTime = workTime.get("amTime").toString();
                        String pmTime = workTime.get("pmTime").toString();
                        setStartAndEndTime(amTime,pmTime);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 初始化弹性班制时间属性
     */
    private void setStartAndEndTimeInFlexibleRule() {
        String fixedAttendRule = attendGroup.getFixedAttendRule();
        Map jsonMap = JSON.parseObject(fixedAttendRule);
        int day = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
        String attendRule = jsonMap.get(day).toString();
        pmTime = attendRule.substring(attendRule.indexOf(",")+11,attendRule.indexOf("}")-1);
        amTime = attendRule.substring(attendRule.indexOf(":")+2,attendRule.indexOf(",")-1);
        String pmString = pmTime.split("-")[1];
        String amString = amTime.split("-")[0];
        String[] amSplit = amString.split(":");
        String[] pmSplit = pmString.split(":");
        String am = amSplit[1];
        String ah = amSplit[0];
        String pm = pmSplit[1];
        String ph = pmSplit[0];
        int am_minutes =  (int) (attendGroup.getFlexitime() * 60 + Integer.parseInt(ah) * 60 + Integer.parseInt(am));
        int am_hours = (int) Math.floor(am_minutes / 60);
        int am_minute = am_minutes % 60;
        int pm_minutes = (int) (attendGroup.getFlexitime() * 60 + Integer.parseInt(ph) * 60 + Integer.parseInt(pm));
        int pm_hours = (int) Math.floor(pm_minutes / 60);
        int pm_minute = pm_minutes % 60;
        int mid_hours = 0;
        if ((pm_hours-am_hours)>6) {
            mid_hours = am_hours + 3;
        } else {
            mid_hours = (int) Math.floor((pm_hours+am_hours) / 2);
        }
        if (am_minute==0) {
            this.amTime = String.format("%02d", am_hours)+":"+am_minute+"0-"+String.format("%02d", mid_hours)+":"+am_minute+"0";
        } else {
            this.amTime = String.format("%02d", am_hours)+":"+am_minute+"-"+String.format("%02d", mid_hours)+":"+am_minute;
        }
        if (pm_minute==0) {
            this.pmTime = String.format("%02d", mid_hours)+":"+pm_minute+"0-"+String.format("%02d", pm_hours)+":"+pm_minute+"0";
        } else {
            this.pmTime = String.format("%02d", mid_hours)+":"+pm_minute+"-"+String.format("%02d", pm_hours)+":"+pm_minute;
        }

        // 时间以秒数结尾
        String Suffix = ":00";
        // 设置上午班次打卡的截止时间 12:00:00
        this.morningEndTime = AtdcTimeUtil.getEndTime(this.amTime) + Suffix;
        // 设置上午班次打卡的开始时间 09:00:00
        this.morningStartTime = AtdcTimeUtil.getStartTime(this.amTime) + Suffix;
        // 设置下午班次打卡的截止时间 18:00:00
        this.afternoonEndTime = AtdcTimeUtil.getEndTime(this.pmTime) + Suffix;
    }

    public static void main(String[] args) {
        AttendanceConfig config = AttendanceConfig.getInstance();
        String[] rowDays = config.getRowDays(TimeUtil.getCurrentYear()).split(SEPARATOR.DATE_LIST);
        System.out.println(rowDays);
        System.out.println(Arrays.asList(rowDays).contains(TimeUtil.date2String(new Date(),"yyyy-MM-dd")));
    }
}

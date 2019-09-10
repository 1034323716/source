/**
 * 文件名：MessageServiceImpl.java
 * 创建日期： 2018年1月2日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月2日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import richinfo.attendance.SMS.SmsSendUtil;
import richinfo.attendance.asyn.HistoryMessageAsynTask;
import richinfo.attendance.asyn.MessageAsynTask;
import richinfo.attendance.asyn.SendMessageAsynTask;
import richinfo.attendance.common.ServiceObject;
import richinfo.attendance.dao.*;
import richinfo.attendance.entity.*;
import richinfo.attendance.entity.AttendCalendar.CalendarStatus;
import richinfo.attendance.entity.AttendGroup.AttendType;
import richinfo.attendance.entity.AttendGroup.GroupStatus;
import richinfo.attendance.msg.Constants;
import richinfo.attendance.msg.RcsMsgUtil;
import richinfo.attendance.service.AttendEmployService;
import richinfo.attendance.service.MessageService;
import richinfo.attendance.util.*;
import richinfo.bcomponet.cache.CachedUtil;
import richinfo.dbcomponent.resourceloader.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 功能描述：消息管理服务实现
 *
 */
public class MessageServiceImpl extends ServiceObject implements MessageService
{

    private final Logger logger = LoggerFactory
        .getLogger(MessageServiceImpl.class);

    private String mSMSClockURL = AttendanceConfig.getInstance().getProperty(
        "attend.sms.clock.Url", "https://120.196.212.78:8080/satdc/rcs/index.html");

    private AttendGroupDao groupDao = new AttendGroupDao();

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();

    private AttendLoginDao loginDao = new AttendLoginDao();

    private MessageDao messageDao = new MessageDao();

    private AttendDao attendDao = new AttendDao();

    private AttendCalendarDao attendCalendarDao = new AttendCalendarDao();
    
    private AttendanceScheduleShiftDao shiftDao = new AttendanceScheduleShiftDao();
    
    private AttendanceScheduleDao scheduleDao = new AttendanceScheduleDao();

    private AttendanceConfig config = AttendanceConfig.getInstance();

    @Override
    public boolean prepareMessage(String cguid)
    {
        boolean flag = false;


            // 查询所有有效考勤组
            List<AttendGroup> allGroups = groupDao.queryAllNormalAttendGroup();
            // 考勤组列表为空，直接返回
            if (AssertUtil.isEmpty(allGroups))
            {
                logger.warn(
                    "AttendGroup is empty,prepareMessage end. cguid={}", cguid);
                return flag;
            }

            ExecutorService fixedThreadPool  = Executors.newFixedThreadPool(AttendanceConfig.getInstance().getMultiThreadedPool());
            try {
                // 循环统计各个考勤组
                for (AttendGroup group : allGroups) {
                    fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            new MessageAsynTask(group, cguid);
                        }
                    });
                    // 以考勤组为单位生成需要进行打卡提醒的待发消息
                    //  AsynTaskProcess.asynExecTask(new MessageAsynTask(group, cguid));
                }
                //线程任务执行完毕后 关闭线程
                fixedThreadPool.shutdown();
                flag = true;
            }
            catch (Exception e) {
                //发生异常时直接回收关闭线程池
                fixedThreadPool.shutdownNow();
                logger.error("prepareMessage error cguid={}|{}", cguid,
                    e.getMessage(),e);
            }
        return flag;
    }

    @Override
    public void handlePrepareMessage(AttendGroup attendGroup, String cguid)
    {
        long start = System.currentTimeMillis();
        
        boolean isAddOrUpdate = false;
        
        // 固定班
        if (AttendType.Fix.getValue() == attendGroup.getAttendType())
        {
            // 签到提醒消息
            handleFixAttendMessage(attendGroup,
                Constants.MsgType.Sign.getValue(), cguid, isAddOrUpdate);

            // 签退提醒消息
            handleFixAttendMessage(attendGroup,
                Constants.MsgType.SignOut.getValue(), cguid, isAddOrUpdate);
        }
        /*// 排班
        else if (AttendType.Schedule.getValue() == attendGroup.getAttendType())
        {
            // 签到提醒消息
            handleScheduleAttendMessage(attendGroup,
                Constants.MsgType.Sign.getValue(), cguid, isAddOrUpdate);

            // 签退提醒消息
            handleScheduleAttendMessage(attendGroup,
                Constants.MsgType.SignOut.getValue(), cguid, isAddOrUpdate);
        }*/
        else
        {
            logger.info("handlePrepareMessage ignore {}", attendGroup);
        }

        logger.info("handlePrepareMessage end cguid={}|useTime={}|group={}",
            cguid, AttendanceUtil.getUseTime(start), attendGroup);

    }
    
    /**
     * 生成考勤组待发提醒消息逻辑处理（生成待发消息流程）--排班
     * @param attendGroup
     * @param msgType
     * @param cguid
     * @param isAddOrUpdate 是否是创建或编辑考勤组
     */
    private void handleScheduleAttendMessage(AttendGroup attendGroup,
        int msgType, String cguid, boolean isAddOrUpdate)
    {
        long start = System.currentTimeMillis();

        // 获取考勤组的员工列表，暂时不考虑分页
        List<UserInfo> userList = employeeDao.queryUserList(
            attendGroup.getEnterId() , attendGroup.getAttendanceId(), GroupStatus.Normal.getValue());
        // 若员工列表为空，直接结束
        if (AssertUtil.isEmpty(userList))
        {
            logger
                .warn(
                    "handleScheduleAttendMessage end, userList is empty. cguid={}|group={}",
                    cguid, attendGroup);
            return;
        }

        // 根据考勤组ID查询排班班次
        List<AttendanceScheduleShift> shiftList = shiftDao
            .queryShiftByAttendanceId(attendGroup.getAttendanceId());
        // 若班次为空，直接结束
        if (AssertUtil.isEmpty(shiftList))
        {
            logger
                .warn(
                    "handleScheduleAttendMessage end, shiftList is empty. cguid={}|group={}",
                    cguid, attendGroup);
            return;
        }

        //判断是否是月末
        boolean isMonthEnd = AtdcTimeUtil.isMonthEnd(new Date());

        // 考勤组所有用户的本月排班
        List<AttendanceSchedule> currentMonthScheduleList = scheduleDao
            .querySchedule(attendGroup.getAttendanceId(),
                AtdcTimeUtil.getCurrentYearMonth(), null);

        // 考勤组所有用户的下月排班
        List<AttendanceSchedule> nextMonthScheduleList = null;
        //如果是月末,则查出下月排班
        if (isMonthEnd)
        {
            nextMonthScheduleList = scheduleDao.querySchedule(
                attendGroup.getAttendanceId(), AtdcTimeUtil.getNextMonth(),
                null);
        }

        List<Message> list = new ArrayList<Message>();
        // 组装待发消息（排班制）
        assembleScheduleMessages(list, userList, shiftList,
            currentMonthScheduleList, nextMonthScheduleList, isMonthEnd, attendGroup,
            msgType, cguid, isAddOrUpdate);

        // 存储待发消息（通过ON DUPLICATE KEY UPDATE进行新增，有记录则更新，没有则新增）
        saveMessags(list, attendGroup, msgType, cguid);

        logger
            .info(
                "handleScheduleAttendMessage end cguid={}|msgType={}|size={}|useTime={}|isAddOrUpdate={}|group={}",
                cguid, msgType, list.size(), AttendanceUtil.getUseTime(start),
                isAddOrUpdate, attendGroup);
    }

    /**
     * 组装待发消息（排班制）
     * @param list
     * @param userList
     * @param shiftList
     * @param currentMonthScheduleList
     * @param nextMonthScheduleList
     * @param isMonthEnd
     * @param attendGroup
     * @param msgType
     * @param cguid
     * @param isAddOrUpdate 是否是创建或编辑考勤组
     */
    private void assembleScheduleMessages(List<Message> list,
        List<UserInfo> userList, List<AttendanceScheduleShift> shiftList,
        List<AttendanceSchedule> currentMonthScheduleList,
        List<AttendanceSchedule> nextMonthScheduleList, boolean isMonthEnd,
        AttendGroup attendGroup, int msgType, String cguid,
        boolean isAddOrUpdate)
    {
        // 转换成班次Map(key为scheduleShiftId)
        Map<String, AttendanceScheduleShift> shiftMap = transformationToShiftMap(shiftList);

        // 转换成Map（key为attendanceId + attendMonth + uid）
        Map<String, AttendanceSchedule> currentMonthScheduleMap = transformationToScheduleMap(currentMonthScheduleList);

        Map<String, AttendanceSchedule> nextMonthScheduleMap = transformationToScheduleMap(nextMonthScheduleList);

        // 根据员工列表循环汇总
        for (UserInfo user : userList)
        {
            // 汇总员工考勤提醒信息
            if (AssertUtil.isNotEmpty(user.getEnterId())
                && AssertUtil.isNotEmpty(user.getUid()))
            {
                // 获取（排班制）某用户今明两天的考勤时间（签到/签退时间）
                List<Date> attendDatelist = getTowScheduleAttendDate(user,
                    currentMonthScheduleMap, nextMonthScheduleMap, shiftMap,
                    isMonthEnd, attendGroup, msgType);

                // 循环组装今明两天的待发消息
                if (AssertUtil.isNotEmpty(attendDatelist))
                {
                    for (Date attendDate : attendDatelist)
                    {
                        // 组装消息，并放入到list中
                        assembleMsg(list, attendGroup, user, attendDate,
                            msgType, cguid, isAddOrUpdate);
                    }
                }

            }
        }

    }

    /**
     * 获取（排班制）某用户今明两天的考勤时间（签到/签退时间）
     * @param user 
     * @param currentMonthScheduleMap
     * @param nextMonthScheduleMap
     * @param shiftMap
     * @param isMonthEnd 
     * @param attendGroup 
     * @param msgType
     * @return
     */
    private List<Date> getTowScheduleAttendDate(UserInfo user,
        Map<String, AttendanceSchedule> currentMonthScheduleMap,
        Map<String, AttendanceSchedule> nextMonthScheduleMap,
        Map<String, AttendanceScheduleShift> shiftMap, boolean isMonthEnd,
        AttendGroup attendGroup, int msgType)
    {
        List<Date> list = new ArrayList<Date>();
        // 某人当月考勤
        AttendanceSchedule currentMonthSchedule = currentMonthScheduleMap
            .get(String.valueOf(attendGroup.getAttendanceId())
                + AtdcTimeUtil.getCurrentYearMonth() + user.getUid());

        Date today = new Date();
        // 今天的
        
        // 当天
        int current_day = ConverUtil.string2Int(TimeUtil.getCurrentDay());
        if (AssertUtil.isNotEmpty(currentMonthSchedule))
        {
            // 当天的班次ID
            Long currentShiftId = currentMonthSchedule.getDay(current_day);
            if (AssertUtil.isNotEmpty(currentShiftId) && currentShiftId > 0)
            {
                AttendanceScheduleShift shift = shiftMap.get(String
                    .valueOf(currentShiftId));
                if (AssertUtil.isNotEmpty(shift))
                {
                    // 当天的工作时间
                    String currentWorkTime = shift.getWorkTime();
                    // 获取（排班制）当天的考勤时间（签到/签退时间）
                    Date currentAttendDate = getScheduleAttendDate(today,
                        currentWorkTime, msgType);
                    if (AssertUtil.isNotEmpty(currentAttendDate))
                    {
                        list.add(currentAttendDate);
                    }
                }
            }
        }
        else
        {
            logger
                .info(
                    "this user has no schedule this month uid={}|month={}|msgType={}|atte{}",
                    user.getUid(), AtdcTimeUtil.getCurrentYearMonth(), msgType,
                    attendGroup.toSimpleString());
        }

        // 明天的

        // 明天的班次ID
        Long tomorrowShiftId = 0L;
        if (!isMonthEnd && AssertUtil.isNotEmpty(currentMonthSchedule))
        {
            tomorrowShiftId = currentMonthSchedule.getDay(current_day + 1);
        }
        else if (isMonthEnd && AssertUtil.isNotEmpty(nextMonthScheduleMap))
        {
            // 某人下月考勤
            AttendanceSchedule nextMonthSchedule = nextMonthScheduleMap
                .get(String.valueOf(attendGroup.getAttendanceId())
                    + AtdcTimeUtil.getNextMonth() + user.getUid());
            if (AssertUtil.isNotEmpty(nextMonthSchedule))
            {
                tomorrowShiftId = nextMonthSchedule.getDay1();
            }
        }
        else if (AssertUtil.isEmpty(nextMonthScheduleMap))
        {
            logger
                .info(
                    "this user has no schedule next month uid={}|month={}|msgType={}|{}",
                    user.getUid(), AtdcTimeUtil.getNextMonth(), msgType,
                    attendGroup.toSimpleString());
        }

        if (AssertUtil.isNotEmpty(tomorrowShiftId) && tomorrowShiftId > 0)
        {
            AttendanceScheduleShift shift = shiftMap.get(String
                .valueOf(tomorrowShiftId));
            if (AssertUtil.isNotEmpty(shift))
            {
                // 明天的工作时间
                String tomorrowWorkTime = shift.getWorkTime();
                // 获取（排班制）明天的考勤时间（签到/签退时间）
                Date tomorrowAttendDate = getScheduleAttendDate(
                    AtdcTimeUtil.getNextDay(today), tomorrowWorkTime, msgType);
                if (AssertUtil.isNotEmpty(tomorrowAttendDate))
                {
                    list.add(tomorrowAttendDate);
                }
            }
        }

        return list;
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
     * 转换成Map
     * @param list
     * @return
     */
    private Map<String, AttendanceSchedule> transformationToScheduleMap(
        List<AttendanceSchedule> list)
    {
        Map<String, AttendanceSchedule> map = new HashMap<String, AttendanceSchedule>();
        if (AssertUtil.isNotEmpty(list))
        {
            for (AttendanceSchedule info : list)
            {
                // key为attendanceId + attendMonth + uid
                map.put(
                    String.valueOf(info.getAttendanceId())
                        + info.getAttendMonth() + info.getUid(), info);
            }
        }
        return map;
    }

    /**
     * 转换成Map
     * @param shiftList
     * @return
     */
    private Map<String, AttendanceScheduleShift> transformationToShiftMap(
        List<AttendanceScheduleShift> shiftList)
    {
        Map<String, AttendanceScheduleShift> map = new HashMap<String, AttendanceScheduleShift>();
        if (AssertUtil.isNotEmpty(shiftList))
        {
            for (AttendanceScheduleShift shift : shiftList)
            {
                map.put(String.valueOf(shift.getScheduleShiftId()), shift);
            }
        }
        return map;
    }

    /**
     * 生成考勤组待发提醒消息逻辑处理（生成待发消息流程）--固定班
     * @param attendGroup
     * @param msgType
     * @param cguid
     * @param isAddOrUpdate 是否是创建或编辑考勤组
     */
    private void handleFixAttendMessage(AttendGroup attendGroup, int msgType,
        String cguid, boolean isAddOrUpdate)
    {
        // 获取今明两天的考勤时间（签到/签退时间）
        List<Date> attendDatelist = getTowFixAttendDate(
            attendGroup.getFixedAttendRule(), msgType);

        if (AssertUtil.isNotEmpty(attendDatelist))
        {
            for (Date attendDate : attendDatelist)
            {
                // 生成固定班考勤组待发提醒消息
                handleFixAttendMessage(attendGroup, attendDate, msgType, cguid,
                    isAddOrUpdate);
            }
        }
    }
    
    /**
     * 根据考勤时间，生成固定班考勤组待发提醒消息
     * @param attendGroup
     * @param attendDate
     * @param msgType
     * @param cguid
     * @param isAddOrUpdate 是否是创建或编辑考勤组
     */
    private void handleFixAttendMessage(AttendGroup attendGroup,
        Date attendDate, int msgType, String cguid, boolean isAddOrUpdate)
    {

        long start = System.currentTimeMillis();
        
        // 校验相关参数，校验失败，则直接return
        if (!validateHandleMessage(attendGroup, attendDate, msgType,
            cguid))
        {
            logger
                .warn(
                    "handleFixAttendMessage failed. cguid={}|group={}|attendDate={}|msgType={}",
                    cguid, attendGroup, attendDate, msgType);
            return;
        }
        
        // 获取考勤组的员工列表，暂时不考虑分页
        List<UserInfo> userList = employeeDao.queryUserList(
            attendGroup.getEnterId(),attendGroup.getAttendanceId(), GroupStatus.Normal.getValue());
        // 若员工列表为空，直接结束
        if (AssertUtil.isEmpty(userList))
        {
            logger
                .warn(
                    "handleFixAttendMessage end, userList is empty. cguid={}|group={}",
                    cguid, attendGroup.getAttendanceId());
            return;
        }

        List<Message> list = new ArrayList<Message>();
        // 组装待发消息
        assembleMessages(list, userList, attendGroup, attendDate, msgType,
            cguid, isAddOrUpdate);

        // 存储待发消息（通过ON DUPLICATE KEY UPDATE进行新增，有记录则更新，没有则新增）
        saveMessags(list, attendGroup, msgType, cguid);

        logger
            .info(
                "handleFixAttendMessage end cguid={}|msgType={}|size={}|useTime={}|attendDate={}|isAddOrUpdate={}|group={}",
                cguid, msgType, list.size(), AttendanceUtil.getUseTime(start),
                attendDate, isAddOrUpdate, attendGroup.getAttendanceId());
        
    }

    private boolean validateHandleMessage(AttendGroup attendGroup,
        Date attendDate, int msgType, String cguid)
    {
        if (AssertUtil.isEmpty(attendDate))
        {
            return false;
        }

        /*
         * 固定班制：如果（签到时间、签退时间）不是非工作日，则不生成考勤打卡消息，直接返回。 排班制，则没有限制
         */
        if (AttendType.Fix.getValue() == attendGroup.getAttendType()
            && 0 == attendGroup.getRelyHoliday()
            && !isWeekday(attendGroup, attendDate))
        {
            logger
                .warn(
                    "validateHandleMessage failed, attendDate({}) is not Weekday. cguid={}|group={}|msgType={}",
                    attendDate, cguid, attendGroup, msgType);
            return false;
        }

        return true;
    }

    /**
     * 获取排班制最近两天的考勤时间（签到/签退时间）
     * @return
     */
    private List<Date> getTowFixAttendDate(String fixedAttendRule,
        int msgType)
    {
        if (AssertUtil.isEmpty(fixedAttendRule))
        {
            return null;
        }

        /*
         * 星期天、一、二、三、四、五、六，对应的值是1、2、3、4、5、6、7 减去1后，则变成了0、1、2、3、4、5、6
         */

        Calendar calendar = Calendar.getInstance();
        // 当天是这个星期的第几天
        int today_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        // 第二天是这个星期的第几天
        int tomorrow_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        List<Date> towDays = AttendanceUtil.getTowDays();

        Date todayFixAttendDate = getFixAttendDate(fixedAttendRule, msgType,
            towDays.get(0), today_of_week);
        Date tomorrowFixAttendDate = getFixAttendDate(fixedAttendRule, msgType,
            towDays.get(1), tomorrow_of_week);

        List<Date> list = new ArrayList<Date>();
        if (AssertUtil.isNotEmpty(todayFixAttendDate))
        {
            list.add(todayFixAttendDate);
        }

        if (AssertUtil.isNotEmpty(tomorrowFixAttendDate))
        {
            list.add(tomorrowFixAttendDate);
        }

        return list;
    }

    /**
     * 获取排班制某一天的考勤时间（签到/签退时间）
     * @param fixedAttendRule
     * @param msgType
     * @param date
     * @param day_of_week
     * @return
     */
    @SuppressWarnings("unchecked")
    private Date getFixAttendDate(String fixedAttendRule, int msgType,
        Date date, int day_of_week)
    {
        if (AssertUtil.isEmpty(fixedAttendRule))
        {
            return null;
        }

        /*
         * fixedAttendRule的值
         * {"1":{"amTime":"06:00-11:30","pmTime":"13:00-17:31"},
         * "2":{"amTime":"07:00-11:30","pmTime":"13:00-17:32"},
         * "3":{"amTime":"08:00-11:30","pmTime":"13:00-17:33"},
         * "4":{"amTime":"08:30-11:30","pmTime":"13:00-17:34"},
         * "5":{"amTime":"09:00-11:30","pmTime":"13:00-17:35"},
         * "6":{"amTime":"09:30-11:30","pmTime":"13:00-17:36"},
         * "7":{"amTime":"10:00-11:30","pmTime":"13:00-17:37"}}
         */

        Map<String, Object> fixedAttendRuleMap = (HashMap<String, Object>) JsonUtil
            .jsonToMap(fixedAttendRule);

        if (AssertUtil.isNotEmpty(fixedAttendRuleMap))
        {

            LinkedTreeMap<String, String> workTimeMap = null;
            if (0 == day_of_week)
            {
                // 如果为0，则取星期天的数据
                workTimeMap = (LinkedTreeMap<String, String>) fixedAttendRuleMap
                    .get("7");
            }
            else
            {
                workTimeMap = (LinkedTreeMap<String, String>) fixedAttendRuleMap
                    .get(String.valueOf(day_of_week));
            }

            if (AssertUtil.isNotEmpty(workTimeMap))
            {
                Date attendDate = null;
                // 签到时间
                if (Constants.MsgType.Sign.getValue() == msgType)
                {
                    String amTime = (String) workTimeMap.get("amTime");
                    if (AssertUtil.isNotEmpty(amTime))
                    {
                        attendDate = AttendanceUtil.getAttendDate(date, amTime,
                            0);
                    }
                }
                // 签退时间
                else if (Constants.MsgType.SignOut.getValue() == msgType)
                {
                    String pmTime = (String) workTimeMap.get("pmTime");
                    if (AssertUtil.isNotEmpty(pmTime))
                    {
                        attendDate = AttendanceUtil.getAttendDate(date, pmTime,
                            1);
                    }
                }

                return attendDate;
            }
        }

        return null;
    }

    /**
     * 存储待发消息（通过ON DUPLICATE KEY UPDATE进行新增，有记录则更新，没有则新增）
     * @param list
     * @param attendGroup
     * @param msgType
     * @param cguid
     */
    private void saveMessags(List<Message> list, AttendGroup attendGroup,
        int msgType, String cguid)
    {
        boolean result = false;
        int size = 0;
        long saveStart = System.currentTimeMillis();
        if (AssertUtil.isNotEmpty(list))
        {
            size = list.size();
            // 数量可能较多,拆分后再批量入库
            int splitSize = config.getGenerateMsgSplitSize();
            List<List<Message>> lists = AttendanceUtil.splitList(list,
                splitSize);
            for (List<Message> ls : lists)
            {
                if (AssertUtil.isNotEmpty(ls))
                {
                    result = messageDao.batchSaveMessage(ls);
                }
            }
        }
        logger
            .info(
                "batchSaveMessage success={}|cguid={}|msgType={}|size={}|useTime={}|group={}",
                result, cguid, msgType, size,
                AttendanceUtil.getUseTime(saveStart), attendGroup.getAttendanceId());
    }

    /**
     * 组装待发消息
     * @param list
     * @param userList
     * @param attendGroup
     * @param attendDate
     * @param msgType
     * @param cguid
     * @param isAddOrUpdate 是否是创建或编辑考勤组
     */
    private void assembleMessages(List<Message> list, List<UserInfo> userList,
        AttendGroup attendGroup, Date attendDate, int msgType, String cguid,
        boolean isAddOrUpdate)
    {
        // 根据员工列表循环汇总
        for (UserInfo user : userList)
        {
            // 汇总员工考勤提醒信息
            if (AssertUtil.isNotEmpty(user.getEnterId())
                && AssertUtil.isNotEmpty(user.getUid()))
            {
                // 组装消息，并放入到list中
                assembleMsg(list, attendGroup, user, attendDate, msgType,
                    cguid, isAddOrUpdate);
            }
        }
    }

    /**
     * 组装消息，并放入到list中
     * @param list
     * @param attendGroup
     * @param user
     * @param attendDate
     * @param msgType
     * @param cguid
     * @param isAddOrUpdate 是否是创建或编辑考勤组
     */
    private void assembleMsg(List<Message> list, AttendGroup attendGroup,
        UserInfo user, Date attendDate, int msgType, String cguid,
        boolean isAddOrUpdate)
    {
        long currentTime = System.currentTimeMillis();

        // 如果是创建或编辑考勤组，则设置的打卡时间是30分钟后，才执行后边的逻辑
        if (isAddOrUpdate)
        {
            // 设置的打卡时间是否是30分钟后
            if (attendDate.getTime() - currentTime > config
                .getUpdateMsgIntervalTime())
            {
                // 执行后边的逻辑
            }
            else
            {
                // 结束
                logger
                    .debug(
                        "ignore msg because attendDate({}) is not more than currentTime({}) {}ms. cguid={}|attendanceId={}|uid={}|msgType={}|isAddOrUpdate={}",
                        attendDate, new Date(currentTime),
                        config.getUpdateMsgIntervalTime(), cguid,
                        attendGroup.getAttendanceId(), user.getUid(), msgType,
                        isAddOrUpdate);
                return;
            }
        }

        Message msg = initMessage(attendGroup, user, attendDate, msgType);
        if (msg != null && msg.getSendTime() != null)
        {
            // 当前时间大于发送时间，则这条消息丢弃
            if (currentTime > msg.getSendTime().getTime())
            {
                logger
                    .debug(
                        "ignore msg because currentTime more than sendTime. cguid={}|isAddOrUpdate={}|{}",
                        cguid, isAddOrUpdate, msg.toSimpleString());
            }
            else
            {
                list.add(msg);
            }
        }
    }

    /**
     * 判定是否是工作日
     * @param attendGroup
     * @param attendDate
     * @return
     */
    private boolean isWeekday(AttendGroup attendGroup, Date attendDate)
    {
        AttendCalendar attendCalendar = attendCalendarDao
            .queryAttendCalendarByDate(attendDate);
        if (AssertUtil.isNotEmpty(attendCalendar)
            && CalendarStatus.Weekday.getValue() == attendCalendar.getStatus())
        {
            return true;
        }
        return false;
    }

    private Message initMessage(AttendGroup attendGroup, UserInfo user,
        Date attendDate, int msgType)
    {
        Message msg = new Message();
        msg.setAttendanceId(user.getAttendanceId());
        msg.setEnterId(user.getEnterId());
        msg.setUid(user.getUid());
        String appId = config.getRcsAppId();
        msg.setAppId(appId);
        String accessNo = config.getRcsAccessNo();
        msg.setSender(accessNo);
        msg.setServerNo(AttendanceUtil.getOneServerNo());
        msg.setMsgType(msgType);
        Date nowDate = new Date();
        msg.setCreateTime(nowDate);
        msg.setModifyTime(nowDate);
        msg.setSmsSwitch(user.getSmsSwitch());

        // 签到消息标题、内容、摘要，发送时间
        if (Constants.MsgType.Sign.getValue() == msgType)
        {
            msg.setTopic(config.getSignMsgTopic());
            long remindTime = config.getRemindTime();
            String remindMinute = String
                .valueOf((int) (remindTime / (60 * 1000L)));
            msg.setContent(config.getSignMsgContent().replace(
                "[@remindMinute@]", remindMinute));
            msg.setSummary(config.getSignMsgSummary().replace(
                "[@remindMinute@]", remindMinute));

            if (!AssertUtil.isNull(attendDate))
            {
                Date sendDate = new Date(attendDate.getTime() - remindTime);
                msg.setSendTime(sendDate);
            }
        }

        // 签退消息标题、内容、摘要，发送时间
        else if (Constants.MsgType.SignOut.getValue() == msgType)
        {
            msg.setTopic(config.getSignOutMsgTopic());
            msg.setContent(config.getSignOutMsgContent());
            msg.setSummary(config.getSignOutMsgSummary());

            if (!AssertUtil.isNull(attendDate))
            {
                msg.setSendTime(attendDate);
            }
        }
        //消息推送的时候，在标题里增加对应公司名称。
        if (AssertUtil.isNotEmpty(user.getEnterName()))
        {
            String enterName = user.getEnterName();
            if ((msg.getTopic() + "-" + enterName).length() > 20){
                enterName =  enterName.substring(0,5)+"..."+enterName.substring(enterName.length()-5);
            }
            msg.setTopic(msg.getTopic() + "-" + enterName);
    }
        return msg;
    }

    @Override
    public void updatePrepareMessage(MessageUpdateInfo info)
    {
        long start = System.currentTimeMillis();
        AttendGroup newAttendGroup = info.getNewAttendGroup();
        if (AssertUtil.isEmpty(newAttendGroup))
        {
            return;
        }
        
        // 如果是删除考勤组
        if (info.isDel())
        {
            // 删除该考勤组的待发消息
            delAttendMsg(info);
            return;
        }

        /*
         * 注掉该段代码，因为此为原无排班制的逻辑
        // 如果是编辑考勤组
        if (info.isUpdate())
        {
            if (AssertUtil.isEmpty(info.getOldAttendGroup()))
            {
                return;
            }

            // 判断是否需要更新考勤组的用户提醒消息
            if (!isNeedUpdateMsg(info))
            {
                logger.info("do not need to update msg. cguid={}|{}",
                    info.getCguid(), info.getNewAttendGroup());
                return;
            }
            // 判断是否只需要删除减少用户的提醒消息
            else if (isJustDelDecreaseMember(info))
            {
                // 删除减少用户的提醒消息
                boolean flag = messageDao.batchDelMsgByUids(info
                    .getDecreaseMember());
                logger
                    .info(
                        "just del the msgs of decreaseMember. success={}|cguid={}|{}",
                        flag, info.getCguid(), newAttendGroup);

                return;
            }
        }
        */

        // 不管是创建还是编辑考勤，都先删除（更新用户和减少用户的提醒消息）
        delMsg(info);

        // 从数据库重新查询最新的考勤组信息
        AttendGroup attendGroup = groupDao.queryAttendGroupInfo(
            newAttendGroup.getAttendanceId(), newAttendGroup.getEnterId(),
            GroupStatus.Normal.getValue());

        if (AssertUtil.isNotEmpty(attendGroup))
        {
            // 更新签到消息
            updatePrepareMessage(attendGroup,
                Constants.MsgType.Sign.getValue(), info.getCguid());

            // 更新签退消息
            updatePrepareMessage(attendGroup,
                Constants.MsgType.SignOut.getValue(), info.getCguid());

            logger.info(
                "updatePrepareMessage end cguid={}|attendanceId={}|useTime={}",
                info.getCguid(), attendGroup.getAttendanceId(),
                AttendanceUtil.getUseTime(start));
        }

    }

    /**
     * 删除该考勤组的待发消息
     * @param info
     */
    private void delAttendMsg(MessageUpdateInfo info)
    {
        if (AssertUtil.isEmpty(info.getOldAttendGroup())) {
            return;
        }

        long start = System.currentTimeMillis();
        int msgType = -1;
        // 从消息待发表中删除该考勤组下的待发消息
        boolean delFlag = messageDao.delMsgByAttendanceId(info
            .getOldAttendGroup().getAttendanceId(), msgType);

        logger
            .info(
                "delAttendMsg success={}|cguid={}|attendanceId={}|msgType={}|useTime={}",
                delFlag, info.getCguid(), info.getOldAttendGroup()
                    .getAttendanceId(), msgType, AttendanceUtil
                    .getUseTime(start));

    }

    /**
     * 删除（更新用户和减少用户的提醒消息）
     * @param info
     */
    private void delMsg(MessageUpdateInfo info)
    {
        // 删除减少用户的提醒消息
        if (AssertUtil.isNotEmpty(info.getDecreaseMember()))
        {
            boolean flag = messageDao.batchDelMsgByUids(info
                .getDecreaseMember());
            logger.info(
                "del the msgs of decreaseMember. success={}|cguid={}|{}", flag,
                info.getCguid(), info.getNewAttendGroup().getAttendanceId());
        }

        // 删除更新用户的提醒消息
        if (AssertUtil.isNotEmpty(info.getUpdateIncrease()))
        {
            boolean flag = messageDao.batchDelMsgByUids(info
                .getUpdateIncrease());
            logger.info(
                "del the msgs of updateIncrease. success={}|cguid={}|AttendanceId={}", flag,
                info.getCguid(), info.getNewAttendGroup().getAttendanceId());
        }
    }

    /**
     * 判断是否只需要删除减少用户的提醒消息
     * @param info
     * @return
    */
    private boolean isJustDelDecreaseMember(MessageUpdateInfo info)
    {
        // 如果减少成员列表不为空，且新增、更新成员列表为空，且上午打卡时间和下午打卡时间都没变，只需要删除减少用户的提醒消息
        if (AssertUtil.isNotEmpty(info.getDecreaseMember())
            && AssertUtil.isEmpty(info.getUpdateIncrease())
            && AssertUtil.isEmpty(info.getInsertIncrease())
            && !isChangeAttendTime(info))
        {
            return true;
        }
        return false;
    }

    /**
     * 判断是否需要更新考勤组的用户提醒消息
     * @param info
     * @return
     */
    private boolean isNeedUpdateMsg(MessageUpdateInfo info)
    {
        // 如果三个成员列表都为空，且上午打卡时间和下午打卡时间都没变，则不用更新该考勤组的用户提醒消息
        if (AssertUtil.isEmpty(info.getDecreaseMember())
            && AssertUtil.isEmpty(info.getUpdateIncrease())
            && AssertUtil.isEmpty(info.getInsertIncrease())
            && !isChangeAttendTime(info))
        {
            return false;
        }
        return true;
    }

    /**
     * 判断 上午打卡时间和下午打卡时间 是否变了
     * @param info
     * @return
     */
    private boolean isChangeAttendTime(MessageUpdateInfo info)
    {
        AttendGroup oldGroup = info.getOldAttendGroup();
        AttendGroup newGroup = info.getNewAttendGroup();
        boolean flag = true;
        // 传进来的amTime和pmTime都为空，则时间没变
        if (AssertUtil.isEmpty(newGroup.getAmTime())
            && AssertUtil.isEmpty(newGroup.getPmTime()))
        {
            flag = false;
        }
        // 传进来的amTime和pmTime都与原来的一样，则时间没变
        else if (AssertUtil.isNotEmpty(newGroup.getAmTime())
            && AssertUtil.isNotEmpty(newGroup.getPmTime())
            && newGroup.getAmTime().equals(oldGroup.getAmTime())
            && newGroup.getPmTime().equals(oldGroup.getPmTime()))
        {
            flag = false;
        }
       return flag;
    }

    /**
     * 更新待发消息（更新待发消息子流程）
     * @param attendGroup
     * @param msgType
     * @param cguid
     */
    private void updatePrepareMessage(AttendGroup attendGroup, int msgType,
        String cguid)
    {
        long currentTime = System.currentTimeMillis();
        // 从消息待发表中删除该考勤组下的待发消息
        boolean delFlag = messageDao.delMsgByAttendanceId(
            attendGroup.getAttendanceId(), msgType);

        logger
            .info(
                "del msg by attendanceId success={}|cguid={}|attendanceId={}|msgType={}|useTime={}",
                delFlag, cguid, attendGroup.getAttendanceId(), msgType,
                AttendanceUtil.getUseTime(currentTime));

        boolean isAddOrUpdate = true;

        // 固定班
        if (AttendType.Fix.getValue() == attendGroup.getAttendType())
        {
            handleFixAttendMessage(attendGroup, msgType, cguid, isAddOrUpdate);
        }
        // 排班
        else if (AttendType.Schedule.getValue() == attendGroup.getAttendType())
        {
            handleScheduleAttendMessage(attendGroup, msgType, cguid,
                isAddOrUpdate);
        }

    }

    @Override
    public boolean sendScheduleMsg() {
        String cguid = AttendanceUtil.getCguid();
        try
        {
            // 累计发送消息数
            int totalCount = 0;
            // 获取每次扫描发送消息任务的最大累计发送消息数
            int maxMsgTotalCount = config.getMaxMsgTotalCount();
            int msgCount = 0;
            logger.info("start send schedule msg... cguid={}", cguid);
            // 定义每页获取100条数据
            int limit = config.getMsgScanLimit();
            int skip = 0;
            int currentServerNo = config.getCurrentServerNo();
            // 定时消息发送的截止时间
            Date endDate = new Date();

            while (true)
            {
                // 从数据库查询需要发送的定时消息列表
                long queryStart = System.currentTimeMillis();
                List<Message> msgList = messageDao.queryPageNeedSendMsgs(skip,
                    limit, currentServerNo, endDate);
                logger.debug("queryPageNeedSendMsgs cguid={}|useTime={}",
                    cguid, AttendanceUtil.getUseTime(queryStart));
                if (AssertUtil.isEmpty(msgList))
                {
                    break;
                }

                msgCount = msgList.size();
                totalCount += msgCount;

                if (AssertUtil.isNotEmpty(msgList))
                {
                    logger.debug("msgList.get(0)={}|cguid={}", msgList.get(0),
                        cguid);
                }

                // 批量删除待发消息
                long delStart = System.currentTimeMillis();
                boolean f = messageDao.batchDelMessage(msgList);
                logger
                    .info(
                        "batchDelMessage success={}|cguid={}|msgCount={}|totalCount={}|useTime={}",
                        f, cguid, msgCount, totalCount,
                        AttendanceUtil.getUseTime(delStart));

                // 处理定时消息
                handleSchduleMsgList(msgList, cguid);

                // 如果累计发送消息数 大于或等于 最大累计发送消息数，则退出循环
                if (totalCount >= maxMsgTotalCount)
                {
                    logger
                        .info(
                            "total send {} msgs, to break cguid={}|maxMsgTotalCount={}",
                            totalCount, cguid, maxMsgTotalCount);
                    break;
                }
            }

            logger
                .info("end send schedule msg. cguid={}|msgCount={}|cost={}",
                    cguid, msgCount,
                    System.currentTimeMillis() - endDate.getTime());
        }
        catch (Exception e)
        {
            logger.error("sendScheduleMsg error cguid={}|{}", cguid,
                e.getMessage(),e);
            return false;
        }

        return true;
    }

    private void handleSchduleMsgList(List<Message> msgList, String cguid)
    {
        logger.debug("start handleSchduleMsgList. cguid={}|size={}", cguid,
            msgList.size());
        long start = System.currentTimeMillis();
        int splitSize = config.getMsgSplitSize();
        List<List<Message>> lists = AttendanceUtil
            .splitList(msgList, splitSize);
        ExecutorService fixedThreadPool  = Executors.newFixedThreadPool(AttendanceConfig.getInstance().getMultiThreadedPool());
        try {
            for (List<Message> list : lists)
            {
                if (AssertUtil.isNotEmpty(list))
                {
                    fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            new SendMessageAsynTask(list, cguid);
                        }
                    });
                    // 异步发送消息
                    // AsynTaskProcess.asynExecTask(new SendMessageAsynTask(list, cguid));
                }
            }
            //线程任务支持完毕后   关闭多线程
            fixedThreadPool.shutdown();
        }catch (Exception e){
            //发生异常时直接回收关闭线程池
            fixedThreadPool.shutdownNow();
        }
        long end = System.currentTimeMillis();
        logger.debug("end handleSchduleMsgList. cguid={}|size={}|useTime={}",
            cguid, msgList.size(), (end - start));

    }

    @Override
    public void handleMsgSend(List<Message> list, String cguid)
    {
        long startTime = System.currentTimeMillis();
        String consumerKey = config.getRcsConsumerKey();
        String consumerSecret = config.getRcsConsumerSecret();

        // 获取接口调用凭证accessToken
        String accessToken = getAccessToken(list.get(0).getAppId(),
            consumerKey, consumerSecret, cguid);
        if (AssertUtil.isNotEmpty(accessToken))
        {
            int successNum = 0;
            List<HistoryMessage> hisMsgList = new ArrayList<HistoryMessage>();
            for (Message msg : list)
            {
                //签到信息判断是否已经打卡
                if(msg.getMsgType() == Constants.MsgType.Sign.getValue()){
                    List<AttendEntity> attendList = attendDao.queryEmployRecord(msg.getUid(), new Date());
                    if (AssertUtil.isNotEmpty(attendList)){
                        logger.info("handleMsgSend 该用户已经打卡上班，不再推送消息！uid={}，enterId={}，attendList={}",
                                msg.getUid(),msg.getEnterId(),attendList);
                        continue;
                    }
                }
                // 推送消息
                HistoryMessage hisMsg = RcsMsgUtil.sendMsg(msg, consumerKey,
                    consumerSecret, accessToken, cguid);

                if (AssertUtil.isEmpty(hisMsg) || !hisMsg.isResultFlag())
                {
                    // 如果推送失败，则再推送一次
                    hisMsg = RcsMsgUtil.sendMsg(msg, consumerKey,
                        consumerSecret, accessToken, cguid);
                }

                if (AssertUtil.isNotEmpty(hisMsg))
                {
                    hisMsgList.add(hisMsg);
                    if (hisMsg.isResultFlag())
                    {
                        successNum++;
                    }
                }

                //短信推送
                if(msg.getSmsSwitch() == 1) {
                    UserInfo userInfo = loginDao.queryUserInfoByUid(msg.getUid(), 0);
                    logger.info("sms userInfo : {}", userInfo);
                    if(null != userInfo) {
                        int msgType = msg.getMsgType();
                        if(msgType == 1 || msgType == 2) {
                            String placeHolderContent = "{[placeholder:url]}##" + mSMSClockURL + "?uid=" + userInfo.getUid()
                                + "|@|{[placeholder:remark]}##  ";
                            if (userInfo.getFirstSend()==1) {
                                //place_holder_id1##替换内容|@|place_holder_id2##替换内容
                                placeHolderContent = "{[placeholder:url]}##" + mSMSClockURL + "?uid=" + userInfo.getUid()
                                    + "|@|{[placeholder:remark]}##，目前只支持4G环境打卡\n如遇到IOS终端无法登录，请关闭“设置-safari浏览器-阻止跨网站跟踪”按钮或使用其它浏览器\n退订请在和飞信考勤应用设置";
                            }
                            //手机
//                            boolean sendResult = SmsSendUtil.sendSmsWithOutAddressBook(userInfo.getPhone(),
//                                userInfo.getEmployeeName(), userInfo.getEnterName(), placeHolderContent, 3, false, msgType);
                            //contactId

                            boolean sendResult = SmsSendUtil.sendSmsWithInAddressBook(userInfo.getEnterId(), userInfo.getContactId(),
                                AttendanceConfig.getInstance().getProperty("attend.qytxl.appid", "9fdcd721d954456b8c7ea53f80635456"), placeHolderContent, 3, false, msgType);

                            if (sendResult == true && userInfo.getFirstSend()==1){
                                employeeDao.updateEmployeeFirstSend(userInfo.getUid());
                            }
                            logger.info("send user data : {}, sms sendResult : {}",userInfo, sendResult);
                        }

                    }
                }

            }
            logger
                .info(
                    "handleMsgSend cguid={}|list.size()={}|successNum={}|useTime={}",
                    cguid, list.size(), successNum,
                    AttendanceUtil.getUseTime(startTime));

            // 异步存储历史消息
            if (AssertUtil.isNotEmpty(hisMsgList))
            {
                new HistoryMessageAsynTask(
                    hisMsgList, cguid);
            }
        }
        else
        {
            logger.info("handleMsgSend ignore {} messages cguid={}",
                list.size(), cguid);
        }
    }

    private String getAccessToken(String appId, String consumerKey,
        String consumerSecret, String cguid)
    {
        // 先从缓存取，取不到，再去调用接口获取
        String accessToken = RcsMsgUtil.getRcsMsgAccessTokenFromCache(appId);
        if (AssertUtil.isEmpty(accessToken))
        {
            // 调用接口获取
            accessToken = RcsMsgUtil.getAccessToken(appId, consumerKey,
                consumerSecret, cguid);
            if (AssertUtil.isEmpty(accessToken))
            {
                // 如果失败，则再调用一次
                accessToken = RcsMsgUtil.getAccessToken(appId, consumerKey,
                    consumerSecret, cguid);
            }
        }
        return accessToken;
    }


    /**
     * 保存日报信息推送消息
     * @param
     */
    @Override
    public void saveDailyPushMsg(List<AttendGroup>groupList) {
        List<Message>list = new ArrayList();
        for(AttendGroup attendGroup   : groupList){
            Message message =  initDailyMessage(attendGroup);
            list.add(message);
        }
        boolean result = messageDao.batchSaveMessage(list);
        if (!result){
            messageDao.batchSaveMessage(list);
        }


    }

    /**
     * 封装日报信息
     * @param attendGroup
     * @return
     */
    private Message initDailyMessage(AttendGroup attendGroup) {
        Message msg = new Message();
        msg.setAttendanceId(000000);
        msg.setEnterId(attendGroup.getEnterId());
        msg.setUid(attendGroup.getAdminUid());
        String appId = config.getRcsAppId();
        msg.setAppId(appId);
        String accessNo = config.getRcsAccessNo();
        msg.setSender(accessNo);
        msg.setServerNo(AttendanceUtil.getOneServerNo());
        msg.setMsgType(Constants.MsgType.daily.getValue());
        Date nowDate = new Date();
        msg.setCreateTime(nowDate);
        msg.setModifyTime(nowDate);


        msg.setTopic(config.getDaily());
        msg.setContent(config.getDailyMsgContent());
        msg.setSummary(config.getDailyMsgSummary());
        msg.setSendTime(new Date());

        //消息推送的时候，在标题里增加对应公司名称。
        String enterName = attendGroup.getEnterName();
        if (AssertUtil.isNotEmpty(enterName))
        {
            if ((msg.getTopic() + "-" + enterName).length() > 20){
                    enterName =  enterName.substring(0,5)+"..."+enterName.substring(enterName.length()-5);
            }
            msg.setTopic(msg.getTopic() + "-" + enterName);
        }
        return msg;
    }
}

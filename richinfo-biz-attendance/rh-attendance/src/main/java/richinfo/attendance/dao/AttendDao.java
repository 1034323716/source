/**
 * 文件名：AttendDao.java
 * 创建日期： 2017年6月5日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AttendClockVo;
import richinfo.attendance.bean.AttendRes;
import richinfo.attendance.common.AtdcConsts;
import richinfo.attendance.common.AtdcConsts.SEPARATOR;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.entity.AttendCalendar;
import richinfo.attendance.entity.AttendEntity;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.entity.EmployeeMonthDetail;
import richinfo.attendance.util.*;
import richinfo.dbcomponent.exception.PersistException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述：考勤模块Dao层
 * 
 */
public class AttendDao extends BaseAttendanceDao
{
    private Logger log = LoggerFactory.getLogger(AttendDao.class);
    /** 上午打卡截止时间，用于判断上午、下午班次 */
    private String morningEndTime;
    /** 上午打卡开始时间，用于判断是否迟到 */
    private String morningStartTime;
    /** 下午打卡截止时间，用于判断是否早退 */
    private String afternoonEndTime;
    /** 上午工作时间 */
    private String amTime;
    /** 下午工作时间*/
    private String pmTime;
    /** 弹性开关*/
    private boolean isFlexible = false;

    private AttendanceConfig config = AttendanceConfig.getInstance();


    /**
     * 查询员工一天的考勤记录列表，顺序排列
     * @param uid 企业联系人ID
     * @param attendanceDate 查询日期 应传递当前时间:new Date()，日期截取在sql侧进行处理
     * @return 考勤记录列表，顺序排列
     */
    @SuppressWarnings("unchecked")
    public List<AttendEntity> queryEmployRecord(String uid, Date attendanceDate )
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("attendanceDate", TimeUtil.formatDateTime(attendanceDate, TimeUtil.BASE_DATE_FORMAT));
//        map.put("attendanceId", attendanceId);
        try
        {
            List<AttendEntity> list = attendanceDao.queryForList(
                "attendance.queryEmployRecord", map);
            log.info(
                "queryEmployRecord success from DB,uid={}|attendanceDate={}",
                uid, TimeUtil.formatDateTime(attendanceDate, TimeUtil.BASE_DATE_FORMAT));
            return list;
        }
        catch (Exception e)
        {
            log.error("queryEmployRecord failed,uid={}|attendanceDate={}", uid,
                attendanceDate, e);
        }
        return null;
    }

    /**
     * 查询员工某时间段的考勤记录列表，顺序排列
     * @param attendanceDate 企业联系人ID
//     * @param attendanceTime 查询日期 应传递当前时间:new Date()，日期截取在sql侧进行处理
     * @return 考勤记录列表，顺序排列
     */
    @SuppressWarnings("unchecked")
    public List<AttendEntity> queryEmployRecordByTime(String attendanceDate, String attendanceStartTime,String attendanceEndTime)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceDate",attendanceDate);
        map.put("attendanceStartTime",attendanceStartTime);
        map.put("attendanceEndTime",attendanceEndTime);
        try
        {
            List<AttendEntity> list = attendanceDao.queryForList(
                    "attendance.queryEmployRecordByTime", map);
            log.info(
                    "queryEmployRecord success from DB,attendanceDate={}|attendanceStartTime={}|attendanceEndTime={}",
                    attendanceDate,attendanceStartTime, attendanceEndTime);
            return list;
        }
        catch (Exception e)
        {
            log.error("queryEmployRecord failed,attendanceDate={}|attendanceStartTime={}|attendanceEndTime={}", attendanceDate,
                    attendanceStartTime, attendanceEndTime, e);
        }
        return null;
    }
    
    

    /**
     * 查询员工跨天某时间段考勤记录列表，顺序排列
     * @param uid 企业联系人ID
     * @param endDate 查询日期 应传递当前时间:new Date()，日期截取在sql侧进行处理
     * @return 考勤记录列表，顺序排列
     */
    @SuppressWarnings("unchecked")
    public List<AttendEntity> queryEmployRecordByCrossDay(String uid, Date startDate, Date endDate)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("startDate", TimeUtil.formatDateTime(startDate, TimeUtil.BASE_DATE_FORMAT));
        map.put("endDate", TimeUtil.formatDateTime(endDate, TimeUtil.BASE_DATE_FORMAT));
        map.put("startTime", TimeUtil.formatDateTime(startDate, TimeUtil.BASE_TIME_FORMAT));
        map.put("endTime", TimeUtil.formatDateTime(endDate, TimeUtil.BASE_TIME_FORMAT));
        log.debug("queryEmployRecordByCrossDay uin={}|startDate={}|endDate={}|startTime={}|endTime={}",
        		uid,
        		TimeUtil.formatDateTime(startDate, TimeUtil.BASE_DATE_FORMAT),
        		TimeUtil.formatDateTime(endDate, TimeUtil.BASE_DATE_FORMAT),
        		TimeUtil.formatDateTime(startDate, TimeUtil.BASE_TIME_FORMAT),
        		TimeUtil.formatDateTime(endDate, TimeUtil.BASE_TIME_FORMAT)
        		
        		);
        try
        {
            List<AttendEntity> list = attendanceDao.queryForList(
                "attendance.queryEmployRecordByCrossDay", map);
            log.info(
                "queryEmployRecordByCrossDay success from DB,uid={}|startDate={}|endDate={}",
                uid, startDate, endDate);
            return list;
        }
        catch (Exception e)
        {
            log.error("queryEmployRecordByCrossDay failed,uid={}|startDate={}|endDate={}", uid,
            		startDate, endDate, e);
        }
        return null;
    }
    
    /**
     * 查询员工跨天某时间段考勤记录列表，顺序排列
     * @param uid 企业联系人ID
     * @param endDate 查询日期 应传递当前时间:new Date()，日期截取在sql侧进行处理
     * @return 考勤记录列表，顺序排列
     */
    @SuppressWarnings("unchecked")
    public List<AttendEntity> queryEmployRecordByCrossTime(String uid, Date startDate, Date endDate)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("attendanceDate", TimeUtil.formatDateTime(startDate, TimeUtil.BASE_DATE_FORMAT));
        map.put("startTime", TimeUtil.formatDateTime(startDate, TimeUtil.BASE_TIME_FORMAT));
        map.put("endTime", TimeUtil.formatDateTime(endDate, TimeUtil.BASE_TIME_FORMAT));
        log.debug("queryEmployRecordByCrossDay uin={}|startDate={}|startTime={}|endTime={}",uid,
        		TimeUtil.formatDateTime(startDate, TimeUtil.BASE_DATE_FORMAT),
        		TimeUtil.formatDateTime(startDate, TimeUtil.BASE_TIME_FORMAT),
        		TimeUtil.formatDateTime(endDate, TimeUtil.BASE_TIME_FORMAT)
        		
        		);
        try
        {
            List<AttendEntity> list = attendanceDao.queryForList(
                "attendance.queryEmployRecordByCrossTime", map);
            log.info(
                "queryEmployRecordByCrossTime success from DB,uid={}|startDate={}|endDate={}",
                uid, startDate, endDate);
            return list;
        }
        catch (Exception e)
        {
            log.error("queryEmployRecordByCrossTime failed,uid={}|startDate={}|endDate={}", uid,
            		startDate, endDate, e);
        }
        return null;
    }

    /**
     * 添加考勤打卡记录
     * @param attendRecord
     * @return
     */
    public AttendRes saveAttendRecord(AttendEntity attendRecord) {
        AttendRes attendRes = new AttendRes();
        try
        {
            attendanceDao.startTransaction();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("uid", attendRecord.getUid());
            map.put("attendanceDate", TimeUtil.formatDateTime(new Date(), TimeUtil.BASE_DATE_FORMAT));
            map.put("attendanceId", attendRecord.getAttendanceId());
            //获取当天打卡信息
            List<AttendEntity> attendList = attendanceDao.queryForList("attendance.queryEmployRecord", map);
           // log.info("打卡信息 attendList={}",attendList);
            AttendGroup attendGroup =(AttendGroup) attendanceDao.queryForObject("attendance.queryGroupClockTime",map);
            //log.info("考勤组信息 attendGroup={}",attendGroup);
            //获取公共日期 判断是否节假日
            Date calendarDate = new Date();
            AttendCalendar attendCalendar = (AttendCalendar) attendanceDao.queryForObject(
                    "attendance.queryAttendCalendarByDate", calendarDate);
           // log.info("公共日期 attendCalendar={}",attendCalendar);
            //处理统计数据
            //获取个人当天月报
            EmployeeMonthDetail employeeMonthDetail = (EmployeeMonthDetail)attendanceDao.queryForObject("attendance.queryNomMonthly",map);
            //打卡计算************
            EmployeeMonthDetail monthDetail = clockInStatistics(attendList,attendRecord,attendGroup,attendCalendar);
            attendanceDao.insert("attendance.saveAttendRecord", attendRecord);
            List<EmployeeMonthDetail> detailList = new ArrayList<>();
            detailList.add(monthDetail);
            //不为空  考勤组id不一致
            if (employeeMonthDetail != null && !employeeMonthDetail.getAttendanceId().equals(monthDetail.getAttendanceId()) ) {
                log.debug("不一致employeeMonthDetail.getAttendanceId()={}||||||monthDetail.getAttendanceId()={}",employeeMonthDetail.getAttendanceId() , monthDetail.getAttendanceId());
                monthDetail.setMonthRcdId(employeeMonthDetail.getMonthRcdId());
                attendanceDao.update("attendance.updateEmployeeMonthDetailById",monthDetail);
            }else {
                log.debug("为空或一致employeeMonthDetail.getAttendanceId()={}||||||monthDetail.getAttendanceId()={}",employeeMonthDetail==null?null:employeeMonthDetail.getAttendanceId(), monthDetail.getAttendanceId());
                attendanceDao.insert("attendance.batchSaveEmployeeMonthDetail", monthDetail);
            }
            //  log.info("detailList 保存个人月报后 = {}",detailList);
            AttendClockVo attendClockVo=  addReturnData(monthDetail);
            attendRes.addClockVo(attendClockVo);
            attendRes.setCode(AtdcResultCode.S_OK);
            attendRes.setClockStatue(1);
            attendanceDao.commitTransaction();
            log.info("clock success.attendRecord={}|attendClockVo={}",attendRecord,attendClockVo);
        } catch (Exception e)
        {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                e1.printStackTrace();
            }
            log.error("saveAttendRecord error.attendRecord=", attendRecord, e);
            attendRes.setCode(AtdcResultCode.ATDC107.CLOCK_ERROR);
            attendRes.setSummary(AtdcResultSummary.ATDC107.CLOCK_ERROR);
        }

        return attendRes;
    }

    /**
     * 封装返回数据
     * @param
     * @param monthDetail
     */
    private AttendClockVo addReturnData(EmployeeMonthDetail monthDetail) {

        AttendClockVo attendClockVo = new AttendClockVo();
        int amPmStatue = monthDetail.getAmPmStatue();
        //上班
        if (amPmStatue == 0){
            attendClockVo.setClockTime(monthDetail.getGoWork());
            attendClockVo.setLocation(monthDetail.getGoLocation());
            attendClockVo.setAmPmStatue(0);
            attendClockVo.setStatus(monthDetail.getGoRegionStatus());
            attendClockVo.setRegionStatus(attendDescribeStatue(monthDetail.getGoWorkDesc()));
            //下班
        }else{
            attendClockVo.setClockTime(monthDetail.getLeaveWork());
            attendClockVo.setLocation(monthDetail.getLeaveLocation());
            attendClockVo.setAmPmStatue(1);
            attendClockVo.setStatus(monthDetail.getLeaveRegionStatus());
            attendClockVo.setRegionStatus(attendDescribeStatue(monthDetail.getLeaveWorkDesc()));
        }
        return attendClockVo;
    }

    /**
     * 描述状态
     * @param
     * @return
     */
    private int attendDescribeStatue(String desc) {
        if ("正常".equals(desc)){
            return  0;
        }else if ("迟到".equals(desc)){
            return  1;
        }else if ("早退".equals(desc)) {
            return 2;
        }else if ("未打卡".equals(desc)) {
            return 3;
        }else {
            return -1;
        }
    }

    /**
     * 处理统计数据
     * @param attendList
     * @param attendRecord
     * @param attendGroup
     * @param attendCalendar
     * @return
     */
    private EmployeeMonthDetail clockInStatistics(List<AttendEntity> attendList, AttendEntity attendRecord, AttendGroup attendGroup, AttendCalendar attendCalendar) throws PersistException {
        EmployeeMonthDetail monthDetail = new EmployeeMonthDetail();
        monthDetail.setAttendanceId(attendRecord.getAttendanceId());
        monthDetail.setEnterId(attendGroup.getEnterId());
        monthDetail.setUid(attendRecord.getUid());
        monthDetail.setAttendanceDate(new Date());
        monthDetail.setAttendType(attendGroup.getAttendType());
        monthDetail.setCreateTime(new Date());
        monthDetail.setPhone(attendRecord.getPhone());
        monthDetail.setEmployeeName(attendRecord.getEmployeeName());
        monthDetail.setLateMinutes(0);
        monthDetail.setEarlyMinutes(0);
        monthDetail.setWorkMinutes(0);
        monthDetail.setScheduleShiftId(0L);
        //设置考勤状态为正常
        monthDetail.setRecordState(1);
        //说明法定节假日
       if (attendCalendar.getStatus()== AttendCalendar.CalendarStatus.Holiday.getValue()){
           monthDetail.setRemark(attendCalendar.getRemark());
       }else{
           monthDetail.setRemark(AtdcConsts.REMARK.WEEKEND);
       }

        if (attendGroup.getAttendType() == AttendGroup.AttendType.Fix.getValue()){//固定班
            int week=0;
            //是否工作日
            boolean workday = isFixedAttendWorkDay(attendGroup,attendCalendar);
            log.debug("判断是不是工作日 = {}",workday);
            //是否是补班日期
            boolean rowday = getRowDays(attendGroup,attendCalendar,week);
            log.debug("判断是不是补班日期 = {}",rowday);
            //补班日期判断 需要在非节假日和工作日之前
            if (rowday && attendGroup.getRelyHoliday()== AttendGroup.RelyHoliday.NotRely.getValue()) {
                monthDetail = statisticsWeekdayAttend(rowday,attendList, monthDetail, attendRecord, attendGroup,attendCalendar);
                monthDetail.setRemark(AtdcConsts.REMARK.WEEKDAY);
            }
            //不是工作日且按国家法定节假日处理考勤 即不需要打卡
            else if ((!attendCalendar.isWeekDay()
                    &&attendGroup.getRelyHoliday()== AttendGroup.RelyHoliday.NotRely.getValue())) {
                // 进行非工作日的统计
                monthDetail = statisticsRestdayAttend(attendList,monthDetail,attendRecord);
            }
            //不是节假日，并且不是工作日
            else if(attendCalendar.isWeekDay()&&!workday) {
               // log.info("进行非工作日的统计");
                monthDetail = statisticsRestdayAttend(attendList,monthDetail,attendRecord);
            }
            //节假日，并且节假日需要打卡，并且不是工作日
            else if(!attendCalendar.isWeekDay()
                    &&attendGroup.getRelyHoliday()== AttendGroup.RelyHoliday.Rely.getValue()
                    &&!workday) {
               // log.info("进行非工作日的统计");
                // 进行非工作日的统计
                monthDetail = statisticsRestdayAttend(attendList,monthDetail,attendRecord);
            }
            else {
               // log.info("进行工作日的统计");
                // 进行工作日的统计
//                week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                monthDetail = statisticsWeekdayAttend(false,attendList, monthDetail, attendRecord, attendGroup,attendCalendar);
                monthDetail.setRemark(AtdcConsts.REMARK.WEEKDAY);
            }
        }else  if (attendGroup.getAttendType() == AttendGroup.AttendType.Free.getValue()){
            //是否工作日
            boolean workday = isFreeAttendWorkDay(attendGroup,attendCalendar);
            log.info("自由班 workday={}",workday);
            //节假日并且中国节假日不需要打卡或
            if ((!attendCalendar.isWeekDay()
                    &&attendGroup.getRelyHoliday()== AttendGroup.RelyHoliday.NotRely.getValue()))
            {
                // 进行非工作日的统计
                monthDetail = statisticsRestdayAttend(attendList,monthDetail,attendRecord);
                //不是节假日，并且不是工作日
            }
            else if(attendCalendar.isWeekDay()&&!workday)
            {
                monthDetail = statisticsRestdayAttend(attendList,monthDetail,attendRecord);
            }
            //节假日，并且节假日需要打卡，并且不是工作日
            else if(!attendCalendar.isWeekDay()
                    &&attendGroup.getRelyHoliday()== AttendGroup.RelyHoliday.Rely.getValue()
                    &&!workday)
            {
                // 进行非工作日的统计
                monthDetail = statisticsRestdayAttend(attendList,monthDetail,attendRecord);
            }
            else
            {
                // 进行工作日的统计
                monthDetail =  statisticsWeekdayFreeAttend(attendList,monthDetail,attendRecord);
                monthDetail.setRemark(AtdcConsts.REMARK.WEEKDAY);
            }
        }
        else {
            monthDetail =  statisticsRestdayAttend(attendList,monthDetail,attendRecord);
            monthDetail.setRemark("休息");
            monthDetail.setLeaveWorkDesc(monthDetail.getRemark());
            monthDetail.setGoWorkDesc(monthDetail.getRemark());
            monthDetail.setRecordState(1);
        }
        if (monthDetail.getRegionStatus()==1) {
            monthDetail.setOutWorkRemark(attendRecord.getOutWorkRemark());
        }
        return monthDetail;
    }

    /**
     * 判断是否自由班工作日
     * @return
     */
    private boolean isFreeAttendWorkDay(AttendGroup attendGroup,AttendCalendar attendCalendar){
        boolean workday =  false;
        String freeAttendRule = attendGroup.getFreeAttendRule();
            if (AssertUtil.isEmpty(freeAttendRule)){
                return workday;
            }
            Map jsonObject = JSONObject.parseObject(freeAttendRule);
            log.info("attendGroup.getFreeAttendRule()={}",jsonObject);
            if(AssertUtil.isNotEmpty(jsonObject))
            {
              //  log.info("isFreeAttendWorkDay jsonObject={}",jsonObject);
                int week = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
             //   log.info("isFreeAttendWorkDay week={}",week);
                String iswork = (String) jsonObject.get(ConverUtil.object2String(week));
              //  log.info("isFreeAttendWorkDay week={}",iswork);
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
    //判断固定班是否工作日
    private boolean isFixedAttendWorkDay(AttendGroup attendGroup,AttendCalendar attendCalendar) {
        boolean workday =  false;
        String fixedAttendRule = attendGroup.getFixedAttendRule();
        if (AssertUtil.isEmpty(fixedAttendRule)){
            return workday;
        }
        Map jsonObject = JSON.parseObject(fixedAttendRule);

            if(AssertUtil.isNotEmpty(jsonObject))
            {
                int week = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
                JSONObject workTime = (JSONObject)jsonObject.get(ConverUtil.object2String(week));
                if(AssertUtil.isNotEmpty(workTime))
                {
                    workday = true;
                    this.amTime = workTime.get("amTime").toString();
                    this.pmTime = workTime.get("pmTime").toString();
                }else
                {
                    workday = false;
                }

            }

        return workday;
    }

    /**
     * 固定班工作日处理
     * @param attendList
     * @param monthDetail
     * @param attendRecord
     * @param
     * @return
     */
    private EmployeeMonthDetail statisticsWeekdayAttend(boolean rowday,List<AttendEntity> attendList, EmployeeMonthDetail monthDetail, AttendEntity attendRecord, AttendGroup attendGroup,AttendCalendar attendCalendar) {
        String fixedAttendRule = attendGroup.getFixedAttendRule();
        Map jsonMap = JSON.parseObject(fixedAttendRule);
        int day = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
//        int day = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) == 0 ?7:Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        log.info("jsonMap={}",jsonMap);
        log.info("workDay={}",day);
        if (rowday) {
            Map jsonObject = JSON.parseObject(attendGroup.getFixedAttendRule());
            if (AssertUtil.isNotEmpty(jsonObject)) {
                List dayNum = new ArrayList(jsonObject.keySet());
                day = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
                //周六按固定班最后一个工作日计算 周日按第一个工作日计算
                if (day == 6) {
                    day = Integer.parseInt(dayNum.get(dayNum.size() - 1).toString());
                } else if (day == 7) {
                    day = Integer.parseInt(dayNum.get(0).toString());
                }
            }
        }
        Map attendRuleMap = JSON.parseObject(jsonMap.get(day).toString());
        String amTime = (String)attendRuleMap.get("amTime");
        String pmTime = (String)attendRuleMap.get("pmTime");
        this.amTime = amTime;
        this.pmTime = pmTime;
        //初期化日期
        // 时间以秒数结尾
        String Suffix = ":00";
        // 设置上午班次打卡的截止时间
        this.morningEndTime = AtdcTimeUtil.getEndTime(amTime) + Suffix;
        // 设置上午班次打卡的开始时间
        this. morningStartTime = AtdcTimeUtil.getStartTime(amTime) + Suffix;
        // 设置下午班次打卡的截止时间
        this.afternoonEndTime = AtdcTimeUtil.getEndTime(pmTime) + Suffix;
//        log.info("amTime={}|pmTime={}|morningEndTime={}|morningStartTime={}|afternoonEndTime={}",amTime,pmTime,this.morningEndTime,this. morningStartTime,this.afternoonEndTime);

        //说明是最新打卡
        if (AssertUtil.isEmpty(attendList)){
            monthDetail = handleSingleAttendanceCase(monthDetail, attendRecord,attendGroup,attendCalendar,rowday);
            monthDetail.setRegionStatus(attendRecord.getStatus());
            monthDetail.setRecordState(2);
            //已经存在多次打卡
        }else {
            // 多次打卡记录
            monthDetail = handleMultipleAttendanceCase(monthDetail, attendList.get(0),attendRecord, attendGroup,attendCalendar,rowday);
        }
        return monthDetail;
    }

    // 固定班多次打卡记录
    private EmployeeMonthDetail handleMultipleAttendanceCase(EmployeeMonthDetail monthDetail, AttendEntity firstAttend, AttendEntity lastAttend, AttendGroup attendGroup, AttendCalendar attendCalendar, boolean rowday) {
        // 首次、尾次打卡均在下午班次，相当于只有一次下午打卡记录
        if (!isMorningHours(firstAttend.getAttendanceTime(),attendGroup)
                && !isMorningHours(lastAttend.getAttendanceTime(),attendGroup))
        {
            // 对尾次打卡记录进行判断
            handleSingleAttendanceCase(monthDetail, lastAttend, attendGroup,attendCalendar,rowday);
            monthDetail.setRegionStatus(lastAttend.getStatus());
            monthDetail.setRecordState(2);
            return monthDetail;
        }

        // 首次打卡在上午班次，尾次打卡在下午班次，分别判断上午下午考勤状态
        // 记录上午考勤状态
        handleMorningAttend(monthDetail, firstAttend,attendGroup);
        isFlexible = true;
        // 记录下午考勤状态
        handleAfternoonAttend(monthDetail, lastAttend,attendGroup,attendCalendar,rowday);

        monthDetail.setRegionStatus((firstAttend.getStatus() + lastAttend.getStatus()) == 0 ? 0 : 1);

        int workTime = 0;
        try {
            workTime = (int) (( new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(monthDetail.getLeaveWork(),"HH:mm")).getTime() -
                new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(monthDetail.getGoWork(),"HH:mm")).getTime()) / (60 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        monthDetail.setWorkMinutes(workTime);
        // 判断该天考勤打卡记录是否正常
        if (AtdcConsts.ATTEND_DESC.NORMAL.equals(monthDetail.getGoWorkDesc())
                && AtdcConsts.ATTEND_DESC.NORMAL.equals(monthDetail.getLeaveWorkDesc()))
        {
            monthDetail.setRecordState(1);
        }
        else {
            monthDetail.setRecordState(2);
        }
        return monthDetail;
    }

    //固定班只有一次打卡
    private EmployeeMonthDetail handleSingleAttendanceCase(EmployeeMonthDetail monthDetail, AttendEntity attendRecord,AttendGroup attendGroup,AttendCalendar attendCalendar,boolean rowday) {
        // 判断是否为上午班次
        if (isMorningHours(attendRecord.getAttendanceTime(),attendGroup)) {
            // 判断上午的考勤状态
            handleMorningAttend(monthDetail, attendRecord, attendGroup);
        } else {
            // 记录下午打卡时间、地点
            handleAfternoonAttend(monthDetail, attendRecord, attendGroup,attendCalendar,rowday);
            monthDetail.setGoWork(null);
            monthDetail.setGoWorkDate(null);
            monthDetail.setGoLocation(null);
            monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        }
        monthDetail.setOutWorkRemark(attendRecord.getOutWorkRemark());

        return monthDetail;
    }

    // 记录下午打卡时间、地点
    private void handleAfternoonAttend(EmployeeMonthDetail monthDetail, AttendEntity attendRecord,AttendGroup attendGroup,AttendCalendar attendCalendar,boolean rowday) {
        // 记录下午打卡时间、地点
        monthDetail.setLeaveWork(attendRecord.getAttendanceTime());
        monthDetail.setLeaveWorkDate(attendRecord.getAttendanceDate());
        monthDetail.setLeaveLocation(attendRecord.getLocation());
        //设置打卡状态
        monthDetail.setLeaveRegionStatus(attendRecord.getStatus());
        monthDetail.setAmPmStatue(1);
        // 判断下午是否早退
        if (isEarlyClocked(attendRecord.getAttendanceTime(),attendGroup,monthDetail,attendCalendar,rowday)) {
            //  计算早退时长，排班时还需再优化
            String pmTime = this.pmTime.substring(6);
            Date date = TimeUtil.string2Date(pmTime, "HH:mm");
            int earlyMinutes = (int) ((date.getTime() - TimeUtil.dateToTimeData(monthDetail.getLeaveWork())
                    .getTime()) / (60 * 1000));
            monthDetail.setEarlyMinutes(earlyMinutes);
            // 下午早退
            monthDetail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.EARLY);
        } else {
            // 下午正常
            monthDetail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        }
        monthDetail.setOutWorkRemark(attendRecord.getOutWorkRemark());
    }

    // 判断下午是否早退
    private boolean isEarlyClocked(Date attendanceTime,AttendGroup attendGroup,EmployeeMonthDetail monthDetail,AttendCalendar attendCalendar,boolean rowday) {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
                TimeUtil.BASE_TIME_FORMAT);
        log.info("弹性不弹性={}",(isFlexible && attendGroup.getUseFlexibleRule()==0));
        if (isFlexible &&attendGroup.getUseFlexibleRule()==0) {
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
            Map jsonObject = JSON.parseObject(attendGroup.getFixedAttendRule());
            int day = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
            if (rowday) {
                if (AssertUtil.isNotEmpty(jsonObject) && jsonObject.get(day) == null) {
                    List dayNum = new ArrayList(jsonObject.keySet());
                    //周六按固定班最后一个工作日计算 周日按第一个工作日计算
                    if (day == 6) {
                        day = Integer.parseInt(dayNum.get(dayNum.size() - 1).toString());
                    } else if (day == 7) {
                        day = Integer.parseInt(dayNum.get(0).toString());
                    }
                }
            }
            Map attendRuleMap = JSON.parseObject(jsonObject.get(day).toString());
            String pmTime = (String)attendRuleMap.get("pmTime");
            this.pmTime = pmTime;
            this.afternoonEndTime = AtdcTimeUtil.getEndTime(pmTime) + ":00";
            return this.afternoonEndTime.compareTo(time) > 0;
        }
    }

    // 记录弹性班制的下午打卡时间、地点
    private void handleAfternoonAttendInFlexibleRule(EmployeeMonthDetail monthDetail, AttendEntity attendRecord,AttendGroup attendGroup) {
        // 记录下午打卡时间、地点
        monthDetail.setLeaveWork(attendRecord.getAttendanceTime());
        monthDetail.setLeaveWorkDate(attendRecord.getAttendanceDate());
        monthDetail.setLeaveLocation(attendRecord.getLocation());
        //设置打卡状态
        monthDetail.setLeaveRegionStatus(attendRecord.getStatus());
        monthDetail.setAmPmStatue(1);
        // 判断下午是否早退
        if (isEarlyClockedInFlexibleRule(attendRecord.getAttendanceTime(),attendGroup.getFlexitime())) {
            //  计算早退时长，排班时还需再优化
            String pmTime = this.pmTime.substring(6);
            Date date = TimeUtil.string2Date(pmTime, "HH:mm");
            int earlyMinutes = (int) ((date.getTime() - TimeUtil.dateToTimeData(monthDetail.getLeaveWork())
                .getTime()) / (60 * 1000));
            monthDetail.setEarlyMinutes(earlyMinutes);
            // 下午早退
            monthDetail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.EARLY);
        } else {
            // 下午正常
            monthDetail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        }
        monthDetail.setOutWorkRemark(attendRecord.getOutWorkRemark());
    }

    // 判断弹性班制的下午是否早退
    private boolean isEarlyClockedInFlexibleRule(Date attendanceTime,double flexitime) {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        String m = TimeUtil.getDateTimeOfMinute(this.pmTime);
        String h = TimeUtil.getDateTimeOfHour(this.pmTime);
        int minutes = (int) (flexitime * 60 + Integer.parseInt(h) * 60 + Integer.parseInt(m));
        int hours = (int) Math.floor(minutes / 60);
        int minute = minutes % 60;
        this.morningStartTime = "\""+hours+":"+minute+":00"+"\"";
        String endTime = this.morningStartTime;
        return endTime.compareTo(time) > 0;
    }

    // 判断上午的考勤状态
    private void handleMorningAttend(EmployeeMonthDetail monthDetail, AttendEntity attendRecord,AttendGroup attendGroup) {
        // 记录上午打卡时间、地点
        monthDetail.setGoWork(attendRecord.getAttendanceTime());
        monthDetail.setGoWorkDate(attendRecord.getAttendanceDate());
        monthDetail.setGoLocation(attendRecord.getLocation());
        //设置打卡状态
        monthDetail.setGoRegionStatus(attendRecord.getStatus());

        monthDetail.setAmPmStatue(0);
        monthDetail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        // 判断上午是否迟到
        if (isLateClocked(attendRecord.getAttendanceTime())) {
            //  计算迟到时长，排班时还需再优化
            String amTime = this.amTime.substring(0, 5);
            Date date = TimeUtil.string2Date(amTime, "HH:mm");
            int lateMinutes = (int) ((TimeUtil.dateToTimeData(attendRecord.getAttendanceTime()).getTime() - date.getTime()) / (60 * 1000));
            monthDetail.setLateMinutes(lateMinutes);
            // 当迟到时间超过了允许迟到时间 则判定为迟到
            if(lateMinutes<=attendGroup.getAllowLateTime()) {
                monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
            }else {
                monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.LATE);
            }
        } else {
            // 上午正常
            monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        }
        monthDetail.setOutWorkRemark(attendRecord.getOutWorkRemark());
    }

    // 判断上午的考勤状态
    private void handleMorningAttendInFlexibleRule(EmployeeMonthDetail monthDetail, AttendEntity attendRecord,AttendGroup attendGroup) {
        // 记录上午打卡时间、地点
        monthDetail.setGoWork(attendRecord.getAttendanceTime());
        monthDetail.setGoWorkDate(attendRecord.getAttendanceDate());
        monthDetail.setGoLocation(attendRecord.getLocation());
        //设置打卡状态
        monthDetail.setGoRegionStatus(attendRecord.getStatus());

        monthDetail.setAmPmStatue(0);
        monthDetail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
        // 判断上午是否迟到
        if (isLateClockedInFlexibleRule(attendRecord.getAttendanceTime(),attendGroup.getFlexitime())) {
            //  计算迟到时长，排班时还需再优化
            String amTime = this.amTime.substring(0, 5);
            Date date = TimeUtil.string2Date(amTime, "HH:mm");
            int lateMinutes = (int) ((TimeUtil.dateToTimeData(attendRecord.getAttendanceTime()).getTime() - date
                .getTime()) / (60 * 1000));
            monthDetail.setLateMinutes(lateMinutes);
            // 当迟到时间超过了允许迟到时间 则判定为迟到
            if(lateMinutes<=attendGroup.getAllowLateTime()) {
                monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
            }else {
                monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.LATE);
            }
        } else {
            // 上午正常
            monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
        }
        monthDetail.setOutWorkRemark(attendRecord.getOutWorkRemark());
    }

    // 判断上午是否迟到
    private boolean isLateClocked(Date attendanceTime) {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
                TimeUtil.BASE_TIME_FORMAT);
        return this.morningStartTime.compareTo(time) < 0;
    }

    // 判断是否为上午班次
    private boolean isMorningHours(Date attendanceTime,AttendGroup attendGroup) {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime, TimeUtil.BASE_TIME_FORMAT);
       //判断是否使用了弹性班制规则
        if (AssertUtil.isNotEmpty(attendGroup.getUseFlexibleRule())) {
            if (attendGroup.getUseFlexibleRule()==0) {
                setStartAndEndTimeInFlexibleRule(attendGroup);
                log.info("this.morningEndTime={}",this.morningEndTime);
                log.info("this.morningStartTime={}",this.morningStartTime);
                log.info("this.afternoonEndTime={}",this.afternoonEndTime);
            }
        }
        return this.morningEndTime.compareTo(time) >= 0;
    }

    // 判断上午是否迟到
    private boolean isLateClockedInFlexibleRule(Date attendanceTime,double flexitime) {
        // 截取时间
        String time = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        String m = TimeUtil.getDateTimeOfMinute(this.amTime);
        String h = TimeUtil.getDateTimeOfHour(this.amTime);
        int minutes = (int) (flexitime * 60 + Integer.parseInt(h) * 60 + Integer.parseInt(m));
        int hours = (int) Math.floor(minutes / 60);
        int minute = minutes % 60;
        this.morningStartTime = "\""+hours+":"+minute+":00"+"\"";
        String startTime = this.morningStartTime;
        return startTime.compareTo(time) < 0;
    }

    /**
     * 固定班非工作日   排班非工作日/自由班
      */
    private EmployeeMonthDetail statisticsRestdayAttend(List<AttendEntity> attendList, EmployeeMonthDetail monthDetail, AttendEntity attendRecord) {
        //  monthDetail.setRemark(AtdcConsts.REMARK.HOLIDAY);
        //第一次打卡
        if(AssertUtil.isEmpty(attendList) ){
            monthDetail.setGoWork(attendRecord.getAttendanceTime());
            monthDetail.setGoWorkDate(attendRecord.getAttendanceDate());
            monthDetail.setGoLocation(attendRecord.getLocation());
            //设置打卡状态
            monthDetail.setGoRegionStatus(attendRecord.getStatus());
            monthDetail.setAmPmStatue(0);
            monthDetail.setGoWorkDesc(monthDetail.getRemark());
            monthDetail.setLeaveWorkDesc(monthDetail.getRemark());
            monthDetail.setRegionStatus(attendRecord.getStatus());
        }else{
            //上班
            AttendEntity firstAttend = attendList.get(0);
            monthDetail.setGoWork(firstAttend.getAttendanceTime());
            monthDetail.setGoWorkDate(firstAttend.getAttendanceDate());
            monthDetail.setGoLocation(firstAttend.getLocation());
            monthDetail.setGoWorkDesc(monthDetail.getRemark());
            //下班
            monthDetail.setLeaveWork(attendRecord.getAttendanceTime());
            monthDetail.setLeaveWorkDate(attendRecord.getAttendanceDate());
            monthDetail.setLeaveLocation(attendRecord.getLocation());
            monthDetail.setLeaveWorkDesc(monthDetail.getRemark());
            //设置状态
            //设置打卡状态
            monthDetail.setGoRegionStatus(firstAttend.getStatus());
            monthDetail.setLeaveRegionStatus(attendRecord.getStatus());
            monthDetail.setAmPmStatue(1);
            int workTime = 0;
            try {
                workTime = (int) (( new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(monthDetail.getLeaveWork(),"HH:mm")).getTime() -
                    new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(monthDetail.getGoWork(),"HH:mm")).getTime()) / (60 * 1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            monthDetail.setWorkMinutes(workTime);
            monthDetail.setRegionStatus((firstAttend.getStatus() + attendRecord.getStatus()) == 0 ? 0 : 1);
        }
        return monthDetail;
    }
    /**
     * 工作日   自由班
      */
    private EmployeeMonthDetail statisticsWeekdayFreeAttend(List<AttendEntity> attendList, EmployeeMonthDetail monthDetail, AttendEntity attendRecord) {
       //第一次打卡
        if(AssertUtil.isEmpty(attendList) ){
            monthDetail.setGoWork(attendRecord.getAttendanceTime());
            monthDetail.setGoWorkDate(attendRecord.getAttendanceDate());
            monthDetail.setGoLocation(attendRecord.getLocation());
            monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
            monthDetail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NOT_CLOCKED);
            //设置打卡状态
            monthDetail.setGoRegionStatus(attendRecord.getStatus());
            monthDetail.setAmPmStatue(0);
            monthDetail.setRegionStatus(attendRecord.getStatus());
        }else{
            //上班
            AttendEntity firstAttend = attendList.get(0);
            monthDetail.setGoWork(firstAttend.getAttendanceTime());
            monthDetail.setGoWorkDate(firstAttend.getAttendanceDate());
            monthDetail.setGoWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
            monthDetail.setGoLocation(firstAttend.getLocation());
            //下班
            monthDetail.setLeaveWork(attendRecord.getAttendanceTime());
            monthDetail.setLeaveWorkDate(attendRecord.getAttendanceDate());
            monthDetail.setLeaveLocation(attendRecord.getLocation());
            monthDetail.setLeaveWorkDesc(AtdcConsts.ATTEND_DESC.NORMAL);
            //设置状态
            //设置打卡状态
            monthDetail.setGoRegionStatus(firstAttend.getStatus());
            monthDetail.setLeaveRegionStatus(attendRecord.getStatus());
            monthDetail.setAmPmStatue(1);

            //  计算工作时长(单位为分钟)，后续增加排班制时需要进行修改
            /*int workTime = (int) ((TimeUtil.dateToTimeData(attendRecord.getAttendanceTime()).getTime() - firstAttend
                    .getAttendanceTime().getTime()) / (60 * 1000));
            monthDetail.setWorkMinutes(workTime);*/
            int workTime = 0;
            try {
                workTime = (int) (( new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(monthDetail.getLeaveWork(),"HH:mm")).getTime() -
                    new SimpleDateFormat("HH:mm").parse(TimeUtil.date2String(monthDetail.getGoWork(),"HH:mm")).getTime()) / (60 * 1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            monthDetail.setWorkMinutes(workTime);
            monthDetail.setRegionStatus((firstAttend.getStatus() + attendRecord.getStatus()) == 0 ? 0 : 1);

        }
        return monthDetail;
    }


    /**
     * 硬件打卡保存
     * @param attendRecord
     * @return
     */
    public boolean saveAttendRecordHardClock(AttendEntity attendRecord) {

        try
        {
            attendanceDao.insert("attendance.saveAttendRecord", attendRecord);

            log.debug("saveAttendRecord.attendRecord={}", attendRecord);
            return true;
        }
        catch (PersistException e)
        {
            log.error("saveAttendRecord error.attendRecord=", attendRecord, e);
            return false;
        }
    }

    /**
     * 原生态sql查询
     * @param
     * @return
     * @throws PersistException
     */
    public List originalSqlQuery(Map<String, String> paramMap) {
        try {
            String sql = String.valueOf(paramMap.get("sql"));
            String pageSize  = paramMap.get("pageSize");
            String pageNo  = paramMap.get("pageNo");
            pageSize = StringUtils.isBlank(pageSize)|| pageSize.equals("0")?"10":pageSize;
            pageNo = StringUtils.isBlank(pageNo) || pageNo.equals("0") || pageNo.equals("1")?"0":String.valueOf((Integer.parseInt(pageNo)-1)*Integer.parseInt(pageSize));
            sql=sql+" limit "+pageNo+","+pageSize;
            log.info("originalSqlQuery query sql={}",sql);
            return attendanceDao.originalSqlQuery(sql);
        } catch (PersistException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取补班日期列表
     */
    private boolean getRowDays(AttendGroup attendGroup,AttendCalendar calendar,int week) {
        String[] rowDays = config.getRowDays(TimeUtil.getCurrentYear()).split(SEPARATOR.DATE_LIST);
        log.info("rowDays={}",Arrays.asList(rowDays));
        if (AssertUtil.isNotEmpty(rowDays)) {
            List<String> strings = Arrays.asList(rowDays);
            if (strings.contains(TimeUtil.date2String(calendar.getCalendarDate(),"yyyy-MM-dd"))){
                Map jsonObject = JSON.parseObject(attendGroup.getFixedAttendRule());
                if (AssertUtil.isNotEmpty(jsonObject)) {
                    List dayNum = new ArrayList(jsonObject.keySet());
                    week = AtdcTimeUtil.getWeekNum(calendar.getWeek());
                    JSONObject workTime = null;
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
     * 初始化时间属性
     */
    private void setStartAndEndTime(String amTime,String pmTime)
    {
        this.amTime = amTime;
        this.pmTime = pmTime;
        // 时间以秒数结尾
        String Suffix = ":00";
        // 设置上午班次打卡的截止时间 12:00:00
        this.morningEndTime = AtdcTimeUtil.getEndTime(amTime) + Suffix;
        // 设置上午班次打卡的开始时间 09:00:00
        this.morningStartTime = AtdcTimeUtil.getStartTime(amTime) + Suffix;
        // 设置下午班次打卡的截止时间 18:00:00
        this.afternoonEndTime = AtdcTimeUtil.getEndTime(pmTime) + Suffix;
    }

    /**
     * 初始化弹性班制时间属性
     */
    private void setStartAndEndTimeInFlexibleRule(AttendGroup attendGroup) {
        String fixedAttendRule = attendGroup.getFixedAttendRule();
        Map jsonMap = JSON.parseObject(fixedAttendRule);
        int day = AtdcTimeUtil.getWeekNum("星期"+TimeUtil.getWeekDay());
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

        // 设置上午班次打卡的截止时间 12:00:00
        this.morningEndTime = AtdcTimeUtil.getEndTime(this.amTime) + ":00";
        // 设置上午班次打卡的开始时间 09:00:00
        this.morningStartTime = AtdcTimeUtil.getStartTime(this.amTime) + ":00";
        // 设置下午班次打卡的截止时间 18:00:00
        this.afternoonEndTime = AtdcTimeUtil.getEndTime(this.pmTime) + ":00";
    }

    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        String timeStr = scanner.nextLine();
//        Calendar calendar = Calendar.getInstance();
//        Date date = null;
//        while (!timeStr.equals("exit")) {
//            try {
//                date = new SimpleDateFormat("yyyy-MM-dd").parse(timeStr);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            calendar.setTime(date);
//            int day = (calendar.get(Calendar.DAY_OF_WEEK) - 1) == 0 ?7:calendar.get(Calendar.DAY_OF_WEEK) - 1;
//            System.out.println(day);
//            timeStr = scanner.nextLine();
//        }

    }

    //查询当天的签到（打卡）记录
    public List<AttendEntity> querySignRecords(){
        String time = TimeUtil.formatDateTime(new Date(), TimeUtil.BASE_DATE_FORMAT);
        List<AttendEntity> attendList = new ArrayList<>();
        try {
            attendList = attendanceDao.queryForList("attendance.queryEmployRecord", time);
        } catch (PersistException e) {
            e.printStackTrace();
        }
        return attendList;
    }

}

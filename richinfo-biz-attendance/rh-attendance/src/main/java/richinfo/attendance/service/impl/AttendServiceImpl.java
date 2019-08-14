/**
 * 文件名：AttendServiceImpl.java
 * 创建日期： 2017年6月5日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AttendClockVo;
import richinfo.attendance.bean.AttendReq;
import richinfo.attendance.bean.AttendRes;
import richinfo.attendance.bean.HardAttendReq;
import richinfo.attendance.cache.UserInfoCache;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.dao.*;
import richinfo.attendance.entity.*;
import richinfo.attendance.service.AttendService;
import richinfo.attendance.util.*;

import java.text.ParseException;
import java.util.*;

/**
 * 功能描述：考勤模块接口实现类
 * 
 */
public class AttendServiceImpl implements AttendService
{
    private final Logger logger = LoggerFactory
        .getLogger(AttendServiceImpl.class);

    private AttendDao attendDao = new AttendDao();
    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();
    private AttendGroupDao attendGroupDao = new AttendGroupDao();
    private AttendReportDao attendReportDao = new AttendReportDao();
    private AttendCalendarDao calendarDao = new AttendCalendarDao();
    private UserInfoCache userInfoCache = UserInfoCache.getInstance();
    private AttendanceConfig config = AttendanceConfig.getInstance();



    /**
     * 查询员工当天考勤记录信息
     */
    @Override
    public AttendRes queryEmployRecord(AttendReq req)
    {
        AttendRes respBean = new AttendRes();

        UserInfo userInfo = req.getUserInfo();

        checkQueryEmployeeRecord(req, respBean, userInfo);
        if (!respBean.isSuccess())
        {
            logger
                .info(
                    "queryEmployRecord check param failed,phone={}|sessionUid={}|paramUid={}|resultCode={}|summary={}",
                    userInfo.getPhone(), userInfo.getUid(), req.getUid(),
                    respBean.getCode(), respBean.getSummary());
            return respBean;
        }
        logger.info("queryEmployRecord check param success,phone={}|uid={}",
            userInfo.getPhone(), userInfo.getUid());

        //获取考勤数据
        List<AttendEntity> list = attendDao.queryEmployRecord(req.getUid(),
            new Date());
       // logger.info("queryEmployRecord check  success,return={}",list);


        //封装参数
        if (AssertUtil.isNotEmpty(list) ){
           // logger.info("有打卡进来判断按钮封装参数！");
            //获取个人月报的当天数据
            EmployeeMonthDetail employeeMonthDetail =  attendReportDao.queryNomMonthly(req.getUid(),
                    TimeUtil.formatDateTime(new Date(),TimeUtil.BASE_DATE_FORMAT));
           // logger.info("有打卡进来判断按钮封装参数！employeeMonthDetail = {}",employeeMonthDetail);
            AttendEntity attendEntity = list.get(0);
          //  logger.info("时间判断：attendEntity.getAttendanceTime().getTime()={}|employeeMonthDetail.getGoWork().getTime()={}",
             //       attendEntity.getAttendanceTime(),employeeMonthDetail.getGoWork());
            //只有一条记录
            if(list.size() == 1){
                //上班
                if (null !=employeeMonthDetail && employeeMonthDetail.getGoWork() != null &&
                        TimeUtil.formatDateTime(attendEntity.getAttendanceTime(),TimeUtil.BASE_TIME_FORMAT)
                                        .equals(TimeUtil.formatDateTime(employeeMonthDetail.getGoWork(),TimeUtil.BASE_TIME_FORMAT))){
                    //封装上班打卡数据
                    logger.debug("一条数据封装 上班............");
                    List<AttendClockVo>clockList = new ArrayList();
                    posttingGoClockData( clockList,attendEntity,employeeMonthDetail);
                    respBean.setAttendClockVos(clockList);
                    respBean.setClockStatue(1);
                    //下班
                }else{
                    logger.debug("一条数据封装 下班............");
                    //封装下班打卡数据
                    List<AttendClockVo>clockList = new ArrayList();
                    posttingLetClockData( clockList,attendEntity,employeeMonthDetail);
                    respBean.setAttendClockVos(clockList);
                    respBean.setClockStatue(1);

                }
                //2条以上
            }else {
                //说明一条上班  一条下班
                if (employeeMonthDetail.getGoWork() != null &&
                        TimeUtil.formatDateTime(attendEntity.getAttendanceTime(),TimeUtil.BASE_TIME_FORMAT)
                                .equals(TimeUtil.formatDateTime(employeeMonthDetail.getGoWork(),TimeUtil.BASE_TIME_FORMAT))){
                    //封装上班打卡数据
                    List<AttendClockVo>clockList = new ArrayList();
                    posttingGoClockData( clockList,attendEntity,employeeMonthDetail);
                    //封装下班打卡数据
                    posttingLetClockData( clockList,list.get(list.size()-1),employeeMonthDetail);
                    respBean.setAttendClockVos(clockList);
                    respBean.setClockStatue(1);
                    //说明都是下班
                    logger.debug("两条数据封装 上下班............");
                }else{
                    logger.debug("两条数据封装  下班............");
                    List<AttendClockVo>clockList = new ArrayList();
                    //封装下班打卡数据
                    posttingLetClockData( clockList,list.get(list.size()-1),employeeMonthDetail);
                    respBean.setAttendClockVos(clockList);
                    respBean.setClockStatue(1);
                }
            }

        }else {
            logger.debug("没有打卡进来判断按钮！");
            //设置打卡按钮标识
            respBean.setClockStatue(isClockStatue(userInfo.getAttendanceId(),list));
           /* AttendClockVo attendClockVo = new AttendClockVo();
            respBean.addClockVo(attendClockVo);*/
        }
        respBean.setSummary(AtdcResultCode.S_OK);
        logger.info("查询用户当天打卡数据respBean={}",respBean);
        return respBean;
    }

    /**
     * 封装下班打卡数据
     * @param
     * @param attendEntity
     * @param employeeMonthDetail
     */
    private void posttingLetClockData(List<AttendClockVo>clockList, AttendEntity attendEntity, EmployeeMonthDetail employeeMonthDetail) {
        AttendClockVo letAttendClockVo = new AttendClockVo();
        letAttendClockVo.setClockTime(attendEntity.getAttendanceTime());
        letAttendClockVo.setLocation(attendEntity.getLocation());
        letAttendClockVo.setStatus(attendEntity.getStatus());
        letAttendClockVo.setRegionStatus(attendDescribeStatue(employeeMonthDetail.getLeaveWorkDesc()));
        letAttendClockVo.setAmPmStatue(1);
        clockList.add(letAttendClockVo);
    }

    /**
     * 封装上班打卡数据
     * @param attendEntity
     * @param employeeMonthDetail
     */
    private void posttingGoClockData(List<AttendClockVo>clockList ,AttendEntity attendEntity, EmployeeMonthDetail employeeMonthDetail) {

        AttendClockVo attendClockVo = new AttendClockVo();
        attendClockVo.setClockTime(attendEntity.getAttendanceTime());
        attendClockVo.setLocation(attendEntity.getLocation());
        attendClockVo.setStatus(attendEntity.getStatus());
        attendClockVo.setRegionStatus(attendDescribeStatue(employeeMonthDetail.getGoWorkDesc()));
        attendClockVo.setAmPmStatue(0);
        clockList.add(attendClockVo);
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

    //打卡状态标识
    private int isClockStatue(long attendanceId,List<AttendEntity> list) {

        //查询考勤时间
        List<AttendGroup> GroupList = attendGroupDao.queryGroupClockTime(attendanceId);
        AttendGroup attendGroup = GroupList.get(0);
        int attendType = attendGroup.getAttendType();
        //int clockStatue = 0;
         if (attendType == AttendGroup.AttendType.Fix.getValue()){
             //获取节假日
             Date yesterday = new Date();
             AttendCalendar attendCalendar = calendarDao.queryAttendCalendarByDate(yesterday);
             //是否工作日
             boolean workday = isFixedAttendWorkDay(attendGroup ,attendCalendar);
             //节假日并且中国节假日不需要打卡或
             if ((!attendCalendar.isWeekDay()
                     &&attendGroup.getRelyHoliday()== AttendGroup.RelyHoliday.NotRely.getValue()))
             {
                 logger.debug("节假日统计");
                 // 进行非工作日的统计
                 return  AssertUtil.isEmpty(list)? 0 : 1;
                 //不是节假日，并且不是工作日
             }
             else if(attendCalendar.isWeekDay()&&!workday)
             {
                 logger.debug("节假日统计");
                 return  AssertUtil.isEmpty(list)? 0 : 1;
             }
             //节假日，并且节假日需要打卡，并且不是工作日
             else if(!attendCalendar.isWeekDay()
                     &&attendGroup.getRelyHoliday()== AttendGroup.RelyHoliday.Rely.getValue()
                     &&!workday)
             {
                 logger.debug("节假日统计");
                 // 进行非工作日的统计
                 return  AssertUtil.isEmpty(list)? 0 : 1;
             }
             else
             {
                 logger.debug("工作日统计");
                 //存在打卡返回下班按钮标识
                 if (AssertUtil.isNotEmpty(list)){
                    return 1;
                    //没有打卡
                 }else{

                     String fixedAttendRule = attendGroup.getFixedAttendRule();
                     Map jsonMap = JSON.parseObject(fixedAttendRule);
                     int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                   //  logger.info("固定班工作日 jsonMap= {} ，day={}",jsonMap,day==0?7:day);
                     if(jsonMap != null){
                         Map attendRuleMap = JSON.parseObject(jsonMap.get(day==0?7:day).toString());
                         String amTime = (String)attendRuleMap.get("amTime");
                         amTime = AtdcTimeUtil.getEndTime(amTime) + ":00";
                         // 截取时间
                         String time = TimeUtil.date2String(new Date(),
                                 TimeUtil.BASE_TIME_FORMAT);
                         return   amTime.compareTo(time) < 0 ? 1:0;
                     }
                     return AssertUtil.isEmpty(list)? 0 : 1;
                 }

             }

        }else {
             return  AssertUtil.isEmpty(list)? 0 : 1;
         }
    }

    /**
     * 判断是否固定班工作日
     * @return
     */
    private boolean isFixedAttendWorkDay(AttendGroup attendGroup,AttendCalendar attendCalendar){
        boolean workday =  false;
        try
        {
            Map jsonObject = JSON.parseObject(attendGroup.getFixedAttendRule());

            if(AssertUtil.isNotEmpty(jsonObject))
            {

                int week = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());

                Map workTime = (Map) jsonObject.get(ConverUtil.object2String(week));

                if(AssertUtil.isNotEmpty(workTime))
                {
                    workday = true;

                }else
                {
                    workday = false;
                }

            }

        }catch(Exception e){
                logger.error("json 解析异常e{}" ,e);

        }
        return workday;
    }

    /**
     * 校验查询员工考勤记录请求参数
     * @param req
     * @param respBean
     * @param userInfo
     */
    private void checkQueryEmployeeRecord(AttendReq req, AttendRes respBean,
        UserInfo userInfo)
    {
        // 1、校验参数合法性
        if (AssertUtil.isEmpty(req.getUid()))
        {
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }
        // 2、判断该员工信息与传递的会话信息是否一致
        if (!req.getUid().equals(userInfo.getUid()))
        {
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_ILLEGAL_UID);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_ILLEGAL_UID);
            return;
        }
    }

    @Override
    public AttendRes clock(AttendReq attendReq) {
        // 校验请求参数
        AttendRes attendRes = checkClockReqPara(attendReq);
        if (!attendRes.isSuccess()) {
            logger.warn("checkClockReqPara falied.attendReq={}", attendReq);
            return attendRes;
        }

        // 组装打卡记录
        AttendEntity attendRecord = assembleAttendRecord(attendReq);
        logger.info("打卡封装参数完成 attendRecord={},outWorkRemark={}",attendRecord,attendRecord.getOutWorkRemark());

        //查询该打卡员工有无设备开启设备打卡功能
        //根据enterid查得equipmentLimit
        Map<String,Object> temp = new HashMap();
        temp.put("enterId",attendReq.getUserInfo().getEnterId());
        if(attendReq.getClockSource() == null || "0".equals(attendReq.getClockSource())) {
            AttendanceEquipmentControl attendanceEquipmentControl = attendGroupDao.queryEquipmentStatus(temp);
            if (AssertUtil.isNotEmpty(attendanceEquipmentControl)) {
                if ("0".equals(attendanceEquipmentControl.getEquipmentUseStatus())) {
                    if (AssertUtil.isEmpty(attendReq.getEquipmentSerial())) {
                        attendRes.setCode(AtdcResultCode.S_ERROR);
                        attendRes.setSummary("获取设备号失败，请更新和飞信最新版");
                        return attendRes;
                    }
                    //获取该企业已录入的设备列表
                    List<AttendanceEquipment> equipmentList = attendGroupDao.queryEquipments(temp);
                    temp.put("uid",attendReq.getUserInfo().getUid());
                    int i = attendGroupDao.queryEquipmentNumByUid(temp);
                    boolean flag = true;
                    //若该列表为空 则说明没有录入过
                    if (AssertUtil.isNotEmpty(equipmentList)) {
                        for (AttendanceEquipment equipment : equipmentList) {
                            //判断是否已经有录入过相同的设备 若已经录入则不再录入 若已录入的设备被删除则还原
                            if (attendReq.getEquipmentSerial().equals(equipment.getEquipmentSerial()) && "0".equals(equipment.getEquipmentStatus())) {
                                //判断该设备是否是本人的
                                if (attendReq.getUid().equals(equipment.getUid())) {
                                    logger.info("**********************clockSerial={}", attendReq.getEquipmentSerial() + "******************************");
                                    return attendDao.saveAttendRecord(attendRecord);
                                } else {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                    }

                    if (flag) {
                        //判断该员工的设备是否已经满额
                        if (Integer.parseInt(attendanceEquipmentControl.getEquipmentLimit()) < i) {
                            attendRes.setCode(AtdcResultCode.S_ERROR);
                            attendRes.setSummary("设备数量超出了限制");
                            return attendRes;
                        }
                    } else {
                        attendRes.setCode(AtdcResultCode.S_ERROR);
                        attendRes.setSummary("当前设备已被绑定，无法打卡");
                        return attendRes;
                    }
                }
            }
        }

        logger.info("**********************clockSerial={}",attendReq.getEquipmentSerial()+"******************************");
        // 员工打卡
        attendRes = attendDao.saveAttendRecord(attendRecord);
        return attendRes;
    }

    /**
     * 组装考勤打卡记录的入库实体
     * @param attendReq
     * @return
     */
    private AttendEntity assembleAttendRecord(AttendReq attendReq)
    {
        AttendEntity attendRecord = new AttendEntity();

        // 设置打卡员工、打卡地点
        attendRecord.setUid(attendReq.getUid());
        attendRecord.setPhone(attendReq.getPhone());
        attendRecord.setAttendanceId(attendReq.getAttendanceId());
        attendRecord.setLocation(attendReq.getLocation());
        attendRecord.setDetailAddr(attendReq.getDetailAddr());
        attendRecord.setLongitude(attendReq.getLongitude());
        attendRecord.setLatitude(attendReq.getLatitude());
        attendRecord.setStatus(attendReq.getStatus());
        if (attendReq.getStatus()==1){
            attendRecord.setOutWorkRemark(attendReq.getOutWorkRemark());
        }
        attendRecord.setEmployeeName(attendReq.getUserInfo().getEmployeeName());

        // 设置打卡时间，日期、时间由数据库底层截断
        attendRecord.setAttendanceDate(new Date());
        attendRecord.setAttendanceTime(new Date());

        return attendRecord;
    }

    /**
     * 参数的基本校验，非空、长度等
     * @param attendReq
     * @return
     */
    private AttendRes checkClockReqPara(AttendReq attendReq)
    {
        AttendRes attendRes = new AttendRes();
        // 企业联系人ID校验
        if (AssertUtil.isEmpty(attendReq.getUid()))
        {
            attendRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            attendRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return attendRes;
        }
        // 企业联系人ID匹配性校验
        if (!attendReq.getUid().equals(attendReq.getUserInfo().getUid()))
        {
            logger.warn("uid is not match.attendancId={}|userInfo={}",
                attendReq.getUid(), attendReq.getUserInfo());
            attendRes.setCode(AtdcResultCode.ATDC106.UID_NOT_MATCHED);
            attendRes.setSummary(AtdcResultSummary.ATDC106.UID_NOT_MATCHED);
            return attendRes;
        }


        // 考勤组非空校验，暂时不进行考勤组匹配性校验，防止考勤组关系变动导致不能打卡
        if (attendReq.getAttendanceId() <= 0)
        {
            attendRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            attendRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return attendRes;
        }
        // 校验考勤地点
        if (AssertUtil.isEmpty(attendReq.getLocation())
            || AssertUtil.isEmpty(attendReq.getDetailAddr()))
        {
            attendRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            attendRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return attendRes;
        }

        // 考勤地点,长度校验，超长截断处理
        String location = attendReq.getLocation();
        if (location.length() > config.getCommonNameLength())
        {
            logger
                .warn("location is too long,cut it off.location={}", location);
            attendReq.setLocation(location.substring(0,
                config.getCommonNameLength()));
        }
        // 考勤详细地址,长度校验
        String detailAddr = attendReq.getDetailAddr();
        if (detailAddr.length() > config.getDetailAddrLength())
        {
            logger.warn("detailAddr is too long,cut it off.detailAddr={}",
                detailAddr);
            attendReq.setDetailAddr(detailAddr.substring(0,
                config.getDetailAddrLength()));
        }

        logger.debug("checkClockReqPara success.attendReq={}|userInfo={}",
            attendReq, attendReq.getUserInfo());
        return attendRes;
    }

    @Override
    public AttendRes hardClock(HardAttendReq attendReq)
    {
        //参数校验
        AttendRes res = checkHardReqPara(attendReq);
        if(!res.isSuccess()){
            return res;
        }
        
        // 检查打卡用户参数enterId、phone的有效性
        List<UserInfo> userList = employeeDao.queryUserByPhone(attendReq.getEnterId(), attendReq.getPhone());
        if(AssertUtil.isEmpty(userList)){
            logger.info("hardClock queryUserByPhone failure,userList is empty! enterId={}|phone={}",
                attendReq.getEnterId(),attendReq.getPhone());
            res.setCode(AtdcResultCode.ATDC102.USER_SESSION_ERROR);
            res.setSummary(AtdcResultSummary.ATDC102.USER_SESSION_ERROR);
            return res;
        }else{
            // 组装打卡记录
            UserInfo user = userList.get(0);
            attendReq.setUid(user.getUid());
            attendReq.setAttendanceId(user.getAttendanceId());
            AttendEntity attendRecord = assembleHardRecord(attendReq);
            // 员工打卡
            if (attendDao.saveAttendRecordHardClock(attendRecord))
            {
                logger.info("hardClock success.attendRecord={}|useTime={}",
                    attendRecord, attendReq.getUseTime());
            }
            else
            {
                // 打卡失败
                res.setCode(AtdcResultCode.ATDC107.CLOCK_ERROR);
                res.setSummary(AtdcResultSummary.ATDC107.CLOCK_ERROR);
                logger.error("hardClock failed.attendRecord={}", attendRecord);
            }

        }
        
        return res;
    }

    /**
     * 
     * @param attendReq
     * @return
     */
    private AttendEntity assembleHardRecord(HardAttendReq attendReq)
    {

        AttendEntity attendRecord = new AttendEntity();

        // 设置打卡员工、打卡地点
        attendRecord.setUid(attendReq.getUid());
        attendRecord.setPhone(attendReq.getPhone());
        attendRecord.setAttendanceId(attendReq.getAttendanceId());
        attendRecord.setLocation(attendReq.getLocation());
        attendRecord.setDetailAddr(attendReq.getDetailAddr());
        attendRecord.setLongitude(attendReq.getLongitude());
        attendRecord.setLatitude(attendReq.getLatitude());
        attendRecord.setStatus(attendReq.getStatus());

        // 设置打卡时间，日期、时间由数据库底层截断
        Date date = null;
        if (AssertUtil.isNotEmpty(attendReq.getClockTime()))
        {   
            try
            {
                // 打卡时间转换
                date = TimeUtil.string2DateTime(attendReq.getClockTime());
            }
            catch (ParseException e)
            {
                logger.error("string2DateTime error! uid={}| phone ={} | clockTime={}",
                    attendReq.getUid(), attendReq.getPhone(), attendReq.getClockTime(),e);
                date = new Date();
            }
        }else {
            date = new Date();
        }
        attendRecord.setAttendanceDate(date);
        attendRecord.setAttendanceTime(date);

        return attendRecord;
    
    }

    /**
     * 硬件打卡参数校验
     * @param attendReq
     * @return
     */
    private AttendRes checkHardReqPara(HardAttendReq attendReq)
    {
        AttendRes res = new AttendRes();
        long curTime = System.currentTimeMillis();
        // 检查前面参数时间戳  离目前时间大于30分钟则失效
        if(AssertUtil.isEmpty(attendReq.getEnterId()) || AssertUtil.isEmpty(attendReq.getPhone())){
            res.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return res;
        }
        // 检查前面参数时间戳  离目前时间大于30分钟则失效
        if((attendReq.getCurTime() - curTime) > 1800000){
            logger.info("checkHardReqPara failure,sign is too old! enterId={}|phone={}",
                attendReq.getEnterId(),attendReq.getPhone());
            res.setCode(AtdcResultCode.ATDC102.USER_SESSION_ERROR);
            res.setSummary(AtdcResultSummary.ATDC102.USER_SESSION_ERROR);
            return res;
        }
        // 检查签名的有效性
        String sign = creatSign(attendReq.getEnterId(),attendReq.getCurTime());
        if(!sign.equalsIgnoreCase(attendReq.getToken())){
            logger.info("checkHardReqPara, sign check is failure! enterId={}|phone={}|sign={}",
                attendReq.getEnterId(),attendReq.getPhone(),sign);
            res.setCode(AtdcResultCode.ATDC102.USER_SESSION_ERROR);
            res.setSummary(AtdcResultSummary.ATDC102.USER_SESSION_ERROR);
            return res;
        }
        
        return res;
    }

    /**
     * 按约定参数生成sign签名  MD5
     * @param enterId
     * @param curTime
     * @return
     */
    private String creatSign(String enterId, long curTime)
    {
        String key = AttendanceConfig.getInstance().getProperty("attend.hardkey."+enterId);
        return EncryptionUtil.getMD5ByUtf8(enterId+curTime+key);
    }

}

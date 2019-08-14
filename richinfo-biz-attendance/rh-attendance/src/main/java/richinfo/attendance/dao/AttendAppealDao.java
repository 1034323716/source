/**
 * 文件名：AttendAppealDao.java
 * 创建日期： 2017年10月13日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月13日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AbnormalAppealRes;
import richinfo.attendance.bean.AttendAppealReq;
import richinfo.attendance.bean.AttendGroupReq;
import richinfo.attendance.bean.AttendGroupRes;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.common.ResBean;
import richinfo.attendance.entity.*;
import richinfo.attendance.msg.Constants;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.attendance.util.TimeUtil;
import richinfo.dbcomponent.exception.PersistException;

import java.util.*;

/**
 * 功能描述：考勤异常申诉
 *
 */
public class AttendAppealDao extends BaseAttendanceDao
{

    private Logger logger = LoggerFactory.getLogger(AttendAppealDao.class);
    private AttendanceConfig config = AttendanceConfig.getInstance();
    private MessageDao messageDao = new MessageDao();

    /**
     * 具体申诉单详情
     * @param reqBean
     * @return
     * @throws PersistException
     */
    public AttendAppealReq querySingleAppealInfo(AttendAppealReq reqBean)
        throws PersistException
    {
        long appealId = reqBean.getAppealId();
        return (AttendAppealReq) attendanceDao.queryForObject(
            "attendance.querySingleAppealInfo", appealId);
    }

    /**
     * 用户已申诉数据信息
     * @param reqBean
     * @throws PersistException
     */
    @SuppressWarnings("unchecked")
    public List<AttendAppealReq> queryUserAppealList(AttendAppealReq reqBean)
        throws PersistException
    {
        return (List<AttendAppealReq>) attendanceDao.queryForList(
            "attendance.queryUserAppealList", reqBean);
    }

    /**
     * 用户已申诉总数量
     * @param reqBean
     * @return
     * @throws PersistException
     */
    public long queryUserAppealListCount(AttendAppealReq reqBean)
        throws PersistException
    {
        return (Long) attendanceDao.queryForObject(
            "attendance.queryUserAppealListCount", reqBean);
    }

    /**
     * 审批员待审批单总数量
     * @param reqBean
     * @return
     * @throws PersistException
     */
    public long queryManageAppealListCount(AttendAppealReq reqBean)
        throws PersistException
    {
        return (Long) attendanceDao.queryForObject(
            "attendance.queryManageAppealListCount", reqBean);
    }

    /**
     * 审批员待审批单数据
     * @param reqBean
     * @return
     * @throws PersistException
     */
    @SuppressWarnings("unchecked")
    public List<AttendAppealReq> queryManageAppealList(AttendAppealReq reqBean)
        throws PersistException
    {
        return (List<AttendAppealReq>) attendanceDao.queryForList(
            "attendance.queryManageAppealList", reqBean);
    }

    /***
     * 查询用户个人月报详情某一天的记录
     * @param reqBean
     * @return
     * @throws PersistException
     */
    public EmployeeMonthDetail queryUserAttendDetail(AttendAppealReq reqBean)
        throws PersistException
    {
        return (EmployeeMonthDetail) attendanceDao.queryForObject(
            "attendance.queryUserAttendDetail", reqBean);
    }

    /**
     * 查询考勤组对应的审批员信息
     * @param reqBean
     * @return
     * @throws PersistException
     */
    public AttendExamineEntity queryExamineUid(AttendAppealReq reqBean)
        throws PersistException
    {
        return (AttendExamineEntity) attendanceDao.queryForObject(
            "attendance.queryExamineUid", reqBean);
    }

    /**
     * 插入考勤异常申诉记录
     * @param reqBean
     * @return
     * @throws PersistException
     */
    public void abnormalAppeal(AttendAppealReq reqBean,
        AbnormalAppealRes resBean)
    {
        try
        {
            attendanceDao.startTransaction();
            long appealId= (long) attendanceDao.insert("attendance.abnormalAppeal", reqBean);
            logger.info("提交审批单返回 appealId={}",appealId);
            resBean.setAppealId(appealId);
            Map<String, Object> mapParam = new HashMap<String, Object>();

            mapParam.put("recordState", 3);
            mapParam.put("monthRcdId", reqBean.getMonthRcdId());
            int result = updatePersonMonthExamineResult(mapParam);
            logger.info("abnormalAppeal isnert DB,reqParam={}|result={}",
                reqBean, result);
            if (result < 1)
            {
                resBean.setCode(AtdcResultCode.ATDC107.ATTEND_APPEAL_FAIL);
                resBean.setSummary(AtdcResultSummary.ATDC107.ATTEND_APPEAL_FAIL);
                attendanceDao.rollbackTransaction();
                return;
            }

            reqBean.setAppealId(appealId);
            //保存审批推送消息
            //封装消息推送
            Message message = initMessage(reqBean, Constants.MsgType.authorization.getValue());
            List<Message>list = new ArrayList<>();
            list.add(message);
            boolean bool = attendanceDao.batchInsert("attendance.batchSaveMessage",
                    list);
            if (!bool){
                messageDao.batchSaveMessage(list);
            }

            attendanceDao.commitTransaction();
        }
        catch (PersistException e)
        {
            try {
                attendanceDao.rollbackTransaction();
            }
            catch (PersistException e1)
            {}
            logger.error("abnormalAppeal insert into DB failed,enterId={}|uid={}|phone={}|attendanceId={}",
                reqBean.getEnterId(), reqBean.getUid(), reqBean.getUserInfo().getPhone(), reqBean.getAttendanceId(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return;
        }
    }

    private Message initMessage( AttendAppealReq reqBean ,int msgType)
    {
        Message msg = new Message();
        msg.setAttendanceId(reqBean.getAttendanceId());
        msg.setEnterId(reqBean.getEnterId());
        msg.setAppealId(reqBean.getAppealId());
        String appId = config.getRcsAppId();
        msg.setAppId(appId);
        String accessNo = config.getRcsAccessNo();
        msg.setSender(accessNo);
        msg.setServerNo(AttendanceUtil.getOneServerNo());
        msg.setMsgType(msgType);
        Date nowDate = new Date();
        msg.setCreateTime(nowDate);
        msg.setModifyTime(nowDate);
        msg.setSendTime(new Date());

        // 审批提交标题、内容、摘要
        if (Constants.MsgType.authorization.getValue() == msgType)
        {
            msg.setUid(reqBean.getExamineUid());
            msg.setTopic(config.getAuthorization());
            msg.setContent(config.getAuthorizationMsgContent());
            msg.setSummary(config.getAuthorizationMsgSummary());

        }

        // 审批通过、内容、摘要
        else if (Constants.MsgType.authorizationPass.getValue() == msgType)
        {
            msg.setUid(reqBean.getUid());
            msg.setTopic(config.getAuthorizationPass());
            msg.setContent(config.getAuthorizationPassMsgContent());
            msg.setSummary(config.getAuthorizationPassMsgSummary());

        }// 审批拒绝、内容、摘要
        else if (Constants.MsgType.authorizationReject.getValue() == msgType){
            msg.setUid(reqBean.getUid());
            msg.setTopic(config.getAuthorizationReject());
            msg.setContent(config.getAuthorizationRejectMsgContent());
            msg.setSummary(config.getAuthorizationRejectMsgSummary());
        }
        //消息推送的时候，在标题里增加对应公司名称。
        if (AssertUtil.isNotEmpty(reqBean.getEnterName()))
        {
            String enterName = reqBean.getEnterName();
            if ((msg.getTopic() + "-" + enterName).length() > 20){
                if (Constants.MsgType.authorization.getValue() == msgType){
                    enterName =  enterName.substring(0,5)+"..."+enterName.substring(enterName.length()-4);
                }else {
                    enterName =  enterName.substring(0,4)+"..."+enterName.substring(enterName.length()-4);
                }
            }
            msg.setTopic(msg.getTopic() + "-" + enterName);
        }
        return msg;
    }


    /**
     * 撤销考勤异常申诉单
     * @param reqBean
     * @return
     * @throws PersistException
     */
    public ResBean cancelAppeal(AttendAppealReq reqBean, ResBean resBean,
        AttendAppealReq appealInfo)
    {
        try
        {
            attendanceDao.startTransaction();
            int result = cancelAppealRecord(reqBean);
            if (result < 1)
            {
                resBean.setCode(AtdcResultCode.ATDC107.APPEAL_CANCEL_FAIL);
                resBean
                    .setSummary(AtdcResultSummary.ATDC107.APPEAL_CANCEL_FAIL);
                attendanceDao.rollbackTransaction();
                return resBean;
            }
            // 4、将个人月报详情中的记录数据审核结果进行更新
            Map<String, Object> mapParam = new HashMap<String, Object>();
            mapParam.put("recordState", 6);
            mapParam.put("monthRcdId", appealInfo.getMonthRcdId());
            result = updatePersonMonthExamineResult(mapParam);
            if (result < 1)
            {
                resBean.setCode(AtdcResultCode.ATDC107.APPEAL_EXAMINE_FAIL);
                resBean
                    .setSummary(AtdcResultSummary.ATDC107.APPEAL_EXAMINE_FAIL);
                attendanceDao.rollbackTransaction();
                return resBean;
            }
            logger
                .info(
                    "cancelAppeal update success,enterId={}|uid={}|phone={}|attendanceId={}|appealId={}",
                    reqBean.getEnterId(), reqBean.getUid(), reqBean
                        .getUserInfo().getPhone(),
                    appealInfo.getAttendanceId(), appealInfo.getAppealId());
            attendanceDao.commitTransaction();
        }
        catch (PersistException e)
        {
            try
            {
                attendanceDao.rollbackTransaction();
            }
            catch (PersistException e1)
            {}
            logger
                .error(
                    "cancelAppeal update DB failed,enterId={}|uid={}|phone={}|attendanceId={}|appealId={}",
                    reqBean.getEnterId(), reqBean.getUid(), reqBean
                        .getUserInfo().getPhone(),
                    appealInfo.getAttendanceId(), appealInfo.getAppealId(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return resBean;
        }
        return resBean;
    }

    /**
     * 撤销已申请的考勤异常申诉单
     * @param reqBean
     * @return
     * @throws PersistException
     */
    private int cancelAppealRecord(AttendAppealReq reqBean)
        throws PersistException
    {
        return attendanceDao.update("attendance.cancelAppeal", reqBean);
    }

    public ResBean dealAppeal(AttendAppealReq reqBean, ResBean resBean, AttendAppealReq appealInfo) {
        if (1 == reqBean.getExamineResult()) {
            // 审核同意
            try {
                attendanceDao.startTransaction();
                // 1、更新审核结果
                int result = updateAppealResult(reqBean);
                if (result < 1) {
                    logger.info("dealAppeal update appealResult failed,appealId={}|monthRcdId={}|enterId={}|examineUid={}|examineResult={}|result={}",
                            reqBean.getAppealId(), appealInfo.getMonthRcdId(),
                            reqBean.getEnterId(), reqBean.getExamineUid(),
                            reqBean.getExamineResult(), result);
                    resBean.setCode(AtdcResultCode.ATDC107.APPEAL_EXAMINE_FAIL);
                    resBean.setSummary(AtdcResultSummary.ATDC107.APPEAL_EXAMINE_FAIL);
                    attendanceDao.rollbackTransaction();
                    return resBean;
                }
                //查询当天个人月报
                EmployeeMonthDetail employeeMonthDetail =  queryMonthByMonthRcdId(appealInfo.getMonthRcdId());
                if (employeeMonthDetail == null){
                    logger.info("queryMonthByMonthRcdId is null==========");
                    resBean.setCode(AtdcResultCode.ATDC107.APPEAL_EXAMINE_FAIL);
                    resBean.setSummary(AtdcResultSummary.ATDC107.APPEAL_EXAMINE_FAIL);
                    attendanceDao.rollbackTransaction();
                    return resBean;
                }
                // 2、根据申诉结果记录，将个人月报详情表 考勤描述更新为"已申诉"，同时将记录结果状态更新为申诉已通过
                result = updateDescInfo(appealInfo);
                if (result < 1) {
                    logger.info("dealAppeal update descInfo failed,appealId={}|monthRcdId={}|enterId={}|examineUid={}|examineResult={}|result={}",
                            reqBean.getAppealId(), appealInfo.getMonthRcdId(),
                            reqBean.getEnterId(), reqBean.getExamineUid(),
                            reqBean.getExamineResult(), result);
                    resBean.setCode(AtdcResultCode.ATDC107.APPEAL_EXAMINE_FAIL);
                    resBean.setSummary(AtdcResultSummary.ATDC107.APPEAL_EXAMINE_FAIL);
                    attendanceDao.rollbackTransaction();
                    return resBean;
                }
                // 3、更新团队日报报表和团队月报报表统计数据
                Map<String, Object> map = buildUpdateReportDataParam(appealInfo);
                // 更新团队日报统计数据
                result = updateTeamDailyReportData(map);
                if (result < 1)
                {
                    logger.info(
                            "dealAppeal updateTeamDailyReportData failed,appealId={}|monthRcdId={}|enterId={}|examineUid={}|examineResult={}|result={}",
                            reqBean.getAppealId(), appealInfo.getMonthRcdId(),
                            reqBean.getEnterId(), reqBean.getExamineUid(),
                            reqBean.getExamineResult(), result);
                    resBean.setCode(AtdcResultCode.ATDC107.APPEAL_EXAMINE_FAIL);
                    resBean.setSummary(AtdcResultSummary.ATDC107.APPEAL_EXAMINE_FAIL);
                    attendanceDao.rollbackTransaction();
                    return resBean;
                }
                if (map.get("late") != null){
                    map.put("lateMinutes",employeeMonthDetail.getLateMinutes());
                }
                if (map.get("early") != null){
                    map.put("earlyMinutes",employeeMonthDetail.getEarlyMinutes());
                }


                // 更新团队月报统计数据
                result = updateTeamMonthReportData(map);
                if (result < 1)
                {
                    logger
                        .info(
                            "dealAppeal updateTeamMonthReportData failed,appealId={}|monthRcdId={}|enterId={}|examineUid={}|examineResult={}|result={}",
                            reqBean.getAppealId(), appealInfo.getMonthRcdId(),
                            reqBean.getEnterId(), reqBean.getExamineUid(),
                            reqBean.getExamineResult(), result);
                    resBean.setCode(AtdcResultCode.ATDC107.APPEAL_EXAMINE_FAIL);
                    resBean.setSummary(AtdcResultSummary.ATDC107.APPEAL_EXAMINE_FAIL);
                    attendanceDao.rollbackTransaction();
                    return resBean;
                }
                AttendAppealReq attendAppealReq =  (AttendAppealReq) attendanceDao.queryForObject(
                        "attendance.querySingleAppealInfo", reqBean.getAppealId());
                //封装推送参数
                attendAppealReq.setEnterName(reqBean.getEnterName());
                Message message= initMessage(attendAppealReq,Constants.MsgType.authorizationPass.getValue());
                List<Message>list = new ArrayList<>();
                list.add(message);
                attendanceDao.batchInsert("attendance.batchSaveMessage",
                        list);

                attendanceDao.commitTransaction();
            }
            catch (PersistException e)
            {
                try
                {
                    attendanceDao.rollbackTransaction();
                }
                catch (PersistException e1)
                {}
                logger
                    .error(
                        "dealAppeal update DB failed,enterId={}|examineUid={}|phone={}",
                        reqBean.getEnterId(), reqBean.getExamineUid(), reqBean
                            .getUserInfo().getPhone(), e);
                resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
                resBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            }
        }
        else
        {
            // 审核拒绝
            try
            {
                attendanceDao.startTransaction();
                int result = updateAppealResult(reqBean);
                if (result < 1)
                {
                    logger
                        .info(
                            "dealAppeal updateTeamMonthReportData failed,appealId={}|monthRcdId={}|enterId={}|examineUid={}|examineResult={}|result={}",
                            reqBean.getAppealId(), appealInfo.getMonthRcdId(),
                            reqBean.getEnterId(), reqBean.getExamineUid(),
                            reqBean.getExamineResult(), result);
                    resBean.setCode(AtdcResultCode.ATDC107.APPEAL_EXAMINE_FAIL);
                    resBean
                        .setSummary(AtdcResultSummary.ATDC107.APPEAL_EXAMINE_FAIL);
                    attendanceDao.rollbackTransaction();
                    return resBean;
                }
                // 4、将个人月报详情中的记录数据审核结果进行更新
                Map<String, Object> mapParam = new HashMap<String, Object>();
                mapParam.put("recordState", 5);
                mapParam.put("monthRcdId", appealInfo.getMonthRcdId());
                result = updatePersonMonthExamineResult(mapParam);
                if (result < 1)
                {
                    logger
                        .info(
                            "dealAppeal updatePersonMonthExamineResult failed,appealId={}|monthRcdId={}|enterId={}|examineUid={}|examineResult={}|result={}",
                            reqBean.getAppealId(), appealInfo.getMonthRcdId(),
                            reqBean.getEnterId(), reqBean.getExamineUid(),
                            reqBean.getExamineResult(), result);
                    resBean.setCode(AtdcResultCode.ATDC107.APPEAL_EXAMINE_FAIL);
                    resBean.setSummary(AtdcResultSummary.ATDC107.APPEAL_EXAMINE_FAIL);
                    attendanceDao.rollbackTransaction();
                    return resBean;
                }

                //封装推送参数
                AttendAppealReq attendAppealReq =  (AttendAppealReq) attendanceDao.queryForObject(
                        "attendance.querySingleAppealInfo", reqBean.getAppealId());
                //封装推送参数
                attendAppealReq.setEnterName(reqBean.getEnterName());
                Message message= initMessage(attendAppealReq,Constants.MsgType.authorizationReject.getValue());
                List<Message>list = new ArrayList<>();
                list.add(message);
                attendanceDao.batchInsert("attendance.batchSaveMessage",
                        list);

            }
            catch (PersistException e)
            {
                try
                {
                    attendanceDao.rollbackTransaction();
                }
                catch (PersistException e1)
                {}
                logger
                    .error(
                        "dealAppeal update DB failed,enterId={}|examineUid={}|phone={}",
                        reqBean.getEnterId(), reqBean.getExamineUid(), reqBean
                            .getUserInfo().getPhone(), e);
                resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
                resBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            }
        }
        return resBean;
    }

    /**
     * 根据个人月报id获取月报
     * @param monthRcdId
     * @return
     */
    private EmployeeMonthDetail queryMonthByMonthRcdId(long monthRcdId) {
        try {
           return (EmployeeMonthDetail) attendanceDao.queryForObject("attendance.queryMonthByMonthRcdId",
                monthRcdId);
        } catch (PersistException e) {
            logger.error("queryMonthByMonthRcdId query DB error monthRcdId={}",monthRcdId);
          return null;
        }
    }

    /**
     * 审核后，更新个人月报详考勤审核结果
     * @param mapParam
     * @return
     * @throws PersistException
     */
    private int updatePersonMonthExamineResult(Map<String, Object> mapParam)
        throws PersistException
    {
        return attendanceDao.update(
            "attendance.updatePersonMonthExamineResult", mapParam);
    }

    /**
     * 更新团队月报统计数据
     * @param map
     * @return
     * @throws PersistException
     */
    private int updateTeamMonthReportData(Map<String, Object> map)
        throws PersistException
    {
        if (TimeUtil.convert2long(String.valueOf(map.get("attendanceMonth")),TimeUtil.BASE_DATE_FORMAT_YYYY_MM) >=
            TimeUtil.convert2long("2018-11",TimeUtil.BASE_DATE_FORMAT_YYYY_MM)) {
            logger.info("==========================新========================");
            return attendanceDao.update("attendance.updateTeamMonthReportData_2018", map);
        }else {
            logger.info("==========================旧========================");
            return attendanceDao.update("attendance.updateTeamMonthReportData", map);
        }
    }

    /**
     * 构建更新报表参数
     * @param info
     * @return
     */
    private Map<String, Object> buildUpdateReportDataParam(AttendAppealReq info)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        if (1 == info.getAppealRecord())
        {
            if ("未打卡".equals(info.getGoWorkDesc()))
            {
                map.put("notClock", 1);
                map.put("goNotClock", 1);
            }
            else if ("迟到".equals(info.getGoWorkDesc()))
            {
                map.put("late", 1);
            }
            else if ("正常".equals(info.getGoWorkDesc())
                && "未打卡".equals(info.getLeaveWorkDesc()))
            {
                map.put("notClock", 1);
                map.put("leaveNotClock", 1);
            }
            else if ("正常".equals(info.getGoWorkDesc())
                && "早退".equals(info.getLeaveWorkDesc()))
            {
                map.put("early", 1);
            }
        }
        else if (2 == info.getAppealRecord())
        {
            if ("未打卡".equals(info.getLeaveWorkDesc()))
            {
                map.put("notClock", 1);
                map.put("leaveNotClock", 1);
            }
            else if ("早退".equals(info.getLeaveWorkDesc()))
            {
                map.put("early", 1);
            }
            else if ("正常".equals(info.getLeaveWorkDesc())
                && "未打卡".equals(info.getGoWorkDesc()))
            {
                map.put("notClock", 1);
                map.put("goNotClock", 1);
            }
            else if ("正常".equals(info.getLeaveWorkDesc())
                && "迟到".equals(info.getGoWorkDesc()))
            {
                map.put("late", 1);
            }
        }
        else if (3 == info.getAppealRecord())
        {
            if ("未打卡".equals(info.getGoWorkDesc())
                && "未打卡".equals(info.getLeaveWorkDesc()))
            {
                map.put("notClock", 2);
                map.put("goNotClock", 1);
                map.put("leaveNotClock", 1);
            }
            else if ("早退".equals(info.getGoWorkDesc())
                && "早退".equals(info.getLeaveWorkDesc()))
            {
                map.put("early", 2);
            }
            else if ("迟到".equals(info.getGoWorkDesc())
                && "迟到".equals(info.getLeaveWorkDesc()))
            {
                map.put("late", 2);
            }
            else if ("未打卡".equals(info.getGoWorkDesc()) && "迟到".equals(info.getLeaveWorkDesc()))
            {
                map.put("notClock", 1);
                map.put("goNotClock", 1);
                map.put("late", 1);
            }
            else if ( ("未打卡".equals(info.getLeaveWorkDesc()) && "迟到".equals(info.getGoWorkDesc()))){
                map.put("notClock", 1);
                map.put("leaveNotClock", 1);
                map.put("late", 1);

            }
            else if ("未打卡".equals(info.getGoWorkDesc()) && "早退".equals(info.getLeaveWorkDesc()) )
            {
                map.put("notClock", 1);
                map.put("goNotClock", 1);
                map.put("early", 1);
            }
            else if ( ("未打卡".equals(info.getLeaveWorkDesc()) && "早退".equals(info.getGoWorkDesc())) )
            {
                map.put("notClock", 1);
                map.put("leaveNotClock", 1);
                map.put("early", 1);
            }
            else if (("迟到".equals(info.getGoWorkDesc())
                && "早退".equals(info.getLeaveWorkDesc()) || (("迟到".equals(info
                .getLeaveWorkDesc()) && "早退".equals(info.getGoWorkDesc())))))
            {
                map.put("late", 1);
                map.put("early", 1);
            }
            else if ("正常".equals(info.getGoWorkDesc())
                && "未打卡".equals(info.getLeaveWorkDesc()))
            {
                map.put("notClock", 1);
                map.put("leaveNotClock", 1);
            }
            else if ("正常".equals(info.getGoWorkDesc())
                && "早退".equals(info.getLeaveWorkDesc()))
            {
                map.put("early", 1);
            }
            else if ("正常".equals(info.getLeaveWorkDesc())
                && "未打卡".equals(info.getGoWorkDesc()))
            {
                map.put("notClock", 1);
                map.put("goNotClock", 1);
            }
            else if ("正常".equals(info.getLeaveWorkDesc())
                && "迟到".equals(info.getGoWorkDesc()))
            {
                map.put("late", 1);
            }
        }
        map.put("normalDays",1);
        map.put("appealDays",1);
        map.put("monthRcdId", info.getMonthRcdId());

        map.put("attendanceId", info.getAttendanceId());
        map.put("uid", info.getUid());
        String attendanceDate = TimeUtil.date2String(info.getAttendanceDate(),
            TimeUtil.BASE_DATE_FORMAT);
        String attendanceMonth = attendanceDate.substring(0,
            attendanceDate.lastIndexOf("-"));
        map.put("attendanceDate", attendanceDate);
        map.put("attendanceMonth", attendanceMonth);
        return map;
    }

    /**
     * 更新团队日报统计数据
     * @param map
     * @return
     * @throws PersistException
     */
    private int updateTeamDailyReportData(Map<String, Object> map)
        throws PersistException
    {
        //使用时间区分新旧数据兼容
        if (TimeUtil.convert2long(String.valueOf(map.get("attendanceDate")),TimeUtil.BASE_DATE_FORMAT)
            >= TimeUtil.convert2long("2018-11-29",TimeUtil.BASE_DATE_FORMAT)
            ) {
            return attendanceDao.update("attendance.updateTeamDailyReportData_2018", map);
        }else {
            return attendanceDao.update("attendance.updateTeamDailyReportData", map);
        }
    }

    /**
     * 审核同意，更新个人月报详情考勤描述信息为已申诉
     * @param appealInfo
     * @return
     * @throws PersistException
     */
    private int updateDescInfo(AttendAppealReq appealInfo)
        throws PersistException
    {
        return attendanceDao.update("attendance.updateDescInfo", appealInfo);
    }

    /**
     * 更新考勤异常申诉单审核结果
     * @param reqBean
     * @return
     * @throws PersistException
     */
    private int updateAppealResult(AttendAppealReq reqBean)
        throws PersistException
    {
        return attendanceDao.update("attendance.updateAppealResult", reqBean);
    }

    /**
     * 查询是否还有审批员未审核的单
     * @param examineUid
     * @return
     * @throws PersistException
     */
    public long queryNotExamineAppeal(String examineUid, String enterId, long attendanceId)
        throws PersistException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("examineUid", examineUid);
        map.put("enterId", enterId);
        map.put("attendanceId", attendanceId);
        return (Long) attendanceDao.queryForObject(
            "attendance.queryNotExamineAppeal", map);
    }

    /**
     * 通过uid查询审批员信息，借此判断是否为审批员
     * @param
     * @return
     * @throws PersistException
     */
    @SuppressWarnings("unchecked")
    public List<AttendExamineEntity> queryExaminerByUid(String examineUid)
        throws PersistException
    {
        return (List<AttendExamineEntity>) attendanceDao.queryForList(
            "attendance.queryExaminerByUid", examineUid);
    }

    /**
     * 通过企业id获取企业审批限制
     * @param enterId
     * @return
     */
    public AttendApprovalRestrict queryApprovalRestrictByEnterId(String enterId) throws Exception{
        try {
            return (AttendApprovalRestrict)attendanceDao.queryForObject("attendance.queryApprovalRestrictByEnterId",enterId);
        }catch (Exception e) {
            logger.error("queryAttendWhitelist failed,enterId={}",enterId,e);
            throw new Exception(e);
        }
    }

    /**
     * 查询审批数
     * @param uid
     * @param attendanceMonth
     * @param enterId
     * @return
     */
    public int queryApprovalCount(String uid, String attendanceMonth, String enterId) throws Exception{
        Map<String ,String> map = new HashMap<>();
        map.put("uid",uid);
        map.put("attendanceMonth",attendanceMonth);
        map.put("enterId",enterId);
        return (int)attendanceDao.queryForObject("attendance.queryApprovalCount",map);
    }

    /**
     * h5 设置审批限制
     * @param groupReq
     * @return
     */
    public AttendGroupRes setApprovalRestrict(AttendGroupReq groupReq) {
        AttendGroupRes attendGroupRes = new AttendGroupRes();
        try {
            attendanceDao.insert("attendance.insertOrUpdateApprovalRestrict",groupReq.getAttendApprovalRestrict());
        }catch (Exception e){
            logger.error("insertOrUpdateApprovalRestrict error attendApprovalRestrict={}|uid={}|errorMsg={}",groupReq.getAttendApprovalRestrict(),groupReq.getUserInfo().getUid(),e);
            attendGroupRes.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            attendGroupRes.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return attendGroupRes;
        }
        return attendGroupRes;
    }
}

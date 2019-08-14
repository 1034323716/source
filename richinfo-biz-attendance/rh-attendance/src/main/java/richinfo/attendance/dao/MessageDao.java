/**
 * 文件名：MessageDao.java
 * 创建日期： 2018年1月2日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月2日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.entity.Message;
import richinfo.attendance.util.TimeUtil;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.attendance.util.AssertUtil;

/**
 * 功能描述：待发消息持久层
 *
 */
public class MessageDao extends BaseAttendanceDao
{

    private Logger log = LoggerFactory.getLogger(MessageDao.class);

    /**
     * 批量入库待发消息
     * @param list
     * @return
     */
    public boolean batchSaveMessage(List<Message> list)
    {

        if (AssertUtil.isEmpty(list))
        {
            log.warn("batchSaveMessage failed,list is empty.");
            return false;
        }
        try
        {
            return attendanceDao.batchInsert("attendance.batchSaveMessage",
                list);

        }
        catch (PersistException e)
        {
            log.warn("batchSaveMessage failed,list is empty.");
            log.error("batchSaveMessage error.", e);
            return false;
        }
    }

    /**
     * 根据考勤组ID删除待发消息
     * @param attendanceId
     * @param msgType
     * @return
     */
    public boolean delMsgByAttendanceId(long attendanceId, int msgType)
    {
        try
        {
            Map<String, Object> reqMap = new HashMap<String, Object>();
            reqMap.put("attendanceId", attendanceId);
            reqMap.put("msgType", msgType);
            int result = attendanceDao.delete(
                "attendance.delMsgByAttendanceId", reqMap);
            log.info(
                "delMsgByAttendanceId success.attendanceId={}|msgType={}|result={}",
                attendanceId, msgType, result);

            return result > 0;
        }
        catch (PersistException e)
        {
            log.error("delMsgByAttendanceId error.attendanceId={}|msgType={}",
                attendanceId, msgType, e);
            return false;
        }
    }

    /**
     * 根据员工UID批量删除待发消息
     * @param
     * @param
     * @return
     */
    public boolean batchDelMsgByUids(List<AttendEmployee> list)
    {
        try
        {
            boolean flag = attendanceDao.batchDelete(
                "attendance.batchDelMsgByUids", list);
            log.info("batchDelMsgByUids success={}|uidSize={}", flag,
                list.size());
            return flag;
        }
        catch (PersistException e)
        {
            log.error("batchDelMsgByUids error", e);
            return false;
        }
    }

    /**
     * 从数据库查询需要发送的定时消息列表
     * @param skip
     * @param limit
     * @param currentServerNo
     * @param endDate
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Message> queryPageNeedSendMsgs(int skip, int limit,
        int currentServerNo, Date endDate)
    {
        try
        {
            // 考勤组状态为正常
            Map<String, Object> reqMap = new HashMap<String, Object>();
            reqMap.put("skip", skip);
            reqMap.put("limit", limit);
            reqMap.put("currentServerNo", currentServerNo);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowdayTime = dateFormat.format(endDate);
            reqMap.put("endDate", nowdayTime);
            return attendanceDao.queryForList(
                "attendance.queryPageNeedSendMsgs", reqMap);
        }
        catch (PersistException e)
        {
            log.error("queryPageNeedSendMsgs error.", e);
            return null;
        }
    }

    /**
     * 批量删除待发消息
     * @param
     * @return
     */
    public boolean batchDelMessage(List<Message> list)
    {
        if (AssertUtil.isEmpty(list))
        {
            log.warn("batchDelMessage failed,list is empty.");
            return false;
        }
        try
        {
            return attendanceDao
                .batchDelete("attendance.batchDelMessage", list);
        }
        catch (PersistException e)
        {
            log.warn("batchDelMessage failed,list is empty.");
            log.error("batchDelMessage error.", e);
            return false;
        }
    }

    /**
     * 插入一条消息
     * @param message
     */
    public int insertMessage(Message message) {

        int id = 0;

        if (AssertUtil.isEmpty(message))
        {
            log.warn("insertMessage failed,list is empty.");
            return id;
        }
        try
        {
            attendanceDao.startTransaction();
             id  = (Integer) attendanceDao.insert("attendance.insertMessage", message);
            attendanceDao.commitTransaction();
            return id;
        }
        catch (PersistException e)
        {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                e1.printStackTrace();
            }
            log.warn("batchSaveMessage failed,list is empty.");
            log.error("batchSaveMessage error.", e);
            return id;
        }
    }

    public int updateMessageBySSMSwitch(String uid, int SMSSwitch) {
        Map<String, Object> reqMap = new HashMap<String, Object>();
        try
        {
            reqMap.put("uid", uid);
            reqMap.put("status", SMSSwitch);
            reqMap.put("nowTime", TimeUtil.formatDateTime(new Date(), TimeUtil.BASE_DATETIME_FORMAT));
            return attendanceDao.update("attendance.updateMessageBySSMSwitch", reqMap);
        }
        catch (PersistException e)
        {
            log.error("updateMessageBySSMSwitch error, params : {}, exception : {}", reqMap, e);
            return 0;
        }
    }
}

/**
 * 文件名：AttendanceScheduleDao.java
 * 创建日期： 2018年4月4日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年4月4日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.bean.AttendScheduleReq;
import richinfo.attendance.bean.AttendScheduleShiftReq;
import richinfo.attendance.entity.AttendanceSchedule;
import richinfo.attendance.entity.AttendanceScheduleShift;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.entity.AttendanceScheduleShift.ShiftStatus;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.attendance.util.AssertUtil;

/**
 * 功能描述：考勤组排班 DAO层
 * 
 */
public class AttendanceScheduleDao extends BaseAttendanceDao
{
    /** table_name="attendance_schedule"; */

    private Logger logger = LoggerFactory
        .getLogger(AttendanceScheduleDao.class);

    /**
     * 查询排班
     * @param attendanceId
     * @param attendMonth
     * @param uid
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceSchedule> querySchedule(long attendanceId,
        String attendMonth, String uid)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        map.put("attendMonth", attendMonth);
        map.put("uid", uid);
        map.put("status", ShiftStatus.Normal.getValue());
        List<AttendanceSchedule> list = null;
        try
        {
            list = attendanceDao.queryForList("attendance.querySchedule", map);
        }
        catch (Exception e)
        {
            logger.error("querySchedule error attendanceId={}. {}",
                attendanceId, e);
        }

        return list;
    }

    /**
     * 更新排班信息
     * @param attendanceId
     * @param attendMonth
     * @param uid
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean updateAttendanceSchedule(
        AttendScheduleReq attendScheduleReq, List<String> deleteUidList,
        UserInfo user)
    {
        try
        {
            // 开启事务
            attendanceDao.startTransaction();
            Map<String, Object> params = new HashMap<String,Object>();
            params.put("afterStatus", AttendanceSchedule.Status.Deleted.getValue());
            params.put("beforeStatus", AttendanceSchedule.Status.Normal.getValue());
            params.put("attendanceId", attendScheduleReq.getAttendanceId());
            params.put("list", deleteUidList);
            if(AssertUtil.isNotEmpty(deleteUidList)){
                attendanceDao.update("attendance.updateAttendScheduleStatusByParam", params);
            }
            return attendanceDao.batchUpdateNoTransaction(
                "attendance.batchSaveAttendanceSchedule",
                attendScheduleReq.getAttendanceScheduleList());
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "updateAttendanceSchedule error updateAttendanceSchedule={}. {}",
                    attendScheduleReq, e);

            try
            {
                attendanceDao.rollbackTransaction();
            }
            catch (Exception e1)
            {
                logger.error(
                    "updateAttendanceSchedule rollback failed,reqParam={}",
                    attendScheduleReq, e1);
            }
            logger.error(
                "updateGroup to DB failed,phone={}|uid={}|reqParam={}",
                user.getPhone(), user.getUid(), attendScheduleReq, e);
        }
        finally
        {
            try
            {
                attendanceDao.commitTransaction();
            }
            catch (Exception e)
            {
                logger.error(
                    "updateGroup commitTransaction failed,reqParam={}",
                    attendScheduleReq, e);
            }
        }
        return false;
    }

    /**
     * 根据参数获取符合条件的UID
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> queryScheduleUidByParam(Map<String, Object> params)
    {
        try
        {
            return (List<String>) attendanceDao.queryForList(
                "attendance.queryScheduleUidByParam", params);
        }
        catch (PersistException e)
        {
            logger.error("queryScheduleUidByParam error params={}. {}", params,
                e);
        }
        return null;
    }

    /**
     * 查询考勤組排班班次列表，顺序排列
     * @param params 查询日期 应传递当前时间:new Date()，日期截取在sql侧进行处理
     * @return 考勤记录列表，顺序排列
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceScheduleShift> queryAttendanceScheduleShift(
        AttendScheduleShiftReq req)
    {
        try
        {
            List<AttendanceScheduleShift> list = attendanceDao.queryForList(
                "attendance.queryAttendanceScheduleShift", req);
            logger
                .info(
                    "queryAttendanceScheduleShift success from DB|pageNo={}|pageSize={}|attendanceId={}",
                    req.getPageNo(), req.getPageSize(), req.getAttendanceId());
            return list;
        }
        catch (Exception e)
        {
            logger
                .error(
                    "queryAttendanceScheduleShift error from DB|pageNo={}|pageSize={}|attendanceId={}|e={}",
                    req.getPageNo(), req.getPageSize(), req.getAttendanceId(),
                    e);
        }
        return null;
    }

    /**
     * 查询考勤組排班列表，顺序排列
     * @param params 查询日期 应传递当前时间:new Date()，日期截取在sql侧进行处理
     * @return 考勤记录列表，顺序排列
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceSchedule> queryAttendanceSchedule(
        AttendScheduleShiftReq req)
    {
        try
        {
            List<AttendanceSchedule> list = attendanceDao.queryForList(
                "attendance.queryAttendanceSchedule", req);
            logger
                .info(
                    "queryAttendanceSchedule success from DB|pageNo={}|pageSize={}|attendanceId={}",
                    req.getPageNo(), req.getPageSize(), req.getAttendanceId());
            return list;
        }
        catch (Exception e)
        {
            logger
                .error(
                    "queryAttendanceSchedule success from DB|pageNo={}|pageSize={}|attendanceId={}",
                    req.getPageNo(), req.getPageSize(), req.getAttendanceId());
        }
        return null;
    }

    /**
     * 查询考勤組排班班次列表总量
     * @param req Param
     * @return
     */
    public long queryAttendanceScheduleShiftCount(AttendScheduleShiftReq req,
        UserInfo userInfo)
    {
        Map<String, Object> params = new HashMap<String, Object>(4);
        try
        {
            params.put("attendanceId", req.getAttendanceId());
            params.put("status",
                AttendanceScheduleShift.ShiftStatus.Normal.getValue());
            return (Long) attendanceDao.queryForObject(
                "attendance.queryAttendanceScheduleShiftCount", params);
        }
        catch (Exception e)
        {
            logger.error(
                "queryAttendanceScheduleShiftCount failed,phone={}|uid={}|{}",
                userInfo.getPhone(), userInfo.getUid(), req.toString(), e);
        }
        return 0;
    }

    /**
     * 查询考勤組排班列表总量
     * @param reqParam
     * @return
     */
    public long queryAttendanceScheduleCount(AttendScheduleShiftReq req,
        UserInfo userInfo)
    {
        Map<String, Object> params = new HashMap<String, Object>(4);
        try
        {
            params.put("attendanceId", req.getAttendanceId());
            params.put("attendMonth", req.getAttendMonth());
            params.put("status",
                AttendanceScheduleShift.ShiftStatus.Normal.getValue());
            return (Long) attendanceDao.queryForObject(
                "attendance.queryAttendanceScheduleCount", params);
        }
        catch (Exception e)
        {
            logger.error(
                "queryAttendanceScheduleCount failed,phone={}|uid={}|{}",
                userInfo.getPhone(), userInfo.getUid(), req.toString(), e);
        }
        return 0;
    }
}

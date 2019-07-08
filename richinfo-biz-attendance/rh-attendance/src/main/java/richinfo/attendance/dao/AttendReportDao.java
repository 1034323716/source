/**
 * 文件名：AttendReportDao.java
 * 创建日期： 2017年6月8日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AttendReportReq;
import richinfo.attendance.entity.AttendGroup.GroupStatus;
import richinfo.attendance.entity.*;
import richinfo.attendance.util.AssertUtil;
import richinfo.dbcomponent.exception.PersistException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：考勤报表类Dao层实现
 * 
 */
public class AttendReportDao extends BaseAttendanceDao
{
    private Logger logger = LoggerFactory.getLogger(AttendReportDao.class);

    /**
     * 查询团队日报详情
     * @param reqParam
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamDailyEntity> queryTeamDailyInfo(TeamDailyEntity reqParam,
        UserInfo userInfo)
    {
        try
        {
            List<TeamDailyEntity> list = attendanceDao.queryForList(
                "attendance.queryTeamDailyInfo", reqParam);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger
                .info(
                    "queryTeamDailyInfo success,phone={}|uid={}|count={}|enterId={}|pageNo={}|pageSize={}",
                    userInfo.getPhone(), userInfo.getUid(), count,
                    reqParam.getEnterId(), reqParam.getPageNo(),
                    reqParam.getPageSize());
            return list;
        }
        catch (Exception e)
        {
            logger
                .error(
                    "queryTeamDailyInfo failed,phone={}|uid={}|enterId={}|queryDate={}|pageNo={}|pageSize={}",
                    userInfo.getPhone(), userInfo.getUid(),
                    reqParam.getEnterId(), reqParam.getAttendanceDate(),
                    reqParam.getPageNo(), reqParam.getPageSize(), e);
        }
        return null;
    }

    /**
     * 查询所有企业Id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> queryAllEnterId()
    {
        try
        {
            return attendanceDao.queryForList("attendance.queryAllEnterId",
                GroupStatus.Normal.getValue());
        }
        catch (Exception e)
        {
            logger.error("queryAllEnterId query from DB failed", e);
        }
        return null;
    }

    /**
     * 团队月报统计
     * @param attendanceDate
     * @param enterId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamMonthReportEntity> teamMonthReportInfo(String startDate,
        String attendanceDate, String enterId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startDate", startDate);
        map.put("attendanceDate", attendanceDate);
        map.put("enterId", enterId);
        try
        {
            return attendanceDao.queryForList("attendance.teamMonthReportInfo",
                map);
        }
        catch (Exception e)
        {
            logger
                .error(
                    "teamMonthReportInfo query from DB failed,enterId={}|attendanceDate={}",
                    enterId, attendanceDate, e);
        }
        return null;
    }

    /**
     * 团队月报统计信息入库
     * @param list
     */
    public void saveTeamMonthReport(List<TeamMonthReportEntity> list)
    {
        try
        {
            attendanceDao.batchInsert("attendance.saveTeamMonthReport", list);
            logger.info("saveTeamMonthReport insert into DB success,count={}",
                list.size());
        }
        catch (Exception e)
        {
            logger.error("saveTeamMonthReport insert into DB failed", e);
        }
    }

    /**
     * 团队日报统计
     * @param attendanceDate
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamDailyReportEntity> teamDailyReport(String attendanceDate,
        String enterId)
    {
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("attendanceDate", attendanceDate);
            map.put("enterId", enterId);
            return attendanceDao.queryForList("attendance.teamDailyReport", map);
        }
        catch (Exception e)
        {
            logger.error("teamDailyReport query DB failed,attendanceDate={}|enterId={}",
                attendanceDate, enterId, e);
        }
        return null;
    }

    /**
     * 入库团队日报统计数据
     * @param list
     * @return
     */
    public boolean saveTeamDailyInfo(List<TeamDailyReportEntity> list)
    {
        try
        {
            attendanceDao.batchInsert("attendance.saveTeamDailyInfo", list);
            logger.info("saveTeamDailyInfo insert into DB success,count={}",
                list.size());
            return true;
        }
        catch (Exception e)
        {
            logger.info("saveTeamDailyInfo insert into DB failed", e);
        }
        return false;
    }

    /**
     * 查询团队日报详情消息总量
     * @param reqParam
     * @return
     */
    public long queryTeamDailyCount(TeamDailyEntity reqParam, UserInfo userInfo)
    {
        try
        {
            return (Long) attendanceDao.queryForObject(
                "attendance.queryTeamDailyCount", reqParam);
        }
        catch (Exception e)
        {
            logger
                .error(
                    "queryTeamDailyCount failed,phone={}|uid={}|enterId={}|queryDate={}",
                    userInfo.getPhone(), userInfo.getUid(),
                    reqParam.getEnterId(), reqParam.getAttendanceDate(), e);
        }
        return 0;
    }

    /**
     * 查询团队月报统计 分页查询
     * @param reqParam
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamMonthEntity> queryTeamMonthReport(AttendReportReq reqParam)
        throws PersistException
    {
        int currentPage = reqParam.getPageNo();
        /** 每次取几条 */
        int pageSize = reqParam.getPageSize();
        /** 计算从第几条记录开始 */
        int begin = currentPage > 1 ? (currentPage - 1) * pageSize : 0;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enterId", reqParam.getEnterId());
        map.put("attendanceMonth", reqParam.getAttendanceMonth());
        map.put("begin", begin);
        map.put("pageSize", pageSize);

        List<TeamMonthEntity> list = attendanceDao.queryForList(
            "attendance.queryTeamMonthReport", map);
        long count = AssertUtil.isEmpty(list) ? 0 : list.size();
        logger
            .info(
                "queryTeamMonthReport success,cout={}|enterId={}|pageNo={}|pageSize={}",
                count, reqParam.getEnterId(), reqParam.getPageNo(),
                reqParam.getPageSize());
        return list;

    }

    /**
     * 查询团队月报统计 查询所有
     * @param reqParam
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamMonthEntity> queryAllTeamMonthReport(
        AttendReportReq reqParam) throws PersistException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enterId", reqParam.getEnterId());
        map.put("attendanceMonth", reqParam.getAttendanceMonth());

        List<TeamMonthEntity> list = attendanceDao.queryForList(
            "attendance.queryTeamMonthReport", map);
        long count = AssertUtil.isEmpty(list) ? 0 : list.size();
        logger.info("queryAllTeamMonthReport success,cout={}|enterId={}",
            count, reqParam.getEnterId());
        return list;

    }

    /**
     * 查询团队日报详情消息总量
     * @param reqParam
     * @return
     */
    public int queryTeamMonthCount(AttendReportReq reqParam)
    {
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("enterId", reqParam.getEnterId());
            map.put("attendanceMonth", reqParam.getAttendanceMonth());
            return (Integer) attendanceDao.queryForObject(
                "attendance.queryTeamMonthCount", map);
        }
        catch (Exception e)
        {
            logger.error("queryTeamMonthCount failed,enterId={}|queryDate={}",
                reqParam.getEnterId(), reqParam.getAttendanceMonth(), e);
        }
        return 0;
    }

    /**
     * 查询员工月报明细列表，查询结果时间顺序排列，日期匹配由mysql的日期函数实现
     * @param uid
     * @param attendanceMonth 格式：yyyy-MM,eg:2017-05
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryPersonalMonthlyReport(String uid,
        String attendanceMonth, String enterId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("attendanceMonth", attendanceMonth);
        map.put("enterId", enterId);
        try
        {
            return attendanceDao.queryForList(
                "attendance.queryPersonalMonthlyReport", map);
        }
        catch (PersistException e)
        {
            logger.error(
                "queryPersonalMonthlyReport error.uid={}|month={}|enterId={}",
                uid, attendanceMonth, enterId, e);
            return null;
        }
    }

    /**
     * 查询团队日报统计，返回企业各个考勤组的统计结果。查询失败，返回null
     * @param enterId 企业ID
     * @param attendanceDate 查询日期，格式：yyyy-MM-dd。eg:2017-06-10
     * @return
     */
    public TeamDailyReportEntity sumTeamDailyReport(String enterId, String attendanceDate) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enterId", enterId);
        map.put("attendanceDate", attendanceDate);
        try
        {
            return (TeamDailyReportEntity) attendanceDao.queryForObject(
                "attendance.sumTeamDailyReport", map);
        }
        catch (PersistException e)
        {
            logger.error("sumTeamDailyReport error.enterId={}|date={}",
                enterId, attendanceDate, e);
            return null;
        }
    }

    /**
     * 批量入库员工个人月报统计列表
     * @param employeeMonthDetailList
     * @return
     */
    public boolean batchSaveEmployeeMonthDetail(
        List<EmployeeMonthDetail> employeeMonthDetailList)
    {
        if (AssertUtil.isEmpty(employeeMonthDetailList)) {
            logger.warn("batchSaveEmployeeMonthDetail failed,list is empty.");
            return false;
        }
        try {
            return attendanceDao.batchInsert(
                "attendance.batchSaveEmployeeMonthDetail",
                employeeMonthDetailList);
        } catch (PersistException e) {
            logger.error("batchSaveEmployeeMonthDetail error.", e);
            return false;
        }
    }

    /**
     * PC端查询团队月报统计数据
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamMonthEntity> queryTeamMonthPc(AttendReportReq req)
    {
        List<TeamMonthEntity> list = null;
        try
        {
            list = attendanceDao.queryForList("attendance.queryTeamMonthPc",
                req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger.info(
                "queryTeamMonthPc success,phone={}|uid={}|count={}|enterId={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                count, req.getUserInfo().getEnterId());
            return list;
        }
        catch (PersistException e)
        {
            logger.error("queryTeamMonthPc failed,reqParam={}|phone={}|uid={}",
                req, req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                e);
        }
        return null;
    }

    /**
     * PC端查询员工月报明细数据总量
     * @param req
     * @return
     */
    public int queryEmpMonthPcCount(AttendReportReq req)
    {
        try
        {
            int count = (int) attendanceDao.queryForObject(
                "attendance.queryEmpMonthPcCount", req);
            logger
                .info(
                    "queryEmpMonthPcCount success,reqParam={}|phone={}|uid={}|count={}",
                    req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                    count);
            return count;
        }
        catch (PersistException e)
        {
            logger.error(
                "queryEmpMonthPcCount failed,reqParam={}|phone={}|uid={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return 0;
    }

    /**
     * 查询PC端员工月报明细
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryEmpMonthPc(AttendReportReq req)
    {
        try
        {
            List<EmployeeMonthDetail> list = attendanceDao.queryForList(
                "attendance.queryEmpMonthPc", req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger.info(
                "queryEmpMonthPc success,phone={}|uid={}|count={}|enterId={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                count, req.getUserInfo().getEnterId());
            return list;
        }
        catch (Exception e)
        {
            logger.error(
                "queryEmpMonthPc failed,phone={}|uid={}|enterId={}|req={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), req
                    .getUserInfo().getEnterId(), req, e);
        }
        return null;
    }

    /**
     * PC端查询团队月报统计数据总量
     * @param req
     * @return
     */
    public int queryTeamMonthPcCount(AttendReportReq req)
    {
        try
        {
            int count = (int) attendanceDao.queryForObject(
                "attendance.queryTeamMonthPcCount", req);
            logger
                .info(
                    "queryTeamMonthPcCount success,reqParam={}|phone={}|uid={}|count={}",
                    req, req.getUserInfo().getPhone(), req.getUserInfo()
                        .getUid(), count);
            return count;
        }
        catch (PersistException e)
        {
            logger.error(
                "queryTeamMonthPcCount failed,reqParam={}|phone={}|uid={}",
                req, req.getUserInfo().getPhone(), req.getUserInfo().getUid(),
                e);
        }
        return 0;
    }

    /**
     * 查询PC端导出团队月报统计数据
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamMonthEntity> queryExportTeamMonthPc(AttendReportReq req)
    {
        List<TeamMonthEntity> list = null;
        try
        {
            list = attendanceDao.queryForList(
                "attendance.queryExportTeamMonthPc", req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger.info("queryExportTeamMonthPc success,count={}|enterId={}",
                count, req.getEnterId());
        }
        catch (PersistException e)
        {
            logger.error(
                "queryExportTeamMonthPc failed,phone={}|uid={}|req={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), req,e);
        }
        return list;
    }

    /**
     * 查询PC端导出员工月报明细数据
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryExportEmpMonthPc(AttendReportReq req)
    {
        List<EmployeeMonthDetail> list = null;
        try
        {
            list = attendanceDao.queryForList(
                "attendance.queryExportEmpMonthPc", req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger.info("queryExportEmpMonthPc success,count={}|enterId={}",
                count, req.getEnterId());
        }
        catch (PersistException e)
        {
            logger.error("queryExportEmpMonthPc failed,phone={}|uid={}|req={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(), req,e);
        }
        return list;
    }

    /**
     * 查询某企业某天迟到分项总数据量
     * @param req
     * @return
     */
    public int queryLateItemCount(AttendReportReq req)
    {
        try
        {
            return (int) attendanceDao.queryForObject(
                "attendance.queryLateItemCount", req);
        }
        catch (PersistException e)
        {
            logger.error(
                "queryLateItemCount failed,reqParam={}|phone={}|uid={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return 0;
    }

    /**
     * 查询某企业某天迟到分项数据
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryLateItem(AttendReportReq req)
    {
        List<EmployeeMonthDetail> list = null;
        try
        {
            list = attendanceDao.queryForList("attendance.queryLateItem", req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger.info(
                "queryLateItem success,count={}|enterId={}|attendanceDate={}",
                count, req.getEnterId(), req.getAttendanceDate());
        }
        catch (PersistException e)
        {
            logger.error("queryLateItem failed,reqParam={}|phone={}|uid={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return list;
    }

    /**
     * 查询某企业某天早退分项总数据量
     * @param req
     * @return
     */
    public int queryEarlyItemCount(AttendReportReq req)
    {
        try
        {
            return (int) attendanceDao.queryForObject(
                "attendance.queryEarlyItemCount", req);
        }
        catch (PersistException e)
        {
            logger.error(
                "queryEarlyItemCount failed,reqParam={}|phone={}|uid={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return 0;
    }

    /**
     * 查询某企业某天早退分项数据
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryEarlyItem(AttendReportReq req)
    {
        List<EmployeeMonthDetail> list = null;
        try
        {
            list = attendanceDao.queryForList("attendance.queryEarlyItem", req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger.info(
                "queryEarlyItem success,count={}|enterId={}|attendanceDate={}",
                count, req.getEnterId(), req.getAttendanceDate());
        }
        catch (PersistException e)
        {
            logger.error("queryEarlyItem failed,reqParam={}|phone={}|uid={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return list;
    }

    /**
     * 查询某企业某未打卡分项总数据量
     * @param req
     * @return
     */
    public int queryNotClockedItemCount(AttendReportReq req)
    {
        try
        {
            return (int) attendanceDao.queryForObject(
                "attendance.queryNotClockedItemCount", req);
        }
        catch (PersistException e)
        {
            logger.error(
                "queryNotClockedItemCount failed,reqParam={}|phone={}|uid={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return 0;
    }

    /**
     * 查询某企业某天未打卡分项数据
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryNotClockedItem(AttendReportReq req)
    {
        List<EmployeeMonthDetail> list = null;
        try
        {
            list = attendanceDao.queryForList("attendance.queryNotClockedItem",
                req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger
                .info(
                    "queryNotClockedItem success,count={}|enterId={}|attendanceDate={}",
                    count, req.getEnterId(), req.getAttendanceDate());
        }
        catch (PersistException e)
        {
            logger.error(
                "queryNotClockedItem failed,reqParam={}|phone={}|uid={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return list;
    }

    /**
     * 查询团队日报个人月列表考勤数据统计
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamMonthEntity> queryEmpMonthList(AttendReportReq req)
    {

        try
        {
            return (List<TeamMonthEntity>) attendanceDao.queryForList(
                "attendance.queryEmpMonthList", req);
        }
        catch (PersistException e)
        {
            logger.error(
                "queryEmpMonthList failed,reqParam={}|phone={}|uid={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return null;
    }

    /**
     * 查询团队日报分项个人迟到详细信息
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryLateDetailInfo(AttendReportReq req)
    {
        List<EmployeeMonthDetail> list = null;
        try
        {
            list = attendanceDao.queryForList("attendance.queryLateDetailInfo",
                req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger
                .info(
                    "queryLateDetailInfo success,count={}|enterId={}|attendanceDate={}",
                    count, req.getEnterId(), req.getAttendanceDate());
        }
        catch (PersistException e)
        {
            logger.error(
                "queryLateDetailInfo failed,reqParam={}|phone={}|uid={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return list;
    }

    /**
     * 查询团队日报分项个人早退详细信息
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryEarlyDetailInfo(AttendReportReq req)
    {
        List<EmployeeMonthDetail> list = null;
        try
        {
            list = attendanceDao.queryForList(
                "attendance.queryEarlyDetailInfo", req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger
                .info(
                    "queryEarlyDetailInfo success,count={}|enterId={}|attendanceDate={}",
                    count, req.getEnterId(), req.getAttendanceDate());
        }
        catch (PersistException e)
        {
            logger.error(
                "queryEarlyDetailInfo failed,reqParam={}|phone={}|uid={}", req
                    .getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return list;
    }

    /**
     * 查询团队日报分项个人未打卡详细信息
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryNotClockedDetailInfo(
        AttendReportReq req)
    {
        List<EmployeeMonthDetail> list = null;
        try
        {
            list = attendanceDao.queryForList(
                "attendance.queryNotClockedDetailInfo", req);
            long count = AssertUtil.isEmpty(list) ? 0 : list.size();
            logger
                .info(
                    "queryNotClockedDetailInfo success,count={}|enterId={}|attendanceMonth={}",
                    count, req.getEnterId(), req.getAttendanceMonth());
        }
        catch (PersistException e)
        {
            logger.error(
                "queryNotClockedDetailInfo failed,reqParam={}|phone={}|uid={}",
                req.getUserInfo().getPhone(), req.getUserInfo().getUid(), e);
        }
        return list;
    }

    /**
     * 查询导出PC端员工月报明细数据总量
     * @param req
     * @return
     */
    public int queryExportEmpMonthPcCount(AttendReportReq req)
    {
        try
        {
            int totalCount = (int) attendanceDao.queryForObject(
                "attendance.queryExportEmpMonthPcCount", req);
            logger.info(
                "queryExportEmpMonthPcCount success,totalCount={}|req={}",
                totalCount, req);
            return totalCount;
        }
        catch (PersistException e)
        {
            logger.error("queryExportEmpMonthPcCount success,req={}", req, e);
        }
        return 0;
    }

    /**
     * 查询导出PC端团队月报统计数据总量
     * @param req
     * @return
     */
    public int queryExportTeamMonthPcCount(AttendReportReq req)
    {
        try
        {
            int totalCount = (int) attendanceDao.queryForObject(
                "attendance.queryExportTeamMonthPcCount", req);
            logger.info(
                "queryExportTeamMonthPcCount success,totalCount={}|req={}",
                totalCount, req);
            return totalCount;
        }
        catch (PersistException e)
        {
            logger.error("queryExportTeamMonthPcCount success,req={}", req, e);
        }
        return 0;
    }

    /**
     * 获取个人月报的一天
     * @param uid
     * @param
     * @param
     * @return
     */
    public EmployeeMonthDetail queryNomMonthly(String uid, String attendanceDate) {
        EmployeeMonthDetail employeeMonthDetail = new EmployeeMonthDetail();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("attendanceDate", attendanceDate);

        try {
            employeeMonthDetail = (EmployeeMonthDetail)attendanceDao.queryForObject("attendance.queryNomMonthly",map);
            return employeeMonthDetail;
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("queryExportTeamMonthPcCount error,req={}", map, e);
        }
        return employeeMonthDetail;

    }

    /**
     * h5查询个人月报统计
     * @param uid
     * @param attendanceMonth 格式：yyyy-MM,eg:2017-05
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetailVO> queryEmpMonthStatistics(String uid,String attendanceMonth,String enterId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("attendanceMonth", attendanceMonth);
        map.put("enterId", enterId);
        try
        {
            return attendanceDao.queryForList(
                "attendance.queryEmpMonthStatistics", map);
        }
        catch (PersistException e)
        {
            logger.error(
                "queryEmpMonthStatistics error.uid={}|month={}|enterId={}",
                uid, attendanceMonth, enterId, e);
            return null;
        }
    }

    /**
     * 查h5查询个人月报明细
     * @param uid
     * @param attendanceMonth 格式：yyyy-MM,eg:2017-05
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetailVO> queryEmpMonthDetail(String uid,String attendanceMonth,String enterId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("attendanceMonth", attendanceMonth);
        map.put("enterId", enterId);
        try
        {
            return attendanceDao.queryForList(
                "attendance.queryEmpMonthDetail", map);
        }
        catch (PersistException e)
        {
            logger.error(
                "queryEmpMonthDetail error.uid={}|month={}|enterId={}",
                uid, attendanceMonth, enterId, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetailVO> queryEmpMonthDetailList() {
        try
        {
            return attendanceDao.queryForList(
                "attendance.queryEmpMonthDetailList","");
        }
        catch (PersistException e)
        {
            return null;
        }
    }

    /**
     * 查询原始数据满足条数
     * @param reportReq
     * @return
     */
    public int queryOriginalClockDataPcCount(AttendReportReq reportReq) {
        try
        {
            int totalCount = (int) attendanceDao.queryForObject(
                "attendance.queryOriginalClockDataPcCount", reportReq);
            logger.info(
                "queryOriginalClockDataPcCount success,totalCount={}|req={}",
                totalCount, reportReq);
            return totalCount;
        }
        catch (PersistException e)
        {
            logger.error("queryOriginalClockDataPcCount success,req={}", reportReq, e);
        }
        return 0;
    }

    /**
     *查询原始数据
     * @param reportReq
     * @return
     */
    public List<AttendEntity> queryOriginalClockDataPc(AttendReportReq reportReq) {
        try
        {
            logger.info(
                "queryOriginalClockDataPc req={}",
                reportReq);
            return attendanceDao.queryForList(
                "attendance.queryOriginalClockDataPc", reportReq);

        }
        catch (PersistException e)
        {
            logger.error("queryOriginalClockDataPc error,req={}", reportReq, e);
        }
        return null;
    }

    /**
     *查询导出原始数据
     * @param reportReq
     * @return
     */
    public List<AttendEntity> exportOriginalClockDataPc(AttendReportReq reportReq) {
        try
        {
            logger.info(
                "exportOriginalClockDataPc req={}",
                reportReq);
            return attendanceDao.queryForList(
                "attendance.exportOriginalClockDataPc", reportReq);

        }
        catch (PersistException e)
        {
            logger.error("exportOriginalClockDataPc error,req={}", reportReq, e);
        }
        return null;
    }


    /**
     * h5查询团队月报统计
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamMonthEntity> queryTeamMonthStatistics(AttendReportReq reqParam) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceMonth", reqParam.getAttendanceMonth());
        map.put("enterId", reqParam.getEnterId());

        try {
            return attendanceDao.queryForList("attendance.queryTeamMonthStatistics", map);
        } catch (PersistException e) {
            logger.error("queryTeamMonthStatistics error.month={}|enterId={}",
                reqParam.getAttendanceMonth(), reqParam.getEnterId(), e);
            return null;
        }
    }

    /**
     * h5查询团队月报分项详情
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetailVO> queryTeamMonthDetails(String attendanceMonth,String enterId,int item) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("attendanceMonth", attendanceMonth);
        paramMap.put("enterId", enterId);
        switch (item) {
            case 1 :
                paramMap.put("late", item);
                break;
            case 2 :
                paramMap.put("early", item);
                break;
            case 4 :
                paramMap.put("goWorkNoClock",item);
                break;
            case 5 :
                paramMap.put("leaveWorkNoClock",item);
                break;
            case 6 :
                paramMap.put("regionStatus", item);
                break;
            default:
                paramMap.put("goWorkNoClock",item);
        }
        try
        {
            return attendanceDao.queryForList("attendance.queryTeamMonthDetails", paramMap);
        }
        catch (PersistException e)
        {
            logger.error("queryTeamMonthDetails error",e);
            return null;
        }
    }

    /**
     * h5查询团队日报统计
     * @return
     */
    @SuppressWarnings("unchecked")
    public TeamDailyReportEntity queryTeamDailyStatistics(String enterId,String attendanceDate) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enterId", enterId);
        map.put("attendanceDate", attendanceDate);
        try
        {
            return (TeamDailyReportEntity) attendanceDao.queryForObject(
                "attendance.queryTeamDailyStatistics", map);
        }
        catch (PersistException e)
        {
            logger.error("queryTeamDailyStatistics error.enterId={}|date={}",
                enterId, attendanceDate, e);
            return null;
        }
    }

    /**
     * h5查询团队日报打卡明细
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamDailyEntity> queryTeamDailyDetail(String attendanceDate,String enterId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceDate", attendanceDate);
        map.put("enterId", enterId);
        try {
            return attendanceDao.queryForList(
                "attendance.queryTeamDailyDetail", map);
        } catch (PersistException e) {
            logger.error(
                "queryTeamMonthStatistics error.month={}|enterId={}",
                attendanceDate, enterId, e);
            return null;
        }
    }

    /**
     * h5查询团队日报分项详情
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EmployeeMonthDetail> queryTeamDailyItemDetails(String attendanceDate,String enterId,int item) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("attendanceDate", attendanceDate);
        paramMap.put("enterId", enterId);
        switch (item) {
            case 1 :
                paramMap.put("late", item);
                break;
            case 2 :
                paramMap.put("early", item);
                break;
            case 4 :
                paramMap.put("goWorkNoClock",item);
                break;
            case 5 :
                paramMap.put("leaveWorkNoClock",item);
                break;
            case 6 :
                paramMap.put("regionStatus", item);
                break;
//            case 7 :
//                paramMap.put("regionStatus", item);
//                break;
                default:
                    paramMap.put("goWorkNoClock",item);
        }
        try {
            return attendanceDao.queryForList(
                "attendance.queryTeamDailyItemDetails", paramMap);
        } catch (PersistException e) {
            logger.error("queryTeamDailyItemDetails error",e);
            return null;
        }
    }

    /**
     * h5发送月报统计
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TeamMonthEntity> sendTeamMonthStatistics(AttendReportReq reqParam) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceMonth", reqParam.getAttendanceMonth());
        map.put("enterId", reqParam.getEnterId());

        try {
            return attendanceDao.queryForList("attendance.sendTeamMonthStatistics", map);
        } catch (PersistException e) {
            logger.error("sendTeamMonthStatistics error.month={}|enterId={}",
                reqParam.getAttendanceMonth(), reqParam.getEnterId(), e);
            return null;
        }
    }

    /**
     * 批量插入手机号
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean batchInsertPhone(List insertList) {
        try {
            return attendanceDao.batchUpdateNoTransaction(
                "attendance.batchInsertPhone", insertList);
        } catch (PersistException e) {
            logger.error(
                "queryTeamMonthStatistics error.month={}|enterId={}", e);
            return false;
        }
    }

}

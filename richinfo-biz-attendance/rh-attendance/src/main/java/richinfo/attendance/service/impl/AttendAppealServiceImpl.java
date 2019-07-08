/**
 * 文件名：AttendAppealServiceImpl.java
 * 创建日期： 2017年10月13日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月13日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AbnormalAppealRes;
import richinfo.attendance.bean.AttendAppealReq;
import richinfo.attendance.bean.AttendAppealRes;
import richinfo.attendance.bean.AttendExamineRes;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.common.ResBean;
import richinfo.attendance.dao.AttendAppealDao;
import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.dao.AttendGroupDao;
import richinfo.attendance.entity.*;
import richinfo.attendance.service.AttendAppealService;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.TimeUtil;
import richinfo.dbcomponent.exception.PersistException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：考勤异常申诉接口实现类
 *
 */
public class AttendAppealServiceImpl implements AttendAppealService
{
    private Logger logger = LoggerFactory
        .getLogger(AttendAppealServiceImpl.class);

    private AttendAppealDao appealDao = new AttendAppealDao();
    private AttendGroupDao attendGroupDao = new AttendGroupDao();

    private static final String NORMAL_INFO = "正常";

    /** 申诉上午 */
    private static final int APPEAL_MORNING = 1;
    /** 申诉下午 */
    private static final int APPEAL_AFTERNOON = 2;
    /** 申诉上、下午 */
    private static final int APPEAL_BOTH = 3;

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();

    @Override
    public AttendAppealRes querySingleAppealInfo(AttendAppealReq reqBean)
    {
        AttendAppealRes resBean = new AttendAppealRes();
        // 参数校验
        checkQuerySingleAppealInfoParam(reqBean, resBean);
        if (!resBean.isSuccess())
        {
            logger
                .info(
                    "querySingleAppealInfo checkParam failed,phone={}|enterId={}|uid={}|code={}|summary={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getUserInfo()
                        .getEnterId(), reqBean.getUserInfo().getUid(), resBean
                        .getCode(), resBean.getSummary());
            return resBean;
        }
        logger
            .info(
                "querySingleAppealInfo checkParam success,phone={}|enterId={}|uid={}",
                reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                reqBean.getUid());

        AttendAppealReq data = null;
        try
        {
            data = appealDao.querySingleAppealInfo(reqBean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "querySingleAppealInfo from DB failed,enterId={}|uid={}|phone={}",
                    reqBean.getUserInfo().getEnterId(), reqBean.getUserInfo()
                        .getUid(), reqBean.getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return resBean;
        }
        if (AssertUtil.isEmpty(data))
        {
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_NOT_EXISTS);
            resBean.setSummary(AtdcResultSummary.ATDC104.APPEAL_NOT_EXISTS);
            resBean.setData(null);
            return resBean;
        }
        List<AttendAppealReq> list = new ArrayList<AttendAppealReq>();
        list.add(data);
        resBean.setData(list);
        return resBean;
    }

    /**
     * 单个考勤异常申诉单详情查询参数校验
     * @param reqBean
     * @param resBean
     */
    private void checkQuerySingleAppealInfoParam(AttendAppealReq reqBean,
        AttendAppealRes resBean)
    {
        if (-1 == reqBean.getAppealId())
        {
            // 参数为空
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }
    }

    @Override
    public AttendAppealRes queryUserAppealList(AttendAppealReq reqBean)
    {
        AttendAppealRes resBean = new AttendAppealRes();
        // 参数校验
        checkQueryUserAppealListParam(reqBean, resBean);
        if (!resBean.isSuccess())
        {
            logger
                .info(
                    "queryUserAppealList checkParam failed,phone={}|enterId={}|uid={}|code={}|summary={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                    reqBean.getUid(), resBean.getCode(), resBean.getSummary());
            return resBean;
        }
        logger
            .info(
                "queryUserAppealList checkParam success,phone={}|enterId={}|uid={}",
                reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                reqBean.getUid());
        long totalCount = 0;
        try
        {
            totalCount = appealDao.queryUserAppealListCount(reqBean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "queryUserAppealListCount from DB failed,enterId={}|uid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getUid(), reqBean
                        .getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return resBean;
        }
        if (totalCount <= 0)
        {
            logger
                .info(
                    "queryUserAppealList not find any data,totalCount is 0,phone={}|enterId={}|uid={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                    reqBean.getUid());
            resBean.setPageNo(reqBean.getPageNo());
            resBean.setPageSize(reqBean.getPageSize());
            resBean.setTotalCount(0);
            return resBean;
        }
        int pageSize = reqBean.getPageSize();
        int totalSize = (int) (totalCount % pageSize == 0 ? totalCount
            / pageSize : totalCount / pageSize + 1);

        if (totalSize < reqBean.getPageNo())
        {
            resBean.setPageNo(reqBean.getPageNo());
            resBean.setPageSize(reqBean.getPageSize());
            resBean.setTotalCount(0);
            return resBean;
        }
        // 设定偏移量
        reqBean.setOffset((reqBean.getPageNo() - 1) * reqBean.getPageSize());
        List<AttendAppealReq> list = null;
        try
        {
            list = appealDao.queryUserAppealList(reqBean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "queryUserAppealList from DB failed,enterId={}|uid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getUid(), reqBean
                        .getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return resBean;
        }
        logger.info(
            "queryUserAppealList query success,phone={}|enterId={}|uid={}",
            reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
            reqBean.getUid());
        if (AssertUtil.isEmpty(list))
        {
            resBean.setCode(AtdcResultCode.ATDC108.NOT_APPEAL_DATA);
            resBean.setSummary(AtdcResultSummary.ATDC108.NOT_APPEAL_DATA);
            return resBean;
        }
        resBean.setPageNo(reqBean.getPageNo());
        resBean.setPageSize(reqBean.getPageSize());
        resBean.setTotalCount(totalCount);
        resBean.setData(list);
        return resBean;
    }

    /**
     * 查询用户已申诉列表信息参数校验
     * @param reqBean
     * @param resBean
     */
    private void checkQueryUserAppealListParam(AttendAppealReq reqBean,
        AttendAppealRes resBean)
    {
        UserInfo userInfo = reqBean.getUserInfo();

        reqBean.setEnterId(userInfo.getEnterId());
        reqBean.setUid(userInfo.getUid());
        if (50 <= reqBean.getPageSize() || 0 > reqBean.getPageSize())
        {
            // pageSize过长或小于0，则使用默认值
            reqBean.setPageSize(20);
        }
        if (0 >= reqBean.getPageNo())
        {
            reqBean.setPageNo(1);
        }
    }

    @Override
    public AttendAppealRes queryManageAppealList(AttendAppealReq reqBean)
    {
        AttendAppealRes resBean = new AttendAppealRes();
        // 参数校验
        checkQueryManageAppealListParam(reqBean, resBean);
        if (!resBean.isSuccess())
        {
            logger
                .info(
                    "queryManageAppealList checkParam failed,phone={}|enterId={}|examineUid={}|code={}|summary={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                    reqBean.getExamineUid(), resBean.getCode(),
                    resBean.getSummary());
            return resBean;
        }
        logger
            .info(
                "queryManageAppealList checkParam success,phone={}|enterId={}|examineUid={}",
                reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                reqBean.getExamineUid());
        long totalCount = 0;
        try
        {
            totalCount = appealDao.queryManageAppealListCount(reqBean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "queryManageAppealListCount from DB failed,enterId={}|examineUid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getExamineUid(), reqBean
                        .getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return resBean;
        }
        if (totalCount <= 0)
        {
            logger
                .info(
                    "queryManageAppealList not find data,totalCount is 0,phone={}|enterId={}|examineUid={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                    reqBean.getExamineUid());
            resBean.setPageNo(reqBean.getPageNo());
            resBean.setPageSize(reqBean.getPageSize());
            resBean.setTotalCount(0);
            return resBean;
        }
        int pageSize = reqBean.getPageSize();
        int totalSize = (int) (totalCount % pageSize == 0 ? totalCount
            / pageSize : totalCount / pageSize + 1);

        if (totalSize < reqBean.getPageNo())
        {
            resBean.setPageNo(reqBean.getPageNo());
            resBean.setPageSize(reqBean.getPageSize());
            resBean.setTotalCount(0);
            return resBean;
        }
        // 设定偏移量
        reqBean.setOffset((reqBean.getPageNo() - 1) * reqBean.getPageSize());
        List<AttendAppealReq> list = null;
        try
        {
            list = appealDao.queryManageAppealList(reqBean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "queryManageAppealList from DB failed,enterId={}|examineUid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getExamineUid(), reqBean
                        .getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return resBean;
        }
        logger.info(
            "queryManageAppealList success,phone={}|enterId={}|examineUid={}",
            reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
            reqBean.getExamineUid());
        if (AssertUtil.isEmpty(list))
        {
            resBean.setCode(AtdcResultCode.ATDC108.NOT_APPEAL_DATA);
            resBean.setSummary(AtdcResultSummary.ATDC108.NOT_APPEAL_DATA);
            return resBean;
        }
        resBean.setPageNo(reqBean.getPageNo());
        resBean.setPageSize(reqBean.getPageSize());
        resBean.setTotalCount(totalCount);
        resBean.setData(list);
        return resBean;
    }

    /**
     * 查询管理员待审批单列表信息参数校验
     * @param reqBean
     * @param resBean
     */
    private void checkQueryManageAppealListParam(AttendAppealReq reqBean,
        AttendAppealRes resBean)
    {
        UserInfo userInfo = reqBean.getUserInfo();
        reqBean.setExamineUid(userInfo.getUid());
        reqBean.setEnterId(userInfo.getEnterId());

        List<AttendExamineEntity> data = null;
        try
        {
            data = appealDao.queryExaminerByUid(userInfo.getUid());
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "queryExaminerByUid from DB failed when queryManageAppealList,enterId={}|examineUid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getExamineUid(), reqBean
                        .getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return;
        }

        if (AssertUtil.isEmpty(data))
        {
            // 非审批员，不能查询待审批信息
            resBean.setCode(AtdcResultCode.ATDC104.NOT_EXAMINER);
            resBean.setSummary(AtdcResultSummary.ATDC104.NOT_EXAMINER);
            return;
        }
        if (50 <= reqBean.getPageSize() || 0 > reqBean.getPageSize())
        {
            // pageSize过长或小于0，则使用默认值
            reqBean.setPageSize(20);
        }
        if (0 >= reqBean.getPageNo())
        {
            reqBean.setPageNo(1);
        }
    }

    @Override
    public AbnormalAppealRes abnormalAppeal(AttendAppealReq reqBean)
    {
        AbnormalAppealRes resBean = new AbnormalAppealRes();
        checkAbnormalAppealParam(reqBean, resBean);
        if (!resBean.isSuccess()) {
            logger.info("abnormalAppeal checkParam failed,phone={}|enterId={}|attendanceId={}|name={}|uid={}|" +
                    "examineName={}|examineUid={}|code={}|summary={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                    reqBean.getAttendanceId(), reqBean.getName(),
                    reqBean.getUid(), reqBean.getExamineName(),
                    reqBean.getExamineUid(), resBean.getCode(),
                    resBean.getSummary());
            return resBean;
        }
        logger.info("abnormalAppeal checkParam success,phone={}|enterId={}|attendanceId={}|name={}|uid={}|examineName={}|examineUid={}",
                reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                reqBean.getAttendanceId(), reqBean.getName(), reqBean.getUid(),
                reqBean.getExamineName(), reqBean.getExamineUid());

        reqBean.setExamineState(1);
        Date date = new Date();
        reqBean.setCreateTime(date);
        reqBean.setUpdateTime(date);
        appealDao.abnormalAppeal(reqBean, resBean);
        return resBean;
    }

    /**
     * 考勤异常申诉参数校验
     * @param reqBean
     * @param resBean
     */
    private void checkAbnormalAppealParam(AttendAppealReq reqBean, AbnormalAppealRes resBean) {
        //校验考勤审批上限数
        //查询企业是否开启
        try {
            //获取用户信息
            UserInfo userInfo = reqBean.getUserInfo();
            AttendApprovalRestrict attendApprovalRestrict = appealDao.queryApprovalRestrictByEnterId(userInfo.getEnterId());
            //说明没有开启
            if (attendApprovalRestrict != null && attendApprovalRestrict.getRestrictStatus() == 1){
                //查询月份的提交审批数
                //获取请求审批的月份
                Date attendanceDate = reqBean.getAttendanceDate();
                String attendanceMonth = TimeUtil.date2String(attendanceDate,TimeUtil.BASE_DATE_FORMAT_YYYY_MM);
                int approvalCount = appealDao.queryApprovalCount(userInfo.getUid(), attendanceMonth, userInfo.getEnterId());
                //提交上限
                if (approvalCount >= attendApprovalRestrict.getRestrictNumber()){
                    resBean.setCode(AtdcResultCode.ATDC107.ATTEND_APPEAL_FAIL);
                    resBean.setSummary(AtdcResultSummary.ATDC107.ATTEND_APPEAL_CAP);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            UserInfo userInfo = reqBean.getUserInfo();
            logger.error("checkAbnormalAppealParam queryApprovalRestrictByEnterId  error uid={}|enterId={}|errorMsg={} ",userInfo.getUid(),userInfo.getEnterId(),e);
            resBean.setCode(AtdcResultCode.ATDC107.BASE);
            resBean.setSummary(AtdcResultSummary.ATDC107.S_ERROR);
        }

        List<AttendGroup> attendGroups = attendGroupDao.queryGroupClockTime(reqBean.getAttendanceId());
        if (AssertUtil.isNotEmpty(attendGroups)){
            AttendGroup attendGroup = attendGroups.get(0);
            if (attendGroup.getStatus() != 0){
                // 考勤组已经删除
                resBean.setCode(AtdcResultCode.ATDC104.GROUP_DEL);
                resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_GROUP_DEL);
                return;
            }
        }
        if (abnormalAppealIsEmpty(reqBean)) {
            // 参数为空
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }

        // 对考勤异常申诉理由长度进行校验
        if (AssertUtil.isNotEmpty(reqBean.getReason())
            && reqBean.getReason().length() > AttendanceConfig.getInstance().getAppealReasonLength()) {
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_LENGTH_ILLEGAL);
            resBean.setSummary(AtdcResultSummary.ATDC104.APPEAL_LENGTH_ILLEGAL);
            return;
        }

        int appealRecord = reqBean.getAppealRecord();
        // 对申诉场景的参数进行判断（即只有申诉上午；申诉下午；申诉上、下午三种场景）
        if (appealRecord != APPEAL_MORNING && appealRecord != APPEAL_AFTERNOON && appealRecord != APPEAL_BOTH) {
            // 申诉班次参数非法
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_RECORD_ILLEGAL);
            resBean.setSummary(AtdcResultSummary.ATDC104.APPEAL_RECORD_ILLEGAL);
            return;
        }

        // 查询用户个人月报详情某一天的记录,与传递的参数进行比对，防止非法参数传递
        EmployeeMonthDetail info = null;
        try
        {
            info = appealDao.queryUserAttendDetail(reqBean);
        } catch (PersistException e) {
            logger.error("queryUserAttendDetail from DB failed when abnormalAppeal,enterId={}|uid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getUid(), reqBean.getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return;
        }

        if (AssertUtil.isEmpty(info)
            || info.getAttendanceId() != reqBean.getAttendanceId()
            || !info.getAttendanceDate().equals(reqBean.getAttendanceDate())
            || info.getUid().equals(reqBean.getUid())) {
            // 未查询到记录信息
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_ILLEGAL_NORECORD);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_ILLEGAL_NORECORD);
            return;
        }
        Date attendanceDate = info.getAttendanceDate();
        if (TimeUtil.formatDateTime(attendanceDate,TimeUtil.BASE_DATE_FORMAT)
                .equals(TimeUtil.formatDateTime(new Date(),TimeUtil.BASE_DATE_FORMAT))){
            // 申诉班次参数非法
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_ILLEGAL_DATE);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_AUTHORIZATION_DATE);
            return;
        }

        if (NORMAL_INFO.equals(info.getGoWorkDesc())
            && NORMAL_INFO.equals(info.getLeaveWorkDesc())) {
            // 正常无需异常申诉
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_ATTENDANCE_NORMAL);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_ATTENDANCE_NORMAL);
            return;
        }
        // 判断申诉场景值与用户实际打卡记录是否符合（防止一天记录中上午正常也进行申诉上午、下午场景），下次加判断，等待前端加好
        // 判断考勤组对应的审批人员
        // AttendExamineEntity data = null;
        UserGroupEntity userGroup = null;
        try
        {
            // data = appealDao.queryExamineUid(reqBean);
            userGroup = employeeDao.queryOwnGroup(reqBean.getUserInfo()
                .getUid());
        } catch (Exception e) {
            logger.error("queryExamineUid from DB failed when abnormalAppeal,enterId={}|uid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getUid(), reqBean.getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return;
        }
        if (AssertUtil.isEmpty(userGroup)
            || AssertUtil.isEmpty(userGroup.getExamineName())
            || AssertUtil.isEmpty(userGroup.getExamineUid())) {
            // 考勤组未设定审批员
            resBean.setCode(AtdcResultCode.ATDC104.ATTENDGROUP_NO_EXAMINER);
            resBean.setSummary(AtdcResultSummary.ATDC104.ATTENDGROUP_NO_EXAMINER);
            return;
        }

        // 从会话中获取用户基本参数
        UserInfo userInfo = reqBean.getUserInfo();
        reqBean.setUid(userInfo.getUid());
        reqBean.setEnterName(userInfo.getEnterName());
        reqBean.setEnterId(userInfo.getEnterId());
        reqBean.setName(userInfo.getEmployeeName());

        // 设定审批人信息
        reqBean.setExamineUid(userGroup.getExamineUid());
        reqBean.setExamineName(userGroup.getExamineName());
        reqBean.setName(reqBean.getUserInfo().getEmployeeName());
    }

    /**
     * 异常申诉判空
     * @param reqBean
     * @return
     */
    private boolean abnormalAppealIsEmpty(AttendAppealReq reqBean)
    {
        if (AssertUtil.isEmpty(reqBean.getReason()))
        {
            return true;
        }
        if (AssertUtil.isEmpty(reqBean.getAttendanceDate())
            || AssertUtil.isEmpty(reqBean.getRemark()))
        {
            return true;
        }
        if (reqBean.getAttendanceDate().getTime() < TimeUtil.convert2long("2018-11-01",TimeUtil.BASE_DATE_FORMAT)){
            logger.info("===============走历史数据=================");
            if (AssertUtil.isEmpty(reqBean.getGoWorkDesc()))
            {
                // return true;
                if (AssertUtil.isEmpty(reqBean.getGoWork())){
                    reqBean.setGoWorkDesc("未打卡");
                }
            }
            if (AssertUtil.isEmpty(reqBean.getLeaveWorkDesc()))
            {
                //return true;
                if (AssertUtil.isEmpty(reqBean.getLeaveWork())){
                    reqBean.setLeaveWorkDesc("未打卡");
                }
            }
        }
        if (AssertUtil.isEmpty(reqBean.getGoWorkDesc()))
        {
             return true;

        }
        if (AssertUtil.isEmpty(reqBean.getLeaveWorkDesc()))
        {
            return true;

        }
        if (-1 == reqBean.getAttendanceId() || -1 == reqBean.getAppealRecord())
        {
            return true;
        }
        if (-1 == reqBean.getMonthRcdId())
        {
            return true;
        }
        return false;
    }

    @Override
    public AttendExamineRes queryExamineUid(AttendAppealReq reqBean)
    {
        AttendExamineRes resBean = new AttendExamineRes();
        checkQueryExamineUidParam(reqBean, resBean);
        if (!resBean.isSuccess())
        {
            logger
                .info(
                    "queryExamineUid checkParam failed,phone={}|enterId={}|uid={}|attendanceId={}|code={}|summary={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                    reqBean.getUserInfo().getUid(), reqBean.getAttendanceId(),
                    resBean.getCode(), resBean.getSummary());
            return resBean;
        }
        logger
            .info(
                "queryExamineUid checkParam success,phone={}|enterId={}|uid={}|attendanceId={}",
                reqBean.getUserInfo().getPhone(), reqBean.getEnterId(), reqBean
                    .getUserInfo().getUid(), reqBean.getAttendanceId());

        AttendExamineEntity data = null;
        try
        {
            data = appealDao.queryExamineUid(reqBean);
        }
        catch (PersistException e)
        {
            logger.error(
                "queryExamineUid from DB failed,enterId={}|uid={}|phone={}",
                reqBean.getEnterId(), reqBean.getUserInfo().getAttendanceId(),
                reqBean.getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return resBean;
        }
        if (AssertUtil.isEmpty(data))
        {
            // 考勤组未设定审批员
            resBean.setCode(AtdcResultCode.ATDC104.ATTENDGROUP_NO_EXAMINER);
            resBean
                .setSummary(AtdcResultSummary.ATDC104.ATTENDGROUP_NO_EXAMINER);
            return resBean;
        }
        resBean.setAttendanceId(data.getAttendanceId());
        resBean.setEnterId(data.getEnterId());
        resBean.setExamineUid(data.getExamineUid());
        return resBean;
    }

    /**
     * 查询考勤组对应的审批员参数校验
     * @param reqBean
     * @param resBean
     */
    private void checkQueryExamineUidParam(AttendAppealReq reqBean,
        AttendExamineRes resBean)
    {
        if (AssertUtil.isEmpty(reqBean.getEnterId())
            || -1 == reqBean.getAttendanceId())
        {
            // 参数为空
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }

        // 根据传递进来的考勤组Id查询考勤组信息
        AttendGroupServiceImpl group = new AttendGroupServiceImpl();
        AttendGroup groupInfo = group.getAttendGroupInfoFromCache(
            reqBean.getAttendanceId(), reqBean.getEnterId());

        if (AssertUtil.isEmpty(groupInfo))
        {
            // 考勤组不存在
            resBean.setCode(AtdcResultCode.ATDC106.ATTENDANCEGROUP_NOT_EXISTS);
            resBean
                .setSummary(AtdcResultSummary.ATDC106.ATTENDANCEGROUP_NOT_EXISTS);
            return;
        }
    }

    @Override
    public ResBean cancelAppeal(AttendAppealReq reqBean)
    {
        ResBean resBean = new ResBean();
        AttendAppealReq appealInfo = checkCancelAppealParam(reqBean, resBean);
        if (!resBean.isSuccess())
        {
            logger
                .info(
                    "cancelAppeal checkParam failed,phone={}|enterId={}|uid={}|appealId={}|code={}|summary={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                    reqBean.getUid(), reqBean.getAppealId(), resBean.getCode(),
                    resBean.getSummary());
            return resBean;
        }
        logger
            .info(
                "cancelAppeal checkParam success,phone={}|enterId={}|uid={}|appealId={}",
                reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                reqBean.getUid(), reqBean.getAppealId());

        return appealDao.cancelAppeal(reqBean, resBean, appealInfo);
    }

    /**
     * 撤销考勤异常申诉单参数校验
     * @param reqBean
     */
    private AttendAppealReq checkCancelAppealParam(AttendAppealReq reqBean,
        ResBean resBean)
    {
        if (-1 == reqBean.getAppealId())
        {
            // 参数为空
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return null;
        }

        AttendAppealReq data = null;
        try
        {
            data = appealDao.querySingleAppealInfo(reqBean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "querySingleAppealInfo from DB failed when cancelAppeal,enterId={}|uid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getUid(), reqBean
                        .getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return null;
        }

        if (AssertUtil.isEmpty(data))
        {
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_NOT_EXISTS);
            resBean.setSummary(AtdcResultSummary.ATDC104.APPEAL_NOT_EXISTS);
            return null;
        }

        if (!data.getUid().equals(reqBean.getUserInfo().getUid()))
        {
            // 无权撤销他人异常申诉单
            resBean.setCode(AtdcResultCode.ATDC104.NOT_CANCEL_OTHER_APPEAL);
            resBean
                .setSummary(AtdcResultSummary.ATDC104.NOT_CANCEL_OTHER_APPEAL);
            return null;
        }

        if (2 == data.getExamineState())
        {
            // 表示该单已完成审批,审批后不能再被撤销
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_ALREADY_EXAMINE);
            resBean
                .setSummary(AtdcResultSummary.ATDC104.APPEAL_ALREADY_EXAMINE);
            return null;
        }

        if (3 == data.getExamineState())
        {
            // 该单已被用户撤销,不能再撤销
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_ALREADY_CANCEL);
            resBean.setSummary(AtdcResultSummary.ATDC104.APPEAL_ALREADY_CANCEL);
            return null;
        }
        return data;
    }

    @Override
    public ResBean dealAppeal(AttendAppealReq reqBean)
    {
        ResBean resBean = new ResBean();
        AttendAppealReq appealInfo = checkDealAppealParam(reqBean, resBean);
        if (!resBean.isSuccess())
        {
            logger
                .info(
                    "dealAppeal checkParam failed,phone={}|enterId={}|examineUid={}|appealId={}|examineResult={}code={}|summary={}",
                    reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                    reqBean.getExamineUid(), reqBean.getAppealId(),
                    reqBean.getExamineResult(), resBean.getCode(),
                    resBean.getSummary());
            return resBean;
        }
        logger
            .info(
                "dealAppeal checkParam success,phone={}|enterId={}|examineUid={}|uid={}|appealId={}|appealResult={}",
                reqBean.getUserInfo().getPhone(), reqBean.getEnterId(),
                reqBean.getExamineUid(), appealInfo.getUid(),
                reqBean.getAppealId(), reqBean.getExamineResult());
        // 审核同意涉及多个表数据修改，需加事务，需向下内聚
        appealDao.dealAppeal(reqBean, resBean, appealInfo);
        return resBean;
    }

    /**
     * 审批员审批申诉单参数校验
     * @param reqBean
     * @param resBean
     */
    private AttendAppealReq checkDealAppealParam(AttendAppealReq reqBean,
        ResBean resBean)
    {
        if (-1 == reqBean.getAppealId() || -1 == reqBean.getExamineResult())
        {
            // 参数为空
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return null;
        }

        if (1 != reqBean.getExamineResult() && 2 != reqBean.getExamineResult())
        {
            // 审批结果只能是同意或拒绝，没有其他情景
            resBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            resBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return null;
        }
        UserInfo userInfo = reqBean.getUserInfo();
        List<AttendExamineEntity> info = null;
        try
        {
            info = appealDao.queryExaminerByUid(userInfo.getUid());
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "queryExaminerByUid from DB failed when dealAppeal,enterId={}|examineUid={}|phone={}",
                    userInfo.getEnterId(), userInfo.getUid(),
                    userInfo.getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return null;
        }

        if (AssertUtil.isEmpty(info))
        {
            // 非审批员，不能查询待审批信息
            resBean.setCode(AtdcResultCode.ATDC104.NOT_EXAMINER);
            resBean.setSummary(AtdcResultSummary.ATDC104.NOT_EXAMINER);
            return null;
        }

        AttendAppealReq data = null;
        try
        {
            data = appealDao.querySingleAppealInfo(reqBean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "querySingleAppealInfo from DB failed when dealAppeal,enterId={}|examineUid={}|phone={}",
                    reqBean.getEnterId(), reqBean.getExamineUid(), reqBean
                        .getUserInfo().getPhone(), e);
            resBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            resBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return null;
        }

        if (AssertUtil.isEmpty(data))
        {
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_NOT_EXISTS);
            resBean.setSummary(AtdcResultSummary.ATDC104.APPEAL_NOT_EXISTS);
            return null;
        }

        if (!data.getExamineUid().equals(reqBean.getUserInfo().getUid()))
        {
            // 无权审核其他审批员的申诉单
            resBean.setCode(AtdcResultCode.ATDC104.NOT_ALLOW_APPEAL);
            resBean.setSummary(AtdcResultSummary.ATDC104.NOT_ALLOW_APPEAL);
            return null;
        }

        if (2 == data.getExamineState())
        {
            // 单已完成审核,审批后不能再再次审核
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_ALREADY_EXAMINE);
            resBean.setSummary(AtdcResultSummary.ATDC104.APPEAL_ALREADY_EXAMINE);
            return null;
        }

        if (3 == data.getExamineState())
        {
            // 该单已被用户撤销,不能再审核
            resBean.setCode(AtdcResultCode.ATDC104.APPEAL_ALREADY_CANCEL);
            resBean.setSummary(AtdcResultSummary.ATDC104.APPEAL_ALREADY_CANCEL);
            return null;
        }
        reqBean.setEnterId(userInfo.getEnterId());
        reqBean.setEnterName(userInfo.getEnterName());
        reqBean.setExamineUid(userInfo.getUid());
        return data;
    }
}

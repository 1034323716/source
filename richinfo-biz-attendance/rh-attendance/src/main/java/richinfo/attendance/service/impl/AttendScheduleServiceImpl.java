package richinfo.attendance.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.bean.AttendScheduleShiftReq;
import richinfo.attendance.bean.AttendScheduleShiftRsp;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.dao.AttendanceScheduleDao;
import richinfo.attendance.entity.AttendanceSchedule;
import richinfo.attendance.entity.AttendanceScheduleShift;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.service.AttendScheduleService;
import richinfo.attendance.util.AtdcTimeUtil;
import richinfo.attendance.util.AttendanceConfig;

public class AttendScheduleServiceImpl implements AttendScheduleService
{

    private AttendanceScheduleDao attendScheduleDao = new AttendanceScheduleDao();

    private Logger logger = LoggerFactory
        .getLogger(AttendScheduleServiceImpl.class);

    @Override
    public AttendScheduleShiftRsp queryAttendScheduleShifts(
        AttendScheduleShiftReq req)
    {
        AttendScheduleShiftRsp resp = new AttendScheduleShiftRsp();
        UserInfo userInfo = req.getUserInfo();
        // 校验请求参数
        checkQueryAttendScheduleShiftsParam(req, resp, userInfo);
        if (!resp.isSuccess())
        {
            logger.info("queryScheduleShifts checkParam failed|req :{}",
                req.toString());
            return resp;
        }
        logger.info("queryScheduleShifts checkParam success|req :{}",
            req.toString());

        // 查询消息总量
        long totalCount = attendScheduleDao.queryAttendanceScheduleShiftCount(
            req, userInfo);
        dealQueryAttendanceScheduleShiftPages(req, totalCount);

        if (totalCount <= 0 || req.getPageNo() > req.getTotalSize())
        {
            logger
                .info(
                    "queryTeamDailyInfo can't find data,phone={}|uid={}|reqParam={}",
                    userInfo.getPhone(), userInfo.getUid(), req);
            resp.setPageNo(req.getPageNo());
            resp.setTotalCount(req.getTotalCount());
            resp.setPageSize(req.getPageSize());
            resp.setData(null);
            return resp;
        }
        List<AttendanceScheduleShift> list = attendScheduleDao
            .queryAttendanceScheduleShift(req);
        resp.setPageNo(req.getPageNo());
        resp.setTotalCount(totalCount);
        resp.setPageSize(req.getPageSize());
        resp.setData(list);
        return resp;

    }

    @Override
    public AttendScheduleShiftRsp queryAttendSchedule(AttendScheduleShiftReq req)
    {
        AttendScheduleShiftRsp resp = new AttendScheduleShiftRsp();
        UserInfo userInfo = req.getUserInfo();
        // 校验请求参数
        checkQueryAttendScheduleParam(req, resp, userInfo);
        if (!resp.isSuccess())
        {
            logger.info("querySchedule checkParam failed|req :{}",
                req.toString());
            return resp;
        }
        logger.info("querySchedule checkParam success|req :{}", req.toString());

        // 查询消息总量
        long totalCount = attendScheduleDao.queryAttendanceScheduleCount(req,
            userInfo);
        dealQueryAttendanceScheduleShiftPages(req, totalCount);

        if (totalCount <= 0 || req.getPageNo() > req.getTotalSize())
        {
            logger
                .info(
                    "queryAttendSchedule can't find data,phone={}|uid={}|reqParam={}",
                    userInfo.getPhone(), userInfo.getUid(), req);
            resp.setPageNo(req.getPageNo());
            resp.setTotalCount(req.getTotalCount());
            resp.setPageSize(req.getPageSize());
            resp.setData(null);
            return resp;
        }
        List<AttendanceSchedule> list = attendScheduleDao
            .queryAttendanceSchedule(req);
        resp.setPageNo(req.getPageNo());
        resp.setTotalCount(totalCount);
        resp.setPageSize(req.getPageSize());
        resp.setAttendanceScheduleList(list);
        resp.setAttendMonth(req.getAttendMonth());
        return resp;

    }

    private void checkQueryAttendScheduleShiftsParam(
        AttendScheduleShiftReq req, AttendScheduleShiftRsp resp,
        UserInfo userInfo)
    {
        if (userInfo.getIsAdmin() != 1)
        {
            // 是否为管理员
            resp.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            resp.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }
    }

    private void checkQueryAttendScheduleParam(AttendScheduleShiftReq req,
        AttendScheduleShiftRsp resp, UserInfo userInfo)
    {
        if (userInfo.getIsAdmin() != 1)
        {
            // 是否为管理员
            resp.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            resp.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }
        // 校验格式 yyyy-mm
        if (!AtdcTimeUtil.isAttendanceMonthLegal(req.getAttendMonth()))
        {
            logger.warn("scheduleMonth format is wrong.scheduleMonth={}",
                req.getAttendMonth());
            resp.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
            resp.setSummary(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
            return;
        }
    }

    /**
     * 处理考勤組排班班次查询分页数据
     * @param teamReq
     * @param totalCount
     */
    private void dealQueryAttendanceScheduleShiftPages(
        AttendScheduleShiftReq req, long totalCount)
    {
        if (totalCount > 0)
        {
            int pageSize = req.getPageSize();

            if (pageSize > AttendanceConfig.getInstance().getPropertyInt(
                "attend.page.maxSize", 200)
                || pageSize < 1)
            {
                pageSize = 20;
                req.setPageSize(20);
            }
            int totalSize = (int) ((totalCount % pageSize == 0) ? totalCount
                / pageSize : (totalCount / pageSize) + 1);
            req.setTotalSize(totalSize);

            req.setOffset((req.getPageNo() - 1) * pageSize);

            if (req.getPageNo() <= 0)
            {
                req.setPageNo(1);
                req.setOffset(0);
            }
        }
    }
}

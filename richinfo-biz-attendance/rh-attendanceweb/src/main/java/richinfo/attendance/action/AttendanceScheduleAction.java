/**
 * 文件名：AttendanceScheduleAction.java
 * 创建日期： 2018年4月17日
 * 作者：     JaceJiang
 * All rights reserved.
 */
package richinfo.attendance.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import richinfo.attendance.bean.AttendScheduleShiftReq;
import richinfo.attendance.bean.AttendScheduleShiftRsp;
import richinfo.attendance.service.AttendScheduleService;
import richinfo.attendance.service.impl.AttendScheduleServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 功能描述： 考勤组排班班次接口类
 */
@Controller
@RequestMapping("/schedule")
public class AttendanceScheduleAction extends BaseAttendanceAction {
    private AttendScheduleService attendScheduleService = new AttendScheduleServiceImpl();

    private final Logger logger = LoggerFactory
        .getLogger(AttendanceScheduleAction.class);

    /**
     * 考勤组排班班次列表查询
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryScheduleShifts", method = RequestMethod.POST)
    public void queryScheduleShifts(HttpServletRequest request,
        HttpServletResponse response) {
        AttendScheduleShiftReq req = new AttendScheduleShiftReq();
        setReqBean(req, request);
        Map<String, Object> map = parserReqJsonParam(request);
        paserAttendScheduleReqParam(map, req);
        AttendScheduleShiftRsp attendScheduleShiftRsp = attendScheduleService.queryAttendScheduleShifts(req);
        logger.debug("queryScheduleShifts.res={}|useTime={}", attendScheduleShiftRsp, req.getUseTime());
        processJsonTemplate(response, "attendance/queryAttendScheduleShift_json.ftl", attendScheduleShiftRsp);
    }

    /**
     * 考勤组排班列表查询
     * @param request
     * @param response
     */
   // @RequestMapping(value = "/querySchedule", method = RequestMethod.POST)
    public void querySchedule(HttpServletRequest request, HttpServletResponse response) {
        AttendScheduleShiftReq req = new AttendScheduleShiftReq();
        setReqBean(req, request);
        Map<String, Object> map = parserReqJsonParam(request);
        paserAttendScheduleReqParam(map, req);
        AttendScheduleShiftRsp attendScheduleShiftRsp = attendScheduleService.queryAttendSchedule(req);
        logger.debug("querySchedule.res={}|useTime={}", attendScheduleShiftRsp, req.getUseTime());
        processJsonTemplate(response, "attendance/queryAttendSchedule_json.ftl", attendScheduleShiftRsp);
    }

    /**
     * 组装考勤组排班班次请求参数
     * @param map
     * @return
     */
    private void paserAttendScheduleReqParam(Map<String, Object> map, AttendScheduleShiftReq attendScheduleShiftReq) {
        Integer pageNo = Integer.valueOf((String) map.get("pageNo"));
        Integer pageSize = Integer.valueOf((String) map.get("pageSize"));
        Long attendanceId = Long.valueOf((String) map.get("attendanceId"));
        String attendMonth = (String) map.get("attendMonth");
        if (pageNo != null) {
            attendScheduleShiftReq.setPageNo(pageNo);
        }
        if (pageSize != null) {
            attendScheduleShiftReq.setPageSize(pageSize);
        }
        if (attendanceId != null) {
            attendScheduleShiftReq.setAttendanceId(attendanceId);
        }
        if (attendMonth != null && !"".equals(attendMonth)) {
            attendScheduleShiftReq.setAttendMonth(attendMonth);
        }
    }
}

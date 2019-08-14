/**
 * 文件名：AttendanceAction.java
 * 创建日期： 2017年6月1日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月1日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import richinfo.attendance.bean.AttendReq;
import richinfo.attendance.bean.AttendRes;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.common.PublicConstant;
import richinfo.attendance.common.ResBean;
import richinfo.attendance.service.AttendEmployService;
import richinfo.attendance.service.AttendService;
import richinfo.attendance.service.impl.AttendEmployServiceImpl;
import richinfo.attendance.service.impl.AttendServiceImpl;
import richinfo.attendance.util.AtdcStringUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.ConverUtil;
import richinfo.bcomponet.cache.CachedUtil;
import richinfo.bcomponet.cache.comm.CacheKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： 考勤接口类
 */
@Controller
@RequestMapping("/attend")
public class AttendanceAction extends BaseAttendanceAction {
    private AttendService attednService = new AttendServiceImpl();
    private AttendEmployService attendEmployService = new AttendEmployServiceImpl();

    private final Logger logger = LoggerFactory.getLogger(AttendanceAction.class);

    /**
     * 员工打卡
     * @param request
     * @param response
     */
    @RequestMapping(value = "/clock", method = RequestMethod.POST)
    public void clock(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);
        // 组装请求信息
        AttendReq attendReq = parserReqMap(reqMap);
        // 获取会话中的数据信息
        setReqBean(attendReq, request);
        //判断打卡间隔时间
        //生成key
        String clockKey = String.format(CacheKey.Attendance.ATTENDANCE_USER_FIRST_LOGIN_U, PublicConstant.ATTENDANCE_USER_CLOCK_DATE_U+attendReq.getUserInfo().getUid());
        //获取时间
        Object object = CachedUtil.get(clockKey);
        //不为空说明离上次打卡的时间没有超过间隔时间
        if (object != null) {
            AttendRes attendRes = new AttendRes();
            logger.info("clock time has not exceeded the interval requirement uid={}",attendReq.getUserInfo().getUid());
            attendRes.setCode(AtdcResultCode.ATDC107.CLOCK_ERROR_DATE);
            attendRes.setSummary(AtdcResultSummary.ATDC107.CLOCK_ERROR_DATE);
            // 模板内容返回固定的值
            processJsonTemplate(response, "attendance/clock_json.ftl" , attendRes);
            return;
        }
        // 缓存30秒
        CachedUtil.set(clockKey, "0", AttendanceConfig.getInstance().getPropertyLong("attend.user.clockTime", 30 * 1000));
        // 考勤打卡
        AttendRes attendRes = attednService.clock(attendReq);
        // 打卡失败，输出请求日志
        if (!attendRes.isSuccess()) {
            //打卡失败删除缓存
            CachedUtil.delete(clockKey);
            logger.error("clock failed.req={}|uid={}", AtdcStringUtil.getRequestString(reqMap),attendReq.getUserInfo().getUid());
        }
        // 模板内容返回固定的值
        processJsonTemplate(response, "attendance/clock_json.ftl" , attendRes);
    }

    /**
     * 查询员工当天考勤记录
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryEmployRecord", method = RequestMethod.POST)
    public void queryEmployRecord(HttpServletRequest request, HttpServletResponse response) {
        AttendReq req = new AttendReq();
        setReqBean(req, request);
        Map<String, Object> map = parserReqJsonParam(request);
        req.setUid((String) map.get("uid"));
        AttendRes attendRes = attednService.queryEmployRecord(req);
        logger.info("queryEmployRecord return response attendClockVos = {}",attendRes.attendClockVos);
        processJsonTemplate(response, "attendance/queryEmployRecord_json.ftl", attendRes);
    }

    /**
     * 组装考勤打卡的请求信息
     * @param reqMap
     * @return
     */
    private AttendReq parserReqMap(Map<String, Object> reqMap) {
        AttendReq attendReq = new AttendReq();
        attendReq.setUid((String) reqMap.get("uid"));
        attendReq.setPhone((String) reqMap.get("phone"));
        attendReq.setAttendanceId(ConverUtil.string2Long((String) reqMap.get("attendanceId")));
        attendReq.setLocation((String) reqMap.get("location"));
        attendReq.setDetailAddr((String) reqMap.get("detailAddr"));
        attendReq.setLongitude(ConverUtil.string2Double((String) reqMap.get("longitude")));
        attendReq.setLatitude(ConverUtil.string2Double((String) reqMap.get("latitude")));
        attendReq.setStatus(ConverUtil.string2Int((String) reqMap.get("status")));
        attendReq.setClockSource((String) reqMap.get("clockSource"));
        if ("1".equals(reqMap.get("status"))) {
            attendReq.setOutWorkRemark((String) reqMap.get("outWorkRemark"));
        }
        attendReq.setEquipmentSerial((String) reqMap.get("equipmentSerial")==null?"":(String) reqMap.get("equipmentSerial"));
//        attendReq.setEquipmentSerial((String) reqMap.get("equipmentSerial"));

        return attendReq;
    }

    /**
     * 短信打卡设置接口
     */
    @RequestMapping(value = "/updateEmploySMSSwitch", method = RequestMethod.POST)
    public void updateEmploySMSSwitch(HttpServletRequest request, HttpServletResponse response) {
//        AttendReq req = new AttendReq();
//        setReqBean(req, request);
        Map<String, Object> map = parserReqJsonParam(request);
        String uid = (String) map.get("uid");
        String smsSwitch = (String) map.get("smsSwitch");
        boolean modifyLine = attendEmployService.settingSMSRemider(uid, Integer.valueOf(smsSwitch));
        ResBean resultResp = new ResBean();
        resultResp.setSummary("短信打卡设置成功");
        if(!modifyLine) {
            resultResp.setCode("S_ERROR");
            resultResp.setSummary("短信打卡设置失败");
        }
        processJsonTemplate(response, "attendance/common_json.ftl", resultResp);
    }

    /**
     * 短信打卡设置接口
     */
    @RequestMapping(value = "/getEmploySMSSwitch", method = RequestMethod.POST)
    @ResponseBody
    public Map getEmploySMSSwitch(HttpServletRequest request) {
//        AttendReq req = new AttendReq();
//        setReqBean(req, request);
        Map<String, Object> map = parserReqJsonParam(request);
        String uid = (String) map.get("uid");
        Map<String,Object> responseObj = new HashMap<>();
        if(StringUtils.isEmpty(uid)) {
            responseObj.put("code","S_ERROR");
            responseObj.put("summary","uid入参不能为空");
            return responseObj;
        }

        int modifyLine = attendEmployService.getSMSRemiderStatus(uid);
        Map<String,Integer> SMSStatus = new HashMap<>();
        SMSStatus.put("status", modifyLine);
        responseObj.put("code","S_OK");
        responseObj.put("summary","查询成功");
        responseObj.put("var", SMSStatus);

        return responseObj;
    }
}

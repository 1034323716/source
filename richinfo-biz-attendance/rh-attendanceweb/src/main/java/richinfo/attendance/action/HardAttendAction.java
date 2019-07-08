/**
* 文件名：HardAttendAction.java
* 创建日期： 2017年8月30日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年8月30日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import richinfo.attendance.bean.AttendRes;
import richinfo.attendance.bean.HardAttendReq;
import richinfo.attendance.service.AttendService;
import richinfo.attendance.service.impl.AttendServiceImpl;
import richinfo.attendance.util.AtdcStringUtil;
import richinfo.attendance.util.ConverUtil;
import richinfo.bcomponet.util.BUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 功能描述：
 * 硬件打卡上报action类
 */
@Controller
@RequestMapping("/hard")
public class HardAttendAction extends BaseAttendanceAction {
 
    private AttendService attednService = new AttendServiceImpl();
    
    private final Logger logger = LoggerFactory.getLogger(HardAttendAction.class);

    /**
     * 员工打卡
     * @param request
     * @param response
     */
    @RequestMapping(value = "/hardClock", method = RequestMethod.POST)
    public void clock(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> reqMap = parserReqJsonParam(request);

        // 组装请求信息
        HardAttendReq req = new HardAttendReq();
        req.setEnterId((String) reqMap.get("enterId"));
        req.setClientIP(BUtils.getIpAddr(request));
        req.setToken(request.getParameter("sign"));
        req.setPhone((String) reqMap.get("phone"));
        req.setLocation((String) reqMap.get("location"));
        req.setDetailAddr((String) reqMap.get("detailAddr"));
        req.setClockTime((String) reqMap.get("clockTime"));
        req.setCurTime(ConverUtil.string2Long((String) reqMap.get("curTime")));
        req.setLongitude(ConverUtil.string2Double((String) reqMap.get("longitude")));
        req.setLatitude(ConverUtil.string2Double((String) reqMap.get("latitude")));
        req.setStatus(ConverUtil.string2Int((String) reqMap.get("status")));

        AttendRes attendRes = attednService.hardClock(req);
        // 打卡失败，输出请求日志
        if (!attendRes.isSuccess()) {
            logger.warn("hard clock failed.req={}", AtdcStringUtil.getRequestString(reqMap));
        }
        // 模板内容返回固定的值
        processJsonTemplate(response, "attendance/common_json.ftl", attendRes);
    }
}

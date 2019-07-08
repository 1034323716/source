/**
 * 文件名：QytxlRegisterAction.java
 * 创建日期： 2018年6月9日
 * 作者：     黄学振
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 */
package richinfo.attendance.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import richinfo.attendance.service.QytxlService;
import richinfo.attendance.service.impl.QytxlServiceImpl;
import richinfo.attendance.util.AttendanceConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 功能描述： 企业通讯录注册
 */
@Controller
@RequestMapping("/qytxl")
public class QytxlAction extends BaseAttendanceAction {
    private static Logger log = LoggerFactory.getLogger(QytxlAction.class);
    
    private QytxlService qytxlService = new QytxlServiceImpl();

    /**
     * 单点登录请求
     * @param request
     * @param response
     * @throws ServletException
     */
    @RequestMapping(value = "/registerEnters", method = RequestMethod.GET)
    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> reqMap = parserReqParam(request);
        String isAdmin = (String) reqMap.get("isAdmin");
        int flag = AttendanceConfig.getInstance().getQytxlRegisterFlag();
        if(flag==1) {
            qytxlService.registerAll();
        }
    }
}

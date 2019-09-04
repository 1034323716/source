/**
 * 文件名：AttendGroupAction.java
 * 创建日期： 2017年6月1日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 *  1.修改时间：2017年6月1日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import richinfo.attendance.action.BaseAttendanceAction;
import richinfo.attendance.cache.UserInfoCache;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：
 * 会话校验拦截器
 */
public class LoginValidateInterceptor extends HandlerInterceptorAdapter {
    private Logger log = LoggerFactory.getLogger(LoginValidateInterceptor.class);
    private UserInfoCache userInfoCache = UserInfoCache.getInstance();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("request url:" + request.getRequestURL());
        String token = getToken(request);
        log.debug("token:" + token);
        if (AssertUtil.isEmpty(token)) {
            log.info("don't get loginToken from cookie,ip={}|requestUrl={}", request.getRemoteHost(),request.getRequestURI());
            checkLoginFailure(response);
            return false;
        }
        UserInfo userInfo = userInfoCache.get(token);
        log.debug("get userInfo from cached,userInfo={},token={}|requestUrl={}", userInfo,token,request.getRequestURI());
        if (AssertUtil.isNotEmpty(userInfo)) {
            long current = System.currentTimeMillis();
            // 如果（当前时间-缓存更新时间） > 用户缓存刷新间隔时间，则更新缓存
            if ((current - userInfo.getCacheupdatetime()) >
                AttendanceConfig.getInstance().getPropertyLong("attend.user.cacheUpdateInterval", 120000)) {
                userInfo.setCacheupdatetime(System.currentTimeMillis());
                userInfoCache.save(token, userInfo, AttendanceConfig.getInstance().getPropertyLong("attend.user.cacheTime", 1800000));
            }
            return true;
        }
        checkLoginFailure(response);
        return false;
    }

    /**
     * 从cookie里面获取loginToken
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (AssertUtil.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if ("loginToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }

    /**
     * 功能描述：
     * 继承基类Action以实现freemark模版调用
     */
    class Inner extends BaseAttendanceAction {
        @Override
        protected void processJsonTemplate(HttpServletResponse resp,
            String template, Object resultMap) {
            super.processJsonTemplate(resp, template, resultMap);
        }
    }
    
    /**
     * 接口请求会话校验失败处理
     * @param resp
     */
    private void checkLoginFailure(HttpServletResponse resp) {
        log.info("chekLoginFailue:没有Userinfo");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", AtdcResultCode.ATDC102.USER_SESSION_ERROR);
        resultMap.put("summary",AtdcResultSummary.ATDC102.USER_SESSION_ERROR);
        new Inner().processJsonTemplate(resp, "attendance/common_json.ftl",  resultMap);
    }
}

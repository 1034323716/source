package richinfo.attendance.action;

import org.springframework.web.bind.annotation.ExceptionHandler;
import richinfo.attendance.bean.AttendReqBean;
import richinfo.attendance.cache.UserInfoCache;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.common.ResBean;
import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.entity.AttendEmployee.EmployeeType;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.service.AttendLoginService;
import richinfo.attendance.service.impl.AttendLoginServiceImpl;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.ConverUtil;
import richinfo.attendance.util.JsonUtil;
import richinfo.bcomponet.util.BUtils;
import richinfo.frame.action.BaseAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAttendanceAction extends BaseAction {
    protected UserInfoCache userInfoCache = UserInfoCache.getInstance();
    protected AttendLoginService loginService = new AttendLoginServiceImpl();
    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();



//    * 校验用户登录信息
//    * @param
//    * @return
    protected UserInfo checkLogin(HttpServletRequest request) {
        String token = BUtils.getCookie(request, "loginToken");

        UserInfo info = userInfoCache.get(token);

        if (!AssertUtil.isNull(info)) {
            long current = System.currentTimeMillis();
            // 如果（当前时间-缓存更新时间） > 用户缓存刷新间隔时间，则更新缓存
            if ((current - info.getCacheupdatetime()) >
                AttendanceConfig.getInstance().getPropertyLong("attend.user.cacheUpdateInterval", 120000)) {
                info.setCacheupdatetime(System.currentTimeMillis());
                userInfoCache.save(
                    token, info,AttendanceConfig.getInstance().getPropertyLong("attend.user.cacheTime", 1800000));
            }
        }
        return info;
    }
    
    /**
     * 设置考勤系统的基本请求参数
     * @param reqBean 基本bean
     * @param request 请求对象
     */
    protected void setReqBean(AttendReqBean reqBean, HttpServletRequest request) {
        String token = BUtils.getCookie(request, "token");
        UserInfo userInfo = userInfoCache.get(token);
        // 内部接口测试 先new一个对象便于测试，2017-06-21，注释测试代码
/*        if(AssertUtil.isEmpty(userInfo)){
            userInfo = new UserInfo();
            userInfo.setIsAdmin(1);
            userInfo.setAttendanceId(5);
            userInfo.setUid("100886");
            userInfo.setEnterId("12345678");
        }*/
        // TODO 先设置全部是管理员 便于测试 上线要修改
        // 临时设置管理员列表，上线后修改，2017-06-27
        /*if (AttendanceConfig.getInstance().getTestAdminId()
            .indexOf("," + userInfo.getEnterId() + ",") > -1
            || AttendanceConfig.getInstance().getTestAdminId()
                .indexOf("," + userInfo.getUid() + ",") > -1
            || 1 == userInfo.getIsAdmin())
        {
            userInfo.setIsAdmin(1);
        }
        else
        {
            userInfo.setIsAdmin(0);
        }*/
        
        if(AssertUtil.isNotEmpty(userInfo)){
            reqBean.setToken(token);
            reqBean.setUserInfo(userInfo);
            AttendEmployee attendEmployee = employeeDao.queryEmployeeByUid(reqBean.getUserInfo().getUid());
            if (AssertUtil.isNotEmpty(attendEmployee)) {
                reqBean.getUserInfo().setRoleType(attendEmployee.getRoleType()==null?0:attendEmployee.getRoleType());
            } else {
                List<String> attendanceIds = employeeDao.queryChargeGroupByUid(reqBean.getUserInfo().getUid());
                if (AssertUtil.isNotEmpty(attendanceIds)) {
                    reqBean.getUserInfo().setRoleType(EmployeeType.ChargeMan.getValue());
                } else {
                    reqBean.getUserInfo().setRoleType(EmployeeType.NormalEmp.getValue());
                }
            }
        }
        reqBean.setClientIP(BUtils.getIpAddr(request));
        reqBean.setProxyIP(BUtils.getRemoteIP(request));
    }
    
    /**
     * 接口请求会话校验失败处理
     * @param resp
     */
    public void checkLoginFailure(HttpServletResponse resp) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", AtdcResultCode.ATDC102.USER_SESSION_ERROR);
        resultMap.put("summary",AtdcResultSummary.ATDC102.USER_SESSION_ERROR);
        processJsonTemplate(resp, "attendance/common_json.ftl",  resultMap);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String url) {
        if(!url.startsWith("http")){
            url = request.getContextPath() + url;
        }
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            logger.warn("跳转至{}失败，原因为:{}", url, e.getMessage());
        }
    }

    protected void forward(HttpServletRequest request, HttpServletResponse response, String url) {
        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (IOException e) {
            logger.warn("跳转至{}失败，原因为:{}", url, e.getMessage());
        } catch (ServletException e) {
            logger.warn("跳转至{}失败，原因为:{}", url, e.getMessage());
        }
    }

    protected int getInt(HttpServletRequest request, String key, int defaultVal) {
        String value = request.getParameter(key);
        return ConverUtil.string2Int(value, defaultVal);
    }

    protected int getInt(HttpServletRequest request, String key) {
        String value = request.getParameter(key);
        return ConverUtil.string2Int(value);
    }


    protected void makeResponse(HttpServletResponse response, ResBean resBean) {
        String text = JsonUtil.beanToJson(resBean);
        writeResponse(response, text);
    }

    protected void writeResponse(HttpServletResponse response, String content) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/javascript;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(content);
        } catch (IOException e) {
            logger.warn("", e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 系统全局异常处理类，由springMVC框架调用
     * @param response
     * @param ex
     */
    @ExceptionHandler
    public void GlobalExceptionHandle(HttpServletResponse response, Exception ex) {
        // 打印异常堆栈信息
        logger.error("Global Exception happened.", ex);
        // 返回系统错误码，s_ERROR
        ResBean resBean = new ResBean();
        resBean.setCode("S_ERROR");
        resBean.setSummary("操作失败，请重试");
        // 模板内容返回
        processJsonTemplate(response, "attendance/common_json.ftl", resBean);
    }
}

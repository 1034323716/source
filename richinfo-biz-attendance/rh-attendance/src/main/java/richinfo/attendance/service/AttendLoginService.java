/**
 * 文件名：AttendLoginService.java
 * 创建日期： 2017年6月5日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.service;

import richinfo.attendance.bean.AttendLoginReq;
import richinfo.attendance.bean.AttendLoginRes;
import richinfo.attendance.bean.UmcGetArtifactRes;
import richinfo.attendance.entity.UserInfo;

/**
 * 功能描述： 考勤系统登录接口类
 */
public interface AttendLoginService
{
    /**
     * 
     * @param token 会话登录token
     * @param isAdmin 管理员标识 1是0否
     * @return
     */
    public AttendLoginRes ssoAttendance(AttendLoginReq req);

    /**
     * PC端单点登录校验
     * @param req
     * @return
     */
    public AttendLoginRes ssoAttendancePc(AttendLoginReq req);

    /**
     * PC端获取凭证
     * @param req
     * @return
     */
	public UmcGetArtifactRes getArtifactPc(UserInfo userinfo);

}

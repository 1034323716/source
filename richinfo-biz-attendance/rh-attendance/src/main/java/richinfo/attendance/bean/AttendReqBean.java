/**
* 文件名：AttendReqBean.java
* 创建日期： 2017年6月5日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年6月5日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.bean;

import java.io.Serializable;

import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.common.BeanObject;
import richinfo.attendance.common.IgnoreBinding;

/**
 * 功能描述：
 * 考勤公共请求bean
 */
public class AttendReqBean extends BeanObject implements Serializable
{
    private static final long serialVersionUID = -1764581169777515260L;

    /** 用户唯一Id，对应和通讯录系统唯一企业联系人id */
    private String uid;
    
    /** 考勤组Id，显示设置默认值为0，判断考勤人员是否重复时，不在考勤组时，返回ID为0 */
    private long attendanceId = 0L;

    /** 企业Id */
    private String enterId;
    
    /**企业Id,针对集团类企业(由enterId和firstDeptId的组合值)*/
    private String enterDeptId;
    
    /** 企业名称 */
    private String enterName;

    /**
     * 用户会话
     */
    @IgnoreBinding
    private UserInfo userInfo;
    
    /** 登录token */
    @IgnoreBinding
    private String token;
    
    /** 客户端ip */
    @IgnoreBinding
    private String clientIP;

    /** 服务器ip */
    @IgnoreBinding
    private String proxyIP;
    
    /** 创建时间，记录当前时间用于处理耗时 */
    private long createTime = System.currentTimeMillis();

    public String getEnterDeptId()
    {
        return enterDeptId;
    }

    public void setEnterDeptId(String enterDeptId)
    {
        this.enterDeptId = enterDeptId;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public String getEnterName()
    {
        return enterName;
    }

    public void setEnterName(String enterName)
    {
        this.enterName = enterName;
    }

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo)
    {
        this.userInfo = userInfo;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getClientIP()
    {
        return clientIP;
    }

    public void setClientIP(String clientIP)
    {
        this.clientIP = clientIP;
    }

    public String getProxyIP()
    {
        return proxyIP;
    }

    public void setProxyIP(String proxyIP)
    {
        this.proxyIP = proxyIP;
    }
    
    /**
     * 获取处理耗时
     * @return
     */
    public long getUseTime()
    {
        return System.currentTimeMillis() - createTime;
    }
}

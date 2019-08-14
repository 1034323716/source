/**
 * 文件名：UmcArtifactValidRes.java
 * 创建日期： 2018年2月6日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年2月6日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;

/**
 * 功能描述：PC端校验凭证返回用户节点信息实体类
 *
 */
public class UmcArtifactValidRes extends ResBean
{
    private static final long serialVersionUID = -3260382677796127316L;
    
    /**用户单点登录标识*/
    private String uid;
    /**通行证号*/
    private String passId;
    /**用户内部标识，供密码二次哈希使用*/
    private String userId;
    /**通行证绑定的手机号码，11位*/
    private String mobileNumber;
    /**通行证绑定的邮箱地址*/
    private String emailAddress;
    /**昵称*/
    private String nickName;
    /**时间戳，通行证信息最后一次更新时间，精确到毫秒，共17位*/
    private String lastChangeTime;
    /**扩展参数。格式：param1=value1&param2=value2 方式传递*/
    private String expandParams;
    public String getUid()
    {
        return uid;
    }
    public void setUid(String uid)
    {
        this.uid = uid;
    }
    public String getPassId()
    {
        return passId;
    }
    public void setPassId(String passId)
    {
        this.passId = passId;
    }
    public String getUserId()
    {
        return userId;
    }
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    public String getMobileNumber()
    {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }
    public String getEmailAddress()
    {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }
    public String getNickName()
    {
        return nickName;
    }
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }
    public String getLastChangeTime()
    {
        return lastChangeTime;
    }
    public void setLastChangeTime(String lastChangeTime)
    {
        this.lastChangeTime = lastChangeTime;
    }
    public String getExpandParams()
    {
        return expandParams;
    }
    public void setExpandParams(String expandParams)
    {
        this.expandParams = expandParams;
    }
    @Override
    public String toString()
    {
        return "UmcArtifactValidRes [uid=" + uid + ", passId=" + passId
            + ", userId=" + userId + ", mobileNumber=" + mobileNumber
            + ", emailAddress=" + emailAddress + ", nickName=" + nickName
            + ", lastChangeTime=" + lastChangeTime + ", expandParams="
            + expandParams + "]";
    }
}

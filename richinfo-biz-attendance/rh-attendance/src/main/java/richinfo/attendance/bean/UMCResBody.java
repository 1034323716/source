/**
* 文件名：UMCResBody.java
* 创建日期： 2017年6月6日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年6月6日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.bean;

import java.io.Serializable;

import richinfo.attendance.common.BeanObject;

/**
 * 功能描述： 统一认证UMC返回报文body
 */
public class UMCResBody extends BeanObject implements Serializable
{

    private static final long serialVersionUID = 6613931331283411463L;

    /**
     * 统一认证平台为用户生成的单点登录凭证
     */
    private String usessionid;

    /**
     * 用户统一账号的系统标识
     */
    private String passid;

    /**
     * 用户的“和ID”
     */
    private String andid;
    
    /**
     * 表示手机号码
     */
    private String msisdn;

    /**
     * 表示邮箱地址
     */
    private String email;

    /**
     * 登录使用的用户标识：0：手机号码1：邮箱
     */
    private String loginidtype;
    
    /**
     * 手机号码的归属运营商
     * 0：中国移动，1：中国电信，2：中国联通，99：未知的异网手机号码
     */
    private String msisdntype;

    /**
     * 用户所属省份
     */
    private String province;

    /**
     * 认证方式
     */
    private String authtype;

    /**
     * 统一认证平台认证用户的时间
     */
    private String authtime;
    
    /**
     * 业务平台为该用户的最近一次报活时间
     */
    private String lastactivetime;

    /**
     * 用户在本业务平台的账号是否已经关联到统一账号，
     * 若已关联，与统一账号中手机/邮箱相同的业务账号不能再登录
     * 0：已经关联,1：未关联
     */
    private String relateToAndPassport;

    public String getUsessionid()
    {
        return usessionid;
    }

    public void setUsessionid(String usessionid)
    {
        this.usessionid = usessionid;
    }

    public String getPassid()
    {
        return passid;
    }

    public void setPassid(String passid)
    {
        this.passid = passid;
    }

    public String getAndid()
    {
        return andid;
    }

    public void setAndid(String andid)
    {
        this.andid = andid;
    }

    public String getMsisdn()
    {
        return msisdn;
    }

    public void setMsisdn(String msisdn)
    {
        this.msisdn = msisdn;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getLoginidtype()
    {
        return loginidtype;
    }

    public void setLoginidtype(String loginidtype)
    {
        this.loginidtype = loginidtype;
    }

    public String getMsisdntype()
    {
        return msisdntype;
    }

    public void setMsisdntype(String msisdntype)
    {
        this.msisdntype = msisdntype;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getAuthtype()
    {
        return authtype;
    }

    public void setAuthtype(String authtype)
    {
        this.authtype = authtype;
    }

    public String getAuthtime()
    {
        return authtime;
    }

    public void setAuthtime(String authtime)
    {
        this.authtime = authtime;
    }

    public String getLastactivetime()
    {
        return lastactivetime;
    }

    public void setLastactivetime(String lastactivetime)
    {
        this.lastactivetime = lastactivetime;
    }

    public String getRelateToAndPassport()
    {
        return relateToAndPassport;
    }

    public void setRelateToAndPassport(String relateToAndPassport)
    {
        this.relateToAndPassport = relateToAndPassport;
    }

}

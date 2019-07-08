/**
 * 文件名：AttendLoginRes.java
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

import richinfo.attendance.common.ResBean;

/**
 * 功能描述：
 * 
 */
public class AttendLoginRes extends ResBean
{

    private static final long serialVersionUID = -5060239766427940142L;

    /**
     * 用户会话id
     */
    private String usessionid;

    /**
     * 用户会话id
     */
    private String phone;

    /**
     * 登录操作后跳转url
     */
    private String jumpUrl;

    /**
     * 用户标识 0标识普通员工 1表示管理员
     */
    private String status;

    /**
     * 炒鸡管理员标识 1是0否
     */
    private String AmStatus;

    /**
     * 用户首次登录标识 0否1是
     */
    private String firstLogin;

    /**
     * 审批员标识 0否 1是
     */
    private int examinerState;

    /**
     * 白名单用户标识  0否 1是
     * 默认 否0
     */
    private int whitelistStatus = 0;

    /**
     * 角色类型 1为考勤组管理员
     * 默认 0 普通员工
     */
    private int roleType;

    public String getAmStatus() {
        return AmStatus;
    }

    public void setAmStatus(String amStatus) {
        AmStatus = amStatus;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 用户contactId

     */
    private String contactId;

    /**
     * 用户name
     */
    private String name;

    //是否允许外勤打卡标志，0允许1不允许
    private String allowOutRangeClock;

    public String getAllowOutRangeClock() {
        return allowOutRangeClock;
    }

    public void setAllowOutRangeClock(String allowOutRangeClock) {
        this.allowOutRangeClock = allowOutRangeClock;
    }

    private String enterName;

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public int getWhitelistStatus() {
        return whitelistStatus;
    }

    public void setWhitelistStatus(int whitelistStatus) {
        this.whitelistStatus = whitelistStatus;
    }

    public int getExaminerState()
    {
        return examinerState;
    }

    public void setExaminerState(int examinerState)
    {
        this.examinerState = examinerState;
    }

    public String getUsessionid()
    {
        return usessionid;
    }

    public void setUsessionid(String usessionid)
    {
        this.usessionid = usessionid;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getJumpUrl()
    {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl)
    {
        this.jumpUrl = jumpUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getFirstLogin()
    {
        return firstLogin;
    }

    public void setFirstLogin(String firstLogin)
    {
        this.firstLogin = firstLogin;
    }

}

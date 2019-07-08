/**
 * 文件名：AttendLoginReq.java
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

/**
 * 功能描述： 登录会话请求bean
 */
public class AttendLoginReq extends AttendReqBean
{
    private static final long serialVersionUID = -247396783390636309L;

    /** 管理员标识 1是0否 */
    private String isAdmin;

    /** 第一部门id 大型企业用这个参数替代企业id */
    private String firstDeptId;

    /** 统一认证凭证校验请求参数：操作类型 */
    private String optype;

    /** 统一认证凭证校验请求参数：业务平台编码 */
    private String sourceid;
    /** 统一认证凭证校验请求参数：通行证号 */
    private String passid;
    /** 统一认证凭证校验请求参数：临时凭证 */
    private String artifact;
    /** 统一认证凭证校验请求参数：请求时间戳,时间格式为yyyyMMddHHmmssSSS */
    private String reqtime;
    /** 统一认证凭证校验请求参数：检验码,sha-1(optype+sourceid+passid+artifact+reqtime+双方约定的密钥) */
    private String check;

    private  String contactId;

    //超级管理员标识 1是0否
    private  String aM;

    public String getaM() {
        return aM;
    }

    public void setaM(String aM) {
        this.aM = aM;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getOptype()
    {
        return optype;
    }

    public void setOptype(String optype)
    {
        this.optype = optype;
    }

    public String getSourceid()
    {
        return sourceid;
    }

    public void setSourceid(String sourceid)
    {
        this.sourceid = sourceid;
    }

    public String getPassid()
    {
        return passid;
    }

    public void setPassid(String passid)
    {
        this.passid = passid;
    }

    public String getArtifact()
    {
        return artifact;
    }

    public void setArtifact(String artifact)
    {
        this.artifact = artifact;
    }

    public String getReqtime()
    {
        return reqtime;
    }

    public void setReqtime(String reqtime)
    {
        this.reqtime = reqtime;
    }

    public String getCheck()
    {
        return check;
    }

    public void setCheck(String check)
    {
        this.check = check;
    }

    public String getIsAdmin()
    {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin)
    {
        this.isAdmin = isAdmin;
    }

    public String getFirstDeptId()
    {
        return firstDeptId;
    }

    public void setFirstDeptId(String firstDeptId)
    {
        this.firstDeptId = firstDeptId;
    }

}

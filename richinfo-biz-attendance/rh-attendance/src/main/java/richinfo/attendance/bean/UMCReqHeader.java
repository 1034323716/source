/**
* 文件名：UMCHeader.java
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

import richinfo.attendance.common.BeanObject;

/**
 * 功能描述：
 * 统一认证UMC请求体head
 */
public class UMCReqHeader  extends BeanObject implements Serializable
{

    private static final long serialVersionUID = 2700906660586525080L;
    
    /**
     * 版本号,初始版本号1.0,有升级后续调整
     */
    private String version;
    
    /**
     * 使用UUID标识请求的唯一性
     */
    private String msgid;
    /**
     * 请求消息发送的系统时间，精确到毫秒，共17位，格式：20121227180001165
     */
    private String systemtime;
    /**
     * 统一认证sourceId
     */
    private String sourceid;

    private String id;
    private String idtype;
    
    /**
     * 业务平台编码定义 1BOSS、2web 、3wap 、4pc客户端 、5手机客户端
     */
    private String apptype;

    private String sign;
    

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getMsgid()
    {
        return msgid;
    }

    public void setMsgid(String msgid)
    {
        this.msgid = msgid;
    }

    public String getSystemtime()
    {
        return systemtime;
    }

    public void setSystemtime(String systemtime)
    {
        this.systemtime = systemtime;
    }

    public String getSourceid()
    {
        return sourceid;
    }

    public void setSourceid(String sourceid)
    {
        this.sourceid = sourceid;
    }

    public String getApptype()
    {
        return apptype;
    }

    public void setApptype(String apptype)
    {
        this.apptype = apptype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}

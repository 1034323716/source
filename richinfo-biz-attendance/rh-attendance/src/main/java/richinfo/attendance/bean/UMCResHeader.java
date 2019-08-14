/**
* 文件名：UMCResHeader.java
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
 * 功能描述：
 *  统一认证UMC返回报文head
 */
public class UMCResHeader extends BeanObject implements Serializable
{
    private static final long serialVersionUID = -3983164447840774903L;
    
    /**
     * 版本号,初始版本号1.0,有升级后续调整
     */
    private String version;
 
    /**
     * 对应的请求消息中的msgid  标识请求的唯一性
     */
    private String inresponseto;
    
    /**
     * 请求消息发送的系统时间，精确到毫秒，共17位，格式：20121227180001165
     */
    private String systemtime;
    
    /**
     * 根据实际情况在每对消息中自行定义，响应消息头中的resultcode取值如下：
     * 103000:成功
     * 103113:报文格式错误
     * 103114:ks过期
     * 103115:ks不存在
     * 103116:sqn错误
     * 103117:mac错误
     * 103121:平台用户不存在
     * 103122:btid不存在
     * 103123:业务验证缓存用户不存在
     * 
     */
    private String resultcode;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getInresponseto()
    {
        return inresponseto;
    }

    public void setInresponseto(String inresponseto)
    {
        this.inresponseto = inresponseto;
    }

    public String getSystemtime()
    {
        return systemtime;
    }

    public void setSystemtime(String systemtime)
    {
        this.systemtime = systemtime;
    }

    public String getResultcode()
    {
        return resultcode;
    }

    public void setResultcode(String resultcode)
    {
        this.resultcode = resultcode;
    }

    @Override
    public String toString(){
        StringBuilder sb =new StringBuilder();
        sb.append("version=")
            .append(version)
            .append(", inresponseto=")
            .append(inresponseto)
            .append(", systemtime=")
            .append(systemtime)
            .append(", resultcode=")
            .append(resultcode);
        return sb.toString();
    }
}

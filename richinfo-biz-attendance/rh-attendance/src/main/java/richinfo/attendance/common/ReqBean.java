package richinfo.attendance.common;

import java.io.Serializable;

import richinfo.basemodel.validate.annotation.IgnoreBinding;

/**
 * 功能描述：请求服务的参数基类
 * 
 */
public class ReqBean extends BeanObject implements Serializable
{
    private static final long serialVersionUID = 1799739638005660598L;
    /** 请求标记 */
    @IgnoreBinding
    private String cguid;

    /** 手机号码 */
    @IgnoreBinding
    private String userNumber;
    /** 登录号码 */
    @IgnoreBinding
    private String loginName;

    /** 客户端ip */
    @IgnoreBinding
    private String clientIP;

    /** 服务器ip */
    @IgnoreBinding
    private String proxyIP;

    /** 渠道来源 */
    @IgnoreBinding
    private int comfromID;

    public String getUserNumber()
    {
        return userNumber;
    }

    public void setUserNumber(String userNumber)
    {
        this.userNumber = userNumber;
    }

    /**
     * @return the clientIP
     */
    public String getClientIP()
    {
        return clientIP;
    }

    /**
     * @param clientIP the clientIP to set
     */
    public void setClientIP(String clientIP)
    {
        this.clientIP = clientIP;
    }

    /**
     * @return the proxyIP
     */
    public String getProxyIP()
    {
        return proxyIP;
    }

    /**
     * @param proxyIP the proxyIP to set
     */
    public void setProxyIP(String proxyIP)
    {
        this.proxyIP = proxyIP;
    }

    /**
     * @return the comfromID
     */
    public int getComfromID()
    {
        return comfromID;
    }

    /**
     * @param comfromID the comfromID to set
     */
    public void setComfromID(int comfromID)
    {
        this.comfromID = comfromID;
    }
    public String getCguid()
    {
        return cguid;
    }
    public void setCguid(String cguid)
    {
        this.cguid = cguid;
    }
    public String getLoginName()
    {
        return loginName;
    }
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

}

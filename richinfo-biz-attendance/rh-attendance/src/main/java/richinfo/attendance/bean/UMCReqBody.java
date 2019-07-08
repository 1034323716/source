/**
* 文件名：UMCBody.java
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
 * 统一认证UMC请求体body
 */
public class UMCReqBody extends BeanObject implements Serializable
{

    private static final long serialVersionUID = 4795743449533155973L;
    /**
     * 单点登录会话token
     */
    private String token;
    
    public String getToken()
    {
        return token;
    }
    public void setToken(String token)
    {
        this.token = token;
    }
    
}

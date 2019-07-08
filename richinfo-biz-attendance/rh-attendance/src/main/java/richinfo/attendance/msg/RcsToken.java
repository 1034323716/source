/**
 * 文件名：RcsToken.java
 * 创建日期： 2018年1月2日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 *  1.修改时间：2018年1月2日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.msg;

import java.io.Serializable;

/**
 * 功能描述：和飞信token
 *
 */
public class RcsToken implements Serializable
{

    private static final long serialVersionUID = 6346988428495075114L;

    private String accessToken;

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

}
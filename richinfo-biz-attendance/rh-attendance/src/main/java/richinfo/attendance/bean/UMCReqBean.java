/**
* 文件名：UMCReqBean.java
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
 * 功能描述： UMC统一认证 token校验请求bean
 */
public class UMCReqBean extends BeanObject implements Serializable
{
    private static final long serialVersionUID = 2884926289979066661L;

    /**
     * UMC统一认证token校验请求head
     */
    private UMCReqHeader header;

    /**
     * UMC统一认证token校验请求body
     */
    private UMCReqBody body;

    public UMCReqHeader getHeader()
    {
        return header;
    }

    public void setHeader(UMCReqHeader header)
    {
        this.header = header;
    }

    public UMCReqBody getBody()
    {
        return body;
    }

    public void setBody(UMCReqBody body)
    {
        this.body = body;
    }

}

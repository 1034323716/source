/**
* 文件名：UMCResBean.java
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
 * 
 */
public class UMCResBean extends BeanObject implements Serializable
{

    private static final long serialVersionUID = -3296657429076718763L;
    
    /**
     * UMC统一认证token校验请求head
     */
    private UMCResHeader header;

    /**
     * UMC统一认证token校验请求body
     */
    private UMCResBody body;

    public UMCResHeader getHeader()
    {
        return header;
    }

    public void setHeader(UMCResHeader header)
    {
        this.header = header;
    }

    public UMCResBody getBody()
    {
        return body;
    }

    public void setBody(UMCResBody body)
    {
        this.body = body;
    }

}

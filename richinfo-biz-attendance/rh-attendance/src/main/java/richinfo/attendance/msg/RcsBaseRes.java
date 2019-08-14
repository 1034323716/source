/**
 * 文件名：RcsBaseRes.java
 * 创建日期： 2018年1月2日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 *   1.修改时间：2018年1月2日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.msg;

import java.io.Serializable;

/**
 * 功能描述：和飞信基础返回实体
 *
 */
public class RcsBaseRes implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -5197151466829745773L;

    /**
     * 请求处理状态码
     */
    private int code;

    /**
     * 请求处理状态码说明
     */
    private String msg;

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

}
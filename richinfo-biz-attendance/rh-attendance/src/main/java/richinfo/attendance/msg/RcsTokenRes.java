/**
 * 文件名：RcsTokenRes.java
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

/**
 * 功能描述：和飞信token返回实体
 *
 */
public class RcsTokenRes extends RcsBaseRes
{

    private static final long serialVersionUID = 7256096710768965272L;

    /**
     * 返回业务数据结果集合
     */
    private RcsToken data;

    public RcsToken getData()
    {
        return data;
    }

    public void setData(RcsToken data)
    {
        this.data = data;
    }

}
/**
 * 文件名：AttendExportReptRes.java
 * 创建日期： 2018年2月11日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年2月11日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;

/**
 * 功能描述：PC端导出报表返回Bean
 *
 */
public class AttendExportReptRes extends ResBean
{
    private static final long serialVersionUID = 7199571356702449223L;

    /** 下载URL地址 */
    private String url;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}

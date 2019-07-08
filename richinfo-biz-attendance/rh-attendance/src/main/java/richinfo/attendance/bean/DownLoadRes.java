/**
 * 文件名：DownLoadRes.java
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
 * 功能描述：考勤下载返回实体Bean
 *
 */
public class DownLoadRes extends ResBean
{
    private static final long serialVersionUID = 2276333062984695989L;

    /** 文件数据 */
    private byte[] bytes;

    /** 文件名称 */
    private String fileName;

    /** 文件路径 */
    private String filePath;

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public void setBytes(byte[] bytes)
    {
        this.bytes = bytes;
    }
}

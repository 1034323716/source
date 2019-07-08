/**
 * 文件名：AttendDownLoadService.java
 * 创建日期： 2018年2月11日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年2月11日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.service;

import richinfo.attendance.bean.DownLoadRes;

/**
 * 功能描述：考勤报表下载接口
 *
 */
public interface AttendDownLoadService
{
    /**
     * 考勤报表下载
     * @param contendDirId
     * @param fileName
     * @param clientIp
     * @return
     */
    public DownLoadRes getDownLoadResource(String contendDirId,
        String fileName, String clientIp);

}



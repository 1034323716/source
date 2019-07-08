/**
 * 文件名：DownLoadTempFileDelTask.java
 * 创建日期： 2018年2月12日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年2月12日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.task;

import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.attendance.util.DeleteFileUtil;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

/**
 * 功能描述：定时任务删除文件下载产生的临时文件
 *
 */
public class DownLoadTempFileDelTask extends Task
{
    @Override
    public void repeat(TaskContext context) throws TaskException
    {
        long start = System.currentTimeMillis();
        // 判断是否为定时任务机器，保证只有一台机器执行定时任务
        if (!AttendanceConfig.getInstance().isTaskServer())
        {
            logger.info("not task server,ignore DownLoadTempFileDelTask.");
            return;
        }
        String cguid = AttendanceUtil.getCguid();
        logger.info("start DownLoadTempFileDelTask. cguid={}", cguid);
        try
        {
            DeleteFileUtil.getInstace().deleteFile(cguid);
            logger.info(
                "downLoadTempFileDelTask execute success,useTime={}|cguid={}",
                System.currentTimeMillis() - start, cguid);
        }
        catch (Exception e)
        {
            logger.error("downLoadTempFileDelTask execute failed,cguid={}",
                cguid, e);
        }
    }
}

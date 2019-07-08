/**
 * 文件名：MessageTask.java
 * 创建日期： 2018年1月2日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月2日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.task;

import richinfo.attendance.service.MessageService;
import richinfo.attendance.service.impl.MessageServiceImpl;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

/**
 * 功能描述：待发消息任务
 *
 */
public class MessageTask extends Task
{

    private MessageService messageService = new MessageServiceImpl();

    @Override
    public void repeat(TaskContext context) throws TaskException
    {
        long start = System.currentTimeMillis();
        // 判断是否为定时任务机器，保证只有一台机器执行定时任务
        if (!AttendanceConfig.getInstance().isTaskServer())
        {
            logger.info("not task server,ignore MessageTask.");
            return;
        }

        String cguid = AttendanceUtil.getCguid();
        logger.info("Start MessageTask. cguid={}", cguid);

        boolean flag = messageService.prepareMessage(cguid);

        logger.info("end MessageTask success={}|cguid={}|useTime={}", flag,
            cguid, AttendanceUtil.getUseTime(start));
    }

}

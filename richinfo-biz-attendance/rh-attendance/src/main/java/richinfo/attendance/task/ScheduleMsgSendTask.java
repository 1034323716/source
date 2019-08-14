/**
 * 文件名：ScheduleMsgSendTask.java
 * 创建日期： 2018年1月10日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月10日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.task;

import richinfo.attendance.service.MessageService;
import richinfo.attendance.service.impl.MessageServiceImpl;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

/**
 * 功能描述：定时消息发送任务
 *
 */
public class ScheduleMsgSendTask extends Task
{
    private MessageService messageService = new MessageServiceImpl();

    @Override
    public void repeat(TaskContext context) throws TaskException
    {
        // 发送定时消息
        messageService.sendScheduleMsg();
    }

}

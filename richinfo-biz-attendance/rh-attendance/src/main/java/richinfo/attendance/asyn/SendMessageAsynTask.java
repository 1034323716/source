/**
 * 文件名：SendMessageAsynTask.java
 * 创建日期： 2018年1月11日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月11日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.asyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.entity.Message;
import richinfo.attendance.service.MessageService;
import richinfo.attendance.service.impl.MessageServiceImpl;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.bcomponet.asyn.task.AsynTask;

import java.util.List;

/**
 * 功能描述：发送消息异步任务
 *
 */
public class SendMessageAsynTask implements AsynTask<String>
{

    private final Logger logger = LoggerFactory
        .getLogger(SendMessageAsynTask.class);

    /** 待发消息 */
    private List<Message> list;
    private String cguid;

    private MessageService messageService = new MessageServiceImpl();

    public SendMessageAsynTask(List<Message> list, String cguid)
    {
        this.list = list;
        this.cguid = cguid;
        this.task();
    }

    @Override
    public String task()
    {
        long startTime = System.currentTimeMillis();
        logger.info("SendMessageAsynTask start. cguid={}|list.size()={}",
            cguid, list.size());

        messageService.handleMsgSend(list, cguid);

        logger.info(
            "SendMessageAsynTask end. cguid={}|list.size()={}|useTime={}",
            cguid, list.size(), AttendanceUtil.getUseTime(startTime));

        return null;
    }

}

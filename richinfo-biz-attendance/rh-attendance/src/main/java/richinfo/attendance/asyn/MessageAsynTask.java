/**
 * 文件名：MessageAsynTask.java
 * 创建日期： 2018年1月2日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月2日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.asyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.service.MessageService;
import richinfo.attendance.service.impl.MessageServiceImpl;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.bcomponet.asyn.task.AsynTask;

/**
 * 功能描述：以考勤组为单位生成需要进行打卡提醒的待发消息
 *
 */
public class MessageAsynTask implements AsynTask<String>
{

    private final Logger logger = LoggerFactory
        .getLogger(MessageAsynTask.class);

    /** 考勤组 */
    private AttendGroup attendGroup;
    private String cguid;

    private MessageService messageService = new MessageServiceImpl();

    public MessageAsynTask(AttendGroup attendGroup, String cguid)
    {
        this.attendGroup = attendGroup;
        this.cguid = cguid;
        this.task();
    }

    @Override
    public String task()
    {
        long start = System.currentTimeMillis();
        logger.info("MessageAsynTask start. cguid={}|group={}", cguid,
            attendGroup);

        messageService.handlePrepareMessage(attendGroup, cguid);

        logger.info("MessageAsynTask end. cguid={}|useTime={}|group={}", cguid,
            AttendanceUtil.getUseTime(start), attendGroup);

        return null;
    }

}

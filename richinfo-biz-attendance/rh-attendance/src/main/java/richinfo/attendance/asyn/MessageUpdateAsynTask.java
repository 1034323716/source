/**
 * 文件名：MessageUpdateAsynTask.java
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
import richinfo.attendance.entity.MessageUpdateInfo;
import richinfo.attendance.service.MessageService;
import richinfo.attendance.service.impl.MessageServiceImpl;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.bcomponet.asyn.task.AsynTask;

/**
 * 功能描述：更新打卡提醒的待发消息（更新待发消息（创建/编辑考勤组））
 *
 */
public class MessageUpdateAsynTask implements AsynTask<String>
{

    private final Logger logger = LoggerFactory
        .getLogger(MessageUpdateAsynTask.class);

    /** 编辑、创建考勤组时，更新用户提醒消息的所需实体信息 */
    private MessageUpdateInfo info;

    private MessageService messageService = new MessageServiceImpl();

    public MessageUpdateAsynTask(MessageUpdateInfo info)
    {
        this.info = info;
    }

    @Override
    public String task()
    {
        long start = System.currentTimeMillis();
        logger.info("MessageUpdateAsynTask start. cguid={}|group={}|AttendanceId={}",
            info.getCguid(), info.getNewAttendGroup().getAttendanceName(),info.getNewAttendGroup().getAttendanceId());

        messageService.updatePrepareMessage(info);

        logger.info("MessageUpdateAsynTask end. cguid={}|useTime={}|group={}",
            info.getCguid(), AttendanceUtil.getUseTime(start),
            info.getNewAttendGroup().getAttendanceId());

        return null;
    }

}

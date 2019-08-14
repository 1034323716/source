/**
 * 文件名：HistoryMessageAsynTask.java
 * 创建日期： 2018年1月12日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月12日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.asyn;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.entity.HistoryMessage;
import richinfo.attendance.service.HistoryMessageService;
import richinfo.attendance.service.impl.HistoryMessageServiceImpl;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.bcomponet.asyn.task.AsynTask;

/**
 * 功能描述：存储历史消息任务
 *
 */
public class HistoryMessageAsynTask implements AsynTask<String>
{

    private final Logger logger = LoggerFactory
        .getLogger(HistoryMessageAsynTask.class);

    /** 待发消息 */
    private List<HistoryMessage> list;
    private String cguid;

    private HistoryMessageService historyMessageService = new HistoryMessageServiceImpl();

    public HistoryMessageAsynTask(List<HistoryMessage> list, String cguid)
    {
        this.list = list;
        this.cguid = cguid;
        task();
    }

    @Override
    public String task()
    {
        long startTime = System.currentTimeMillis();
        logger.info("HistoryMessageAsynTask start. cguid={}|size={}", cguid,
            list.size());

        historyMessageService.saveHistoryMessageList(list);

        logger.info("HistoryMessageAsynTask end. cguid={}|size={}|useTime={}",
            cguid, list.size(), AttendanceUtil.getUseTime(startTime));

        return null;
    }

}

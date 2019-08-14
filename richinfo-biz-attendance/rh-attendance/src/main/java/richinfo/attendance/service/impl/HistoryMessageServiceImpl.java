/**
 * 文件名：HistoryMessageServiceImpl.java
 * 创建日期： 2018年1月12日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月12日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.dao.HistoryMessageDao;
import richinfo.attendance.entity.HistoryMessage;
import richinfo.attendance.service.HistoryMessageService;
import richinfo.attendance.util.AttendanceUtil;

/**
 * 功能描述：历史消息管理服务实现
 *
 */
public class HistoryMessageServiceImpl implements HistoryMessageService
{
    private final Logger logger = LoggerFactory
        .getLogger(HistoryMessageServiceImpl.class);
    private HistoryMessageDao historyMessageDao = new HistoryMessageDao();

    @Override
    public void saveHistoryMessageList(List<HistoryMessage> list)
    {
        long start = System.currentTimeMillis();
        boolean flag = historyMessageDao.batchSaveHistoryMessage(list);
        logger.debug("saveHistoryMessageList success={}|size={}|useTime={}",
            flag, list.size(), AttendanceUtil.getUseTime(start));
    }

}

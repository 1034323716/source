/**
 * 文件名：RegisterQytxlAsynTask.java
 * 创建日期： 2018年6月10日
 * 作者：     黄学振
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 */
package richinfo.attendance.asyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.service.QytxlService;
import richinfo.attendance.service.impl.QytxlServiceImpl;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.bcomponet.asyn.task.AsynTask;

/**
 * 功能描述：注册企业通讯录回调异步
 *
 */
public class RegisterQytxlAsynTask implements AsynTask<String>
{

    private final Logger logger = LoggerFactory
        .getLogger(RegisterQytxlAsynTask.class);

    
    private String enterId;

    private QytxlService qytxlService = new QytxlServiceImpl();

    public RegisterQytxlAsynTask(String enterId)
    {
        this.enterId = enterId;
        this.task();
    }

    @Override
    public String task()
    {
        long startTime = System.currentTimeMillis();
        logger.info("RegisterQytxlAsynTask start. enterId={}",enterId);

        qytxlService.registerQytxl(enterId);

        logger.info(
            "RegisterQytxlAsynTask end. enterId={}|useTime={}",
            enterId, AttendanceUtil.getUseTime(startTime));

        return null;
    }

}

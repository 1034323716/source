/**
 * 文件名：HistoryMessageDao.java
 * 创建日期： 2018年1月12日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月12日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.dao;

import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.entity.HistoryMessage;
import richinfo.attendance.util.TimeUtil;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.attendance.util.AssertUtil;

/**
 * 功能描述：历史消息持久层
 *
 */
public class HistoryMessageDao extends BaseAttendanceDao
{
    private Logger log = LoggerFactory.getLogger(HistoryMessageDao.class);

    /**
     * 批量入库历史消息
     * @param list
     * @return
     */
    public boolean batchSaveHistoryMessage(List<HistoryMessage> list)
    {
        if (AssertUtil.isEmpty(list))
        {
            log.warn("batchSaveHistoryMessage failed,list is empty.");
            return false;
        }
        try
        {
            return attendanceDao.batchInsert(
                "attendance.batchSaveHistoryMessage", list);
        }
        catch (PersistException e)
        {
            log.warn("batchSaveHistoryMessage failed,list is empty.");
            log.error("batchSaveHistoryMessage error.", e);
            return false;
        }
    }

    /**
     * 查询历史日报发送数据
     * @param
     * @param
     * @return
     */
    public List<HistoryMessage> queryNotificationHistory(String sendTime, int msgType)  {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startSendTime", sendTime+" 00:00:00");
        //获取明天的日期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        String endSendTime = sdf.format(date);
        map.put("endSendTime", endSendTime+" 00:00:00");
        map.put("msgType", msgType);
        try {
            return attendanceDao.queryForList(
                    "attendance.queryNotificationHistory", map);
        } catch (PersistException e) {
            log.warn("queryNotificationHistory 查询历史推送消息失败");
            log.error("queryNotificationHistory error.", e);
            return null;
        }
    }

    public static void  main(String[]strings){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        String endSendTime = sdf.format(date);
        System.out.println(endSendTime);
    }

}

/**
 * 文件名：MessageService.java
 * 创建日期： 2018年1月2日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月2日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.service;

import java.util.List;

import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.entity.Message;
import richinfo.attendance.entity.MessageUpdateInfo;

/**
 * 功能描述：消息管理服务
 *
 */
public interface MessageService
{

    /**
     * 准备待发消息
     * @param cguid
     */
    boolean prepareMessage(String cguid);

    /**
     * 处理（准备待发消息）的逻辑
     * @param attendGroup
     * @param cguid
     */
    void handlePrepareMessage(AttendGroup attendGroup, String cguid);

    /**
     * 更新消息待发表中的推送目标数据（更新待发消息（创建/编辑考勤组））
     * @param messageUpdateInfo
     */
    void updatePrepareMessage(MessageUpdateInfo messageUpdateInfo);

    /**
     * 发送定时消息
     * @return
     */
    boolean sendScheduleMsg();

    /**
     * 处理消息
     * @param list
     * @param cguid
     */
    void handleMsgSend(List<Message> list, String cguid);

    void saveDailyPushMsg(List<AttendGroup>groupList);
}

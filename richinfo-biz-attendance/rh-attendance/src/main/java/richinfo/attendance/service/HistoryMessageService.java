/**
 * 文件名：HistoryMessageService.java
 * 创建日期： 2018年1月12日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月12日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.service;

import java.util.List;

import richinfo.attendance.entity.HistoryMessage;

/**
 * 功能描述：历史消息管理服务
 *
 */
public interface HistoryMessageService
{

    /**
     * 批量存储历史消息
     * @param list
     */
    void saveHistoryMessageList(List<HistoryMessage> list);

}

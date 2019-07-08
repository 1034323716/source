/**
 * 文件名：QytxlService.java
 * 创建日期： 2018年6月10日
 * 作者：     黄学振
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 */
package richinfo.attendance.service;

import richinfo.attendance.bean.RegisterCallBackResBean;

/**
 * 功能描述： 考勤系统登录接口类
 */
public interface QytxlService
{
    /**
     * 注册通讯录回调(所有历史企业)
     * @param req
     */
    public void registerAll();



    /**
     * 通过企业ID注册通讯录回调
     * @param enterId
     */
    public RegisterCallBackResBean register(String enterId,String method,String eventType);
    
    /**
     * 创建考勤组时注册通讯回调
     * @param enterId
     * @param method
     * @param eventType
     */
    public void registerQytxl(String enterId);

}

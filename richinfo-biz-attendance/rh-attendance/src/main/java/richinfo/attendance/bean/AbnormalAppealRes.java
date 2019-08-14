/**
 * 文件名：AbnormalAppealRes.java
 * 创建日期： 2017年10月17日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月17日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;

/**
 * 功能描述：
 *
 */
public class AbnormalAppealRes extends ResBean
{
    private static final long serialVersionUID = 7195854863750356374L;

    private long appealId;

    public long getAppealId()
    {
        return appealId;
    }

    public void setAppealId(long appealId)
    {
        this.appealId = appealId;
    }
}

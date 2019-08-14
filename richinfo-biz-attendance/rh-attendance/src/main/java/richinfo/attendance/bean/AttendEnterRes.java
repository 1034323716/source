/**
 * 文件名：AttendAppealReq.java
 * 创建日期： 2017年10月12日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月12日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import java.io.Serializable;
import java.util.Date;

import richinfo.attendance.common.BeanObject;

/**
 * 功能描述：
 *
 */
public class AttendEnterRes extends BeanObject implements Serializable
{
    private static final long serialVersionUID = -1302042355643250350L;

    /** 企业ID */
    private String enterId;

    /** 注册状态， 0：未注册，1：已注册*/
    private int status;
  
    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;
    
    private String eventType;
    

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEnterId() {
		return enterId;
	}

	public void setEnterId(String enterId) {
		this.enterId = enterId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "AttendEnterReq [enterId=" + enterId + ", status=" + status + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + "]";
	}

   
}

/**
* 文件名：RegisterCallBackResBean.java
* 创建日期： 2018年6月9日
* 作者：    黄学振
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
*/
package richinfo.attendance.bean;

import richinfo.attendance.common.BeanObject;

import java.io.Serializable;

/**
 * 功能描述：企业通讯录回调注册返回值Bean
 * 
 */
public class RegisterCallBackResBean extends BeanObject implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -3882230588702574146L;

	/**
     * 请求是否处理.同步成功：0;其它失败
     */
    private String error_code;
    
    /**
     * 企业ID
     */
    private String enterpriseId;
    
    /**
     * 如果error_code不为0时，返回错误描述
     */
    private String error_msg;
    
    private String eventType;
    
    

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	@Override
	public String toString() {
		return "RegisterCallBackResBean [error_code=" + error_code + ", enterpriseId=" + enterpriseId + ", error_msg="
				+ error_msg + "]";
	}

	
}

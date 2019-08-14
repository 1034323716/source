/**
 * 文件名：UmcArtifactValidRes.java
 * 创建日期： 2018年2月6日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年2月6日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;

/**
 * 功能描述：PC端校验凭证返回用户节点信息实体类
 *
 */
public class UmcGetArtifactRes extends ResBean
{
    private static final long serialVersionUID = -3260382677796127316L;
    
    /**对应的请求消息中的MSGID*/
    private String inResponseTo;
    /**请求消息发送的系统时间，精确到毫秒，共17位，格式：20121227180001165*/
    private String systemTime;
    /**处理状态编码，0：成功  1：用户不存在 */
    private int resultCode;
    /** 用户身份查询凭证  */
    private String artifact;
   
   
    public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}

	public String getInResponseTo() {
		return inResponseTo;
	}

	public void setInResponseTo(String inResponseTo) {
		this.inResponseTo = inResponseTo;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	@Override
    public String toString()
    {
        return "UmcGetArtifactRes [inResponseTo=" + inResponseTo + ", systemTime=" + systemTime
            + ", resultCode=" + resultCode + "]";
    }
}

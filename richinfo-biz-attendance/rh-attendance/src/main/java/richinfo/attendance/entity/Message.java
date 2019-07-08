/**
 * 文件名：Message.java
 * 创建日期： 2018年1月2日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月2日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.Date;

import richinfo.attendance.common.BeanObject;

/**
 * 功能描述：待发消息
 *
 */
public class Message extends BeanObject implements Serializable
{

    private static final long serialVersionUID = 7323298570195834929L;

    /** 消息ID */
    private long msgId;

    /** 考勤组ID */
    private long attendanceId;

    /** 企业ID */
    private String enterId;

    /** 企业联系人ID */
    private String uid;
    /*审批单id*/
    private long appealId=0;

    /** 当前应用ID */
    private String appId;

    /** 消息发送者 */
    private String sender;

    /** 消息标题 */
    private String topic;

    /** 消息内容 */
    private String content;

    /** 消息摘要 */
    private String summary;

    /** 机器编号（目前两个，1和2） */
    private int serverNo;

    /** 消息类型（1 签到提醒消息，2 签退提醒消息） */
    private int msgType;

    /** 创建时间 */
    private Date createTime;

    /** 发送时间 */
    private Date sendTime;

    /** 最后修改时间 */
    private Date modifyTime;

    public long getAppealId() {
        return appealId;
    }

    public void setAppealId(long appealId) {
        this.appealId = appealId;
    }

    public long getMsgId()
    {
        return msgId;
    }

    public void setMsgId(long msgId)
    {
        this.msgId = msgId;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public int getServerNo()
    {
        return serverNo;
    }

    public void setServerNo(int serverNo)
    {
        this.serverNo = serverNo;
    }

    public int getMsgType()
    {
        return msgType;
    }

    public void setMsgType(int msgType)
    {
        this.msgType = msgType;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getModifyTime()
    {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime)
    {
        this.modifyTime = modifyTime;
    }

    public String toSimpleString()
    {
        return "SimpleMessage [msgId=" + msgId + ", attendanceId="
            + attendanceId + ", uid=" + uid + ", topic=" + topic + ", msgType="
            + msgType + ", createTime=" + createTime + ", sendTime=" + sendTime
            + "]";
    }

    @Override
    public String toString()
    {
        return "Message [msgId=" + msgId + ", attendanceId=" + attendanceId
            + ", enterId=" + enterId + ", uid=" + uid + ", appId=" + appId
            + ", sender=" + sender + ", topic=" + topic + ", content="
            + content + ", summary=" + summary + ", serverNo=" + serverNo
            + ", msgType=" + msgType + ", createTime=" + createTime
            + ", sendTime=" + sendTime + ", modifyTime=" + modifyTime + "]";
    }

}

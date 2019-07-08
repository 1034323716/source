/**
 * 文件名：MessageUpdateInfo.java
 * 创建日期： 2018年1月19日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月19日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述:编辑、创建考勤组时，更新用户提醒消息的所需实体信息
 *
 */
public class MessageUpdateInfo implements Serializable
{

    private static final long serialVersionUID = 3264825587092585064L;

    /**
     * 原考勤组信息
     */
    private AttendGroup oldAttendGroup;

    /** 创建、编辑考勤组时传过来的考勤组信息实体 */
    private AttendGroup newAttendGroup;

    /** 是否是更新,true 编辑考勤组； false 创建考勤组 */
    private boolean isUpdate;

    /** 事物ID */
    private String cguid;

    /** 减少成员 */
    private List<AttendEmployee> decreaseMember;

    /** 更新的成员uid列表(update) */
    private List<AttendEmployee> updateIncrease;

    /** 新增的成员uid列表(insert) */
    private List<AttendEmployee> insertIncrease;
    
    /** 是否是删除考勤组,true 是； false 不是 */
    private boolean isDel;

    public MessageUpdateInfo()
    {
    }

    public MessageUpdateInfo(AttendGroup oldAttendGroup,
        AttendGroup newAttendGroup, boolean isUpdate, String cguid,
        List<AttendEmployee> decreaseMember,
        List<AttendEmployee> updateIncrease,
        List<AttendEmployee> insertIncrease, boolean isDel)
    {
        super();
        this.oldAttendGroup = oldAttendGroup;
        this.newAttendGroup = newAttendGroup;
        this.isUpdate = isUpdate;
        this.cguid = cguid;
        this.decreaseMember = decreaseMember;
        this.updateIncrease = updateIncrease;
        this.insertIncrease = insertIncrease;
        this.isDel = isDel;
    }

    public AttendGroup getOldAttendGroup()
    {
        return oldAttendGroup;
    }

    public void setOldAttendGroup(AttendGroup oldAttendGroup)
    {
        this.oldAttendGroup = oldAttendGroup;
    }

    public AttendGroup getNewAttendGroup()
    {
        return newAttendGroup;
    }

    public void setNewAttendGroup(AttendGroup newAttendGroup)
    {
        this.newAttendGroup = newAttendGroup;
    }

    public boolean isUpdate()
    {
        return isUpdate;
    }

    public void setUpdate(boolean isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    public String getCguid()
    {
        return cguid;
    }

    public void setCguid(String cguid)
    {
        this.cguid = cguid;
    }

    public List<AttendEmployee> getDecreaseMember()
    {
        return decreaseMember;
    }

    public void setDecreaseMember(List<AttendEmployee> decreaseMember)
    {
        this.decreaseMember = decreaseMember;
    }

    public List<AttendEmployee> getUpdateIncrease()
    {
        return updateIncrease;
    }

    public void setUpdateIncrease(List<AttendEmployee> updateIncrease)
    {
        this.updateIncrease = updateIncrease;
    }

    public List<AttendEmployee> getInsertIncrease()
    {
        return insertIncrease;
    }

    public void setInsertIncrease(List<AttendEmployee> insertIncrease)
    {
        this.insertIncrease = insertIncrease;
    }

    public boolean isDel()
    {
        return isDel;
    }

    public void setDel(boolean isDel)
    {
        this.isDel = isDel;
    }
    
}

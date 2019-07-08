/**
 * 文件名：AttendRes.java
 * 创建日期： 2017年6月5日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;
import richinfo.attendance.entity.AttendEntity;
import richinfo.attendance.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：考勤模块响应实体类
 * 
 */
public class AttendRes extends ResBean
{

    private static final long serialVersionUID = -8260571737042181899L;
    /** 当前页 */
    private int pageNo;
    /** 每页显示数据大小 */
    private int pageSize;
    /** 总数据量 */
    private int totalCount;

    public List<AttendEntity> attendRecord;

    public List<AttendClockVo> attendClockVos;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<AttendClockVo> getAttendClockVos() {
        return attendClockVos;
    }

    public void setAttendClockVos(List<AttendClockVo> attendClockVos) {
        this.attendClockVos = attendClockVos;
    }

    public List<AttendEntity> getAttendRecord()
    {
        return attendRecord;
    }

    public void setAttendRecord(List<AttendEntity> attendRecord)
    {
        this.attendRecord = attendRecord;
    }

    //上下班打卡状态  0代表上班  1代表下班
    private  int clockStatue = 0;

    public int getClockStatue() {
        return clockStatue;
    }

    public void setClockStatue(int clockStatue) {
        this.clockStatue = clockStatue;
    }

    /**
     * 添加单条记录对象到list中，打卡成功后需要返回该条记录
     * @param entity
     */
    public void addRecord(AttendEntity entity)
    {
        if (AssertUtil.isNull(attendRecord))
        {
            attendRecord = new ArrayList<AttendEntity>();
        }
        attendRecord.add(entity);
    }
    /**
     * 添加单条记录对象到list中，打卡成功后需要返回该条记录
     * @param
     */
    public void addClockVo(AttendClockVo attendClockVo)
    {
        if (AssertUtil.isNull(attendRecord))
        {
            attendClockVos = new ArrayList<AttendClockVo>();
        }
        attendClockVos.add(attendClockVo);
    }

    @Override
    public String toString() {
        return "AttendRes{" +
            "pageNo=" + pageNo +
            ", pageSize=" + pageSize +
            ", totalCount=" + totalCount +
            ", attendRecord=" + attendRecord +
            ", attendClockVos=" + attendClockVos +
            ", clockStatue=" + clockStatue +
            '}';
    }
}

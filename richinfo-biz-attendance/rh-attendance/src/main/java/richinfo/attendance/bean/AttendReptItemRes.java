/**
 * 文件名：AttendReptItemRes.java
 * 创建日期： 2018年1月24日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年1月24日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;
import richinfo.attendance.entity.EmployeeMonthDetail;

import java.util.List;

/**
 * 功能描述：报表优化团队日报分项统计返回实体
 *
 */
public class AttendReptItemRes extends ResBean
{
    private static final long serialVersionUID = -8998902413014416977L;

    private int pageNo;

    private int pageSize;

    private int totalCount;

    /** 新旧数据标识*/
    private int isNewData;

    private List<EmployeeMonthDetail> data;

    public int getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<EmployeeMonthDetail> getData()
    {
        return data;
    }

    public void setData(List<EmployeeMonthDetail> data)
    {
        this.data = data;
    }

    public int getIsNewData() {
        return isNewData;
    }

    public void setIsNewData(int isNewData) {
        this.isNewData = isNewData;
    }
}

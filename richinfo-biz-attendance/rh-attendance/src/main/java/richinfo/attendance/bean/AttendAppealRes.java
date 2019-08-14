/**
 * 文件名：AttendAppealRes.java
 * 创建日期： 2017年10月13日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月13日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import java.util.List;

import richinfo.attendance.common.ResBean;

/**
 * 功能描述：
 *
 */
public class AttendAppealRes extends ResBean
{
    private static final long serialVersionUID = -8259804919199410503L;

    private List<AttendAppealReq> data;

    private int pageNo;

    private int pageSize;

    private long totalCount;

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

    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<AttendAppealReq> getData()
    {
        return data;
    }

    public void setData(List<AttendAppealReq> data)
    {
        this.data = data;
    }
}

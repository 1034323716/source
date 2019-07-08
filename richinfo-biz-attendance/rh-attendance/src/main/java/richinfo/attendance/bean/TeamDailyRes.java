/**
 * 文件名：TeamDailyRes.java
 * 创建日期： 2017年6月8日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.bean;

import java.util.List;

import richinfo.attendance.entity.TeamDailyEntity;
import richinfo.attendance.common.ResBean;

/**
 * 功能描述：团队日报详情返回实体
 *
 */
public class TeamDailyRes extends ResBean
{
    private static final long serialVersionUID = 826794059504387955L;

    private int pageNo;

    private int pageSize;

    private long totalCount;

    private List<TeamDailyEntity> data;

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

    public List<TeamDailyEntity> getData()
    {
        return data;
    }

    public void setData(List<TeamDailyEntity> data)
    {
        this.data = data;
    }

    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    @Override
    public String toString()
    {
        return "TeamDailyRes [pageNo=" + pageNo + ", pageSize=" + pageSize
            + ", totalCount=" + totalCount + ", data=" + data + "]";
    }
}

/**
* 文件名：TeamMonthRes.java
* 创建日期： 2017年6月9日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年6月9日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.bean;

import java.util.List;

import richinfo.attendance.entity.TeamMonthEntity;
import richinfo.attendance.common.ResBean;

/**
 * 功能描述：
 * 团队月报数据查询返回bean
 */
public class TeamMonthRes extends ResBean
{

    private static final Long serialVersionUID = 6141235971119569573L;

    /** 分页显示，当前页 */
    private Integer pageNo;
    
    /** 分页显示，每页大小 */
    private Integer pageSize;
    
    /** 分页显示，总记录数*/
    private Integer totalCount;

    /** 分页显示，列表数据*/
    private List<TeamMonthEntity> data;

    public Integer getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(Integer pageNo)
    {
        this.pageNo = pageNo;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<TeamMonthEntity> getData()
    {
        return data;
    }

    public void setData(List<TeamMonthEntity> data)
    {
        this.data = data;
    }

}

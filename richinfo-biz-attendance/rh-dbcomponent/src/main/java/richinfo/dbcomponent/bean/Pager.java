/**
 * 文件名：Pager.java
 * 创建日期： 2014年4月17日
 * 作者：     tangguanfeng
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2014年4月17日
 *   修改人：tangguanfeng
 *   修改内容：
 */
package richinfo.dbcomponent.bean;

import java.util.List;

/**
 * 功能描述：分页器
 * 
 */
public class Pager<T> {
    /** 总记录数 */
    private long total;
    /** 每页大小 */
    private int pageSize;
    /** 当前页 */
    private int pageNo;
    /** 查询结果对象列表 */
    private List<T> beans;

    public Pager() {
    }

    public long getTotal() {
        if (total < 0) {
            total = 0;
        }
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        return pageNo;
    }

    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }

    public List<T> getBeans()
    {
        return beans;
    }

    public void setBeans(List<T> beans)
    {
        this.beans = beans;
    }

    public int getPageCount() {
        int count = 0;
        if (getTotal() > 0) {
            int mod = (int) getTotal() % getPageSize();
            count = (int) (getTotal() / getPageSize());
            if (mod > 0) {
                count++;
            }
        }
        return count;
    }

    public int getLimit()
    {
        return getPageSize();
    }

    public long getSkip()
    {
        return (getPageNo() - 1) * getPageSize();
    }
}

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

import java.util.List;

/**
 * 功能描述：考勤模块响应实体类
 * 
 */
public class AttendQueryRes extends ResBean
{
    private List resultList;

    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }
}

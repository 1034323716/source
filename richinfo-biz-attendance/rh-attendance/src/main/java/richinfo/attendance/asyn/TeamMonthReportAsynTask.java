/**
 * 文件名：TeamMonthReportAsynTask.java
 * 创建日期： 2017年6月9日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月9日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.asyn;

import java.util.List;

import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.entity.TeamMonthReportEntity;
import richinfo.bcomponet.asyn.task.AsynTask;

/**
 * 功能描述：
 *
 */
public class TeamMonthReportAsynTask implements AsynTask<String>
{
    private List<TeamMonthReportEntity> monthes;

    private AttendReportDao reportDao = new AttendReportDao();

    public TeamMonthReportAsynTask(List<TeamMonthReportEntity> monthes)
    {
        this.monthes = monthes;
        this.task();
    }

    @Override
    public String task()
    {
        reportDao.saveTeamMonthReport(monthes);
        return null;
    }

}

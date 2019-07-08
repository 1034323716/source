/**
 * 文件名：TeamDailyReportAsynTask.java
 * 创建日期： 2017年6月22日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月22日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.asyn;

import java.util.List;

import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.entity.TeamDailyReportEntity;
import richinfo.bcomponet.asyn.task.AsynTask;

/**
 * 功能描述：
 *
 */
public class TeamDailyReportAsynTask implements AsynTask<String>
{
    private List<TeamDailyReportEntity> list;

    private AttendReportDao reportDao = new AttendReportDao();

    public TeamDailyReportAsynTask(List<TeamDailyReportEntity> list)
    {
        this.list = list;
        this.task();

    }

    @Override
    public String task()
    {
        reportDao.saveTeamDailyInfo(list);
        return null;
    }
}

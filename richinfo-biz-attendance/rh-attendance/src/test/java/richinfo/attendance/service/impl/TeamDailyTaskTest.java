package richinfo.attendance.service.impl;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.asyn.TeamMonthReportAsynTask;
import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.entity.TeamMonthReportEntity;
import richinfo.attendance.task.TeamDailyTask;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.bcomponet.asyn.AsynTaskProcess;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.util.ArrayList;
import java.util.List;

public class TeamDailyTaskTest
{

	private AttendReportDao reportDao = new AttendReportDao();

    private Logger logger = LoggerFactory.getLogger(TeamDailyTask.class);
	
	@Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }
   

	
   
    
    
//    @Test
    public void testTeamMonthReportTaskAll()
    {
        
    	// 判断是否为定时任务机器，保证只有一台机器执行定时任务
        if (!AttendanceConfig.getInstance().isTaskServer())
        {
            logger.info("not task server,ignore TeamMonthReportTask.");
            return;
        }
        logger.info("start execute TeamMonthReportTask");
        String attendanceDate = "2018-06-06";
        String firstDate = "2018-06-01";

        List<String> enterIds = reportDao.queryAllEnterId();
        if (AssertUtil.isEmpty(enterIds))
        {
            logger.info("TeamMonthReportTask can't find enterIds");
            return;
        }
        for (String enterId : enterIds)
        {
            List<TeamMonthReportEntity> monthes = new ArrayList<TeamMonthReportEntity>();
            monthes = reportDao.teamMonthReportInfo(firstDate, attendanceDate,
                enterId);
            if (AssertUtil.isEmpty(monthes))
            {
                logger
                    .info(
                        "TeamMonthReportTask can't find report data,enterId={}|attendanceDate={}",
                        enterId, attendanceDate);
                continue;
            }
            // 暂时直接存储
            // reportDao.saveTeamMonthReport(monthes);
            // 异步将各企业数据入库
            AsynTaskProcess.asynExecTask(new TeamMonthReportAsynTask(monthes));
        }
        

    }

}

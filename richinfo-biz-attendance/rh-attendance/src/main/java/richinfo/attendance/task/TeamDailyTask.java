package richinfo.attendance.task;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.asyn.TeamDailyReportAsynTask;
import richinfo.attendance.dao.AttendGroupDao;
import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.dao.HistoryMessageDao;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.entity.HistoryMessage;
import richinfo.attendance.entity.TeamDailyReportEntity;
import richinfo.attendance.msg.Constants;
import richinfo.attendance.service.MessageService;
import richinfo.attendance.service.impl.MessageServiceImpl;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.attendance.util.TimeUtil;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

/**
 * 功能描述：团队日报统计定时任务
 *
 */
public class TeamDailyTask extends Task
{
    private AttendReportDao reportDao = new AttendReportDao();
    private HistoryMessageDao historyMessageDao = new HistoryMessageDao();
    private AttendGroupDao attendGroupDao = new AttendGroupDao();
    private MessageService messageService = new MessageServiceImpl();

    private Logger logger = LoggerFactory.getLogger(TeamDailyTask.class);

    private String attendanceDate;

    public TeamDailyTask() {
    }

    //有参构造复制统计时间
    public TeamDailyTask(String attendanceDate) {
        this.attendanceDate = attendanceDate;
        this.repeat();
    }

    @Override
    public void init(TaskContext context) throws TaskException
    {
    }

    @Override
    public void repeat(TaskContext context) throws TaskException
    {}

    public void repeat() {
        // 判断是否为定时任务机器，保证只有一台机器执行定时任务
        if (!AttendanceConfig.getInstance().isTaskServer())
        {
            logger.info("not task server,ignore TeamDailyTask.");
            return;
        }
        long start = System.currentTimeMillis();
        logger.info("start execute TeamDailyTask，attendanceDate={}",attendanceDate);
        
        List<String> enterIds = reportDao.queryAllEnterId();
        if (AssertUtil.isEmpty(enterIds))
        {
            logger.info("TeamDailyTask can't find enterIds");
            return;
        }
        //创建一个线程数默认为20的线程池
        ExecutorService fixedThreadPool  = Executors.newFixedThreadPool(AttendanceConfig.getInstance().getMultiThreadedPool());
        try {

            for (String enterId : enterIds)
            {
                // 统计日报数据
                List<TeamDailyReportEntity> list = reportDao.teamDailyReport(attendanceDate, enterId);
                if (AssertUtil.isEmpty(list))
                {
                    logger.info("TeamDailyTask can't find data,enterId={}", enterId);
                    continue;
                }
                //  logger.info("TeamDailyTask status data,list={}", list);
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        new TeamDailyReportAsynTask(list);
                    }
                });
                // reportDao.saveTeamDailyInfo(list);
                // 异步执行任务
                //AsynTaskProcess.asynExecTask(new TeamDailyReportAsynTask(list));

            }
            //任务执行完毕后   关闭多线程
            fixedThreadPool.shutdown();

            logger.info("end TeamDailyTask useTime={}",
                AttendanceUtil.getUseTime(start));
        }catch (Exception e){
            //发生异常直接回收关闭线程池
            fixedThreadPool.shutdownNow();
        }
        //团队日报推送 一天只推送一次 在当天第一次统计团队日报的时候
        //查询所有考勤组创建的管理者
        //查询今天是否存在推送日报
        logger.info("开始===保存日报信息推送消息=====" );
        List<HistoryMessage> msgList =  historyMessageDao.queryNotificationHistory(this.attendanceDate, Constants.MsgType.daily.getValue());
      //  logger.info("推送日报 msgList={} | attendanceDate={}|",msgList,attendanceDate);
        if(this.attendanceDate.equals(TimeUtil.formatDateTime(new Date(),TimeUtil.BASE_DATE_FORMAT))
                && AssertUtil.isEmpty(msgList)) {
            //查询考勤组创建者信息UID、enterID
            List<AttendGroup>groupList = attendGroupDao.queryGroupCreator();
            if (AssertUtil.isEmpty(groupList))
            {
                logger.info("查询考勤组创建者信息==================无");
                return;
            }
                        //封装保存日报信息
            messageService.saveDailyPushMsg(groupList);

        }
        logger.info("保存日报信息推送消息===完成=====" );


    }
}

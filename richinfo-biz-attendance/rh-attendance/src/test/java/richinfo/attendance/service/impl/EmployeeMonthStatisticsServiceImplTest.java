package richinfo.attendance.service.impl;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.junit.Before;
import richinfo.attendance.asyn.EmployeeMonthDetailAsynTask;
import richinfo.attendance.dao.AttendCalendarDao;
import richinfo.attendance.dao.AttendGroupDao;
import richinfo.attendance.entity.AttendCalendar;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.entity.AttendGroup.AttendType;
import richinfo.attendance.service.EmployeeMonthStatisticsService;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.TimeUtil;
import richinfo.bcomponet.asyn.AsynTaskProcess;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EmployeeMonthStatisticsServiceImplTest
{

	private AttendGroupDao groupDao = new AttendGroupDao();
	private AttendCalendarDao calendarDao = new AttendCalendarDao();
	
	
	@Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }
   

//	@Test
    public void testEmployeeMonthDatailStatistics()
    {
        // 考勤组信息
        AttendGroup attendGroup = new AttendGroup();
        attendGroup.setAttendanceId(101627L);
        attendGroup.setEnterId("483454");
        attendGroup.setAmTime("08:51-09:21");
        attendGroup.setPmTime("09:51-17:30");
        attendGroup.setAttendanceName("排班，夸天");
        String  ss = "{\"1\":{\"amTime\":\"10:00-12:00\",\"pmTime\":\"13:00-15:00\"},\"2\":{\"amTime\":\"10:00-12:00\",\"pmTime\":\"13:00-15:00\"},\"3\":{\"amTime\":\"10:00-12:00\",\"pmTime\":\"13:00-15:00\"},\"4\":{\"amTime\":\"10:00-12:00\",\"pmTime\":\"13:00-15:00\"},\"5\":{\"amTime\":\"10:00-12:00\",\"pmTime\":\"13:00-15:00\"}}";
        attendGroup.setFixedAttendRule(ss);
        JSONObject jsonObject = JSONObject.fromObject(ss);
        Map map = JSON.parseObject(ss);
        System.out.println(map);
        attendGroup.setAttendType(1);
        attendGroup.setRelyHoliday(0);
        attendGroup.setAllowLateTime(5);

        // 日期信息
        AttendCalendar attendCalendar = new AttendCalendar();
        Calendar cal = Calendar.getInstance();
        cal.set(2018, 5, 5);
        Date date = cal.getTime();
        attendCalendar.setCalendarDate(date);
        attendCalendar.setRemark("工作日");
        attendCalendar.setStatus(0); 
        attendCalendar.setWeek("星期二");

        // 员工个人月报统计
        EmployeeMonthStatisticsService service = new EmployeeMonthStatisticsServiceImpl(
            attendGroup, attendCalendar);
//        service.employeeMonthDatailStatistics();
    }
    
    
//    @Test
    public void testEmployeeMonthDatailStatisticsAll()
    {
        // 考勤组信息
    	// 查询所有有效考勤组
        List<AttendGroup> allGroups = groupDao.queryAllNormalAttendGroup();
     // 统计昨天考勤数据
        String dateTest = "2018-01-02";
        while(!dateTest.equals("2018-02-01")){
        	Date yesterday = TimeUtil.string2Date(dateTest,"yyyy-MM-dd");
            AttendCalendar attendCalendar = calendarDao
                .queryAttendCalendarByDate(yesterday);
            String qiantian = TimeUtil.minusDays(dateTest, 1);
            Date beforeYesterday = TimeUtil.string2Date(qiantian,"yyyy-MM-dd");
            AttendCalendar beforeAttendCalendar = calendarDao
                .queryAttendCalendarByDate(beforeYesterday);
            if (AssertUtil.isEmpty(attendCalendar))
            {
                return;
            }
            // 循环统计各个考勤组
            for (AttendGroup group : allGroups)
            {
            	if(group.getAttendType()==AttendType.Schedule.getValue())
            	{
            		AsynTaskProcess.asynExecTask(new EmployeeMonthDetailAsynTask(group,
            				beforeAttendCalendar));
            	}else
            	{
            		// 以考勤组为单位统计考勤状态：便于判断考勤状态，避免互相影响
                    AsynTaskProcess.asynExecTask(new EmployeeMonthDetailAsynTask(group,
                        attendCalendar));
            	}
                
                try
                {
                    //TODO 瞬间并发太高，导致数据库处理不过来，临时加一个延时间隔
                    Thread.sleep(100);
                }
                catch (Exception e)
                {
                }
            }
            dateTest = TimeUtil.plusDays(dateTest, 1);
        }
    
        

       
    }
    
   
    public static void main(String[] args)
    {
    	EmployeeMonthStatisticsServiceImplTest test = new EmployeeMonthStatisticsServiceImplTest();
    	test.testEmployeeMonthDatailStatistics();
    }

}

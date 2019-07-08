package richinfo.attendance.dao;

import org.junit.Before;
import org.junit.Test;
import richinfo.attendance.bean.AttendReportReq;
import richinfo.attendance.common.DaoObject;
import richinfo.attendance.entity.*;
import richinfo.attendance.task.TeamDailyTask;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.EnterpriseUtil;
import richinfo.attendance.util.TimeUtil;
import richinfo.bcomponet.tasks.exception.TaskException;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.util.*;

import static richinfo.attendance.util.TimeUtil.BASE_DATE_FORMAT_YYYY_MM;

public class AttendReportDaoTest extends DaoObject{
    private AttendReportDao dao = new AttendReportDao();

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();

    private AttendWhitelistDao attendWhitelistDao = new AttendWhitelistDao();

    @Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

//    @Test
    public void setList(){
        List<AttendWhitelistEntity> list = new ArrayList<>();

        AttendWhitelistEntity entity = new AttendWhitelistEntity();

        entity.setUid("123");
        entity.setContactId("123");
        entity.setEmployeeName("ceshi123");
        entity.setPhone("123");
        entity.setEnterId("123");
        entity.setEnterName("123");
        entity.setDeptId("123");
        entity.setDeptName("123");
        entity.setEmail("123");
        entity.setPosition("123");
        //考勤人员白名单标识 设置为0
        entity.setStatus(0);
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        entity.setCreator("123");
        entity.setCreatorId("123");

        list.add(entity);

//        attendWhitelistDao.setGlobalWhiteList(list);
    }

  //  @Test
    public void testQueryTeamDailyInfo() throws Exception
    {
        TeamDailyEntity t = new TeamDailyEntity();
        t.setAttendanceDate("2018-10-23");
        t.setEnterId("7479603");
//        List<TeamDailyEntity> tt = dao.queryTeamDailyInfo(t, new UserInfo());
        System.out.println("----------------");
//        for (TeamDailyEntity ts : tt)
//        {
//            System.out.println(ts);
//            System.out.println(ts.getRegionStatus());
//            System.out.println(TimeUtil.long2Date(ts.getEarlyTimes(),
//                "yyyy-MM-dd HH:mm:ss"));
//            System.out.println(TimeUtil.long2Date(ts.getLastTimes(),
//                "yyyy-MM-dd HH:mm:ss"));
//        }
        System.out.println("------------------");
    }

//    @Test
    public void testQueryAllEnterId()
    {
        List<String> list = dao.queryAllEnterId();
        for (String l : list)
        {
            System.out.println(l);
        }
    }

    // @Test
    public void testTeamMonthReportInfo()
    {
        List<TeamMonthReportEntity> monthes = dao.teamMonthReportInfo(
            "2017-07-01", "2017-07-20", "483460");
        System.out.println("-------------------------");
        for (TeamMonthReportEntity l : monthes)
        {
            System.out.println(l);
        }
        System.out.println("--------------------------");
    }

//    @Test
    public void testSaveTeamMonthReport()
    {
        // 获取当前月第一天：
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        String firstDate = TimeUtil.date2String(calendar.getTime(),
            TimeUtil.BASE_DATE_FORMAT);

        // 获取当前系统时间的前一天时间
        Calendar calendarY = Calendar.getInstance();
        calendarY.setTime(new Date());
        calendarY.add(Calendar.DAY_OF_MONTH, -1);
        String attendanceDate = TimeUtil.date2String(calendarY.getTime(),
            TimeUtil.BASE_DATE_FORMAT);

        List<String> enterIds = dao.queryAllEnterId();
        if (AssertUtil.isEmpty(enterIds))
        {
            return;
        }
        for (String enterId : enterIds)
        {
            List<TeamMonthReportEntity> monthes = new ArrayList<TeamMonthReportEntity>();
            monthes = dao.teamMonthReportInfo(firstDate, attendanceDate,
                enterId);
            if (AssertUtil.isEmpty(monthes))
            {
                continue;
            }
            // 异步将各企业数据入库
            dao.saveTeamMonthReport(monthes);
            /*
             * List<TeamMonthReportEntity> a = new
             * ArrayList<TeamMonthReportEntity>(); a.add(monthes.get(0));
             */
            // AsynTaskProcess.asynExecTask(new
            // TeamMonthReportAsynTask(monthes));
        }
    }

//    @Test
    public void testTeamDailyReport()
    {
        // 获取当前系统时间的前一天时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String attendanceDate = TimeUtil.date2String(calendar.getTime(),
            TimeUtil.BASE_DATE_FORMAT);
        attendanceDate = "2017-11-03";
        List<String> enterIds = dao.queryAllEnterId();

        for (String enterId : enterIds)
        {
            // 统计日报数据
            List<TeamDailyReportEntity> list = dao.teamDailyReport(
                attendanceDate, enterId);
            System.out.println(list.size());
            /*
             * for (TeamDailyReportEntity t : list) { //System.out.println(t); }
             */
            System.out.println("==================");
            // dao.saveTeamDailyInfo(list);
        }
    }

    //@Test
    public void testSaveTeamDailyInfo() throws TaskException
    {
        TeamDailyTask t = new TeamDailyTask(null);
        t.repeat(null);
    }

//    @Test
    public void testQueryTeamDailyCount()
    {
    }

//    @Test
    public void testQueryTeamMonthReportAttendReportReq()
    {
    }

//    @Test
    public void testQueryTeamMonthCount()
    {
    }

    // @Test
    public void testQueryPersonalMonthlyReport()
    {
        System.out.println(dao.queryPersonalMonthlyReport("100886", "2017-06",
            "483460").size());
    }

    // @Test
    public void testSumTeamDailyReport()
    {
        System.out.println(dao.sumTeamDailyReport("1", "2017-06-10"));
    }

//    @Test
    public void testQueryTeamMonthPc()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("7479603");
        req.setAttendanceMonth("2018-10");
        req.setPageNo(1);
        req.setPageSize(5);
        req.setAttendanceId(-1);
        req.setEmployeeName("张");
        List<TeamMonthEntity> list = dao.queryTeamMonthPc(req);
        if (AssertUtil.isEmpty(list))
        {
            System.out.println("无数据");
        }
        for (TeamMonthEntity t : list)
        {
            System.out.println(t.getEnterId() + "\t" + t.getAttendanceId()
                + "\t" + t.getAttendanceId() + "\t" + t.getEmployeeName()
                + "\t" + t.getTotalWorkTime());
        }

    }

//    @Test
    public void testQueryTeamMonthPcCount()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceMonth("2017-07");
        req.setAttendanceId(-1);
        int count = dao.queryTeamMonthPcCount(req);
        System.out.println(count);
    }

//    @Test
    public void testQueryEmpMonthPc()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("7479603");
        req.setStartDate("2018-09-05");
        req.setEndDate("2018-11-05");
        req.setAttendanceId(-1);
        List<EmployeeMonthDetail> list = dao.queryEmpMonthPc(req);
//        System.out.println(list);
        if (AssertUtil.isEmpty(list))
        {
            System.out.println("无数据");
        }
//        for (EmployeeMonthDetail t : list)
//        {
////            System.out.println(t.getOutWorkRemark());
////            System.out.println(t.getOutWorkRemark().getClass());
//        }
    }

//    @Test
    public void testQueryEmpMonthPcCount()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setStartDate("2017-07-05");
        req.setEndDate("2017-09-05");
        req.setAttendanceId(-1);
        int count = dao.queryEmpMonthPcCount(req);
        System.out.println(count);
    }

//    @Test
    public void testQueryExportTeamMonthPc()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceMonth("2017-07");
        req.setAttendanceId(1);
        req.setEmployeeName("王进");
        List<TeamMonthEntity> list = dao.queryExportTeamMonthPc(req);
        System.out.println(list.size());
    }

//    @Test
    public void testQueryExportEmpMonthPc()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setStartDate("2017-07-05");
        req.setEndDate("2017-09-05");
        req.setAttendanceId(1);
        req.setEmployeeName("王进");
        List<EmployeeMonthDetail> list = dao.queryExportEmpMonthPc(req);
        System.out.println(list.size());
    }

//    @Test
    public void testQueryLateItemCount()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceDate("2017-07-05");
        int count = dao.queryLateItemCount(req);
        System.out.println(count);
    }

//    @Test
    public void testQueryLateItem()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceDate("2017-07-05");
        List<EmployeeMonthDetail> list = dao.queryLateItem(req);
        System.out.println(list.size());
    }

//    @Test
    public void testQueryEarlyItemCount()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceDate("2017-07-05");
        int count = dao.queryEarlyItemCount(req);
        System.out.println(count);
    }

//    @Test
    public void testQueryEarlyItem()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceDate("2017-07-05");
        List<EmployeeMonthDetail> list = dao.queryEarlyItem(req);
        System.out.println(list.size());
    }

//    @Test
    public void testQueryNotClockedItemCount()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceDate("2017-07-05");
        int count = dao.queryNotClockedItemCount(req);
        System.out.println(count);
    }

//    @Test
    public void testQueryNotClockedItem()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceDate("2017-07-05");
        req.setPageSize(100);
        List<EmployeeMonthDetail> list = dao.queryNotClockedItem(req);
        System.out.println(list.size());
    }

//    @Test
    public void testQueryEmpMonthListPc()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceMonth("2017-07");
        req.setUid("E44F41D646FBE2F59083919E9D22B480");
        req.setAttendanceId(1);
       // TeamMonthEntity list = (TeamMonthEntity) dao.queryEmpMonthList(req);
        List<TeamMonthEntity> list =  dao.queryEmpMonthList(req);
        System.out.println(list);
    }

//    @Test
    public void testQueryLateDetailInfo()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceMonth("2017-07");
        req.setUid("2AAEC46B6D589A15B6996A85F1EBB6DF");
        req.setAttendanceId(1);
        List<EmployeeMonthDetail> count = dao.queryLateDetailInfo(req);
        System.out.println(count.size());
    }

//    @Test
    public void testQueryEarlyDetailInfo()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceMonth("2017-07");
        req.setUid("2AAEC46B6D589A15B6996A85F1EBB6DF");
        req.setAttendanceId(1);
        List<EmployeeMonthDetail> list = dao.queryEarlyDetailInfo(req);
        System.out.println(list.size());
    }

//    @Test
    public void testQueryNotClockedDetailInfo()
    {
        AttendReportReq req = new AttendReportReq();
        UserInfo user = new UserInfo();
        req.setUserInfo(user);
        req.setEnterId("483460");
        req.setAttendanceMonth("2017-07");
        req.setUid("2AAEC46B6D589A15B6996A85F1EBB6DF");
        req.setAttendanceId(1);
        List<EmployeeMonthDetail> list = dao.queryNotClockedDetailInfo(req);
        System.out.println(list.size());
    }

    @Test
    public void testQueryEmpDatas() {
        AttendReportReq req = new AttendReportReq();
        req.setAttendanceMonth("2018-11");
        req.setEnterId("7188935");
        req.setPageSize(1);
        req.setPageNo(1);
        String uid = "54EDA7742FCB1F3081E89C00FCE20239";
        String enterId = "118648502";
        String attendanceDate = "2018-10";

//        System.out.println(list.get(0));
//        List<EmployeeMonthDetailVO> list = dao.queryEmpMonthDetail(uid,attendanceDate,enterId);
//        System.out.println(list.size());
        System.out.println(TimeUtil.convert2long(req.getAttendanceMonth(),BASE_DATE_FORMAT_YYYY_MM) >
            TimeUtil.convert2long("2018-10",BASE_DATE_FORMAT_YYYY_MM));
//        System.out.println(list.get(0).getLeaveNotClockedDays());
    }

    @Test
    public void testQueryTeamMonthDetails() {
        Map<String, Object> map = new HashMap<>();
        String enterId = "7188935";
        String uid = "EE62C7A0BF1EDF1EEB2F5E6EE6D71C6F";
//        map.put("enterId",enterId);
//        map.put("status",1);
//        List<UserInfo> employeeList = employeeDao.queryEmployeeByEnterid(enterId);
//        System.out.println(employeeList);
//        AttendGroupRes attendGroupRes = new AttendGroupRes();
//        attendGroupRes.setEmployees(employeeList);
//        System.out.println(attendGroupRes.getEmployees());

        UserInfo info = new UserInfo();
        info.setUid("EE62C7A0BF1EDF1EEB2F5E6EE6D71C6F");
        System.out.println(info.getUid()==uid);

    }

    public static void main(String[] args) {
        String once = EnterpriseUtil.getNum(12);
        String app_key = "d7eec29775ca42a894ab3ce432667e70";
        String version = "1.0";
        String channel = "d7eec29775ca42a894ab3ce432667e70";
        String sdk = "java";
        String QYTXL_APPKEY= "ca7dc22b57fa45a7a6a8eb89a3dc7b49";
        HashMap<String, String> Map = new HashMap<String, String>();
        Map.put("app_key", app_key);
        Map.put("once", once);
        Map.put("version", version);
        Map.put("channel", channel);
        Map.put("sdk", sdk);
//        Map.put("attendanceId", "719");
//        Map.put("attendanceDate", "2019-05-08");
//        Map.put("type","全部");
        Map.put("enterId", "7188935");
        String signature = EnterpriseUtil.getNornmalSignature(Map,QYTXL_APPKEY);
        System.out.println(once);
        System.out.println(signature);

    }
}

package richinfo.attendance.service.impl;

import org.junit.Test;
import richinfo.attendance.bean.AttendReportReq;
import richinfo.attendance.entity.UserInfo;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

public class AttendReportServiceImplTest
{

    static
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

    @Test
    public void testSendTeamMonthlyReport() {
        AttendReportServiceImpl service = new AttendReportServiceImpl();
        UserInfo userInfo = new UserInfo();
        userInfo.setEnterName("彩讯科技股份有限公司");
        userInfo.setIsAdmin(1);
        userInfo.setRoleType(1);

//        AttendReportReq reqParam = new AttendReportReq();
//        reqParam.setEnterId("7188935");
//        reqParam.setAttendanceMonth("2018-11");
//        reqParam.setUserInfo(userInfo);
//        TeamMonthRes teamMonthRes = service.sendTeamMonthlyReport(reqParam);
//        List<TeamMonthEntity> data = teamMonthRes.getData();
//        for (TeamMonthEntity entity:data) {
//            System.out.println(entity.getUid());
//            System.out.println(entity.getEmployeeName());
//            System.out.println(entity.getPhone()+"空字符");
//        }

    }
    
    @Test
    public void testSendTeamDailyInfo()
    {
        AttendReportServiceImpl service = new AttendReportServiceImpl();
        UserInfo userInfo = new UserInfo();
        userInfo.setEnterName("彩讯科技股份有限公司");

        AttendReportReq reqParam = new AttendReportReq();
        reqParam.setEnterId("483460");
        reqParam.setAttendanceDate("2017-06-27");
        reqParam.setUserInfo(userInfo);
//        service.sendTeamDailyInfo(reqParam);
    }

}

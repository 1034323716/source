package richinfo.attendance.dao;

import org.junit.Before;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

public class AttendEmpMonthlyDaoTest
{


    private AttendDao attendDao = new AttendDao();

    @Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

    private AttendReportDao reportDao = new AttendReportDao();

    private AttendDao attendanceDao = new AttendDao();

//    @Test
    public void testQueryEmp(){


    }

//    @Test
    public void testQueryDatas(){
        String returnColumn = "uid,employeeName";
        String table = "attendance_employee_info";
        String columns = "enterId=7188935";

 //       List list = attendanceDao.queryDatas(columns,table,returnColumn);

 //       System.out.println(list.size());
//        System.out.println(list.get(1));
    }

}

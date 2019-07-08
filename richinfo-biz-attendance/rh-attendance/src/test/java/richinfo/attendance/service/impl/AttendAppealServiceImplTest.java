package richinfo.attendance.service.impl;

import richinfo.attendance.bean.AttendAppealReq;
import richinfo.attendance.bean.AttendAppealRes;
import richinfo.attendance.entity.UserInfo;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

public class AttendAppealServiceImplTest
{
    static
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

//    @Test
    public void testQueryUserAppealList()
    {
        AttendAppealServiceImpl att = new AttendAppealServiceImpl();
        UserInfo u = new UserInfo();
        u.setUid("2309044");
        u.setEnterId("483460");
        AttendAppealReq req = new AttendAppealReq();
        req.setUserInfo(u);
        att.queryUserAppealList(req);
        
    }

//    @Test
    public void testQueryManageAppealList()
    {
        AttendAppealServiceImpl att = new AttendAppealServiceImpl();
        UserInfo u = new UserInfo();
        u.setUid("2309044");
        u.setEnterId("483460");
        AttendAppealReq req = new AttendAppealReq();
        req.setPageNo(1);
        req.setPageSize(20);
        req.setUserInfo(u);
        AttendAppealRes  res = att.queryManageAppealList(req);
        System.out.println(res);
    }

}

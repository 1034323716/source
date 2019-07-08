package richinfo.attendance.dao;

import richinfo.attendance.bean.AbnormalAppealRes;
import richinfo.attendance.bean.AttendAppealReq;
import richinfo.attendance.common.ResBean;
import richinfo.attendance.entity.AttendExamineEntity;
import richinfo.attendance.entity.EmployeeMonthDetail;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.util.TimeUtil;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.util.Date;
import java.util.List;

public class AttendAppealDaoTest
{
    private AttendAppealDao appealDao = new AttendAppealDao();

    static
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

    //@Test
    public void testQuerySingleAppealInfo()
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setAppealId(1);
        try
        {
            AttendAppealReq info = appealDao.querySingleAppealInfo(reqBean);
            System.out.println(info);
        }
        catch (PersistException e)
        {
            e.printStackTrace();
        }
    }

   // @Test
    public void testQueryUserAppealList() throws PersistException
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setUid("2309042");
        reqBean.setPageNo(1);
        reqBean.setPageSize(20);
        List<AttendAppealReq> list = appealDao.queryUserAppealList(reqBean);
        System.out.println(list);
    }

    //@Test
    public void testQueryUserAppealListCount() throws PersistException
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setUid("2309042");
        long count = appealDao.queryUserAppealListCount(reqBean);
        System.out.println(count);
    }

   // @Test
    public void testQueryManageAppealListCount() throws PersistException
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setExamineUid("2278248");
        long count = appealDao.queryManageAppealListCount(reqBean);
        System.out.println(count);
    }

   // @Test
    public void testQueryManageAppealList() throws PersistException
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setPageSize(20);
        reqBean.setOffset(0);
        reqBean.setExamineUid("2278242");
        List<AttendAppealReq> list = appealDao.queryManageAppealList(reqBean);
        System.out.println(list);
    }

    //@Test
    public void testQueryUserAttendDetail() throws PersistException
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setMonthRcdId(4);
        EmployeeMonthDetail info = appealDao.queryUserAttendDetail(reqBean);
        System.out.println(info);
    }

    //@Test
    public void testQueryExamineUid() throws PersistException
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        reqBean.setAttendanceId(3);
        AttendExamineEntity info = appealDao.queryExamineUid(reqBean);
        System.out.println(info);
    }

    //@Test
    public void testAbnormalAppeal() throws PersistException
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        AbnormalAppealRes resBean = new AbnormalAppealRes();
        reqBean.setAttendanceId(1);
        reqBean.setMonthRcdId(11);
        reqBean.setEnterId("483460");
        reqBean.setUid("2278227");
        reqBean.setName("张海");
        reqBean.setReason("\uD83D\uDE0F\uD83D\uDE0F\uD83D\uDE0F\uD83D");
        reqBean.setGoWork(null);
        reqBean.setGoWorkDesc("未打卡");
        reqBean.setLeaveWork(TimeUtil.string2Date("18:16:14", "HH:mm:ss"));
        reqBean.setLeaveWorkDesc("正常");
        reqBean.setExamineName("唐兴均");
        reqBean.setExamineUid("2278248");
        reqBean.setExamineState(1);
        reqBean.setAttendanceDate(TimeUtil.string2Date("2017-06-27",
            TimeUtil.BASE_DATE_FORMAT));
        reqBean.setAppealRecord(1);
        reqBean.setRemark("工作日");
        reqBean.setCreateTime(new Date());
        reqBean.setUpdateTime(new Date());

        appealDao.abnormalAppeal(reqBean, resBean);
        System.out.println(reqBean.getAppealId());
    }

   // @Test
    public void testCancelAppeal()
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        ResBean resBean = new ResBean();
        AttendAppealReq appealInfo = new AttendAppealReq();
        reqBean.setUserInfo(new UserInfo());
        reqBean.setAppealId(4);
        appealInfo.setMonthRcdId(11);
        appealDao.cancelAppeal(reqBean, resBean, appealInfo);
    }

//    @Test
    public void testDealAppeal()
    {
        AttendAppealReq reqBean = new AttendAppealReq();
        ResBean resBean = new ResBean();
        AttendAppealReq appealInfo = new AttendAppealReq();
        reqBean.setUserInfo(new UserInfo());
        reqBean.setAppealId(45);
        reqBean.setEnterId("7188935");
        reqBean.setExamineUid("FB362F7E489F283333377F80C6E3F455");
        reqBean.setExamineResult(1);
        appealInfo.setMonthRcdId(11632);
        appealInfo.setAttendanceId(7);
        appealInfo.setUid("EE62C7A0BF1EDF1EEB2F5E6EE6D71C6F");
        appealInfo.setAttendanceDate(TimeUtil.string2Date("2018-08-27",
            TimeUtil.BASE_DATE_FORMAT));
        appealInfo.setAppealRecord(1);
        appealDao.dealAppeal(reqBean, resBean, appealInfo);
        System.out.println(resBean.getCode() + "\t" + resBean.getSummary());
    }

    //@Test
    public void testQueryNotExamineAppeal() throws PersistException
    {
        //long count = appealDao.queryNotExamineAppeal("2278242", 1);
        //System.out.println(count);
    }

}

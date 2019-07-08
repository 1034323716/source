/**
 * 文件名：AttendGroupDaoTest.java
 * 创建日期： 2017年6月6日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月6日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.dao;

import org.junit.Before;
import org.junit.Test;
import richinfo.attendance.common.DaoObject;
import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.entity.AttendGroup.GroupStatus;
import richinfo.attendance.entity.UserInfo;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * 
 */
public class AttendGroupDaoTest extends DaoObject
{
    private AttendGroupDao groupDao = new AttendGroupDao();

    private AttendWhitelistDao attendWhitelistDao = new AttendWhitelistDao();

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

    /**
     * Test method for
     * {@link richinfo.attendance.dao.AttendGroupDao#
     * queryAttendGroupByName(java.lang.String, java.lang.String)}
     * .
     * @throws PersistException
     */
    // @Test
    public void testQueryAttendGroupByName() throws PersistException
    {
        /*
         * AttendGroupServiceImpl aa = new AttendGroupServiceImpl();
         * AttendGroupReq a = new AttendGroupReq(); a.setEnterId("483460");
         * a.setAttendanceId(21); a.setAttendanceName("新建十一");
         */

        List<AttendGroup> list = groupDao.queryAttendGroupByName("483460",
            "你是", -1);
        System.out.println("=============");
        for (AttendGroup group : list)
        {
            System.out.println(group);
        }
        System.out.println("============");
    }

    /**
     * Test method for
     * {@link richinfo.attendance.dao.AttendGroupDao#
     * saveAttendGroup(richinfo.attendance.entity.AttendGroup)}
     * .
     */
//    @Test
    public void testSaveAttendGroup()
    {
        Date now = new Date();

        // 考勤组基本信息
        AttendGroup group = new AttendGroup();
        group.setAttendanceName("客服部白班");
        group.setEnterId("12345678");
        group.setAmTime("09:00-11:30");
        group.setPmTime("13:00-18:00");
        group.setLocation("长虹科技大厦");
        group.setDetailAddr("广东省深圳市南山区科技南十二路18号");
        group.setLongitude(116.404);
        group.setLatitude(39.915);
        group.setAttendanceRange(300);
        group.setCreateTime(now);
        group.setModifyTime(now);

        // 新增考勤人员列表
        List<AttendEmployee> saveList = new ArrayList<AttendEmployee>();

        AttendEmployee employee1 = new AttendEmployee();
        employee1.setUid("100886");
        employee1.setEmployeeName("张三");
        employee1.setPhone("13800000123");
        employee1.setEnterId("12345678");
        employee1.setDeptName("产品事业部-产品管理室");
        employee1.setCreateTime(now);
        employee1.setModifyTime(now);

        saveList.add(employee1);

        AttendEmployee employee2 = new AttendEmployee();
        employee2.setUid("100887");
        employee2.setEmployeeName("李四");
        employee2.setPhone("13800000124");
        employee2.setEnterId("12345678");
        employee2.setDeptName("产品事业部-产品管理室");
        employee2.setCreateTime(now);
        employee2.setModifyTime(now);

        saveList.add(employee2);

        AttendEmployee employee3 = new AttendEmployee();
        employee3.setUid("100888");
        employee3.setEmployeeName("王五");
        employee3.setPhone("13800000125");
        employee3.setEnterId("12345678");
        employee3.setDeptName("客户服务部");
        employee3.setCreateTime(now);
        employee3.setModifyTime(now);

        saveList.add(employee3);

        // 修改考勤人员列表
        List<AttendEmployee> updateList = new ArrayList<AttendEmployee>();

        AttendEmployee employee4 = new AttendEmployee();
        employee4.setUid("111886");
        employee4.setEmployeeName("李四");
        employee4.setPhone("13800000123");
        employee4.setEnterId("12345678");
        employee4.setDeptName("产品事业部-产品管理室");
        employee4.setCreateTime(now);
//        employee4.setModifyTime(now);

        updateList.add(employee4);

        AttendEmployee employee5 = new AttendEmployee();
        employee5.setUid("111888");
        employee5.setEmployeeName("王五");
        employee5.setPhone("13800000123");
        employee5.setEnterId("12345678");
        employee5.setDeptName("产品事业部-产品管理室");
        employee5.setCreateTime(now);
//        employee5.setModifyTime(now);

        updateList.add(employee5);

        //groupDao.saveAttendGroup(group, saveList, null);
    }

    /**
     * 测试查看考勤列表详情
     */
//    @Test
    public void testQueryGroupList(){
//        List<AttendGroupWithEmpRes> list = groupDao.queryExamineName(
//                "7479603", 1);
//        System.out.println(list.toString());
    }

    /**
     * 删除考勤组
     */
    // @Test
    public void testDelete()
    {
        AttendGroup req = new AttendGroup();

        req.setAttendanceId(10001L);
        req.setEnterId("aaa111");
        req.setModifyTime(new Date());
        req.setStatus(GroupStatus.Abnormal.getValue());
        boolean flag = groupDao.deleteGroup(req, null);
        System.out.println("结果：" + flag);
    }

    /**
     * 编辑考勤组
     */
    // @Test
    public void testUpdateGroup()
    {
        AttendGroup attendGroup = new AttendGroup();
        attendGroup.setAttendanceId(43L);
        attendGroup.setModifyTime(new Date());
        attendGroup.setLatitude(-1L);
        attendGroup.setLongitude(-1L);
        attendGroup.setAttendanceRange(-1);
        /*boolean flag = groupDao.updateGroup(attendGroup, null, null, null,
            new UserInfo());
        System.out.println("结果:" + flag);*/
    }

    // @Test
    public void testQueryAttendGroupInfo()
    {
        AttendGroup a = groupDao.queryAttendGroupInfo(4L, "12345678", 0);
        System.out.println(a);
    }

    // @Test
    public void testQueryUidRecord()
    {
        List<UserInfo> users = new ArrayList<UserInfo>();

        UserInfo u1 = new UserInfo();
        u1.setUid("114");
        users.add(u1);

        UserInfo u2 = new UserInfo();
        u2.setUid("108");
        users.add(u2);

        UserInfo u3 = new UserInfo();
        u3.setUid("116");
        users.add(u3);

        List<AttendEmployee> list = groupDao.queryUidRecord(users);
        for (AttendEmployee temp : list)
        {
            System.out.println(temp);
        }

    }

    // @Test
    public void deleteEmployeesOfGroup()
    {
        AttendGroup attendGroup = new AttendGroup();
        attendGroup.setAttendanceId(501);
        attendGroup.setModifyTime(new Date());
        attendGroup.setStatus(GroupStatus.Abnormal.getValue());

//        boolean f = groupDao.deleteGroup(attendGroup, new UserInfo());
        System.out.println("=========");
//        System.out.println(f);
        System.out.println("--------------");
    }

    /**
     * 测试企业考勤组
     */
    @Test
    public void testBatchUpdateEmployeeWhiteListStatus() throws PersistException {

    }
    
}

/**
 * 文件名：AttendEmployeeDaoTest.java
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
import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.util.AssertUtil;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：
 * 
 */
public class AttendEmployeeDaoTest
{
    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();
    private AttendGroupDao groupDao = new AttendGroupDao();

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
     * {@link richinfo.attendance.dao.AttendEmployeeDao#
     * queryUidInGroups(java.lang.String, java.util.List)}
     * .
     * @throws PersistException
     */
    // @Test
    public void testQueryUidInGroups() throws PersistException
    {
        List<UserInfo> users = new ArrayList<UserInfo>();

        UserInfo user1 = new UserInfo();
        user1.setUid("100886");
        users.add(user1);

        UserInfo user2 = new UserInfo();
        user2.setUid("100887");
        users.add(user2);

        UserInfo user3 = new UserInfo();
        user3.setUid("100888");
        users.add(user3);

        List<String> list = employeeDao.queryUidInGroups(users);
        if (AssertUtil.isNotEmpty(list))
        {
            for (int i = 0; i < list.size(); i++)
            {
                System.out.println(i + ":" + list.get(i));
            }
        }
        else
        {
            System.out.println("empty");
        }

    }

    // @Test
    public void testQueryEmployeeByUid() {
        System.out.println(AssertUtil.isEmpty(employeeDao.queryEmployeeByUid("EE62C7A0BF1EDF1EEB2F5E6EE6D71C6F").getPhone()));
         System.out.println(employeeDao.queryEmployeeByUid("EA28360E4A22E451B489C66EC15EB3E1").getPhone());
    }

//     @Test
    public void testQueryAttendGroupByUid()
    {
        List<UserInfo> users = new ArrayList<UserInfo>();

        UserInfo user1 = new UserInfo();
        user1.setUid("100886");
        users.add(user1);

        UserInfo user2 = new UserInfo();
        user2.setUid("100887");
        users.add(user2);

        UserInfo user3 = new UserInfo();
        user3.setUid("100888");
        users.add(user3);

        UserInfo user4 = new UserInfo();
        user4.setUid("100000");
        users.add(user4);

        List<UserInfo> list = employeeDao
            .queryAttendGroupByUid(users, true, -1);
        if (AssertUtil.isNotEmpty(list))
        {
            for (UserInfo temp : list)
            {
                System.out.println(temp);
            }
        }
        else
        {
            System.out.println("empty");
        }
    }

    @Test
    public void testQueryOwnGroup() {
        List<AttendEmployee> users = new ArrayList<AttendEmployee>();

        AttendEmployee user1 = new AttendEmployee();
        user1.setAttendanceId(18184537);
        user1.setEnterId("7188935");
        user1.setUid("752EF0A86C67ED2EB67B45EF92439D41");
        users.add(user1);

        AttendEmployee user2 = new AttendEmployee();
        user2.setAttendanceId(187);
        user2.setEnterId("7188935");
        user2.setUid("752EF0A86C67ED2EB67B45EF92439D41");
        users.add(user2);

        AttendEmployee user3 = new AttendEmployee();
        user3.setAttendanceId(10087);
        user3.setEnterId("7188935");
        user3.setUid("EE62C7A0BF1EDF1EEB2F5E6EE6D71C6F");
        users.add(user3);

        Map<String,Object> insertMap = new HashMap();
        insertMap.put("attendanceName","我要的卡kksksks券");
        insertMap.put("enterId","7188935");
        insertMap.put("attendanceId","34534636");
        insertMap.put("employee",users);
        groupDao.updateEquipmentAttdenNameByUid(insertMap);
    }
}

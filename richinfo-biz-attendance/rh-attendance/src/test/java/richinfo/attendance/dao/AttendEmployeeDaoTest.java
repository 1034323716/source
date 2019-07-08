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
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.util.AssertUtil;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能描述：
 * 
 */
public class AttendEmployeeDaoTest
{
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
        List<UserInfo> users = new ArrayList<UserInfo>();

        UserInfo user1 = new UserInfo();
        user1.setAttendanceId(18184537);
        users.add(user1);

        UserInfo user2 = new UserInfo();
        user2.setAttendanceId(187);
        users.add(user2);

        UserInfo user3 = new UserInfo();
        user3.setAttendanceId(10087);
        users.add(user3);

        Comparator<UserInfo> comparator = Comparator.comparingLong(UserInfo::getAttendanceId);

        List<UserInfo> userInfoList = users.stream().sorted(comparator).collect(Collectors.toList());

        System.out.println(Arrays.asList(userInfoList));
    }
}

/**
 * 文件名：AttendanceScheduleShiftDaoTest.java
 * 创建日期： 2018年4月3日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年4月3日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.dao;

import org.junit.Before;
import richinfo.attendance.entity.AttendanceScheduleShift;
import richinfo.attendance.util.AssertUtil;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.text.ParseException;
import java.util.List;

/**
 * 功能描述：考勤组排班班次 DAO层 测试类
 *
 */
public class AttendanceScheduleShiftDaoTest
{
    private AttendanceScheduleShiftDao dao = new AttendanceScheduleShiftDao();

    @Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

//    @Test
    public void testQueryShiftByAttendanceId() throws ParseException
    {

        List<AttendanceScheduleShift> list = dao.queryShiftByAttendanceId(39);
        if (AssertUtil.isNotEmpty(list))
        {
            for (AttendanceScheduleShift info : list)
            {
                System.out.println(info);
            }
        }
        else
        {
            System.out.println("result is empty.");
        }
    }
}

/**
* 文件名：TestMessageService.java
* 创建日期： 2018年4月9日
* 作者：     liyongde
* Copyright (c) 2009-2011 个邮开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2018年4月9日
*   修改人：liyongde
*   修改内容：
*/
package richinfo.attendance.dao;

import org.junit.Before;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.service.impl.MessageServiceImpl;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.text.ParseException;

/**
 * 功能描述：
 *
 */
public class TestMessageService
{
    private MessageServiceImpl service = new MessageServiceImpl();
    
    @Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

//    @Test
    public void handleScheduleAttendMessage() throws ParseException
    {
        AttendGroup attendGroup = new AttendGroup();
        attendGroup.setAttendanceId(85);
        attendGroup.setAttendanceName("测试考勤消息推送");
        attendGroup.setAttendType(2);
//        service.handleScheduleAttendMessage(attendGroup, 2, "123456");
    }
}

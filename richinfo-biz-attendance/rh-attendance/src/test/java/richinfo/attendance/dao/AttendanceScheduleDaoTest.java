/**
 * 文件名：AttendanceScheduleDaoTest.java
 * 创建日期： 2018年4月4日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年4月4日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.dao;

import org.junit.Before;
import richinfo.attendance.entity.AttendanceSchedule;
import richinfo.attendance.util.AESEncryptUtil;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.EnterpriseUtil;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

/**
 * 功能描述：考勤组排班 DAO层 测试类
 *
 */
public class AttendanceScheduleDaoTest
{
    private AttendanceScheduleDao dao = new AttendanceScheduleDao();

    @Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

//    @Test
    public void testQuerySchedule() throws ParseException
    {

        List<AttendanceSchedule> list = dao.querySchedule(85L, "2018-04", "543E53DAEBB04AA9459BFFAD7AD6434D");
        System.out.println("ListSize="+list.size());
        if (AssertUtil.isNotEmpty(list))
        {
            for (AttendanceSchedule info : list)
            {
                System.out.println(info);
            }
        }
        else
        {
            System.out.println("result is empty.");
        }
    }

    public static void main(String[] args) throws Exception {
        String once = EnterpriseUtil.getNum(12);
        String timeStamp =  String.valueOf(System.currentTimeMillis());
        String token ="3ad8id23i907o2kmli03";

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("once", once);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("token", token);
        String signatur = EnterpriseUtil.getNornmalSignature(hashMap,"6af15ca383ee45dd959bf0e84d8eadac");
        hashMap.put("signature",signatur);

        System.out.println(signatur);
        System.out.println(once);
        System.out.println(timeStamp);

        String str = "{ “EventType”: “event_user_modify”,”UserId”: [“17FA1FC6F1451AEC8A6B31A54E10023A”],”DepId”:[“1”],”CorpId”: “514165”}";
        String encrypt = AESEncryptUtil.aesEncrypt(str,AESEncryptUtil.getAESKey(AESEncryptUtil.aesEncrypt("6af15ca383ee45dd", "PKCS5Padding")));
        System.out.println(encrypt);
    }
}

package richinfo.attendance.dao;

import com.alibaba.fastjson.JSONArray;
import org.junit.Before;
import org.junit.Test;
import richinfo.attendance.entity.AttendEntity;
import richinfo.attendance.util.*;
import richinfo.dbcomponent.service.impl.SqlMapClientBeanFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AttendDaoTest
{


    private AttendDao attendDao = new AttendDao();

    @Before
    public void setUp() throws Exception
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.setConfigLocation("classpath:/attendance-sql-map.xml");
    }

    @Test
    public void testQueryEmployRecordByTime(){
        Date now = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat( TimeUtil.BASE_DATE_FORMAT);
        SimpleDateFormat sdfTime = new SimpleDateFormat( TimeUtil.BASE_TIME_FORMAT);
        String date = sdfDate.format(now);
        String time = sdfTime.format(now);
//        List<AttendEntity> attendEntityList = attendDao.queryEmployRecordByTime(date, time);
    }

    public static void main(String[] args) {
        String appid = AttendanceConfig.getInstance().getProperty("attend.qytxl.appkey", "9fdcd721d954456b8c7ea53f80635456");
        String aeskey = AttendanceConfig.getInstance().getProperty("attend.qytxl.aes_key", "6af15ca383ee45dd");
        String url = AttendanceConfig.getInstance().getProperty("attend.qytxl.callbackUrl", "http://121.15.167.235:10721/atdc/sync/getRegisterCallBackInfo");
        String appkey = AttendanceConfig.getInstance().getProperty("attend.qytxl.appsecret", "6af15ca383ee45dd959bf0e84d8eadac");
        String postUrl = AttendanceConfig.getInstance().getProperty("qytxl.register.url", "https://open.cytxl.com.cn/enterprise/getCallBack.json");

        String app_key= appid;
        String once = EnterpriseUtil.getNum(12);
        String version = "2.0";
        String channel = appid;
        String sdk_from = "java";
        JSONArray eventType = new JSONArray();
        eventType.add("event_user_remove");
        String departmentId = "";
        String token ="3ad8id23i907o2kmli03";
        String aes_key = null;
        try {
            aes_key = AESEncryptUtil.aesEncrypt(aeskey, "PKCS5Padding");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        HashMap<String,String> Map = new HashMap<String,String>();
        Map.put("app_key", app_key);
        Map.put("once", once);
        Map.put("version", version);
        Map.put("channel", channel);
        Map.put("sdk_from", sdk_from);
        Map.put("eventType", eventType.toString());
        Map.put("enterpriseId", "113251120");
        Map.put("departmentId", departmentId);
        Map.put("url", url);
        Map.put("token", token);
        Map.put("aes_key", aes_key);
//        if(method.equals("create")||method.equals("update")){
//            Map.put("token", token);
//            Map.put("aes_key", aes_key);
//        }

        String signature = EnterpriseUtil.getNornmalSignature(Map, appkey);

        StringBuffer params = new StringBuffer();
        params.append("app_key="+app_key);
        params.append("&once="+once);
        params.append("&version="+version);
        params.append("&channel="+channel);
        params.append("&sdk_from="+sdk_from);
        params.append("&signature="+signature);
        params.append("&eventType="+eventType);
        params.append("&enterpriseId="+"113251120");
        params.append("&departmentId="+departmentId);
        params.append("&url="+url);
        params.append("&token="+token);
        params.append("&aes_key="+aes_key);
        String parame = params.toString();

//        JSONObject responseJson = EnterpriseUtil.sendPost(postUrl, parame, null);
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
    }

//    @Test
    public void testQueryEmployRecord() throws ParseException
    {
        Date date = new Date();

        // long now = System.currentTimeMillis();
        // date = TimeUtil.long2Date(now, TimeUtil.BASE_DATE_FORMAT);

        List<AttendEntity> list = attendDao.queryEmployRecord("2278227", date);
        if (AssertUtil.isNotEmpty(list))
        {
            for (AttendEntity attendRecord : list)
            {
                System.out.println(attendRecord);
            }
        }
        else
        {
            System.out.println("result is empty.");
        }
    }

    // @Test
    public void testSaveAttendRecord()
    {
        AttendEntity attendRecord = new AttendEntity();

        attendRecord.setUid("100888");
        attendRecord.setPhone("13800000123");
        attendRecord.setAttendanceId(1);
        attendRecord.setLocation("长虹科技大厦");
        attendRecord.setDetailAddr("广东省深圳市南山区科技南十二路18号");
        attendRecord.setLongitude(116.404);
        attendRecord.setLatitude(39.915);
        attendRecord.setStatus(0);

        // 设置打卡时间
        attendRecord.setAttendanceDate(new Date());
        attendRecord.setAttendanceTime(new Date());

        System.out.println(attendDao.saveAttendRecord(attendRecord));
    }

   // @Test
    public void testQueryRecord() throws Exception {
//        List<AttendEntity> list = attendDao.queryEmployRecord("1001", TimeUtil.string2Date("2017-06-12"));
        System.out.println("--------------------");
//        for (AttendEntity a : list)
//        {
//            System.out.println(a);
//            System.out.println(TimeUtil.long2Date(a.getTime(),
//                "yyyy-MM-dd HH:mm:ss"));
//        }
        System.out.println("------------------------");
    }

    private AttendReportDao reportDao = new AttendReportDao();

    private AttendDao attendanceDao = new AttendDao();

    @Test
    public void testAll() {
        String gbString = "6J6N6YCa5YWs5Y 4Mi4w";
        String s = base64Dcode(gbString);
        System.out.println(s);
    }

    /*
   * 中文转unicode编码
   */
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }


    /**
     * base64解码：先转换并补齐位数，url传输后+号会被替换成空格，=号会丢失。 base64编码url直接传输有弊端
     * @param enterName
     * @return
     */
    private String base64Dcode(String enterName)
    {
        enterName = enterName.trim().replaceAll(" ", "+");
        int i = enterName.length() % 4;
        if (i != 0)
        {
            int j = 4 - i;
            for (int k = 0; k < j; k++)
            {
                enterName = enterName + "=";
            }
        }
        enterName = Base64Coder.decodeString(enterName);
        return enterName;
    }
}

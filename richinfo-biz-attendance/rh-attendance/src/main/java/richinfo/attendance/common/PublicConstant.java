package richinfo.attendance.common;

import richinfo.attendance.util.AttendanceConfig;

/**
 * Created by qiang on 2018/9/20.
 */
public class PublicConstant {
    //限制用户打卡间隔时间key
    public static  final  String ATTENDANCE_USER_CLOCK_DATE_U = "ATTENDANCE_USER_CLOCK_DATE_";

    public static String APP_KEY = AttendanceConfig.getInstance().getProperty("attend.qytxl.appkey", "9fdcd721d954456b8c7ea53f80635456");
    public static String APP_SECRET = AttendanceConfig.getInstance().getProperty("attend.qytxl.appsecret", "6af15ca383ee45dd959bf0e84d8eadac");
    public static String AES_KEY = AttendanceConfig.getInstance().getProperty("attend.qytxl.aes_key", "6af15ca383ee45dd");

}

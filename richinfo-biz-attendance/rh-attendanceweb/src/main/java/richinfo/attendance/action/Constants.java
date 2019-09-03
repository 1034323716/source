package richinfo.attendance.action;

import richinfo.attendance.util.AttendanceConfig;

/**
 * created by cxming on 2019/9/3
 */
public class Constants {
    public static String APP_KEY = AttendanceConfig.getInstance().getProperty("attend.qytxl.appkey", "9fdcd721d954456b8c7ea53f80635456");
    public static String APP_SECRET = AttendanceConfig.getInstance().getProperty("attend.qytxl.appsecret", "6af15ca383ee45dd959bf0e84d8eadac");
    public static String AES_KEY = AttendanceConfig.getInstance().getProperty("attend.qytxl.aes_key", "6af15ca383ee45dd");
}

package richinfo.attendance.service;


/**
 * 功能描述：考勤员工功能逻辑实现规则
 */
public interface AttendEmployService {

    /**
     * 短信开关提醒设置
     */
    boolean settingSMSRemider(String uid, int status);

    /**
     * 获取短信开关提醒状态
     */
    int getSMSRemiderStatus(String uid);
}

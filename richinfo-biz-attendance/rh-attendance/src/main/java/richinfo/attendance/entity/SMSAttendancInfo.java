package richinfo.attendance.entity;

import richinfo.attendance.bean.AttendClockVo;

import java.util.List;

public class SMSAttendancInfo {
    private int ssoStatus = 0;
    public List<AttendEntity> attendRecord;
    public List<AttendClockVo> attendClockVos;
    /**
     * 用户和考勤组关系对象
     */
    private UserGroupEntity userGroup;
    //允许打卡范围
    private String allowOutRangeClock;

    private String charge;
    private String token;

    public int getSsoStatus() {
        return ssoStatus;
    }

    public void setSsoStatus(int ssoStatus) {
        this.ssoStatus = ssoStatus;
    }

    public List<AttendEntity> getAttendRecord() {
        return attendRecord;
    }

    public void setAttendRecord(List<AttendEntity> attendRecord) {
        this.attendRecord = attendRecord;
    }

    public List<AttendClockVo> getAttendClockVos() {
        return attendClockVos;
    }

    public void setAttendClockVos(List<AttendClockVo> attendClockVos) {
        this.attendClockVos = attendClockVos;
    }

    public UserGroupEntity getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroupEntity userGroup) {
        this.userGroup = userGroup;
    }

    public String getAllowOutRangeClock() {
        return allowOutRangeClock;
    }

    public void setAllowOutRangeClock(String allowOutRangeClock) {
        this.allowOutRangeClock = allowOutRangeClock;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SMSAttendancInfo{");
        sb.append("attendRecord='").append(attendRecord).append('\'');
        sb.append(", attendClockVos='").append(attendClockVos).append('\'');
        sb.append(", userGroup='").append(userGroup).append('\'');
        sb.append(", allowOutRangeClock='").append(allowOutRangeClock).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", charge='").append(charge);
        sb.append('}');
        return sb.toString();
    }

}

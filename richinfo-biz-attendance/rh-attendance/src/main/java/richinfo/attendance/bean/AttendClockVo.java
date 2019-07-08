package richinfo.attendance.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户打卡数据上下班时间展示展示
 */
public class AttendClockVo  implements Serializable{

    private static final long serialVersionUID = 295737034152267792L;

    /** 打卡时间 */
   @JSONField(format = "HH:mm:ss")
    private Date clockTime;
    private long time;
    /** 打卡地点 */
    private String location;

    /** 打卡状态 0 正常1迟到2早退3未打卡*/
    private int regionStatus;

    /** 上班考勤状态 0打卡公司打卡  1外勤打卡 */
    private int status;

    /** 区分上下班 0 上班 1 下班 */
    private int amPmStatue;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Date getClockTime() {
        return clockTime;
    }

    public void setClockTime(Date clockTime) {
        this.clockTime = clockTime;
        this.time = this.clockTime.getTime();
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRegionStatus() {
        return regionStatus;
    }

    public void setRegionStatus(int regionStatus) {
        this.regionStatus = regionStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAmPmStatue() {
        return amPmStatue;
    }

    public void setAmPmStatue(int amPmStatue) {
        this.amPmStatue = amPmStatue;
    }

    @Override
    public String toString() {
        return "AttendClockVo{" +
                "clockTime=" + clockTime +
                ", time=" + time +
                ", location='" + location + '\'' +
                ", regionStatus=" + regionStatus +
                ", status=" + status +
                ", amPmStatue=" + amPmStatue +
                '}';
    }
}

package richinfo.attendance.entity;

import java.io.Serializable;

/**
 * Created by qiang on 2018/9/4.
 * 打卡多地点
 */


public class AttendClockSite implements Serializable{

    /*地点主键*/
    private long siteId=0;
    /*考勤组id*/
    private  long attendanceId;
    /** 下午上班时间 */
    private String pmTime;

    /** 考勤地点 */
    private String location;

    /** 详细考勤地址 */
    private String detailAddr;

    /** 经度 */
    private double longitude;

    /** 纬度 */
    private double latitude;

    /** 有效范围 (内勤) */
    private int attendanceRange;

    /** 为了兼容短信打卡**/
    private int range;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getPmTime() {
        return pmTime;
    }

    public void setPmTime(String pmTime) {
        this.pmTime = pmTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetailAddr() {
        return detailAddr;
    }

    public void setDetailAddr(String detailAddr) {
        this.detailAddr = detailAddr;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getAttendanceRange() {
        return attendanceRange;
    }

    public void setAttendanceRange(int attendanceRange) {
        this.attendanceRange = attendanceRange;
    }

    @Override
    public String toString() {
        return "AttendClockSite{" +
                "siteId=" + siteId +
                ", attendanceId=" + attendanceId +
                ", pmTime='" + pmTime + '\'' +
                ", location='" + location + '\'' +
                ", detailAddr='" + detailAddr + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", attendanceRange=" + attendanceRange +
                '}';
    }
}

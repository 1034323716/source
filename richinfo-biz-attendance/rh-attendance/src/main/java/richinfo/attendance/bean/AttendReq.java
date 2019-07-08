/**
 * 文件名：AttendReq.java
 * 创建日期： 2017年6月7日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月7日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.bean;

/**
 * 功能描述：考勤模块请求信息类
 * 
 */
public class AttendReq extends AttendReqBean
{

    private static final long serialVersionUID = -4633017022161552224L;

    /** 手机号 */
    private String phone;

    /** 打卡地点 */
    private String location;

    /** 详细地址 */
    private String detailAddr;

    /** 经度 */
    private double longitude;

    /** 纬度 */
    private double latitude;

    /** 考勤状态，公司打卡0 OR 外勤1 */
    private int status;

    /** 外勤备注 */
    private String outWorkRemark;

    //是否允许开启设备打卡功能，0使用1不使用
    private String useEquipmentClock;

    private String equipmentSerial;

    public String getEquipmentSerial() {
        return equipmentSerial;
    }

    public void setEquipmentSerial(String equipmentSerial) {
        this.equipmentSerial = equipmentSerial;
    }

    public String getUseEquipmentClock() {
        return useEquipmentClock;
    }

    public void setUseEquipmentClock(String useEquipmentClock) {
        this.useEquipmentClock = useEquipmentClock;
    }

    public String getOutWorkRemark() {
        return outWorkRemark;
    }

    public void setOutWorkRemark(String outWorkRemark) {
        this.outWorkRemark = outWorkRemark;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getDetailAddr()
    {
        return detailAddr;
    }

    public void setDetailAddr(String detailAddr)
    {
        this.detailAddr = detailAddr;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("AttendReq[uid=").append(super.getUid()).append(",phone=")
            .append(phone).append(",attendanceId=")
            .append(super.getAttendanceId()).append(",location=")
            .append(location).append(",longitude=").append(longitude)
            .append(",latitude=").append(latitude).append(",status=")
            .append(status).append("]");
        return sb.toString();
    }

}

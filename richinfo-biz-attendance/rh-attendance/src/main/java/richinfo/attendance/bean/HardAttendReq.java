/**
* 文件名：HardAttendReq.java
* 创建日期： 2017年8月31日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年8月31日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.bean;

/**
 * 功能描述：
 * 硬件打卡请求bean
 */
public class HardAttendReq extends AttendReq
{
    private static final long serialVersionUID = 54959230408198522L;
    
    /** 打卡时间 */
    private String clockTime;
    
    /** 时间戳 */
    private long curTime;

    public long getCurTime()
    {
        return curTime;
    }

    public void setCurTime(long curTime)
    {
        this.curTime = curTime;
    }

    public String getClockTime()
    {
        return clockTime;
    }

    public void setClockTime(String clockTime)
    {
        this.clockTime = clockTime;
    }
    
}

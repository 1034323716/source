/**
 * 文件名：AttendService.java
 * 创建日期： 2017年6月5日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.service;

import richinfo.attendance.bean.AttendClockVo;
import richinfo.attendance.bean.AttendReq;
import richinfo.attendance.bean.AttendRes;
import richinfo.attendance.bean.HardAttendReq;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：考勤模块接口
 * 
 */
public interface AttendService
{
    /**
     * 查询员工当天考勤记录信息
     * @param employeeId
     * @return
     */
    public AttendRes queryEmployRecord(AttendReq req);

    /**
     * 员工进行考勤打卡
     * @param map
     * @return
     */
    public AttendRes clock(AttendReq attendReq);
    
    /**
     * 员工进行考勤打卡  硬件打卡
     * @param map
     * @return
     */
    public AttendRes hardClock(HardAttendReq attendReq);

}

/**
 * 文件名：AttendScheduleService.java
 * 创建日期： 2018年4月8日
 * 作者：     jiangshengsheng
 * Copyright (c) 2009-2018 
 * All rights reserved.
 */
package richinfo.attendance.service;

import richinfo.attendance.bean.AttendScheduleShiftReq;
import richinfo.attendance.bean.AttendScheduleShiftRsp;

/**
 * 功能描述：考勤模块接口
 * 
 */
public interface AttendScheduleService
{
    /**
     * 查询考勤组排班班次列表
     * @param employeeId
     * @return
     */
    public AttendScheduleShiftRsp queryAttendScheduleShifts(AttendScheduleShiftReq req);
    

    /**
     * 查询考勤组排班列表
     * @param employeeId
     * @return
     */
    public AttendScheduleShiftRsp queryAttendSchedule(AttendScheduleShiftReq req);
 
}

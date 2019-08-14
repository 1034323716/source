/**
 * 文件名：EmployeeMonthStatisticsService.java
 * 创建日期： 2017年6月14日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月14日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.service;

/**
 * 功能描述：员工个人月报详情统计逻辑层
 * 
 */
public interface EmployeeMonthStatisticsService
{
    /**
     * 以考勤组为单位统计考勤状态，考勤组作为属性构造时传递
     */
    void employeeMonthDatailStatistics();

}

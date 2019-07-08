/**
 * 文件名：AtdcConsts.java
 * 创建日期： 2017年6月8日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月8日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.common;

/**
 * 功能描述：考勤打卡系统常量定义类
 * 
 */
public interface AtdcConsts
{

    /**
     * 
     * 功能描述：分隔符常量，公共日历的假日数据
     * 
     */
    public interface SEPARATOR
    {
        /** 节假日列表分隔符 */
        String DATE_LIST = ",";

        /** 日期-描述 分隔符 */
        String DATE_DESC = ":";
    }

    /**
     * 
     * 功能描述：节假日的描述信息常量
     * 
     */
    public interface REMARK
    {
        /** 工作日 */
        String WEEKDAY = "工作日";

        /** 节假日，统称 */
        String HOLIDAY = "节假日";

        /** 休息日 */
        String WEEKEND = "休息日";

        /** 元旦 */
        String NEW_YEAR_DAY = "元旦";

        /** 春节 */
        String SPRING_FESTIVAL = "春节";

        /** 清明节 */
        String QINGMING_FESTIVAL = "清明节";

        /** 劳动节 */
        String LABOR_DAY = "劳动节";

        /** 端午节 */
        String DRAGON_BOAT_FESTIVAL = "端午节";

        /** 中秋节 */
        String MID_AUTUMN_FESTIVAL = "中秋节";

        /** 国庆节 */
        String NATIONAL_DAY = "国庆节";
    }

    /**
     * 
     * 功能描述：星期的固定描述值
     * 
     */
    public interface WEEK
    {
        String SUNDAY = "星期天";

        String MONDAY = "星期一";

        String TUESDAY = "星期二";

        String WEDNESDAY = "星期三";

        String THURSDAY = "星期四";

        String FRIDAY = "星期五";

        String SATURDAY = "星期六";
    }

    /**
     * 
     * 功能描述：考勤描述常量类
     * 
     */
    public interface ATTEND_DESC
    {
        /** 正常 */
        String NORMAL = "正常";

        /** 早退 */
        String EARLY = "早退";

        /** 迟到 */
        String LATE = "迟到";

        /** 未打卡 */
        String NOT_CLOCKED = "未打卡";
    }
    
    /**
     * 
     * 功能描述：业务常量
     *
     */
    public interface ATTEND_BIZ
    {
        /** 考勤报表下载文件名称分隔符*/
        String DOWNLOAD_FILENAME_SEPARATOR = "#_#";
    }
}

/**
 * 文件名：TimeUtil.java
 * 创建日期： 2017年6月2日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月2日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.util;

import com.alibaba.fastjson.JSON;
import richinfo.attendance.common.AtdcConsts;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述：考勤打卡时间工具类
 * 
 */
public final class AtdcTimeUtil
{
    /** 上班时间分隔符 */
    public static final String SEPARATOR = "-";

    /** 年份格式 yyyy */
    public static final String YEAR_FORMAT = "yyyy";

    /** 年月格式 yyyy-MM */
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";

    /**
     * 校验考勤组上班时间是否合法，要求格式：09:00
     * @param time
     * @return
     */
    public static boolean isWorkTimeLegal(String time)
    {
        if (AssertUtil.isEmpty(time))
        {
            return false;
        }

       // String regex = "^(([0-1][0-9])|2[0-3]):[0-5][0-9]-((([0-1][0-9])|2[0-3]):[0-5][0-9]|24:00)$";
        String regex = "^(([0-1][0-9])|2[0-3]):[0-5][0-9]$";

        if (time.matches(regex))
        {
                return true;
        }
        return false;
    }

    /**
     * 判断上午上班时间是否早于下午上班时间，对比上午结束时间和下午开始时间
     * @param amTime 上午上班时间，需通过格式校验
     * @param pmTime 下午上班时间，需通过格式校验
     * @return 小于0，上午早于下午；等于0，上午等于下午；大于0，上午晚于下午
     */
    public static int compareAmtimeAndPmtime(String amTime, String pmTime)
    {
        // am、pm需经过格式校验
       /* String[] am = amTime.split(SEPARATOR);
        String[] pm = pmTime.split(SEPARATOR);*/
        // 对比 上午班次的结束时间 和 下午班次的开始时间
        return amTime.compareTo(pmTime);
    }

    /**
     * 获取上班时间段的起始时间
     * @param workTime 上班时间段 eg:09:00-11:30
     * @return 起始时间 eg:09:00
     */
    public static String getStartTime(String workTime)
    {
        // 校验时间格式
        if (AssertUtil.isNotEmpty(workTime))
        {
            String[] temp = workTime.split(SEPARATOR);
            // 返回开始时间
            return temp[0];
        }
        return "";
    }

    /**
     * 获取上班时间段的结束时间
     * @param workTime 上班时间段 eg:09:00-11:30
     * @return 结束时间 eg:11:30
     */
    public static String getEndTime(String workTime)
    {
        // 校验时间格式
        if (AssertUtil.isNotEmpty(workTime))
        {
            String[] temp = workTime.split(SEPARATOR);
            // 返回结束时间
            return temp[1];
        }
        return "";
    }

    /**
     * 获取下一年的年份
     * @return 格式：YYYY,eg:2018
     */
    public static String getNextYear()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 年份+1
        cal.add(Calendar.YEAR, 1);
        // 取年份
        SimpleDateFormat sdf = new SimpleDateFormat(YEAR_FORMAT);
        return sdf.format(cal.getTime());
    }

    /**
     * 星期描述，返回常量类中的固定常量
     * @param date 日期
     * @return
     */
    public static String getWeekDesc(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        String desc = "";
        switch (week)
        {
        case Calendar.SUNDAY:
            desc = AtdcConsts.WEEK.SUNDAY;
            break;
        case Calendar.MONDAY:
            desc = AtdcConsts.WEEK.MONDAY;
            break;
        case Calendar.TUESDAY:
            desc = AtdcConsts.WEEK.TUESDAY;
            break;
        case Calendar.WEDNESDAY:
            desc = AtdcConsts.WEEK.WEDNESDAY;
            break;
        case Calendar.THURSDAY:
            desc = AtdcConsts.WEEK.THURSDAY;
            break;
        case Calendar.FRIDAY:
            desc = AtdcConsts.WEEK.FRIDAY;
            break;
        case Calendar.SATURDAY:
            desc = AtdcConsts.WEEK.SATURDAY;
            break;
        default:
            ;
        }
        return desc;
    }
    
    /**
     * 星期描述，返回常量周
     * @param
     * @return
     */
    public static int getWeekNum(String week)
    {
 
        int desc = 1;
        switch (week)
        {
        case AtdcConsts.WEEK.SUNDAY:
            desc = 7;
            break;
        case AtdcConsts.WEEK.MONDAY:
            desc = 1;
            break;
        case AtdcConsts.WEEK.TUESDAY:
            desc = 2;
            break;
        case AtdcConsts.WEEK.WEDNESDAY:
            desc = 3;
            break;
        case AtdcConsts.WEEK.THURSDAY:
            desc = 4;
            break;
        case AtdcConsts.WEEK.FRIDAY:
            desc = 5;
            break;
        case AtdcConsts.WEEK.SATURDAY:
            desc = 6;
            break;
        default:
            ;
        }
        return desc;
    }
    
    /**
     * 根据日历获取星期数
     * @param date 日期
     * @return 1-7 （1:周一，2:周二 。。。7:周日
     */
    public static int getWeek(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK)-1;
        if(week == 0)
        {
        	week = 7;
        }
        return week;
    }

    /**
     * 获取该年的所有日期列表
     * @param year 年份，格式：yyyy.eg:2018
     * @return
     */
    public static List<Date> getDateOfYear(String year)
    {
        List<Date> dates = new ArrayList<Date>();

        // 开始时间
        Date begin = getFirstDayOfYear(year);
        // 结束日期
        Date end = getDayOfNextYear(begin);

        // 设置cal，便于日期自增
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);

        Date temp = begin;
        while (temp.compareTo(end) < 0)
        {
            // 加入到列表中
            dates.add(temp);
            // 日期+1
            cal.add(Calendar.DATE, 1);
            temp = cal.getTime();
        }

        return dates;
    }

    /**
     * 获取年份的第一天
     * @param year
     * @return
     */
    public static Date getFirstDayOfYear(String year)
    {
        Calendar cal = Calendar.getInstance();
        // 默认年份，下一年
        int defaultYear = cal.get(Calendar.YEAR) + 1;
        // 起始日期，该年的1月1日
        cal.set(ConverUtil.string2Int(year, defaultYear), 0, 1, 0, 0, 0);
        return cal.getTime();
    }

    /**
     * 获取下一年的日期，即，年份+1
     * @param date
     * @return
     */
    public static Date getDayOfNextYear(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 直接年份+1
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    /**
     * 获取当前年月，格式：yyyy-MM.eg:2017-06
     * @return
     */
    public static String getCurrentYearMonth()
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * 获取年月，格式：yyyy-MM.eg:2017-06
     * @return
     */
    public static String getCurrentYearMonth(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH_FORMAT);
        return sdf.format(date);
    }

    /**
     * 获取昨天的日期，格式：yyyy-MM-dd.eg:2017-06-10
     * @return
     */
    public static String getYesterday(String sFormat)
    {
        Date date = getYesterdayDate();
        // 格式化
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        return sdf.format(date);
    }

    /**
     * 获取昨天的日期
     * @return
     */
    public static Date getYesterdayDate()
    {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 日期-1
        cal.add(Calendar.DATE, -1);

        return cal.getTime();
    }
    
    /**
     * 获取前天的日期
     * @return
     */
    public static Date getBeforeYesterdayDate()
    {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 日期-1
        cal.add(Calendar.DATE, -2);

        return cal.getTime();
    }

    /**
     * 拼接考勤日期和考勤时间，返回对应的毫秒数，便于前端进行时间的格式化
     * @param attendanceDate 考勤日期，数据库中有效数据为date部分，eg：2017-06-22
     * @param attendanceTime 考勤时间，数据库中有效数据为time部分，eg：16:20:00
     * @param clockDate 打卡日期  2017-06-22
     * @return 
     */
    public static long togetherDateAndTime(Date attendanceDate,
        Date attendanceTime,Date clockDate)
    {
        if (AssertUtil.isEmpty(attendanceDate)
            || AssertUtil.isEmpty(attendanceTime))
        {
            return 0L;
        }
        // date部分格式化
        String attendanceDateStr = TimeUtil.date2String(attendanceDate,
            TimeUtil.BASE_DATE_FORMAT);
        
        // time部分格式化
        String attendanceTimeStr = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        // 拼接为datetime格式
        String times = null;
        if(AssertUtil.isNotEmpty(clockDate)){
        	String clockDateStr = TimeUtil.date2String(clockDate,
                    TimeUtil.BASE_DATE_FORMAT);
        	times = clockDateStr + " " + attendanceTimeStr;
        }else{
        	times = attendanceDateStr + " " + attendanceTimeStr;
        }

        long tt = TimeUtil.string2Date(times, TimeUtil.BASE_DATETIME_FORMAT)
            .getTime();

        return tt;
    }

    /**
     * 拼接考勤日期和考勤时间，返回拼接字符串，供导出报表使用
     * @param attendanceDate 考勤日期，数据库中有效数据为date部分，eg：2017-06-22
     * @param attendanceTime 考勤时间，数据库中有效数据为time部分，eg：16:20:00
     * @return
     */
    public static String mergeDateAndTime(Date attendanceDate,
        Date attendanceTime,Date clockDate)
    {
        if (AssertUtil.isEmpty(attendanceDate)
            || AssertUtil.isEmpty(attendanceTime))
        {
            return null;
        }
        // date部分格式化
        String attendanceDateStr = TimeUtil.date2String(attendanceDate,
            TimeUtil.BASE_DATE_FORMAT);
        // time部分格式化
        String attendanceTimeStr = TimeUtil.date2String(attendanceTime,
            TimeUtil.BASE_TIME_FORMAT);
        // 拼接为datetime格式
        String times = null;
        if(AssertUtil.isNotEmpty(clockDate)){
        	String clockDateStr = TimeUtil.date2String(clockDate,
                    TimeUtil.BASE_DATE_FORMAT);
        	times = clockDateStr + " " + attendanceTimeStr;
        }else{
        	times = attendanceDateStr + " " + attendanceTimeStr;
        }
        return times;
    }
    
    /**
     * 判断是否是月末
     * @param date
     * @return
     */
    public static boolean isMonthEnd(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.DATE) == calendar
            .getActualMaximum(Calendar.DAY_OF_MONTH))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 校验考勤组考勤月份是否合法，要求格式：2000-01
     * @param time
     * @return
     */
    public static boolean isAttendanceMonthLegal(String time)
    {
        if (AssertUtil.isEmpty(time))
        {
            return false;
        }

        String regex = "^(([1-9][0-9][0-9][0-9])|2[0-3])-((0[1-9])|(1[0-2]))$";

        if (time.matches(regex))
        {
            String[] temp = time.split(SEPARATOR);
            // 校验格式
            if (temp.length == 2)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取下月的(年月)，格式：yyyy-MM.eg:2017-06
     * @return
     */
    public static String getNextMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);
        return TimeUtil.formatDateTime(calendar.getTime(), YEAR_MONTH_FORMAT);
    }

    /**
     * 获取某天date的第二天的时间
     * @return
     */
    public static Date getNextDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
    
    /**
     * 判断是否是跨天的工作时间 
     * 前一天晚上到早上这种格式 eg：22:00-08:00,这种格式的时间即为跨天的时间
     * @param workTime
     * @return
     */
    public static boolean isCrossWorkTime(String workTime)
    {
        if (AssertUtil.isEmpty(workTime))
        {
            return false;
        }

        String times[] = workTime.split("-");
        if (times.length < 2)
        {
            return false;
        }
        else if (times[0].compareTo(times[1]) > 0)
        {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String test = "{\"1\":{\"amTime\":\"09:00-12:00\",\"pmTime\":\"12:00-18:00\"},\"2\":{\"amTime\":\"09:00-12:00\",\"pmTime\":\"12:00-18:00\"},\"3\":{\"amTime\":\"09:00-12:00\",\"pmTime\":\"12:00-18:00\"}}";
        Map jsonObject = JSON.parseObject(test);
        List<Integer> dayNum = new ArrayList<Integer>(jsonObject.keySet());
        System.out.println(dayNum);
        System.out.println(dayNum.contains("1"));
        System.out.println(dayNum.get(dayNum.size()-1));
        System.out.println(dayNum.get(0));
    }
}

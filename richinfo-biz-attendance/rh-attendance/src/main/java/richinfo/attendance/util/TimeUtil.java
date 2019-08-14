package richinfo.attendance.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static richinfo.tools.common.AssertUtil.isEmpty;

/**
 * 与日期时间有关的一组工具方法。
 * 
 * @author CHENJL
 * @version 1.0
 */
public final class TimeUtil
{

    /** 取年运算 */
    public static final byte YEAR = 1;
    /** 取月运算 */
    public static final byte MONTH = 2;
    /** 取日运算 */
    public static final byte DAY = 3;
    /** 取小时运算 */
    public static final byte HOUR = 4;
    /** 取分钟运算 */
    public static final byte MINUTE = 5;
    /** 取秒运算 */
    public static final byte SECOND = 6;
    /** 日期时间基本格式 yyyy-MM-dd HH:mm:ss */
    public static final String BASE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 日期时间基本格式 yyyy-MM-dd HH:mm:ss:SSS A类型 */
    public static final String DATETIME_FORMAT_SSS_A = "yyyy-MM-dd HH:mm:ss:SSS";
    /** 日期时间基本格式 yyyyMMdd HH:mm:ss:SSS B类型 */
    public static final String DATETIME_FORMAT_SSS_B = "yyyyMMdd HH:mm:ss:SSS";
    /** 日期时间基本格式 yyyy-MM-dd HH:mm:ss.SSS C类型 */
    public static final String DATETIME_FORMAT_SSS_C = "yyyy-MM-dd HH:mm:ss.SSS";
    /** 日期时间基本格式 yyyyMMddHHmmssSSS */
    public static final String DATETIME_FORMAT_yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
    /** 日期基本格式 yyyy-MM */
    public static final String BASE_DATE_FORMAT_YYYY_MM = "yyyy-MM";
    /** 日期基本格式 yyyy-MM-dd */
    public static final String BASE_DATE_FORMAT = "yyyy-MM-dd";
    /** 时间基本格式HH:mm:ss */
    public static final String BASE_TIME_FORMAT = "HH:mm:ss";
    /** 年月基本格式yyMM */
    public static final String BASE_Y_M = "yyMM";
    /** 日期长度 */
    private static final byte BASE_DATE_FORMAT_LEN = 10;
    /** 一天的毫秒数 */
    private static final long ONE_DAY_INTERVAL = 1000 * 3600 * 24;

    private static final Logger LOG = LoggerFactory.getLogger(TimeUtil.class);

    /** 短日期格式化对象 yyyy-MM-dd */
    private static ThreadLocal<DateFormat> tlShortDateFormat = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat(BASE_DATE_FORMAT);
        }
    };

    /** 短时间格式化对象 HH:mm:ss */
    private static ThreadLocal<DateFormat> tlShortTimeFormat = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat(BASE_TIME_FORMAT);
        }
    };

    /** 长日期时间格式化对象 yyyy-MM-dd HH:mm:ss */
    private static ThreadLocal<DateFormat> tlLongDateFormat = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat(BASE_DATETIME_FORMAT);
        }
    };

    /** 时间格式正则表达式 ,精确到分钟 */
    private static final Pattern P_TIME = Pattern
        .compile("^([0-1]?[0-9]|2[0-3]):([0-5][0-9])$");

    /** 时间格式正则表达式 精确到秒 */
    private static final Pattern P_TIME_SEC = Pattern
        .compile("^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$");

    private TimeUtil()
    {
    }

    /**
     * 获取当前日期/时间或日期时间,分不同的格式，如:yyyyMMdd、yyyy-MM-dd 如果sFormat为null将返回yyyy-MM-dd格式
     * 调用示例：getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
     * 
     * @param sFormat 期待返回的格式如下: yyyy-MM-dd yyy年MM月dd日 yyyy-MM-dd HH:mm:ss sss
     *        yyyy年MM月dd日 HH时mm分ss秒 HH:mm:ss 时间格式中,大写HH表示为24小时制,小写hh表示为12小时制.
     * @return 按sFormat返回的当前日期字符串
     */
    public static String getCurrentDateTime(String sFormat)
    {
        if (isEmpty(sFormat))
        {
            sFormat = BASE_DATE_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        return sdf.format(new Date());
    }

    public static Date dateToTimeData(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String format = simpleDateFormat.format(date);

        try {
            return simpleDateFormat.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
            LOG.error("Failed format date.", e);
        }
        return null;
    }

    /**
     * 日期时间加运算，可以加年，月，日，时，分，秒的任一部分。
     * 调用示例：plusDateTime("12:12:23",BASE_TIME_FORMAT, HOUR, 10)
     * 
     * @param sDateTime 在该日期上加运算
     * @param sFormat sDateTime的格式，返回时也是该格式。
     * @param plusPart 需增加的部分，有常量YEAR,MONTH,DAY....
     * @param size 增加基数，非负数.
     * @return 返回sFormat制定的格式字符串
     */
    public static String plusDateTime(String sDateTime, String sFormat,
        byte plusPart, int size)
    {
        if (isEmpty(sDateTime) || plusPart <= 0 || size < 0 || isEmpty(sFormat))
        {
            return null;
        }
        return calculateDateTime(sDateTime, sFormat, plusPart, size);
    }

    /**
     * 日期时间减运算，可以减年，月，日，时，分，秒的任一部分。
     * 调用示例：minusDateTime("12:12:23",BASE_TIME_FORMAT, HOUR, 10)
     * 
     * @param sDateTime 在该日期上减运算
     * @param sFormat sDateTime的格式，返回时也是该格式。
     * @param minusPart 需减少的部分，有常量YEAR,MONTH,DAY....
     * @param size 减少的基数，非负数.
     * @return 返回sFormat制定的格式字符串
     */
    public static String minusDateTime(String sDateTime, String sFormat,
        byte minusPart, int size)
    {
        if (isEmpty(sDateTime) || minusPart <= 0 || isEmpty(sFormat))
        {
            return null;
        }
        return calculateDateTime(sDateTime, sFormat, minusPart, -size);
    }

    /**
     * 日期时间运算，可以对年，月，日，时，分，秒的任一部分进入加减运算，该方法是私有，
     * 外部通过plusDateTime和minusDateTime间接调用
     * 调用示例：calculateDateTime("12:12:23",BASE_TIME_FORMAT, HOUR, 10)
     * 
     * @param sDateTime 在该日期上运算
     * @param sFormat sDateTime的格式，返回时也是该格式。
     * @param plusPart 需运算的部分，有常量YEAR,MONTH,DAY....
     * @param size 运算基数，任意整数，可以为负数，为负数时即为减运算，为正数时，即为加运算.
     * @return
     */
    private static String calculateDateTime(String sDateTime, String sFormat,
        byte plusPart, int size)
    {
        Calendar cal = Calendar.getInstance();
        try
        {
            if (sFormat.equals(BASE_TIME_FORMAT))
            {
                cal.setTime(string2Time(sDateTime));
            }
            else if (sFormat.equals(BASE_DATE_FORMAT))
            {
                cal.setTime(string2Date(sDateTime));
            }
            else if (sFormat.equals(BASE_DATETIME_FORMAT))
            {
                cal.setTime(string2DateTime(sDateTime)); // 设置长格式当前日期时间yyyy-MM-dd
            }
            else
            { // 格式不正确
                return null;
            }
        }
        catch (ParseException e)
        {
            return null;
        }
        catch (Exception ex)
        {
            return null;
        }
        setTime(cal, plusPart, size);
        return formatDateTime(cal.getTime(), sFormat);
    }

    /**
     * 设置时间
     * @param cal 当前时间对象
     * @param plusPart 运算
     * @param size 运算基数
     */
    private static void setTime(Calendar cal, int plusPart, int size)
    {
        switch (plusPart)
        {
        case YEAR:
            cal.add(Calendar.YEAR, size);
            break;
        case MONTH:
            cal.add(Calendar.MONTH, size);
            break;
        case DAY:
            cal.add(Calendar.DATE, size);
            break;
        case HOUR:
            cal.add(Calendar.HOUR, size);
            break;
        case MINUTE:
            cal.add(Calendar.MINUTE, size);
            break;
        case SECOND:
            cal.add(Calendar.SECOND, size);
            break;
        default:
        }
    }

    /**
     * 在某日期上加几天,如2010-06-21, 加1天,即后延1天,返回2010-06-21 转换失败将返回null.
     * 
     * @param sDate 需要增加天数的起始日期,必须为10位日期格式,如2010-06-21.
     * @param intDay 需要加的天数
     * @return 返回sDate日期加上intDay的天数后的日期
     */
    public static String plusDays(String sDate, int intDay)
    {
        if (intDay < 0)
        { // 必须为正整数
            return null;
        }
        if (!isEmpty(sDate) && sDate.length() == BASE_DATE_FORMAT_LEN)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(BASE_DATE_FORMAT);
            Date dt;
            try
            {
                dt = sdf.parse(sDate);
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(dt);
                rightNow.add(Calendar.DAY_OF_YEAR, intDay);// 日期加多少天
                Date dt1 = rightNow.getTime();
                return sdf.format(dt1);
            }
            catch (ParseException e)
            {
                LOG.error("Failed format date.", e);
            }
        }
        return null;
    }

    /**
     * 在某日期上加月数,即后延N个月,如:2010-06-23,后延1月为:2010-07-23
     * sDate为null将返回null,intMonth必须为正整数,否则返回null 转换失败将返回null.
     * 
     * @param sDate 需要后延的起始月份.
     * @param intMonth 后延月份数
     * @return 返回在sDate起始,后延intMonth的日期字符串.
     */
    public static String plusMonth(String sDate, int intMonth)
    {
        if (intMonth < 0)// 必须为正整数
        {
            return null;
        }
        if (!isEmpty(sDate) && sDate.length() == BASE_DATE_FORMAT_LEN)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(BASE_DATE_FORMAT);
            Date dt;
            try
            {
                dt = sdf.parse(sDate);
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(dt);
                rightNow.add(Calendar.MONTH, intMonth);// 日期加多少天
                Date dt1 = rightNow.getTime();
                return sdf.format(dt1);
            }
            catch (ParseException e)
            {
                LOG.error("Failed format date.", e);
            }
        }
        return null;
    }

    /**
     * 在某日期上减几天,如2010-06-21, 减1天,即前推1天,返回2010-06-21 转换失败将返回null.
     * 
     * @param sDate 在该日期上开始减少,必须为10位日期格式,如2010-06-21
     * @param intDay 需要减的天数.
     * @return 运算后的日期字符串
     */
    public static String minusDays(String sDate, int intDay)
    {
        if (intDay < 0)
        {
            return null;
        }
        if (!isEmpty(sDate) && sDate.length() == BASE_DATE_FORMAT_LEN)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(BASE_DATE_FORMAT);
            Date dt;
            try
            {
                dt = sdf.parse(sDate);
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(dt);
                rightNow.add(Calendar.DAY_OF_YEAR, -intDay);// 日期减多少天
                Date dt1 = rightNow.getTime();
                return sdf.format(dt1);
            }
            catch (ParseException e)
            {
                LOG.error("Failed format date.", e);
            }
        }
        return null;
    }

    /**
     * 在某日期上减月数,即前推N个月,如2010-06-12,前推2月为:2010-04-12. 转换失败将返回null.
     * 
     * @param sDate 在该日期上前推
     * @param intMonth 前推的月数
     * @return 返回sDate日期上前推intMonth月,得到的日期字符串.
     */
    public static String minusMonth(String sDate, int intMonth)
    {
        if (intMonth < 0)
        {
            return null;
        }
        if (!isEmpty(sDate) && sDate.length() == BASE_DATE_FORMAT_LEN)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(BASE_DATE_FORMAT);
            Date dt;
            try
            {
                dt = sdf.parse(sDate);
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(dt);
                rightNow.add(Calendar.MONTH, -intMonth);// 日期加多少天
                Date dt1 = rightNow.getTime();
                return sdf.format(dt1);
            }
            catch (ParseException e)
            {
                LOG.error("Failed format date.", e);
            }
        }
        return null;
    }

    /**
     * 根据传入的日期和指定的字符串格式,返回符合sFormat指定格式的日期字符串
     * 
     * @param date 需要格式化的日期
     * @param sFormat 字符串格式,如:yyyy-MM-dd
     * @return 返回符合sFormat指定格式的日期字符串
     */
    public static String formatDateTime(Date date, String sFormat)
    {
        if (date == null)
        {
            return null;
        }
        if (isEmpty(sFormat))
        {
            sFormat = BASE_DATE_FORMAT;
        }
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        return sf.format(date);
    }

    /**
     * 根据传入的日期和指定的字符串格式,返回符合sFormat指定格式的日期字符串
     * 
     * @param date 需要格式化的日期,格式为yyyy-MM-dd HH:mm:ss,如果参数为null将返回null.
     * @param sFormat 字符串格式,如:yyyy-MM-dd,如果为null将默认为yyyy-MM-dd HH:mm:ss
     * @return 返回符合sFormat指定格式的日期字符串,转换异常将返回null.
     */
    public static String formatDateTime(String date, String sFormat)
    {
        if (isEmpty(date))
        {
            return null;
        }
        if (isEmpty(sFormat))
        {
            sFormat = BASE_DATETIME_FORMAT;
        }
        SimpleDateFormat sf = new SimpleDateFormat(BASE_DATETIME_FORMAT);
        Date d = null;
        try
        {
            d = sf.parse(date);
            sf = new SimpleDateFormat(sFormat);
            return sf.format(d);
        }
        catch (ParseException e)
        {
            LOG.error("Failed format date,date={} ,sformat={} ,error=", date,
                sFormat, e);
        }
        return null;
    }

    /**
     * 根据传入的字符串日期及日期格式，转换成指定类型的字符串日期格式. 注意：如果格式不对或者参数非法返回date参数.
     * 
     * @param date 需要格式化的日期,格式为srcFormat指定.
     * @param srcFormat 原日期格式,如:yyyy-MM-dd,yyyy-MM-dd HH:mm:ss.
     * @param destFormat 待转换的日期格式,如:yyyy-MM-dd,yyyy-MM-dd HH:mm:ss.
     * @return 返回符合destFormat指定格式的日期字符串.
     */
    public static String formatDateTime(String date, String srcFormat,
        String destFormat)
    {
        if (isEmpty(date) || isEmpty(srcFormat))
        {
            return date;
        }
        SimpleDateFormat sf = new SimpleDateFormat(srcFormat);
        Date d = null;
        try
        {
            d = sf.parse(date);
            sf = new SimpleDateFormat(destFormat);
            return sf.format(d);
        }
        catch (ParseException e)
        {

        }
        return date;
    }

    /**
     * 获取小时分钟,格式为hh:mm:ss. 如输入233将返回2:33:00秒,如果格式不对将返回00:00:00.
     * 
     * @param hourAndMinute 整数类型的时间，223表示2点23分,1223表示12点23分.
     * @return 返回时分秒，格式hh:mm:ss 秒默认为00.
     */
    public static String getHourAndMinute(int hourAndMinute)
    {
        String temp = String.valueOf(hourAndMinute);
        String resultTime = null;
        if (AssertUtil.isNotEmpty(temp) && temp.length() == 3)
        {
            resultTime = temp.substring(0, 1) + ":" + temp.substring(1, 3)
                + ":00";
        }
        else if (AssertUtil.isNotEmpty(temp) && temp.length() == 4)
        {
            resultTime = temp.substring(0, 2) + ":" + temp.substring(2, 4)
                + ":00";
        }
        else
        {
            resultTime = "00:00:00";// 默认值
        }
        return resultTime;
    }

    /**
     * 取当年,如2010
     * 
     * @return 返回当前四位年份
     */
    public static String getCurrentYear()
    {
        return getCurrentDateTime("yyyy");
    }

    /**
     * 取当月份,如06
     * 
     * @return 返回当前二位月份
     */
    public static String getCurrentMonth()
    {
        return getCurrentDateTime("MM");
    }

    /**
     * 取当天,如21
     * 
     * @return 返回当天
     */
    public static String getCurrentDay()
    {
        return getCurrentDateTime("dd");
    }

    /**
     * 取当前小时
     * 
     * @return 返回当前二位小时
     */
    public static String getCurrentHour()
    {
        return getCurrentDateTime("HH");
    }

    /**
     * 取当前分钟
     * 
     * @return 返回当前二位分钟
     */
    public static String getCurrentMinute()
    {
        return getCurrentDateTime("mm");
    }

    /**
     * 根据日期获取年份
     * 
     * @param date 指定的日期对象
     * @return 返回4位年份
     */
    public static String getDateTimeOfYear(Date date)
    {
        return formatDateTime(date, "yyyy");
    }

    /**
     * 根据字符串的长度不同，生成不同的simpleDateFormat对象. yyyy-MM-dd HH:mm:ss yyyy-MM-dd
     * HH:mm:ss
     * @param date 日期字符串.
     * @return SimpleDateFormat对象.
     */
    private static SimpleDateFormat getDateFormat(String date)
    {
        SimpleDateFormat sf = null;
        if (date.length() == 10)
        {
            sf = new SimpleDateFormat(BASE_DATE_FORMAT);
        }
        else if (date.length() == 19)
        {
            sf = new SimpleDateFormat(BASE_DATETIME_FORMAT);
        }
        else if (date.length() == 8)
        {
            sf = new SimpleDateFormat(BASE_TIME_FORMAT);
        }
        return sf;
    }

    /**
     * 根据日期获取年份,获取失败将返回null.
     * 
     * @param date 指定的日期对象字符串,格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd,HH:mm:ss
     * @return 返回4位年份,格式yyyy
     */
    public static String getDateTimeOfYear(String date)
    {
        SimpleDateFormat sf = getDateFormat(date);
        try
        {
            return formatDateTime(sf.parse(date), "yyyy");
        }
        catch (ParseException e)
        {

        }
        return null;
    }

    /**
     * 根据日期获取月份
     * 
     * @param date 指定的日期对象
     * @return 返回2位月份,格式MM
     */
    public static String getDateTimeOfMonth(Date date)
    {
        return formatDateTime(date, "MM");
    }

    /**
     * 根据日期获取月份,获取失败将返回null.
     * 
     * @param date 指定的日期对象,格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd
     * @return 返回2位月份,格式MM
     */
    public static String getDateTimeOfMonth(String date)
    {
        SimpleDateFormat sf = getDateFormat(date);
        try
        {
            return formatDateTime(sf.parse(date), "MM");
        }
        catch (ParseException e)
        {

        }
        return null;
    }

    /**
     * 根据日期获取所在天
     * 
     * @param date 日期
     * @return 返回天数,格式dd
     * 
     */
    public static String getDateTimeOfDay(Date date)
    {
        return formatDateTime(date, "dd");
    }

    /**
     * 根据日期获取所在天,获取失败将返回null.
     * 
     * @param date 日期,格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd
     * @return 返回天数,格式dd
     * 
     */
    public static String getDateTimeOfDay(String date)
    {
        SimpleDateFormat sf = getDateFormat(date);
        try
        {
            return formatDateTime(sf.parse(date), "dd");
        }
        catch (ParseException e)
        {

        }
        return null;
    }

    /**
     * 根据日期时间取小时
     * 
     * @param date 指定日期对象
     * @return 小时,格式HH
     */
    public static String getDateTimeOfHour(Date date)
    {
        return formatDateTime(date, "HH");
    }

    /**
     * 根据日期时间取小时,获取失败将返回null.
     * 
     * @param date 指定日期对象,格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd,HH:mm:ss
     * @return 小时,格式HH
     */
    public static String getDateTimeOfHour(String date)
    {
        SimpleDateFormat sf = getDateFormat(date);
        try
        {
            return formatDateTime(sf.parse(date), "HH");
        }
        catch (ParseException e)
        {

        }
        return null;
    }

    /**
     * 根据日期时间取分钟
     * 
     * @param date 指定日期对象
     * @return 返回分钟,格式mm
     */
    public static String getDateTimeOfMinute(Date date)
    {
        return formatDateTime(date, "mm");
    }

    /**
     * 根据日期时间取分钟,获取失败将返回null.
     * 
     * @param date 指定日期对象,格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd,HH:mm:ss
     * @return 返回分钟,格式mm
     */
    public static String getDateTimeOfMinute(String date)
    {
        SimpleDateFormat sf = getDateFormat(date);
        try
        {
            return formatDateTime(sf.parse(date), "mm");
        }
        catch (ParseException e)
        {

        }
        return null;
    }

    /**
     * 根据日期时间获取年月,获取失败将返回null.
     * @param date 日期字符串,格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd,HH:mm:ss
     * @return 返回年月 yyyyMM.
     */
    public static String getDateTimeOfYearAndMonth(String date)
    {
        SimpleDateFormat sf = getDateFormat(date);
        try
        {
            return formatDateTime(sf.parse(date), "yyyy-MM");
        }
        catch (ParseException e)
        {

        }
        return null;
    }

    /**
     * 根据日期时间获取月日,获取失败将返回null.
     * @param date 日期字符串,格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd,HH:mm:ss
     * @return 返回年月 yyyyMM.
     */
    public static String getDateTimeOfMonthAndDay(String date)
    {
        SimpleDateFormat sf = getDateFormat(date);
        try
        {
            return formatDateTime(sf.parse(date), "MM-dd");
        }
        catch (ParseException e)
        {

        }
        return null;
    }

    /**
     * 根据合法的日期字符串,构建一个日期并返回,如果sDate为null将返回null
     * 
     * @param sDate 日期,格式:yyyy-MM-dd
     * @return Date 生成的日期对象
     */
    public static Date string2Date(String sDate) throws ParseException
    {
        if (isEmpty(sDate))
        {
            return null;
        }
        return ((DateFormat) tlShortDateFormat.get()).parse(sDate);
    }

    /**
     * 将格式为yyyy-MM-dd HH:mm:ss的日期字符串,转换成日期时间类型. sDateTime为null将返回null
     * 
     * @param sDateTime 格式为yyyy-MM-dd HH:mm:ss的字符串.
     * @return 返回日期时间
     * @throws ParseException 格式不正确,抛出异常
     */
    public static Date string2DateTime(String sDateTime) throws ParseException
    {
        if (isEmpty(sDateTime))
        {
            return null;
        }
        return ((DateFormat) tlLongDateFormat.get()).parse(sDateTime);
    }

    /**
     * 将格式为HH:mm:ss时间字符串,转换成时间. 如果sTime为null将返回null
     * 
     * @param sTime 格式为HH:mm:ss字符串
     * @return 返回时间
     * @throws ParseException 格式不正确,抛出异常.
     */
    public static Date string2Time(String sTime) throws ParseException
    {
        if (isEmpty(sTime))
        {
            return null;
        }
        return ((DateFormat) tlShortTimeFormat.get()).parse(sTime);
    }

    /**
     * 将格式为yyyyMMdd的八位日期字符串转换成格式为yyyy-MM-dd的十位日期字符串 strDate为null或者长度不符合要求将返回null
     * 
     * @param strDate 格式为yyyyMMdd待转换日期串
     * @return 返回转换后格式为yyyy-MM-dd的字符串.
     */
    public static String octetDate2TensDate(String strDate)
    {

        if (isEmpty(strDate))
        {
            return null;
        }
        if (strDate.length() != 8)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder(strDate);
        sb.insert(4, "-");
        sb.insert(7, "-");
        return sb.toString();
    }

    /**
     * 将格式为yyyy-MM-dd的十位日期字符串转换成格式为yyyyMMdd的八位日期字符串 strDate如果为null或者长度不符合将返回null
     * 
     * @param strDate 格式为yyyy-MM-dd待转换日期串
     * @return 返回转换后格式为yyyyMMdd的字符串.
     */
    public static String tensDate2OctetDate(String strDate)
    {

        if (isEmpty(strDate))
        {
            return null;
        }
        if (strDate.length() != BASE_DATE_FORMAT_LEN)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder(strDate);
        sb.replace(4, 5, "");// 将-替换成""
        sb.replace(6, 7, "");
        return sb.toString();
    }

    /**
     * 验证一个字符串是否是合法的日期格式
     * 
     * @param strDate 日期字符串
     * @return 返回Boolean值,如果是合法的日期格式,返回true;否则返回false;
     */

    public static boolean isDate(String strDate)
    {

        if (isEmpty(strDate))
        {
            return false;
        }
        if (strDate.length() == 8)
        {
            return isDate(octetDate2TensDate(strDate), "-");
        }
        else if (strDate.length() == BASE_DATE_FORMAT_LEN)
        {
            return isDate(strDate, "-");
        }
        else
        {
            return false;
        }

    }

    /**
     * 验证一个字符串是否是合法的日期格式
     * 
     * @param strDate 日期字符串
     * @param splitChar 年月日之间的分隔符
     * @return 返回Boolean值,如果是合法的日期格式,返回true;否则返回false;
     */
    public static boolean isDate(String strDate, String splitChar)
    {

        if (isEmpty(strDate))
        {
            return false;
        }
        String[] arrDate; // 分别存储年月日
        arrDate = strDate.split("\\" + splitChar);

        if (arrDate.length != 3)
        {
            return false;
        }
        if (arrDate[0].length() != 4 || arrDate[1].length() > 2
            || arrDate[2].length() > 2 || arrDate[1].length() < 1
            || arrDate[2].length() < 1)
        {
            return false;
        }
        int year = 0;
        int month = 0;
        int day = 0;

        year = ConverUtil.string2Int(arrDate[0], -1);
        month = ConverUtil.string2Int(arrDate[1], -1);
        day = ConverUtil.string2Int(arrDate[2], -1);
        if (year == -1 || month == -1 || day == -1)
        {
            return false;
        }
        // 判断年是否合法
        if (year < 1000 || year > 9999)
        { // 是正整数,范围为1 - 9999
            return false;
        }
        // 判断月是否合法
        if (month < 1 || month > 12)
        {
            return false;
        }
        // 判断日是否合法
        if (day == 0)
        {
            return false;
        }

        if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30)
        {
            return false;
        }
        else if (day > 31)
        {
            return false;
        }

        if (month == 2)
        {
            if (year % 4 > 0 && day > 28)
            {
                return false;
            }
            else if (day > 29)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查传入的参数是否是合法的时间（格式是HH:mm）
     * 
     * @param stime 待检测的时间
     * @return 合法返回true
     */
    public static boolean isTime(String stime)
    {
        if (isEmpty(stime))
        {
            return false;
        }
        Matcher m = P_TIME.matcher(stime);
        return m.matches();
    }

    /**
     * 检查传入的参数是否是合法的时间（格式是HH:mm:ss）
     * 
     * @param stime 待验证的参数
     * @return 符合标准格式返回true
     */
    public static boolean isTimeSec(String stime)
    {
        if (isEmpty(stime))
        {
            return false;
        }
        Matcher m = P_TIME_SEC.matcher(stime);
        return m.matches();
    }

    /**
     * 判断给定时间是否大于当前时间,精确到天。
//     * @param data 日期时间，格式yyyy-MM-dd
     * @return 返回true表示大于.
     */
    public static boolean isThanCurrentDay(String date)
    {
        try
        {
            Date d = string2Date(date);
            int i = d.compareTo(new Date());
            return i > 0;
        }
        catch (Exception e)
        {
            // ...忽略日志
        }
        return false;
    }

    /**
     * 判断给定时间是否大于当前时间,可以根据传入的时间格式进行判断。
//     * @param data 日期时间，格式yyyy-MM-dd 或者 yyyy-MM-dd HH:mm:ss 或者HH:mm:ss
     * @return 返回true表示大于.
     */
    public static boolean isThanCurrentTime(String date)
    {
        try
        {
            Date d = null;
            if (date.length() == 10)
            {
                d = string2Date(date);
            }
            else if (date.length() == 19)
            {
                d = string2DateTime(date);
            }
            else if (date.length() == 8)
            {
                d = string2Time(date);
            }

            int i = d.compareTo(new Date());
            return i > 0;
        }
        catch (Exception e)
        {
            // ...忽略日志
        }
        return false;
    }

    /**
     * 求两个日期间隔的天数
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 返回结束日期-开始日期的天数
     */
    public static long getDaysBetween(Date startDate, Date endDate)
    {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (toCalendar.getTime().getTime()
            - fromCalendar.getTime().getTime()) / ONE_DAY_INTERVAL;
    }

    /**
     * 计算某年某月的天数
     * 
     * @param year 年份
     * @param month 月份
     * @return 返回这个月的天数
     */
    public static int getDaysOfMonth(int year, int month)
    {

        switch (month)
        {
        case 2:
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))
                return 29;
            else
                return 28;
        case 4:
            return 30;
        case 6:
            return 30;
        case 9:
            return 30;
        case 11:
            return 30;
        default:
            return 31;
        }
    }

    /**
     * 根据会计期间(月份)取得所属的会计期间(季节)。
     * 
     * @param sYearMonth 年月，格式为：yyyy-MM
     * @return 返回格式为2010-A
     */
    public static String getSeason(String sYearMonth)
    {

        if (isEmpty(sYearMonth))
        { // 为空，返回null;
            return null;
        }
        if (sYearMonth.length() != 7)
        { // 格式不是yyyy-MM,返回null
            return null;
        }
        String year = sYearMonth.substring(0, 4);
        String month = sYearMonth.substring(5).trim();
        if ((month.equals("01")) || (month.equals("02"))
            || (month.equals("03")))
        {
            return year + "-A";
        }
        else if ((month.equals("04")) || (month.equals("05"))
            || (month.equals("06")))
        {
            return year + "-B";
        }
        else if ((month.equals("07")) || (month.equals("08"))
            || (month.equals("09")))
        {
            return year + "-C";
        }
        return year + "-D";
    }

    /**
     * 获取当前日期,日期格式为：yyyy-MM-dd
     * 
     * @return 返回当前日期格式字符串
     */
    public static String getCurrentDate()
    {
        return date2String(new Date(), BASE_DATE_FORMAT);
    }

    /**
     * 获取当前日期年月(yyMM)
     * 
     * @return 返回当前年月字符串
     */
    public static String getCurrentDateYM()
    {
        return date2String(new Date(), BASE_Y_M);
    }

    /**
     * 日期按照指定格式转换为字符串
     * @param date 待格式化的日期对象
     * @param formatStr 日期格式化格式
     * @return 格式化后的日期字符串
     */
    public static String date2String(Date date, String formatStr)
    {
        return date2String(date, formatStr, Locale.getDefault());
    }


    /**
     * 使用参数Format将字符串转为Date 转换失败将返回null.
     * 
     * @param strDate 日期字符串
     * @param formatStr 格式化格式
     * @return Date 日期对象
     */
    public static Date string2Date(String strDate, String formatStr)
    {
        try
        {
            return string2Date(strDate, formatStr, Locale.getDefault());
        }
        catch (ParseException e)
        {
            /*
             * LOG.error("Failed fromat string to date,strDate=" + strDate +
             * ",formatStr=" + formatStr, e);
             */
            return null;
        }
    }

    /**
     * 尝试使用三种format转换时间字符串(yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm,yyyy-MM-dd)
     * @param strDate
     * @return
     */
    public static Date string2DateMulti(String strDate)
    {
        // 尝试各种format
        Date date = string2Date(strDate, "yyyy-MM-dd hh:mm:ss");
        if (date == null)
        {
            date = string2Date(strDate, "yyyy-MM-dd hh:mm");
            if (date == null)
            {
                date = string2Date(strDate, "yyyy-MM-dd");
            }
        }

        return date;
    }

    /**
     * 比较StartDate和EndDate, startDate和endDate必须是凌晨的0:00:00:000.
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 相差的天数
     */
    public static int dateDiff(Date startDate, Date endDate)
    {
        assert (startDate.getTime() % ONE_DAY_INTERVAL == 0
            && endDate.getTime() % ONE_DAY_INTERVAL == 0);
        long interval = endDate.getTime() - startDate.getTime();
        return (int) (interval / ONE_DAY_INTERVAL);
    }

    /**
     * 根据给定的格式化方式格式化当前提供的日期
     * 
     * @param strDate 字符串格式日期对象
     * @param formatStr 格式化格式
     * @param locale 系统环境
     * @return 格式化后的日期对象
     * @throws ParseException 解析失败抛出这个异常
     */
    private static Date string2Date(String strDate, String formatStr,
        Locale locale) throws ParseException
    {
        Date date = new SimpleDateFormat(formatStr, locale).parse(strDate);
        return date;
    }

    /**
     * 将日期对象格式化成指定的格式字符串
     * @param date 日期对象
     * @param formatStr 格式化方式
     * @param locale 系统环境
     * @return 指定格式化后的日期字符串
     */
    private static String date2String(Date date, String formatStr,
        Locale locale)
    {
        Format format = new SimpleDateFormat(formatStr, locale);
        return format.format(date);
    }

    /**
     * 将long类型的时间转换成指定的日期对象 当longTime小于0时将返回null.
     * 
     * @param longTime 时间的毫秒数
     * @param sFormat 格式化方式
     * @return 格式化后的日期对象
     * @throws ParseException 解析失败抛出这个异常
     */
    public static Date long2Date(long longTime, String sFormat)
        throws ParseException
    {
        if (longTime <= 0)
        {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        String dateStr = sdf.format(longTime);
        return sdf.parse(dateStr);
    }

    /**
     * 取出格式化的日期格式,将年份去掉精确到分钟,如果参数为null将返回null.
     * 
     * @param date 2010-11-03 16:08:51.0
     * @return MM-dd HH:mm
     */
    public static String dateFormatStr(String date)
    {
        if (AssertUtil.isEmpty(date))
        {
            return date;
        }
        if (date.length() > 15)
        {
            return date.substring(5, 16);
        }
        return date;
    }

    /**
     * 返回当前一天最早的时间.
     * @param date 当前时间.
     * @return 当前一天最早时间.
     */
    public static Calendar getFirstDateOfDay(Date date)
    {
        SimpleDateFormat sf = new SimpleDateFormat(BASE_DATE_FORMAT);
        String tempDate = sf.format(date);
        tempDate = tempDate + " 00:00:00";
        sf = new SimpleDateFormat(BASE_DATETIME_FORMAT);
        Date d;
        Calendar temp = null;
        try
        {
            d = sf.parse(tempDate);
            temp = Calendar.getInstance();
            temp.setTime(d);
        }
        catch (ParseException e)
        {

        }
        return temp;
    }

    /**
     * 返回当前一天最早的时间.
     * @param calendar 当前时间.
     * @return 当前一天最早时间.
     */
    public static Calendar getFirstDateOfDay(Calendar calendar)
    {
        Calendar temp = (Calendar) calendar.clone();

        SimpleDateFormat sf = new SimpleDateFormat(BASE_DATE_FORMAT);
        String tempDate = sf.format(temp.getTime());
        tempDate = tempDate + " 00:00:00";
        sf = new SimpleDateFormat(BASE_DATETIME_FORMAT);
        Date d;
        try
        {
            d = sf.parse(tempDate);
            temp.setTime(d);
        }
        catch (ParseException e)
        {

        }
        return temp;
    }

    /**
     * 返回当前一天最晚的时间.
     * @param date 当前时间.
     * @return 当前一天最晚时间.
     */
    public static Calendar getLastDateOfDay(Date date)
    {
        SimpleDateFormat sf = new SimpleDateFormat(BASE_DATE_FORMAT);
        String tempDate = sf.format(date);
        tempDate = tempDate + " 23:59:59";
        sf = new SimpleDateFormat(BASE_DATETIME_FORMAT);
        Date d;
        Calendar temp = null;
        try
        {
            d = sf.parse(tempDate);
            temp = Calendar.getInstance();
            temp.setTime(d);
        }
        catch (ParseException e)
        {

        }
        return temp;
    }

    /**
     * 返回当前一天最晚的时间.
     * @param calendar 当前时间.
     * @return 当前一天最晚时间.
     */
    public static Calendar getLastDateOfDay(Calendar calendar)
    {
        Calendar temp = (Calendar) calendar.clone();

        SimpleDateFormat sf = new SimpleDateFormat(BASE_DATE_FORMAT);
        String tempDate = sf.format(temp.getTime());
        tempDate = tempDate + " 23:59:59";
        sf = new SimpleDateFormat(BASE_DATETIME_FORMAT);
        Date d;
        try
        {
            d = sf.parse(tempDate);
            temp.setTime(d);
        }
        catch (ParseException e)
        {

        }
        return temp;
    }

    /**
     * 返回指定格式时间字符串，时间格式yyyy-MM-dd HH:mm:ss
     * @param calendar 指定时间.
     * @return 返回格式化后的时间.
     */
    public static String getFormatTime(Calendar calendar)
    {
        SimpleDateFormat sf = new SimpleDateFormat(BASE_DATETIME_FORMAT);
        return sf.format(calendar.getTime());
    }

    /**
     * 获取当前星期
     * @return 当前星期,如一，二等.
     */
    public static String getWeekDay()
    {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        String result = null;
        switch (day)
        {
        case 0:
            result = "日";
            break;
        case 1:
            result = "一";
            break;
        case 2:
            result = "二";
            break;
        case 3:
            result = "三";
            break;
        case 4:
            result = "四";
            break;
        case 5:
            result = "五";
            break;
        case 6:
            result = "六";
            break;
        default:
            result = "日";
            break;
        }
        return result;
    }

    /**
     * 检测日期格式是否是指定的格式,如 date=1984-2-5 23:23:34 ,format=yyyy-MM-dd
     * HH:mm:ss将返回false. 如：date=1984-02-05,format=yyyyMMdd将返回false.
     * 
     * @param date 待检测的日期.
     * @param format 日期格式.
     * @return 检测成功返回true,检测失败返回false.
     */
    public static boolean checkDateFormat(String date, String format)
    {
        if (AssertUtil.isEmpty(date) || AssertUtil.isEmpty(format))
        {
            return false;
        }
        // 如果待检测的日期和格式长度不符合将返回false.
        // 如 date=1984-2-5 23:23:34,format=yyyy-MM-dd HH:mm:ss将返回false.
        if (date.length() != format.length())
        {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try
        {// 如果解析失败将返回false.
            sdf.parse(date);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * 将日期格式的字符串转换为长整型
     * 
     * @param date
     * @param format
     * @return
     */
    public static long convert2long(String date, String format)
    {
        try
        {
            if (AssertUtil.isNotEmpty(date))
            {
                if (AssertUtil.isBlank(format))
                    format = BASE_DATETIME_FORMAT;
                SimpleDateFormat sf = new SimpleDateFormat(format);
                return sf.parse(date).getTime();
            }
        }
        catch (ParseException e)
        {}
        return 0l;
    }


    public static void main(String[] args) throws ParseException {
        long daySec = 1000 * 24 * 60 * 60 ;
        long hourSec = 1000 * 60 * 60;
        long minSec = 1000 * 60 ;
        //计算相差分钟数
        String  startTime = "11:15:12";
        String endTime = "";
//        TimeUtil.string2Date(startTime,"HH:mm:ss").getTime();

//        long diff = (TimeUtil.string2Date(endTime,"HH:mm:ss").getTime()-TimeUtil.string2Date(startTime,"HH:mm:ss").getTime()) % daySec % hourSec / minSec;

        String dateStr = TimeUtil.date2String(TimeUtil.string2Date(startTime,"HH:mm:ss"),TimeUtil.BASE_DATE_FORMAT);

        System.out.println(dateStr);

    }
}
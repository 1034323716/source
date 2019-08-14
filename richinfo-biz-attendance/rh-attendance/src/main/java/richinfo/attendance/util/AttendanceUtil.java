/**
 * 文件名：AttendanceUtil.java
 * 创建日期： 2017年6月26日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月26日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.bcomponet.cache.comm.CacheKey;

/**
 * 功能描述： 考勤功能工具类
 */
public class AttendanceUtil
{

    /** 检测是否是utf8字符 */
    public static final String EMOJI_REGEX = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
    /** 检测是否是utf8字符 */
    public static final Pattern EMOJI_PATTER = Pattern.compile(EMOJI_REGEX);

    private static List<Integer> serverNos = new ArrayList<Integer>();

    private static final Logger logger = LoggerFactory
        .getLogger(AttendanceUtil.class);

    /**
     * 考勤组缓存key
     * @param attendanceId
     * @return
     */
    public static String getGroupCachekey(long attendanceId)
    {
        String skey = String.format(CacheKey.Attendance.ATTENDANCE_GROUP_INFO,
            attendanceId);
        return skey;
    }

    /**
     * 判断字符串是否是正确的utf8编码
     * @param tmpstr 待检测的编码
     * @return 符合返回true.
     */
    public static boolean isEmoji(String tmpstr)
    {
        if (AssertUtil.isEmpty(tmpstr))
        {
            return false;
        }
        boolean result = false;
        if (EMOJI_PATTER.matcher(tmpstr).find())
        {
            result = true;
        }
        return result;
    }

    /**
     * 生成加密key
     * @return
     */
    public static String createKey()
    {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    private String creatSign(String enterId, long curTime)
    {
        String key = "97d99d8143744510a8f468ebb1d63c22";
        EncryptionUtil.getMD5ByUtf8(enterId + curTime + key);
        return null;
    }

    /**
     * 获取考勤时间（i为0时，则获取签到时间，i为1时，则获取签退时间）
     * @param date
     * @param attendTime
     * @param i
     * @return
     */
    public static Date getAttendDate(Date date, String attendTime, int i)
    {
        if (AssertUtil.isEmpty(attendTime))
        {
            return null;
        }

        try
        {
            // 08:30-11:30
            String[] attendTimes = attendTime.split("\\s*-\\s*");
            String day = TimeUtil.date2String(date, TimeUtil.BASE_DATE_FORMAT);
            day = day + " " + attendTimes[i] + ":00";
            Date attendDate = TimeUtil.string2Date(day,
                TimeUtil.BASE_DATETIME_FORMAT);
            return attendDate;
        }
        catch (Exception e)
        {
            logger.error("getAttendDate error.", e);
            return null;
        }
    }
    
    
    public static Date getPlusOrMinusDate(Date date,int time){
    	 Calendar cal = Calendar.getInstance();
         cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, time);
        return cal.getTime();
    }

    /**
     * 获取创建/编辑考勤组时的考勤时间（i为0时，则获取签到时间，i为1时，则获取签退时间）--如果attendDate小于当前时间，
     * 则给attendDate加一天
     * @param date
     * @param attendTime
     * @param i
     * @return
     */
    public static Date getUpdateAttendDate(Date date, String attendTime, int i)
    {
        if (AssertUtil.isEmpty(attendTime))
        {
            return null;
        }

        try
        {
            // 08:30-11:30
            String[] attendTimes = attendTime.split("\\s*-\\s*");
            String day = TimeUtil.date2String(date, TimeUtil.BASE_DATE_FORMAT);
            day = day + " " + attendTimes[i] + ":00";
            Date attendDate = TimeUtil.string2Date(day,
                TimeUtil.BASE_DATETIME_FORMAT);

            // 如果attendDate小于当前时间，则给attendDate加一天
            if (AssertUtil.isNotEmpty(attendDate)
                && attendDate.getTime() < System.currentTimeMillis())
            {
                attendDate = DateUtils.addDays(attendDate, 1);
            }

            return attendDate;
        }
        catch (Exception e)
        {
            logger.error("getAttendDate error.", e);
            return null;
        }
    }

    /**
     * 随机获取一个机器编号
     * @return
     */
    public static int getOneServerNo()
    {
        int size = serverNos.size();
        if (size <= 0)
        {
            synchronized (serverNos)
            {
                if (size <= 0)
                {
                    String serverNoList = AttendanceConfig.getInstance()
                        .getServerNoList();
                    String[] list = serverNoList.split("\\s*,\\s*");
                    for (String serverNo : list)
                    {
                        serverNos.add(ConverUtil.string2Int(serverNo));
                    }
                    size = serverNos.size();
                    logger.info("init serverNos. size={}", size);
                }
            }
        }

        Random r = new Random();
        int index = r.nextInt(size);
        return serverNos.get(index);
    }

    /**
     * 将list分割成大小为splitSize的list集合
     * @param list
     * @param splitSize 被切割后每个list的大小
     * @return
     */
    public static <T extends Object> List<List<T>> splitList(List<T> list,
        int splitSize)
    {
        List<List<T>> listSplit = new ArrayList<List<T>>();
        if (AssertUtil.isNotEmpty(list))
        {
            if (splitSize <= 0)
            {
                listSplit.add(list);
            }
            else
            {
                int listSize = list.size();
                int listCount = listSize / splitSize;
                if (listSize % splitSize != 0)
                {
                    listCount += 1;
                }
                for (int i = 0; i < listCount; i++)
                {
                    int start = i * splitSize;
                    int end = start + splitSize;
                    if (end > listSize)
                    {
                        end = listSize;
                    }
                    listSplit.add(list.subList(i * splitSize, end));
                }
            }
        }
        return listSplit;
    }

    /**
     * 获取事物ID
     * @return
     */
    public static String getCguid()
    {
        return String.valueOf(System.currentTimeMillis())
            + RandomUtil.randOfNumber(3);
    }

    /**
     * 获取耗时
     * @return
     */
    public static long getUseTime(long startTime)
    {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 获取两天的时间（当天和第二天）
     * @return
     */
    public static List<Date> getTowDays()
    {
        List<Date> towDays = new ArrayList<Date>();
        Date nowDate = new Date();
        towDays.add(nowDate);
        towDays.add(DateUtils.addDays(nowDate, 1));
        return towDays;
    }

    public static void main(String[] args)
    {
        // 483460-97d99d8143744510a8f468ebb1d63c22
        // 36101-fd003ee9c12742e79ba365815f8564d9
        // 36301-d28a7fbd15c34af09e741b345100bd37
        // 495551-3c341deb7a1343c89a8226475876fa9c
        // 512611-53d4f103499e4e95aa80e9a819c6a912
        // System.out.println(createKey());
        System.out.println(EncryptionUtil.getMD5ByUtf8("36301"
            + "1511243129000" + "d28a7fbd15c34af09e741b345100bd37"));
    }
}

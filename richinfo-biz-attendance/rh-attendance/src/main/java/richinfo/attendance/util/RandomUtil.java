/*
 * @(#)RandomTool.java
 */

package richinfo.attendance.util;

import java.util.Random;

/**
 * 该类提供生成任意长度的随机数
 * 
 * Title: RandomTool <br>
 * Description: TODO<br>
 * Copyright: Copyright (c) 2008 <br>
 * Company:深圳彩讯科技有限公司 <br>
 * 
 * @version v1.0
 * @author dumh
 */
public final class RandomUtil
{

    private RandomUtil()
    {
    }

    /**
     * 得到len长度的随机码,随机数由纯数字组成.
     * 
     * @param len 随机码长度.
     * @return 随机码.
     */
    public static String randOfNumber(int len)
    {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < len; i++)
        {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 在范围内取得一个整数
     * 
     * @param upLimit 上线阈值
     * @param downLimit 下线阈值
     * @return 随机数
     */
    public static int getIntByUpDown(int upLimit, int downLimit)
    {
        double random = Math.random();
        double value = (int) (random * (upLimit - downLimit - 1)) + downLimit;
        return (int) value;
    }

    /**
     * 在范围内取得一个整数
     * 
     * @param upLimit 上线阈值
     * @param downLimit 下线阈值
     * @return 随机数
     */
    public static int getLongByUpDown(long upLimit, long downLimit)
    {
        double random = Math.random();
        double value = (int) (random * (upLimit - downLimit - 1)) + downLimit;
        return (int) value;
    }

    /**
     * 生成一个指定前缀的唯一随机数，该随机数为指定的len长度.
     * 
     * 生成规则：prefix+yyyyMMddHHmmssSSS+随机数. 精确到毫秒级别(前提是len需要大于prefix的长度+17).
     * @param prefix 随机数前缀.
     * @param len 随机数长度.
     * @return 随机数,纯数字组成.
     */
    public static String getRandomString(String prefix, int len)
    {
        String temp = "";
        int prefixLength = 0;
        if (!AssertUtil.isEmpty(prefix))
        {
            temp = prefix;
            prefixLength = temp.length();
        }
        if (prefixLength >= len)
        {
            return temp.substring(0, len);
        }
        String time = TimeUtil.getCurrentDateTime("yyyyMMddHHmmssSSS");
        int timeLen = 17;
        if (len > prefixLength + timeLen)
        {
            temp = temp + time;
            temp += RandomUtil.randOfNumber((len - (prefixLength + timeLen)));
        }
        else
        {
            temp = temp + time;
            temp = temp.substring(0, len);
        }
        char[] chars = temp.toCharArray();
        int size = chars.length;
        Random rnd = new Random();
        for (int i = size; i > 1; i--)
        {
            swap(chars, i - 1, rnd.nextInt(i));
        }
        return new String(chars);
    }

    /**
     * 交换容器中的两个值
     * @param arr 数组.
     * @param i 第一个元素的下标.
     * @param j 第二个元素的下标.
     */
    private static void swap(char[] arr, int i, int j)
    {
        char tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
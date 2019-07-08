/**
 * 文件名：AssertUtil.java
 * 创建日期： 2012-8-14
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-8-14
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.attendance.util;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 功能描述：一些常见的断言判断工具类,如字符串是否为空等,当断言失败时将抛出运行时异常
 * 
 */
public final class AssertUtil
{

    /** 判断字符串全部为数字的正则表达式对象 */
    public static final Pattern ALL_DIGITAL_PATTER = Pattern.compile("^\\d*$");
    /** 判断是否为邮箱的正则表达式 */
    public static final String EMAIL_REGEX = "^[\\w-]+(\\.[\\w-]*)*@[\\w-]+(\\.[\\w-]+)+$";
    /** 判断是否为邮箱的正则表达式 对象 */
    public static final Pattern EMAIL_PARTTER = Pattern.compile(EMAIL_REGEX);
    /** 判断是否是数字的正则表达式对象,这个正则表达式好像有点问题如果输入-也将匹配 */
    public static final Pattern DIGITAL_PATTER = Pattern.compile("\\d*|-\\d*");
    public static final String TEL_REGEX = "^\\d{3}-\\d{8}$|^\\d{4}-\\d{7}$|^\\d{8}$|^\\d{7}$|^\\d{3}-\\d{7}$"
        + "|^\\d{4}-\\d{8}$|^\\d{3,12}$";
    /** 判断字符串是否是电话号码 */
    public static final Pattern TEL_PATTER = Pattern.compile(TEL_REGEX);
    /** 判断是否包含特殊字符 */
    public static final Pattern CONTAIN_OF_OTHER_PREGEX3 = Pattern
        .compile("[^\\u4e00-\\u9fa5a-zA-Z0-9]");
    /** 检测是否是utf8字符 */
    public static final String UTF_REGEX = "[^\\f\\a\\v\\t\\r\\n\u0020-\u007E\u3400-\u4DB5\u4E00-\u9FA5\u9FA6-\u9FBB\uF900-\uFA2D\uFA30-\uFA6A\uFA70-\uFAD9\u20000-\u2A6D6\u2F800-\u2FA1D\uFF00-\uFFEF\u2E80-\u2EFF\u3000-\u303F\u31C0-\u31EF\u3040-\u309F\u30A0-\u30FF\u31F0-\u31FF\uAC00-\uD7AF\u1100-\u11FF\u3130-\u318F|\uFFFE\uFEFF]";
    /** 检测是否是utf8字符 */
    public static final Pattern UTF_PATTER = Pattern.compile(UTF_REGEX);
    /** 检测邮件地址是否符合 */
    public static final String EMAIL_REGEX2 = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    /** 检测邮件地址是否符合 */
    public static final Pattern EMAIL_PATTER2 = Pattern.compile(EMAIL_REGEX2);

    private AssertUtil()
    {
    }

    /**
     * 判断字符串是否为空.
     * @param str 待判断的字符串.
     * @return true表示字符串为空或者为null.
     */
    public static boolean isEmpty(String str)
    {
        if (str == null || str.isEmpty() || "null".equalsIgnoreCase(str))
        {
            return true;
        }
        return false;
    }

    /**
     * 判断collection是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection)
    {
        return collection == null ? true : collection.isEmpty();
    }

    /**
     * 判断collection是否为空
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection<?> collection)
    {
        return !isEmpty(collection);
    }

    /**
     * 判断map是否为空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map)
    {
        return map == null ? true : map.isEmpty();
    }

    /**
     * 判断map是否为空
     * @param map
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> map)
    {
        return !isEmpty(map);
    }

    /**
     * 判断输入参数是否为空,是否为null,不会抛出任何异常
     * 
     * @param obj 待判断的对象
     * @return 为null将返回true
     */
    public static boolean isEmpty(Object obj)
    {
        if (obj == null)
        {
            return true;
        }

        if (obj instanceof String)
        {
            return isEmpty((String) obj);
        }

        if (obj instanceof Map<?, ?>)
        {
            return isEmpty((Map<?, ?>) obj);
        }

        if (obj instanceof Collection<?>)
        {
            return isEmpty((Collection<?>) obj);
        }

        return false;
    }

    /**
     * 判断数组是否为空
     * @param arr
     * @return
     */
    public static boolean isEmpty(Object[] arr)
    {
        return arr == null ? true : arr.length == 0;
    }

    /**
     * 判断数组是否为空
     * @param arr
     * @return
     */
    public static boolean isNotEmpty(Object[] arr)
    {
        return !isEmpty(arr);
    }

    /**
     * 判断给定参数是否为null,为null将返回true.
     * 
     * @param obj 待判断参数
     * @return 参数为null返回true
     */
    public static boolean isNull(Object obj)
    {
        return obj == null ? true : false;
    }

    /**
     * 判断输入字符串参数是否不为empty,不会抛出任何异常
     * 
     * @param str 待判断的参数
     * @return 如果参数为null或者empty将返回false
     */
    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }

    /**
     * 判断输入参数是否不为空.
     * @param obj 待判断参数.
     * @return 是否不为空.
     */
    public static boolean isNotEmpty(Object obj)
    {
        return !isEmpty(obj);
    }

    /**
     * 判断字符串是否是全数字
     * 
     * @param str 待判断的字符串
     * @return 如果字符串全部为数字,将返回true,否则返回false
     */
    public static boolean isNumber(String str)
    {
        if (isEmpty(str))
        {
            return false;
        }
        if (ALL_DIGITAL_PATTER.matcher(str).matches())
        {
            return true;
        }
        return false;
    }

    /**
     * 
     * 判断目标字符串在源字符中是否存在,判断原则是先将src进行逗号分割 然后逐个和dest比较，如果相等将返回true
     * 
     * 主要使用场景是ip白名单控制判断,次方法为忽略大小写比较,如果要模糊匹配请使用isIndexOf方法.
     * 如果要精确比较请用isContains方法.
     * 
     * @param src 源字符串，如果为null将返回false.
     * @param dest 目标字符串，如果为null将返回false.
     * @return 包含返回true,否则返回false.
     */
    public static boolean isContainsIgnoreCase(String src, String dest)
    {
        if (isEmpty(src) || isEmpty(dest))
        {
            return false;
        }
        String[] tempArray = src.split(",");
        for (String temp : tempArray)
        {
            if (isNotEmpty(temp) && temp.trim().equalsIgnoreCase(dest.trim()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * 判断目标字符串在源字符中是否存在,判断原则是先将src进行逗号分割 然后逐个和dest比较，如果相等将返回true
     * 
     * 主要使用场景是ip白名单控制判断,次方法为精确比较,如果要模糊匹配请使用isIndexOf方法.
     * 
     * @param src 源字符串，如果为null将返回false.
     * @param dest 目标字符串，如果为null将返回false.
     * @return 包含返回true,否则返回false.
     */
    public static boolean isContains(String src, String dest)
    {
        if (isEmpty(src) || isEmpty(dest))
        {
            return false;
        }
        String[] tempArray = src.split(",");
        for (String temp : tempArray)
        {
            if (isNotEmpty(temp) && temp.trim().equals(dest.trim()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * 判断目标字符串在源字符中是否存在,判断原则是先将src进行逗号分割 然后逐个和dest比较，如果相等将返回true
     * 
     * 主要使用场景是ip白名单控制判断,次方法为模糊匹配,如果要精确比较,请使用isContains方法.
     * 
     * @param src 源字符串，如果为null将返回false.
     * @param dest 目标字符串，如果为null将返回false.
     * @return 包含返回true,否则返回false.
     */
    public static boolean isIndexOf(String src, String dest)
    {
        if (isEmpty(src) || isEmpty(dest))
        {
            return false;
        }
        String[] tempArray = src.split(",");
        for (String temp : tempArray)
        {
            if (AssertUtil.isNotEmpty(temp) && temp.indexOf(dest) >= 0)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断邮件地址是否正确
     * 
     * @param email 邮件地址
     * @return 符合返回true
     */
    public static boolean checkEmail(String email)
    {
        boolean isemail = false;
        if (email.indexOf("<") > -1 && email.indexOf(">") > -1)
        {
            int start = email.indexOf("<");
            int end = email.indexOf(">");
            email = email.substring(start + 1, end);
        }
        Matcher mat3 = EMAIL_PATTER2.matcher(email);
        isemail = mat3.matches();
        return isemail;
    }

    /**
     * 判断字符串是否是正确的utf8编码
     * @param tmpstr 待检测的编码
     * @return 符合返回true.
     */
    public static boolean isRightUTF(String tmpstr)
    {
        if (isEmpty(tmpstr))
        {
            return true;
        }
        boolean result = true;
        if (UTF_PATTER.matcher(tmpstr).find())
        {
            result = false;
        }
        return result;
    }

    /**
     * 检查是不是utf字符。 utf字符的特点是其中一个char数组应该大于255 只要有一个大于255,即认为是utf
     * 
     * @param str 待检测的字符串
     */
    public static boolean checkIsUTF(String str)
    {
        char[] charstr = str.toCharArray();
        for (int i = 0; i < charstr.length; i++)
        {
            if (charstr[i] > 255)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * 判断这个字符是否是空白字符 Checks if a String is whitespace, empty ("") or null.
     * </p>
     * 
     * <pre>
     * Tools.isBlank(null)      = true
     * Tools.isBlank(&quot;&quot;)        = true
     * Tools.isBlank(&quot; &quot;)       = true
     * Tools.isBlank(&quot;bob&quot;)     = false
     * Tools.isBlank(&quot;  bob  &quot;) = false
     * </pre>
     * 
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     */
    public static boolean isBlank(String str)
    {
        if (isEmpty(str))
        {
            return true;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++)
        {
            if (!Character.isWhitespace(str.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断字符串是否是数字
     * @param str 待判断的字符串
     * @return 如果全部是数字将返回true
     */
    public static boolean isNum(String str)
    {
        Matcher m = DIGITAL_PATTER.matcher(str);
        return m.matches();
    }
    
}

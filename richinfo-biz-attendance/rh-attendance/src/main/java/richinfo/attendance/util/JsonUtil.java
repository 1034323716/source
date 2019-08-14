/**
 * 文件名：JsonUtil.java
 * 创建日期： 2014年6月30日
 * 作者：     tangguanfeng
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2014年6月30日
 *   修改人：tangguanfeng
 *   修改内容：
 */
package richinfo.attendance.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：json序列化与反序列化功能
 * 
 */
public final class JsonUtil
{
    private static Gson gson;

    private JsonUtil()
    {
    }

    /**
     * 将JavaBean/Map序列化成json字符串
     * @param obj
     * @return
     */
    public static String beanToJson(Object obj)
    {
        return getGson().toJson(obj);
    }

    /**
     * 从json字符串反序列化为JavaBean
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T jsonToBean(String json, Class<T> clazz)
    {
        return getGson().fromJson(json, clazz);
    }

    /**
     * <pre>
     * 将json串反序列化为Map
     * 注意对于值为int类型的字段会被转换为double类型的值.
     * 需要调用者自己再进行处理
     * 比如:
     * {
     *   age: 23,
     *   name: "a"
     * }
     * </pre>
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonToMap(String json)
    {
        return getGson().fromJson(json, HashMap.class);
    }

    /**
     * <pre>
     * 获取Gson的JsonObject对象,主要用于不方便将json串映射到JavaBean的情况
     * 可以使用Gson对象对串进行逐步处理.
     * 比如以下json串
     * {
     *    "code": "S_OK",
     *    "var": {"a":1,"b":"na"}
     * }
     * 要获取var字段进行处理 
     * JsonObject varObj = jsonObj.getAsJsonObject("var");
     * int a = varObj.get("a").getAsInt();
     * String b = varObj.get("b").getAsString();
     * 处理可能不存在的字段:
     * if(varObj.has("c")){
     *      double c = varObj.get("c").getAsDouble();
     * }
     * </pre>
     * @return
     */
    public static JsonObject getJsonObject(String json)
    {
        return getGson().fromJson(json, JsonObject.class);
    }

    public static Gson getGson()
    {
        if (gson == null)
        {
            synchronized (JsonUtil.class)
            {
                if (gson == null)
                {
                    gson = new Gson();
                }
            }
        }

        return gson;
    }

    /**
     * 将字符串做json转义处理
     * @param value
     * @return
     */
    public static String escapseJsonString(String value)
    {
        if (AssertUtil.isEmpty(value))
        {
            return value;
        }

        return new Escapser(value).toString();
    }

    /**
     * 功能描述：转义器
     * 
     */
    private static class Escapser
    {
        private char[] buf;
        private int incre;
        private String value;
        private int valueLen;
        private int count;

        public Escapser(String value)
        {
            this.value = value;
            this.incre = 16;
            this.valueLen = value.length();
            this.buf = new char[valueLen + incre];

            escapse();
        }

        /**
         * 
         */
        private void escapse()
        {
            for (int i = 0; i < valueLen; i++)
            {
                char ch = value.charAt(i);
                if ('"' == ch || '\\' == ch || '/' == ch)
                {
                    save2buf('\\');
                    save2buf(ch);
                }
                else if ('\b' == ch)
                {
                    save2buf('\\');
                    save2buf('b');
                }
                else if ('\f' == ch)
                {
                    save2buf('\\');
                    save2buf('f');
                }
                else if ('\n' == ch)
                {
                    save2buf('\\');
                    save2buf('n');
                }
                else if ('\r' == ch)
                {
                    save2buf('\\');
                    save2buf('r');
                }
                else if ('\t' == ch)
                {
                    save2buf('\\');
                    save2buf('t');
                }
                else
                {
                    save2buf(ch);
                }
            }
        }

        /**
         * @param ch
         */
        private void save2buf(char ch)
        {
            if (count >= buf.length)
            {
                buf = Arrays.copyOf(buf, buf.length + incre);
            }
            buf[count++] = ch;
        }

        @Override
        public String toString()
        {
            return new String(buf, 0, count);
        }
    }
}

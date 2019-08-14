/**
 * 文件名：AtdcStringUtil.java
 * 创建日期： 2017年6月24日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月24日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.util;

import java.util.Map;
import java.util.Map.Entry;

import richinfo.attendance.util.AssertUtil;

/**
 * 功能描述：
 * 
 */
public final class AtdcStringUtil
{
    /**
     * 返回请求参数
     * @param reqMap
     * @return
     */
    public static String getRequestString(Map<String, Object> reqMap)
    {
        if (AssertUtil.isEmpty(reqMap))
        {
            return "";
        }
        StringBuilder sb = new StringBuilder("reqMap[");
        for (Entry<String, Object> entity : reqMap.entrySet())
        {
            sb.append(entity.getKey()).append("=").append(entity.getValue())
                .append(",");
        }
        sb.append("]");
        return sb.toString();
    }

}

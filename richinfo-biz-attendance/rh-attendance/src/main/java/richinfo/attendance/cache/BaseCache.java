/**
 * 文件名：BaseCache.java
 * 创建日期： 2014年5月14日
 * 作者：     tangguanfeng
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2014年5月14日
 *   修改人：tangguanfeng
 *   修改内容：
 */
package richinfo.attendance.cache;

import java.io.Serializable;

import richinfo.bcomponet.cache.CachedUtil;

/**
 * 功能描述：
 * @param<T> 
 */
public class BaseCache<T>
{
    private String cacheKey;

    public BaseCache(String cacheKey)
    {
        this.cacheKey = cacheKey;
    }

    public void save(Serializable key, T obj)
    {
        String skey = String.format(cacheKey, key);
        CachedUtil.set(skey, obj);
    }

    public void save(Serializable key, T obj, long timeout)
    {
        String skey = String.format(cacheKey, key);
        CachedUtil.set(skey, obj, timeout);
    }

    @SuppressWarnings("unchecked")
    public T get(Serializable key)
    {
        String skey = String.format(cacheKey, key);
        skey = skey.replaceAll("\"","");
        return (T) CachedUtil.get(skey);
    }

    public void delete(Serializable key)
    {
        String skey = String.format(cacheKey, key);
        CachedUtil.delete(skey);
    }
}

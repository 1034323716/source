/**
 * 文件名：PersistContext.java
 * 创建日期： 2012-8-20
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-8-20
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import richinfo.dbcomponent.bean.DataSourceInfo;
import richinfo.dbcomponent.filter.FilterChain;
import richinfo.dbcomponent.service.PersistService;

import com.mongodb.Mongo;

/**
 * 功能描述：组件上下文,衔接组件上下文所需要的信息.
 * 
 * 创建日期： 2012-8-20 作者： zhou gui ping
 */
public class PersistContext
{
    /** 缓存数据源信息,key为数据源别名,value为数据源. */
   // private static ConcurrentMap<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();

    /*新数据源druid*/
    private static ConcurrentMap<String, DruidDataSource> druidDataSourceConcurrentMap =  new ConcurrentHashMap<String, DruidDataSource>();

    /** 缓存数据源信息,key为数据源别名，vlaue为数据源信息 */
    private static ConcurrentMap<String, DataSourceInfo> dbsourceinfoMap = new ConcurrentHashMap<String, DataSourceInfo>();

    /** 缓存持久化服务 */
    private static ConcurrentMap<String, PersistService> service = new ConcurrentHashMap<String, PersistService>();

    /** 缓存nosql数据库信息 */
    private static ConcurrentMap<String, Mongo> nosqlSourceMap = new ConcurrentHashMap<String, Mongo>();

    /** 可重入读写锁 */
    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 上下文信息
     */
    private static ThreadLocal<PersistContext> local = new ThreadLocal<PersistContext>() {
        @Override
        protected PersistContext initialValue()
        {
            return new PersistContext();
        }
    };

    /**
     * 获取环境上下文.
     * 
     * @return 持久化上下文.
     */
    public static PersistContext getContext()
    {
        return local.get();
    }

    /**
     * 清除当前上下文.
     */
    public static void clear()
    {
        local.remove();
    }

    /**
     * 获取过滤器链.
     * 
     * @return 过滤器链.
     */
    public FilterChain getFilterChain()
    {
        return null;
    }

    /**
     * 获取具体的服务类型,如oracle,mysql,nosql.
     * 
     * @param alias 服务别名.
     * @return 持久化服务.
     */
    public PersistService getServiceBytype(String alias)
    {
        return service.get(alias);
    }

    /**
     * 获取所有数据源信息,禁止对返回结果进行修改,尝试对结果进行修改将会获取到一个异常.
     * 
     * @return 数据源对象
     */
   /* public static Map<String, DataSource> getDataSourceMap()
    {
        return Collections.unmodifiableMap(dataSourceMap);
    }*/

    /**
     * 获取mongo实例对象.
     * @param databaseName 数据库名.
     * @return Mongo实例.
     */
    public static Mongo getNoSqlSourceByName(String databaseName)
    {
        return nosqlSourceMap.get(databaseName);
    }

    public static void setNoSqlSource(ConcurrentMap<String, Mongo> map)
    {
        lock.writeLock().lock();
        try
        {
            PersistContext.nosqlSourceMap = map;
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    public static Map<String, DruidDataSource> getDruidDataSourceConcurrentMap() {
        return Collections.unmodifiableMap(druidDataSourceConcurrentMap);
    }

    public static void setDruidDataSourceConcurrentMap(ConcurrentMap<String, DruidDataSource> druidDataSourceConcurrentMap) {
        lock.writeLock().lock();
        try
        {
            PersistContext.druidDataSourceConcurrentMap = druidDataSourceConcurrentMap;
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

  /*  public static void setDataSourceMap(
        ConcurrentMap<String, DataSource> dataSourceMap)
    {
        lock.writeLock().lock();
        try
        {
            PersistContext.dataSourceMap = dataSourceMap;
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }*/

    /**
     * 获取数据库配置信息,禁止对返回结果进行修改,尝试对结果进行修改将会获取到一个异常.
     * 
     * @return 数据库配置信息.
     */
    public static Map<String, DataSourceInfo> getDbsourceinfoMap()
    {
        return Collections.unmodifiableMap(dbsourceinfoMap);
    }

    public static void setDbsourceinfoMap(
        ConcurrentMap<String, DataSourceInfo> dbsourceinfoMap)
    {
        lock.writeLock().lock();
        try
        {
            PersistContext.dbsourceinfoMap = dbsourceinfoMap;
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    /**
     * 获取别名对应的数据源信息，没有别名对应的数据源将返回null.
     * 
     * @param alias 别名.
     * @return 数据源信息.
     */
    public static DataSource getDataSource(String alias)
    {
       // return dataSourceMap.get(alias);
        return druidDataSourceConcurrentMap.get(alias);
    }

    /**
     * 是否有这个别名对应的数据源.
     * 
     * @param alias 数据源对应的别名.
     * @return 存在返回true.
     */
    public static boolean containsAlias(String alias)
    {
        return druidDataSourceConcurrentMap.containsKey(alias);
    }

}

/**
 * 文件名：StartUp.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.logicalcobwebs.proxool.ProxoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.dbcomponent.bean.DataSourceInfo;
import richinfo.dbcomponent.datasource.*;
import richinfo.dbcomponent.filter.FilterManager;
import richinfo.dbcomponent.util.DBConfig;
import richinfo.dbcomponent.util.DBConfigParse;
import richinfo.dbcomponent.util.Resource;
import richinfo.tools.common.AssertUtil;
import richinfo.tools.io.StreamUtil;

/**
 * 功能描述：启动组件,初始化组件，加载配置文件，加载驱动等.
 * 
 * 创建日期： 2012-8-20 作者： zhou gui ping
 */
public class StartUp
{

    private static Logger log = LoggerFactory.getLogger(StartUp.class);
    /** proxool数据库驱动 */
    private static final String DRIVER = "org.logicalcobwebs.proxool.ProxoolDriver";
    /** DRUID 数据库驱动 */
    private static final String DRUID_DRIVER = "com.alibaba.druid.pool.DruidDataSource";
    /** 邮箱服务数据源配置文件 */
    private static final String WAPPROXOOL = "proxooljboss.xml";
    /** 增值服务数据源配置文件 */
    private static final String WSPROXOOL = "proxooltomcat.xml";
    /** 数据源配置文件,properties格式 */
    @SuppressWarnings("unused")
    private static final String DATASOURCE_FILENAME_PROPER = "proxool.properties";
    /** 数据源配置文件,xml文件格式 */
    private static final String DATASOURCE_FILENAME_XML = "proxool.xml";
    /** tomcat jdbc pool */
    private static String TOMCAT_JDBC_POOL = "tomcat";
    /** 当前实例对象 */
    private static volatile StartUp instance;
    /** 是否已经启动 */
    private static AtomicBoolean running = new AtomicBoolean(false);
    /** 判断是否是增值服务还是邮箱服务 */
    private static boolean istomcat = System.getProperty("jboss.server.name") == null;
    /** 可重入锁 */
    private ReentrantLock lock = new ReentrantLock();

    private StartUp()
    {
    }

    /** 获取当前实例对象 */
    public static StartUp getInstance()
    {
        if (instance == null)
        {
            synchronized (StartUp.class)
            {
                if (instance == null)
                {
                    instance = new StartUp();
                }
            }
        }
        return instance;
    }

    /**
     * 启动组件
     */
    public final void startUp()
    {

        try
        {
            lock.lock();
            if (running.get())
            {
                log.info("The persist service has been started.");
                return;
            }
            running.compareAndSet(false, true);
            // 获取基准路径
            String configPath = DBConfig.getInstance().getProperty("configdir");
            // 是否不加载sql连接池，因为有些项目没用到关系数据库，而是使用nosql
            String isNotLoadProxol = DBConfig.getInstance().getProperty(
                "dbcomponent.not.loadproxool", "false");
            if ("false".equals(isNotLoadProxol))
            {
                if (AssertUtil.isEmpty(configPath))
                {
                    // 外部配置文件不存在就加载classpath下的proxool.xml配置文件.
                    loadDriver(DATASOURCE_FILENAME_XML);
                    return;
                }
                // 加载外部配置文件,分为jboss和tomcat
                if (istomcat)
                {
                    loadDriver(configPath + File.separator + WSPROXOOL);
                }
                else
                {
                    loadDriver(configPath + File.separator + WAPPROXOOL);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Failed start persist component.", e);
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 加载驱动，初始化连接池，优先从指定路径加载，加载不到就从 classpath路径加载.
     * 
     * @param configPath 数据源配置文件.
     * @throws IOException
     * @throws ProxoolException
     */
    public void loadDriver(String configPath) throws IOException
    {
        log.info("start load database config from configpath,configpath="
            + configPath);
        //旧数据源
        ConcurrentMap<String, DataSource> dataSourceMap = null;
        //新数据源
        ConcurrentMap<String, DruidDataSource> druidDataSourceConcurrentMap = null;

        // 获取连接池类型配置
        String poolType = DBConfig.getInstance().getProperty("poolType");
        // 加载数据源配置信息,解析xml文件.
        DBConfigParse parse = new DBConfigParse();
        ConcurrentMap<String, DataSourceInfo> sourceInfomap = parse
            .parseXml2Object(configPath);
        // 根据类型初始化连接池 根据需要增加
        if (TOMCAT_JDBC_POOL.equals(poolType))
        {
            // tomcat jdbc pool
            //dataSourceMap = initTomcatPool(sourceInfomap);
            //新数据源
             druidDataSourceConcurrentMap = initTomcatDruidPool(sourceInfomap);
        }
        else
        {
            // Proxool
            //dataSourceMap = initProxool(configPath, sourceInfomap);
            //新数据源
            druidDataSourceConcurrentMap = initDruidProxool(configPath, sourceInfomap);
        }
        // 存储到上下文中.
        //PersistContext.setDataSourceMap(dataSourceMap);
        PersistContext.setDruidDataSourceConcurrentMap(druidDataSourceConcurrentMap);
        // 存储数据源信息到上下文中.
        PersistContext.setDbsourceinfoMap(sourceInfomap);
    }

    /**
     * 初始化tomcat连接池
     * @param sourceInfomap
     * @return
     */
    private ConcurrentMap<String, DataSource> initTomcatPool(
        ConcurrentMap<String, DataSourceInfo> sourceInfomap)
    {
        // 构建数据源
        TomcatDataSource.getInstance().createDataSource(sourceInfomap);
        // 初始化
        ConcurrentMap<String, DataSource> dataSourceMap = initDataSource(sourceInfomap);
        return dataSourceMap;
    }

    /**
     * 初始化 TomcatDruidDataSource 连接池
     * @param sourceInfomap
     * @return
     */
    private ConcurrentMap<String, DruidDataSource> initTomcatDruidPool(
        ConcurrentMap<String, DataSourceInfo> sourceInfomap)
    {
        // 构建数据源
        TomcatDruidDataSource.getInstance().createDruidDataSource(sourceInfomap);
        // 初始化
        ConcurrentMap<String, DruidDataSource> dataSourceMap = initDruidDataSource(sourceInfomap);
        return dataSourceMap;
    }

    /**
     * 初始化proxool连接池
     * @param configPath
     * @param sourceInfomap
     * @return
     */
    private ConcurrentMap<String, DataSource> initProxool(String configPath,
        ConcurrentMap<String, DataSourceInfo> sourceInfomap)
    {
        InputStream in = null;
        ConcurrentMap<String, DataSource> dataSourceMap = null;
        try
        {
            lock.lock();
            File file = new File(configPath);
            if (file.exists())
            {
                in = new FileInputStream(file);
            }
            else
            {
                log.error("Failed load database config,file is not exist,path="
                    + configPath);
                in = getResourceAsStream(configPath);
            }
            if (in != null)
            {
                Class.forName(DRIVER);// 加载驱动.
                initConfigure(in, false);// 初始化配置.
            }
        }
        catch (Throwable e)
        {
            log.error("Failed init ProxoolDriver,fileName=" + configPath
                + ",error=" + e.getMessage());
        }
        finally
        {
            StreamUtil.close(in);
            lock.unlock();
        }

        // 这里需要初始化数据源.
        dataSourceMap = initDataSource(sourceInfomap);

        return dataSourceMap;
    }

    /**
     * 初始化DruidDataSorce连接池
     * @param configPath
     * @param sourceInfomap
     * @return
     */
    private ConcurrentMap<String, DruidDataSource> initDruidProxool(String configPath,
                                                          ConcurrentMap<String, DataSourceInfo> sourceInfomap)
    {
        InputStream in = null;
        ConcurrentMap<String, DruidDataSource> dataSourceMap = null;
        try
        {
            lock.lock();
            File file = new File(configPath);
            if (file.exists())
            {
                in = new FileInputStream(file);
            }
            else
            {
                log.error("Failed load database config,file is not exist,path="
                    + configPath);
                in = getResourceAsStream(configPath);
            }
            if (in != null)
            {
                Class.forName(DRUID_DRIVER);// 加载驱动.
                initConfigure(in, false);// 初始化配置.
            }
        }
        catch (Throwable e)
        {
            log.error("Failed init ProxoolDriver,fileName=" + configPath
                + ",error=" + e.getMessage());
        }
        finally
        {
            StreamUtil.close(in);
            lock.unlock();
        }

        // 这里需要初始化数据源.
        dataSourceMap = initDruidDataSource(sourceInfomap);

        return dataSourceMap;
    }

    /**
     * 初始化数据源，初始化失败将抛出运行时异常.
     * 
     * @param sourceInfo 数据源配置信息.
     * @return ConcurrentMap<String,DataSource> 数据源信息.
     */
    private ConcurrentMap<String, DataSource> initDataSource(
        Map<String, DataSourceInfo> sourceInfo)
    {
        ConcurrentMap<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();
        if (sourceInfo == null || sourceInfo.isEmpty())
        {
            // throw new IllegalArgumentException("Failed init datasource.");
            log.error("Failed init dataSource,not found file.");
            return dataSourceMap;
        }
        String alias = "";// 数据源别名.
        DataSource sourceImpl = null;
        // 获取连接池类型配置
        String poolType = DBConfig.getInstance().getProperty("poolType");
        for (Map.Entry<String, DataSourceInfo> entry : sourceInfo.entrySet())
        {
            alias = entry.getKey();
            if (!AssertUtil.isEmpty(alias))
            {
                if (TOMCAT_JDBC_POOL.equals(poolType))
                {
                    sourceImpl = new TomcatPoolDataSourceImpl(alias);
                }
                else
                {
                    sourceImpl = new ProxoolDataSourceImpl(alias);
                }
                dataSourceMap.put(alias, sourceImpl);
            }
        }
        return dataSourceMap;
    }

    /**
     * 初始化数据源，初始化失败将抛出运行时异常.
     *
     * @param sourceInfo 数据源配置信息.
     * @return ConcurrentMap<String,DruidDataSource> 数据源信息.
     */
    private ConcurrentMap<String, DruidDataSource> initDruidDataSource(
        Map<String, DataSourceInfo> sourceInfo)
    {
        ConcurrentMap<String, DruidDataSource> dataSourceMap = new ConcurrentHashMap<String, DruidDataSource>();
        if (sourceInfo == null || sourceInfo.isEmpty())
        {
            // throw new IllegalArgumentException("Failed init datasource.");
            log.error("Failed init dataSource,not found file.");
            return dataSourceMap;
        }
        String alias = "";// 数据源别名.
        DruidDataSource sourceImpl = null;
        // 获取连接池类型配置
        String poolType = DBConfig.getInstance().getProperty("poolType");
        for (Map.Entry<String, DataSourceInfo> entry : sourceInfo.entrySet())
        {
            alias = entry.getKey();
            if (!AssertUtil.isEmpty(alias))
            {
                if (TOMCAT_JDBC_POOL.equals(poolType))
                {
                    sourceImpl = new TomcatPoolDruidDataSourceImpl(alias);
                }
                else
                {
                    sourceImpl = new DruidDataSourceImpl(alias);
                }
                dataSourceMap.put(alias, sourceImpl);
            }
        }
        return dataSourceMap;
    }

    /**
     * proxool初始化数据库配置,数据库连接池读取数据源
     * @param in 数据源.
     * @param validate 是否验证.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initConfigure(InputStream in, boolean validate)
    {
        String className = "org.logicalcobwebs.proxool.configuration.JAXPConfigurator";
        try
        {
            Class clazz = Class.forName(className);
            Class[] types = new Class[2];
            types[0] = org.xml.sax.InputSource.class;
            types[1] = boolean.class;
            Method method = clazz.getMethod("configure", types);
            Object[] objs = new Object[2];
            objs[0] = new org.xml.sax.InputSource(in);
            objs[1] = false;
            method.invoke(null, objs);// 执行初始化方法
        }
        catch (Exception e)
        {
            log.error("Failed init datasource configure,error" + e.getMessage());
        }
    }

    /**
     * 从classpath下加载资源文件.
     * @param path 加载路径.
     * @return 文件流.
     */
    private InputStream getResourceAsStream(String path)
    {
        InputStream in = null;
        try
        {
            in = Resource.getResourceAsStream(path);
        }
        catch (Exception ex)
        {
            log.error("Failed load config from classpath,configPath=" + path
                + ",error=" + ex.getMessage());
        }

        if (in != null)// 加载到资源就直接返回
        {
            return in;
        }
        log.info("start load default proxool config,configPath=proxool.xml");
        path = DATASOURCE_FILENAME_XML;// 加载proxool.xml文件
        try
        {
            in = Resource.getResourceAsStream(path);
        }
        catch (Exception ex)
        {
            log.error("Failed load proxool.xml from classpath,error="
                + ex.getMessage());
        }
        return in;
    }

    /**
     * 加载ibatis的配置文件，可以传入多个文件名
     * 
     * @param location 配置文件.
     */
    public void loaderIbatisConfig(String[] location)
    {
        SqlMapClientBeanFactory factory = new SqlMapClientBeanFactory();
        factory.initSqlMapConfig(location);
    }

    /**
     * 停止组件,释放资源
     */
    public final void destory()
    {
        lock.lock();
        try
        {
            if (running.get())
            {
                FilterManager.getInstance().clearAll();
                running.compareAndSet(true, false);
            }
            else
            {
                log.info("The persist service has been stoped.");
            }
        }
        finally
        {
            lock.unlock();
        }
    }
}

/**
 * 文件名：SqlMapClientFacade.java
 * 创建日期： 2012-9-4
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-9-4
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.dbcomponent.resourceloader.DefaultResourceLoader;
import richinfo.dbcomponent.resourceloader.ResourceLoader;
import richinfo.tools.common.AssertUtil;
import richinfo.tools.io.StreamUtil;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapConfigParser;

/**
 * 功能描述：sqlMapClient对象工厂类，创建SqlMapClient对象。
 * 
 * 创建日期： 2012-9-4 作者： zhou gui ping
 */
public class SqlMapClientBeanFactory
{

    private static Logger log = LoggerFactory.getLogger(SqlMapClientBeanFactory.class);
    /** SqlMapClient对象 */
    private static SqlMapClient sqlMapClient;
    /** sql映射资源,key为文件名,value为资源对象 */
    private static Map<String, ResourceLoader> resourceLoaderMap = new HashMap<String, ResourceLoader>();
    /** 可重入锁 */
    private static ReentrantLock lock = new ReentrantLock();

    /** ibatis配置文件信息 */
    private String[] configLocation;

    /** ibatis的映射文件 */
    private String[] sqlMapConfig;

    static
    {
        StartUp.getInstance().startUp();// 启动组件,加载驱动和数据源
    }

    public SqlMapClientBeanFactory()
    {

    }

    /**
     * 获取一个sqlMapClient对象，这个SqlMapClient对象没有设置数据源的 调用方需要自己设置数据源到这个client对象中.
     * 
     * @return SqlMapClient对象.
     */
    public static SqlMapClient createSqlMapClient() throws IOException
    {
        SqlMapClient client = null;
        InputStream is = null;
        SqlMapConfigParser configParser = null;

        lock.lock();
        try
        {
            if (sqlMapClient != null)
            {
                return sqlMapClient;
            }
            log.info("start create SqlMapClient instance.");
            configParser = new SqlMapConfigParser();
            for (Map.Entry<String, ResourceLoader> entry : resourceLoaderMap
                .entrySet())
            {
                log.info("config file name=" + entry.getKey()
                    + ", config file resource=" + entry.getValue());
                is = entry.getValue().getInputStream();
                client = configParser.parse(is);
            }
            sqlMapClient = client;
        }
        catch (RuntimeException ex)
        {
            log.error("Failed Instance SqlMapClient.", ex);
            throw new IOException("Failed to parse config resource,error="+ex.getMessage());
        }
        finally
        {
            lock.unlock();
            StreamUtil.close(is);
        }
        // if (mappingLocations != null) {
        // SqlMapParser mapParser =
        // SqlMapParserFactory.createSqlMapParser(configParser);
        // for (int i = 0; i < mappingLocations.length; i++) {
        // try {
        // mapParser.parse(mappingLocations[i].getInputStream());
        // }
        // catch (NodeletException ex) {
        // throw new NestedIOException("Failed to parse mapping resource: " +
        // mappingLocations[i], ex);
        // }
        // }
        // }
        return client;
    }

    public String[] getConfigLocation()
    {
        return configLocation;
    }

    /**
     * 设置文件路径,如classpath:/sql-map-config.xml
     * 
     * @param configLocation
     */
    public void setConfigLocation(String configLocation)
    {
        log.info("Start Dependency injection SqlMapClient config,configLocation="
            + configLocation);
        if (AssertUtil.isNotEmpty(configLocation))
        {
            this.configLocation = configLocation.split(",");// 多个地址进行分割
        }
        initSqlMapConfig(this.configLocation);
    }

    /**
     * 设置文件路径,如classpath:/sql-map-config.xml,WEB-INF/classes/sql-map-config.xml
     * 
     * @param configLocations 文件路径
     */
    public void setConfigLocation(String[] configLocations)
    {
        log.info("Start Dependency injection SqlMapClient config.");
        for (String config : configLocations)
        {
            log.info("injection SqlMapClient config,config=" + config);
        }
        this.configLocation = configLocations;
        initSqlMapConfig(this.configLocation);
    }

    public String[] getSqlMapConfig()
    {
        return sqlMapConfig;
    }

    /**
     * ibatis的映射文件名 ,用于加载多个sqlMap resource
     * 
     * @param sqlMapConfig 映射文件名.
     */
    public void setSqlMapConfig(String[] sqlMapConfig)
    {
        this.sqlMapConfig = sqlMapConfig;
    }

    public void setSqlMapClient(SqlMapClient sqlMapClient)
    {
        SqlMapClientBeanFactory.sqlMapClient = sqlMapClient;
    }

    /**
     * 初始化配置文件
     * @param config 配置文件
     */
    public void initSqlMapConfig(String[] config)
    {
        if (config == null || config.length == 0)
        {
            log.error("Failed load sqlMapconfig,config is null.");
            return;
        }
        lock.lock();
        try
        {
            DefaultResourceLoader loader = new DefaultResourceLoader();
            ResourceLoader resource = null;
            for (String con : config)
            {
                resource = loader.getResource(con);
                if (resource != null)
                {
                    resourceLoaderMap.put(con, resource);
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }

}

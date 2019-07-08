/**
 * 文件名：JdbcConnectionAdapt.java
 * 创建日期： 2012-9-3
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-9-3
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.components.log.logger.MonitorLog;
import richinfo.dbcomponent.service.impl.PersistContext;
import richinfo.dbcomponent.service.impl.StartUp;
import richinfo.dbcomponent.util.DBConstants;
import richinfo.tools.common.AssertUtil;

/**
 * 功能描述：jdbc连接适配器,该类主要提供获取Connection.
 * 
 * 创建日期： 2012-9-3 
 * 作者： zhou gui ping
 */
public class JdbcConnectionAdapt
{

    /** 默认的数据源别名 */
    public static final String DEFAULT_ALIAS = "proxool.oramail";
    /** 别名的前缀,必须要带上. */
    private static final String ALIAS_PREFIX = "proxool.";
    
    private static Logger logger = LoggerFactory.getLogger(JdbcConnectionAdapt.class);

    static
    {
        // 初始化数据库组件，只会初始化一次,线程安全
        StartUp.getInstance().startUp();
    }

    private JdbcConnectionAdapt()
    {
    }

    /**
     * 通过默认的别名获取数据库链接,默认数据库别名是proxool.oramail 不建议使用这个方法,可读性不好
     * 
     * @return 数据库链接.
     */
    public static Connection getConnection()
    {
        return getConnection(DEFAULT_ALIAS);
    }

    /**
     * 根据指定数据源别名获取一个数据库连接，如果alias为null或者为空，将返回null. 如果没有别名对应的数据源将返回null.
     * 
     * @param alias 别名.
     * @return 数据库连接.
     */
    public static Connection getConnection(String alias)
    {
        if (AssertUtil.isEmpty(alias))
        {
            logger.error("Failed Get DataSource by alias,alias is null.");
            return null;
        }
        String temp = alias;
        if (!alias.startsWith(ALIAS_PREFIX))
        {// 有了前缀就不加前缀,没有就加前缀.
            temp = ALIAS_PREFIX + alias;// 加上别名前缀
        }
        Connection conn = null;
        boolean result = PersistContext.containsAlias(temp);
        if (result)
        {
            DataSource dataSource = PersistContext.getDataSource(temp);
            if (dataSource != null)
            {
                try
                {
                    conn = getConnection(alias, dataSource);
                    return conn;
                }
                catch (SQLException e)
                {
                    logger.error("Failed Get Connection.", e);
                }
            }
            logger.error("Failed Get DataSource by alias,alias=" + temp);
            return null;
        }
        logger.error("Failed Get DataSource by alias,alias=" + temp);
        return null;
    }

    /**
     * 获取连接.
     * @param alias 别名.
     * @param dataSource 数据源对象.
     * @return 数据库连接.
     * @throws SQLException 数据库异常.
     */
    private static Connection getConnection(String alias, DataSource dataSource)
        throws SQLException
    {
        Connection conn = null;
        Date time = new Date();
        try
        {
            conn = dataSource.getConnection();
            conn.setAutoCommit(true);// 设置为自动提交
        }
        finally
        {
            writeMonitorLog(alias, time, conn != null);
        }
        return conn;
    }

    /**
     * 获取性能监控日志对象.
     * @param sqlId 数据库语句id.
     * @param time 开始时间.
     * @param result 执行结果.
     * @return 监控日志对象.
     */
    private static void writeMonitorLog(String alias, Date time, boolean result)
    {
        try
        {
            MonitorLog monitorLog = richinfo.components.log.logger.LoggerFactory.getMinitorLog();
            monitorLog.setBeginTime(time);
            monitorLog.setProtocol("tcp");
            monitorLog.setUrl(alias);
            monitorLog.setName("数据库组件");
            monitorLog.setResult(result);
            monitorLog.info("");
        }
        catch (Exception e)
        {}
    }
}

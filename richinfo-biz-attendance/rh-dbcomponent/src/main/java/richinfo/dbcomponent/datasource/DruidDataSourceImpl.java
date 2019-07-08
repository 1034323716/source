/**
 * 文件名：ProxoolDataSourceImpl.java
 * 创建日期： 2012-9-3
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-9-3
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.tools.common.AssertUtil;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * 功能描述：proxool数据源,proxool本身支持多数据源,所以该类只是简单的从proxool驱动中获取连接 一个数据源别名对应一个数据源
 * 
 * 创建日期： 2012-9-3 作者： zhou gui ping
 */
public class DruidDataSourceImpl extends DruidDataSource {

    private static Logger logger = LoggerFactory.getLogger(DruidDataSourceImpl.class);
    /** 数据源别名前缀 */
    private static final String DATASOURCE_PREFIX = "proxool.";
    /** 数据库别名 */
    private String alias = "";

    public DruidDataSourceImpl() {
    }

    public DruidDataSourceImpl(String alias) {
        if (AssertUtil.isEmpty(alias)) {
            logger.error("Failed create DataSource as alias is null.");
            throw new IllegalArgumentException("DataSource alias is null.");
        }
        if (!alias.startsWith(DATASOURCE_PREFIX)) {
            alias = DATASOURCE_PREFIX + alias; 
        }
        this.alias = alias;
    }

    /**
     * 根据别名获取数据库连接,获取连接失败将返回null.
     * 
     * @return Connection 数据库连接.
     * @exception SQLException
     */


    /**
     * 根据别名获取数据库连接,获取连接失败将返回null.
     * 
     * @param username 用户名.
     * @param password 密码.
     * @return Connection 数据库连接.
     * @exception SQLException
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public int getLoginTimeout()
    {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public PrintWriter getLogWriter()
    {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLoginTimeout(int seconds)
    {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public void setLogWriter(PrintWriter out)
    {
        DriverManager.setLogWriter(out);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface)
    {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface)
    {
        return null;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // TODO Auto-generated method stub
        return null;
    }

}

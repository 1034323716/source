package richinfo.dbcomponent.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.tools.common.AssertUtil;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by qiang on 2018/11/16.
 */
public class TomcatPoolDruidDataSourceImpl extends DruidDataSource{
    private static Logger logger = LoggerFactory.getLogger(TomcatPoolDataSourceImpl.class);

    /** 数据库别名 */
    private String alias = "";

    public TomcatPoolDruidDataSourceImpl(String alias) {
        if (AssertUtil.isEmpty(alias)) {
            logger.error("Failed create TomcatPoolDataSourceImpl as alias is null.");
            throw new IllegalArgumentException("TomcatPoolDataSourceImpl alias is null.");
        }
        this.alias = alias;
    }

    /**
     * 根据别名获取数据库连接,获取连接失败将返回null.
     *
     * @return Connection 数据库连接.
     * @exception SQLException
     */
    @Override
    public DruidPooledConnection getConnection() throws SQLException {
            //return  getConnection(10000);
        DruidPooledConnection conn = null;
        try {
            DruidDataSource dataSource = TomcatDruidDataSource.getInstance()
                .getDataSource(alias);
            // 异步方式获取数据库连接，提高并发响应能力
            conn = dataSource.getConnection(10000);
        } catch (Exception e) {
            logger.error("Failed Get TomcatPool Connection by alias " + alias, e);
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = null;
        }
        return conn;

    }

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
    public void setLogWriter(PrintWriter out) throws SQLException {
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

}

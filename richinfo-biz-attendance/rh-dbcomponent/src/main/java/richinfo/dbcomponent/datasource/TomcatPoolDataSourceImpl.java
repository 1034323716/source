package richinfo.dbcomponent.datasource;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.tools.common.AssertUtil;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TomcatPoolDataSourceImpl extends DataSource {
    private static Logger logger = LoggerFactory
        .getLogger(TomcatPoolDataSourceImpl.class);

    /** 数据库别名 */
    private String alias = "";

    public TomcatPoolDataSourceImpl(String alias) {
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
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            DataSource datasource = TomcatDataSource.getInstance()
                .getDataSource(alias);
            // 异步方式获取数据库连接，提高并发响应能力
            Future<Connection> future = datasource.getConnectionAsync();
//            while (!future.isDone())
//            {
//                logger
//                    .info("Connection is not yet available. Do some background work!");
//                try
//                {
//                    Thread.sleep(100);
//                }
//                catch (InterruptedException x)
//                {
//                    Thread.currentThread().interrupt();
//                }
//            }
//            conn = future.get();
              conn = future.get(10000, TimeUnit.MILLISECONDS);
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
    public PrintWriter getLogWriter() throws SQLException {
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
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
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

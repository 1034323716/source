/**
 * 文件名：IbatisPersistServiceImpl.java
 * 创建日期： 2012-8-21
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-8-21
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.service.impl;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.components.log.logger.MonitorLog;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.dbcomponent.service.PersistService;
import richinfo.dbcomponent.util.DBConfig;
import richinfo.dbcomponent.util.DBUtil;
import richinfo.tools.common.AssertUtil;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 功能描述：所有数据库操作会委托给ibatis处理,数据源由外部提供.
 * 
 * 创建日期： 2012-8-21 作者： zhou gui ping
 */
public final class IbatisPersistServiceImpl implements PersistService
{

    private static Logger log = LoggerFactory
        .getLogger(IbatisPersistServiceImpl.class);

    /** 数据库别名 */
    private String alias = "";

    /** ibatis执行客户端 */
    private SqlMapClient sqlMapClient;

    /** ibatis执行客户端代理对象 */
    private IbatisSqlMapClientProxy proxy;

    /** 最大并发请求数，默认500 */
    private int requestMax = 500;

    /** 最大session并发请求数，默认128 */
    private int sessionMax = 128;

    public IbatisPersistServiceImpl(String alias)
    {
        this.alias = alias;
    }

    public DataSource getDataSource()
    {
        return PersistContext.getDataSource(alias);
    }

    /**
     * 初始化数据库组件对象.
     */
    private void initSqlMapClient()
    {
        try
        {
            if (this.sqlMapClient == null)
            {
                this.sqlMapClient = SqlMapClientBeanFactory.createSqlMapClient();
                ExtendedSqlMapClient client = (ExtendedSqlMapClient) sqlMapClient;
                SqlMapExecutorDelegate delegate = client.getDelegate();
                sessionMax = DBConfig.getInstance().getPropertyInt(
                    "ibatis.max.concurrent.session", 128);
                requestMax = DBConfig.getInstance().getPropertyInt(
                    "ibatis.max.concurrent.request", 500);
                delegate.setMaxSessions(sessionMax);// 增大并发量，默认才128，太小了
                delegate.setMaxRequests(requestMax);// 增大并发量，默认才500，太小了
                // 实例化代理对象
                proxy = new IbatisSqlMapClientProxy(delegate);
            }
        }
        catch (Exception e)
        {
            log.error("Failed createSqlMapClient.", e);
        }
    }

    /**
     * 执行操作.
     * @param action 待执行操作.
     * @param sqlId sql语句标识.
     * @return 操作返回结果.
     * @throws PersistException
     */
    private Object execute(String sqlId, SqlMapClientCallback action)
        throws PersistException
    {
        AssertUtil.assertNotNull(action, "SqlMapClientCallback");
        initSqlMapClient();
        AssertUtil.assertNotNull(this.sqlMapClient, "SqlMapClient");
        AssertUtil.assertNotNull(this.proxy, "proxy");
        SqlMapSessionImpl session = null;
        Connection persistCon = null;
        // 是否是自动提交，不是自动提交说明是手动事务
        boolean isAutoCommit = TransactionStateInfo.geTransactionStateInfo().isAutoCommit();
        try
        {
            // 从当前现存获取一个Session对象
            session = proxy.getLocalSession();
            persistCon = session.getCurrentConnection();
            if (persistCon == null || persistCon.isClosed())
            {
             // 手动事务控制这里可能不为null.
             // 从数据源中获取连接,再将连接设置到SqlMapSession对象中.
                persistCon = getConnection(sqlId, isAutoCommit);
                if(persistCon == null){//从池里面获取连接失败
                    throw new PersistException("Failed Get Connection from pool.");
                }
                session.setUserConnection(persistCon);
            }
            return action.doInSqlMapClient(session);
        }
        catch (SQLException e)
        {
            log.error("Failed Get SqlMapSession.", e);
            throw new PersistException("Failed Get SqlMapSession.", e);
        }
        finally
        {
            if (isAutoCommit)
            {
                try{
                    if(session !=null){
                        session.close();
                    }
                }catch(Exception e){
                    //.....
                }
            }
            if (isAutoCommit && persistCon != null)
            {
                DBUtil.close(persistCon);
            }
        }
    }

    /**
     * 获取连接方法，记录获取连接监控日志.
     * @param sqlId sql标识.
     * @param isAutoCommit 是否自动提交.
     * @return 数据库连接.
     * @throws SQLException
     */
    private Connection getConnection(String sqlId,
        boolean isAutoCommit) throws SQLException
    {
        Date startTime = new Date();
        Connection persistCon = null;
        try
        {
            persistCon = getDataSource().getConnection();
           // log.info("数据源=====================getDataSource()={}|Connection={}",getDataSource(),persistCon);
            if (!isAutoCommit && persistCon!=null)
            {// 将连接对象保存起来
                persistCon.setAutoCommit(false);
                TransactionStateInfo.geTransactionStateInfo().setConnection(
                    persistCon);
            }
        }
        finally
        {
            writeMonitorLog(sqlId, startTime, persistCon);
        }
        return persistCon;
    }


    /**
     * 获取性能监控日志对象.
     * @param sqlId 数据库语句id.
     * @param time 开始时间.
     * @param con 数据库连接.
     * @return 监控日志对象.
     */
    private void writeMonitorLog(String sqlId, Date time, Connection con)
    {
        try
        {
            MonitorLog monitorLog = richinfo.components.log.logger.LoggerFactory.getMinitorLog();
            monitorLog.setBeginTime(time);
            monitorLog.setProtocol("tcp");
            monitorLog.setUrl("alias=" + alias + ",sqlId=" + sqlId);
            monitorLog.setName("数据库组件");
            monitorLog.setResult(con != null);
            monitorLog.info("");
        }
        catch (Exception e)
        {

        }
    }
    
    @Override
    public Object insert(final String sqlId, final Object params)
        throws PersistException
    {
        Object object = execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return executor.insert(sqlId, params);
                }
                catch (Exception e)
                {
                    log.error("Failed insert data by sqlId,sqlId=" + sqlId
                        + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
        if (object != null)
        {
            log.info("insert data result ,obj=" + object);
        }
        else
        {
            log.warn("insert data result ,obj==null");
        }
        return object;
    }
    
    @Override
    public int delete(final String sqlId, final Object params)
        throws PersistException
    {
        Integer result = (Integer) execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return new Integer(executor.delete(sqlId, params));
                }
                catch (SQLException e)
                {
                    log.error("Failed delete data by sqlId,sqlId=" + sqlId
                        + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
        return result.intValue();
    }
    
    @Override
    public int update(final String sqlId, final Object params)
        throws PersistException
    {
        Integer result = (Integer) execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return new Integer(executor.update(sqlId, params));
                }
                catch (SQLException e)
                {
                    log.error("Failed update data by sqlId,sqlId=" + sqlId
                        + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
        return result.intValue();
    }

    /**
     * 查询数据.
     * 
     * @param sqlId sql语句标识.
     * @param parameterObject 参数对象.
     * @param keyProperty 属性key.
     * @return Map 结果.
     */    
    public Map<?,?> queryForMap(final String sqlId, final Object parameterObject,
        final String keyProperty) throws PersistException
    {
        return executeWithMapResult(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return executor.queryForMap(sqlId, parameterObject,
                        keyProperty);
                }
                catch (Exception e)
                {
                    log.error("Failed query data by sqlId,sqlId=" + sqlId
                        + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 查询数据信息封装到map对象中.
     * 
     * @param sqlId sql语句标识.
     * @param parameterObject 查询参数.
     * @param keyProperty 属性key.
     * @param valueProperty 属性value.
     * @return Map 返回数据map
     */    
    public Map<?,?> queryForMap(final String sqlId, final Object parameterObject,
        final String keyProperty, final String valueProperty)
        throws PersistException
    {
        return executeWithMapResult(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return executor.queryForMap(sqlId, parameterObject,
                        keyProperty, valueProperty);
                }
                catch (Exception e)
                {
                    log.error("Failed query data by sqlId,sqlId=" + sqlId
                        + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 执行数据查询功能.
     * 
     * @param sqlId sql语句标识.
     * @param skipResults 跳过多少条记录集.
     * @param maxResults 最大记录集.
     * @return 返回查询数据列表.
     * @throws PersistException 抛出持久化异常.
     */
    @SuppressWarnings("rawtypes")
    public List queryForList(String sqlId, int skipResults, int maxResults)
        throws PersistException
    {
        return queryForList(sqlId, null, skipResults, maxResults);
    }

    /**
     * 执行数据查询.
     * 
     * @param sqlId sql语句标识.
     * @param parameterObject 参数对象.
     * @param skipResults 跳过多少条记录集.
     * @param maxResults 最大记录集.
     * @return
     * @throws PersistException
     */
    @SuppressWarnings("rawtypes")
    public List queryForList(final String sqlId, final Object parameterObject,
        final int skipResults, final int maxResults) throws PersistException
    {
        return executeWithListResult(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return executor.queryForList(sqlId, parameterObject,
                        skipResults, maxResults);
                }
                catch (Exception e)
                {
                    log.error("Failed query data by sqlId,sqlId=" + sqlId
                        + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 执行指定的操作.
     * 
     * @param action 待执行的操作.
     * @param sqlId sql标识.
     * @return 返回执行结果List.
     * @throws PersistException
     */
    @SuppressWarnings("rawtypes")
    private List executeWithListResult(String sqlId, SqlMapClientCallback action)
        throws PersistException
    {
        return (List) execute(sqlId, action);
    }

    /**
     * 执行指定的操作
     * 
     * @param action 待执行的操作.
     * @param sqlId sql标识.
     * @return 返回执行结果Map.
     * @throws PersistException
     */
    @SuppressWarnings("rawtypes")
    private Map executeWithMapResult(final String sqlId,
        SqlMapClientCallback action) throws PersistException
    {
        return (Map) execute(sqlId, action);
    }

    /**
     * 查询数据信息.
     * @param statementName sql语句标识.
     * @param parameterObject 参数对象.
//     * @param Object 返回数据.
     */
    public Object queryForObject(final String statementName,
        final Object parameterObject) throws PersistException
    {
        return execute(statementName, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return executor.queryForObject(statementName,
                        parameterObject);
                }
                catch (Exception e)
                {
                    log.error("Failed query data by sqlId,sqlId="
                        + statementName + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 查询数据信息.
     * 
     * @param sqlId sql语句标识.
     * @param parameterObject 参数对象.
     * @param resultObject 结果对象.
     * @return object 返回数据
     * @throws PersistException
     */
    public Object queryForObject(final String sqlId,
        final Object parameterObject, final Object resultObject)
        throws PersistException
    {
        return execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return executor.queryForObject(sqlId, parameterObject,
                        resultObject);
                }
                catch (Exception e)
                {
                    log.error("Failed query data by sqlId,sqlId=" + sqlId
                        + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 查询数据信息.
     * @param sqlId sql语句标识.
     * @param parameterObject 参数对象.
     * @return List 返回数据列表
     */    
    public List<?> queryForList(final String sqlId, final Object parameterObject)
        throws PersistException
    {
        return executeWithListResult(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    return executor.queryForList(sqlId, parameterObject);
                }
                catch (Exception e)
                {
                    log.error("Failed query data by sqlId,sqlId=" + sqlId
                        + ",cause=" + e.getMessage());
                    throw new PersistException(e.getMessage(), e);
                }
            }
        });
    }
    
    @Override
    public boolean batchInsert(final String sqlId, final List<?> paramsList)
        throws PersistException
    {
        startTransaction();
        execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    // executor.startBatch();
                    for (Object param : paramsList)
                    {
                        executor.insert(sqlId, param);
                    }
                    commitTransaction();
                }
                catch (SQLException e)
                {
                    log.error("Failed batchInsert,cause=" + e.getMessage());
                    rollbackTransaction();
                    throw new PersistException(e.getMessage(), e);
                }
                return null;
            }
        });
        return true;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean batchDelete(final String sqlId, final List paramsList)
        throws PersistException
    {
        startTransaction();
        execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    // executor.startBatch();
                    for (Object param : paramsList)
                    {
                        executor.delete(sqlId, param);
                    }
                    commitTransaction();
                }
                catch (SQLException e)
                {
                    log.error("Failed batchDelete,cause=" + e.getMessage());
                    rollbackTransaction();
                    throw new PersistException(e.getMessage(), e);
                }
                return null;
            }
        });
        return true;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean batchUpdate(final String sqlId, final List paramsList)
        throws PersistException
    {
        startTransaction();
        execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    // executor.startBatch();
                    for (Object param : paramsList)
                    {
                        executor.update(sqlId, param);
                    }
                    commitTransaction();
                }
                catch (SQLException e)
                {
                    log.error("Failed batchUpdate,cause=" + e.getMessage());
                    rollbackTransaction();
                    throw new PersistException(e.getMessage(), e);
                }
                return null;
            }
        });
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void startTransaction() throws PersistException
    {
        TransactionStateInfo info = TransactionStateInfo
            .geTransactionStateInfo();
        info.setAutoCommit(false);
        info.setState(State.start);
        initSqlMapClient();
        log.info("start transaction...");
    }

    /** {@inheritDoc} */
    @Override
    public void commitTransaction() throws PersistException
    {
        // 是否是自动提交，不是自动提交说明是手动事务
        boolean isAutoCommit = TransactionStateInfo.geTransactionStateInfo()
            .isAutoCommit();
        if (!isAutoCommit)
        {
            TransactionStateInfo.geTransactionStateInfo()
                .setState(State.commit);
            SqlMapSessionImpl session = proxy.getLocalSession();
            Connection conn = TransactionStateInfo.geTransactionStateInfo()
                .getConnection();
            try
            {
                commit(session, conn);
                log.info("commit transaction...");
            }
            catch (Exception e)
            {
                log.error("Failed commit transaction,cause=" + e.getMessage());
                throw new PersistException("Failed commit transaction.", e);
            }
            finally
            {
                TransactionStateInfo.geTransactionStateInfo().clear();// 清除事务信息
                DBUtil.close(conn);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void rollbackTransaction() throws PersistException
    {
        // 是否是自动提交，不是自动提交说明是手动事务
        boolean isAutoCommit = TransactionStateInfo.geTransactionStateInfo()
            .isAutoCommit();
        if (!isAutoCommit)
        {
            TransactionStateInfo.geTransactionStateInfo().setState(State.end);
            Connection conn = TransactionStateInfo.geTransactionStateInfo()
                .getConnection();
            SqlMapSessionImpl session = proxy.getLocalSession();

            try
            {
                rollback(session, conn);
                log.info("rollback transaction...");
            }
            catch (Exception e)
            {
                log.error("Failed end transaction,cause=" + e.getMessage());
                throw new PersistException("Failed end transaction.", e);
            }
            finally
            {
                TransactionStateInfo.geTransactionStateInfo().clear();// 清理
                DBUtil.close(conn);
            }
        }
    }

    /**
     * 提交操作和关闭会话.
     * @param session 会话.
     * @param conn 数据库连接.
     */
    private void commit(SqlMapSessionImpl session, Connection conn)
    {
        try
        {
            if (conn != null && !conn.isClosed())
            {
                conn.commit();
            }
        }
        catch (Exception e)
        {
            log.error("Failed close connection,cause=" + e.getMessage());
        }
        try
        {
            if (session != null)
            {
                session.close();
            }
        }
        catch (Exception e)
        {
            log.error("Failed close session,cause=" + e.getMessage());
        }
    }
    

    /**
     * 回滚操作和关闭会话.
     * @param session 会话.
     * @param conn 数据库连接.
     */
    private void rollback(SqlMapSessionImpl session, Connection conn)
    {
        try
        {
            if (conn != null && !conn.isClosed())
            {
                conn.rollback();
            }
        }
        catch (Exception e)
        {
            log.error("Failed close connection,cause=" + e.getMessage());
        }
        try
        {
            if (session != null)
            {
                session.close();
            }
        }
        catch (Exception e)
        {
            log.error("Failed close session,cause=" + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean batchInsertNoTransaction(final String sqlId, final List paramsList)
        throws PersistException
    {
        execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    // executor.startBatch();
                    for (Object param : paramsList)
                    {
                        executor.insert(sqlId, param);
                    }
                }
                catch (SQLException e)
                {
                    log.error("Failed batchInsert,cause=" + e.getMessage());
                    rollbackTransaction();
                    throw new PersistException(e.getMessage(), e);
                }
                return null;
            }
        });
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean batchDeleteNoTransaction(final String sqlId, final List paramsList)
        throws PersistException
    {
        execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    // executor.startBatch();
                    for (Object param : paramsList)
                    {
                        executor.delete(sqlId, param);
                    }
                }
                catch (SQLException e)
                {
                    log.error("Failed batchDelete,cause=" + e.getMessage());
                    rollbackTransaction();
                    throw new PersistException(e.getMessage(), e);
                }
                return null;
            }
        });
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean batchUpdateNoTransaction(final String sqlId, final List paramsList)
        throws PersistException
    {
        execute(sqlId, new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                throws PersistException
            {
                try
                {
                    // executor.startBatch();
                    for (Object param : paramsList)
                    {
                        executor.update(sqlId, param);
                    }
                }
                catch (SQLException e)
                {
                    log.error("Failed batchUpdate,cause=" + e.getMessage());
                    rollbackTransaction();
                    throw new PersistException(e.getMessage(), e);
                }
                return null;
            }
        });
        return true;
    }

    /**
     * 原生态sql查询
     * @param sql
     * @return
     * @throws PersistException
     */
    @Override
    public List originalSqlQuery(String sql) throws PersistException {
        Connection connection = null;
        ResultSet resultSet = null;
        List<Map<String,Object>>dataByClassFiled = new ArrayList<>();
        try {
            //log.info("获取连接======");
             connection = getDataSource().getConnection();
           // log.info("connection连接======{}",connection);
            Statement statement = connection.createStatement();
             resultSet = statement.executeQuery(sql);
            // log.info("==============resultSet={}",resultSet);
            ResultSetMetaData md = resultSet.getMetaData(); //获得结果集结构信息,元数据
            int columnCount = md.getColumnCount();   //获得列数
            while (resultSet.next()) {
                Map<String,Object> rowData = new HashMap<String,Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), resultSet.getObject(i));
                }
                dataByClassFiled.add(rowData);

            }
            resultSet.close();
            connection.close();
            return dataByClassFiled;
        } catch (SQLException e) {
        }finally {
            if (resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataByClassFiled;
    }
}

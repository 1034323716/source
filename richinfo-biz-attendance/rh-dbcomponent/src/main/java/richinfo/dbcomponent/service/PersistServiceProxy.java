package richinfo.dbcomponent.service;

import richinfo.components.log.logger.LoggerFactory;
import richinfo.components.log.logger.MonitorLog;
import richinfo.dbcomponent.exception.PersistException;
import richinfo.dbcomponent.service.impl.IbatisPersistServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * 功能描述：持久化客户端代理类,提供数据持久化，这里不提供Connection对象. 主要是做性能监控用.
 * 
 * 创建日期 ： 2012-9-3 作者： zhou gui ping
 * 
 */
public class PersistServiceProxy implements PersistService
{

    /** 监控协议名称 */
    private static final String MONITOR_PROTOCOL_NAME = "jdbc";
    /** 数据源别名,根据这个别名获取对应的数据源 */
    private String alias = "";
    /** 持久化服务 */
    private PersistService service;

    public PersistServiceProxy(PersistService service)
    {
        this.service = service;
    }

    public PersistServiceProxy(String alias)
    {
        this.alias = alias;
        service = new IbatisPersistServiceImpl(this.alias);
    }

    /**
     * 获取性能监控日志对象.
     * @param sqlId 数据库语句id.
     * @param time 开始时间.
     * @param result 执行结果.
     * @return 监控日志对象.
     */
    private void writeMonitorLog(String sqlId, Date time, boolean result)
    {
        try
        {
            MonitorLog monitorLog = LoggerFactory.getMinitorLog();
            monitorLog.setBeginTime(time);
            monitorLog.setProtocol(MONITOR_PROTOCOL_NAME);
            monitorLog.setUrl(sqlId);
            monitorLog.setName("数据库组件");
            monitorLog.setResult(result);
            monitorLog.info("");//无需传入任何信息.
        }
        catch (Exception e)
        {
            //...忽略异常
        }
    }

    /** {@inheritDoc} */
    @Override
    public int delete(String sqlId, Object params) throws PersistException
    {
        Date time = new Date();
        int result = 0;
        try
        {
            result = service.delete(sqlId, params);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result > 0);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Object insert(String sqlId, Object params) throws PersistException
    {
        Object result = null;
        Date time = new Date();
        try
        {
            result = service.insert(sqlId, params);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result != null);
        }
        return result;
    }
   
    @Override
    public List<?> queryForList(String sqlId, Object params)
        throws PersistException
    {
        List<?> list = null;
        Date time = new Date();
        try
        {
            list = service.queryForList(sqlId, params);
        }
        finally
        {
            writeMonitorLog(sqlId, time, list != null);
        }
        return list;
    }
    
    @Override
    public Map<?,?> queryForMap(String sqlId, Object params, String keyProperty,
        String valueProperty) throws PersistException
    {
        Map<?,?> map = null;
        Date time = new Date();
        try
        {
            map = service
                .queryForMap(sqlId, params, keyProperty, valueProperty);
        }
        finally
        {
            writeMonitorLog(sqlId, time, map != null);
        }
        return map;
    }
    
    @Override
    public Map<?,?> queryForMap(String sqlId, Object params, String keyProperty)
        throws PersistException
    {
        Map<?,?> map = null;
        Date time = new Date();
        try
        {
            map = service.queryForMap(sqlId, params, keyProperty);
        }
        finally
        {
            writeMonitorLog(sqlId, time, map != null);
        }
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public Object queryForObject(String sqlId, Object params)
        throws PersistException
    {
        Object object = null;
        Date time = new Date();
        try
        {
            object = service.queryForObject(sqlId, params);
        }
        finally
        {
            writeMonitorLog(sqlId, time, object != null);
        }
        return object;
    }

    /** {@inheritDoc} */
    @Override
    public int update(String sqlId, Object params) throws PersistException
    {
        int result = 0;
        Date time = new Date();
        try
        {
            result = service.update(sqlId, params);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result > 0);
        }
        return result;
    }

    /** {@inheritDoc} */    
    @Override
    public boolean batchInsert(String sqlId, List<?> paramsList)
        throws PersistException
    {
        boolean result = false;
        Date time = new Date();
        try
        {
            result = service.batchInsert(sqlId, paramsList);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result);
        }
        return result;
    }

    /** {@inheritDoc} */    
    @Override
    @SuppressWarnings("rawtypes")
    public boolean batchDelete(String sqlId, List paramsList)
        throws PersistException
    {
        boolean result = false;
        Date time = new Date();
        try
        {
            result = service.batchDelete(sqlId, paramsList);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result);
        }
        return result;
    }

    /** {@inheritDoc} */    
    @Override
    @SuppressWarnings("rawtypes")
    public boolean batchUpdate(String sqlId, List paramsList)
        throws PersistException
    {
        boolean result = false;
        Date time = new Date();
        try
        {
            result = service.batchUpdate(sqlId, paramsList);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void startTransaction() throws PersistException
    {
        service.startTransaction();
    }

    /** {@inheritDoc} */
    @Override
    public void commitTransaction() throws PersistException
    {
        service.commitTransaction();
    }

    /** {@inheritDoc} */
    @Override
    public void rollbackTransaction() throws PersistException
    {
        service.rollbackTransaction();
    }

    public PersistService getService()
    {
        return service;
    }

    public void setService(PersistService service)
    {
        this.service = service;
    }

    @Override
    public boolean batchInsertNoTransaction(String sqlId, List<?> paramsList)
        throws PersistException
    {
        boolean result = false;
        Date time = new Date();
        try
        {
            result = service.batchInsertNoTransaction(sqlId, paramsList);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result);
        }
        return result;
    }

    @Override
    public boolean batchDeleteNoTransaction(String sqlId, List paramsList)
        throws PersistException
    {
        boolean result = false;
        Date time = new Date();
        try
        {
            result = service.batchDeleteNoTransaction(sqlId, paramsList);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result);
        }
        return result;
    }

    @Override
    public boolean batchUpdateNoTransaction(String sqlId, List paramsList)
        throws PersistException
    {
        boolean result = false;
        Date time = new Date();
        try
        {
            result = service.batchUpdateNoTransaction(sqlId, paramsList);
        }
        finally
        {
            writeMonitorLog(sqlId, time, result);
        }
        return result;
    }

    /**
     * 原生态sql查询
     * @param sql
     * @return
     * @throws PersistException
     */
    @Override
    public List<?> originalSqlQuery(String sql) throws PersistException {
        List<?> list = null;
        Date time = new Date();
        try
        {
            list = service.originalSqlQuery(sql);
        }
        finally
        {
            writeMonitorLog(sql, time, list != null);
        }
        return list;
    }
}

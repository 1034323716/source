package richinfo.dbcomponent.service;

import richinfo.dbcomponent.exception.PersistException;

import java.util.List;
import java.util.Map;

/**
 * 
 * 功能描述：数据库持久化服务,提供数据库的统一服务.
 * 
 * 创建日期： 2012-9-3 
 * 作者： zhou gui ping
 * 
 */
public interface PersistService
{

    /**
     * 新增一条数据到数据库中.
     * 
     * @param sqlId sql标识.
     * @param params 参数对象,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 返回唯一标识.
     * @throws PersistException 持久化异常.
     */
    public Object insert(final String sqlId, final Object params)
        throws PersistException;

    /**
     * 批量插入数据到数据库中,批量新增数据成功返回true,批量新增失败将会回滚，返回false.
     * 
     * @param sqlId sql标识.
     * @param paramsList 参数列表,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 批量新增成功返回true,否则返回false.
     * @throws PersistException 持久化异常.
     */    
    public boolean batchInsert(final String sqlId, final List<?> paramsList)
        throws PersistException;
    
    /**
     * 批量插入数据到数据库中,批量新增数据成功返回true,批量新增失败返回false. 
     * 需要外部调用者调用前手动开启事务，否则不能保证批量提交效果。
     * 
     * @param sqlId sql标识.
     * @param paramsList 参数列表,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 批量新增成功返回true,否则返回false.
     * @throws PersistException 持久化异常.
     */    
    public boolean batchInsertNoTransaction(final String sqlId, final List<?> paramsList)
        throws PersistException;

    /**
     * 删除一条数据
     * @param sqlId sql标识.
     * @param params 待传入的参数,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 影响的行数.
     * @throws PersistException 持久化异常.
     */
    public int delete(final String sqlId, final Object params)
        throws PersistException;

    /**
     * 批量删除数据,批量删除数据成功返回true,批量删除失败将会回滚，返回false.
     * 
     * @param sqlId sql标识.
     * @param paramsList 参数列表,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 批量删除成功返回true,否则返回false.
     * @throws PersistException 持久化异常.
     */   
    @SuppressWarnings("rawtypes")
    public boolean batchDelete(final String sqlId, final List paramsList)
        throws PersistException;
    
    /**
     * 批量删除数据,批量删除数据成功返回true,批量删除失败返回false.
     * 需要外部调用者调用前手动开启事务，否则不能保证批量提交效果。
     * 
     * @param sqlId sql标识.
     * @param paramsList 参数列表,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 批量删除成功返回true,否则返回false.
     * @throws PersistException 持久化异常.
     */   
    @SuppressWarnings("rawtypes")
    public boolean batchDeleteNoTransaction(final String sqlId, final List paramsList)
        throws PersistException;

    /**
     * 更新一条数据.
     * 
     * @param sqlId sql标识
     * @param params 待传入的参数,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 更新影响的行数.
     * @throws PersistException 持久化异常.
     */
    public int update(final String sqlId, final Object params)
        throws PersistException;

    /**
     * 批量更新数据,批量更新数据成功返回true,批量更新失败将会回滚，返回false.
     * 需要外部调用者调用前手动开启事务，否则不能保证批量提交效果。
     * 
     * @param sqlId sql标识.
     * @param paramsList 参数列表,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 批量更新成功返回true,否则返回false.
     * @throws PersistException
     */   
    @SuppressWarnings("rawtypes")
    public boolean batchUpdateNoTransaction(final String sqlId, final List paramsList)
        throws PersistException;
    
    /**
     * 批量更新数据,批量更新数据成功返回true,批量更新失败返回false.
     * 
     * @param sqlId sql标识.
     * @param paramsList 参数列表,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 批量更新成功返回true,否则返回false.
     * @throws PersistException
     */   
    @SuppressWarnings("rawtypes")
    public boolean batchUpdate(final String sqlId, final List paramsList)
        throws PersistException;

    /**
     * 查询数据列表.
     * 
     * @param sqlId SQL配置文件的ID.
     * @param params sql所需要的参数,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return 返回数据列表.
     */    
    @SuppressWarnings("rawtypes")
    public List queryForList(final String sqlId, final Object params)
        throws PersistException;

    /**
     * 查询数据列表.
     * 
     * @param sqlId SQL配置文件的ID.
     * @param params sql所需要的参数,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @param keyProperty map 的 key
     * @return 数据列表.
     */    
    @SuppressWarnings("rawtypes")
    public Map queryForMap(final String sqlId, final Object params,
        final String keyProperty) throws PersistException;

    /**
     * 查询数据列表.
     * 
     * @param sqlId SQL配置文件的ID.
     * @param params sql所需要的参数,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @param keyProperty map 的 key.
     * @param valueProperty
     * @return
     */   
    @SuppressWarnings("rawtypes")
    public Map queryForMap(final String sqlId, final Object params,
        final String keyProperty, final String valueProperty)
        throws PersistException;

    /**
     * 查询操作,返回一行数据时采用此方法.
     * 
     * @param sqlId SQL配置文件的ID.
     * @param params sql所需要的参数,参数比较多时建议传入vo对象,参数不多使用Map或者常用类型,如String,int等.
     * @return
     */
    public Object queryForObject(final String sqlId, final Object params)
        throws PersistException;

    /**
     * 手动开启事务.
     * @throws PersistException
     */
    public void startTransaction() throws PersistException;

    /**
     * 手动提交事务.
     * @throws PersistException
     */
    public void commitTransaction() throws PersistException;

    /**
     * 结束事务.
     * @throws PersistException
     */
    public void rollbackTransaction() throws PersistException;

    /**
     * 原生态sql查询
     * @param sql
     * @return
     * @throws PersistException
     */
    public List originalSqlQuery(final String sql) throws PersistException;
}

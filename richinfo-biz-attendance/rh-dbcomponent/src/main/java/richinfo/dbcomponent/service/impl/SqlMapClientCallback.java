/**
* 文件名：SqlMapClientCallback.java
* 创建日期： 2012-8-31
* 作者：     zhou gui ping
* Copyright (c) 2009-2011 产品开发一部
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2012-8-31
*   修改人：zhou gui ping
*   修改内容：
*/
package richinfo.dbcomponent.service.impl;


import richinfo.dbcomponent.exception.PersistException;

import com.ibatis.sqlmap.client.SqlMapExecutor;

/**
 * 功能描述：业务操作回调接口,统一处理持久化服务的接口，统一参数，作用是减少重复代码
 *           将多个功能入口收敛到一个地方，好维护及统一控制.
 * 
 * 创建日期： 2012-8-31
 * 作者：           zhou gui ping
 */
public interface SqlMapClientCallback
{
    
    /**
     * sqlMapClient统一执行方法,
     * 
     * @param executor Sql执行器.
     * @return 返回结果.
     * @throws PersistException 持久化异常.
     */
    public Object doInSqlMapClient(SqlMapExecutor executor) throws PersistException;
}

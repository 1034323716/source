/**
 * 文件名：IbatisSqlMapClientProxy.java
 * 创建日期： 2012-9-11
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 *   1.修改时间：2012-9-11
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.service.impl;

import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl;

/**
 * 功能描述：该类所有的事情都委托给父类执行，这里只是想获取到SqlMapSession对象.
 * 获取这个对象是为了做手动事务处理用,手动处理事务需要确保在一个线程
 * 中操作多个方法是用的是同一个Connection对象，所以需要将SqlMapSession对象
 * 放置到线程上下文中,而ibatis中的getLocalSqlMapSession()方法修饰符是子类
 * 才能访问,所以这里为了每次从线程上下文中获取Session而继承了父类并重写了该 方法从而达到目的.
 * 
 * 创建日期： 2012-9-11 作者： zhou gui ping
 */
public class IbatisSqlMapClientProxy extends SqlMapClientImpl
{

    /**
     * 初始化父类.
     * @param delegate
     */
    public IbatisSqlMapClientProxy(SqlMapExecutorDelegate delegate)
    {
        super(delegate);
    }

    /**
     * 每次从线程上下文中获取一个会话，在一个线程里面每次获取到的是同一个SqlMapSession
     * 同一个SqlMapSession获取到的Connection当然也是同一个.从而到达控制事务.
     * 
     * @return SqlMapSessionImpl对象.
     */
    public SqlMapSessionImpl getLocalSession()
    {
        // 调用父类的方法获取getLocalSqlMapSession();
        return super.getLocalSqlMapSession();
    }
}

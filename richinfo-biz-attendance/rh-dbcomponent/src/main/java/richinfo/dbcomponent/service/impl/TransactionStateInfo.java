/**
 * 文件名：TransactionStateInfo.java
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

import java.sql.Connection;

/**
 * 功能描述：事务状态信息，主要用于手动事务处理,当用户需要控制多个操作 在同一个事务中 时须确保一致性 所以需要确保几个操作的信息一致保持着 直到事务结束.
 * 
 * 创建日期： 2012-9-11 作者： zhou gui ping
 */
public class TransactionStateInfo
{

    /** 线程本地变量,保存事务信息 */
    private static ThreadLocal<TransactionStateInfo> local = new ThreadLocal<TransactionStateInfo>() {
        @Override
        protected TransactionStateInfo initialValue()
        {
            return new TransactionStateInfo();
        }
    };

    /** 是否自动提交，默认是自动提交的,即不做事务控制 */
    private boolean isAutoCommit = true;

    /** 　事务当前的状态 */
    private State state;

    /** 数据库连接对象 */
    private Connection connection;

    /**
     * 获取当前线程事务状态信息,同一个线程调用多次获取的信息相同, 第一次 调用会实例化一个新的事务信息并放置到本地线程变量中.
     * 
     * @return
     */
    public static TransactionStateInfo geTransactionStateInfo()
    {
        return local.get();
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public boolean isAutoCommit()
    {
        return isAutoCommit;
    }

    public void setAutoCommit(boolean isAutoCommit)
    {
        this.isAutoCommit = isAutoCommit;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * 移除当前线程变量.
     */
    public void clear()
    {
        local.remove();
    }
}

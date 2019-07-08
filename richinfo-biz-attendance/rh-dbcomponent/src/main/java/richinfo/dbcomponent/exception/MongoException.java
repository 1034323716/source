/**
 * 文件名：MongoException.java
 * 创建日期： 2014年8月1日
 * 作者：     tangguanfeng
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2014年8月1日
 *   修改人：tangguanfeng
 *   修改内容：
 */
package richinfo.dbcomponent.exception;

/**
 * 功能描述：Mongo基础错误类
 * 
 */
public class MongoException extends PersistException
{
    private static final long serialVersionUID = -6341444140345043654L;

    public MongoException(String message)
    {
        super(message);
    }

    public MongoException(Exception ex)
    {
        super(ex);
    }

    public MongoException(String message, Exception ex)
    {
        super(message, ex);
    }
}

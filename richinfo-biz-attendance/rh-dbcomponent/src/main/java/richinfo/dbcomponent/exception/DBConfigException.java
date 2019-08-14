/**
 * 文件名：DBConfigException.java
 * 创建日期： 2015年3月6日
 * 作者：     feng
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2015年3月6日
 *   修改人：zhuofeng
 *   修改内容：
 */
package richinfo.dbcomponent.exception;

/**
 * 功能描述：
 * 
 */
public class DBConfigException extends MongoException
{

    /**
     * 
     */
    private static final long serialVersionUID = -3677492075167128666L;

    public DBConfigException(String msg)
    {
        super(msg);
    }

    public DBConfigException(String msg, Exception ex)
    {
        super(msg, ex);
    }
}

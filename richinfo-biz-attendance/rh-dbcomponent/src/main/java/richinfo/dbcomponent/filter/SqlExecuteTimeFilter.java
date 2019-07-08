/**
* 文件名：SqlExecuteTimeFilter.java
* 创建日期： 2012-8-21
* 作者：     zhou gui ping
* Copyright (c) 2009-2011 产品开发一部
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2012-8-21
*   修改人：zhou gui ping
*   修改内容：
*/
package richinfo.dbcomponent.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.dbcomponent.service.PersistService;
import richinfo.dbcomponent.util.DBConfig;


/**
 * 功能描述：sql执行时间过滤器，主要用于记录执行一条sql用了多长时间.
 * 
 * 创建日期： 2012-8-21
 * 作者：           zhou gui ping
 */
public class SqlExecuteTimeFilter implements Filter
{
    
    private static Logger log = LoggerFactory.getLogger(SqlExecuteTimeFilter.class);
    
    /** 时间阈值,默认500毫秒 */
    private long timeThreshold = 500; 
    
    public SqlExecuteTimeFilter(){
        timeThreshold = DBConfig.getInstance().getPropertyInt("sql.time.threshold", 500);
    }
    
    @Override
    public void filter(FilterChain filterChain, PersistService service)
    {
        //执行sql开始时间.
        long startTime = System.currentTimeMillis();
        
        filterChain.doFilter(service);
        
        //执行sql之后的时间.
        long endTime = System.currentTimeMillis();
        
        //sql执行时间超过了时间阈值,就记录日志，日志级别为warn,会把用户的sqlid记录下来，
        //用户执行的方法记录下来,连接池中空闲连接记录下来，服务器cpu记录下来.
        //这些值可以从上下文中获取,也可以从方法参数中获取.
        if((endTime-startTime)>=timeThreshold){
            log.warn("sqlId=|callMethodName=|connectionSize");
        }
    }
}

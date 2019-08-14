/**
* 文件名：FilterChain.java
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

import java.util.ArrayList;
import java.util.List;

import richinfo.dbcomponent.service.PersistService;


/**
 * 功能描述：过滤器链，将所有的过滤器组成一个链状连接起来,每个请求都经过这个过滤器链.
 * 
 * 创建日期： 2012-8-21
 * 作者：           zhou gui ping
 */
public final class FilterChain
{
    
    /** 所有过滤器集合 */
    private static List<Filter> filters = new ArrayList<Filter>();
    
    /** 过滤器索引位置 */
    private int index = 0;
    
    public FilterChain(){
    }
    
    /**
     * 在过滤器链中过滤.
     * 
     * @param service 持久化服务.
     */
    public void doFilter(PersistService service){
        if(index < filters.size()){
            Filter filter = filters.get(index++);
            filter.filter(this, service);
        }else{//后面已经没有过滤器,直接执行业务逻辑.
            //怎么执行业务逻辑
        }
    }
    
    /**
     * 添加一个过滤器到链的末尾,添加过滤器只有启动的时候一个线程添加，线程安全
     * 如果多个线程要添加过滤器，这里就要加锁.
     * 
     * @param filter 过滤器.
     * @return 添加成功返回true.
     */
    public static boolean addFilter(Filter filter){
        return filters.add(filter);
    }
}

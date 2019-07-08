/**
* 文件名：Filter.java
* 创建日期： 2012-8-20
* 作者：     zhou gui ping
* Copyright (c) 2009-2011 产品开发一部
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2012-8-20
*   修改人：zhou gui ping
*   修改内容：
*/
package richinfo.dbcomponent.filter;

import richinfo.dbcomponent.service.PersistService;



/**
 * 功能描述：过滤器,所有请求将通过这个过滤器,然后可以做一些处理，如记录日志，编码处理等.
 * 
 * 创建日期： 2012-8-20
 * 作者：           zhou gui ping
 */
public interface Filter
{
    
    /**
     * 过滤器过滤.
     * 
     * @param filterChain 过滤器链
     * @param service 请求服务.
     * @param 响应参数.
     */
    public void filter(FilterChain filterChain, PersistService service);
}

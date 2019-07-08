/**
* 文件名：FilterManager.java
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
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 功能描述：过滤器管理类，加载所有过滤器,然后添加到过滤器链中.
 * 
 * 创建日期： 2012-8-21
 * 作者：           zhou gui ping
 */
public class FilterManager
{

    private static volatile FilterManager instance = null;
    
    /** 可重入的读写锁 */
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    /** 过滤器集合 */
    private List<Filter> filters = new ArrayList<Filter>();
    
    private FilterManager(){
    }

    public static FilterManager getInstance(){
        if(instance ==null){
            synchronized (FilterManager.class)
            {
                if(instance ==null){
                    instance = new FilterManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * 获取过滤器数量.
     * 
     * @return 过滤器数量 
     */
    public int size(){
        lock.readLock().lock();
        try{
            return filters.size();
        }finally{
            lock.readLock().unlock();
        }
    }
    
    /**
     * 根据下标获取一个过滤器,下标超出当前的过滤器数量将返回null.
     * 
     * @param index 过滤器下标.
     * @return 返回过滤器.
     */
    public Filter getFilter(int index){
        Filter filter = null;
        lock.readLock().lock();
        try{
            if(index>=0 && index < size()){
                filter = filters.get(index);    
            }
            return filter;
        }finally{
            lock.readLock().unlock();
        }
    }
    
    /**
     * 删除一个过滤器.
     * 
     * @param filter 过滤器对象.
     */
    public void remove(Filter filter){
        lock.writeLock().lock();
        try{
            filters.remove(filter);
        }finally{
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 添加一个过滤器.
     * 
     * @param filter 过滤器.
     */
    public void addFilter(Filter filter){
        lock.writeLock().lock();
        try{
            filters.add(filter);
        }finally{
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 清除所有过滤器
     */
    public void clearAll(){
        lock.writeLock().lock();
        try{
            filters.clear();
        }finally{
            lock.writeLock().unlock();
        }
    }
}

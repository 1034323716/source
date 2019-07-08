/**
* 文件名：State.java
* 创建日期： 2012-9-11
* 作者：     zhou gui ping
* Copyright (c) 2009-2011 产品开发一部
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2012-9-11
*   修改人：zhou gui ping
*   修改内容：
*/
package richinfo.dbcomponent.service.impl;

/**
 * 功能描述：事务状态枚举类，提供事务的3种状态,和ibatis中的事务一一对应.
 * 
 * 创建日期： 2012-9-11
 * 作者：           zhou gui ping
 */
public enum State {
    
    /**
     * 开始事务
     */
    start,
    
    /**
     * 提交事务
     */
    commit,
    
    /**
     * 结束事务
     */
    end
}

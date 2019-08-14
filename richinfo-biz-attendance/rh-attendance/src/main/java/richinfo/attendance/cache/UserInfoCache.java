/**
* 文件名：UserInfoCache.java
* 创建日期： 2016年2月20日
* 作者：     liyongde
* Copyright (c) 2009-2011 个邮开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2016年2月20日
*   修改人：liyongde
*   修改内容：
*/
package richinfo.attendance.cache;

import richinfo.attendance.entity.UserInfo;
import richinfo.bcomponet.cache.comm.CacheKey;

/**
 * 功能描述：
 * 用户（会话）缓存对象类
 */
public class UserInfoCache extends BaseCache<UserInfo>
{
    private static UserInfoCache instance = new UserInfoCache();

    private UserInfoCache()
    {
        //TODO 需新定义KEY
        super(CacheKey.Attendance.ATTENDANCE_USER_INFO);
    }

    public static UserInfoCache getInstance()
    {
        return instance;
    }

}

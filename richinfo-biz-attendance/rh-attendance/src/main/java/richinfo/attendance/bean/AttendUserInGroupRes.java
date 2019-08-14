/**
* 文件名：AttendUserInGroupRes.java
* 创建日期： 2017年6月12日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年6月12日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.bean;

import richinfo.attendance.entity.UserGroupEntity;
import richinfo.attendance.common.ResBean;

/**
 * 功能描述：
 * 用户所属考勤组返回信息
 */
public class AttendUserInGroupRes extends ResBean
{
    private static final long serialVersionUID = 7650342041618643138L;
    
    /**
     * 用户和考勤组关系对象
     */
    private UserGroupEntity userGroup;

    private String allowOutRangeClock;

    private String charge;

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getAllowOutRangeClock() {
        return allowOutRangeClock;
    }

    public void setAllowOutRangeClock(String allowOutRangeClock) {
        this.allowOutRangeClock = allowOutRangeClock;
    }

    public UserGroupEntity getUserGroup()
    {
        return userGroup;
    }

    public void setUserGroup(UserGroupEntity userGroup)
    {
        this.userGroup = userGroup;
    }

    @Override
    public String toString() {
        return "UserGroupEntity:" +userGroup;
    }
    
}

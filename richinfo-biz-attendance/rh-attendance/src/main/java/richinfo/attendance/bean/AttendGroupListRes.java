/**
* 文件名：AttendGroupListRes.java
* 创建日期： 2017年6月9日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年6月9日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.bean;

import java.util.List;

import richinfo.attendance.common.ResBean;

/**
 * 功能描述：
 * 考勤组列表返回bean
 */
public class AttendGroupListRes extends ResBean
{

    private static final long serialVersionUID = 8670655588021067983L;
    /**
     * 考勤组列表数据
     */
    private List<AttendGroupWithEmpRes> data;
    
    public List<AttendGroupWithEmpRes> getData()
    {
        return data;
    }
    public void setData(List<AttendGroupWithEmpRes> data)
    {
        this.data = data;
    }
    
}

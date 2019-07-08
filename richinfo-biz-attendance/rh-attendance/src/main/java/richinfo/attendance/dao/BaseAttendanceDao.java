/**
 * 文件名：BaseAttendanceDao.java
 * 创建日期： 2017年6月5日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.dao;

import richinfo.attendance.common.DaoObject;
import richinfo.dbcomponent.service.PersistClientBuilder;
import richinfo.dbcomponent.service.PersistService;

/**
 * 功能描述：数据库连接信息
 * 
 */
public class BaseAttendanceDao extends DaoObject
{
    /** 数据源信息 */
    private String ATTENDANCE_DATASOURCE = "proxool.attendance";

    protected PersistService attendanceDao = PersistClientBuilder
        .createPersistClient(ATTENDANCE_DATASOURCE);

}

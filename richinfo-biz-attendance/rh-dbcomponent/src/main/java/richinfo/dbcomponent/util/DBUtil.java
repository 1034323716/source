/**
 * 文件名：DBUtil.java
 * 创建日期： 2012-8-20
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-8-20
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 功能描述：主要提供关闭Connection,ResultSet,Statement对象的方法.
 * 
 * 创建日期： 2012-8-20 作者： zhou gui ping
 */
public final class DBUtil
{

    /**
     * 关闭数据库链接.
     * 
     * @param conn 数据库链接.
     */
    public static void close(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                conn.close();
            }
            catch (SQLException e)
            {
                
            }
        }
    }

    /**
     * 关闭结果集.
     * 
     * @param rs 结果集.
     */
    public static void close(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
                
            }
        }
    }

    /**
     * 关闭Statement.
     * 
     * @param stmt statement对象.
     */
    public static void close(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                
            }
        }
    }

    /**
     * 关闭多有对象.
     * 
     * @param conn 数据库连接.
     * @param stmt Statement对象.
     * @param rs 结果集.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs)
    {
        close(rs);
        close(stmt);
        close(conn);
    }

}

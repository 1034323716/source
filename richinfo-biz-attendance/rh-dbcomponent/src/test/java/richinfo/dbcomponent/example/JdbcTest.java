/**
 * 
 */
package richinfo.dbcomponent.example;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import richinfo.dbcomponent.service.JdbcConnectionAdapt;
import richinfo.dbcomponent.util.DBConstants;
import richinfo.dbcomponent.util.DBUtil;


/**
 * 
 * jdbc客户端调用举例.
 * @author zhou gui ping
 *
 */
public class JdbcTest {

	/**
	 * 客户端通过jdbc方式获取连接，用完需要手动关闭连接。
	 * 
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
		    //获取连接，如果不传入参数，将是按照默认的别名获取连接，即:proxool.oramail
			conn = JdbcConnectionAdapt.getConnection();
			//conn = JdbcConnectionAdapt.getConnection(DBConstants.ORAMAIL_DATASOURCE);
			//conn = JdbcConnectionAdapt.getConnection(DBConstants.ORAPUT_DATASOURCE);
			
			pstmt = conn.prepareStatement("select * from mail_card_material");
			rs = pstmt.executeQuery();
			String id = null;
			if(rs.next()){
				id = rs.getString(1);
			}
		}catch(Exception e){
		    
		}finally{
			//关闭连接.
			DBUtil.close(conn,pstmt,rs);
		}
	}
	

}

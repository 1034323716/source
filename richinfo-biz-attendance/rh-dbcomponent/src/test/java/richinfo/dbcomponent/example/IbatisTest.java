/**
 * 
 */
package richinfo.dbcomponent.example;

import java.util.List;

import richinfo.dbcomponent.exception.PersistException;
import richinfo.dbcomponent.service.PersistClientBuilder;
import richinfo.dbcomponent.service.PersistService;
import richinfo.dbcomponent.service.impl.StartUp;
import richinfo.dbcomponent.util.DBConstants;


/**
 * @author Administrator
 *
 */
public class IbatisTest {

	/**
	 * @param args
	 * @throws PersistException 
	 */
	public static void main(String[] args) throws PersistException {
		
		//第一步，根据别名获取PersisClient对象.
		PersistService oraMail = PersistClientBuilder.createPersistClient("");
		
		PersistService orapub = PersistClientBuilder.createPersistClient("");
		
		//第二步，调用执行sql方法。
		List list = oraMail.queryForList("queryAllUser", null);
		
		//同一个数据源的oraMail可以执行多个操作
		int result = oraMail.delete("deleteAllUser", null);
		
		System.out.println(result>0?"删除成功":"删除失败");
		
		//第二个数据源的操作
		result = orapub.delete("deleteAllUser", null);
		System.out.println(result>0?"删除成功":"删除失败");
	}

}

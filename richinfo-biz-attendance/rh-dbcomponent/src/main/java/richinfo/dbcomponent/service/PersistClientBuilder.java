package richinfo.dbcomponent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 功能描述：持久化客户端构建器.
 * 
 * 创建日期： 2012-9-3 作者： zhou gui ping
 * 
 */
public class PersistClientBuilder
{
    private static Logger logger = LoggerFactory
        .getLogger(PersistClientBuilder.class);

    /**
     * 根据数据源返回一个持久化代理类,数据源名称是在proxool.xml文件中配置的 一个数据源名称对应一个数据源.
     * 
     * @param alias 数据源别名,如proxool.orapub等
     * @return PersistService
     */
    public static PersistService createPersistClient(String alias)
    {
        PersistService service = null;
        try
        {
            service = new PersistServiceProxy(alias);
        }
        catch (Exception e)
        {
            logger.error("Failed new Instance PersistProxy.", e);
        }
        return service;
    }
}

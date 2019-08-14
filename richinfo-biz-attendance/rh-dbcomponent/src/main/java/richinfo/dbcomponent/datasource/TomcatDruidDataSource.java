package richinfo.dbcomponent.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.dbcomponent.bean.DataSourceInfo;
import richinfo.tools.common.AssertUtil;
import richinfo.tools.common.ConverUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by qiang on 2018/11/16.
 */
public class TomcatDruidDataSource extends DruidDataSource {

    private static Logger logger = LoggerFactory.getLogger(TomcatDataSource.class);

    /** 缓存数据源信息,key为数据源别名,value为数据源. */
   // private static ConcurrentMap<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();
    private static ConcurrentMap<String, DruidDataSource> druidDataSourceMap = new ConcurrentHashMap<String, DruidDataSource>();

    /** 可重入锁 */
    private ReentrantLock lock = new ReentrantLock();

    /** 当前实例名称 */
    private static volatile TomcatDruidDataSource dataSource;

    private TomcatDruidDataSource() {
    }

    public static TomcatDruidDataSource getInstance() {
        if (dataSource == null) {
            synchronized (TomcatDataSource.class) {
                if (dataSource == null) {
                    dataSource = new TomcatDruidDataSource();
                }
            }
        }
        return dataSource;
    }


    /**
     * tomcat连接池初始化 构建druid数据源
     * @param sourceInfomap
     */
    public void createDruidDataSource(ConcurrentMap<String, DataSourceInfo> sourceInfomap) {
        String alias = "";// 数据源别名.
        DataSourceInfo source = new DataSourceInfo();

        try {
            lock.lock();
            if (AssertUtil.isEmpty(druidDataSourceMap)) {
                DruidDataSource sourceImpl = null;
                for (Map.Entry<String, DataSourceInfo> entry : sourceInfomap.entrySet()) {
                    alias = entry.getKey();
                    source = entry.getValue();
                    logger.info("tomcat init datasource alias={}", alias);
                    if (!AssertUtil.isEmpty(alias) && !AssertUtil.isEmpty(source)) {
                        sourceImpl = getDruidDataSource(alias, source);
                        druidDataSourceMap.put(alias, sourceImpl);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed init tomcat dataSourceMap.size={}", druidDataSourceMap.size(), e);
        } finally {
            lock.unlock();
        }
    }

    /*创建阿里巴巴druid 数据源*/
    private DruidDataSource getDruidDataSource(String alias, DataSourceInfo source) {
        DruidDataSource ds = new DruidDataSource();
        // 数据库连接URL
        ds.setUrl(source.getDriverUrl());
        // 数据库连接驱动
        ds.setDriverClassName(source.getDriverClass());
        // 用户名
        ds.setUsername(source.getUser());
        // 密码
        ds.setPassword(source.getPassword());
        // 是否将连接池注册到JMX
        // ds.setJmxEnabled(ConverUtil.string2Boolean(source.getJmx()));
        // 指明连接是否被空闲连接回收器( 如果有) 进行检验. 如果检测失败, 则连接将被从池中去除
        ds.setTestWhileIdle(source.isTestWhileIdle());
        // 指明是否在从池中取出连接前进行检验, 如果检验失败, 则从池中去除连接并尝试取出另一个
        ds.setTestOnBorrow(source.isTestOnBorrow());
        // 测试数据库连接
        ds.setValidationQuery(source.getTestSql());
        // 指明是否在归还到池中前进行检验
        ds.setTestOnReturn(source.isTestOnReturn());
        // 避免过度验证，保证验证不超过这个频率，验证线程睡眠时间
        // ds.setValidationInterval(ConverUtil.string2Long(source.getSleepTime(),30000));
        // 在空闲连接回收器线程运行期间休眠的时间值
        ds.setTimeBetweenEvictionRunsMillis(ConverUtil.string2Int(source.getTimeBetweenEvictionRunsMillis(), 30000));
        // 连接池的最大数据库连接数。设为0表示无限制
        ds.setMaxActive(ConverUtil.string2Int(source.getMaximum(), 30));
        // 初始化连接:连接池启动时创建的初始化连接数量
        ds.setInitialSize(ConverUtil.string2Int(source.getMinimum(), 10));
        // 最大建立连接等待时间。如果超过此时间将接到异常。设为-1表示无限制
        ds.setMaxWait(ConverUtil.string2Int(source.getMaxWait(), 10000));
        // 连接在池中保持空闲而不被空闲连接回收器线程(如果有) 回收的最小时间值，单位毫秒
        ds.setMinEvictableIdleTimeMillis(ConverUtil.string2Int(source.getMinEvictableIdleTimeMillis(), 30000));
        // 最小空闲连接:连接池中容许保持空闲状态的最小连接数量,低于这个数量将创建新的连接
        ds.setMinIdle(ConverUtil.string2Int(source.getMinimum(), 10));
        // 最大空闲连接:连接池中容许保持空闲状态的最大连接数量
        ds.setMaxIdle(ConverUtil.string2Int(source.getMaximum(), 30));
        // 标记当Statement 或连接被泄露时是否打印程序的stack traces 日志
        ds.setLogAbandoned(source.isLogAbandoned());
        // 超过removeAbandonedTimeout时间后，是否进行没用连接（废弃）的回收（默认为false，调整为true)
        ds.setRemoveAbandoned(source.isRemoveAbandoned());
        // 超过时间限制，回收没有用(废弃)的连接（默认为 300秒，调整为180）
        ds.setRemoveAbandonedTimeout(ConverUtil.string2Int(source.getRemoveAbandonedTimeout(), 180));
        // 预制的拦截器
        // ConnectionState - 追踪自动提交、只读状态、catalog和事务隔离等级等状态
        // StatementFinalizer - 追踪打开的statement，当连接被归还时关闭它们
        // p.setJdbcInterceptors(source.getJdbcInterceptors());
        ds.setPoolPreparedStatements(true);
        ds.setMaxPoolPreparedStatementPerConnectionSize(50);
        return ds;
    }

    public DruidDataSource getDataSource(String alias)
    {
        return druidDataSourceMap.get(alias);
    }
}

/**
 * 文件名：DBConstants.java
 * 创建日期： 2012-9-13
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-9-13
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.util;

/**
 * 功能描述：数据库组件常量类，如常用的数据源别名之类的常量
 * 
 * 创建日期： 2012-9-13 作者： zhou gui ping
 */
public interface DBConstants
{
    /**
     * 功能描述：数据源配置key
     */
    public interface ConfigKey
    {

        /** 配置文件中的driver_url节点名称 */
        public static final String DRIVER_URL = "driver-url";

        /** 配置文件中的driver_class节点名称 */
        public static final String DRIVER_CLASS = "driver-class";

        /** 配置文件中的driver_properties节点名称 */
        public static final String DRIVER_PROPERTIES = "driver-properties";

        /** 配置文件中的alias节点名称 */
        public static final String ALIAS_NODE = "alias";

        /** 配置文件中的用户名节点名称 */
        public static final String USER_NODE = "user";

        /** 配置文件中的密码节点名称 */
        public static final String PASSWORD_NODE = "password";

        /** 配置文件中的house-keeping-sleep-time节点名称 */
        public static final String HOUSE_KEEPING_SLEEP_TIME = "house-keeping-sleep-time";

        /** 配置文件中的maximum-connection-count节点名称 */
        public static final String MAXIMUM_CONNECTION_COUNT = "maximum-connection-count";

        /** 配置文件中的minimum-connection-count节点名称 */
        public static final String MINIMUM_CONNECTION_COUNT = "minimum-connection-count";

        /** 配置文件中的maximum-active-time节点名称 */
        public static final String MAXIMUM_ACTIVE_TIME = "maximum-active-time";

        /** 配置文件中的maximum-connection-lifetime节点名称 */
        public static final String MAXIMUM_CONNECTION_LIFETIME = "maximum-connection-lifetime";
        
        /** 配置文件中的prototype-counte节点名称 */
        public static final String PROTOTYPE_COUNT = "prototype-count";

        /** 配置文件中的simultaneous-build-throttle节点名称 */
        public static final String SIMULTANEOUS_BUILD_THROTTLE = "simultaneous-build-throttle";

        /** 配置文件中的recently-started-threshold节点名称 */
        public static final String RECENTLY_STARTED_THRESHOLD = "recently-started-threshold";

        /** 配置文件中的statistics-log-level节点名称 */
        public static final String STATISTICS_LOG_LEVEL = "statistics-log-level";

        /** 配置文件中的house-keeping-test-sql节点名称 */
        public static final String HOUSE_KEEPING_TEST_SQL = "house-keeping-test-sql";

        /** 配置文件中的jmx节点名称 */
        public static final String JMX = "jmx";

        /** 配置文件中的verbose节点名称 */
        public static final String VERBOSE = "verbose";

        /** 配置文件中的trace节点名称 */
        public static final String TRACE = "trace";

        /** 配置文件中的overload-without-refusal-lifetime节点名称 */
        public static final String OVERLOAD_WITHOUT_REFUSAL_LIFETIME = "overload-without-refusal-lifetime";
        
        /** 配置文件中的testWhileIdle节点名称 */
        public static final String TEST_WHILE_IDLE = "testWhileIdle";
        
        /** 配置文件中的testOnBorrow节点名称 */
        public static final String TEST_ON_BORROW = "testOnBorrow";
        
        /** 配置文件中的testOnReturn节点名称 */
        public static final String TEST_ON_RETURN = "testOnReturn";
        
        /** 配置文件中的timeBetweenEvictionRunsMillis节点名称 */
        public static final String TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
        
        /** 配置文件中的maxWait节点名称 */
        public static final String MAX_WAIT = "maxWait";
        
        /** 配置文件中的minEvictableIdleTimeMillis节点名称 */
        public static final String MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
        
        /** 配置文件中的removeAbandoned节点名称 */
        public static final String REMOVE_ABANDONED = "removeAbandoned";
        
        /** 配置文件中的removeAbandonedTimeout节点名称 */
        public static final String REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
        
        /** 配置文件中的logAbandoned节点名称 */
        public static final String LOG_ABANDONED = "logAbandoned";
        
        /** 配置文件中的jdbcInterceptors节点名称 */
        public static final String JDBC_INTERCEPTORS = "jdbcInterceptors";

        /** 别名的前缀,必须要带上. */
        public static final String ALIAS_PREFIX = "proxool.";

    }

}

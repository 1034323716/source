/**
 * 文件名：DatasourceInfo.java
 * 创建日期： 2012-8-30
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-8-30
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：数据源常用配置信息.
 * 
 * 创建日期： 2012-8-30 作者： zhou gui ping
 */
public class DataSourceInfo {
    /** 数据源别名 */
    private String alias;
    /** 驱动类 */
    private String driverClass;
    /** 访问数据库url */
    private String driverUrl;
    /** 数据库用户名 */
    private String user;
    /** 数据库密码 */
    private String password;
    /** 扩展属性 */
    private Map<String, Object> properties = new HashMap<String, Object>();
    /** 验证线程睡眠时间 */
    private String sleepTime;
    /** 最大数据库连接数 */
    private String maximum;
    /** 最小初始化连接数 */
    private String minimum;
    /** 最大存活时间 proxool */
    private String activeTime;
    /** 一个线程的最大寿命 proxool */
    private String lifeTime;
    /** 一次最多新增连接数 单总数需低于maximum */
    private String buildThrottle;
    /** 空闲（可用的）连接数 proxool */
    private String prototypeCount;
    /** 最近连接阀值 proxool */
    private String threshold;
    /** 日志级别 proxool */
    private String logLevel;
    /** 连接测试语句 */
    private String testSql;
    /** 是否注册JMX管理 */
    private String jmx;
    /** 详细信息设置 proxool */
    private String verbose;
    /** 是否记录执行中的sql日志 proxool */
    private String trace;
    /** 过载时间阀值 proxool */
    private String overloadTime;
    /** 指明连接是否被空闲连接回收器( 如果有) 进行检验. 如果检测失败, 则连接将被从池中去除 tomcat */
    private boolean testWhileIdle = true;
    /** 指明是否在从池中取出连接前进行检验, 如果检验失败, 则从池中去除连接并尝试取出另一个 tomcat */
    private boolean testOnBorrow = true;
    /** 指明是否在归还到池中前进行检验 tomcat */
    private boolean testOnReturn = true;
    /** 在空闲连接回收器线程运行期间休眠的时间值 tomcat */
    private String timeBetweenEvictionRunsMillis;
    /** 最大建立连接等待时间。如果超过此时间将接到异常。设为-1表示无限制 tomcat */
    private String maxWait;
    /** 连接在池中保持空闲而不被空闲连接回收器线程(如果有) 回收的最小时间值，单位毫秒 tomcat */
    private String minEvictableIdleTimeMillis;
    /** 超过removeAbandonedTimeout时间后，是否进行没用连接（废弃）的回收 tomcat */
    private boolean removeAbandoned = true;
    /** 超过时间限制，回收没有用(废弃)的连接 tomcat */
    private String removeAbandonedTimeout;
    /** 标记当Statement 或连接被泄露时是否打印程序的stack traces 日志 tomcat */
    private boolean logAbandoned = true;
    /**
     * 预制的拦截器
     * ConnectionState - 追踪自动提交、只读状态、catalog和事务隔离等级等状态 StatementFinalizer -
     * 追踪打开的statement，当连接被归还时关闭它们 tomcat
     */
    private String jdbcInterceptors = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

    /** 数据库访问IP mongodb使用 */
    private String ip;

    /** 数据库访问端口 mongodb使用 */
    private int port;

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public String getDriverClass()
    {
        return driverClass;
    }

    public void setDriverClass(String driverClass)
    {
        this.driverClass = driverClass;
    }

    public String getDriverUrl()
    {
        return driverUrl;
    }

    public void setDriverUrl(String driverUrl)
    {
        this.driverUrl = driverUrl;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSleepTime()
    {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime)
    {
        this.sleepTime = sleepTime;
    }

    public String getMaximum()
    {
        return maximum;
    }

    public void setMaximum(String maximum)
    {
        this.maximum = maximum;
    }

    public String getMinimum()
    {
        return minimum;
    }

    public void setMinimum(String minimum)
    {
        this.minimum = minimum;
    }

    public String getActiveTime()
    {
        return activeTime;
    }

    public void setActiveTime(String activeTime)
    {
        this.activeTime = activeTime;
    }

    public String getLifeTime()
    {
        return lifeTime;
    }

    public void setLifeTime(String lifeTime)
    {
        this.lifeTime = lifeTime;
    }

    public String getBuildThrottle()
    {
        return buildThrottle;
    }

    public void setBuildThrottle(String buildThrottle)
    {
        this.buildThrottle = buildThrottle;
    }

    public String getPrototypeCount()
    {
        return prototypeCount;
    }

    public void setPrototypeCount(String prototypeCount)
    {
        this.prototypeCount = prototypeCount;
    }

    public String getThreshold()
    {
        return threshold;
    }

    public void setThreshold(String threshold)
    {
        this.threshold = threshold;
    }

    public String getLogLevel()
    {
        return logLevel;
    }

    public void setLogLevel(String logLevel)
    {
        this.logLevel = logLevel;
    }

    public String getTestSql()
    {
        return testSql;
    }

    public void setTestSql(String testSql)
    {
        this.testSql = testSql;
    }

    public String getJmx()
    {
        return jmx;
    }

    public void setJmx(String jmx)
    {
        this.jmx = jmx;
    }

    public String getVerbose()
    {
        return verbose;
    }

    public void setVerbose(String verbose)
    {
        this.verbose = verbose;
    }

    public String getTrace()
    {
        return trace;
    }

    public void setTrace(String trace)
    {
        this.trace = trace;
    }

    public String getOverloadTime()
    {
        return overloadTime;
    }

    public void setOverloadTime(String overloadTime)
    {
        this.overloadTime = overloadTime;
    }

    public boolean isTestWhileIdle()
    {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle)
    {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow()
    {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow)
    {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn()
    {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn)
    {
        this.testOnReturn = testOnReturn;
    }

    public String getTimeBetweenEvictionRunsMillis()
    {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(String timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public String getMaxWait()
    {
        return maxWait;
    }

    public void setMaxWait(String maxWait)
    {
        this.maxWait = maxWait;
    }

    public String getMinEvictableIdleTimeMillis()
    {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(String minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public boolean isRemoveAbandoned()
    {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned)
    {
        this.removeAbandoned = removeAbandoned;
    }

    public String getRemoveAbandonedTimeout()
    {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(String removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public boolean isLogAbandoned()
    {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned)
    {
        this.logAbandoned = logAbandoned;
    }

    public String getJdbcInterceptors()
    {
        return jdbcInterceptors;
    }

    public void setJdbcInterceptors(String jdbcInterceptors)
    {
        this.jdbcInterceptors = jdbcInterceptors;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    @Override
    public String toString() {
        return "DataSourceInfo [alias=" + alias + ", driverClass="
            + driverClass + ", driverUrl=" + driverUrl + ", user=" + user
            + ", password=" + password + ", properties=" + properties + ", ip="
            + ip + ", port=" + port + "]";
    }

}

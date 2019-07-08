/**
 * 文件名：DataBaseConfigParse.java
 * 创建日期： 2012-9-3
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-9-3
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.dbcomponent.bean.DataSourceInfo;
import richinfo.tools.common.AssertUtil;
import richinfo.tools.common.ConverUtil;
import richinfo.tools.io.StreamUtil;
import richinfo.tools.io.XmlParserUtil;

/**
 * 功能描述：数据库配置信息解析类.
 * 
 * 创建日期： 2012-9-3 作者： zhou gui ping
 */
public class DBConfigParse
{

    private static Logger log = LoggerFactory.getLogger(DBConfigParse.class);

//    /** 配置文件中的driver_url节点名称 */
//    private static final String DRIVER_URL = "driver-url";
//
//    /** 配置文件中的driver_class节点名称 */
//    private static final String DRIVER_CLASS = "driver-class";
//
//    /** 配置文件中的driver_properties节点名称 */
//    private static final String DRIVER_PROPERTIES = "driver-properties";
//
//    /** 配置文件中的alias节点名称 */
//    private static final String ALIAS_NODE = "alias";
//
//    /** 配置文件中的用户名节点名称 */
//    private static final String USER_NODE = "user";
//
//    /** 配置文件中的密码节点名称 */
//    private static final String PASSWORD_NODE = "password";
//
//    /** 别名的前缀,必须要带上. */
//    private static final String ALIAS_PREFIX = "proxool.";

    /**
     * 解析xml文件，并将内容转换成DataSourceInfo对象.
     * 
     * @param fileName 文件名.
     * @return map对象.
     */
    public ConcurrentMap<String, DataSourceInfo> parseXml2Object(String fileName)
    {

        ConcurrentMap<String, DataSourceInfo> sourceInfo = new ConcurrentHashMap<String, DataSourceInfo>();
        try
        {
            Document document = getDocument(fileName);
            Element root = document.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> proxools = root.elements();
            DataSourceInfo info = null;
            for (Element e : proxools)
            {
                info = getDataSourceInfo(e);
                sourceInfo.put(info.getAlias(), info);
            }
        }
        catch (Exception e)
        {
            log.error("Failed load datasource config.", e);
        }
        return sourceInfo;
    }

    /**
     * 解析赋值DataSourceInfo
     * @param e
     * @return
     */
    private DataSourceInfo getDataSourceInfo(Element e)
    {
        String value = "";
        DataSourceInfo info = new DataSourceInfo();
        
        value = getNodeString(e, DBConstants.ConfigKey.ALIAS_NODE);
        info.setAlias(DBConstants.ConfigKey.ALIAS_PREFIX + value);
        
        value = getNodeString(e, DBConstants.ConfigKey.DRIVER_URL);
        info.setDriverUrl(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.DRIVER_CLASS);
        info.setDriverClass(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.HOUSE_KEEPING_SLEEP_TIME);
        info.setSleepTime(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.MAXIMUM_CONNECTION_COUNT);
        info.setMaximum(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.MINIMUM_CONNECTION_COUNT);
        info.setMinimum(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.MAXIMUM_ACTIVE_TIME);
        info.setActiveTime(value);
        
        value = getNodeString(e,
            DBConstants.ConfigKey.MAXIMUM_CONNECTION_LIFETIME);
        info.setLifeTime(value);
        
        value = getNodeString(e,
            DBConstants.ConfigKey.SIMULTANEOUS_BUILD_THROTTLE);
        info.setBuildThrottle(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.PROTOTYPE_COUNT);
        info.setPrototypeCount(value);
        
        value = getNodeString(e,
            DBConstants.ConfigKey.RECENTLY_STARTED_THRESHOLD);
        info.setThreshold(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.STATISTICS_LOG_LEVEL);
        info.setLogLevel(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.HOUSE_KEEPING_TEST_SQL);
        info.setTestSql(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.JMX);
        info.setJmx(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.VERBOSE);
        info.setVerbose(value);
        
        value = getNodeString(e, DBConstants.ConfigKey.TRACE);
        info.setTrace(value);
        
        value = getNodeString(e,
            DBConstants.ConfigKey.OVERLOAD_WITHOUT_REFUSAL_LIFETIME);
        info.setOverloadTime(value);
        
        value = getNodeString(e,
            DBConstants.ConfigKey.TEST_WHILE_IDLE);
        if(AssertUtil.isNotEmpty(value)){
            info.setTestWhileIdle(ConverUtil.string2Boolean(value));
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.TEST_ON_BORROW);
        if(AssertUtil.isNotEmpty(value)){
            info.setTestOnBorrow(ConverUtil.string2Boolean(value));
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.TEST_ON_RETURN);
        if(AssertUtil.isNotEmpty(value)){
            info.setTestOnReturn(ConverUtil.string2Boolean(value));
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.TIME_BETWEEN_EVICTION_RUNS_MILLIS);
        if(AssertUtil.isNotEmpty(value)){
            info.setTimeBetweenEvictionRunsMillis(value);
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.MAX_WAIT);
        if(AssertUtil.isNotEmpty(value)){
            info.setMaxWait(value);
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.MIN_EVICTABLE_IDLE_TIME_MILLIS);
        if(AssertUtil.isNotEmpty(value)){
            info.setMinEvictableIdleTimeMillis(value);
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.REMOVE_ABANDONED);
        if(AssertUtil.isNotEmpty(value)){
            info.setRemoveAbandoned(ConverUtil.string2Boolean(value));
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.REMOVE_ABANDONED_TIMEOUT);
        if(AssertUtil.isNotEmpty(value)){
            info.setRemoveAbandonedTimeout(value);
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.LOG_ABANDONED);
        if(AssertUtil.isNotEmpty(value)){
            info.setLogAbandoned(ConverUtil.string2Boolean(value));
        }
        
        value = getNodeString(e,
            DBConstants.ConfigKey.JDBC_INTERCEPTORS);
        if(AssertUtil.isNotEmpty(value)){
            info.setJdbcInterceptors(value);
        }
       
        getNodeProperties(e, DBConstants.ConfigKey.DRIVER_PROPERTIES, info);
        
        return info;
    }

    /**
     * 根据指定的文件名加载xml格式文件并转换成document对象.
     * 
     * @param fileName 文件名.
     * @return doc对象
     */
    private Document getDocument(String fileName)
    {
        InputStream in = null;
        Document doc = null;
        try
        {
            File file = new File(fileName);
            in = new FileInputStream(file);
            doc = XmlParserUtil.readDatabyStream(in);
        }
        catch (Exception e)
        {
            log.error("Failed load config file from path,fileName=" + fileName);
            try
            {
                in = Resource.getResourceAsStream(fileName);
                doc = XmlParserUtil.readDatabyStream(in);
            }
            catch (IOException e1)
            {
                log.error("Failed load config file from classpath,filename="
                    + fileName, e1);
            }
        }
        finally
        {
            StreamUtil.close(in);
        }
        return doc;
    }

    /**
     * 
     * 加载指定properties文件并生成Reader对象.
     * 
     * @param fileName 文件名.
     * @return reader对象.
     */
    private Reader getReader(String fileName)
    {
        InputStream in = null;
        Reader reader = null;
        try
        {
            File file = new File(fileName);
            in = new FileInputStream(file);
            reader = new InputStreamReader(in);
        }
        catch (Exception e)
        {
            log.error("Failed load config file from path,fileName=" + fileName);
            try
            {
                reader = Resource.getResourceAsReader(fileName);
            }
            catch (IOException e1)
            {
                log.error("Failed load config file from classpath,filename="
                    + fileName, e1);
            }
        }
        finally
        {
            StreamUtil.close(reader);
        }

        return reader;
    }

    /**
     * 解析properties文件,并将内容生成DataSourceInfo对象.
     * 
     * @param fileName 文件名.
     * @return map对象.
     */
    public Map<String, DataSourceInfo> parse2Object(String fileName)
    {
        // 数据源信息
        Map<String, DataSourceInfo> dataSourceInfo = new HashMap<String, DataSourceInfo>();
        Reader reader = null;
        try
        {
            reader = getReader(fileName);
            BufferedReader buff = new BufferedReader(reader);
            String ss = null;
            DataSourceInfo info = null;
            String[] temp = null;
            String configValue = "";
            while ((ss = buff.readLine()) != null)
            {
                temp = ss.split("=");
                if (temp != null && temp.length > 2)
                {// 有多个等号
                    configValue = ss.substring(ss.indexOf("=") + 1);
                }
                else if (temp.length == 2)
                {
                    configValue = temp[1];
                }
                if (DBConstants.ConfigKey.ALIAS_NODE.equalsIgnoreCase(temp[0]))
                {
                    info = new DataSourceInfo();
                    info.setAlias(DBConstants.ConfigKey.ALIAS_PREFIX + configValue);
                }
                else if (DBConstants.ConfigKey.DRIVER_CLASS.equalsIgnoreCase(temp[0]))
                {
                    info.setDriverClass(configValue);
                }
                else if (DBConstants.ConfigKey.DRIVER_URL.equalsIgnoreCase(temp[0]))
                {
                    info.setDriverUrl(configValue);
                }
                else if (DBConstants.ConfigKey.USER_NODE.equalsIgnoreCase(temp[0]))
                {
                    info.setUser(configValue);
                }
                else if (DBConstants.ConfigKey.PASSWORD_NODE.equalsIgnoreCase(temp[0]))
                {
                    info.setPassword(configValue);
                    dataSourceInfo.put(info.getAlias(), info);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Failed load datasource config.", e);
        }
        finally
        {
            StreamUtil.close(reader);
        }
        return dataSourceInfo;
    }

    /**
     * 解析用户名密码节点属性,将用户名密码的内容存放到DataSourceInfo对象中.
     * 
     * @param e 待解析的元素.
     * @param nodeName 节点名称.
     * @param info 实体信息.
     */
    private void getNodeProperties(Element e, String nodeName,
        DataSourceInfo info)
    {
        String value = "";
        Element child = e.element(nodeName);
        if (child != null)
        {
            @SuppressWarnings("unchecked")
            List<Element> lists = child.elements();
            for (Element ee : lists)
            {
                value = ee.attributeValue("name");
                if (DBConstants.ConfigKey.USER_NODE.equalsIgnoreCase(value))
                {
                    value = ee.attributeValue("value");
                    info.setUser(value);
                }
                if (DBConstants.ConfigKey.PASSWORD_NODE.equalsIgnoreCase(value))
                {
                    value = ee.attributeValue("value");
                    info.setPassword(value);
                }
            }
        }
    }

    /**
     * 解析指定的节点,获取节点内容.
     * 
     * @param e 待解析的元素.
     * @param nodeName 节点名称.
     * @return 节点内容.
     */
    private String getNodeString(Element e, String nodeName)
    {
        String value = "";
        Element child = e.element(nodeName);
        if (child != null)
        {
            value = child.getTextTrim();
        }
        return value;
    }
    //
    // /**
    // * 解析指定的节点,获取节点内容.
    // *
    // * @param e 待解析的元素.
    // * @param nodeName 节点名称.
    // * @return 节点内容.
    // */
    // private String getNodeString(Element e, String nodeName)
    // {
    // String value = "";
    // String name = e.getName();
    // if (!AssertUtil.isEmpty(nodeName)&&nodeName.equalsIgnoreCase(name))
    // {
    // value = e.getTextTrim();
    // }
    // return value;
    // }
}

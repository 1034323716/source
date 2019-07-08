/**
 * 文件名：PersistConfig.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.tools.common.ConverUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * 功能描述：组件自身读取配置文件类.
 * 
 * 创建日期： 2012-9-3 作者： zhou gui ping
 */
public class DBConfig
{
    private static Logger logger = LoggerFactory.getLogger(DBConfig.class);
    /** 当前实例名称 */
    private static volatile DBConfig config;
    /** 组件内部配置文件名称 */
    private static String fileName = "persist.properties";
    /** 基本配置文件local.properties名称 */
    private static String baseconfigFileName = "local.properties";
    /** 属性集 */
    private Properties props = new Properties();

    private DBConfig()
    {
        loadConfig();
    }

    public static DBConfig getInstance()
    {
        if (config == null)
        {
            synchronized (DBConfig.class)
            {
                if (config == null)
                {
                    config = new DBConfig();
                }
            }
        }
        return config;
    }

    /** 初始化配置文件 */
    private void loadConfig()
    {
        try
        {
            this.props = Resource.getResourceAsProperties(fileName);
            // 加载基本配置文件local.properties
            this.props.load(Resource.getResourceAsStream(baseconfigFileName));
        }
        catch (IOException e)
        {
            logger.error("Failed load persist config.", e);
        }
    }

    /**
     * 获取配置文件目录
     * @return
     */
    public String getConfigDir()
    {
        return getProperty("configdir");
    }

    /**
     * 获取属性值.
     * 
     * @param key 属性key.
     * @return 属性值.
     */
    public int getPropertyInt(String key)
    {
        String result = props.getProperty(key);
        return ConverUtil.string2Int(result);
    }

    /**
     * 获取属性的int值.
     * 
     * @param key 属性key
     * @param defaultvalue 默认值
     * @return 属性值.
     */
    public int getPropertyInt(String key, int defaultvalue)
    {
        int result = getPropertyInt(key);
        if (result == 0)
        {
            result = defaultvalue;
        }
        return result;
    }

    /**
     * 获取属性值.
     * @param key 属性key.
     * @return 属性值.
     */
    public String getProperty(String key)
    {
        return props.getProperty(key);
    }

    /**
     * 获取属性值.
     * @param key 属性key.
     * @param defaultValue 默认值.
     * @return 属性值.
     */
    public String getProperty(String key, String defaultValue)
    {
        return props.getProperty(key, defaultValue);
    }
}

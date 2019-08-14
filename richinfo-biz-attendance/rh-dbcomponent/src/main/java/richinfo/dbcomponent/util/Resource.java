/**
 * 文件名：Resource.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

/**
 * 功能描述：加载配置文件的类，默认从当前线程类加载加载文件,可以指定其他类加载器来加载资源.
 * 
 * 创建日期： 2012-8-29 作者： zhou gui ping
 */
public final class Resource
{

    private Resource()
    {
    }

    /**
     * 获取当前线程上下文类加载器.
     * @return
     */
    public static ClassLoader getClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 根据资源名获取对应的URL对象.
     * 
     * @param resource 资源名称.
     * @return 对应的URL对象.
     * @throws IOException
     */
    public static URL getResourceURL(String resource) throws IOException
    {
        return getResourceURL(getClassLoader(), resource);
    }

    /**
     * 根据类加载器和资源名称获取对应的资源URL对象.
     * 
     * @param loader 加载器对象.
     * @param resource 资源名称.
     * @return 对应的URL对象.
     * @throws IOException
     */
    public static URL getResourceURL(ClassLoader loader, String resource)
        throws IOException
    {
        URL url = null;
        if (loader != null)
        {
            url = loader.getResource(resource);
        }
        if (url == null)
        {
            url = ClassLoader.getSystemResource(resource);
        }
        if (url == null)
        {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }

    /**
     * 加载指定的资源文件，默认从当前线程类加载器加载资源文件.
     * 
     * @param resource 资源文件.
     * @return 输入流.
     * @throws IOException
     */
    public static InputStream getResourceAsStream(String resource)
        throws IOException
    {
        return getResourceAsStream(getClassLoader(), resource);
    }

    /**
     * 将给定的类加载器和资源名称加载资源到InputStream对象中.
     * 
     * @param loader 类加载器.
     * @param resource 资源名称.
     * @return 输入流.
     * @throws IOException
     */
    public static InputStream getResourceAsStream(ClassLoader loader,
        String resource) throws IOException
    {
        InputStream in = null;
        if (loader != null)
        {
            in = loader.getResourceAsStream(resource);
        }
        if (in == null)
        {
            in = ClassLoader.getSystemResourceAsStream(resource);
        }
        if (in == null)
        {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    /**
     * 将给定的资源名称用当前线程类加载器加载资源到Properties对象中.
     * 
     * @param resource 资源名称.
     * @return Properties对象.
     * @throws IOException
     */
    public static Properties getResourceAsProperties(String resource)
        throws IOException
    {
        Properties props = new Properties();
        InputStream in = null;
        String propfile = resource;
        in = getResourceAsStream(propfile);
        props.load(in);
        in.close();
        return props;
    }

    /**
     * 将给定的资源名称和类加载器将资源加载到Properties对象中.
     * 
     * @param loader 类加载器.
     * @param resource 资源名称.
     * @return Properties对象。
     * @throws IOException
     */
    public static Properties getResourceAsProperties(ClassLoader loader,
        String resource) throws IOException
    {
        Properties props = new Properties();
        InputStream in = null;
        String propfile = resource;
        in = getResourceAsStream(loader, propfile);
        props.load(in);
        in.close();
        return props;
    }

    /**
     * 将给定的资源名称加载到一个Reader对象中.
     * 
     * @param resource 资源名称
     * @return Reader对象.
     * @throws IOException
     */
    public static Reader getResourceAsReader(String resource)
        throws IOException
    {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    /**
     * 根据ClassLoader和资源文件名称加载资源文件.
     * 
     * @param loader 类加载器.
     * @param resource 待加载的资源.
     * @return 返回已经加载到的资源
     * @throws IOException
     */
    public static Reader getResourceAsReader(ClassLoader loader, String resource)
        throws IOException
    {
        return new InputStreamReader(getResourceAsStream(loader, resource));
    }

    /**
     * 加载一个类,先重当前线程上下文中加载这个类,如果加载不到就从系统类加载器加载.
     * 
     * @param className 类名字.
     * @return Class 对象.
     * @throws ClassNotFoundException 类没有找到.
     */
    @SuppressWarnings("rawtypes")
    public static Class classForName(String className)
        throws ClassNotFoundException
    {
        Class clazz = null;
        try
        {
            clazz = getClassLoader().loadClass(className);
        }
        catch (Exception e)
        {
            
        }
        if (clazz == null)
        {
            clazz = Class.forName(className);
        }
        return clazz;
    }
}

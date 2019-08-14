/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package richinfo.dbcomponent.resourceloader;

import java.net.MalformedURLException;
import java.net.URL;

import richinfo.tools.common.AssertUtil;

/**
 * 
 * 
 * 功能描述：默认的资源加载器，用于加载classpath下的资源文件. 
 * 创建日期： 2012-9-15 
 * 作者： zhou gui ping
 */
public class DefaultResourceLoader
{

    /** 类路径前缀 */
    private static final String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;
    /** 类加载器 */
    private ClassLoader classLoader;

    public DefaultResourceLoader()
    {
        this.classLoader = ResourceUtils.getDefaultClassLoader();
    }

    public DefaultResourceLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    /**
     * 根据指定的文件加载文件. 如果有classpath:协议就从classpath下加载,否则从URL中加载
     * 
     * @param location 文件路径.
     * @return 资源加载器.
     */
    public ResourceLoader getResource(String location)
    {
        AssertUtil.assertNotNull(location, "Location");
        if (location.startsWith(CLASSPATH_URL_PREFIX))
        {
            return new ClassPathResource(
                location.substring(CLASSPATH_URL_PREFIX.length()),
                getClassLoader());
        }
        else
        {
            try
            {
                URL url = new URL(location);
                return new UrlResource(url);
            }
            catch (MalformedURLException ex)
            {
                return getResourceByPath(location);
            }
        }
    }

    /**
     * 获取资源加载器通过路径.
     * @param path 路径.
     * @return 资源加载器.
     */
    protected ResourceLoader getResourceByPath(String path)
    {
        return new ClassPathContextResource(path, getClassLoader());
    }

    /**
     * 
     * 功能描述：classpath下加载资源文件内部类.
     * @author zhou gui ping
     * @company: 深圳彩讯科技有限公司
     * 
     */
    private static class ClassPathContextResource extends ClassPathResource
        implements ContextResource
    {

        public ClassPathContextResource(String path, ClassLoader classLoader)
        {
            super(path, classLoader);
        }

        public String getPathWithinContext()
        {
            return getPath();
        }

        /**
         * 创建加载资源的对象.
         * @param 相关路径.
         */
        public ResourceLoader createRelative(String relativePath)
        {
            String pathToUse = StringUtils.applyRelativePath(getPath(),
                relativePath);
            return new ClassPathContextResource(pathToUse, getClassLoader());
        }
    }

    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader()
    {
        return this.classLoader;
    }

}

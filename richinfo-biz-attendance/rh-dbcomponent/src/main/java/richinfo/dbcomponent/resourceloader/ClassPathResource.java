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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import richinfo.tools.common.AssertUtil;

/**
 * 
 * 功能描述： classpath下资源.
 * 
 * 创建日期： 2012-9-15 
 * 作者： zhou gui ping
 */
public class ClassPathResource extends AbstractResource
{
    
    /** 资源路径 */
    private final String path;
    /** 类加载器 */
    private ClassLoader classLoader;
    /** 字节码 */
    private Class clazz;

    public ClassPathResource(String path)
    {
        this(path, (ClassLoader) null);
    }
    
    /**
     * 构造器.
     * @param path
     * @param classLoader
     */
    public ClassPathResource(String path, ClassLoader classLoader)
    {
        AssertUtil.assertNotNull(path, "path");
        String pathToUse = StringUtils.cleanPath(path);
        if (pathToUse.startsWith("/"))
        {
            pathToUse = pathToUse.substring(1);
        }
        this.path = pathToUse;
        this.classLoader = classLoader != null ? classLoader : ResourceUtils
            .getDefaultClassLoader();
    }

    /**
     * Create a new ClassPathResource for Class usage. The path can be relative
     * to the given class, or absolute within the classpath via a leading slash.
     * @param path relative or absolute path within the class path
     * @param clazz the class to load resources with
     * @see java.lang.Class#getResourceAsStream
     */
    public ClassPathResource(String path, Class clazz)
    {
        AssertUtil.assertNotNull(path, "Path");
        this.path = StringUtils.cleanPath(path);
        this.clazz = clazz;
    }

    /**
     * Create a new ClassPathResource with optional ClassLoader and Class. Only
     * for internal usage.
     * @param path relative or absolute path within the classpath
     * @param classLoader the class loader to load the resource with, if any
     * @param clazz the class to load resources with, if any
     */
    protected ClassPathResource(String path, ClassLoader classLoader,
        Class clazz)
    {
        this.path = StringUtils.cleanPath(path);
        this.classLoader = classLoader;
        this.clazz = clazz;
    }

    /**
     * Return the path for this resource (as resource path within the class
     * path).
     */
    public final String getPath()
    {
        return this.path;
    }

    /**
     * Return the ClassLoader that this resource will be obtained from.
     */
    public final ClassLoader getClassLoader()
    {
        return this.classLoader != null ? this.classLoader : this.clazz
            .getClassLoader();
    }

    /**
     * This implementation opens an InputStream for the given class path
     * resource.
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     * @see java.lang.Class#getResourceAsStream(String)
     */
    public InputStream getInputStream() throws IOException
    {
        InputStream is = null;
        if (this.clazz != null)
        {
            is = this.clazz.getResourceAsStream(this.path);
        }
        else
        {
            is = this.classLoader.getResourceAsStream(this.path);
        }
        if (is == null)
        {
            throw new FileNotFoundException(getDescription()
                + " cannot be opened because it does not exist");
        }
        return is;
    }

    /**
     * This implementation returns a URL for the underlying class path resource.
     * @see java.lang.ClassLoader#getResource(String)
     * @see java.lang.Class#getResource(String)
     */
    public URL getURL() throws IOException
    {
        URL url = null;
        if (this.clazz != null)
        {
            url = this.clazz.getResource(this.path);
        }
        else
        {
            url = this.classLoader.getResource(this.path);
        }
        if (url == null)
        {
            throw new FileNotFoundException(getDescription()
                + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    /**
     * This implementation returns a File reference for the underlying class
     * path resource, provided that it refers to a file in the file system.
     * @see richinfo.persist.resourceloader.springframework.util.ResourceUtils#getFile(java.net.URL,
     *      String)
     */
    public File getFile() throws IOException
    {
        return ResourceUtils.getFile(getURL(), getDescription());
    }

    /**
     * This implementation determines the underlying File (or jar file, in case
     * of a resource in a jar/zip).
     */
    protected File getFileForLastModifiedCheck() throws IOException
    {
        URL url = getURL();
        if (ResourceUtils.isJarURL(url))
        {
            URL actualUrl = ResourceUtils.extractJarFileURL(url);
            return ResourceUtils.getFile(actualUrl);
        }
        else
        {
            return ResourceUtils.getFile(url, getDescription());
        }
    }

    /**
     * This implementation creates a ClassPathResource, applying the given path
     * relative to the path of the underlying resource of this descriptor.
     * @see richinfo.persist.resourceloader.springframework.util.StringUtils#applyRelativePath(String,
     *      String)
     */
    public ResourceLoader createRelative(String relativePath)
    {
        String pathToUse = StringUtils.applyRelativePath(this.path,
            relativePath);
        return new ClassPathResource(pathToUse, this.classLoader, this.clazz);
    }

    /**
     * This implementation returns the name of the file that this class path
     * resource refers to.
     * @see richinfo.persist.resourceloader.springframework.util.StringUtils#getFilename(String)
     */
    public String getFilename()
    {
        return StringUtils.getFilename(this.path);
    }

    /**
     * This implementation returns a description that includes the class path
     * location.
     */
    public String getDescription()
    {
        return "class path resource [" + this.path + "]";
    }

    /**
     * This implementation compares the underlying class path locations.
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof ClassPathResource)
        {
            ClassPathResource otherRes = (ClassPathResource) obj;
            return (this.path.equals(otherRes.path)
                && nullSafeEquals(this.classLoader, otherRes.classLoader) && nullSafeEquals(
                    this.clazz, otherRes.clazz));
        }
        return false;
    }

    public int hashCode()
    {
        return this.path.hashCode();
    }

    private static boolean nullSafeEquals(Object o1, Object o2)
    {
        if (o1 == o2)
        {
            return true;
        }
        if (o1 == null || o2 == null)
        {
            return false;
        }
        if (o1.equals(o2))
        {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray())
        {
            if (o1 instanceof Object[] && o2 instanceof Object[])
            {
                return Arrays.equals((Object[]) o1, (Object[]) o2);
            }
            if (o1 instanceof boolean[] && o2 instanceof boolean[])
            {
                return Arrays.equals((boolean[]) o1, (boolean[]) o2);
            }
            if (o1 instanceof byte[] && o2 instanceof byte[])
            {
                return Arrays.equals((byte[]) o1, (byte[]) o2);
            }
            if (o1 instanceof char[] && o2 instanceof char[])
            {
                return Arrays.equals((char[]) o1, (char[]) o2);
            }
            if (o1 instanceof double[] && o2 instanceof double[])
            {
                return Arrays.equals((double[]) o1, (double[]) o2);
            }
            if (o1 instanceof float[] && o2 instanceof float[])
            {
                return Arrays.equals((float[]) o1, (float[]) o2);
            }
            if (o1 instanceof int[] && o2 instanceof int[])
            {
                return Arrays.equals((int[]) o1, (int[]) o2);
            }
            if (o1 instanceof long[] && o2 instanceof long[])
            {
                return Arrays.equals((long[]) o1, (long[]) o2);
            }
            if (o1 instanceof short[] && o2 instanceof short[])
            {
                return Arrays.equals((short[]) o1, (short[]) o2);
            }
        }
        return false;
    }

}

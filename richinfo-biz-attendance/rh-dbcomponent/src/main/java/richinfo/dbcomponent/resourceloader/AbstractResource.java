/**
 * 文件名：AbstractResource.java
 * 创建日期： 2012-9-4
 * 作者：     zhou gui ping
 * Copyright (c) 2009-2011 产品开发一部
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2012-9-4
 *   修改人：zhou gui ping
 *   修改内容：
 */
package richinfo.dbcomponent.resourceloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import richinfo.tools.common.AssertUtil;
import richinfo.tools.io.StreamUtil;

/**
 * 功能描述：抽象资源类.
 * 
 * 创建日期： 2012-9-4 作者： zhou gui ping
 */
public abstract class AbstractResource implements ResourceLoader
{
    
    /** {@inheritDoc} */
    @Override
    public boolean exists()
    {
        try
        {
            return getFile().exists();
        }
        catch (IOException ex)
        {
            InputStream is = null;
            try
            {
                is = getInputStream();
                return true;
            }
            catch (Throwable isEx)
            {
                return false;
            }finally
            {
                StreamUtil.close(is);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReadable()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOpen()
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public URL getURL() throws IOException
    {
        throw new FileNotFoundException(getDescription()
            + " cannot be resolved to URL");
    }

    /** {@inheritDoc} */
    @Override
    public URI getURI() throws IOException
    {
        URL url = getURL();
        try
        {
            return new URI(replace(url.toString(), " ", "%20"));
        }
        catch (URISyntaxException ex)
        {
            throw new IOException("Invalid URI [" + url + "]", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public File getFile() throws IOException
    {
        throw new FileNotFoundException(getDescription()
            + " cannot be resolved to absolute file path");
    }

    /** {@inheritDoc} */
    @Override
    public long lastModified() throws IOException
    {
        long lastModified = getFileForLastModifiedCheck().lastModified();
        if (lastModified == 0L)
        {
            throw new FileNotFoundException(
                getDescription()
                    + " cannot be resolved in the file system for resolving its last-modified timestamp");
        }
        return lastModified;
    }

    /** {@inheritDoc} */
    @Override
    public ResourceLoader createRelative(String relativePath)
        throws IOException
    {
        throw new FileNotFoundException(
            "Cannot create a relative resource for " + getDescription());
    }
    
    /** {@inheritDoc} */
    @Override
    public String getFilename() throws IllegalStateException
    {
        throw new IllegalStateException(getDescription()
            + " does not carry a filename");
    }
    
    /**
     * 获取文件最后修改日期前的检测文件是否存在.
     * @return 文件对象.
     * @throws IOException
     */
    protected File getFileForLastModifiedCheck() throws IOException
    {
        return getFile();
    }

    /**
     * 将输入字符串中的oldPattern替换为newPattern.
     * @param inString 输入字符串.
     * @param oldPattern 旧的字符串模式.
     * @param newPattern 新的字符串模式.
     * @return 处理过后的字符串.
     */
    private static String replace(String inString, String oldPattern,
        String newPattern)
    {
        if (AssertUtil.isEmpty(inString) || AssertUtil.isEmpty(oldPattern)
            || AssertUtil.isEmpty(newPattern))
        {
            return inString;
        }
        StringBuffer sbuf = new StringBuffer();
        int pos = 0;
        int index = inString.indexOf(oldPattern);
        int patLen = oldPattern.length();
        while (index >= 0)
        {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));
        return sbuf.toString();
    }

    @Override
    public String toString()
    {
        return getDescription();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj == this || (obj instanceof ResourceLoader && ((ResourceLoader) obj)
            .getDescription().equals(getDescription())));
    }
    
    @Override
    public int hashCode()
    {
        return getDescription().hashCode();
    }
}

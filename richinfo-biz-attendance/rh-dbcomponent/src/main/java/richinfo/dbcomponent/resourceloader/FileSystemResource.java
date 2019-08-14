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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import richinfo.tools.common.AssertUtil;

/**
 * 
 * 功能描述： 文件系统资源加载器.
 * 
 * 创建日期： 2012-9-15 
 * 作者： zhou gui ping
 */
public class FileSystemResource extends AbstractResource
{
    /** 文件对象 */
    private final File file;
    /** 文件路径 */
    private final String path;

    public FileSystemResource(File file)
    {
        AssertUtil.assertNotNull(file, "file");
        this.file = file;
        this.path = StringUtils.cleanPath(file.getPath());
    }

    public FileSystemResource(String path)
    {
        AssertUtil.assertNotNull(path, "path");
        this.file = new File(path);
        this.path = StringUtils.cleanPath(path);
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists()
    {
        return this.file.exists();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReadable()
    {
        return this.file.canRead() && !this.file.isDirectory();
    }

    /**
     * 获取文件对应的输入流.
     * 
     * @return InputStream 输入流.
     */
    @Override
    public InputStream getInputStream() throws IOException
    {
        return new FileInputStream(this.file);
    }

    /** {@inheritDoc} */
    @Override
    public URL getURL() throws IOException
    {
        return this.file.toURI().toURL();
    }

    /** {@inheritDoc} */
    @Override
    public URI getURI() throws IOException
    {
        return this.file.toURI();
    }

    /** {@inheritDoc} */
    @Override
    public File getFile()
    {
        return this.file;
    }
    
    /** {@inheritDoc} */
    @Override
    public ResourceLoader createRelative(String relativePath)
    {
        String pathToUse = StringUtils.applyRelativePath(this.path,
            relativePath);
        return new FileSystemResource(pathToUse);
    }
    
    /** {@inheritDoc} */
    @Override
    public String getFilename()
    {
        return this.file.getName();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return "file [" + this.file.getAbsolutePath() + "]";
    }

    public final String getPath()
    {
        return this.path;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj)
    {
        return obj == this || (obj instanceof FileSystemResource && this.path
            .equals(((FileSystemResource) obj).path));
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return this.path.hashCode();
    }
}

/**
 * 文件名：ResourceLoader.java
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * 功能描述：资源加载器接口.
 * 
 * 创建日期： 2012-9-4 
 * 作者： zhou gui ping
 */
public interface ResourceLoader
{

    /**
     * 判断指定资源是否存在.
     * @return 返回true表示存在.
     */
    boolean exists();

    /**
     * 判断资源是否可以读取.
     * @return 返回true表示可以读.
     */
    boolean isReadable();

    /**
     * 资源是否已经打开.
     * @return 返回true表示已打开.
     */
    boolean isOpen();

    /**
     * 获取资源的统一定位符.
     * @return url对象.
     * @throws IOException
     */
    URL getURL() throws IOException;

    /**
     * 获取资源.
     * @return
     * @throws IOException
     */
    URI getURI() throws IOException;

    /**
     * 获取文件
     * @return 文件对象.
     * @throws IOException
     */
    File getFile() throws IOException;

    /**
     * 获取文件最后修改时间.
     * @return
     * @throws IOException
     */
    long lastModified() throws IOException;

    /**
     * 创建一个关联的资源加载器.
     * @param relativePath
     * @return
     * @throws IOException
     */
    ResourceLoader createRelative(String relativePath) throws IOException;

    /**
     * 获取文件名称.
     * @return 文件名称.
     */
    String getFilename();

    /**
     * 获取资源文件描述.
     * @return
     */
    String getDescription();

    /**
     * 获取资源对应的文件输入流.
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;
}

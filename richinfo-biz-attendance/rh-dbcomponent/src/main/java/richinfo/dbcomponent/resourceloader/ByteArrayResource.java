/*
 * Copyright 2002-2007 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 
 * 功能描述：将字节数组中的数据转换成基于字节数组的资源.
 * 
 * 创建日期： 2012-9-15 作者： zhou gui ping
 */
public class ByteArrayResource extends AbstractResource
{

    /** 字节数组 */
    private final byte[] byteArray;
    /** 资源描述 */
    private final String description;

    /**
     * 构造器.
     * @param byteArray
     */
    public ByteArrayResource(byte[] byteArray)
    {
        this(byteArray, "resource loaded from byte array");
    }

    /**
     * 构造器.
     * @param byteArray
     * @param description
     */
    public ByteArrayResource(byte[] byteArray, String description)
    {
        if (byteArray == null)
        {
            throw new IllegalArgumentException("Byte array must not be null");
        }
        this.byteArray = byteArray;
        this.description = description != null ? description : "";
    }

    /**
     * 后去字节数组.
     * @return
     */
    public final byte[] getByteArray()
    {
        return this.byteArray;
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists()
    {
        return true;
    }

    /**
     * 获取字节数组输入流.
     * 
     * @return InputStream 输入流.
     */
    @Override
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(this.byteArray);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this
            || (obj instanceof ByteArrayResource && Arrays.equals(
                ((ByteArrayResource) obj).byteArray, this.byteArray));
    }

    @Override
    public int hashCode()
    {
        int hashCode = byte[].class.hashCode();
        hashCode = hashCode * 29;
        hashCode = hashCode * this.byteArray.length;
        return hashCode;
    }

}

/*
 * Copyright 2002-2006 the original author or authors.
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

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * 功能描述：输入流资源. 创建日期： 2012-9-15 作者： zhou gui ping
 */
public class InputStreamResource extends AbstractResource
{

    /** 输入流 */
    private final InputStream inputStream;
    /** 描述符 */
    private final String description;
    /** 是否读 */
    private boolean read;

    public InputStreamResource(InputStream inputStream)
    {
        this(inputStream, "resource loaded through InputStream");
    }

    public InputStreamResource(InputStream inputStream, String description)
    {
        if (inputStream == null)
        {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        this.inputStream = inputStream;
        this.description = description != null ? description : "";
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOpen()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getInputStream() throws IOException,
        IllegalStateException
    {
        if (this.read)
        {
            throw new IllegalStateException(
                "InputStream has already been read - "
                    + "do not use InputStreamResource if a stream needs to be read multiple times");
        }
        this.read = true;
        return this.inputStream;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return this.description;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj)
    {
        return (obj == this || (obj instanceof InputStreamResource && ((InputStreamResource) obj).inputStream
            .equals(this.inputStream)));
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return this.inputStream.hashCode();
    }

}

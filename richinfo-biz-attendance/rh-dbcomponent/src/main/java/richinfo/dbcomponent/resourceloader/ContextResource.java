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

/**
 * 
 * 功能描述：上下文资源.
 * 
 * 创建日期： 2012-9-15 作者： zhou gui ping
 */
public interface ContextResource extends ResourceLoader
{

    /**
     * 获取上下文路径.
     * 
     * @return 上下文路径.
     */
    String getPathWithinContext();
}

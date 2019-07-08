///*
// * Copyright 2002-2008 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package richinfo.dbcomponent.resourceloader;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.BeanFactoryAware;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.support.ResourceEditorRegistrar;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.HierarchicalMessageSource;
//import org.springframework.context.Lifecycle;
//import org.springframework.context.MessageSource;
//import org.springframework.context.MessageSourceAware;
//import org.springframework.context.MessageSourceResolvable;
//import org.springframework.context.NoSuchMessageException;
//import org.springframework.context.ResourceLoaderAware;
//import org.springframework.context.event.ApplicationEventMulticaster;
//import org.springframework.context.event.ContextClosedEvent;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.context.event.ContextStartedEvent;
//import org.springframework.context.event.ContextStoppedEvent;
//import org.springframework.context.event.SimpleApplicationEventMulticaster;
//import org.springframework.core.JdkVersion;
//import org.springframework.core.OrderComparator;
//import org.springframework.core.Ordered;
//import org.springframework.core.PriorityOrdered;
//import org.springframework.core.io.DefaultResourceLoader;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.util.Assert;
//import org.springframework.util.ClassUtils;
//import org.springframework.util.ObjectUtils;
//
///**
// * Abstract implementation of the {@link org.springframework.context.ApplicationContext}
// * interface. Doesn't mandate the type of storage used for configuration; simply
// * implements common context functionality. Uses the Template Method design pattern,
// * requiring concrete subclasses to implement abstract methods.
// *
// * <p>In contrast to a plain BeanFactory, an ApplicationContext is supposed
// * to detect special beans defined in its internal bean factory:
// * Therefore, this class automatically registers
// * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessors},
// * {@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessors}
// * and {@link org.springframework.context.ApplicationListener ApplicationListeners}
// * which are defined as beans in the context.
// *
// * <p>A {@link org.springframework.context.MessageSource} may also be supplied
// * as a bean in the context, with the name "messageSource"; otherwise, message
// * resolution is delegated to the parent context. Furthermore, a multicaster
// * for application events can be supplied as "applicationEventMulticaster" bean
// * of type {@link org.springframework.context.event.ApplicationEventMulticaster}
// * in the context; otherwise, a default multicaster of type
// * {@link org.springframework.context.event.SimpleApplicationEventMulticaster} will be used.
// *
// * <p>Implements resource loading through extending
// * {@link org.springframework.core.io.DefaultResourceLoader}.
// * Consequently treats non-URL resource paths as class path resources
// * (supporting full class path resource names that include the package path,
// * e.g. "mypackage/myresource.dat"), unless the {@link #getResourceByPath}
// * method is overwritten in a subclass.
// *
// * @author Rod Johnson
// * @author Juergen Hoeller
// * @author Mark Fisher
// * @since January 21, 2001
// * @see #refreshBeanFactory
// * @see #getBeanFactory
// * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor
// * @see org.springframework.beans.factory.config.BeanPostProcessor
// * @see org.springframework.context.event.ApplicationEventMulticaster
// * @see org.springframework.context.ApplicationListener
// * @see org.springframework.context.MessageSource
// */
//public abstract class AbstractApplicationContext extends DefaultResourceLoader
//		implements ConfigurableApplicationContext, DisposableBean {
//
//	/** ResourcePatternResolver used by this context */
//	private ResourcePatternResolver resourcePatternResolver;
//
//	/**
//	 * Return the ResourcePatternResolver to use for resolving location patterns
//	 * into Resource instances. Default is a
//	 * {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver},
//	 * supporting Ant-style location patterns.
//	 * <p>Can be overridden in subclasses, for extended resolution strategies,
//	 * for example in a web environment.
//	 * <p><b>Do not call this when needing to resolve a location pattern.</b>
//	 * Call the context's <code>getResources</code> method instead, which
//	 * will delegate to the ResourcePatternResolver.
//	 * @return the ResourcePatternResolver for this context
//	 * @see #getResources
//	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
//	 */
//	protected ResourcePatternResolver getResourcePatternResolver() {
//		return new PathMatchingResourcePatternResolver(this);
//	}
//
//
//
//	//---------------------------------------------------------------------
//	// Implementation of ResourcePatternResolver interface
//	//---------------------------------------------------------------------
//
//	public Resource[] getResources(String locationPattern) throws IOException {
//		return this.resourcePatternResolver.getResources(locationPattern);
//	}
//
//}

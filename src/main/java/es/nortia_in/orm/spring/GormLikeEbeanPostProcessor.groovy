package es.nortia_in.orm.spring


import javax.persistence.Entity;

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.avaje.ebean.config.ServerConfig;

import es.nortia_in.orm.controllers.AutoPkGenerationHook
import es.nortia_in.orm.controllers.BeanLifecycleHook
import es.nortia_in.orm.directory.DomainDirectory
import es.nortia_in.orm.enhance.BasicPropertiesEnhancer
import es.nortia_in.orm.enhance.ChainedPropertyAccessorEnhancer
import es.nortia_in.orm.enhance.ClassUtils
import es.nortia_in.orm.enhance.DomainServiceEnhancer
import es.nortia_in.orm.enhance.GormLikeMethodsEnhancer
import es.nortia_in.orm.enhance.LogEnhancer



/**
 * Post processor for customizing domain object classes
 * @author angel
 *
 */
class GormLikeEbeanPostProcessor implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(GormLikeEbeanPostProcessor.class)

	/**
	 * The spring application context
	 */
	def applicationContext

	/**
	 * The service locator bean
	 */
	def serviceLocator
	
	/**
	 * The domain class directory
	 */
	def domainDirectory


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
	throws BeansException {

		//Register GORM Like listeners
		if(bean instanceof ServerConfig) {
			return preProcessServerConfig(bean)
		}

		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
	throws BeansException {
		return bean;
	}

	/**
	 * Pre process the ebeans Server Config bean to register entity manage listeners
	 * @param serverConfig the server config bean
	 * @return pre processed server config bean
	 */
	protected def preProcessServerConfig(ServerConfig serverConfig) {

		assert serverConfig

		//Register listeners to be executed at the end
		
		//First, bean lifecycle interceptor methods hook (beforeInsert, beforeDelete, etc.)
		serverConfig.add(new BeanLifecycleHook(order:Integer.MAX_VALUE-1))
		
		//Next, PK autogeneration interceptor
		serverConfig.add(new AutoPkGenerationHook(order:Integer.MAX_VALUE))

		return serverConfig
	}

	/**
	 * Factory method to create transient-related enhancers. This enhancers will be applicable to every domain entity or embeddable
	 * Transient domains should be enhanced with: chained property accessor, domain service accessor and log.
	 *  
	 * @param domainDirectory the domain directory to be processed
	 * @return the applicable enhancer list
	 */
	protected def createTransientEnhancers(DomainDirectory domainDirectory) {
		
		def enhancers = []
		
		enhancers << new ChainedPropertyAccessorEnhancer()
		enhancers << new DomainServiceEnhancer(serviceLocator:serviceLocator)
		enhancers << new LogEnhancer()
		
		return enhancers
	}
	
	/**
	 * Factory method to create persistent specific enhancers. This enhacers will only be applied over @Entity classes
	 * @param domainDirectory the domain directory to be processed
	 * @return the applicable enhancer list
	 */
	protected def createPersistentEnhancers(DomainDirectory domainDirectory) {
		
		def enhancers = []
		enhancers << new BasicPropertiesEnhancer(domainDirectory:domainDirectory)
		enhancers << new GormLikeMethodsEnhancer(domainDirectory:domainDirectory)
		return enhancers		
	}
	
	/**
	 * Post process domain directory to enhance all domain classes
	 * @param domainDirectory the domain directory to process
	 * @return the processed domain directory
	 */
	protected def postProcessDomainDirectory(DomainDirectory domainDirectory) {
		
		assert domainDirectory
		
		
		//enhance default methods, for entities, transient entities and embeddables
		def entities = domainDirectory?.getDomainClasses() ?: []
		def embeddables = domainDirectory?.getEmbeddableClasses() ?: []
		
		//Create enhancers
		def transientEnhancers = createTransientEnhancers(domainDirectory)
		def persistentEnhancers = createPersistentEnhancers(domainDirectory)
		
		//Enhance all classes with basic enhancers
		(entities+embeddables).each {clazz ->
			
			//Enhance meta class
			def mc = ClassUtils.getExpandoMetaClass(clazz)
			assert mc
		
			transientEnhancers.each {enhancer ->
				enhancer.enhance(mc, clazz)
			}
				
		}
		
		//enhance persistent entities
		entities.findAll{it.getAnnotation(Entity)}.each {clazz->
			persistentEnhancers.each {enhancer ->
				enhancer.enhance(clazz.metaClass, clazz)
			}
		}
		
		return domainDirectory
		
	}
	
	/**
	 * Enhance a given class. This method is intended to be used solely for testing pruporses
	 * @param clazz the class to be enhanced
	 */
	void enhanceClass(Class clazz) {
		assert clazz
		
		//Retrieve meta class
		def mc = clazz.metaClass
		
		//Enhance transient methods
		def transientEnhancers = createTransientEnhancers(domainDirectory)
		transientEnhancers.each {
			it.enhance(mc, clazz)
		}
		
		//If entity is not persistent do not enhance as persistent
		if (!clazz.getAnnotation(Entity)) {
			return
		}
		
		//Enhance persistent
		def persistentEnhancers = createPersistentEnhancers(domainDirectory)
		persistentEnhancers.each {
			it.enhance(mc, clazz)
		}
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
	throws BeansException {
		this.applicationContext = applicationContext

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		if (domainDirectory == null) {
			throw new BeanCreationException("GormLikePostProcessor requires DomaindDirectory injected bean")
		}
		
		//Enable meta class globally to enchance domain classes
		ExpandoMetaClass.enableGlobally();
	
		//Post process
		postProcessDomainDirectory(domainDirectory)
			
	}

}

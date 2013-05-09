package es.nortia_in.orm.spring



import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanPostProcessor


import com.avaje.ebean.config.ServerConfig
import com.avaje.ebean.event.BeanPersistListener

import es.nortia_in.orm.annotations.DomainAnnotationsProcessor
import es.nortia_in.orm.annotations.TransientEntity
import es.nortia_in.orm.directory.DomainDirectory
import es.nortia_in.orm.enhance.ClassUtils

/**
 * Post processor for discovering and registering domain classes inside EBean Server/s.
 * @author angel
 *
 */
class DomainClassRegisterPostProcessor implements BeanPostProcessor, BeanFactoryAware {

	/**
	 * The class logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DomainClassRegisterPostProcessor)

	/**
	 * Domain class finder for domain class looking for
	 */
	DomainDirectory domainDirectory

	/**
	 * The spring bean factory
	 */
	BeanFactory beanFactory;

	/**
	 * The @Domain annotations processor
	 */
	DomainAnnotationsProcessor annotationsProcessor = new DomainAnnotationsProcessor()

	/**
	 * Utility method to perform class registration
	 * @param serverConfig the server config for class registering
	 * @param clazz the class to be registered
	 * @return the registered class or null if no class could be registered
	 */
	protected Class registerClassInServer(ServerConfig serverConfig, Class clazz) {
		assert serverConfig
		assert clazz

		// Check if persistence unit of entity is equals to name of serverConfig
		if (!classBelongsToServerConfig(clazz,serverConfig)) {
			return null
		}

		//Check @Domain annotations
		annotationsProcessor.checkAnnotations(clazz)

		//Register class
		serverConfig.addClass(clazz)
		return clazz
	}

	/**
	 * Utility method to register domain class inside given ebean server config
	 * @param serverConfig the server config for class registering
	 * @param domainClass the domain class to be registered
	 * @return the registered class or null if class cannot be registered
	 */
	protected Class registerDomainClass(ServerConfig serverConfig, Class domainClass) {

		assert serverConfig


		//Only register @Entity classes
		if (!domainClass.isAnnotationPresent(Entity.class)){

			//Transient entities are validated but not registered
			if (domainClass.isAnnotationPresent(TransientEntity)) {
				annotationsProcessor.checkAnnotations(domainClass)
			}

			return null
		}

		return registerClassInServer(serverConfig, domainClass)
	}

	/**
	 * Utility method to register embeddable classes inside given ebean server config
	 * @param serverConfig the server config for class registering
	 * @param clazz the embeddable class to be registered
	 * @return the registered class or null if class cannot be registered
	 */
	protected Class registerEmbeddableClass(ServerConfig serverConfig, Class clazz) {

		assert serverConfig

		//Only register @Embeddable classes
		if (!clazz.isAnnotationPresent(Embeddable.class)){
			return null
		}

		return registerClassInServer(serverConfig, clazz)
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
	throws BeansException {

		//Extending ebeans spring beans
		if(bean instanceof ServerConfig) {
			assert domainDirectory

			//Find domain classes
			def domainClasses = domainDirectory.getDomainClasses()

			//Process them
			domainClasses?.each {clazz ->
				registerDomainClass(bean,clazz)
			}

			// Find complex primary key classes
			def embeddableClasses = domainDirectory.getEmbeddableClasses()
			// Process them
			embeddableClasses?.each {clazz ->
				registerEmbeddableClass(bean, clazz)
			}

			//Register persistence listeners
			registerPersistenceListeners(bean)
		}

		return bean;
	}

	/**
	 * Register all persistence listeners listed in domain directory inside given server config
	 * @param serverConfig the server config for listener registration
	 */
	protected void registerPersistenceListeners(ServerConfig serverConfig) {
		assert serverConfig

		def listeners = domainDirectory?.getPersistListeners()

		listeners = listeners.findAll {
			def interfaces = ClassUtils.getGenericInterfaces(it)
			 
			return interfaces.find {iface ->

				if (iface instanceof ParameterizedType) {

					//Ignore not persist listeners
					if (!BeanPersistListener.class.isAssignableFrom(iface.getRawType())) {
						return false
					}

					//Register only the listeners attached to server registered entities
					def arg = iface.getActualTypeArguments()[0] 
					
					//If interface type is a parameter and not a concrete class...search it inside class declaration
					if (arg instanceof TypeVariable) {
						arg = it.getGenericSuperclass()?.getActualTypeArguments()[0];
					}
					
					return arg in serverConfig.classes
				}

				return false
			}
		}

		listeners?.each {

			//Search in bean factory all beans with the given persist listener class
			def beans
			if (beanFactory) {
				beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, it)
				beans = beans.values()
			}

			//If not defined any bean, instantiate a empty listener of given class
			if (!beans) {
				beans = it.newInstance()
			}

			//Register listeners
			beans.each {bean ->
				log.debug "Registering persist listener: $bean"
				serverConfig.add(bean)
			}
		}
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
	throws BeansException {
		return bean;
	}

	/**
	 * Checks if persistence unit of class is equals to name of serverConfig.
	 * @param clazz
	 * @param serverConfig
	 * @return <code>true</code> if persistence unit of class is equals to name of serverConfig.
	 */
	protected boolean classBelongsToServerConfig(Class clazz, ServerConfig serverConfig) {
		// Look for persistence unit annotation
		def persistenceUnitName = domainDirectory.getPersistenceUnit(clazz)

		// If serverConfig is default server, class can not have persistence unit
		if (serverConfig.defaultServer && !persistenceUnitName) {
			return true
		}

		// Persistence unit of entity must be equals to name of serverConfig (ebeanServer)
		if (persistenceUnitName == serverConfig.name) {
			return true
		}

		return false
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory
	}

}

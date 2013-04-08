package es.nortia_in.orm.enhance

import org.slf4j.LoggerFactory

import groovy.lang.MetaClass;

/**
 * Enhance domain class, injecting Logger by setting the "log" property 
 * @author angel
 *
 */
class LogEnhancer implements DomainClassEnhancer {

	/**
	 * The log propertu name
	 */
	public static final String LOG_PROPERTY_NAME = "log"


	@Override
	public void enhance(MetaClass metaClass, Class clazz) {
		
		assert metaClass
		assert clazz

		//Only enhace logger if does't has any other
		if (metaClass.hasProperty(LOG_PROPERTY_NAME)) {
			return
		}

		//Inject logger
		metaClass."$LOG_PROPERTY_NAME" = LoggerFactory.getLogger(clazz)		
	}	
}

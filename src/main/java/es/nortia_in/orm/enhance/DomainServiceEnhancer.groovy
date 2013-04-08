package es.nortia_in.orm.enhance

import es.nortia_in.orm.service.DomainServiceLocator;
import groovy.lang.MetaClass;

/**
 * Enhance domain class injecting "getService()" static method.
 * Service will be search using configured {@see DomainServiceLocator}
 * @author angel
 *
 */
class DomainServiceEnhancer implements DomainClassEnhancer {

	/**
	 * The domain service locator for service searching
	 */
	DomainServiceLocator serviceLocator
	
	@Override
	public void enhance(MetaClass metaClass, Class clazz) {
		
		assert metaClass
		
		//Inject getService method
		metaClass.'static'.getService = {serviceName ->
			serviceLocator?.getService(serviceName)			
		}
		
	}

}

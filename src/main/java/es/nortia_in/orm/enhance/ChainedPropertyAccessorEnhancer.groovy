package es.nortia_in.orm.enhance

import groovy.lang.MetaClass;

/**
 * Enhance domain classes to inject "retrieve()" method which allows
 * chained property access.
 * 
 * I.ex: <code> entity.retrieve("customers.name") </code> allow
 * to retrieve the list of all customers name
 * @author angel
 *
 */
class ChainedPropertyAccessorEnhancer implements DomainClassEnhancer {

	@Override
	public void enhance(MetaClass metaClass, Class clazz) {
		
		assert metaClass
		
		metaClass.retrieve = {property ->
			ClassUtils.getProperty(delegate, property)
		}
		
	}	
}

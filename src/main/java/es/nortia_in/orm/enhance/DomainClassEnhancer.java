package es.nortia_in.orm.enhance;

import groovy.lang.MetaClass;


/**
 * Enhace any given domain class to add some new behaviour
 * @author angel
 *
 */
public interface DomainClassEnhancer {

	/**
	 * Perform class enhancement
	 * @param metaClass the meta class to be enhanced
	 * @param clazz the domain class to be enhanced
	 */
	void enhance(MetaClass metaClass, Class clazz);
	
}

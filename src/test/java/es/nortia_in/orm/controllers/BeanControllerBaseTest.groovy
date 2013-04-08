package es.nortia_in.orm.controllers;

import com.avaje.ebean.event.BeanPersistRequest;

/**
 * Base class for ebaen persist controller tests
 * @author angel
 *
 */
abstract class BeanControllerBaseTest {

	/**
	* Wraps the given entity inside a mock bean persist request
	* @param entity the entity to be wrapped
	* @return the bean persist request
	*/
   protected BeanPersistRequest wrapEntity(def entity) {
	   
	   def request = new Expando()
	   request.getBean = {entity }
	   return request as BeanPersistRequest
   }
	
}

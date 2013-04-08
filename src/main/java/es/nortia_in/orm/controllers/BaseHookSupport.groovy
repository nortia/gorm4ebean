package es.nortia_in.orm.controllers

import com.avaje.ebean.event.BeanPersistAdapter

/**
 * Base class for Bean Controller implementations. Allow configurable dynamic precedence
 * @author angel
 *
 */
abstract class BaseHookSupport extends BeanPersistAdapter {

	/**
	 * The execution order. 0 by default
	 */
	int order = 0
	
	@Override
	public int getExecutionOrder() {
		return order
	}

}

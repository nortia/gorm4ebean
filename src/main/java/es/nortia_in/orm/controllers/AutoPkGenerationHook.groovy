package es.nortia_in.orm.controllers


import com.avaje.ebean.event.BeanPersistRequest;

import es.nortia_in.orm.pk.CompositePkProcessor

/**
 * Intercept bean insertions to autogenerate Primary Key values
 * for composite PKs.
 * @author angel
 *
 */
class AutoPkGenerationHook extends BaseHookSupport {

	/**
	 * Manager for composite pk
	 */
	protected CompositePkProcessor compositePkManager = new CompositePkProcessor()

	@Override
	public boolean isRegisterFor(Class<?> clazz) {
		//Registered for every class
		return true;
	}
	
	
	@Override
	public boolean preInsert(BeanPersistRequest<?> request) {
		
		//Extract entity from request
		def entity = request.bean
		if (!entity) {
			return true
		}
		
		//Check if entity has a pk
		if (entity.id) {
			//If has an assigned pk do nothing
			return true
		}
		
		//Generate PK
		def pk = compositePkManager.computePk(entity)
		//Assign the pk
		def pkField = entity.pk
		assert pkField
		entity."${pkField}" = pk
		
		return true
	}

}

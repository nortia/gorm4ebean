package es.nortia_in.orm.pk

import es.nortia_in.orm.enhance.ClassUtils;





/**
 * Property based PK component. The PK component will be composed by accessing a 
 * given entity property. This property can be an entity property or a chained property
 * from any related entity
 * @author angel
 *
 */
class PkPropertyComponent extends PkComponent{

	/**
	 * The property name or property chain
	 */
	String name
	
	@Override
	public Object doGetValue(Object entity) {
		assert entity != null
		assert name
		
		return ClassUtils.getProperty(entity, name)
	}

}

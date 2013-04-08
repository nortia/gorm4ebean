package es.nortia_in.orm.pk



class PkDynamicComponent extends PkComponent{

	Closure closure
	
	@Override
	public Object doGetValue(Object entity) {
		closure.call(entity)
	}

}

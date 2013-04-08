package es.nortia_in.orm.pk


class PkConstantComponent extends PkComponent{

	String value

	void setValue(String value) {
		this.value = value
		this.length = value.size()
		this.type = String.class
	}
	
	
	@Override
	public Object validate(String pk) {
		if (!pk) {
			return null
		}
		
		if (pk.startsWith(value)) {
			return super.validate(pk)
		}
		
		return null
	}



	@Override
	public String getValue(Object entity) {
		return doGetValue(entity)
	}



	@Override
	public Object doGetValue(Object entity) {
		return value
	}	
	
}

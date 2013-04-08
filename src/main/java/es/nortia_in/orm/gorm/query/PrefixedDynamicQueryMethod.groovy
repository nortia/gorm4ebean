package es.nortia_in.orm.gorm.query


import com.avaje.ebean.Query;


/**
 * Base class for dynamic query methods identified by its prefix.
 * I.e: findBy, findAllBy, etc.
 * 
 * @author angel
 *
 */
abstract class PrefixedDynamicQueryMethod extends DynamicQueryMethod {

	/**
	 * Return method prefix
	 * @return the method prefix
	 */
	protected abstract String getMethodPrefix();

	/**
	 * Check if given method name can be executed by this object.
	 * This object can execute method starting by: <code>findBy</code>
	 * @param methodName the method name to check
	 * @return true if method can be managed, false otherwise
	 */
	public boolean canExecute(String methodName) {
		return methodName.startsWith(getMethodPrefix()) &&
		(getMethodPrefix() != methodName)
	}

	/**
	 * Extracts query form method name
	 * @param methodName method name
	 * @return the query extracted for method name
	 */
	protected String extractQuery(String methodName) {
		assert methodName
		return methodName[getMethodPrefix().size()..-1]
	}
	
	


}

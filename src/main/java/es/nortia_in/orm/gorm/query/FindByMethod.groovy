package es.nortia_in.orm.gorm.query


import com.avaje.ebean.Query;


/**
 * GORM-Like method for queries of the type: findByXXX, where
 * XXX is a boolean expression formed by domain class property names joined by
 * boolean operators "And" and/or "Or".
 * @author angel
 *
 */
class FindByMethod extends PrefixedDynamicQueryMethod {


	@Override
	protected String getMethodPrefix() {
		return "findBy"
	}
	
	/**
	 * Execute the eEbean query selecting query mode:unique, list, set, etc.
	 * @param query the generated ebean query or expression list 
	 * @return the query result
	 */
	protected def doQueryExecution(def query, def mappings) {
		assert query
		
		//Retrieve only one object
		query.setFirstRow(0)
		query.setMaxRows(1)
		
		//Query list
		def result = query.findList()
		
		//Return the object or null
		return result ? result[0] : null
	}




}

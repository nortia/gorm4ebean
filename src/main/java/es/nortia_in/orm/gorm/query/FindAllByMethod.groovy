package es.nortia_in.orm.gorm.query


import com.avaje.ebean.Query;


/**
 * GORM-Like method for queries of the type: findAllByXXX, where
 * XXX is a boolean expression formed by domain class property names joined by
 * boolean operators "And" and/or "Or".
 * @author angel
 *
 */
class FindAllByMethod extends PrefixedDynamicQueryMethod {

	@Override
	protected String getMethodPrefix() {
		return "findAllBy"
	}
	
	/**
	 * Execute the eEbean query selecting query mode:unique, list, set, etc.
	 * @param query the generated ebean query or expression list 
	 * @return the query result
	 */
	protected def doQueryExecution(def query, def mappings) {
		assert query
		
		//Apply paging directives
		query = QueryDirectiveMapper.applyPageDirectives(query, mappings)
		
		
		//Execute query
		return query.findList()
	}




}

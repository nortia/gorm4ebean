package es.nortia_in.orm.gorm.query


import com.avaje.ebean.Query;


/**
 * GORM-Like method for queries of the type: findPagedByXXX, where
 * XXX is a boolean expression formed by domain class property names joined by
 * boolean operators "And" and/or "Or".
 * 
 * Result is ever a PagedList
 * @author angel
 *
 */
class FindPagedByMethod extends PrefixedDynamicQueryMethod {

	/**
	 * Page size property name
	 */
	public static final String PAGE_SIZE = "pageSize" 
	
	/**
	 * Default page size
	 */
	public static final int DEFAULT_PAGE_SIZE = 100
	
	@Override
	protected String getMethodPrefix() {
		return "findPagedBy"
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
		
		//Retrieve page size
		def pageSize = (mappings.get(PAGE_SIZE) as Integer) ?: DEFAULT_PAGE_SIZE
		
		//Execute query
		return query.findPagingList(pageSize)
	}


}

package es.nortia_in.orm.gorm.query

/**
 * Utility class to map query directives (sort, order, max and offset) to a 
 * eBeans query.
 * 
 * @author angel
 *
 */
class QueryDirectiveMapper {

	/**
	 * Sort directive name
	 */
	public static final String SORT = "sort"

	/**
	 * Order directive name
	 */
	public static final String ORDER = "order"

	/**
	 * Order ascendant directive value
	 */
	public static final String ASC = "asc"

	/**
	 * Order descendant directive value
	 */
	public static final String DESC = "desc"

	/**
	 * Limit result size directive
	 */
	public static final String MAX = "max"


	/**
	 * Offset paging directive
	 */
	public static final String OFFSET = "offset"

	/**
	 * Apply a set of given query directives to a eBeans query 
	 * @param query the eBean query
	 * @param directives directive map to apply
	 * @return the query with directives applied
	 */
	static def applyQueryDirectives(def query, def directives) {

		assert query

		//Sorting directives
		query = applySortDirectives(query, directives)

		//Paging directives
		query = applyPageDirectives(query, directives)

		return query
	}

	/**
	 * Apply size and paging directives (max and offset) to a given query
	 * @param query the query to manage
	 * @param directives the directive map to apply
	 * @return the query with directives applied to
	 */
	static def applyPageDirectives(def query, def directives) {
		assert query

		//Max
		def pMax = directives?.get(MAX) as Integer
		if (pMax) {
			query = max(query,pMax)
		}

		//Offset.
		def pOffset = directives?.get(OFFSET) as Integer
		if (pOffset) {
			query = offset(query,pOffset)
		}

		return query
	}

	/**
	 * Apply sorting directives to a given query
	 * @param query the query to manage
	 * @param directives the directive map to apply
	 * @return the resulting query
	 */
	static def applySortDirectives(def query, def directives) {
		assert query

		def sort = directives?.get(SORT)

		//If no sort definition, do nothing
		if (!sort) {
			return query
		}


		return orderBy(query, sort, directives?.get(ORDER))
	}

	/**
	 * Configure query for max result size 
	 * @param query the query to configure
	 * @param max the max size
	 * @return the configured query
	 */
	static def max(def query, int max) {
		assert query
		query.setMaxRows(max)
		return query
	}

	/**
	 * Configure query for offset page query
	 * @param query the query to configure
	 * @param offset the offset (page) to retrieve
	 * @return the configured query
	 */
	static def offset(def query, int offset) {
		assert query
		query.setFirstRow(offset)
		return query
	}

	/**
	 * Configure query with order by directive
	 * @param query the query to configure
	 * @param field the field for sorting
	 * @param order the order ASC or DESC
	 * @return the configured query
	 */
	static def orderBy(def query, def field, def order) {
		assert query
		assert field

		//Descendant order by default
		def method = (order == DESC) ? "desc" : "asc"
		assert method

		if(field instanceof List<String>){
			field.each{
				query.orderBy()."$method"(it)
			}
		}else{
			query.orderBy()."$method"(field)
		}

		return query
	}

}

package es.nortia_in.orm.gorm.query

import com.avaje.ebean.Ebean
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Junction;
import com.avaje.ebeaninternal.server.expression.JunctionExpression;

import es.nortia_in.orm.gorm.EBeanGormException;
import groovy.util.logging.Slf4j;

/**
 * Detached Criteria for complex query execution. See GORM DetachedCriteria class
 * @author angel
 *
 */
@Slf4j
class GormLikeCriteria {


	/**
	 * Operation mapping. Maps each GORM operator with iths symbolic version
	 */
	final static operatorsMap = ["eq":"=", "gt":">", "ge":">=", "lt":"<", "le":"<=", "ne":"<>"]

	/**
	 * The criteria class
	 */
	Class domainClass

	/**
	 * The expression stack  to be created. Only NOT operator currently push new stack levels
	 */
	Stack exprStack = []

	/**
	 * The query to be created
	 */
	def query

	/**
	 * The stack for object graph navigation
	 */
	Stack navigationPath = []

	/**
	 * The EBean Server for query execution
	 */
	EbeanServer server;
	
	private Class disjunctionClass 
	
	private Class conjunctionClass

	/**
	 * 
	 * @param domainClass the domain class to be queried
	 */
	GormLikeCriteria(Class domainClass, EbeanServer server) {
		super()

		assert domainClass
		this.domainClass = domainClass

		assert server
		this.server = server
	}


	/**
	 * Factory method for query creation
	 * @return the created query
	 */
	protected def createQuery() {
		assert domainClass
		assert server

		//Create the query
		return server.find(domainClass)
	}

	/**
	 * Apply generated expression to actual query
	 * @return the new generated query
	 */
	protected def applyExpressionsToQuery() {

		assert query

		//With empry stack there are no expressions to be added
		if (exprStack.isEmpty()) {
			return query
		}

		def q = query
		def expr = exprStack.pop()
		if (expr) {
			expr = waveExpression(expr)
			if (!expr) {
				return q
			}
			q = q.where(expr)
		}

		return q
	}


	/**
	 * Build the criteria using given closure DSL
	 * @param closure the closure DSL for criteria building. See DetchaedCriteria GORM class
	 * @return the built detached criteria
	 */
	protected GormLikeCriteria build(Closure closure) {
		if (closure == null) {
			return
		}

		//Create the query and assign it
		if (!query) {
			query = createQuery()
		}

		//Initialize classes
		
		
		//Initialize stack. By default, every query is a big AND
		def initialAndExpression = Expr.conjunction(query)
		this.disjunctionClass = Expr.disjunction(query).getClass()
		this.conjunctionClass = initialAndExpression.getClass()
		
		pushExpression(initialAndExpression)

		//Execute the closure
		closure.delegate = this
		closure.call()

		//Finalize stack
		pushExpression(initialAndExpression)

		return this

	}

	/**
	 * GORM-like listDistinct method
	 * @param c the closure for criteria building
	 * @return the distinct results found
	 */
	def listDistinct(Closure c) {
		return list([distinct:true], c)
	}

	/**
	 * GORM like listDistinct method with sorting, ordering and limit mappings
	 * @param args the sorting, ordering and limit mappings
	 * @param c the closure for criteria building
	 * @return the distinct results found
	 */
	def listDistinct(Map args, Closure c) {
		def params = [:]
		if (args) {
			params.putAll(args)
		}
		args.distinct = true
		list(args, c)
	}

	/**
	 * GORM-like method list. Retrieves all entities for criteria class
	 * @return the entity list
	 */
	def list() {
		return list({} as Closure)
	}

	/**
	 * GORM-Like list method
	 * @param c the closure for criteria building
	 * @return the found entity list
	 */
	def list(Closure c) {
		return list([:], c)
	}

	/**
	 * GROM-Like list method. Receives a property map for sorting, order and limit configuration.
	 * If "pageSize" mapping is defined, result set should be packaged inside a paging list 
	 * @param args the sorting, ordering and limit mappings
	 * @param c the closure for criteria building
	 * @return the entity list
	 */
	def list(Map args, Closure c) {

		//Build the query
		build(c)

		def q = applyExpressionsToQuery()

		if (args) {
			q = QueryDirectiveMapper.applyQueryDirectives(q, args)
		}

		//Distinct
		q.setDistinct(args.distinct ?: false)

		//If paginating are requested...
		if (args.pageSize) {
			return q.findPagingList(args.pageSize.asType(Integer))
		}

		return q.findList()
	}

	/**
	 * GORM-like scroll method. Result is packaged into paginated list
	 * @param c the closure for criteria building
	 * @throws UnsupportedOperationException not supported by ebean
	 */
	def scroll(Closure c) {
		throw new UnsupportedOperationException("Scrollable Result Sets not supported by eBean")
	}

	/**
	 * GORM-Like scroll method. Result is packaged into paginated list. Receives a property map for sorting, order and limit configuration
	 * @param args the sorting, ordering and limit mappings
	 * @param c the closure for criteria building
	 * @throws UnsupportedOperationException not supported by ebean
	 */
	def scroll(Map args, Closure c) {
		throw new UnsupportedOperationException("Scrollable Result Sets not supported by eBean")
	}



	/**
	 * GORM-like get method. Returns a single one result
	 * @param c the closure for criteria building
	 * @return the result found
	 */
	def get(Closure c) {

		//Build the query
		build(c)

		def q = applyExpressionsToQuery()
		return q.findUnique()
	}

	/**
	 * Gorm-like fetchMode method. Configure fetch mode for each association
	 * @param path the accociation path to be configured
	 * @param mode the fetch mode to be used
	 */
	protected def fetchMode(String path, FetchConfig mode){
		query = query.fetch(path, mode)
	}

	/**
	 * Gorm-like join method. Tell a join over configured associated property 
	 * @param path the association to be joined
	 */
	protected def join(String path) {
		query = query.fetch(path)
	}

	/**
	 * Gorm-like projections method. Projections are still not supported
	 * @param c the builder closure
	 * @throws UnsupportedOperationException ever
	 */
	protected def projections(Closure c) {
		throw new UnsupportedOperationException("Pojections feature not supported. Use eBean rawSql instead")
	}


	/**
	 * Push the given expression into expressions queue
	 * @param expression the expression to be pushed. 
	 */
	protected void pushExpression(def expression) {
		assert expression
		exprStack.push(expression)
	}

	/**
	 * Wave the given expression with others in the expression queue to create a single composite expression that represents the whole queue
	 * @return the composite expression
	 */
	protected Expression waveExpression(Expression expression) {
		assert expression
		return expression
	}

	/**
	 * Wave the given expression with others in the expression queue to create a single composite expression that represents the whole queue
	 * @return the composite expression
	 */
	protected Expression waveExpression(Junction expression) {
		assert expression
		
		def op = (disjunctionClass.isInstance(expression)) ? "or" : "and"
		def expr = exprStack.pop()
		def compositeExpression 
		
		//Lookup for next expression occurrence
		while (expr != expression) {
			//Wave the expression
			expr = waveExpression(expr)
			if (expr && compositeExpression) {
				compositeExpression = Expr."$op"(compositeExpression, expr)
			} else {
				compositeExpression = expr
			}
			
			expr = exprStack.pop()
		}
		
		return compositeExpression
		
	}

	/**
	 * Disjunction operation.
	 * @param closure the disjuntion members definition
	 * @return the result query
	 */
	protected def or(Closure closure) {
		assert closure != null

		//Change active operator
		def operator = Expr.disjunction(query)
		pushExpression(operator)
		
		closure.call()

		//Restore previous operator
		pushExpression(operator)
	}

	/**
	 * Conjuction operation.
	 * @param closure the conjunction members definition
	 * @return the result query
	 */
	protected def and(Closure closure) {
		assert closure != null

		//Change active operator
		def operator = Expr.conjunction(query)
		pushExpression(operator)

		closure.call()

		//Restore previous operator
		pushExpression(operator)

	}


	/**
	 * Support for gorm like "isEmpty" operator. Beware: Due to eBean implementation issues with inner joins, 
	 * this method desn't work as expected.
	 * @param propertyName the property to compare
	 */
	protected def isEmpty(String propertyName) {
		executeComparation("isNull", [propertyName]as Object[])
	}

	/**
	 * Support for gorm like "isNotEmpty" operator. B
	 * @param propertyName the property to compare
	 */
	protected def isNotEmpty(String propertyName) {
		executeComparation("isNotNull", [propertyName]as Object[])
	}

	/**
	 * Support for beteenProperties operator.
	 * @param property1 the fist property (lower)
	 * @param property2 the second property (higher)
	 * @param value the value to match
	 */
	protected def betweenProperties(String property1, String property2, Object value) {

		property1 = composeNavigationPath(property1)
		property2 = composeNavigationPath(property2)

		pushExpression(Ebean.getExpressionFactory().betweenProperties(property1, property2, value))
	}

	/**
	 * Support for gorm-like equals ignore case condition.
	 * 
	 * eq "propertyName", "value", [ignoreCase:true]
	 * 
	 * @param propertyName the property name to compare
	 * @param value the value to be compared
	 * @param conditions the comparation conditions
	 */
	protected def eq(String propertyName, String value, Map conditions) {

		assert propertyName

		String op = (conditions?.ignoreCase) ? "ieq" : "eq"
		executeComparation(op, [propertyName, value]as Object[])
	}

	/**
	 * Expr class does not support idIn method, so a special mangement for this comparator should be ad hoc implemented in this class 
	 */
	protected def idIn(Object args) {
		pushExpression(Ebean.getExpressionFactory().idIn(args))
	}

	/**
	 * Utility method to perform raw ebean comparison  between two properties
	 * @param operator the operator to be used
	 * @param propertyName the first property
	 * @param propertyToCompare the second property to be compared
	 */
	protected def rawPropertyComparation(String methodName, String propertyName, String propertyToCompare) {
		assert methodName
		assert propertyName
		assert propertyToCompare

		def operator = operatorsMap[methodName]
		assert operator

		propertyName = composeNavigationPath(propertyName)
		propertyToCompare = composeNavigationPath(propertyToCompare)

		raw("$propertyName $operator $propertyToCompare")

	}

	/**
	 * Add a SQL-Like query restriction 
	 * @param restriction the query restriction to be added
	 */
	protected def raw(String restriction) {
		pushExpression(Expr.raw(restriction))
	}

	/**
	 * GORM-like sqlRestriction method. It is an alias for "raw" method
	 * @param restriction the sql-like restriction
	 */
	protected def sqlRestriction(String restriction) {
		raw restriction
	}

	/**
	 * Max results directive. Limit the number of retrieved records
	 * @param results max number of retrieved records
	 */
	protected def maxResults(int results) {
		query = QueryDirectiveMapper.max(query, results)
	}

	/**
	 * Offset directive. Skipt the given numner of results -1 
	 * @param offset the first result to be retrieved
	 */
	protected def firstResult(int offset) {
		query = QueryDirectiveMapper.offset(query, offset)
	}

	/**
	 * Configure sorting directives
	 * @param property the property to sort
	 * @param sortOrder the sort order: ASC or DESC
	 */
	protected def order(String property, String sortOrder) {
		query = QueryDirectiveMapper.orderBy(query, property, sortOrder)
	}

	/**
	 * Utility method to generate property navigation path. Navigation path allow queries through related objects graph
	 * @param property the property to be compared
	 * @return the full navigation path to compared property
	 */
	protected String composeNavigationPath(String property) {
		return (navigationPath + property).join(".")
	}

	/**
	 * Gorm-like not operator
	 * @param c negated construction
	 */
	protected def not(Closure c) {

		//Execute inner
		c.call()

		//Retrieve expr
		def expr = exprStack.pop()

		//Not
		if (expr) {
			pushExpression(Expr.not(expr))
		}

	}

	/**
	 * Utility mehtod to execute comparations
	 * 
	 * @param comparator the comparator method name
	 * @param args the comparation arguments
	 */
	protected def executeComparation(String comparator, Object[] args ){
		assert comparator


		//If comparator is not related to ID property...
		if (comparator in ["idEq", "idIn"]) {
			//..no property processing is required
			pushExpression(Expr."$comparator"(*args))
			return
		}

		//Generate property name
		def propertyName = composeNavigationPath(args[0])

		def argList = []
		if (args.size() > 1) {
			argList.addAll(args[1..-1] as List)
		}

		//Execute in query
		pushExpression(Expr."$comparator"(propertyName,  *argList))
	}

	/**
	 * Generate a subquery inside a nested object
	 * @param propertyName the property name for navigation
	 * @param closure the closure for subquery generation
	 */
	protected def navigateTo(String propertyName, Closure closure) {
		assert propertyName
		assert closure != null

		//Push property name
		navigationPath.push(propertyName)

		//Execute closure
		closure.call()

		//Pop navigation path
		navigationPath.pop()

	}

	protected def methodMissing(String methodName, args) {

		assert methodName

		//Property comparation
		if (methodName.endsWith("Property") && args.size() >= 2) {
			rawPropertyComparation(methodName-"Property", args[0], args[1])
			return
		}

		//If method is any comaprator method name...
		def operators = Comparators.COMPARATOR_METHOD_MAPPING.values()
		if (methodName in operators) {
			executeComparation(methodName, args)
			return
		}


		//Execute navigation
		if (domainClass.metaClass.getMetaProperty(methodName)) {
			navigateTo(methodName, *args)
			return
		}

		//size* operators are not supported
		if (methodName.startsWith("size")){
			throw new UnsupportedOperationException("Size* operators are not supported by EBean engine")
		}

		//Re-throw the exception
		throw new MissingMethodException(methodName, this.getClass(), args)
	}

}

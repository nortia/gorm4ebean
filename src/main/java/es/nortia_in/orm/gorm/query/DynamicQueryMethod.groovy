package es.nortia_in.orm.gorm.query

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.avaje.ebean.Query;

import es.nortia_in.orm.gorm.EBeanGormException


/**
 * Base class for GORM-Like dynamic query methods. 
 * 
 * Allow method auto-generation like findAllByNameAndSumnameLike, etc. in GORM style.
 * 
 * @author angel
 *
 */
abstract class DynamicQueryMethod {

	/**
	 * Logger
	 */
	protected final Logger log = LoggerFactory.getLogger(DynamicQueryMethod.getName())

	/**
	 * Check if given method name can be executed by this object.
	 * 
	 * @param methodName the method name to check
	 * @return true if method can be managed, false otherwise
	 */
	abstract boolean canExecute(String methodName);

	/**
	 * Extracts query form method name
	 * @param methodName method name
	 * @return the query extracted for method name
	 */
	protected abstract String extractQuery(String methodName)

	/**
	 * Parse and tokenize query. Extract all query tokens
	 * @param query the query string
	 * @return the list of tokens (words)
	 */
	protected def tokenize(String query) {

		assert query


		def pQuery = query
		//Replace operator tokens
		LogicalOperators.OPERATORS_TOKEN_MAPPING.each {tok, value ->
			pQuery = pQuery.replace(tok, value)
		}

		//Replace comparator tokens
		Comparators.COMPARATOR_TOKEN_MAPPING.each {tok, value ->
			pQuery = pQuery.replace(tok,value)
		}

		//First letter must start in uppercase
		if (!Character.isUpperCase(pQuery[0] as Character)) {
			throw new EBeanGormException("Property names in method query find, should start by uppercase char")
		}

		//Tokenize query
		def tokens = []
		def lastToken
		StringTokenizer tokenizer = new StringTokenizer(pQuery, "<>", true)
		while (tokenizer.hasMoreTokens()) {
			def token = tokenizer.nextToken("<")

			//If is a reserved word
			if (token == "<") {
				//Search for next
				token = tokenizer.nextToken(">")
				//Consume ">"
				tokenizer.nextToken(">")

				//Compose token
				token = "<$token>" as String

				if (token in LogicalOperators.OPERATORS) {
					lastToken = token
					tokens << token
				} else if (token in Comparators.COMPARATORS) {

					//Last token should exist
					if (!lastToken || lastToken in LogicalOperators.OPERATORS || lastToken in Comparators.COMPARATORS) {
						def e = new EBeanGormException("Operator $token does must be declared after property name")
						log.error "Error parsing query $query", e
						throw e
					}

					//Assign operation to token
					lastToken.operator = token
				}

				continue
			}

			//Convert in token bean
			token = new ComparationToken(fieldName:token)

			//Assign
			lastToken = token
			tokens << token
		}

		return tokens
	}

	/**
	 * Process token list to add argument parameters to comparations
	 * @param tokens the query token list
	 * @param parameters the query parameters
	 * @return the token list with parameters assigned
	 */
	protected def processParameters(def tokens, def parameters) {

		assert tokens

		//Assign parameters
		tokens.each{
			if (it instanceof ComparationToken) {
				Comparators.takeParameters(it, parameters)
			}
		}


		return tokens
	}

	/**
	 * Factory method for search query creation
	 * @param domainClass the domain class to query
	 * @return the eBeans query
	 */
	protected createQuery(def server, Class domainClass) {
		assert server
		assert domainClass

		return server.find(domainClass)
	}


	/**
	 * Process token list for junctions search
	 * @param tokens the token list
	 * @param operator operator for junction type
	 * @return the junctions list
	 */
	protected def parseJunctions(def tokens, def operator) {

		assert tokens

		def conjunctions = []
		def current = []
		tokens.each {
			if (it == operator) {
				conjunctions << current
				current = []
			} else {
				current << it
			}
		}

		//Add last disjunction
		conjunctions << current

		return conjunctions

	}

	/**
	 * Process comparation token generating the query
	 * @param where the expression in process
	 * @param comparation the comparation to process
	 * @return the resulting expression
	 */
	protected def processComparation(def where, def comparation) {

		assert where
		assert comparation

		def operator = comparation.operator
		assert operator

		//Extract method
		def method = Comparators.COMPARATOR_METHOD_MAPPING.get(operator)

		if (!method) {
			def e = new EBeanGormException("Unkown operator $operator")
			log.error "Error parsing eBeans dynamic query", e
			throw e
		}

		//Execute unary
		if (operator in Comparators.UNARY_OPERATORS) {
			return where."$method"(comparation.fieldName)
		}

		//Execute trinary
		if (operator in Comparators.TRINARY_OPERATORS) {
			def values = comparation.value;
			def value1, value2
			if (!(values instanceof Collection)) {
				value1 = values
			} else {
				if (values.size() > 1) {
					value2 = values[1]
				}
				if (values.size() > 0) {
					value1 = values[0]
				}
			}
			return where."$method"(comparation.fieldName, value1, value2)
		}


		//Else is binary...
		return where."$method"(comparation.fieldName, comparation.value)
	}



	/**
	 * Load the query where predicate
	 * @param query the query to load
	 * @param tokens the token list
	 * @return the generated expression list
	 */
	protected def loadQueryWhere(Query query, def tokens) {

		assert query
		assert tokens

		//Retreive the "where"
		def where = query.where()
		assert where

		//Process disjunctions (ORs)
		def disjunctions = parseJunctions(tokens, LogicalOperators.OR)

		//If disjunctions greather than 1, add disjunction directive to where
		if (disjunctions.size() > 1) {
			where = where.disjunction()
		}

		//Process conjunctions (ANDs)
		disjunctions.each {disjunction ->
			def conjunctions = parseJunctions(disjunction, LogicalOperators.AND)
			if (conjunctions.size() > 1) {
				where = where.conjunction()
			}
			conjunctions.each {comparation ->
				where = processComparation(where, comparation[0])
			}
			if(conjunctions.size() > 1) {
				where = where.endJunction()
			}
		}

		return where
	}



	/**
	 * Execute the eEbean query seleting/deleting/update/whatever query mode:unique, list, set, etc.
	 * @param query the generated ebean query or expression list 
	 * @return the query result
	 */
	protected abstract def doQueryExecution(def query, def mappings);

	/**
	 * Execute the query
	 * @param clazz the domain class
	 * @param methodName the method name 
	 * @param params the method argument 
	 * @return the query result
	 */
	def execute(def server, Class clazz, String methodName, def params) {

		assert server
		assert clazz
		assert methodName

		//1 Extract query from method name
		def query = extractQuery(methodName)
		assert query

		//2 Tokenize query
		def tokens = tokenize(query)
		assert tokens

		//3 Process tokens
		tokens = processParameters(tokens, params)

		//4 Create query
		def eQuery = createQuery(server, clazz)
		assert eQuery

		//5 Generate where
		eQuery = loadQueryWhere(eQuery, tokens)

		//6 Extract query directives
		def mappings = [:]
		if (params && (params[-1] instanceof Map)) {
			mappings = params[-1]
		}

		//Process order directives
		eQuery = QueryDirectiveMapper.applySortDirectives(eQuery, mappings)

		//7.- Execute and return
		return doQueryExecution(eQuery, mappings)
	}


}

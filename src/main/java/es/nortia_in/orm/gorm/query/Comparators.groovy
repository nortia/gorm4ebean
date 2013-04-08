package es.nortia_in.orm.gorm.query


/**
 * Constant class for condition comparators
 * 
 * These are:
 * 
 * InList - In the list of given values
	LessThan - less than the given value
	LessThanEquals - less than or equal a give value
	GreaterThan - greater than a given value
	GreaterThanEquals - greater than or equal a given value
	Like - Equivalent to a SQL like expression
	Ilike - Similar to a Like, except case insensitive
	NotEqual - Negates equality
	Between - Between two values (requires two arguments)
	IsNotNull - Not a null value (doesn't require an argument)
	IsNull - Is a null value (doesn't require an argument)

 * @author angel
 *
 */
class Comparators {

	public static final String IN_LIST = "<il>"
	public static final String LESS_THAN = "<lt>"
	public static final String LESS_EQUALS = "<le>"
	public static final String GREATHER_THAN = "<gt>"
	public static final String GREATHER_EQUALS = "<ge>"
	public static final String LIKE = "<l>"
	public static final String ILIKE = "<i>"
	public static final String NOT_EQUAL = "<ne>"
	public static final String EQUAL = "<e>"
	public static final String BETWEEN = "<b>"
	public static final String NOT_NULL = "<nn>"
	public static final String IS_NULL = "<n>"
	
	public static final def COMPARATORS = [IN_LIST, LESS_THAN, LESS_EQUALS,
		GREATHER_THAN, GREATHER_EQUALS, LIKE, ILIKE, NOT_EQUAL,  EQUAL, BETWEEN,
		NOT_NULL, IS_NULL]
	
	public static final def UNARY_OPERATORS = [NOT_NULL, IS_NULL]
	
	public static final def TRINARY_OPERATORS = [BETWEEN] 
	
	public static final def LIST_OPERATORS = [IN_LIST]
	
	/**
	 * Comparator mapping for query tokens
	 */
	public static final def COMPARATOR_TOKEN_MAPPING = ["InList": IN_LIST, "LessThan": LESS_THAN, 
		"LessEqualsThan": LESS_EQUALS, "GreaterThan":GREATHER_THAN, "GreaterEqualsThan": GREATHER_EQUALS,
		"Like": LIKE, "Ilike":ILIKE, "Equal":EQUAL, "NotEqual":NOT_EQUAL, "Between":BETWEEN, "IsNotNull":NOT_NULL, "IsNull":IS_NULL]
	
	/**
	 * Comparator mapping for ebean query methods
	 */
	public static final def COMPARATOR_METHOD_MAPPING = [:]
		
	//Initialize comparators mapping
	static  {
		COMPARATOR_METHOD_MAPPING.put(IN_LIST, "in")
		COMPARATOR_METHOD_MAPPING.put(LESS_THAN, "lt")
		COMPARATOR_METHOD_MAPPING.put(LESS_EQUALS, "le")
		COMPARATOR_METHOD_MAPPING.put(GREATHER_THAN, "gt")
		COMPARATOR_METHOD_MAPPING.put(GREATHER_EQUALS, "ge")
		COMPARATOR_METHOD_MAPPING.put(LIKE, "like")
		COMPARATOR_METHOD_MAPPING.put(ILIKE, "ilike")
		COMPARATOR_METHOD_MAPPING.put(NOT_EQUAL, "ne")
		COMPARATOR_METHOD_MAPPING.put(EQUAL, "eq")
		COMPARATOR_METHOD_MAPPING.put(BETWEEN, "between")
		COMPARATOR_METHOD_MAPPING.put(NOT_NULL, "isNotNull")
		COMPARATOR_METHOD_MAPPING.put(IS_NULL, "isNull")
	}
	
	
	/**
	 * Retrieve needed parameters for a given token operator and assign them to given token.
	 * Unary operators should take no parameters, binary should take one and trinary two
	 * @param token the token to process
	 * @param parameters parameter list
	 * @return the token
	 */
	public static def takeParameters(def token, def parameters) {
		assert token
		
		if (token.operator in TRINARY_OPERATORS) {
			def param1 = parameters ? parameters.remove(0) : null
			def param2 = parameters ? parameters.remove(0) : null
			token.value = [param1, param2]
			return
		}
		
		if (token.operator in UNARY_OPERATORS) {
			return
		}
		
		//If is list operator and param is not a list, take complete parameter list as param list
		if (token.operator in LIST_OPERATORS) {
			def param = parameters ? parameters[0] : null
			if (!(param instanceof Collection)) {
				param = [] + parameters
				parameters.clear()
			}
			token.value = param
		}
		
		//Else are binary operator
		def param = parameters ? parameters.remove(0) : null
		token.value = param
		
		return token
	}
	
}

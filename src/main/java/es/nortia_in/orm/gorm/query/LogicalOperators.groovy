package es.nortia_in.orm.gorm.query


/**
 * Constant list for logical operators
 * @author angel
 *
 */
class LogicalOperators {

	/**
	 * And operator
	 */
	public static final String AND = "<a>"

	/**
	 * Or operator
	 */
	public static final String OR = "<o>"
	
	
	/**
	 * Correspondence between query text and operation tokens
	 */
	public static final def OPERATORS_TOKEN_MAPPING = ["And":AND, "Or":OR]
	

	/**
	 * List of known operators
	 */
	public static final def OPERATORS = [AND, OR]
}

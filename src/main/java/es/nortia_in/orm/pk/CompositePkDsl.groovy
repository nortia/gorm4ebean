package es.nortia_in.orm.pk


import javax.persistence.PersistenceException

import es.nortia_in.orm.gorm.EBeanGormException



/**
 * Parser for composite PK definition DSL.
 * 
 * Allows DSL processing for multifield PK composition.
 * 
 * A composite PK can be composed by one or many of the following elements:
 * 
 *  - Constant: a constant value.
 *  - Property: a dynamic value generated from any domain object property value.
 *  Value can be formatted.
 *  - Dynamic: called by execution of a given closure
 * 
 * This parser IS NOT THREAD SAFE!
 * 
 * @author angel
 *
 */
class CompositePkDsl {


	/**
	 * The list of generated components
	 */
	protected def components = []
	
	/**
	 * Context for actual closure processing
	 */
	protected def context = [:]
	
	/**
	 * The domain class whose PK is generating
	 */
	Class clazz

	CompositePkDsl() {
		super()
	}

	/**
	 * Utility method to execute the given closure
	 * @param c the closure to execute
	 * @return the closure result
	 */
	protected def execute(Closure c) {
		if (!c) {
			return null
		}

		c.delegate = this
		c.call()
	}

	/**
	 * Parse method. Main method for DSL parsing
	 * @param c the DSL closure
	 * @return the composite PK definition
	 */
	def parse(Closure c) {

		//Init the buffer
		components = []

		//Execute main closure
		execute(c)

		//Return pk components
		return components
	}

	/**
	 * Parse constant value
	 * @param value the constant value
	 */
	protected void constant(String value) {
		components << new PkConstantComponent(value:value)
	}

	/**
	 * Parse entity property value
	 * @param c the closure
	 */
	protected void property(Map context) {

		if (!context) {
			context = [:]
		}
		
		if (!context.type) {
			//Retrieve property
			def prop = findMetaProperty(clazz, context.name)
			context.type = prop ? prop.type : String.class
		}
		
		//If no format is defined, create a default one
		if ((!context.format) && context.length) {
			context.format = "0" * (context.length)
		}

		//Create and initialize
		def pkComponent = new PkPropertyComponent()
		
		context.each {key, value ->
			pkComponent."$key" = value
		}
		
		//Compose pk
		components << pkComponent
	}

	/**
	 * Closure for format definition
	 * @param format the format
	 */
	protected void format(String format) {
		context.format = format
	}
	
	/**
	 * Closure for lenght definition
	 * @param lenght the length
	 */
	protected void length(int length) {
		context.length = length
	}
	
	/**
	 * Closure for type definition
	 * @param type the type
	 */
	protected void type(Class type) {
		context.type = type
	}
	
	/**
	 * Closure for dynamic closure definition
	 * @param c the closure with dynamic logic
	 */
	protected void closure(Closure c) {
		context.closure = c	
	}
	
	/**
	 * Generate a piece of pk by executing a closure.
	 * The closure should receive the entity as parameter
	 * @param c the closure to execute.
	 */
	protected void dynamic(Closure c) {

		//Init context
		context = [type:String.class]
		
		if (c == null) {
			throw new EBeanGormException("Dynamic Pk property must define a closure")
		}
		
		//Call the inner closure
		execute(c)

		///Create and initialize
		def pkComponent = new PkDynamicComponent()
		context.each {key, value ->
			pkComponent."$key" = value
		}
		
		//make the composition
		components << pkComponent
	}
	
	/**
	 * Retrieve a MetaProperty for a given class. The given meta property name can be a composite name, joined by "."
	 * @param clazz the class whose property are wanted
	 * @param propertyName the property name to search. Can be a composite property 
	 */
	protected MetaProperty findMetaProperty(Class clazz, String propertyName) {
		assert propertyName
		
		//If no cazz defined, nothing to do
		if (!clazz) {
			return null
		}
		
		//Split property chain
		def propertyChain = propertyName.split("\\.")
		
		//Retrieve first
		propertyName = propertyChain[0]
		def prop = clazz.metaClass.properties?.find{it.name == propertyName}
		
		//If property does not exist...
		if (!prop) {
			throw new PersistenceException("Cannot create composite key. Unknown property $propertyName in class $clazz")
		}
		
		//If there are no more composition in chain. return property found
		if (propertyChain.size() == 1) {
			return prop
		}
		
		//Else, repeat...
		return findMetaProperty(prop.type, propertyChain[1..-1].join("."))
	}


}

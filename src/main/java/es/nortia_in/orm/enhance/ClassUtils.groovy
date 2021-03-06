package es.nortia_in.orm.enhance

import groovy.lang.MetaClass;

import org.codehaus.groovy.classgen.Verifier
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

/**
 * Utility method compendium for domain class management
 *
 * @author sgarcia
 * @author angel
 *
 */
class ClassUtils {

	private static final Logger log = LoggerFactory.getLogger(ClassUtils)


	/**
	 * Register a MetaClass for a given class. This new metaclass can be
	 * add new synthetic methods as well new logic
	 * @param clazz the class to enhance
	 */
	static void registerMetaClass(Class clazz, MetaClass metaClass) {
		MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
		assert registry

		registry.setMetaClass(clazz, metaClass)
	}

	/**
	 * Factory method to generate expando meta class for a given domain class
	 * @param clazz the domain class to enhance
	 * @return expando meta class ready to enhance
	 */
	static MetaClass getExpandoMetaClass(Class clazz) {
		
		ensureClassIntialization(clazz)
		
		MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
		assert (registry.getMetaClassCreationHandler() instanceof ExpandoMetaClassCreationHandle) : "Openfidelia requires an instance of [ExpandoMetaClassCreationHandle] to be set in Groovy's MetaClassRegistry!";
		MetaClass mc = registry.getMetaClass(clazz);
		AdaptingMetaClass adapter = null;
		if(mc instanceof AdaptingMetaClass) {
			adapter = (AdaptingMetaClass) mc;
			mc= ((AdaptingMetaClass)mc).getAdaptee();
		}

		if(!(mc instanceof ExpandoMetaClass)) {
			// removes cached version
			registry.removeMetaClass(clazz);
			mc= registry.getMetaClass(clazz);
			if(adapter != null) {
				adapter.setAdaptee(mc);
			}
		}
		assert (mc instanceof ExpandoMetaClass) : "BUG! Method must return an instance of [ExpandoMetaClass]!";
		return mc;
	}
	
	/**
	 * Ensure that any clint blocks are called before class registration. If do not so, clint will fail due to class InitializationException.
	 * To force clint calling, this method only have to request any class property. I.e: <class>Verifier.STATIC_METACLASS_BOOL</class> 
	 * @param clazz the class to be initialized
	 */
	private static void ensureClassIntialization(Class clazz) {
		try {
			clazz."$Verifier.STATIC_METACLASS_BOOL"
		} catch (MissingPropertyException e) {
		}
	}
	
	/**
	 * Remove actual metaclass and register a new empty one
	 * @param clazz the clazz to be cleaned
	 */
	static void clearMetaClass(Class clazz) {
		assert clazz
		
		MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
		assert (registry.getMetaClassCreationHandler() instanceof ExpandoMetaClassCreationHandle) : "Openfidelia requires an instance of [ExpandoMetaClassCreationHandle] to be set in Groovy's MetaClassRegistry!";
		
		registry.removeMetaClass(clazz)
	}

	/**
	 * Retrieve a property from a given object. The given property name can be a property chain: property names joined by ".".
	 * This utiltiy method can navigate property chain to retrieve the final property value.
	 * I.e: object.foo.bar.tee
	 *
	 * @param object the object to retrieve property
	 * @param propertyChain the property chain to navigate
	 * @return the result or null if no value are found
	 */
	static def getProperty(def object, String propertyChain) {
		assert object != null
		assert propertyChain

		//Process "." to access complex objects properties
		def propChain = propertyChain.split('\\.')

		//Initialize entity result
		def result = object

		//Iterate property chain to access final value
		propChain.each {propName ->
			result = result."$propName"
		}

		return result
	}
	
	/**
	 * Retrieve all generic interfaces from a given class. 
	 * Return defined class interfaces and all superclasses generic interfaces as well.
	 * @param clazz the class whose interfaces are requested
	 * @return a collection with all generic interfaces
	 */
	static def getGenericInterfaces(Class clazz) {
		assert clazz
		def interfaces = [] as Set
		while (clazz != Object) {
			interfaces.addAll(clazz.getGenericInterfaces())
			clazz = clazz.superclass
		}
		return interfaces
		
	}
	
}

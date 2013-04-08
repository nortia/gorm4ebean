package es.nortia_in.orm.annotations

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

/**
 * Worker class to process and check {@see Domain} annotations.
 * Domain annotations will be processed and it's constraints validated against implementing domain classes.
 *  
 * If domain class doesn't meet the requirements specifyed by @Domain annotation, an {@see DomainClassValidationException}
 * will be thrown 
 *  
 * @author angel
 *
 */
class DomainAnnotationsProcessor {


	/**
	 * Retrieve domain related annotations defined directly over given class
	 * @param domainClass the domain class to search into
	 * @return the domain related annotation list
	 */
	def getDeclaredDomainAnnotations(Class domainClass) {
		assert domainClass

		//Retrieve all domain class annotations
		Annotation[] classAnnotations = domainClass.getAnnotations();

		//Filter to search only annotations annotated with @Domain annotation
		classAnnotations.findAll {annotation->
			annotation.annotationType().getAnnotation(Domain)
		}
	}


	/**
	 * Retrieve all domain related annotations. Search in given class, superclasses, interfaces and superinterfaces
	 * @param domainClass the domain class to search into
	 * @return the domain related annotation list
	 */
	def getDomainAnnotations(Class domainClass) {
		assert domainClass

		def result = [] as Set

		//Add to result declared annotations
		result += getDeclaredDomainAnnotations(domainClass)

		//Add superclass annotations
		def superclass = domainClass.getSuperclass()
		while (superclass != Object) {
			result += getDomainAnnotations(superclass)
			superclass = superclass.getSuperclass()
		}

		//Add interface annotations
		def interfaces = domainClass.getInterfaces()
		interfaces?.each {it ->
			result += getDomainAnnotations(it)
		}

		return result as List
	}

	/**
	 * Utility method to check domain annotation REQUIRED methods against a given domain class
	 * @param domainClass the domain class to check
	 * @param annotation  the domain annotation to check
	 */
	protected void checkDomainAnnotation(Class domainClass, def annotation) {
		assert domainClass
		assert annotation

		def methodDefinition = annotation.REQUIRED()
		if (!methodDefinition) {
			return
		}

		//If methodDefinition is String, parse it
		def requiredMethods = methodDefinition
		if (requiredMethods instanceof String) {
			requiredMethods = methodDefinition.split(",")
		}

		def methods = domainClass.getDeclaredMethods()
		requiredMethods.each {methodName->

			//Trim method name
			methodName = methodName.trim()

			//Process static
			boolean staticRequired = methodName.startsWith("static")
			if (staticRequired) {
				methodName = (methodName - "static").trim()
			}

			//Find method
			def method = methods.find{it.name == methodName && (Modifier.isStatic(it.modifiers) ? staticRequired : !staticRequired)}

			if (!method) {
				throw new DomainClassValidationException("Domain class $domainClass must implement required method: $methodName")
			}
		}
	}

	/**
	 * Return all interfaces implemented by given class, annotated with @Domain 
	 * @param domainClass the class whose interfaces are wanted
	 * @return the interface list
	 */
	def getAnnotatedInterfaces(Class domainClass) {
		assert domainClass

		def result = []

		//End recursivity
		if (domainClass == Object.class) {
			return result
		}

		//If is an interface...
		if (domainClass.isInterface()) {
			if (domainClass.getAnnotation(Domain)){
				result << domainClass
			}
		}

		//Get all interfaces
		def interfaces =  domainClass.getInterfaces()
		interfaces.each {
			result.addAll(getAnnotatedInterfaces(it))
		}

		//Get superclass
		def superclass = domainClass.superclass
		if (superclass) {
			result.addAll(getAnnotatedInterfaces(superclass))
		}

		return result
	}

	/**
	 * Check domain annotations. Validate class applying domain annotation defined constraints
	 * @param domainClass
	 */
	void checkAnnotations(Class domainClass) {
		assert domainClass

		//Retrieve and check @Domain annotation defined on interfaces
		def annotatedInterfaces = getAnnotatedInterfaces(domainClass)
		annotatedInterfaces.each {inter ->
			def domainAnnotation = inter.getAnnotation(Domain)
			assert domainAnnotation
			checkDomainAnnotation(domainClass, domainAnnotation)
		}

		//Retrieve and check class domain annotations
		def domainAnnotations = getDeclaredDomainAnnotations(domainClass)
		domainAnnotations.each {annotation ->

			//Extract domain annotation
			def domainAnnotation = annotation.annotationType().getAnnotation(Domain)
			assert domainAnnotation

			//Check the annotation
			checkDomainAnnotation(domainClass, domainAnnotation)

			//Validate superclass annotations
			def superclass = domainClass.superclass
			if (superclass != Object) {
				checkAnnotations(superclass)
			}
		}

	}

}

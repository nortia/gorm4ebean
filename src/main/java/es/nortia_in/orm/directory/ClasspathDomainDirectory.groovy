package es.nortia_in.orm.directory

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.avaje.ebean.event.BeanPersistListener;

import es.nortia_in.orm.annotations.TransientEntity



/**
 * {see DomainDirectory} implementation which searches domain classes
 * inside application classpath
 * @author angel
 *
 */
class ClasspathDomainDirectory extends DomainDirectorySupport {

	/**
	 * Base package for model classes
	 */
	String basePackage = ""
	
	/**
	 * Cached domain classes
	 */
	protected def cachedDomainClasses = []

	/**
	 * Cached <code>Embeddable</code> classes.
	 */
	protected def cachedEmbeddableClasses = []
	
	/**
	 * Cached list of bean persist listeners {@link BeanPersistListener}
	 */
	protected def cachedPersistListeners = []

	/**
	 * Utility method to find all classes with given constraints
	 * @param basePackageName the base package for search
	 * @param baseClass the required superclass (optional)
	 * @param annotations annotation list that should be mnatched (optional)
	 * @return the classes found
	 */
	protected def findClasses(String basePackageName, Class baseClass, def annotations) {
		
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		
		//Add annotation filters
		annotations.each {
			scanner.addIncludeFilter(new AnnotationTypeFilter(it));
		}
		
		//Add superclass filter
		if (baseClass && baseClass != Object.class) {
			scanner.addIncludeFilter(new AssignableTypeFilter(baseClass))
		}
		
		
		//Retrieve all candidate classes
		def candidates = scanner.findCandidateComponents(basePackageName)
		
		//Collect classes from bean definitions
		candidates = candidates.collect {Class.forName(it.beanClassName)}
		
		//Filter candidates by base class
		if (baseClass && baseClass != Object.class) {
			candidates = candidates.findAll{baseClass.isAssignableFrom(it)}
		}
		
		return candidates
		
		
	}
	

	@Override
	public List<Class> getDomainClasses() {
		if (!cachedDomainClasses) {
			cachedDomainClasses = findClasses(basePackage, Object.class, [Entity, TransientEntity])
		}

		return cachedDomainClasses
	}


	@Override
	public List<Class<?>> getEmbeddableClasses() {
		if(!cachedEmbeddableClasses){
			cachedEmbeddableClasses = findClasses(basePackage, Object.class, [Embeddable])
		}
		return cachedEmbeddableClasses;
	}

	@Override
	public List<Class> getPersistListeners() {
		if (!cachedPersistListeners) {
			cachedPersistListeners = findClasses(basePackage, BeanPersistListener.class, [])
		}
		return cachedPersistListeners
	}

	
}

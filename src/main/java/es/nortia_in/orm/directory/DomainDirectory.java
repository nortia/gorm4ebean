package es.nortia_in.orm.directory;

import java.util.List;
import com.avaje.ebean.event.BeanPersistListener;

/**
 * Service interface for query registered domain classes
 * 
 * @author angel
 * 
 */
public interface DomainDirectory {

	/**
	 * Retrieve all domain classes
	 * 
	 * @return list of domain classes
	 */
	@SuppressWarnings("rawtypes")
	List<Class> getDomainClasses();

	/**
	 * Retrieve all domain classes annotated with given annotation class
	 * 
	 * @param annotation
	 *            annotation class to filter
	 * @return all annotated domain classes
	 */
	@SuppressWarnings("rawtypes")
	List<Class> getAnnotatedDomainClasses(Class annotation);

	/**
	 * Retrieve all domain classes that implements the given interface
	 * 
	 * @param interfaze
	 *            The interface class to filter
	 * @return all domain classes that implements the interface
	 * @throws IllegalArgumentException
	 *             if parameter is not an interface type
	 * 
	 */
	@SuppressWarnings("rawtypes")
	List<Class> getImplementingClasses(Class interfaze);
	

	/**
	 * Check if given class is a registered Domain class
	 * 
	 * @param clazz
	 *            the class to check
	 * @return true if given class is domain class, false otherwise
	 */
	@SuppressWarnings("rawtypes")
	boolean isDomainClass(Class clazz);

	/**
	 * Find id property name for a given domain class. This property is defined
	 * with @Id annotation
	 * 
	 * @param domainClass
	 *            the property name to find
	 * @return the id property name or null if no property name is defined
	 */
	@SuppressWarnings("rawtypes")
	String getIdPropertyName(Class domainClass);

	/**
	 * Find version property name for a given domain class. This property is
	 * defined with @Version annotation
	 * 
	 * @param domainClass
	 *            the property name to find
	 * @return the id property name or null if no property name is defined
	 */
	@SuppressWarnings("rawtypes")
	String getVersionPropertyName(Class domainClass);

	/**
	 * 
	 * Find a list of all embedable classes registered in ebeans servers
	 * 
	 * @return the list of embedable classes
	 */
	@SuppressWarnings("rawtypes")
	List<Class> getEmbeddableClasses();

	/**
	 * Find all defined persist listeners
	 * 
	 * @return the persist listener list
	 * @see BeanPersistListener
	 */
	@SuppressWarnings("rawtypes")
	List<Class> getPersistListeners();

	/**
	 * Returns the persistence unit for a given domain class. This property is
	 * defined with @PersistenceUnit annotation.
	 * 
	 * @param domainClass
	 * @return the persistence unit or null if isn't defined
	 */
	@SuppressWarnings("rawtypes")
	String getPersistenceUnit(Class domainClass);

	/**
	 * Returns all domain classes with a <code>PersistenceUnit</code> name
	 * equals to <code>persistenceUnit</code>.
	 * 
	 * @see PersistenceUnit
	 * 
	 * @param persistenceUnit
	 * @return a <code>List</code> with <code>PersistenceUnit</code> name equals
	 *         to <code>persistenceUnit</code>
	 */
	@SuppressWarnings("rawtypes")
	List<Class> getClassesWithPersistenceUnit(String persistenceUnit);
}

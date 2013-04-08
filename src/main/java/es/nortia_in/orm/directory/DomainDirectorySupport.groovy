package es.nortia_in.orm.directory

import java.lang.annotation.Annotation
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Version;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Base class for @{see DomainDirectory} implementations 
 * @author angel
 *
 */
abstract class DomainDirectorySupport implements DomainDirectory{

	/**
	 * The logger
	 */
	protected final Logger log = LoggerFactory.getLogger(DomainDirectorySupport.name)

	/**
	 * Sublcasses should override this method to retrieve the domain classes list
	 * @return
	 */
	abstract List<Class> getDomainClasses();


	@Override
	public List<Class> getAnnotatedDomainClasses(Class annotationClass) {
		
		// retrieves all domain classes
		def classes = getDomainClasses()
		
		classes.findAll {clazz ->

			//If there are annotations defined, check if match
			Annotation[] classAnnotations = clazz.getAnnotations();
			classAnnotations.find{annotation->
				annotationClass.isAssignableFrom(annotation.getClass())?.find{it}
			}
		}
	}
	

	@Override
	List<Class> getImplementingClasses(Class interfaze){
				
		assert interfaze
		
		// checks if parameter is an interface
		if(!interfaze.isInterface()){
			def msg = "Parameter 'interfaze' for getImplementingClasses must be an interface type"
			log.error(msg)
			throw new IllegalArgumentException(msg)
		}
		
		// retrieves all domain classes
		def classes = getDomainClasses()
		
		// returns all domain classes that implements the interface
		classes.findAll{ clazz ->
			interfaze.isAssignableFrom(clazz)
		}
	}

	@Override
	public boolean isDomainClass(Class clazz) {
		return getDomainClasses()?.find{it == clazz}
	}

	/**
	* Find id property name for a given domain class.
	* This property is defined with @Id annotation
	* @param domainClass the property name to find
	* @return the id property name or null if no property name is defined
	*/
   public String getIdPropertyName(Class domainClass) {
	   assert domainClass

	   //Retrieve class fields
	   def fields = domainClass.getDeclaredFields()
	   // Look for primary key annotations (simple or complex)
	   def idProperty = fields.find {
		   it.getAnnotation(Id.class) || it.getAnnotation(EmbeddedId.class)
	   }

	   //If found, return name
	   if (idProperty) {
		   return idProperty.name
	   }

	   //IF not found, search in superclass
	   def superclass = domainClass.superclass
	   if (superclass != Object) {
		   return getIdPropertyName(superclass)
	   }

	   //Else id doesn't exist
	   return null
   }

   /**
	* {@inheritDoc}
	*/
   @Override
   public String getPersistenceUnit(Class domainClass) {
	   // Look for persistence unit annotation
	   PersistenceUnit persistenceUnit = domainClass.getAnnotation(PersistenceUnit.class)
	   if (persistenceUnit && persistenceUnit.name()) {
		   return persistenceUnit.name()
	   }

	   //If not found, search in superclass
	   def superclass = domainClass.superclass
	   if (superclass != Object) {
		   return getPersistenceUnit(superclass)
	   }

	   //Else persistence unit doesn't exist
	   return null
   }

   /**
	* {@inheritDoc}
	*/
   @Override
   public List<Class> getClassesWithPersistenceUnit(String persistenceUnit) {

	   if(!persistenceUnit){
		   // classes without persistence unit are those with no PersistenceUnit annotation
		   getDomainClasses().minus(getAnnotatedDomainClasses(PersistenceUnit.class))
	   }else{
		   getAnnotatedDomainClasses(PersistenceUnit.class).findAll{
			   	getPersistenceUnit(it) == persistenceUnit}
	   }

   }

   /**
	* Find version property name for a given domain class.
	* This property is defined with @Version annotation
	* @param domainClass the property name to find
	* @return the id property name or null if no property name is defined
	*/
   public String getVersionPropertyName(Class domainClass) {
	   assert domainClass

	   //Retrieve class fields
	   def fields = domainClass.getDeclaredFields()
	   // Look for property with @Version annotated
	   def versionProperty = fields.find {
		   it.getAnnotation(Version.class)
	   }

	   //If found, return name
	   if (versionProperty) {
		   return versionProperty.name
	   }

	   //IF not found, search in superclass
	   def superclass = domainClass.superclass
	   if (superclass != Object) {
		   return getVersionPropertyName(superclass)
	   }

	   //Else version doesn't exist
	   return null
   }

}

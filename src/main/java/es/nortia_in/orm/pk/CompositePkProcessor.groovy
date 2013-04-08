package es.nortia_in.orm.pk

import java.lang.reflect.Modifier;

import javax.persistence.PersistenceException;

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import es.nortia_in.orm.enhance.ClassUtils


/**
 * Hook to compose and assign composite PK to a given entity.
 * 
 * Injects the following methods to compositePk closure:
 * 
 * - validateKeyFormat: To validate a given String pk
 * - generatePk: To generate the composite pk for a given domain entity
 * @author angel
 *
 */
class CompositePkProcessor {
	
	private static final Logger log = LoggerFactory.getLogger(CompositePkProcessor)
	
	/**
	 * Compute composite PK for a given domain entity
	 * @param entity the entity which PK will be computed
	 * @return the generated pk
	 */
	Serializable computePk(def entity) {
		
		def pkDescriptor = findPkDescriptor(entity.getClass())
		if (!pkDescriptor) {
			return
		}
		
		//Generate PK
		pkDescriptor.generatePk(entity)
	}

	/**
	 * Process a domain class to enhance with composition (if applies)
	 * @param clazz the domain class to enhance
	 */
	void process(Class clazz) {
		
		assert clazz != null
		
		//Check if this clazz has a PK
		def pkDescriptor = findPkDescriptor(clazz)
		
		//If not has a pk descriptor, no composite pk is defined...
		if (pkDescriptor == null) {
			return
		}
		
		assert (pkDescriptor instanceof Closure)
		
		//Generate the pk composing elements
		def components = new CompositePkDsl(clazz:clazz).parse(pkDescriptor)
		if (!components) {
			return
		}
		
		//Enhance descriptor
		def mc = ClassUtils.getExpandoMetaClass(pkDescriptor.class) 
		pkDescriptor.metaClass = mc
		
		//Enhance validation closure
		mc.validateKeyFormat = {String key ->
			def comps = []+components
			while (comps) {
				def component = comps.remove(0)
				key = component.validate(key)
				
				//If do not validate the component...
				if (key == null) {
					return false
				}
			}
			
			//If there are more unvalidated characters...
			return (key == "") ? true : false
		}
		
		mc.generatePk = {entity ->
			try {
				StringBuffer buff = new StringBuffer()
				components.each {
					buff.append(it.getValue(entity))
				}
				return buff.toString()
			} catch (Exception e) {
				log.error "Error generating composite PK for entity $entity", e
				throw new PersistenceException(e)
			}
		}	
	}
	
	/**
	 * Find the entity PK descriptor. If entity class has no PK descriptor
	 * search in superclass recursively.
	 * 
	 * If entity has no composite PK, the PK descriptor should be null
	 * @param clazz the class to search
	 * @return the pk descriptor
	 */
	protected def findPkDescriptor(Class clazz) {
		
		assert clazz
		
		//Object has no composite PK
		if (clazz == Object.class) {
			return null
		}
		
		//Find composite pk field
		def descriptor = clazz.declaredFields.find { 
			it.name == 'compositePk' && Modifier.isStatic(it.modifiers)
		}
		
		//If found, Return descriptor
		if (descriptor) {
			return clazz."$descriptor.name"
		}
		
		//Search in superclass
		return findPkDescriptor(clazz.superclass)
	}
	
}

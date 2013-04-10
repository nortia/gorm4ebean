package es.nortia_in.orm.enhance

import es.nortia_in.orm.directory.DomainDirectory;
import es.nortia_in.orm.gorm.EBeanGormException;
import es.nortia_in.orm.pk.CompositePkProcessor;
import groovy.lang.MetaClass;
import groovy.util.logging.Slf4j;


/**
 * Enhance given domain class with default ORM related properties such as:
 * primary key, version or persistence unit.
 * 
 * Enhanced domain classes are provided with:
 * <ol>
 * <li>static getPersistUnit() method. To retrieve the persistence unit for domain class storage</li>
 * <li>pk property. To retrieve the pk field name. (The field annotated with @Id)</li>
 * <li>getId() method. To retrieve the domain entity's primary key value</li>
 * <li>getLastUpdateField() method. To retrieve the entity version. Entity version should be @Version annotated field value</li>
 * <li>getVersion() method. To retrieve entity's actual version</li> 
 * </ol>
 * @author angel
 *
 */
@Slf4j
class BasicPropertiesEnhancer implements DomainClassEnhancer{

	public static final String GET_PERSISTENCE_UNIT_METHOD_NAME = "getPersistenceUnit"

	public static final String ID_FIELD_NAME = "id"

	public static final String GET_ID_METHOD_NAME = "getId"

	public static final String GET_PK_METHOD_NAME = "getPk"

	public static final String GET_VERSION_FIELD_METHOD_NAME = "getLastUpdateField"
	
	public static final String GET_VERSION_METHOD_NAME = "getVersion"

	/**
	 * The domain directory for domain class quering
	 */
	DomainDirectory domainDirectory

	/**
	 * Perform the enhancing over given class and meta class
	 * @param metaClass the meta class to enhance
	 * @param clazz the domain class to be enhanced
	 */
	void enhance(MetaClass metaClass, Class clazz) {
		assert metaClass
		assert clazz

		//Annotate persistenceUnit property access
		enhancePersistenceUnit(metaClass, clazz)

		//Annotate id property access
		enhanceId(metaClass, clazz)

		//Annotate version property access
		enhanceVersion(metaClass, clazz)

		//Add pk composition annotation
		enhancePkComposition(clazz)
	}

	/**
	 * Enhance class to add static persistence unit property
	 * @param metaClass the metaClass to be enhanced
	 * @param clazz the domain class to be enhanced
	 */
	protected void enhancePersistenceUnit(def metaClass, def clazz) {

		assert metaClass
		assert clazz

		//If has getter do not enhance...
		if (metaClass.static.methods.find{it.name == GET_PERSISTENCE_UNIT_METHOD_NAME}) {
			return
		}

		//Enhance persistenceUnit property as read only
		metaClass.static."${GET_PERSISTENCE_UNIT_METHOD_NAME}" = {
			domainDirectory?.getPersistenceUnit(clazz) ?: null
		}
	}

	/**
	 * Enhance class to perform persistent ID management
	 * @param metaClass them meta class to enhance
	 * @param clazz the enhnacing class
	 */
	protected void enhanceId(def metaClass, def clazz) {

		assert metaClass
		assert clazz


		//Find PK field
		def idField = domainDirectory?.getIdPropertyName(clazz)
		
		//Enhance  Pk properties as read only
		metaClass.static."$GET_PK_METHOD_NAME" = {idField}
		metaClass."$GET_PK_METHOD_NAME" = {idField}

		//Only enhance if Id property is not defined yet
		if (metaClass.hasProperty(ID_FIELD_NAME)) {
			return
		}

		//If has getter do not enhance...
		if (metaClass.methods.find{it.name == GET_ID_METHOD_NAME}) {
			return
		}

		//Enhance the meta class
		metaClass."$GET_ID_METHOD_NAME" = {

			//Return value
			def fieldName = delegate.getClass()."$GET_PK_METHOD_NAME"()
			if (!fieldName) {
				return null
			}
			return delegate."$fieldName"
		}

	}

	/**
	 * Enhance class to perform persistent Version management
	 * @param metaClass them meta class to enhance
	 * @param clazz the enhancing class
	 **/
	protected void enhanceVersion(def metaClass, def clazz) {

		assert metaClass
		assert clazz

		// Find PropertyName with @Version annotation
		final String versionPropertyName = domainDirectory?.getVersionPropertyName(clazz)

		//Only enhance if has any field annotared with @Version
		if(versionPropertyName){

			//If has getter do not enhance...
			if (metaClass.static.methods.find{it.name ==GET_VERSION_FIELD_METHOD_NAME}) {
				return
			}

			//Enhance version property as read only
			metaClass.static."$GET_VERSION_FIELD_METHOD_NAME" = { versionPropertyName }
			
			//Enhace version if method is not defined
			if (metaClass.methods.find{it.name == GET_VERSION_METHOD_NAME}) {
				return
			}
			
			metaClass."$GET_VERSION_METHOD_NAME" = {
				def fieldName = delegate.getClass()."$GET_VERSION_FIELD_METHOD_NAME"()
				assert fieldName
				return delegate."$fieldName"
			}
		}
	}

	/**
	 * Add to domain class support for composite pk DSL
	 * @param clazz the domain class
	 */
	protected void enhancePkComposition(Class clazz) {
		new CompositePkProcessor().process(clazz)
	}


}

package es.nortia_in.orm.gorm

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.avaje.ebean.bean.EntityBean;

import es.nortia_in.orm.gorm.query.FindAllByMethod
import es.nortia_in.orm.gorm.query.FindByMethod
import es.nortia_in.orm.gorm.query.FindPagedByMethod
import es.nortia_in.orm.gorm.query.QueryDirectiveMapper



/**
 * Implements GORM like methods through EBean API
 * @author angel
 *
 */
class GormLikeMethods {

	private static final Logger log = LoggerFactory.getLogger(GormLikeMethods)

	/**
	 * List of dynamic method handlers
	 */
	private static def DYNAMIC_METHODS = [
		new FindByMethod(),
		new FindAllByMethod(),
		new FindPagedByMethod()
	]

	/**
	 * Gorm-like save() method implementation with ebeans api
	 * @param object the object to save
	 */
	static void save(def server, def object) {
		assert server != null
		assert object != null

		//Save
		server.save(object)
	}

	/**
	 * Gorm-like refresh() method implementation with ebeans api
	 * @param object the object to refresh
	 */
	static void refresh(def server, def object) {
		assert server != null
		assert object != null

		//Refresh. Only if bean is entity bean. (is not vanilla)
		if (!(object instanceof EntityBean)){
			return
		}

		//Do not refresh new objects
		if (object._ebean_getIntercept()?.isNew()) {
			return
		}

		server.refresh(object)
	}

	/**
	 * Gorm-like delete() method implementation with ebeans api
	 * @param object the object to delete
	 * @param clazz the entity class
	 */
	static void delete(def server, def object, def clazz) {
		assert server
		assert object

		//If bean haven't been inserted yet, do nothing
		def state = server.getBeanState(object)
		if (!state || state.isNew()) {
			return
		}

		//Delete
		server.delete(object)

		//Remove from cache
		//Only works if id field is injected. Otherwise, a warning should be listed
		try {
			server.currentTransaction()?.getPersistenceContext()?.clear(clazz, object.id)
		} catch (MissingPropertyException e) {
			log.warn "$object entity cannot be removed from transaction cache . Cannot access id field", e
		}

	}

	/**
	 * Gorm-like list() method implementation using ebeans api
	 * @param clazz the domain class to list
	 * @return entities found
	 */
	static def list(def server, Class clazz, def mappings) {
		assert server
		assert clazz != null

		//Create query
		def query = server.find(clazz)
		assert query

		//Apply mappings
		if (mappings) {
			query = QueryDirectiveMapper.applyQueryDirectives(query, mappings)
		}

		//Query all
		return query.findList()
	}

	/**
	 * Gorm-like count() method implementation using ebeans api
	 * @param clazz
	 * @return
	 */
	static int count(def server, Class clazz) {
		assert server
		assert clazz
		return server.find(clazz).findRowCount()
	}

	/**
	 * Gorm-like get() method implementation using ebeans api
	 * @param clazz the domain class to query
	 * @param id the entity to search id 
	 * @return the found entity or null id no entity was found
	 */
	static def get(def server, Class clazz, def id){
		assert server
		assert clazz

		server.find(clazz, id)
	}

	/**
	 * Gorm-like createCriteria() method implementation using ebeans api
	 * @param clazz the domain class to construct the query for
	 * @return Query class for constructing a query using Eban query language
	 * @See {@link Query}
	 */
	static def createQuery (def server, Class clazz){
		assert server
		assert clazz

		server.createQuery(clazz)
	}


	/**
	 * Gorm-like dynamic method (finder or not) implementation using ebeans api
	 * @param clazz the domain class to query
	 * @param methodName the dynamic method name
	 * @param arguments the method arguments
	 * @return the query result
	 */
	static def dynamicMethod(def server, Class clazz, String methodName, def arguments) {
		assert server
		assert clazz
		assert methodName

		//Find method to execute
		def method = DYNAMIC_METHODS.find{it.canExecute(methodName)}

		//If no method, throw exception
		if (!method) {
			throw new MissingMethodException(methodName, clazz, arguments)
		}

		//Never send null parameters
		if (arguments == null) {
			arguments = []
		}

		//Package args in a collection
		if (!arguments instanceof Collection) {
			arguments = [arguments]
		}

		return method.execute(server, clazz, methodName, arguments as List)
	}

	/**
	 * Gorm-like isDirty() method. Check if given entity is dirty
	 * @param object the object to be checked
	 * @return true if object is a dirty entity, false otherwise
	 */
	static boolean isDirty(def object) {

		assert object

		//Not entities are ever dirties
		if (!(object instanceof EntityBean)){
			return true
		}

		return object._ebean_getIntercept()?.isNewOrDirty()
	}

	/**
	 * Gorm-like isSynch() method. Check if given entity has not pending changes to database. 
	 * @param object the object to be checked
	 * @return true if object is a dirty entity, false otherwise
	 */
	static boolean isSynch(def object) {
		return !isDirty(object)
	}

	/**
	 * Gorm-like isNew() method. Check if given entity has not been persisted to database
	 * @param object the object to be checked
	 * @return true if object is a new entity, false otherwise
	 */
	static boolean isNew(def object) {
		assert object

		//Not entities are ever dirties
		if (!(object instanceof EntityBean)){
			return true
		}

		return object._ebean_getIntercept()?.isNew()
	}


	/**
	 * Gorm-like isDirty(String propertyName) method. Check if given entity's property is dirty
	 * @param object the object to be checked
	 * @param propertyName the property to be checked
	 * @return true if property is dirty, false otherwise
	 */
	static boolean isDirtyProperty(def object, String propertyName) {

		assert object
		return propertyName in getDirtyPropertyNames(object)
	}

	/**
	 * Gorm-like getDirtyPropertyNames() method. Retrieve all entity updated properties
	 * @param object the entity to be checked
	 * @return the collection of dirty property names
	 */
	static Set<String> getDirtyPropertyNames(def object) {

		assert object

		if (!(object instanceof EntityBean)) {
			return []
		}

		return object._ebean_getIntercept()?.getChangedProps() ?: []

	}

	/**
	 * Gorm-like getPersistentValue() method. Retrieve the persistent value of a given property.
	 * @param object the entity whose persistent value should be recovered
	 * @param propertyName the property name to be recovered
	 * @return the property's persistent value
	 */
	static def getPersistentValue(def object, String propertyName) {

		assert object

		if (!propertyName) {
			return null
		}

		if (!(object instanceof EntityBean)) {
			return null
		}

		return object._ebean_getIntercept()?.getOldValues()?."$propertyName"

	}
}

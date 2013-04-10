package es.nortia_in.orm.controllers


import com.avaje.ebean.event.BeanPersistRequest


/**
 * Intercept bean life-cycle (preInsert,preDelete, onLoad, postInsert, and so on) events and route them to
 * the affected bean that can handle them properly
 * 
 * Events will be routed to GORM-like methods:
 * <ul>
 * <li>beforeInsert</li>
 * <li>beforeUpdate</li>
 * <li>beforeDelete</li>
 * <li>afterInsert</li>
 * <li>afterUpdate</li>
 * <li>afterDelete</li>
 * <li>onLoad</li>
 * </ul>
 * 
 * @author angel
 *
 */
class BeanLifecycleHook extends BaseHookSupport {


	@Override
	public boolean isRegisterFor(Class<?> cls) {
		//Apply over every entity class
		return true
	}
	
	/**
	 * Utility method to execute a hook method over persisting bean.
	 * If method doesn't be defined for persisting bean, nothing will be done
	 * @param request the bean persist request in process
	 * @param methodName hook method name to be executed.
	 */
	protected void executeHook(BeanPersistRequest<?> request, String methodName) {
		
		assert request
		assert methodName
		
		def bean = request.bean
		
		//If no bean is persisting, return
		if (bean == null) {
			return
		}
		
		//Execute method for a bean
		executeHook(bean, methodName)
		
		return
		
	}

	/**
	* Utility method to execute a hook method over persisting bean.
	* If hook method doesn't be defined for persisting bean, nothing will be done
	* @param bean the bean in persisting process
	* @param methodName method name to be executed.
	*/
   protected void executeHook(Object bean, String methodName) {
	   
	   assert bean != null
	   assert methodName
	  
	   def mc = bean.getMetaClass()
	   assert mc
	   if (mc.respondsTo(bean, methodName)) {
		   bean."$methodName"()
	   }
	   
	   return
	   
   }
	
	@Override
	public boolean preInsert(BeanPersistRequest<?> request) {
		
		executeHook(request, "beforeInsert")
		
		return true
	}

	@Override
	public boolean preUpdate(BeanPersistRequest<?> request) {
		executeHook(request, "beforeUpdate")
		return true
	}

	@Override
	public boolean preDelete(BeanPersistRequest<?> request) {
		executeHook(request, "beforeDelete")
		return true
	}

	@Override
	public void postInsert(BeanPersistRequest<?> request) {
		executeHook(request, "afterInsert")
	}

	@Override
	public void postUpdate(BeanPersistRequest<?> request) {
		executeHook(request, "afterUpdate")
	}

	@Override
	public void postDelete(BeanPersistRequest<?> request) {
		executeHook(request, "afterDelete")		
	}

	@Override
	public void postLoad(Object bean, Set<String> includedProperties) {
		
		if (bean == null) {
			return
		}
		
		executeHook(bean, "onLoad")
	}

}

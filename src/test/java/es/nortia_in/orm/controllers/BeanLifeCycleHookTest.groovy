package es.nortia_in.orm.controllers;

import org.junit.Test;
import static org.junit.Assert.*
import MODEL.EAlmacen

class BeanLifeCycleHookTest extends BeanControllerBaseTest {

	/**
	 * The hook to be tested
	 */
	BeanLifecycleHook hook = new BeanLifecycleHook()
	
	@Test
	void shouldExecuteDefinedCallbackMethods() {
		
		//Create an entity which implements any callback method
		def entity = new EAlmacen(codigo_interno:"foo", descripcion:"foo")
		
		//Execute the hook
		assert hook
		hook.preInsert(wrapEntity(entity))
		
		//BeforeInsert entity method should be executed and description updated
		assertEquals "foo@@@", entity.descripcion
		
	}


}

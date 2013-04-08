package es.nortia_in.orm.enhance;

import static org.junit.Assert.*
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import MODEL.EGoods
import es.nortia_in.orm.service.DomainServiceLocator


class DomainServiceEnhancerTest {

	DomainServiceEnhancer enhancer
	
	Class classToEnhance = EGoods.class
	
	@Before
	void setUp() {
		//Enable expando meta classes
		ExpandoMetaClass.enableGlobally();
		
		def serviceLocator = new Expando()
		serviceLocator.getService = {serviceName ->
			return serviceName
		}
		
		enhancer = new DomainServiceEnhancer(serviceLocator:(serviceLocator as DomainServiceLocator))
	}
	
	@After
	void tearDown() {
		//Remove enhancements
		ClassUtils.clearMetaClass(classToEnhance)
	}
	
	@Test
	void shoudlEnhanceDomainClassWithgetServiceMehtod() {
		
		//Enhance EGoods class
		enhancer.enhance(ClassUtils.getExpandoMetaClass(classToEnhance), classToEnhance)
		
		//asService method are injected
		def service = classToEnhance.getService("foo")
		assertEquals "foo", service
		
	}
	
	
}

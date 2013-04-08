package es.nortia_in.orm.enhance;

import static org.junit.Assert.*
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import MODEL.EGoods
import MODEL.ESeccion


class ChainedPropertyAccessorEnhancerTest {

	ChainedPropertyAccessorEnhancer enhancer
	
	Class classToEnhance = EGoods.class
	
	@Before
	void setUp() {
		//Enable expando meta classes
		ExpandoMetaClass.enableGlobally();
		
		//Create the enhacer
		enhancer = new ChainedPropertyAccessorEnhancer()
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
		
		//Create an entity
		def entity = new EGoods(seccion: new ESeccion(seccion:"SEC1"))
		
		//Can navigate to seccion name from entity
		assertEquals "SEC1", entity.retrieve("seccion.seccion")
		
	}
	
	
}

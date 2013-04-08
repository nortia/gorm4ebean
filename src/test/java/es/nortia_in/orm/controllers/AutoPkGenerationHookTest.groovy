package es.nortia_in.orm.controllers;

import java.text.SimpleDateFormat

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*

import MODEL.EAlmacen
import MODEL.MockCPKEntity
import MODEL.MockCPKSubEntity
import es.nortia_in.orm.directory.InlineDomainDirectory
import es.nortia_in.orm.enhance.BasicPropertiesEnhancer
import es.nortia_in.orm.enhance.ClassUtils

class AutoPkGenerationHookTest extends BeanControllerBaseTest {

	static def testClasses = [MockCPKEntity, EAlmacen, MockCPKSubEntity]
	
	@BeforeClass
	static void setUp() {
		
		//Enable expando meta classes
		ExpandoMetaClass.enableGlobally();
		
		//Create a domain directory
		def domainDirectory = new InlineDomainDirectory()
		domainDirectory.setDomainClasses(testClasses)
		
		//Entities with composite PK should be enhanced
		def enhancer = new BasicPropertiesEnhancer(domainDirectory:domainDirectory)
		testClasses.each {
			enhancer.enhance(ClassUtils.getExpandoMetaClass(it), it)
		}
	}
	
	@AfterClass
	static void tearDown() {
		testClasses.each {
			ClassUtils.clearMetaClass(it)
		}
	}
	

	
	@Test
	void shouldAssignCompositePk() {
		
		//Entity to test
		def entity = new MockCPKEntity(foo:"bar", date:new SimpleDateFormat("yy").parse("12"))
		assert !entity.id
		
		//hook to test
		def hook = new AutoPkGenerationHook()
		
		//process the entity class
		hook.preInsert(wrapEntity(entity))
		
		//Generate pk
		assertEquals "0 bar12", entity.id		
	}
	
	@Test
	void shouldNotOverridePk() {
		
		//Entity to test
		def entity = new MockCPKEntity(id:"muid", foo:"bar", date:new SimpleDateFormat("yy").parse("12"))
		assert entity.id
		
		//processor to test
		//hook to test
		def hook = new AutoPkGenerationHook()
		
		//process the entity class
		hook.preInsert(wrapEntity(entity))

		assertEquals "muid", entity.id
	}
	
	@Test
	void shouldNoAssignCompositePkToNotDefinedCompositionPkInEntity() {
		//Entity to test
		def entity = new EAlmacen(codigo_interno:"111")
		
		//hook to test
		def hook = new AutoPkGenerationHook()
		
		//process the entity class
		hook.preInsert(wrapEntity(entity))
		
		//Do nothing
		assertEquals "111", entity.id
	}

	@Test
	void shouldAssignCompositePkDefinedInSuperclass() {
		
		//Entity to test
		def entity = new MockCPKSubEntity(foo:"bar", date:new SimpleDateFormat("yy").parse("12"))
		assert !entity.id
		
		//Entity is enhanced
		assert entity.pk
		
		//hook to test
		def hook = new AutoPkGenerationHook()
		
		//process the entity class
		hook.preInsert(wrapEntity(entity))

		assertEquals "0 bar12", entity.id
		
	}

		
}

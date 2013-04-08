package es.nortia_in.orm.pk;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import es.nortia_in.orm.directory.InlineDomainDirectory
import es.nortia_in.orm.enhance.BasicPropertiesEnhancer
import es.nortia_in.orm.enhance.ClassUtils
import es.nortia_in.orm.pk.CompositePkProcessor;

import MODEL.EAlmacen
import MODEL.MockCPKEntity;
import MODEL.MockCPKSubEntity;

class CompositePkProcessorTest {

		
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
		
		//processor to test
		def processor = new CompositePkProcessor()
		
		//process the entity class
		processor.process(MockCPKEntity)

		//Validate
		assertTrue entity.compositePk.validateKeyFormat("0aabd12")
		assertFalse entity.compositePk.validateKeyFormat("0aabd1d")
		assertFalse entity.compositePk.validateKeyFormat("0aabd12dd")
		
		//Generate pk
		assertEquals "0 bar12", processor.computePk(entity)		
	}
	

	
	@Test
	void shouldNoAssignCompositePkToNotDefinedCompositionPkInEntity() {
		//Entity to test
		def entity = new EAlmacen(codigo_interno:"111")
		
		//processor to test
		def processor = new CompositePkProcessor()
		
		//process the entity class
		processor.process(EAlmacen)
		
		//Do nothing
		assertNull processor.computePk(entity)
	}

	@Test
	void shouldAssignCompositePkDefinedInSuperclass() {
		
		//Entity to test
		def entity = new MockCPKSubEntity(foo:"bar", date:new SimpleDateFormat("yy").parse("12"))
		assert !entity.id
		
		//Entity is enhanced
		assert entity.pk
		
		//processor to test
		def processor = new CompositePkProcessor()
		
		//process the entity class
		processor.process(MockCPKSubEntity)
	
		assertEquals "0 bar12", processor.computePk(entity)
		
	}

		
}

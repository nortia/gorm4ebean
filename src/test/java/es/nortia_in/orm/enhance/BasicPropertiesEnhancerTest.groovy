package es.nortia_in.orm.enhance

import java.sql.Timestamp

import static org.junit.Assert.*
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import MODEL.EAlmacen
import MODEL.EFamilia
import MODEL.EntityWithNoId

import es.nortia_in.orm.directory.DomainDirectory;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:META-INF/spring/test-context-domain-directory.xml")
public class BasicPropertiesEnhancerTest {

	@Autowired
	DomainDirectory domainDirectory;

	/**
	 * The enhancer to test
	 */
	BasicPropertiesEnhancer enhancer;
	
	@Before
	void setUp() {
		//Enable expando meta classes
		ExpandoMetaClass.enableGlobally();
	
		//Clean metaclasses
		domainDirectory.getDomainClasses().each {
			ClassUtils.clearMetaClass(it)
		}
		
		//Create the enhacer to be tested
		enhancer = new BasicPropertiesEnhancer(domainDirectory:domainDirectory)
	}
	
	
	@Test
	void shouldEnhanceIdAndVersion() {
		
		//Retrieve meta class
		def clazz = EFamilia.class
		def metaClass = ClassUtils.getExpandoMetaClass(clazz)
		assert metaClass
		
		//Class does not have a predefined PK field
		try {
			clazz.pk
			fail();
		} catch (e) {
		}
		
		//Enhance the class
		enhancer.enhance(metaClass, clazz)
		
		//Class now has "familia" as pk field
		assertEquals "familia", clazz.pk 
		
		//Now create an entity instance
		def entity = new EFamilia(familia:"codeFoo", timestamp:new Timestamp(13))
		
		//Check PK field name over entity instance
		assertEquals "familia", entity.pk
		
		//Entity has "getId()" method
		assertEquals "codeFoo", entity.id
		
		//...and lastUpdated property
		assertEquals "timestamp", clazz.getLastUpdateField()
		assertEquals 13, entity.version.time
		
		
		
	}
	
	@Test
	void shouldNotEnhanceVersionIfExistsGetVersionMethod() {
	
		//Retrieve meta class
		def clazz = EAlmacen.class
		def metaClass = ClassUtils.getExpandoMetaClass(clazz)
		assert metaClass
			
		//Enhance the class
		enhancer.enhance(metaClass, clazz)
		
		//Create an entity instance
		def entity = new EAlmacen(version:13)
		
		//Check that version are not enhanced
		assertEquals "version", clazz.getLastUpdateField()
		assertEquals 13, entity.version

	}
	
	@Test
	void shouldEnhancePersistenceUnitInEntitiesAttachedToAPU() {
		
		//Retrieve meta class
		def clazz = EFamilia.class
		def metaClass = ClassUtils.getExpandoMetaClass(clazz)
		assert metaClass
		
		//Class does not have a predefined PK field
		try {
			clazz.pk
			fail();
		} catch (e) {
		}
		
		//Enhance the class
		enhancer.enhance(metaClass, clazz)
		
		//PU have been enhanced
		assertEquals "ES_ARTICULOS", clazz.getPersistenceUnit()
		
	}
	
	@Test
	void shouldNotEnhancePUInEntitiesNotAttachedToAnyPU() {
		
		//Retrieve meta class
		def clazz = EAlmacen.class
		def metaClass = ClassUtils.getExpandoMetaClass(clazz)
		assert metaClass
			
		//Enhance the class
		enhancer.enhance(metaClass, clazz)
		
		//No PU have been enhanced
		assertNull clazz.getPersistenceUnit()
		
	}
	
	
}

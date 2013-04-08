package es.nortia_in.orm.directory

import static org.junit.Assert.*

import javax.persistence.Table;

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import es.nortia_in.orm.directory.DomainDirectory;

import MODEL.EAlmacen
import MODEL.EAlmacenWithPU
import MODEL.EFamilia
import MODEL.EGoods
import MODEL.EGoodsWithPU
import MODEL.ESeccion
import MODEL.ESeccionWithPU


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:META-INF/spring/test-context-domain-directory.xml")
public class DomainDirectorySupportTest {

	@Autowired
	DomainDirectory domainDirectory;

	
	@Test
	public void shouldFindAnnotatedClasses() {
		
		//Search for all annotated classes with @Table annotation
		def found = domainDirectory.getAnnotatedDomainClasses(Table.class);
		assert found
		
		//There are 6 annotated classes defined in domain directory
		assertEquals 6, found.size()
	}
	
	@Test
	public void shouldRetrieveIdProperty() {
		
		//Find id property from domain class with @Id 
		assertEquals "codigo_interno", domainDirectory.getIdPropertyName(EAlmacen.class)
		
	}
	
	@Test
	public void shouldNotRetrieveIdProperty() {
		
		//Find no id property in domain class with no defined @Id
		assertNull domainDirectory.getIdPropertyName(ESeccionWithPU.class)
		
	}
	
	@Test
	public void shouldRetrieveIdPropertyInSuperclass() {
		//Find id property from domain class with @Id define in any superclass 
		assertEquals "codigo_interno", domainDirectory.getIdPropertyName(EGoods.class)
		
	}
	
	@Test
	public void shouldRetrieveVersionProperty() {
		
		//Find version property from domain class with @Version
		assertEquals "version", domainDirectory.getVersionPropertyName(EAlmacen.class)

		
	}
	
	@Test
	public void shouldRetrieveVersionPropertyInSuperclass() {
		//Find id property from domain class with @Version define in any superclass 
		assertEquals "version", domainDirectory.getVersionPropertyName(EGoods.class)
		
	}
	
	@Test
	public void shouldCheckDomainClass() {
		
		//EAlmacen is a domain entity class
		assertTrue domainDirectory.isDomainClass(EAlmacen.class)
		
		//...but ArrayList is not
		assertFalse domainDirectory.isDomainClass(ArrayList.class)
	}
	
	@Test
	public void shouldFindDomainClassesImplementingInterface() {
		
		//Find all "Serializable" domain classes
		def found = domainDirectory.getImplementingClasses(Serializable.class)
		assert found
		
		//There are 2 serializable domain classes
		//Note that subclasses are considered
		assertEquals 2, found.size()
	}
	
	
	
	@Test
	public void shouldFindClassesWithNoDefinedPersistenceUnit() {
		List<Class> classesWithoutPersistenceUnit = domainDirectory
				.getClassesWithPersistenceUnit(null);

		assertNotNull(classesWithoutPersistenceUnit);
		assertEquals(4, classesWithoutPersistenceUnit.size());
		assertTrue(classesWithoutPersistenceUnit.contains(EAlmacen.class));
		assertTrue(classesWithoutPersistenceUnit.contains(EGoods.class));
		assertTrue(classesWithoutPersistenceUnit.contains(EGoodsWithPU.class));
		assertTrue(classesWithoutPersistenceUnit.contains(ESeccion.class));
	}

	@Test
	public void shoulFindClassesWithGivenPU(){
		List<Class> classes = domainDirectory
				.getClassesWithPersistenceUnit("ES_ARTICULOS");

		assertNotNull(classes);
		assertEquals(3, classes.size());
		assertTrue(classes.contains(EAlmacenWithPU.class));
		assertTrue(classes.contains(EFamilia.class));
		assertTrue(classes.contains(ESeccionWithPU.class));
		
	}
}

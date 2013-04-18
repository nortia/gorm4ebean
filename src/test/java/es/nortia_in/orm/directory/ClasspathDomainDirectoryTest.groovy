package es.nortia_in.orm.directory

import static org.junit.Assert.*

import javax.persistence.Table;

import org.junit.Before;
import org.junit.Test

import es.nortia_in.orm.directory.ClasspathDomainDirectory;
import es.nortia_in.orm.directory.DomainDirectory;

import MODEL.EAlmacen
import MODEL.EAlmacenWithPU
import MODEL.EFamilia
import MODEL.EGoods
import MODEL.EGoodsWithPU
import MODEL.ESeccion
import MODEL.ESeccionWithPU



public class ClasspathDomainDirectoryTest {

	DomainDirectory domainDirectory;

	@Before
	void setUp() {
		domainDirectory = new ClasspathDomainDirectory(basePackage:"MODEL")
	}
	
	
	@Test
	public void shouldFindDomainClasses() {
		
		//Find domain classes
		def classes = domainDirectory.getDomainClasses()
		
		//There are 12 Entities and 1 TransientEntity
		assertEquals 13, classes.size()
	}
	
	@Test
	public void shouldFindEmbbedableClasses() {
		
		//Find domain classes
		def classes = domainDirectory.getEmbeddableClasses()
		
		//There are 1 Embeddable entity
		assertEquals 1, classes.size()
		
	}
	
	@Test
	public void shouldFindBeanPersistListeners() {
		//Find persist listeners
		def classes = domainDirectory.getPersistListeners()
		
		//There are only 2 listener
		assertEquals 2, classes.size()
	}
	
	@Test
	public void shouldFindClassesWithNoBasePackage() {
		
		//Create domain directory with no base package defined
		def domainDirectory = new ClasspathDomainDirectory()
		
		//Find all domain classes
		def classes = domainDirectory.getDomainClasses()
		
		//There are 12 Entities in MODEL, 2 in example and 1 TransientEntity
		assertEquals 15, classes.size()
		
	}
	
}

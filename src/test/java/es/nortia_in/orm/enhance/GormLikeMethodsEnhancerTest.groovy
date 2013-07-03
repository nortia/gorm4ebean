package es.nortia_in.orm.enhance;

import javax.persistence.PersistenceException

import static org.junit.Assert.*
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import MODEL.EAlmacen
import MODEL.EAlmacenWithPU
import MODEL.EBadWithPU
import MODEL.EClienteWithPU
import MODEL.EGoods
import MODEL.ESeccion

import com.avaje.ebean.EbeanServer;

import es.nortia_in.orm.directory.DomainDirectory;
import es.nortia_in.orm.gorm.EBeanGormException
import es.nortia_in.orm.gorm.query.GormLikeCriteria
import es.nortia_in.test.db.AbstractDbUnitTransactionalJUnit4SpringContextTests;
import es.nortia_in.test.db.DatasetLocation

@RunWith(SpringJUnit4ClassRunner.class)
@DatasetLocation(value="dataset.xml")
@ContextConfiguration(locations = "classpath:META-INF/spring/test-context-pu.xml")
@DirtiesContext
class GormLikeMethodsEnhancerTest  extends AbstractDbUnitTransactionalJUnit4SpringContextTests{

	@Autowired
	DomainDirectory domainDirectory;
	
	GormLikeMethodsEnhancer enhancer
	
	@Autowired
	EbeanServer ES_ARTICULOS
	
	@Autowired
	EbeanServer ES_CLIENTES
	
	@Autowired
	EbeanServer defaultServer
	
	def classesToEnhance = [EAlmacen.class, EGoods.class, ESeccion.class]
	
	@Before
	void setUp() {
		//Enable expando meta classes
		ExpandoMetaClass.enableGlobally();
		
		//Create the enhancer to test
		enhancer = new GormLikeMethodsEnhancer(domainDirectory:domainDirectory)
		
		//enhance the classes
		classesToEnhance.each {clazz ->
			enhancer.enhance(ClassUtils.getExpandoMetaClass(clazz), clazz)
		}
	}
	
	@After
	void tearDown() {
		//Remove enhancements
		classesToEnhance.each {
			ClassUtils.clearMetaClass(it)
		}
	}
	
	@Test
	void shouldRetrieveEntityEbeanServerByPersistenceUnit() {
		
		assertNotNull ES_CLIENTES
		assertNotNull ES_ARTICULOS
		assertNotNull defaultServer
		assert enhancer
		
		assertEquals defaultServer, enhancer.getEbeanServer(EAlmacen.class)
		assertEquals ES_ARTICULOS, enhancer.getEbeanServer(EAlmacenWithPU.class)
		assertEquals ES_CLIENTES, enhancer.getEbeanServer(EClienteWithPU.class)
	}
	
	@Test(expected=EBeanGormException.class)
	void shouldFailRetrievingUndefinedServerForPU() {
		enhancer.getEbeanServer(EBadWithPU.class)
	}

	@Test
	void shouldFindMethod() {

		def found = EAlmacen.findAllByCodigo_interno("00000002")
		assert found

		assertEquals 1, found.size()

		assertEquals "00000002", found[0].codigo_interno
		assertEquals "1881 VIRGEN B.00", found[0].abreviada
	}

	@Test
	void shouldFindMethodWithDisjunction() {

		def found = EAlmacen.findAllByCodigo_internoOrAbreviada ("00000002", "OLIVA")
		assert found
		assertEquals 1, found.size()

		assertEquals "00000002", found[0].codigo_interno
		assertEquals "1881 VIRGEN B.00", found[0].abreviada
	}


	@Test
	void shouldFindMethodWithUnaryOperator() {


		def found = EAlmacen.findAllByDescripcionIsNull()
		assert found

		assertEquals 1, found.size()

		assertEquals "00000222", found[0].codigo_interno

		//Search with sort directive
		found = EAlmacen.findAllByDescripcionIsNotNull([sort:"codigo_interno"])
		assert found

		assertEquals 2, found.size()

		def almacen = found[0]
		assertEquals "00000002", almacen.codigo_interno

		almacen = found[1]
		assertEquals "00000022", almacen.codigo_interno
	}

	@Test
	void shouldOrderDescendantQueries() {


		def found = EAlmacen.findAllByAbreviadaLike("%VIRGEN%", [sort:"codigo_interno", order:"desc"])
		assert found

		assertEquals 3, found.size()

		def almacen = found[0]
		assertEquals "00000222", almacen.codigo_interno

		almacen = found[1]
		assertEquals "00000022", almacen.codigo_interno

		almacen = found[2]
		assertEquals "00000002", almacen.codigo_interno
	}

	@Test
	void shouldListObjects() {

		def objects = EAlmacen.list()
		assert objects
		assertEquals 3, objects.size()
	}

	@Test
	void shouldListObjectsWithOrderSettings() {

		def found = EAlmacen.list([sort:"codigo_interno", order:"desc"])
		assert found

		assertEquals 3, found.size()

		def almacen = found[0]
		assertEquals "00000222", almacen.codigo_interno

		almacen = found[1]
		assertEquals "00000022", almacen.codigo_interno

		almacen = found[2]
		assertEquals "00000002", almacen.codigo_interno
	}

	@Test(expected=PersistenceException.class)
	void shouldFailSaveObjectWithoutPk() {

		def bean = new EAlmacen()
		bean.save()
	}

	@Test
	void shouldSaveObjectAndCountIt() {

		int beans = EAlmacen.count()
		assertEquals 3, beans

		def bean = new EAlmacen(codigo_interno:"111111")
		bean.save()

		assertEquals 4, EAlmacen.count()		
	}

	@Test
	void shouldRetreiveRelatedToMany() {

		//Retrieve seccion
		def seccion = ESeccion.get(2)
		assert seccion

		def products = seccion.products
		assert products

		assertEquals 2, products.size()

	}

	@Test
	void shouldPersistSubclasess() {

		def good = new EGoods(codigo_interno:"11", price:7)
		good.save()

		def almacenes = EAlmacen.list()
		assert 4, almacenes.size()
	}

	@Test
	void shouldRetrieveIdValue() {

		def almacen = new EAlmacen(codigo_interno:"111")
		assertEquals "111", almacen.codigo_interno

		//Also check when id is in superclass
		def goods = new EGoods(codigo_interno:"123")
		assertEquals "123", goods.codigo_interno
	}

	@Test
	void shouldFindEntityUsingQueryLanguage(){
		def query = EAlmacen.createQuery()

		assertNotNull(query)

		assertEquals 1 , query.where().eq("codigo_interno", "00000222").findList().size()
	}
	
	
	@Test
	void shouldDeleteEntity() {
		
		//Create almacen
		def almacen = new EAlmacen(codigo_interno:"111")
		almacen.save()
		
		//Find it
		almacen = EAlmacen.get(almacen.codigo_interno)
		//In order to be removed from transaction cache, entity should have an "id" field
		almacen.metaClass.id = almacen.codigo_interno
		assert almacen
		
		//Delete it
		almacen.delete()
		
		//Find it again
		almacen = EAlmacen.get("111")
		assert !almacen
		
	}
	
	@Test
	void shouldNotFailAtDeleteNotInsertedYetEntity() {
		
		def entity = new EAlmacen(codigo_interno:"13345")
		entity.delete()
		
		//Should not fail
		assertNull EAlmacen.get(entity.codigo_interno)
	}
	
	@Test
	void shouldCheckDirityEntities() {
		
		//Retrieve a seccion
		def seccion = ESeccion.get(2)
		assert seccion
		
		def oldDescription = seccion.descripcion
		
		//Seccion is not dirty
		assertFalse seccion.isDirty()
		
		//Update description
		seccion.descripcion = "FOO"
		
		//Now is dirty
		assertTrue seccion.isDirty()
		
		//Check previous value
		assertEquals oldDescription, seccion.getPersistentValue("descripcion")
	}
	
	@Test
	void shouldDirtyNewEntities() {
		
		def seccion = new ESeccion()
		assertTrue seccion.isDirty()
	}
	
	@Test
	void shouldGetDirtyPropertyNames() {
		
		//Retrieve a seccion
		def seccion = ESeccion.get(2)
		assert seccion
		
		//Seccion has no dirty properties
		assert !seccion.getDirtyPropertyNames()
		
		
		//Update description
		seccion.descripcion = "FOO"
		
		//Now is dirty
		assertEquals(["descripcion"] as Set, seccion.getDirtyPropertyNames())
		assertTrue seccion.isDirty("descripcion")
	}
	
	@Test
	void shouldCreateCriteria() {
		
		def criteria = EAlmacen.createCriteria()
		assert criteria
		assert criteria instanceof GormLikeCriteria
		
		def result = criteria.list {
			or {
				eq "codigo_interno", "00000002"
				eq "codigo_interno", "00000022"
			}
		}
		
		assertEquals 2, result.size()
	}
	
	@Test
	void shouldInjectWithCriteria() {
		
		
		def result = EAlmacen.withCriteria {
			or {
				eq "codigo_interno", "00000002"
				eq "codigo_interno", "00000022"
			}
		}
		
		assertEquals 2, result.size()
	}
}

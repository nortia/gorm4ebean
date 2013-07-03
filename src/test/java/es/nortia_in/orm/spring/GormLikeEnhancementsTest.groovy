package es.nortia_in.orm.spring;

import static org.junit.Assert.*;

import javax.persistence.PersistenceException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.avaje.ebean.config.ServerConfig;

import es.nortia_in.orm.gorm.query.GormLikeCriteria;
import es.nortia_in.test.db.AbstractDbUnitTransactionalJUnit4SpringContextTests
import es.nortia_in.test.db.DatasetLocation

import MODEL.EAlmacen;
import MODEL.EFamilia;
import MODEL.EGoods;
import MODEL.ESeccion;

@RunWith(SpringJUnit4ClassRunner.class)
@DatasetLocation(value="dataset.xml")
@ContextConfiguration(locations = "classpath:META-INF/spring/test-context-finders.xml")
@DirtiesContext
class GormLikeEnhancementsTest  extends AbstractDbUnitTransactionalJUnit4SpringContextTests{

	@Autowired
	GormLikeEbeanPostProcessor postProcessor;

	@Test
	void shouldDirectEnhanceClass() {

		assert postProcessor

		//Enhance a not defined in domain directory class
		postProcessor.enhanceClass(EFamilia)

		def entity = new EFamilia()

		//Familia now has enhanced methods
		assert entity.respondsTo("getId", [])
		assert entity.respondsTo("save", [])

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

		//Before saved method should be executed
		assert bean.descripcion.endsWith("@@@")
	}

	@Test
	void shouldRetreiveRelatedToMany() {

		//Retrieve seccion
		def seccion = ESeccion.get(2)
		assert seccion

		def products = seccion.products
		assert products

		assertEquals 3, products.size()

	}

	@Test
	void shouldAccessService() {

		//Access any bean in application context
		def serverConfig = EAlmacen.getService("serverConfig")
		assert serverConfig
		assert serverConfig instanceof ServerConfig

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
		assertEquals "111", almacen.id

		//Also check when id is in superclass
		def goods = new EGoods(codigo_interno:"123")
		assertEquals "123", goods.id
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
		def almacen = new EAlmacen(codigo_interno:"00000003")
		almacen.save()

		//Find it
		almacen = EAlmacen.get(almacen.id)
		assert almacen

		//Delete it
		almacen.delete()

		//Find it again
		almacen = EAlmacen.get("00000003")
		assert !almacen

	}

	@Test
	void shouldNotFailAtDeleteNotInsertedYetEntity() {

		def entity = new EAlmacen(codigo_interno:"13345")
		entity.delete()

		//Should not fail
		assertNull EAlmacen.get(entity.id)
	}

	@Test
	void shouldCreateAndExecuteCriteria() {

		def criteria = EAlmacen.createCriteria()

		assertNotNull criteria
		assertTrue criteria instanceof GormLikeCriteria

		def entity = criteria.get { eq "codigo_interno", "00000222" }

		assertNotNull entity
		assertEquals "00000222", entity.codigo_interno
	}

	@Test
	void shouldRefreshBean() {
		
		def seccion = ESeccion.get(2)
		assert seccion
		
		seccion.descripcion = "FOO"
		assertEquals "FOO", seccion.descripcion
		
		seccion.refresh()
		assertFalse "Object description should been refreshed", "FOO" == seccion.descripcion
		
	}
	
	@Test
	void shouldDoNothingRefreshingNotPersistentBean() {
	
		def seccion = new ESeccion(descripcion:"FOO")
		assert seccion
		
		seccion.descripcion = "FOO"
		assertEquals "FOO", seccion.descripcion
		
		seccion.refresh()
		assertTrue "Object description should not been refreshed", "FOO" == seccion.descripcion
			
	}
}

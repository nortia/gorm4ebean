package es.nortia_in.orm.query;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.avaje.ebean.EbeanServer;

import es.nortia_in.orm.gorm.query.FindAllByMethod
import es.nortia_in.test.db.AbstractDbUnitTransactionalJUnit4SpringContextTests
import es.nortia_in.test.db.DatasetLocation


import MODEL.EAlmacen;
import MODEL.ESeccion;

@RunWith(SpringJUnit4ClassRunner.class)
@DatasetLocation(value="dataset.xml")
@ContextConfiguration(locations = "classpath:META-INF/spring/test-context-finders.xml")
@DirtiesContext
class FindAllByMethodTest  extends AbstractDbUnitTransactionalJUnit4SpringContextTests{

	@Autowired
	EbeanServer eorm

	/**
	 * the method to test
	 */
	FindAllByMethod findByMethod = new FindAllByMethod();

	@Test
	void shouldGenerateFindMethod() {

		assert findByMethod

		def found = findByMethod.execute(eorm, EAlmacen.class, "findAllByCodigo_interno", ["00000002"])
		assert found

		assertEquals 1, found.size()

		assertEquals "00000002", found[0].codigo_interno
		assertEquals "1881 VIRGEN B.00", found[0].abreviada
	}

	@Test
	void shouldGenerateFindMethodWithDisjunction() {

		assert findByMethod

		def found = findByMethod.execute(eorm, EAlmacen.class, "findAllByCodigo_internoOrAbreviada", ["00000002", "OLIVA"])
		assert found
		assertEquals 1, found.size()

		assertEquals "00000002", found[0].codigo_interno
		assertEquals "1881 VIRGEN B.00", found[0].abreviada
	}


	@Test
	void shouldFindMethodWithUnaryOperator() {

		assert findByMethod

		def found = findByMethod.execute(eorm, EAlmacen.class, "findAllByDescripcionIsNull", [])
		assert found

		assertEquals 1, found.size()

		assertEquals "00000222", found[0].codigo_interno

		//Search with sort directive
		found = findByMethod.execute(eorm, EAlmacen.class, "findAllByDescripcionIsNotNull", [sort:"codigo_interno"])
		assert found

		assertEquals 2, found.size()

		def almacen = found[0]
		assertEquals "00000002", almacen.codigo_interno

		almacen = found[1]
		assertEquals "00000022", almacen.codigo_interno
	}

	@Test
	void shouldOrderDescendantQueries() {

		assert findByMethod

		def found = findByMethod.execute(eorm, EAlmacen.class, "findAllByAbreviadaLike", [
			"%VIRGEN%",
			[sort:"codigo_interno", order:"desc"]
		])
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
	void shouldQueryByOtherEntity() {

		assert findByMethod

		//Retrieve seccion to test
		def seccion = eorm.find(ESeccion.class,"2")
		assert seccion

		//Find all seccion's products
		def found = findByMethod.execute(eorm, EAlmacen.class, "findAllBySeccion", [seccion])
		assert found

		assertEquals 3, found.size()
	}
}

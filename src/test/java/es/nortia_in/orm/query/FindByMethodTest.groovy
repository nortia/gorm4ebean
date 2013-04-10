package es.nortia_in.orm.query;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.avaje.ebean.EbeanServer;

import es.nortia_in.orm.gorm.query.FindByMethod
import es.nortia_in.test.db.AbstractDbUnitTransactionalJUnit4SpringContextTests
import es.nortia_in.test.db.DatasetLocation


import MODEL.EAlmacen;

@RunWith(SpringJUnit4ClassRunner.class)
@DatasetLocation(value="dataset.xml")
@ContextConfiguration(locations = "classpath:META-INF/spring/test-context-finders.xml")
@DirtiesContext
class FindByMethodTest  extends AbstractDbUnitTransactionalJUnit4SpringContextTests{

	@Autowired
	EbeanServer eorm

	/**
	 * the method to test
	 */
	FindByMethod findByMethod = new FindByMethod();

	@Test
	void shouldGenerateFindMethod() {

		assert findByMethod

		def found = findByMethod.execute(eorm, EAlmacen.class, "findByCodigo_interno", ["00000002"])
		assert found

		assertEquals "00000002", found.codigo_interno
		assertEquals "1881 VIRGEN B.00", found.abreviada
	}

	@Test
	void shouldGenerateFindMethodWithDisjunction() {

		assert findByMethod

		def found = findByMethod.execute(eorm, EAlmacen.class, "findByCodigo_internoOrAbreviada", ["00000002", "OLIVA"])
		assert found

		assertEquals "00000002", found.codigo_interno
		assertEquals "1881 VIRGEN B.00", found.abreviada
	}

	@Test
	void shouldGenerateFindMethodWithConjunction() {

		assert findByMethod

		def found = findByMethod.execute(eorm, EAlmacen.class, "findByCodigo_internoAndAbreviada", [
			"00000002",
			"1881 VIRGEN B.00"
		])
		assert found

		assertEquals "00000002", found.codigo_interno
		assertEquals "1881 VIRGEN B.00", found.abreviada

		found = findByMethod.execute(eorm, EAlmacen.class, "findByCodigo_internoAndAbreviada", [
			"00000002",
			"1881 VIRGEN NO B.00"
		])
		assertNull found
	}

	@Test
	void shouldFindMethodWithUnaryOperator() {

		assert findByMethod

		def found = findByMethod.execute(eorm, EAlmacen.class, "findByDescripcionIsNull", [])
		assert found

		assertEquals "00000222", found.codigo_interno

		found = findByMethod.execute(eorm, EAlmacen.class, "findByDescripcionIsNotNull", [])
		assert found

		assert (found.codigo_interno != "00000222")
	}
	
	
}

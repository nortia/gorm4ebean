package es.nortia_in.orm.query;

import static org.junit.Assert.*;



import org.junit.Test
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import MODEL.ESeccion
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.RawSql
import com.avaje.ebean.RawSqlBuilder

import es.nortia_in.orm.gorm.AggregatedQuery;
import es.nortia_in.test.db.AbstractDbUnitTransactionalJUnit4SpringContextTests;
import es.nortia_in.test.db.DatasetLocation

@RunWith(SpringJUnit4ClassRunner.class)
@DatasetLocation(value="dataset.xml")
@ContextConfiguration(locations = "classpath:META-INF/spring/test-context-finders.xml")
@DirtiesContext
class QueryAggregationsTest  extends AbstractDbUnitTransactionalJUnit4SpringContextTests {

	@Autowired
	EbeanServer eorm

	@Test
	void shouldRetrieveAggregated() {
		
		RawSql rawSql =
		RawSqlBuilder
		.parse("select max(version) as result from e_seccion")
		.create();

		//y ahora la parte where la generamos con ebean
		def criteria = eorm.createQuery(AggregatedQuery)
		criteria = criteria.setRawSql(rawSql)
		criteria = criteria.where().eq("version", 0)
		
		//Ejecutamos el criteria...
		def result = criteria.findUnique()
		assertEquals 0, result.result
	}
	
	@Test
	void shouldRetrieveDynamicAggregated() {
		

		//y ahora la parte where la generamos con ebean
		def criteria = eorm.createSqlQuery("select max(version) as foo from e_seccion")
		
		//Ejecutamos el criteria...
		def result = criteria.findUnique()
		assertEquals 1, result.foo
	}
	
	@Test
	void shouldCreateAggregatedQuery() {
		
		def criteria = eorm.createQuery(ESeccion)
		criteria = criteria.where().eq("version", 0)
		println criteria
	}
	

}

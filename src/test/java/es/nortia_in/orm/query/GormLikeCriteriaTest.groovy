package es.nortia_in.orm.query;

import static org.junit.Assert.*;

import java.sql.ResultSet;

import javax.persistence.PersistenceException;

import org.junit.Ignore;
import org.junit.Test
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.PagingList;

import es.nortia_in.orm.gorm.query.GormLikeCriteria;
import es.nortia_in.test.db.AbstractDbUnitTransactionalJUnit4SpringContextTests;
import es.nortia_in.test.db.DatasetLocation
import MODEL.EAlmacen;
import MODEL.ESeccion;

@RunWith(SpringJUnit4ClassRunner.class)
@DatasetLocation(value="dataset.xml")
@ContextConfiguration(locations = "classpath:META-INF/spring/test-context-finders.xml")
@DirtiesContext
class GormLikeCriteriaTest  extends AbstractDbUnitTransactionalJUnit4SpringContextTests {

	@Autowired
	EbeanServer eorm

	@Test
	void shouldQuerySimpleEquality() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
		
		def result = criteria.list{ eq "codigo_interno", "00000002" }
		assertEquals 1, result.size()
	}

	@Test
	void shouldQueryDisjunction() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)

		def result = criteria.list {
			or {
				eq "codigo_interno", "00000002"
				eq "codigo_interno", "00000022"
			}
		}
		
		assertEquals 2, result.size()
	}

	@Test
	void shouldQueryDisjunctionNested() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
		
		def result = criteria.list{
			or {
				eq "codigo_interno", "00000002"
				eq "codigo_interno", "00000022"
			}
			contains "descripcion", "GIRASOL"
		}
		assertEquals 1, result.size()
	}

	@Test
	void shouldQueryDisjunctionNested2() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)

		def result = criteria.list{
			or {
				and {
					contains "descripcion", "GIRASOL"
					eq "codigo_interno", "00000022"
				}
				eq "codigo_interno", "00000002"
			}
		}
		assertEquals 2, result.size()
	}

	@Test
	void shouldQueryBetween() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)

		def result = criteria.list{ between "codigo_interno", "00000002", "00000022" }
		assertEquals 2, result.size()
	}

	@Test
	void shouldQueryIn() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
	
		def result = criteria.list{
			'in'("codigo_interno", ["00000002", "00000022"])
		}

		assertEquals 2, result.size()
	}


	@Test
	void shouldQueryIdIn() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
		
		def result = criteria.list {
			idIn(["00000002", "00000022"])
		}
		assertEquals 2, result.size()
	}

	@Test
	void shouldQueryLimitSort() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
		
		def result = criteria.list{
			like "abreviada", "%VIRGEN%"
			maxResults(1)
			order("codigo_interno", "DESC")
		}
		assertEquals 1, result.size()

		assertEquals "00000222", result[0].codigo_interno
	}

	@Test
	void shouldQueryLimitSortOffset() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
		
		def result = criteria.list {
			like "abreviada", "%VIRGEN%"
			maxResults(1)
			firstResult(1)
			order("codigo_interno", "DESC")
		}
		assertEquals 1, result.size()

		assertEquals "00000022", result[0].codigo_interno
	}

	@Test
	void shouldQueryNestedObject() {

		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
		
		def result = criteria.list {
			seccion { like "descripcion", "%GRAN CONSUMO%" }
		}

		assertEquals 3, result.size()
	}

	@Test
	void shouldQuertEqualsIgnoreCase() {


		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
		

		def result = criteria.list{
			eq "abreviada", "1881 virgen B.00",[ignoreCase:true]
		}
		assertEquals 2, result.size()
	}

	@Test
	void shouldQueryEqualsProperty() {


		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
	
		def result = criteria.list{ eqProperty "version", "seccion" }
		assertEquals 1, result.size()
	}

	@Test
	void shouldQueryNotEqualsProperty() {


		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)

		def result = criteria.list{ neProperty "version", "seccion" }
		assertEquals 2, result.size()
	}

	@Test
	@Ignore //XXX This method doesn't work due to ebean innej join implementation issues
	void shouldQueryEmptyRelation() {


		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		
		def result = criteria.list { isEmpty "products" }
		assertEquals 2, result.size()


	}

	@Test
	void shouldQueryNotEmptyRelation() {


		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		
		def result = criteria.list{ isNotEmpty "products" }
		assertEquals 1, result.size()


	}

	@Test
	void shouldQuertEqualsId() {


		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)

		def result = criteria.list { idEq "00000222" }
		assertEquals 1, result.size()


	}


	@Test(expected=UnsupportedOperationException)
	void shouldSizeOperatorsNotSupported() {
		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		criteria.list { sizeEq "products", 10 }
	}

	@Test
	void shouldApplySqlRestriction() {
		GormLikeCriteria criteria = new GormLikeCriteria(EAlmacen, eorm)
		
		def result = criteria.list { sqlRestriction "seccion.seccion = 2" }
		assertEquals 3, result.size()
	}

	@Test
	void shouldQueryWithNotOperator() {

		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		
		def result = criteria.list {
			not { eqProperty "version", "seccion" }
		}
		assertEquals 2, result.size()
	}

	@Test
	void shouldQueryEmptyCriteria() {

		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)

		def result = criteria.list()
		assertEquals 3, result.size()
	}
	
	@Test
	void shouldGetUniqueEntity() {
		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		
		assertEquals 1, criteria.get {
			eq "version", 1
		}.version

	}
	
	@Test(expected=PersistenceException)
	void shouldFailWithGettingManyEntities() {
		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		
		criteria.get{
			eq "version", 0
		}
	}
	
	@Test(expected=UnsupportedOperationException)
	void shouldFailScrollNotSupported() {
		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		
		criteria.scroll{
			products {
				eq "abreviada", "1881 virgen B.00",[ignoreCase:true]
			}
		}
	}
	
	@Test
	void shouldRetrievePagingList() {
		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		
		def result = criteria.list([pageSize:10],  {
			products {
				eq "abreviada", "1881 virgen B.00",[ignoreCase:true]
			}
		})
		assertTrue result instanceof PagingList<ESeccion>
	}
	
	@Test(expected=UnsupportedOperationException)
	void shouldProjectionsNotSupported() {
		GormLikeCriteria criteria = new GormLikeCriteria(ESeccion, eorm)
		
		def result = criteria.list([pageSize:10],  {
			projections {
				max "id"
			}
		})
	}
	
}

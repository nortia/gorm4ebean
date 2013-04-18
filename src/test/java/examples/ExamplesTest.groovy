package examples;

import static org.junit.Assert.*;



import org.junit.Test
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.avaje.ebean.RawSql
import com.avaje.ebean.RawSqlBuilder

import es.nortia_in.test.db.AbstractDbUnitTransactionalJUnit4SpringContextTests;
import es.nortia_in.test.db.DatasetLocation

@RunWith(SpringJUnit4ClassRunner.class)
@DatasetLocation(value="examples/dataset.xml")
@ContextConfiguration(locations = "classpath:examples/test-context.xml")
@DirtiesContext
class ExamplesTest  extends AbstractDbUnitTransactionalJUnit4SpringContextTests {

	@Test
	void crudOperations() {
		
		Book quixote = new Book(name:"quixote", title:"El Ingenioso Hidalgo Don Quijote de la Mancha")
		quixote.save()
		
		def books = Book.list()
		assert quixote in books
		
		assert 1 == Book.count()		
		assert quixote == Book.get("quixote")
		
		quixote.delete()
	}
	
	@Test
	void dynamicFinders() {
		
		Book.findByTitleLike("%Ingenioso%")
		Book.findByNameAndPages("quixote", 1221)
		Book.findAllByNameAndAuthor("quixote", Author.get("Cervantes"))
		Book.findAllByNameOrPagesGreaterThan("quixote", 1221)
		Book.findAllByNameInListOrPagesBetween(["quixote","riconete"], 1221, 1331)
	}
	
	@Test
	void criterias() {
		
		def quixote = Book.createCriteria().get {
			eq "name", "quixote"
		}
		
		def notQuixote = Book.createCriteria().get {
			not {
				eq "name", "quixote"
			}
		}
		
		def largeBooks = Book.withCriteria {
			gt "pages", 1000
		}
		
		def cervantesBooks = Book.withCriteria {
			author {
				eq "name", "Cervantes"
			}
		}
		
		def largestCervantesBook = Book.withCriteria {
			author {
				eq "name", "Cervantes"
			}
			
			order("pages", "DESC")
			maxResults(1)
		}
	}
	
	@Test
	void createQuery() {
				
		RawSql rawSql = RawSqlBuilder.parse("select count(*) as result from book join author on author.name = author_name").create()
		def query = BookAggregation.createQuery()
		query.setRawSql(rawSql).where().eq("author.name", "Cervantes")
		
		int cervantesBooks = query.findUnique().result
		
	}
	
	@Test
	void testService() {
		
		def service = Book.getService("bookStoreService")
		service.store(Book.get("quixote"))
		
	}
}
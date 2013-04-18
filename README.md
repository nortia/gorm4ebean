Gorm4Ebean
==========

GORM-like library for [EBean ORM](http://www.avaje.org).

gorm4ebean enhances EBean entity domain classes with GORM-like methods such as save(), delete(), list(), etc.

As original [Grails GORM](http://grails.org/doc/latest/guide/GORM.html), gorm4ebean uses [Spring Framework](http://www.springsource.org) and, of course, [Groovy](http://groovy.codehaus.org/)  and EBean ORM (with ebean-spring module).

Very quick and simple code examples are:
----------------------------------------

* CRUD related methods like create(), delete(), save(), get() or list():

		Book quixote = new Book(name:"quixote", title:"El Ingenioso Hidalgo Don Quijote de la Mancha")
		quixote.save()
		
		def books = Book.list()
		assert quixote in books
		
		assert 1 == Book.count()		
		assert quixote == Book.get("quixote")
		
		quixote.delete()
		
		
* Gorm-Like dynamic finders:

		Book.findByTitleLike("%Ingenioso%")
		Book.findByNameAndPages("quixote", 1221)
		Book.findAllByNameAndAuthor("quixote", Author.get("Cervantes"))
		Book.findAllByNameOrPagesGreaterThan("quixote", 1221)
		Book.findAllByNameInListOrPagesBetween(["quixote","riconete"], 1221, 1331)
		
* Criteria:

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

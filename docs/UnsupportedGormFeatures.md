Unsupported GORM Features
=========================

Next are GORM 2.0 features currently not supported by gorm4ebean library

* Projections. [EBean projections and aggregations](http://www.avaje.org/ebean/introquery_rawsql.html) are too different from GORM-Hibernate approach so it isn't easy to adapt them.
As substitution, EBean Queries can be created with injected method: <code>createQuery</code> and EBean's RawSql query:

		RawSql rawSql = RawSqlBuilder.parse("select count(*) as result from book join author on author.name = author_name").create()
		def query = BookAggregation.createQuery()
		query.setRawSql(rawSql).where().eq("author.name", "Cervantes")
		
		int cervantesBooks = query.findUnique().result
		
* DetachedCriterias. EBean is a session-free API. Standard Gorm-like criterias are ever detached.

* where() method. Not implemented yet.

* scroll() method and ScrollableResultSets. I could't found any similar query method in EBean API

* sizeGreaterThan, sizeLowerThan and other sizeXXX criteria operators. This operators are not directly supported by EBean API.  

  
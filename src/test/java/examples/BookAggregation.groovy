package examples

import javax.persistence.Entity;
import com.avaje.ebean.annotation.Sql;

@Entity
@Sql
class BookAggregation {
	
	int result
	
	Book book
}

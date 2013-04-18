package examples

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
class Author {

	@Id
	String name
	
	long age
	
	@OneToMany
	List<Book> books = []
}

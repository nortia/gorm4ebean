package examples

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
class Book {

	@Id
	String name
	
	String title
	
	long pages
	
	@ManyToOne
	Author author
	
	boolean equals(Object obj) {
		
		if (!(obj instanceof Book)) {
			return false
		}
		
		return name == obj.name
	}
	
}

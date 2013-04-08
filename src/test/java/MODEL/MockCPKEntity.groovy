package MODEL

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
class MockCPKEntity {

	@Id
	String id
	
	String foo
	
	Date date
	
	int number
	
	MockCPKEntity subentity
	
	static compositePk = {
		constant "0"
		property name:"foo", length:4
		property name:"date", format:"yy"
	}
	
}

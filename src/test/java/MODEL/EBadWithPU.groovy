package MODEL

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit

// Needs existing persistence unit
@PersistenceUnit(name="ES_NO_EXISTE")
@Entity
class EBadWithPU {
	
	@Id
	String id

}

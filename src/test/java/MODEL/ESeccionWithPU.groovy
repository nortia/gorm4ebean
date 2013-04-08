package MODEL

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceUnit
import javax.persistence.Table;
import javax.persistence.Version;


@PersistenceUnit(name="ES_ARTICULOS")
@Entity
@Table(name="e_seccion")
class ESeccionWithPU {
	
	String seccion
	
	String descripcion
	
	@Version
	int version

	@OneToMany(mappedBy = "seccion")
	List<EAlmacenWithPU> products = []
	
	String getId() {
		return seccion
	}
	
}

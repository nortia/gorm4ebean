package MODEL

import javax.persistence.Entity;
import javax.persistence.FetchType
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


@Entity
@Table(name="e_seccion")
class ESeccion {

	@Id
	String seccion
	
	String descripcion
	
	@Version
	int version

	@OneToMany(mappedBy = "seccion", fetch=FetchType.LAZY)
	List<EAlmacen> products = []
	
}

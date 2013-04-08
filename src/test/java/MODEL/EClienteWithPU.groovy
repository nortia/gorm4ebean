package MODEL

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit
import javax.persistence.Table
import javax.persistence.Version;

@PersistenceUnit(name="ES_CLIENTES")
@Entity
@Table(name="e_cliente")
@DiscriminatorValue("CLIENTE")
class EClienteWithPU {
	
	@Id
	String nif
	
	@Version
	int version
	
	String nombre
	
	String apellido1
	
	String apellido2

}

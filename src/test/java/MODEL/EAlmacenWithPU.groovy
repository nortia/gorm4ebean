package MODEL

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceUnit
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * Model Almacen for Persistence Unit Tests (multiple database)
 *
 */
@PersistenceUnit(name="ES_ARTICULOS")
@Entity
@Table(name="e_almacen")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ALMACEN")
class EAlmacenWithPU {
	
	@Id
	String codigo_interno
	
	String abreviada
	
	String descripcion
	
	@Version
	int version
	
	@ManyToOne
	ESeccionWithPU seccion
	
	void beforeInsert(){
		descripcion += "@@@"
	}
	
}

package MODEL

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


@Entity
@Table(name="e_almacen")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ALMACEN")
class EAlmacen implements Serializable {
	
	@Id
	String codigo_interno
	
	String abreviada
	
	String descripcion
	
	@Version
	int version
	
	@ManyToOne
	ESeccion seccion
	
	void beforeInsert(){
		descripcion += "@@@"
	}
	
	static Object recover(Object... factors){
		def descr = (factors) ? factors[0] : null
		return new EAlmacen(codigo_interno:"recoveredId", descripcion:descr)
	}
	
}

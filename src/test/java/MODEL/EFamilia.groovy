package MODEL

import java.sql.Timestamp
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.persistence.PersistenceUnit

@PersistenceUnit(name="ES_ARTICULOS")
@Entity
@Table(name="e_familia")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("FAMILIA")
class EFamilia {
	
	@Id
	String familia
	
	@Version
	Timestamp timestamp
	
}

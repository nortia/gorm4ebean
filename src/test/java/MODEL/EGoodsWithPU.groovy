package MODEL

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("GOODS")
class EGoodsWithPU extends EAlmacenWithPU {

	int price
}

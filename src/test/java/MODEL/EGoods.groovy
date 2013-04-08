package MODEL

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("GOODS")
class EGoods extends EAlmacen {

	int price
}

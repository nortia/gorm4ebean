package es.nortia_in.orm.gorm;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.avaje.ebean.annotation.Sql;

@Entity
@Sql
/**
 *
 */
public class AggregatedQuery {

	Integer result;
}

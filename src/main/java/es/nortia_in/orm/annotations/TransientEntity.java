package es.nortia_in.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation for mark transient entity classes. Transient entity classes are domain objects
 * that are not embedded or persisted in database.
 * 
 * Transient entities are registered in Domain Directory
 * 
 * @author angel
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TransientEntity {
	
}

/**
 * 
 */
package es.nortia_in.test.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for dbUnit-based tests. Defines the dataset file to be used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface DatasetLocation {

	/**
	 * Value of the database location (defaults to "")
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * Indicates whether to qualify tables or not (defaults to false)
	 * 
	 * @return
	 */
	boolean qualifyTables() default false;
}

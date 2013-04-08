package es.nortia_in.orm.annotations;


/**
 * This exception will be thrown when domain classes annotated with {@see Domain}
 * annotation (or annotations annotated with @Domain annotation) doesn't meet
 * the required constraints defined by domain annotation
 * 
 * @author angel
 * 
 */
public class DomainClassValidationException extends RuntimeException {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the exception with the specified detail message.
	 * 
	 * @param message
	 *            the detail message
	 */
	public DomainClassValidationException(String message) {
		super(message);
	}

}

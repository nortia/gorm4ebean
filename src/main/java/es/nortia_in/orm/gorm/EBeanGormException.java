package es.nortia_in.orm.gorm;

/**
 * Exception to notify error in ORM-eBeans mapping
 * 
 * @author angel
 * 
 */
public class EBeanGormException extends RuntimeException {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 6417167839308627173L;

	/**
	 * Constructs a new EbeanGormException.
	 */
	public EBeanGormException() {
		super();
	}

	/**
	 * Constructs a new EbeanGormException with the specified detail
	 * message.
	 * 
	 * @param message
	 *            the detail message
	 */
	public EBeanGormException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a new EbeanGormException with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause.
	 */
	public EBeanGormException(String msg, Throwable cause) {
		super(msg, cause);
	}

}

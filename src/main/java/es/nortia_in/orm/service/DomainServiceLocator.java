package es.nortia_in.orm.service;

/**
 * Find services to be used inside domain classes
 * @author angel
 *
 */
public interface DomainServiceLocator {

	/**
	 * Find a service with given name
	 * @param serviceName the service name
	 * @return the service found or null if there aren't any service with given name
	 */
	Object getService(String serviceName);
	
}

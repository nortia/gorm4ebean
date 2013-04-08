package es.nortia_in.orm.directory;


/**
 * Implementation for {@see DomainDirectory} interface which
 * looks for directly injected domain classes
 * 
 * @author angel
 *
 */
class InlineDomainDirectory extends DomainDirectorySupport {

	/**
	 * List of injected classes
	 */
	List<Class> domainClasses;

	/**
	 * List of complex primary key classes.
	 * @return
	 */
	List<Class> embeddableClasses;
	
	/**
	 * List of persist listeners
	 */
	List<Class> beanPersistListeners;

	@Override
	public List<Class> getDomainClasses() {
		return domainClasses
	}

	@Override
	public List<Class<?>> getEmbeddableClasses() {
		return embeddableClasses;
	}

	@Override
	public List<Class> getPersistListeners() {
		return beanPersistListeners;
	}

}

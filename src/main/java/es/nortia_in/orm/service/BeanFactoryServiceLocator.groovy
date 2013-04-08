package es.nortia_in.orm.service

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException

/**
 * Default {@see DomainServiceLocator} implementation. Finds and locate
 * spring bean services. 
 * @author angel
 *
 */
class BeanFactoryServiceLocator implements DomainServiceLocator, BeanFactoryAware {

	/**
	 * The bean factory for bean search
	 */
	protected BeanFactory beanFactory
	
	@Override
	public Object getService(String serviceName) {
		assert beanFactory
		
		try {
			return beanFactory.getBean(serviceName)
		} catch (NoSuchBeanDefinitionException e) {
			return null
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory
	}

}

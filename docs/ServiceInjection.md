Service Injection
=================

Injection of business services into domain classes is sightly different from GORM.
Instead of autowiring services into entity properties, a static method named <code>getService()</code>
in injected into every domain class.

This method receives the service id (usually service name) as parameter and looks it up.

A ServiceLocator design pattern was used to implement getService() method so different lookup strategies can be configured to search for business services.
By default a [BeanFactoryServiceLocator](/src/main/java/es/nortia_in/orm/service/BeanFactoryServiceLocator.groovy) is provided to search services configured as beans inside Spring Application Context.

To enable the bean factory service locator, it is only needed to add it to spring xml file and inject it to GormLikePostProcessor as follows:

	<bean id="bookStoreService" class="examples.BookStoreService"/>

	<bean id="serviceLocator" class="es.nortia_in.orm.service.BeanFactoryServiceLocator" />
	
	<bean class="es.nortia_in.orm.spring.GormLikeEbeanPostProcessor">
 		<property name="serviceLocator" ref="serviceLocator" /> 
		<property name="domainDirectory" ref="domainDirectoryImpl" />
	</bean> 
	

Any defined bean is now avaliable to be retrieved with getService method.

	def service = Book.getService("bookStoreService")
	service.store(Book.get("quixote"))
	
As service locator can be any bean implmenting [<code>DomainServiceLocator</code>](/src/main/java/es/nortia_in/orm/service/DomainServiceLocator.java) it is easy to implement locators to search in any other repositories, 
such as OSGi services, JNDI objects, remote Web Services, etc.   
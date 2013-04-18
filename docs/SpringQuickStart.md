Gorm4ebean Spring Quick Start
=============================

To enhance entity classes with GORM-Like methods in a Spring managed application 
follow the steps:

1. Configure spring-ebean xml beans as explained in [EBean Start Guide](http://www.avaje.org/ebean/getstarted_spring.html).
	You should have a spring beans XML file such as this:

		<!-- Any JDBC standard data source can be used. In the example a C3PO datasource is configured -->
		<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource"
			destroy-method="close">
			<property name="driverClass" value="${your.datasource.driver.class.name}" />
			<property name="jdbcUrl" value="${your.database.url}" />
			<property name="user" value="${your.database.username}" />
			<property name="password" value="${your.database.password}" />
		</bean>
	
		<!-- Spring transaction manager -->
		<bean id="transactionManager"
			class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
			<constructor-arg ref="dataSource" />
		</bean>
	
		<!-- Default abstract Ebean Server configuration -->
		<bean id="defaultEbeanServerConfig" class="com.avaje.ebean.config.ServerConfig"
			abstract="true">
			<property name="autofetchConfig">
				<bean class="com.avaje.ebean.config.AutofetchConfig">
					<property name="logDirectory" value="target" />
				</bean>
			</property>
	
			<!-- turn off all logging by default -->
			<property name="loggingDirectory" value="target" />
			<property name="loggingLevel" value="SQL" />
	
			<property name="externalTransactionManager">
				<bean
					class="com.avaje.ebean.springsupport.txn.SpringAwareJdbcTransactionManager" />
			</property>
	
			<property name="namingConvention">
				<bean class="com.avaje.ebean.config.UnderscoreNamingConvention" />
			</property>
	
		</bean>
	
		<bean id="serverConfig" parent="defaultEbeanServerConfig">
			<property name="dataSource" ref="dataSource" />
			<property name="name" value="ebeanServer" />
			<property name="ddlGenerate" value="false" />
			<property name="ddlRun" value="false" />
			<property name="defaultServer" value="true" />
		</bean>
	
	
		<!-- Ebean Server -->
		<bean id="eorm"
			class="com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean">
			<property name="serverConfig" ref="serverConfig" />
		</bean>   
	
2. Add gorm4ebean beans to your spring beans file.

		<!-- Domain class finder -->
		<bean id="domainDirectoryImpl" class="es.nortia_in.orm.directory.ClasspathDomainDirectory">
			<property name="basePackage" value="examples"/> [1](#callouts)
		</bean>
	
		<!-- Auto domain class register -->
		<bean class="es.nortia_in.orm.spring.DomainClassRegisterPostProcessor">
			<property name="domainDirectory" ref="domainDirectoryImpl" />
		</bean>
	
		<!-- Gorm-like class enhancer -->
		<bean class="es.nortia_in.orm.spring.GormLikeEbeanPostProcessor">
			<property name="domainDirectory" ref="domainDirectoryImpl" />
		</bean>

3. Load the Spring XML Context by using any of the several methods provided by Spring Framework.

		//Create Spring Application Context
		def applicationContext = new ClassPathXmlApplicationContext("classpath:examples/test-context.xml")
		
		//Start using GORM with EBean
		Book.list()
		
Example files
-------------

* Complete Spring Beans XML File. [test-context.xml](/examples/test-context.xml)
* Spring Application Context load [example](src/test/java/examples/ProgramaticallyLoadTest.groovy).
* Several GORM [examples](src/test/java/examples/ExamplesTest.groovy).
* Domain entities: [Book](src/test/java/examples/Book.groovy) and [Author](src/test/java/examples/Book.groovy) 

 <a id="callouts>Callouts</a>
 ----------------------------
 * [1] - Define a base package for your domain stuff is always recomended to avoid performance issues 
 
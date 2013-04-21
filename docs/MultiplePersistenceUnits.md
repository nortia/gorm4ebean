Persistence Units
=================

Persistence Unit JPA annotation (<code>@PersistenceUnit</code>) are supported in gorm4ebean.
In gorm4ebean "Persistece Unit" and eBean Server are synonyms. 

Entities mapped to a any given persistence unit will be automatically registered under
ebean server with same name.

It's mandatory that any ebean server should been defined for every declared persistence unit.

If the @PersistenceUnit annotation is not defined for a given domain class, this will be mapped under default eBean Server.

For example:

	@PersistenceUnit(name="SERVER_2")
	@Entity
	class FooEntity {
	}   
	
	
FooEntity will be registered by gorm4ebean post-processors under <code>SERVER_2</code> ebean server.
SERVER_2 ebean server should be defined in spring beans xml configuration file:

	<bean id="serverConfig" parent="defaultEbeanServerConfig">
		<property name="dataSource" ref="dataSource" />
		<property name="name" value="SERVER_2" />
		<property name="ddlGenerate" value="false" />
		<property name="ddlRun" value="false" />
	</bean>
	
	<bean id="SERVER_2"
		class="com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean">
		<property name="serverConfig" ref="serverConfig" />
	</bean>

Note that "SERVER_2" should be the bean id and the server name (defined as Server Config bean property "name")
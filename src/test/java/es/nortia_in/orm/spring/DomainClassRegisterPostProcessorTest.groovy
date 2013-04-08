package es.nortia_in.orm.spring

import static org.junit.Assert.*

import javax.persistence.Embeddable;


import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.event.BeanPersistListener;

import es.nortia_in.orm.annotations.TransientEntity
import es.nortia_in.orm.directory.DomainDirectory



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:META-INF/spring/test-context-postprocessor.xml")
public class DomainClassRegisterPostProcessorTest {

	@Autowired
	DomainDirectory domainDirectory
	
	@Autowired
	ServerConfig scClientes
	
	@Autowired
	ServerConfig scArticulos
	
	@Autowired
	ServerConfig defaultServer
	
	@Autowired
	BeanPersistListener persistListener
		
	@Test
	public void shouldRegisterDomainClasses() {
		
		//scClientes PersistenceUnit has 1 domain classes
		def scClientesClasses = scClientes.classes
		assertEquals 1, scClientesClasses.size()
		
		//scArticulos has 10 domain classes
		def scArticulosClasses = scArticulos.classes
		assertEquals 4, scArticulosClasses.size()
		
		
		//Default server has the remaining entities
		def defaultClasses = defaultServer.classes
		assertEquals 4, defaultClasses.size()
	}
	
	@Test
	public void shouldNotRegisterTransientEntities() {
		
		//There are one transient entity in domain directory
		def transients = domainDirectory.getAnnotatedDomainClasses(TransientEntity.class)
		assertEquals 1, transients.size()
		
		//There are no transient entities registered in any ebean server
		assertFalse TransientEntity in scClientes.classes
		assertFalse TransientEntity in scArticulos.classes
		assertFalse TransientEntity in defaultServer.classes
	}
	
	@Test
	public void shouldRegisterEmbbedableClasses() {
		
		//There are one embeddable entity in default server
		def embeddable = defaultServer.classes.findAll{it.getAnnotation(Embeddable)}
		assertEquals 1, embeddable.size()
		
	}
	
	@Test
	public void shouldRegisterListeners() {
		
		//Listeners have been injected in every server
		[scClientes, scArticulos, defaultServer].each {server ->
			def listeners = server.persistListeners
			
			//There are 2 registered persist listeners
			assertEquals 2, listeners.size()
			
			//one of them is autowired persistListener
			assertTrue persistListener in listeners
		}
		
	}
}

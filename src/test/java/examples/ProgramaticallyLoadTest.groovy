package examples;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

class ProgramaticallyLoadTest {

	
	@Test
	void shouldLoadSpringApplicationContext() {
		
		//Create Spring Application Context
		def applicationContext = new ClassPathXmlApplicationContext("classpath:examples/test-context.xml")
		
		//Start using GORM with EBean
		Book.list()
	}
	
}

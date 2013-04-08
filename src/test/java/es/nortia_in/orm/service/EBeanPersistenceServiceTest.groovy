package es.nortia_in.orm.service;

import static org.junit.Assert.*;

import javax.persistence.PersistenceException

import org.junit.Before;
import org.junit.Test
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.nortia_in.test.db.DatasetLocation

@RunWith(SpringJUnit4ClassRunner.class)
@DatasetLocation(value="dataset.xml")
@ContextConfiguration(locations = "classpath:META-INF/spring/test-context-pu.xml")
@DirtiesContext
class EBeanPersistenceServiceTest {

	@Autowired
	PersistenceService mockService
	
	@Before
	void setUp() {
		mockService.executed = false
	}
	
	@Test
	void shouldExecuteMockServiceInTransactionWithPU(){
		mockService.query("ES_CLIENTES")
		assertTrue mockService.executed
	}
	
	@Test
	void shouldFailMockServiceWithUnknownPU(){
		try {
			mockService.query("UNKNOWN_PU")
			fail()
		} catch (PersistenceException e) {
		}
	}
	
	@Test
	void shouldExecuteMockServiceInTransactionWithoutPU(){
		mockService.query()
		assertTrue mockService.executed
	}
	
	@Test
	void shouldExecuteMockServiceInTransactionWithDefaultPU(){
		mockService.query(null)
		assertTrue mockService.executed
	}
	
}

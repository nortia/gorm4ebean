package es.nortia_in.orm.annotations;

import static org.junit.Assert.*;

import org.junit.Test;

import es.nortia_in.orm.annotations.DomainAnnotationsProcessor;
import es.nortia_in.orm.annotations.DomainClassValidationException;


class DomainAnnotationsProcessorTest {

	def processor = new DomainAnnotationsProcessor()
	
	@Test
	void shouldFindDomainAnnotations() {
		
		assert processor
		
		def annotations = processor.getDomainAnnotations(MockDomain.class)
		assertEquals 1, annotations.size()
		
		assert annotations[0] instanceof MockAnnotation1
	}
	
	@Test
	void shouldFindAnnotationsDefinedInSuperclassInDomainSubclasses() {
		
		assert processor
		
		def annotations = processor.getDomainAnnotations(MockDomainSubclass.class)
		assertEquals 1, annotations.size()
		
		assert annotations[0] instanceof MockAnnotation1
	}
	
	@Test
	void shouldValidateDomainClassesWellDefined() {
	
		assert processor
		processor.checkAnnotations(MockDomain)			
	}
	
	
	@Test
	void shouldValidateDomainSuperclassesClassesWellDefined() {
	
		assert processor
		processor.checkAnnotations(MockDomainSubclass)
	}
	
	@Test
	void shouldFailWithDomainClassBadDefined() {
		assert processor
		try {
			processor.checkAnnotations(MockDomain2)
			fail()
		} catch (DomainClassValidationException e) {
		
		}
	}
	
	@Test
	void shoulValidateDomainClassInInterfaceAnnotation() {
		assert processor
		processor.checkAnnotations(MockDomain3)
	}

	
	@Test
	void shouldFailWithDomainClassBadDefinedInInterfaceAnnotation() {
		assert processor
		try {
			processor.checkAnnotations(MockDomain5)
			fail()
		} catch (DomainClassValidationException e) {
		
		}
	}

	@Test
	void shouldFindAnnotatedInterfaces() {
		
		//Find annotated interface
		def ints = processor.getAnnotatedInterfaces(MockDomain3.class)
		assertEquals([MockAnnotatedInterface], ints)
		
		//Find no annotated interface
		ints = processor.getAnnotatedInterfaces(MockDomain.class)
		assert !ints
	}
	
	@Test
	void shouldFindAnnotatedSuperInterfaces() {
		
		//Find annotated interface
		def ints = processor.getAnnotatedInterfaces(MockDomain5.class)
		assertEquals([MockAnnotatedInterface], ints)
	}
	
	@Test
	void shouldFindAnnotatedInterfacesInSuperclass() {
		
		//Find annotated interface
		def ints = processor.getAnnotatedInterfaces(MockDomain6.class)
		assertEquals([MockAnnotatedInterface], ints)
	}
}




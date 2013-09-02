package es.nortia_in.orm.enhance;

import java.lang.reflect.Field

import org.codehaus.groovy.classgen.Verifier;
import org.junit.Test;
import static org.junit.Assert.*

class ClassUtilsTest {


	@Test
	void shouldRetrieveObjectProperty() {

		//The object to test
		def object = [foo:"bar"]

		//Retrieve foo property directly
		assertEquals "bar", ClassUtils.getProperty(object, "foo")

	}

	@Test
	void shouldRetrieveChainedProperty() {

		//The object to test (with nested objects)
		def object = [foo:[bar:[tee:"tee"]]]

		//Rretrieve chained property
		assertEquals "tee", ClassUtils.getProperty(object, "foo.bar.tee")

	}


}





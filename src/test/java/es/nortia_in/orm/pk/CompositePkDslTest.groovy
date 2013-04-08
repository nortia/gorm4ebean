package es.nortia_in.orm.pk;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

import org.junit.Test;

import es.nortia_in.orm.pk.CompositePkDsl;

import MODEL.MockCPKEntity;
import MODEL.MockCPKSubEntity;

class CompositePkDslTest {

	@Test
	void testDslConstants() {

		//Create the parser to test
		def parser = new CompositePkDsl()

		//Parse a simple constant
		def components = parser.parse { constant "0" }
		assertEquals 1, components.size()

		//Retrieve constant
		def component = components[0]

		//Should not validate empty string
		assertNull component.validate("")

		//Should not validate "1"
		assertNull component.validate("1")

		//Should validate "0"
		assertNotNull component.validate("0")

		//Should validate "01"
		def result = component.validate("01")
		assertEquals "1", result

		//Parse a constant defined by a variable
		String cte = "1"
		components = parser.parse { constant cte }
		assert components
		assertNotNull components[0].validate("1")

		//Retrieve value
		assertEquals "1", components[0].getValue(null)

	}

	@Test
	void testDslProperty() {

		//Create the entity
		def entity = new MockCPKEntity(foo:"bar", date:new SimpleDateFormat("yy").parse("12"), number:1)

		//Create the parser to test
		def parser = new CompositePkDsl(clazz:MockCPKEntity)

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			property name:"foo", length:3
			property name:"date", format:"yy"
			property name:"number", format:"00"
		}

		assert components
		assertEquals 3, components.size()

		//Validate text ok
		def pk = "bar1201"
		pk = components[0].validate(pk)
		assertEquals "1201", pk

		pk = components[1].validate(pk)
		assertEquals "01", pk

		pk = components[2].validate(pk)
		assertEquals "", pk

		//Validate pk error in date component
		pk = "bar3f01"
		pk = components[0].validate(pk)
		assertEquals "3f01", pk

		pk = components[1].validate(pk)
		assertNull pk

		//Validate error in number component
		pk="foo12er"
		pk = components[0].validate(pk)
		assertEquals "12er", pk

		pk = components[1].validate(pk)
		assertEquals "er", pk

		pk = components[2].validate(pk)
		assertNull pk
	}
	
	@Test
	void shouldFormatIntegerText() {
		
		//Create the entity
		def entity = new MockCPKEntity(number:-123454656)

		//Create the parser to test
		def parser = new CompositePkDsl(clazz:MockCPKEntity)

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			property name:"number", format:"000"
		}
	
		assert components
		def component = components[0]
		
		assertEquals "-12",  component.getValue(entity)
	}

	@Test
	void shouldInfereChainedPropertyType() {

		//Create the entity
		def entity = new MockCPKEntity(foo:"bar", date:new SimpleDateFormat("yy").parse("12"), number:1)


		//Create any entity
		def superEntity = new MockCPKEntity(subentity:entity)

		//Create the parser to test
		def parser = new CompositePkDsl(clazz:MockCPKEntity)

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			property name:"subentity.foo", length:3
			property name:"subentity.date", format:"yy"
			property name:"subentity.number", format:"00"
		}

		assert components
		assertEquals 3, components.size()

		assert components
		assertEquals 3, components.size()

		//Validate text ok
		def pk = "bar1201"
		pk = components[0].validate(pk)
		assertEquals "1201", pk

		pk = components[1].validate(pk)
		assertEquals "01", pk

		pk = components[2].validate(pk)
		assertEquals "", pk

		//Validate pk error in date component
		pk = "bar3f01"
		pk = components[0].validate(pk)
		assertEquals "3f01", pk

		pk = components[1].validate(pk)
		assertNull pk

		//Validate error in number component
		pk="foo12er"
		pk = components[0].validate(pk)
		assertEquals "12er", pk

		pk = components[1].validate(pk)
		assertEquals "er", pk

		pk = components[2].validate(pk)
		assertNull pk

	}

	@Test
	void testDslPropertySubClass() {

		//Create the entity
		def entity = new MockCPKSubEntity(foo:"bar", date:new SimpleDateFormat("yy").parse("12"), number:1)

		//Create the parser to test
		def parser = new CompositePkDsl(clazz:MockCPKSubEntity)

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			property name:"foo", length:3
			property name:"date", format:"yy"
			property name:"number", format:"00"
		}

		assert components
		assertEquals 3, components.size()

		//Validate text ok
		def pk = "bar1201"
		pk = components[0].validate(pk)
		assertEquals "1201", pk

		pk = components[1].validate(pk)
		assertEquals "01", pk

		pk = components[2].validate(pk)
		assertEquals "", pk

		//Validate pk error in date component
		pk = "bar3f01"
		pk = components[0].validate(pk)
		assertEquals "3f01", pk

		pk = components[1].validate(pk)
		assertNull pk

		//Validate error in number component
		pk="foo12er"
		pk = components[0].validate(pk)
		assertEquals "12er", pk

		pk = components[1].validate(pk)
		assertEquals "er", pk

		pk = components[2].validate(pk)
		assertNull pk
	}


	@Test
	void testDslLongStringProperty() {

		//Create the parser to test
		def ctx = [foo:"barbarbarbarbar"]
		def parser = new CompositePkDsl()

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			property name:"foo", length:3
		}

		assertEquals "bar", components[0].getValue(ctx)
	}


	@Test
	void testDslCompositeStringProperty() {

		//Create the parser to test
		def ctx = [foo:[bar:[tee:"tee"]]]
		def parser = new CompositePkDsl()

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			property name:"foo.bar.tee", length:3
		}

		assertEquals "tee", components[0].getValue(ctx)
	}

	@Test
	void testDslShorterStringProperty() {

		//Create the parser to test
		def ctx = [foo:"b"]
		def parser = new CompositePkDsl()

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			property name:"foo", length:3
		}

		assertEquals "  b", components[0].getValue(ctx)

	}

	@Test
	void testDslShorterStringNumericProperty() {

		//Create the parser to test
		def ctx = [foo:"1"]
		def parser = new CompositePkDsl()

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			property name:"foo", length:3, type:Integer.class
		}

		assertEquals "001", components[0].getValue(ctx)

	}



	@Test
	void testDynamicDsl() {

		//Create the parser to test
		def ctx = [foo:"bar"]
		def parser = new CompositePkDsl()

		//Parse a pk composed by 3 properties
		def components  = parser.parse {
			dynamic {
				closure { "foo$it.foo" }
			}
		}

		assertEquals "", components[0].validate("any")
		assertEquals "foobar", components[0].getValue(ctx)

	}

	@Test
	void testDynamicDslWithFormat() {

		//Create the parser to test
		def ctx = [foo:1]
		def parser = new CompositePkDsl()

		//Parse a pk composed by 3 properties
		def components = parser.parse {
			dynamic {
				format "000"
				type Integer.class
				closure { ctx.foo }
			}
		}

		assertEquals "", components[0].validate("123")
		assertNull components[0].validate("bar")
		assertEquals "001", components[0].getValue(ctx)

	}

}


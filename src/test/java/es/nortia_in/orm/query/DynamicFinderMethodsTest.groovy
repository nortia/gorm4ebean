package es.nortia_in.orm.query;

import static org.junit.Assert.*;

import org.junit.Test;

import es.nortia_in.orm.gorm.EBeanGormException
import es.nortia_in.orm.gorm.query.ComparationToken
import es.nortia_in.orm.gorm.query.Comparators
import es.nortia_in.orm.gorm.query.FindByMethod
import es.nortia_in.orm.gorm.query.LogicalOperators

class DynamicFinderMethodsTest {

	/**
	 * the method to test
	 */
	FindByMethod findByMethod = new FindByMethod();

	@Test
	void shouldAcceptFinderMethodName() {
		assert findByMethod

		assertTrue findByMethod.canExecute("findByAge")
		assertTrue findByMethod.canExecute("findByAgeAndFoo")
		assertFalse findByMethod.canExecute("foo")
		assertFalse findByMethod.canExecute("findAge")
		assertFalse findByMethod.canExecute("findBy")
	}

	@Test
	void shouldExtractQueryFromFinderMethodName() {

		assert findByMethod

		assertEquals "Age", findByMethod.extractQuery("findByAge")
		assertEquals "AgeAndHeight", findByMethod.extractQuery("findByAgeAndHeight")
	}

	@Test
	void shouldFailIfPropertyNameDoesntStartsWithCapitalLetter() {

		assert findByMethod

		try {
			findByMethod.tokenize("age")
			fail()
		} catch(EBeanGormException e) {
		}
	}

	

	@Test
	void shouldTokenizeOneTokenNoParameters() {

		assert findByMethod

		def tokens = "Age"
		tokens = findByMethod.tokenize(tokens)
		tokens = findByMethod.processParameters(tokens, [])
		
		def expectedTokens = [new ComparationToken(fieldName:"Age")]
		assertEquals expectedTokens, tokens
	}

	@Test
	void shouldTokenizeOneTokenUniaryOperatorNoParameters() {

		assert findByMethod

		def tokens = "AgeIsNull"
		tokens = findByMethod.tokenize(tokens)
		tokens = findByMethod.processParameters(tokens, [])
		
		def expectedTokens = [new ComparationToken(fieldName:"Age", operator:Comparators.IS_NULL)]
		assertEquals expectedTokens, tokens
	}
	
	@Test
	void shouldTokenizeOneToken() {

		assert findByMethod

		def tokens = "Age"
		tokens = findByMethod.tokenize(tokens)
		tokens = findByMethod.processParameters(tokens, [15])
		
		def expectedTokens = [new ComparationToken(fieldName:"Age", value:15)]
		assertEquals expectedTokens, tokens
	}
	
	@Test
	void shouldTokenizeOneTokenWithOperator() {

		assert findByMethod

		def tokens = "AgeLike"
		tokens = findByMethod.tokenize(tokens)
		tokens = findByMethod.processParameters(tokens, [15])
		
		def expectedTokens = [new ComparationToken(fieldName:"Age", value:15, operator:Comparators.LIKE)]
		assertEquals expectedTokens, tokens
	}
	
	@Test
	void shouldTokenizeSomeTokens() {

		assert findByMethod
		
		def tokens = "AgeAndWeightOrHeightAndAge"
		tokens = findByMethod.tokenize(tokens)
		tokens = findByMethod.processParameters(tokens, [15, 100, 1.5, 25])
		
		def expectedTokens = [
			new ComparationToken(fieldName:"Age", value:15),
			LogicalOperators.AND,
			new ComparationToken(fieldName:"Weight", value:100),
			LogicalOperators.OR,
			new ComparationToken(fieldName:"Height", value:1.5),
			LogicalOperators.AND,
			new ComparationToken(fieldName:"Age", value:25)
		]
		assertEquals(expectedTokens, tokens)
	}

	
	
	@Test
	void shouldProcessJunctions() {
		
		assert findByMethod
		
		def tokens = [
			new ComparationToken(fieldName:"MyAge", value:15),
			LogicalOperators.OR,
			new ComparationToken(fieldName:"LightWeight", value:100),
			LogicalOperators.AND,
			new ComparationToken(fieldName:"HeavyWeight", value:1.5)
		]
		
		def junctions = findByMethod.parseJunctions(tokens, LogicalOperators.OR)
		assertEquals 2, junctions.size()
		
		assertEquals([tokens[0]], junctions[0])
		assertEquals tokens[2..-1], junctions[1]
	}
	
	@Test
	void testTokenizeOperatorUnary() {
		
		assert findByMethod
		
		def method = "DescripcionIsNull"
		def tokens = findByMethod.tokenize(method)
		
		def expectedTokens = [new ComparationToken(fieldName:"Descripcion", operator:Comparators.IS_NULL)]
		assertEquals expectedTokens, tokens
	}
	
}

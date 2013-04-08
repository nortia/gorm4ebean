package es.nortia_in.test.db;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Base class for transaction-based tests with dbUnit.
 * Provides a <code>TestExecutionListener</code> to perform auto-rollback transactions
 */
@TestExecutionListeners(DbUnitInitializerTestExecutionListener.class)
public abstract class AbstractDbUnitTransactionalJUnit4SpringContextTests
		extends AbstractTransactionalJUnit4SpringContextTests {

}

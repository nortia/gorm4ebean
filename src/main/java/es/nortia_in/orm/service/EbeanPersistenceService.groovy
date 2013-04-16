package es.nortia_in.orm.service;

import groovy.lang.Closure;

import javax.persistence.PersistenceException;


import org.slf4j.Logger
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable
import com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean;

/**
 * PersistenceService implementation for EBean framework.
 *
 * @see PersistenceService
 */
public class EbeanPersistenceService implements PersistenceService, BeanFactoryAware {

	protected static final Logger log = LoggerFactory.getLogger(EbeanPersistenceService.class)

	/**
	 * The spring bean factory
	 */
	private ListableBeanFactory beanFactory;

	/**
	 * Transaction managers indexed by persistence unit name
	 */
	private Map transactionManagerMap = [:]

	/**
	 * Retrieve transaction template associated with given persistence unit
	 * @param persistenceUnit the persistence unit name
	 * @return the transaction template
	 */
	protected def retrieveTransactionTemplate(String persistenceUnit) {

		//Search inside map
		def result = transactionManagerMap.get(persistenceUnit ?: "")
		if (!result) {
			result = doRetrieveTransactionTemplate(persistenceUnit)
			transactionManagerMap.put(persistenceUnit ?: "", result)
		}

		return result
	}

	/**
	 * Find transaction template inside bean factory
	 * @param persistenceUnit the persistence unit name
	 * @return the transaction template
	 */
	private def doRetrieveTransactionTemplate(String persistenceUnit) {
		assert beanFactory

		//find ebean server factory
		def factory
		if (persistenceUnit) {
			//If persistence unit is given, find server with persistence unit name
			try {
				factory = beanFactory.getBean("&$persistenceUnit");
			} catch (e) {
			}
		} else {
			//if default, find default server
			factory = beanFactory.getBeansOfType(EbeanServerFactoryBean.class).values().find{it.serverConfig.defaultServer}
		}

		if (!factory) {
			return null
		}

		//Retrieve config
		def config = factory.serverConfig
		if (!config) {
			return null
		}

		//Retrieve data source
		def dataSource = config.dataSource
		if (!dataSource) {
			return null
		}

		//Find data source transaction manager
		def txManager = beanFactory.getBeansOfType(DataSourceTransactionManager.class).values().find{it.dataSource == dataSource}
		if (!txManager) {
			return null
		}

		//find transaction template
		return beanFactory.getBeansOfType(TransactionTemplate.class).values().find{it.transactionManager == txManager}
	}


	/**
	 * Execute business logic outside any transaction
	 * @param c the business logic to be executed
	 * @return the logic result
	 */
	protected def executeBusinessLogic(Closure c) {

		assert c != null
		
		try{
			return c.call()
		}
		catch(Error error){
			log.debug "Intercepted error $error.message during transaction execution. Delegating exception treatement upwards"
			throw error
		}
		catch(RuntimeException re){
			log.debug "Intercepted exception $re.message during transaction execution. Delegating exception treatement upwards"
			throw re
		}

	}

	/**
	 * Exeucte business logic against given EBean Server transaction
	 * @param server the ebean server for execution
	 * @param c the business logic
	 * @return the logic result
	 */
	protected def executeLogicIntoServer(def server, Closure c) {

		assert c != null
		
		//Execute logic outside server context
		if (!server) {
			return executeBusinessLogic(c)
		}

		//Execute logic inside server context
		def result 
		server.execute(new TxRunnable(){
			void run() {
				try{

					result = c.call()
				}
				catch(Error error){
					log.debug "Intercepted error $error.message during transaction execution. Delegating exception treatement upwards"
					throw error
				}
				catch(RuntimeException re){
					log.debug "Intercepted exception $re.message during transaction execution. Delegating exception treatement upwards"
					throw re
				}
			};
		})
		
		return result
	}

	/**
	 * Execute business logic inside a transaction
	 * @param transactionTemplate the transaction template for transaction management
	 * @param c the business logic to be executed
	 * @return the logic result
	 */
	protected def executeInTransaction(def transactionTemplate, Closure c) {
		
		assert c != null
		
		//If no template defined...execute outside transaction
		if (!transactionTemplate) {
			return c.call()
		}
		
		//Execute inside transaction
		def result
		return transactionTemplate.execute ({
			result = c.call()
		} as TransactionCallback)

		return result
	}
	
	/**
	 * Retrieve the EBean server to be used to execute transactions against given persistenceUnit
	 * @param persistenceUnit the persistence unit name
	 * @return the bean server or null if no server should be used
	 */
	protected def retrieveEBeanServer(String persistenceUnit) {
		
		
		def server 
		try {
			server = Ebean.getServer(persistenceUnit)
		} catch (e) {
			log.error "Cannot find server for $persistenceUnit", e
		}	
			
		if (!server) {
			def ex = new PersistenceException("Cannot find Ebean server for $persistenceUnit persistence unit")
			log.error "Cannot execute transaction" , ex
			throw ex
		}
		
		return server
		
	}

	@Override
	public Object doInTransaction(String persistenceUnit, Closure c) {

		//Retrieve transaction template
		def transactionTemplate = retrieveTransactionTemplate(persistenceUnit)
		
		//Find server
		def server = retrieveEBeanServer(persistenceUnit)
		
		//Execute logic
		return executeInTransaction(transactionTemplate, {executeLogicIntoServer(server, c)})
	}

	@Override
	public Object doInTransaction(Closure c) {
		doInTransaction(null, c)
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory
	}


}

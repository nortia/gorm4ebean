package es.nortia_in.orm.service



/**
 * Interface for transactional services
 * @author angel
 *
 */
interface PersistenceService {

	/**
	 * Executes the code include in the Closure param inside a transaction
	 * @param c Closure with the unit work to execute inside a transaction
	 */
	def doInTransaction(Closure c)
	
	/**
	* Executes the code include in the Closure param inside a transaction. Code will be transactional inside 
	* the server related to given persistence unit
	* @param persistenceUnit the persistence unit for transactional context
	* @param c Closure with the unit work to execute inside a transaction
	*/
	def doInTransaction(String persistenceUnit, Closure c)

}
package MODEL

import es.nortia_in.orm.service.EbeanPersistenceService;

class MockPersistentService extends EbeanPersistenceService {

	boolean executed = false
	
	void query(String persistenceUnit) {
		doInTransaction(persistenceUnit,{executed=true})
	}
	
	void query() {
		doInTransaction({executed=true})
	}
	
}

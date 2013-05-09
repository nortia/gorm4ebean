package MODEL

import java.util.Set;

import com.avaje.ebean.event.BeanPersistListener

abstract class AbstractMockPersistListener<T> implements BeanPersistListener<T>{

	@Override
	public boolean deleted(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inserted(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remoteDelete(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remoteInsert(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remoteUpdate(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean updated(Object arg0, Set arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}

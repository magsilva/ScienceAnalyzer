package net.sf.sysrev.util.persistence;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
public interface PersistenceService<K, E>
{
	E save(E entity);

	void delete(E entity);
	
	void refresh(E entity);

	E findById(K key);
	
	List<E> find(String query, Object... parameters);
	
	List<E> findAll();
}

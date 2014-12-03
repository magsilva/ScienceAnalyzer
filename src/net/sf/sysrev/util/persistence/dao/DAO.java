package net.sf.sysrev.util.persistence.dao;

import java.util.List;

public interface DAO<K, E>
{
	void persist(E entity);
	
	E merge(E entity);

	void remove(E entity);
	
	void refresh(E entity);
	
	void flush(E entity);
	
	boolean contains(E entity);

	E findById(K id);
	
	List<E> find(String query, Object... params);
	
	List<E> findAll();
}

package net.sf.sysrev.util.persistence.dao.jpa;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import net.sf.sysrev.introspector.ClassMetadata;
import net.sf.sysrev.util.persistence.dao.DAO;

@Repository
public class JPA_DAO<K, E> implements DAO<K, E>
{
	protected Class<E> entityClass;

	@PersistenceContext
	protected EntityManager entityManager;

	public JPA_DAO()
	{
		ClassMetadata classMetadata = new ClassMetadata(getClass());
		this.entityClass = (Class) classMetadata.getType();
	}

	public void persist(E entity)
	{
		entityManager.persist(entity);
	}

	public void remove(E entity)
	{
		entityManager.remove(entity);
	}

	public void flush()
	{
		entityManager.flush();
	}

	public void refresh(E entity)
	{
		entityManager.refresh(entity);
	}

	public E merge(E entity)
	{
		return entityManager.merge(entity);
	}

	public boolean contains(E entity)
	{
		return entityManager.contains(entity);
	}
	
	/**
	 * Sets all the parameters of a query
	 */
	private void setParameters(Query query, Object... params)
	{
		if (params != null) {
			for (int i = 0; i < params.length; i++)
				query.setParameter(i + 1, params[i]);
		}
	}

	public E findById(K id)
	{
		return entityManager.find(entityClass, id);
	}

	/**
	 * Retrieves a non-paged query. Use with care, this method could potentially instantiate large
	 * amounts of data.
	 */
	public List<E> find(String queryString, Object... params)
	{
		Query query = entityManager.createQuery(queryString);
		setParameters(query, params);
		return query.getResultList();
	}

	public List<E> findAll()
	{
		return (List<E>) find("from " + entityClass.getName());
	}

	public void flush(E entity)
	{
		entityManager.flush();		
	}
	
	public EntityManager getEntityManager()
	{
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

}

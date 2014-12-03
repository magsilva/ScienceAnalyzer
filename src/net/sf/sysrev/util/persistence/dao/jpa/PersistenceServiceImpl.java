package net.sf.sysrev.util.persistence.dao.jpa;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ironiacorp.commons.AnnotationUtil;

import net.sf.sysrev.util.persistence.PersistenceService;

@Transactional(propagation = Propagation.REQUIRED)
public class PersistenceServiceImpl<K, E> implements PersistenceService<K, E>
{
	private JPA_DAO<K, E> dao = new JPA_DAO<K, E>();
	
	/**
	 * http://blog.xebia.com/2009/03/23/jpa-implementation-patterns-saving-detached-entities/
	 */
	public E save(final E entity) 
	{
		// If object in in the persistent context, just merge and exit.
		if (dao.contains(entity)) {
			return dao.merge(entity);
		}
		
		// If object is not in the persistent context, it may have been persisted sometime
		// in the past (what we will check by it's id) or it is a new entity.
		Field[] fields = null;
		try {
			Class<? extends Object> clazz = entity.getClass();
			Class<?> annClazz = Class.forName("javax.persistence.Id");
			fields = AnnotationUtil.getAnnotatedFields(clazz, annClazz);
		} catch (ClassNotFoundException e) {
			assert false : "Class javax.persistence.Id should exist";
		}
		for (Field f : fields) {
			try {
				Object id = f.get(entity);
				if (id != null) {
					// If the object has an id, that can be generated only by the persistence
					// manager, than we can just merge.
					return dao.merge(entity);
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} 
		}

		// If the object has never been persisted, persist it now.
		dao.persist(entity);
		return entity;
	}

	public void delete(E entity)
	{
		dao.remove(entity);
	}

	public E findById(K id)
	{
		return dao.findById(id);
	}

	public List<E> findAll()
	{
		return dao.findAll();
	}

	public List<E> find(String query, Object... parameters)
	{
		return dao.find(query, parameters);
	}

	public void refresh(E entity)
	{
		dao.refresh(entity);
	}
}

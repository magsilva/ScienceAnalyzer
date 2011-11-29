package com.ironiacorp.scienceanalyzer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.ironiacorp.scienceanalyzer.library.Journal;

public class PersonDAO
{
	@PersistenceContext
	private EntityManager em;
	
	public List<Person> find(String name)
	{
		TypedQuery<Person> q = em.createQuery("SELECT p FROM Person AS p WHERE LOWER(p.name) = LOWER(:name)", Person.class);
		q.setParameter("name", name);
		return q.getResultList();
	}
}

package br.usp.icmc.ranking.era;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import au.com.bytecode.opencsv.CSVReader;
import br.usp.icmc.library.Event;
import br.usp.icmc.ranking.CsvRankImporter;


public class EraEventsImporter extends CsvRankImporter
{
    private EntityManagerFactory factory;

	private EntityManager em;
	
	public EraEventsImporter()
	{
	    factory = Persistence.createEntityManagerFactory("academe");
	}
	
	public List<Event> find(String name)
	{
		Query q = em.createQuery("SELECT j FROM Event AS j WHERE LOWER(j.name) = LOWER(:name)");
		q.setParameter("name", name);
		@SuppressWarnings("unchecked")
		List<Event> events = (List<Event>) q.getResultList();
		return events;
	}
	
	public void importRankings() throws IOException
	{
		CSVReader reader;
	    String[] line;

	    reader = new CSVReader(new FileReader(file));
	    em = factory.createEntityManager();
	    while ((line = reader.readNext()) != null) {
	    	String name = line[0].trim();
	    	String acronym = line[1].trim();
	    	String rank = line[2].trim();

	    	em.getTransaction().begin();
	    	try {
				Event event;
		    	List<Event> events = find(name);
		    	if (events.size() > 1) {
		    		throw new IllegalArgumentException();
		    	}
		    		    	
		    	if (events.size() == 1) {
		    		event = events.get(0);
		    	} else {
		    		event = new Event();
					event.setName(name);
		    		em.persist(event);
		    	}

	    		if (event.getAcronym() == null) {
	    			event.setAcronym(acronym);
	    		}

		    	
				if (! "Not ranked".equalsIgnoreCase(rank)) {
			    	EraRanking ranking = new EraRanking();
			    	ranking.setYear(year);
			    	ranking.setRanking(rank);
			    	ranking.setPublication(event);
		    		em.persist(ranking);  	
				}
				em.getTransaction().commit();
	    	} catch (IllegalArgumentException e) {
				em.getTransaction().rollback();
	    		System.out.println("Err: " + name);
	    	}
	    }
		em.close();
		reader.close();
		System.out.println();
	}
}

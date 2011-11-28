package com.ironiacorp.scienceanalyzer.ranking.era;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.ironiacorp.scienceanalyzer.library.Issn;
import com.ironiacorp.scienceanalyzer.library.Journal;
import com.ironiacorp.scienceanalyzer.ranking.CsvRankImporter;

import au.com.bytecode.opencsv.CSVReader;


public class EraJournalsImporter extends CsvRankImporter
{
    private EntityManagerFactory factory;

	private EntityManager em;
	

	public EraJournalsImporter()
	{
	    factory = Persistence.createEntityManagerFactory("academe");
	}
	
	public List<Journal> find(String name)
	{
		Query q = em.createQuery("SELECT j FROM Journal AS j WHERE LOWER(j.name) = LOWER(:name)");
		q.setParameter("name", name);
		@SuppressWarnings("unchecked")
		List<Journal> journals = (List<Journal>) q.getResultList();
		return journals;
	}
	
	public void importRankings() throws IOException
	{
		CSVReader reader;
	    String [] line;

	    reader = new CSVReader(new FileReader(file));
	    em = factory.createEntityManager();
	    while ((line = reader.readNext()) != null) {
	    	String rank = line[1].trim();
		    String name = line[2].trim();
	    	LinkedHashSet<String> issns = new LinkedHashSet<String>();
	    	issns.add(line[3].trim());
	    	issns.add(line[4].trim());
	    	issns.add(line[5].trim());
	    	issns.add(line[6].trim());

	    	em.getTransaction().begin();
	    	try {
				Journal journal;
		    	List<Journal> journals = find(name);
		    	if (journals.size() > 1) {
		    		throw new IllegalArgumentException();
		    	}
		    		    	
		    	if (journals.size() == 1) {
		    		journal = journals.get(0);
		    	} else {
		    		journal = new Journal();
					journal.setName(name);
		    		em.persist(journal);
		    	}
		    	
		    	Iterator<String> i = issns.iterator();
		    	while (i.hasNext()) {
		    		Set<Issn> journalIssn = journal.getIssn();
		    		String value = i.next();
		    		if (value != null && ! value.isEmpty()) {
		    			Issn issn = new Issn();
		    			issn.setValue(value.trim());
		    			journalIssn.add(issn);
		    		}
		    	}
		    	
				if (! "Not ranked".equalsIgnoreCase(rank)) {
			    	EraRanking ranking = new EraRanking();
			    	ranking.setYear(year);
			    	ranking.setRanking(rank);
			    	ranking.setPublication(journal);
			    	ranking.setValidityStart(year);
			    	ranking.setValidityEnd(year);
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

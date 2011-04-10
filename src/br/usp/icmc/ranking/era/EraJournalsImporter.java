package br.usp.icmc.ranking.era;

import java.io.File;
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

import au.com.bytecode.opencsv.CSVReader;
import br.usp.icmc.library.Issn;
import br.usp.icmc.library.Journal;
import br.usp.icmc.ranking.RankImporter;


public class EraJournalsImporter implements RankImporter
{
    private EntityManagerFactory factory;

	private EntityManager em;
	
	private int year;
	
	
	
	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

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
	
	public void read(File srcfile) throws IOException
	{
		CSVReader reader;
	    String [] line;

	    reader = new CSVReader(new FileReader(srcfile));
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

	public static void main(String[] args) throws IOException
	{
		EraJournalsImporter importer = new EraJournalsImporter();
		importer.setYear(2010);
		importer.read(new File("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Ranking/ERA/ERA 2010 - Periódicos.csv"));
	}
	
}

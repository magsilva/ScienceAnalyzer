package br.usp.icmc.ranking.qualis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import au.com.bytecode.opencsv.CSVReader;
import br.usp.icmc.library.Journal;

public class QualisImporter
{
    private EntityManagerFactory factory;

	private EntityManager em;

	private static final Pattern[] NAME_FIELD_DEPRECATED = {
			Pattern.compile("\\(.*cessou.*\\)", Pattern.CASE_INSENSITIVE),
	};

	private static final Pattern[] NAME_FIELD_ONLINE = {
			Pattern.compile("\\((.*)en línea(.*)\\)", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\((.*)online(.*)\\)", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\((.*)internet(.*)\\)", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\((.*)electronic ed\\.(.*)\\)", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\((.*)online(.*)\\)", Pattern.CASE_INSENSITIVE),
	};

	private static final Pattern[] NAME_FIELD_PRINT = {
			Pattern.compile("\\((.*)print(.*)\\)", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\((.*)print(.*)\\)", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\((.*)Printed ed\\.(.*)\\)", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\((.*)impresso(.*)\\)", Pattern.CASE_INSENSITIVE),
	};
	
	private static final Pattern NAME_FIELD_TRIM = Pattern.compile("\\(.*\\)");
	
	private Journal journal;

	public QualisImporter()
	{
	    factory = Persistence.createEntityManagerFactory("academe");
	}
	
	
	private void processNameField(String name)
	{
		Matcher m = NAME_FIELD_TRIM.matcher(name);
		if (m.find()) {
			name = m.replaceAll("");
		}
		journal.setName(name);
	}
		
	private void processNameFieldWithMediaType(String name, Matcher m)
	{
		String before = m.group(1);
		String after = m.group(2);
		name = m.replaceAll("");
		processNameField(name);
		if (before != null) {
			before = before.replaceAll("(\\p{Punct})+$", "");
			before = before.replaceAll("^(\\p{Punct})+", "");
			if (before.length() > 0) {
				try {
					Integer.valueOf(before);
				} catch (Exception e) {
					journal.setAddress(before);
				}
			}
		} else if (after != null) {
			after = after.replaceAll("(\\p{Punct})+$", "");
			after = after.replaceAll("^(\\p{Punct})+", "");
			if (after.length() > 0) {
				try {
					Integer.valueOf(after);
				} catch (Exception e) {
					journal.setAddress(after);
				}
			}
		}
	}
	
	
	private void processIssnNameField(String issn, String name)
	{
		for (Pattern p : NAME_FIELD_DEPRECATED) {
			Matcher m = p.matcher(name);
			if (m.find()) {
				throw new IllegalArgumentException();
			}
		}
		
		for (Pattern p : NAME_FIELD_ONLINE) {
			Matcher m = p.matcher(name);
			if (m.find()) {
				journal.setOnlineIssn(issn);
				processNameFieldWithMediaType(name, m);
				return;
			}
		}
		
		for (Pattern p : NAME_FIELD_PRINT) {
			Matcher m = p.matcher(name);
			if (m.find()) {
				journal.setPrintIssn(issn);
				processNameFieldWithMediaType(name, m);
				return;
			}
		}
	
		processNameField(name);
		journal.setPrintIssn(issn);
	}
	
	public List<Journal> find(String name)
	{
		Query q = em.createQuery("SELECT j FROM Journal AS j WHERE LOWER(j.name) = LOWER(:name)");
		q.setParameter("name", name);
		List<Journal> journals = (List<Journal>) q.getResultList();
		return journals;
	}
	
	public void read(String filename) throws IOException
	{
		File srcfile = new File(filename);
		CSVReader reader;
	    String [] line;

		System.out.println("Processing " + filename);
	    reader = new CSVReader(new FileReader(srcfile));
	    em = factory.createEntityManager();
	    while ((line = reader.readNext()) != null) {
	    	String issn = line[0].trim();
	    	String area = line[1].trim();
	    	String name = line[2].trim();
	    	String rank = line[3].trim();
	    	String year = line[4].trim();

	    	try {
		    	QualisRanking ranking = new QualisRanking();

		    	em.getTransaction().begin();
				journal = new Journal();
		    	processIssnNameField(issn, name);
		    	List<Journal> journals = find(journal.getName());
		    	if (journals.size() > 1) {
		    		throw new IllegalArgumentException();
		    	}
		    		    	
		    	if (journals.size() == 1) {
		    		Journal persistedJournal = journals.get(0);
		    		if (journal.getOnlineIssn() != null) {
		    			persistedJournal.setOnlineIssn(journal.getOnlineIssn());
		    		}
		    		if (journal.getPrintIssn() != null) {
		    			persistedJournal.setPrintIssn(journal.getPrintIssn());
		    		}
		    		ranking.setPublication(persistedJournal);
		    		// System.out.print("Updating journal " + persistedJournal.getName());
		    	} else {
			    	ranking.setPublication(journal);
		    		em.persist(journal);
		    		// System.out.print("Creating journal " + journal.getName());
		    	}
		    	ranking.setYear(Integer.parseInt(year));
		    	if (ranking.getYear() >= 2007 && ranking.getYear() <= 2009) {
			    	ranking.setValidityStart(2007);
			    	ranking.setValidityEnd(2009);
		    	}
		    	ranking.setRanking(rank);
		    	ranking.setArea(area);
		    	// System.out.println(" (" + ranking.getRanking() + ")");
	    		em.persist(ranking);  	
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
		QualisImporter importer =  new QualisImporter();
		importer.read("/media/magsilva/Education/CPG/Egressos/Qualis/Qualis 2009 - Computação - Periódicos.csv");
		importer.read("/media/magsilva/Education/CPG/Egressos/Qualis/Qualis 2009 - Educação - Periódicos.csv");
		importer.read("/media/magsilva/Education/CPG/Egressos/Qualis/Qualis 2009 - Engenharias IV - Periódicos.csv");
		importer.read("/media/magsilva/Education/CPG/Egressos/Qualis/Qualis 2009 - Interdisciplinar - Periódicos.csv");
	}
	
}

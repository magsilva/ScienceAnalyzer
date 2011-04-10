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
import br.usp.icmc.library.Issn;
import br.usp.icmc.library.Journal;
import br.usp.icmc.ranking.RankImporter;

public class QualisJournalImporter implements RankImporter
{
    private EntityManagerFactory factory;

	private EntityManager em;
	
	private int year;
	
	private String area;
	
	private Journal journal;

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
	
	private static final Pattern ACRONYM_NAME = Pattern.compile("$(\\d+)\\s+(\\w+)\\s+(.*)", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern NAME_FIELD_TRIM = Pattern.compile("\\(.*\\)");
	
	public QualisJournalImporter()
	{
	    factory = Persistence.createEntityManagerFactory("academe");
	}
	
	
	private String getCleanNameField2009(String name)
	{
		Matcher m = NAME_FIELD_TRIM.matcher(name);
		if (m.find()) {
			name = m.replaceAll("");
		}
		return name;
	}
		
	private String getNameField2009(String name)
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
				return getNameField2009(name);
			}
		}
		
		for (Pattern p : NAME_FIELD_PRINT) {
			Matcher m = p.matcher(name);
			if (m.find()) {
				return getNameField2009(name);
			}
		}
	
		return getCleanNameField2009(name);
	}
	
	private String getAcronymFrom2003(String nameField)
	{
		Matcher m = ACRONYM_NAME.matcher(nameField);
		if (m.find()) {
			return m.group(2);
		}
		return null;
	}
	
	private String getNameFrom2003(String nameField)
	{
		Matcher m = ACRONYM_NAME.matcher(nameField);
		if (m.find()) {
			return m.group(3);
		}
		return null;
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
	    	String issnField;
	    	String areaField;
	    	String nameField;
	    	String acronymField;
	    	String rankField;
	    	String yearField;
	    	switch (year) {
	    		case 2003:
	    			issnField = null;
	    			areaField = area;
	    			acronymField = getAcronymFrom2003(line[0].trim());
	    			nameField = getNameFrom2003(line[0].trim());
	    			rankField = line[1].trim();
	    			yearField = Integer.toString(year);
	    			break;
	    		case 2007:
	    			issnField = line[0].trim();
	    			areaField = line[1].trim();
	    			if (area != null && ! area.equals(areaField)) {
	    				throw new IllegalArgumentException();
	    			}
	    			acronymField = null;
	    			nameField = line[2].trim();
	    			rankField = line[3].trim();
	    			yearField = line[4].trim();
	    			if (year != 0 && ! yearField.equals(Integer.toString(year))) {
	    				throw new IllegalArgumentException();
	    			}
	    			break;
	    		case 2009:
	    			issnField = line[0].trim();
	    			areaField = line[1].trim();
	    			if (area != null && ! area.equals(areaField)) {
	    				throw new IllegalArgumentException();
	    			}
	    			nameField = getNameField2009(line[2].trim());
	    			rankField = line[3].trim();
	    			yearField = line[4].trim();
	    			if (year != 0 && ! yearField.equals(Integer.toString(year))) {
	    				throw new IllegalArgumentException();
	    			}
	    			break;
	    		default:
	    			throw new IllegalArgumentException();
	    	}

	    	try {
		    	em.getTransaction().begin();
		    	List<Journal> journals = find(nameField);
		    	Issn issn;
		    	if (journals.size() > 1) {
		    		throw new IllegalArgumentException();
		    	}
		    		    	
		    	if (journals.size() == 1) {
		    		journal = journals.get(0);
		    	} else {
		    		journal = new Journal();
		    		journal.setName(nameField);
		    		em.persist(journal);
		    	}
		    	issn = new Issn();
		    	issn.setValue(issnField);
		    	journal.addIssn(issn);

		    	QualisRanking ranking = new QualisRanking();
		    	ranking.setPublication(journal);
		    	ranking.setYear(Integer.parseInt(yearField));
		    	if (ranking.getYear() >= 2001 && ranking.getYear() <= 2003) {
			    	ranking.setValidityStart(2001);
			    	ranking.setValidityEnd(2003);
		    	}
		    	if (ranking.getYear() >= 2004 && ranking.getYear() <= 2006) {
			    	ranking.setValidityStart(2004);
			    	ranking.setValidityEnd(2006);
		    	}
		    	if (ranking.getYear() >= 2007 && ranking.getYear() <= 2009) {
			    	ranking.setValidityStart(2007);
			    	ranking.setValidityEnd(2009);
		    	}
		    	ranking.setRanking(rankField);
		    	ranking.setArea(areaField);
	    		em.persist(ranking);  	
				em.getTransaction().commit();
	    	} catch (IllegalArgumentException e) {
				em.getTransaction().rollback();
	    		System.out.println("Err: " + nameField);
	    	}
	    }
		em.close();
		reader.close();
		System.out.println();
	}

	
	
	public int getYear()
	{
		return year;
	}


	public void setYear(int year)
	{
		this.year = year;
	}


	public String getArea()
	{
		return area;
	}


	public void setArea(String area)
	{
		this.area = area;
	}


	public static void main(String[] args) throws IOException
	{
		QualisJournalImporter importer =  new QualisJournalImporter();
		importer.setYear(2009);
		importer.read(new File("/media/magsilva/Education/CPG/Egressos/Qualis/Qualis 2009 - Computação - Periódicos.csv"));
		importer.read(new File("/media/magsilva/Education/CPG/Egressos/Qualis/Qualis 2009 - Educação - Periódicos.csv"));
		importer.read(new File("/media/magsilva/Education/CPG/Egressos/Qualis/Qualis 2009 - Engenharias IV - Periódicos.csv"));
		importer.read(new File("/media/magsilva/Education/CPG/Egressos/Qualis/Qualis 2009 - Interdisciplinar - Periódicos.csv"));
	}

}

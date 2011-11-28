package com.ironiacorp.scienceanalyzer.ranking.qualis;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.ironiacorp.scienceanalyzer.library.Event;
import com.ironiacorp.scienceanalyzer.ranking.CsvRankImporter;

import au.com.bytecode.opencsv.CSVReader;

public class QualisEventsImporter extends CsvRankImporter
{
    private EntityManagerFactory factory;

	private EntityManager em;
	
	private String area;
	
	private static final Pattern ACRONYM_NAME = Pattern.compile("(\\d+)\\s+(\\w+)\\s+(.*)", Pattern.CASE_INSENSITIVE);
	
	public QualisEventsImporter()
	{
	    factory = Persistence.createEntityManagerFactory("academe");
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
	
	public List<Event> find(String name)
	{
		Query q = em.createQuery("SELECT e FROM Event AS e WHERE LOWER(e.name) = LOWER(:name)");
		q.setParameter("name", name);
		@SuppressWarnings("unchecked")
		List<Event> events = (List<Event>) q.getResultList();
		return events;
	}
	
	public void importRankings() throws IOException
	{
		CSVReader reader;
	    String [] line;

	    reader = new CSVReader(new FileReader(file));
	    em = factory.createEntityManager();
	    while ((line = reader.readNext()) != null) {
	    	String areaField;
	    	String nameField;
	    	String acronymField;
	    	String rankField;
	    	String scopeField;
	    	switch (year) {
	    		case 2003:
	    			areaField = area;
	       			nameField = getNameFrom2003(line[0].trim());
	    			acronymField = getAcronymFrom2003(line[0].trim());
	    			rankField = line[1].trim();
	    			scopeField = null;
	    			break;
	    		case 2007:
	    			areaField = line[0].trim();
	       			nameField = line[1].trim();
	    			acronymField = null;
	    			rankField = line[2].trim();
	    			scopeField = line[3].trim();
	    			break;
	    		case 2009:
	    			areaField = area;
	       			nameField = line[2].trim();
	    			acronymField = line[1].trim();
	    			rankField = line[3].trim();
	    			scopeField = null;
	    			break;
	    		default:
	    			throw new IllegalArgumentException();
	    	}

	    	try {
		    	em.getTransaction().begin();
		    	Event event;
		    	List<Event> events = find(nameField);
		    	if (events.size() > 1) {
		    		throw new IllegalArgumentException();
		    	}
		    		    	
		    	if (events.size() == 1) {
		    		event = events.get(0);
		    	} else {
		    		event = new Event();
		    		event.setName(nameField);
		    		em.persist(event);
		    	}
		    	
		    	if (event.getAcronym() == null) {
		    		event.setAcronym(acronymField);
		    	}
		    	if (event.getScope() == null) {
		    		event.setScope(scopeField);
		    	}
		    	
		    	QualisRanking ranking = new QualisRanking();
		    	ranking.setPublication(event);
		    	ranking.setYear(year);
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

	public String getArea()
	{
		return area;
	}


	public void setArea(String area)
	{
		this.area = area;
	}
}

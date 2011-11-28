package com.ironiacorp.scienceanalyzer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ironiacorp.scienceanalyzer.Degree.DegreeType;
import com.ironiacorp.scienceanalyzer.library.Dissertation;


import au.com.bytecode.opencsv.CSVReader;

public class Advisor
{
	public static void main(String[] args) throws Exception
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("Alumni/ICMC/alumni-icmc-posgrad-ccmc.csv");
		CSVReader reader = new CSVReader(new InputStreamReader(is));
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    	Map<Person, Set<Person>> alumniByAdvisor = new TreeMap<Person, Set<Person>>();
    	Iterator<Person> i;
		String[] line;
		
		reader.readNext();
		while ((line = reader.readNext()) != null) {
	    	String adviseeDegree = line[0].trim();
	    	String dissertationTitle = line[1].trim();
	    	String adviseeName = line[2].trim();
	    	String advisorName = line[3].trim();
	    	String degreeDate = line[4].trim();
	    	String emails = line[8].trim();
	   	
	    	Person advisee = new Person();
	    	Person advisor = new Person();
	    	Degree degree = new Degree();
	    	Dissertation dissertation = new Dissertation();
	    	
	    	dissertation.setTitle(dissertationTitle);
	    	
	    	degree.setAdvisor(advisor);
	    	degree.setDate(dateFormatter.parse(degreeDate));
	    	degree.setDissertation(dissertation);
	    	if ("ME".equals(adviseeDegree)) {
	    		degree.setType(DegreeType.MSC);
	    	}
	    	if ("DO".equals(adviseeDegree)) {
	    		degree.setType(DegreeType.PHD);
	    	}

	    	advisor.setName(advisorName);
	    	
	    	advisee.setName(adviseeName);
	    	advisee.addDegree(degree);
	    	for (String email : emails.split(",")) {
	    		advisee.addEmail(email);
	    	}
	    	
	    	if (! alumniByAdvisor.containsKey(advisor)) {
	    		alumniByAdvisor.put(advisor, new TreeSet<Person>());
	    	}
	    	alumniByAdvisor.get(advisor).add(advisee);
		}
		
		i = alumniByAdvisor.keySet().iterator();
		while (i.hasNext()) {
			Person advisor = i.next();
			Set<Person> advisees = alumniByAdvisor.get(advisor);
			if (advisees.size() > 0) {
				System.out.println(advisor.getName());
				for (Person advisee : advisees) {
					System.out.println("\t" + advisee.getName());
					
					System.out.println("\tEmails:");
					for (String email : advisee.getEmails()) {
						System.out.println("\t\t" + email);
					}
					if (advisee.getEmails().isEmpty()) {
						System.out.println("\t\t(nenhum email foi informado ou encontrado)");
					}
					
					System.out.println("\tTítulos obtidos sob sua orientação:");
					Set<Degree> degrees = advisee.getDegrees();
					for (Degree degree : degrees) {
						if (degree.getAdvisor().equals(advisor)) {
							System.out.println("\t\t" + degree.toString());
						}
					}
					System.out.println();
				}
			}
		}
	}
}

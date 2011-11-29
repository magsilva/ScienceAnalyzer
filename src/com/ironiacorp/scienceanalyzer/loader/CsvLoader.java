package com.ironiacorp.scienceanalyzer.loader;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ironiacorp.scienceanalyzer.Institution;
import com.ironiacorp.scienceanalyzer.Job;
import com.ironiacorp.scienceanalyzer.Person;
import com.ironiacorp.scienceanalyzer.PhoneNumber;
import com.ironiacorp.scienceanalyzer.DegreeType;
import com.ironiacorp.scienceanalyzer.education.Degree;
import com.ironiacorp.scienceanalyzer.education.MasterDegree;
import com.ironiacorp.scienceanalyzer.education.PhdDegree;
import com.ironiacorp.scienceanalyzer.geo.Location;
import com.ironiacorp.scienceanalyzer.library.Dissertation;
import com.ironiacorp.scienceanalyzer.social.FacebookAccount;
import com.ironiacorp.scienceanalyzer.social.LattesAccount;
import com.ironiacorp.scienceanalyzer.social.LinkedInAccount;
import com.ironiacorp.scienceanalyzer.social.OrkutAccount;
import com.ironiacorp.scienceanalyzer.social.TwitterAccount;
import com.ironiacorp.scienceanalyzer.social.UspAccount;


public class CsvLoader
{
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	private EntityManager em;
	
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy"); 

	private static final DateFormat ALT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy"); 

	protected void process(Map<String, Object> data)
	{
		Person advisee = new Person();
		Person advisor = new Person();
		Person coadvisor = new Person();
		Degree degree;
        Dissertation dissertation = new Dissertation();
		Date degreeDate;
        // TODO:
        // Job job = new Job();
        // Institution workplace = new Institution();
        // Location workplaceLocation = new Location();
        // PhoneNumber workplacePhoneNumber = new PhoneNumber();

		// Process data
		String[] advisors = ((String) data.get("Orientador")).split(","); 
        advisor.setName(advisors[0]);
        if (advisors.length > 1) {
        	coadvisor.setName(advisors[1]);
        }

        dissertation.setTitle((String) data.get("Título do trabalho"));
        dissertation.addAuthor(advisee);
        dissertation.setAdvisor(advisor);

        if (((String) data.get("Grau")).compareToIgnoreCase("ME") == 0) {
        	degree = new MasterDegree();
        } else if (((String) data.get("Grau")).compareToIgnoreCase("DO") == 0) {
        	degree = new PhdDegree();
        } else {
        	throw new IllegalArgumentException("Invalid or unknown degree");
        }
        try {
        	degreeDate = DEFAULT_DATE_FORMAT.parse((String) data.get("Data de defesa"));
        } catch (ParseException e1) {
        	try {
        		degreeDate = ALT_DATE_FORMAT.parse((String) data.get("Data de defesa"));
        	} catch (ParseException e2) {
        		throw new IllegalArgumentException("Invalid degree date");
        	}
        	degree.setDate(degreeDate);
        }
        degree.setDissertation(dissertation);
        degree.setAdvisor(advisor);
        degree.setCoadvisor(coadvisor);
        degree.setCourseName((String) data.get("Curso"));

        
        advisee.setName((String)  data.get("Nome"));
        advisee.addDegree(degree);
     
        if (data.containsKey("Email")) {
        	String email = (String) data.get("Email");
        	advisee.addEmail(email);
        }

        if (data.containsKey("Telefone")) {
        	PhoneNumber phone = new PhoneNumber();
        	try {
        		phone.parsePhoneNumber((String) data.get("Telefone"));
        		// TODO:
        	} catch (Exception e) {
        		phone = null;
        	}
        }

    	// TODO:
        if (data.containsKey("Situação/Função/Local")) {
        }

        if (data.containsKey("Nro. USP")) {
        	try {
            	UspAccount usp = new UspAccount();
        		int uspId = Integer.parseInt((String) data.get("Nro. USP"));
        		usp.setUserName(Integer.toString(uspId));
        		advisee.addSocialAccount(usp);
        	} catch (NumberFormatException nfe) {
        		//throw new IllegalArgumentException("Invalid USP id");
        	}
        }

        if (data.containsKey("Identificador Lattes")) {
        	try {
            	LattesAccount lattes = new LattesAccount();
        		int lattesId = Integer.parseInt((String) data.get("Identificador Lattes"));
        		lattes.setUserName(Integer.toString(lattesId));
        		advisee.addSocialAccount(lattes);
        	} catch (NumberFormatException nfe) {
        		//throw new IllegalArgumentException("Invalid Lattes id");
        	}
        }

        if (data.containsKey("Facebook")) {
        	try {
            	FacebookAccount facebook = new FacebookAccount();
        		String facebookId = (String) data.get("Facebook");
        		facebook.setUserName(facebookId);
        		advisee.addSocialAccount(facebook);
        	} catch (NumberFormatException nfe) {
        		//throw new IllegalArgumentException("Invalid Facebook id");
        	}
        }

        if (data.containsKey("Orkut")) {
        	try {
            	OrkutAccount orkut = new OrkutAccount();
        		String orkutId = (String) data.get("Orkut");
        		orkut.setUserName(orkutId);
        		advisee.addSocialAccount(orkut);
        	} catch (NumberFormatException nfe) {
        		//throw new IllegalArgumentException("Invalid Orkut id");
        	}
        }
        
        
        if (data.containsKey("LinkedIn")) {
        	try {
            	LinkedInAccount linkedin = new LinkedInAccount();
        		String linkedInId = (String) data.get("LinkedIn");
        		linkedin.setUserName(linkedInId);
        		advisee.addSocialAccount(linkedin);
        	} catch (NumberFormatException nfe) {
        		//throw new IllegalArgumentException("Invalid LinkedIn id");
        	}
        }
        
        if (data.containsKey("Twitter")) {
        	try {
            	TwitterAccount twitter = new TwitterAccount();
        		String twitterId = (String) data.get("Twitter");
        		twitter.setUserName(twitterId);
        		advisee.addSocialAccount(twitter);
        	} catch (NumberFormatException nfe) {
        		// throw new IllegalArgumentException("Invalid Twitter id");
        	}
        }
        
		em.persist(advisee);
		em.persist(advisor);
		em.persist(coadvisor);
		em.persist(degree);
		em.persist(dissertation);
	}
	
    public void process(InputStream is) throws IOException
    {
    	LinkedHashSet<String> fields = new LinkedHashSet<String>();
    	Iterator<String> i;
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		String[] line;
		
		line = reader.readNext();
		for (String field : line) {
			if (! fields.add(field)) {
				throw new IllegalArgumentException("Duplicate field (column name) found in input stream: " + field);
			}
		}

		em = emf.createEntityManager();
		
		while ((line = reader.readNext()) != null) {
			Map<String, Object> data = new HashMap<String, Object>();

			i = fields.iterator();
			for (String fieldValue : line) {
				if (fieldValue != null && ! fieldValue.trim().isEmpty()) {
					data.put(i.next(), fieldValue);
				}
			}
			em.getTransaction().begin();
			try {
	            // Read all data
				process(data);
				System.out.print(".");
	            em.getTransaction().commit();
			} catch (Exception e) {
				em.getTransaction().rollback();
			}
		}
		em.close();
    }
    
    public static void main(String[] args) throws Exception
    {
    	ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/applicationContext.xml");
    	AutowireCapableBeanFactory fac = context.getAutowireCapableBeanFactory();
    	CsvLoader loader = new CsvLoader();
      	String[] files = {"Alumni/ICMC/alumni-icmc-posgrad-ccmc.csv", "Alumni/ICMC/alumni-icmc-posgrad-mat.csv"};
      	

      	fac.autowireBean(loader);
    	
    	for (String filename : files) {
    	  	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
    		loader.process(is);
    	}
    }
}

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

import com.ironiacorp.scienceanalyzer.Degree;
import com.ironiacorp.scienceanalyzer.Institution;
import com.ironiacorp.scienceanalyzer.Job;
import com.ironiacorp.scienceanalyzer.Person;
import com.ironiacorp.scienceanalyzer.PhoneNumber;
import com.ironiacorp.scienceanalyzer.Degree.DegreeType;
import com.ironiacorp.scienceanalyzer.geo.Location;
import com.ironiacorp.scienceanalyzer.library.Dissertation;
import com.ironiacorp.scienceanalyzer.social.FacebookAccount;
import com.ironiacorp.scienceanalyzer.social.LattesAccount;
import com.ironiacorp.scienceanalyzer.social.LinkedInAccount;
import com.ironiacorp.scienceanalyzer.social.OrkutAccount;
import com.ironiacorp.scienceanalyzer.social.TwitterAccount;
import com.ironiacorp.scienceanalyzer.social.UspAccount;


/**
 *
 * @author Aretha
 */
public class CsvLoader
{
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy"); 

	private static final DateFormat ALT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy"); 

    public void run2(InputStream is) throws IOException
    {
    	LinkedHashSet<String> fields = new LinkedHashSet<String>();
    	Iterator<String> i;
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		String[] line;
		
		line = reader.readNext();
		for (String field : line) {
			if (fields.add(field)) {
				throw new IllegalArgumentException("Duplicate field (column name) found in input stream");
			}
		}
		
		while ((line = reader.readNext()) != null) {
			Map<String, Object> data = new HashMap<String, Object>();
			Person advisee = new Person();
			Person advisor = new Person();
			Person coadvisor = new Person();
			Degree degree = new Degree();
            Dissertation dissertation = new Dissertation();
			Date degreeDate;
            // TODO:
            // Job job = new Job();
            // Institution workplace = new Institution();
            // Location workplaceLocation = new Location();
            // PhoneNumber workplacePhoneNumber = new PhoneNumber();
            
            // Read all data
			i = fields.iterator();
			for (String fieldValue : line) {
				data.put(i.next(), fieldValue);
			}

			// Process data
			String[] advisors = ((String) data.get("Orientador")).split(","); 
            advisor.setName(advisors[0]);
            if (advisors.length > 0) {
            	coadvisor.setName(advisors[1]);
            }

            dissertation.setTitle((String) data.get("Título do trabalho"));
            dissertation.addAuthor(advisee);
            dissertation.setAdvisor(advisor);

            degree.setDissertation(dissertation);
            degree.setAdvisor(advisor);
            degree.setCoadvisor(coadvisor);
            if (((String) data.get("Degree")).compareToIgnoreCase("ME") == 0) {
                degree.setType(DegreeType.MSC);
            } else if (((String) data.get("Degree")).compareToIgnoreCase("DO") == 0) {
                degree.setType(DegreeType.MSC);
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

            advisee.setName((String)  data.get("Nome"));
            advisee.addDegree(degree);
         
            if (data.containsKey("Email")) {
            	String email = (String) data.get("Email");
            	advisee.addEmail(email);
            }

            if (data.containsKey("Telefone")) {
            	PhoneNumber phone = new PhoneNumber();
            	phone.parsePhoneNumber((String) data.get("Telefone"));
            	// TODO: 
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
            		throw new IllegalArgumentException("Invalid USP id");
            	}
            }
  
            if (data.containsKey("Identificador Lattes")) {
            	try {
                	LattesAccount lattes = new LattesAccount();
            		int lattesId = Integer.parseInt((String) data.get("Identificador Lattes"));
            		lattes.setUserName(Integer.toString(lattesId));
            		advisee.addSocialAccount(lattes);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid Lattes id");
            	}
            }
  
            if (data.containsKey("Facebook")) {
            	try {
                	FacebookAccount facebook = new FacebookAccount();
            		String facebookId = (String) data.get("Facebook");
            		facebook.setUserName(facebookId);
            		advisee.addSocialAccount(facebook);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid Facebook id");
            	}
            }
  
            if (data.containsKey("Orkut")) {
            	try {
                	OrkutAccount orkut = new OrkutAccount();
            		String orkutId = (String) data.get("Orkut");
            		orkut.setUserName(orkutId);
            		advisee.addSocialAccount(orkut);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid Orkut id");
            	}
            }
            
            
            if (data.containsKey("LinkedIn")) {
            	try {
                	LinkedInAccount linkedin = new LinkedInAccount();
            		String linkedInId = (String) data.get("LinkedIn");
            		linkedin.setUserName(linkedInId);
            		advisee.addSocialAccount(linkedin);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid LinkedIn id");
            	}
            }
            
            if (data.containsKey("Twitter")) {
            	try {
                	TwitterAccount twitter = new TwitterAccount();
            		String twitterId = (String) data.get("Twitter");
            		twitter.setUserName(twitterId);
            		advisee.addSocialAccount(twitter);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid Twitter id");
            	}
            }
		}
    }
}

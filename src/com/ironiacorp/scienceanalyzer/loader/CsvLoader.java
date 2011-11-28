/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ironiacorp.scienceanalyzer.loader;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jfree.ui.RefineryUtilities;

import com.ironiacorp.email.Email;
import com.ironiacorp.scienceanalyzer.Degree;
import com.ironiacorp.scienceanalyzer.EmailPlusName;
import com.ironiacorp.scienceanalyzer.Institution;
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
			Degree degree = new Degree();
            Dissertation dissertation = new Dissertation();
            Date date;
            Institution workplace = new Institution();
			
			i = fields.iterator();
			for (String fieldValue : line) {
				data.put(i.next(), fieldValue);
			}

            if (((String) data.get("Degree")).compareToIgnoreCase("ME") == 0) {
                degree.setType(DegreeType.MSC);
            } else if (((String) data.get("Degree")).compareToIgnoreCase("DO") == 0) {
                degree.setType(DegreeType.MSC);
            } else {
            	throw new IllegalArgumentException("Invalid or unknown degree");
            }
            advisee.addDegree(degree);
			
            dissertation.setTitle(data.get("Título do trabalho"));
            dissertation.setAuthor(advisee);
            degree.setDissertation(dissertation);
            
            advisor.setName(data.get("Orientador"));
            degree.setAdvisor(advisor);
            dissertation.setAdvisor(advisor);

            try {
            	date = DEFAULT_DATE_FORMAT.parse((String) data.get("Data de defesa"));
            } catch (ParseException e1) {
            	try {
            		date = ALT_DATE_FORMAT.parse((String) data.get("Data de defesa"));
            	} catch (ParseException e2) {
            		throw new IllegalArgumentException("Invalid degree date");
            	}
            }
            	
            if (data.containsKey("Nro. USP")) {
            	try {
                	UspAccount usp = new UspAccount();
            		int uspId = Integer.parseInt((String) data.get("Nro. USP"));
            		usp.setUserName(uspId);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid USP id");
            	}
            }
  
            if (data.containsKey("Identificador Lattes")) {
            	try {
                	LattesAccount usp = new LattesAccount();
            		int lattesId = Integer.parseInt((String) data.get("Identificador Lattes"));
            		usp.setUserName(Integer.toString(lattesId));
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid Lattes id");
            	}
            }
  
            if (data.containsKey("Email")) {
            	String email = (String) data.get("Email");
            	advisee.addEmail(email);
            }

            if (data.containsKey("Telefone")) {
            	PhoneNumber phone = new PhoneNumber((String) data.get("Telefone"));
            	// TODO: 
            }

            Location workPlace = new Location();
            if (data.containsKey("Situação/Função/Local")) {
            	// TODO:
            }

            if (data.containsKey("Facebook")) {
            	try {
                	FacebookAccount usp = new FacebookAccount();
            		String facebookId = (String) data.get("Facebook");
            		usp.setUserName(facebookId);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid Facebook id");
            	}
            }
  
            if (data.containsKey("Orkut")) {
            	try {
                	OrkutAccount usp = new OrkutAccount();
            		String orkutId = (String) data.get("Orkut");
            		usp.setUserName(orkutId);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid Orkut id");
            	}
            }
            
            
            if (data.containsKey("LinkedIn")) {
            	try {
                	LinkedInAccount usp = new LinkedInAccount();
            		String linkedInId = (String) data.get("LinkedIn");
            		usp.setUserName(linkedInId);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid LinkedIn id");
            	}
            }
            
            if (data.containsKey("Twitter")) {
            	try {
                	TwitterAccount usp = new TwitterAccount();
            		String twitterId = (String) data.get("Twitter");
            		usp.setUserName(twitterId);
            	} catch (NumberFormatException nfe) {
            		throw new IllegalArgumentException("Invalid Twitter id");
            	}
            }
		}
    }
}

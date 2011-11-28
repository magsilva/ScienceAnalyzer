package com.ironiacorp.scienceanalyzer.social.lattes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.ironiacorp.io.IoUtil;

import au.com.bytecode.opencsv.CSVReader;

public class IntegrityChecker
{
	private void printLine(PrintWriter writer, String[] line1, String[] line2)
	{
		for (int i = 0; i < line1.length; i++) {
			writer.print("\"");
			writer.print(line1[i]);
			writer.print("\"");
			writer.print(",");
		}
		for (int i = 1; i < line2.length; i++) {
			writer.print("\"");
			writer.print(line2[i]);
			writer.print("\"");
			writer.print(",");
		}
		writer.println();
	}
	
	private void checkLine(String[] line)
	{
		String type = line[0];
		if (! "DO".equals(type) && ! "ME".equals(type)) {
			throw new IllegalArgumentException("Invalid degree");
		}

		String title = line[1];
		if (title.length() < 10) {
			throw new IllegalArgumentException("Invalid title");
		}
		
		String author = line[2];
		if (author.length() < 5) {
			throw new IllegalArgumentException("Invalid author");
		}
		
		String advisor = line[3];
		if (advisor.length() < 5) {
			throw new IllegalArgumentException("Invalid advisor");
		}
		
		String date = line[4];
		try {
			DateFormat dateformat = new SimpleDateFormat("dd/MM/yy");
			dateformat.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		
		try {
			String lattesId = line[5];
			long id = Long.parseLong(lattesId);
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		IntegrityChecker merger = new IntegrityChecker();
	    File srcfile = new File("/home/magsilva/Projects/ICMC/Alumni/resources/icmc/alumni-icmc-posgrad-ccmc.csv");
	    File fileToMerge = new File("/home/magsilva/publications.csv");
	    
		CSVReader reader;
		Map<String, String> newData = new HashMap<String, String>();
	    String [] line;
	    
	    reader = new CSVReader(new FileReader(srcfile));
	    while ((line = reader.readNext()) != null) {
	    	merger.checkLine(line);
	    }
	    reader.close();
	    
	    
	    reader = new CSVReader(new InputStreamReader(new FileInputStream(fileToMerge), Charset.forName("iso-8859-1")));
	    while ((line = reader.readNext()) != null) {
	    	String id = line[0].trim();
	    	String name = line[1].trim();
	    	newData.put(id, name);
	    }
	    reader.close();

	    
	    reader = new CSVReader(new FileReader(srcfile));
	    while ((line = reader.readNext()) != null) {
	    	String id = line[5].trim();
	    	String name = line[2].trim();
	    	if (newData.containsKey(id)) {
	    		if (! name.equals(newData.get(id))) {
	    			System.out.println("Incorrect data: " + name + " -> " + newData.get(id));
	    		}
	    	} else {
	    		System.out.println("Missing data for id " + id);
	    	}
	    }
	    reader.close();
	}
}

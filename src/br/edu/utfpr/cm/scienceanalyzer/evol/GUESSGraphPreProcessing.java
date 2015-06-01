/*
 * Copyright (C) 2011 Marco Aurélio Graciotto Silva <magsilva@ironiacorp.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.edu.utfpr.cm.scienceanalyzer.evol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mariane.HandmadeBibtexParser;
import net.sf.jabref.BibtexDatabase;
import net.sf.sysrev.db.bibtex.Bibtex;
import au.com.bytecode.opencsv.CSVParser;
import br.edu.utfpr.cm.scienceanalyzer.evol.BibTeX2DTM;
import br.edu.utfpr.cm.scienceanalyzer.evol.Corpus;

import com.ironiacorp.computer.Filesystem;
import com.ironiacorp.io.IoUtil;








//import lode.miner.extraction.bibtex.handmade.HandmadeBibtexParser;
import lode.model.*;
import lode.model.publication.EventArticle;
import lode.model.publication.Person;
import lode.model.publication.Publication;

public class GUESSGraphPreProcessing
{
	private class NodeOrEdge {
		private Map<String, String> properties = new HashMap<String, String>();
	}
	
	private static final String[] NODE_FIELDS = {
		"name", "x", "y", "visible", "strokecolor", "style", "width", "height",
		"label", "labelvisible", "labelsize", "betweenness_centrality", "indegree",
		"outdegree", "year"
	};
	
	private static final String[] EDGE_FIELDS = {
		"node1", "node2", "__edgeid", "visible", "width", "weight", "directed", "year"
	};
	
	private lode.model.publication.Collection collection;
	
	
	
	public GUESSGraphPreProcessing()
	{
	}
	
	public void readData(Corpus corpus) throws IOException
	{
		readData(new File(corpus.getBasedir() + File.separator + corpus.getName() + BibTeX2DTM.BIBTEX_EXTENSION));
	}
	
	public void readData(File bibtexFile) throws IOException
	{
		HandmadeBibtexParser parser = new HandmadeBibtexParser();
		parser.setPreferredLanguage("en");
		parser.setIgnoreErrors(true);
		collection = parser.parse(bibtexFile, new BufferedReader(new FileReader(bibtexFile)));
	
	}
	
	public String getExtension(String name){
		int pos = name.lastIndexOf(".");
		String ex = name.substring(pos, name.length());

		return ex;
	}
	
	public String replaceExtension(String oldEx, String newEx){
		return newEx;
	}
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public File createAuthorDocumentGraph(File originalGraphFile) throws Exception
	{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(originalGraphFile));
		Map<String, Publication> documents = new HashMap<String, Publication>();
		File newGraphFile;
		Writer fileWriter; 
		
		String baseName = originalGraphFile.getName();
		String extension = getExtension(baseName);
		//baseName = IoUtil.replaceExtension(IoUtil.getExtension(baseName), ".new." + IoUtil.getExtension(baseName));
		
		baseName = replaceExtension(getExtension(baseName), ".new." + getExtension(baseName));
		newGraphFile = new File(originalGraphFile.getPath() + File.separator + baseName);
		fileWriter = new FileWriter(newGraphFile);
		
		Iterator<Publication> i = collection.iterator();
		while (i.hasNext()) {
			Publication publication = i.next();
			if (publication instanceof EventArticle) {
				documents.put(publication.getTitle().toLowerCase(), publication);
			}
		}
		
		BufferedReader reader = new BufferedReader(isr);
		String line = reader.readLine();
		fileWriter.append(line);
		fileWriter.append(",year INTEGER");
		while ((line = reader.readLine()) != null) {
			CSVParser csvParser = new CSVParser(',', '\'');
			String[] fields = csvParser.parseLine(line);
			String title = fields[11].toLowerCase();
			fileWriter.append("\n");
			fileWriter.append(line);
			fileWriter.append(",");
			if (documents.containsKey(title)) {
				Publication publication = documents.get(title);
				fileWriter.append(Integer.toString(publication.getYear()));
			} else {
				fileWriter.append("0");
			}
		}
		
		fileWriter.close();
		reader.close();
		
		return newGraphFile;
	}

	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	/*TODO ver #TODOMari */
	public File createCoAuthoringGraph(File originalGraphFile) throws Exception
	{
		ArrayList<String> nodeFields = new ArrayList<String>(); // n1 ... nN
		ArrayList<String> edgeFields = new ArrayList<String>();
		InputStreamReader isr = new InputStreamReader(new FileInputStream(originalGraphFile)); // file do arquivo original
		
		
		/*map feito para associar uma string (nome do autor) a um objeto Person correspondente ao autor.
		 * fiz esse map para conseguir usar o contains do publications.getAuthors(), ele precisa comparar um um objeto Person.
		 * */
		Map<String,Person> mapAuthors = new HashMap<String, Person>();

		
		Map<String, Integer> authorsYear = new HashMap<String, Integer>(); // map de (Autor,Ano)
		
		/*map feito para mapear um name (n1,n2 .. nn) a um objeto Person (author) */
		Map<String, Person> nodeAuthor = new HashMap<String, Person>(); // map <node,author> , ex: n0,Arnow
		
		boolean processingNodes = false;
		boolean processingEdges = false;
		
		File newGraphFile;
		Writer fileWriter;
		String line;

		String baseName = originalGraphFile.getName();
		baseName = replaceExtension(baseName, "new" + getExtension(baseName));
		
		newGraphFile = new File(originalGraphFile.getParent() + File.separator + baseName);
		fileWriter = new FileWriter(newGraphFile);

		Iterator<Publication> i = collection.iterator();
		while (i.hasNext()) {
			Publication publication = i.next();
			if (publication instanceof EventArticle) {
				List<Person> authors = publication.getAuthors();
				Integer k = 0;
				for (Person author : authors) {
					/* inserindo um author no mapAuthors */
					mapAuthors.put(author.getName(), author);
					
					String key = author.getName();
					if (authorsYear.containsKey(key)) {
						int year = authorsYear.get(key);
						if (publication.getYear() < year) {
							authorsYear.put(key, publication.getYear());
						}
					} else {
						authorsYear.put(key, publication.getYear());
					}
				}
			}
		}
		
		
		
		BufferedReader reader = new BufferedReader(isr);
		String oldNode1 = null; /* variaveis de controle para nao escrever duas vezes no arquivo final uma aresta, por exemplo n15 n16 estava 
		escrevendo duas vezes, essas variavei evitam que seja escrito duas vezes uma mesma aresta. */
		String oldNode2 = null;
		
		while ((line = reader.readLine()) != null) {
			CSVParser csvParser = new CSVParser(',', '\'');
			String[] fields = csvParser.parseLine(line);
			
			
			if (line.startsWith("nodedef>")) {
				fields[0] = fields[0].replaceFirst("nodedef>", "");
				fileWriter.append("nodedef>");
				processingNodes = true;
				processingEdges = false;
				for (int fieldId = 0; fieldId < fields.length; fieldId++) {
					String field = fields[fieldId];
					String fieldName = field.split(" ")[0];
					String fieldType = field.split(" ")[1];
					nodeFields.add(fieldName);
					if (! fieldName.equals("year")) {
						for (String prefix : NODE_FIELDS) {
							if (fieldName.equals(prefix)) {
								fileWriter.append(field);
								fileWriter.append(",");
							}
						}
					}
				}
				fileWriter.append("xyzTerm BOOLEAN");
				
				if (! nodeFields.contains("year")) {
					nodeFields.add("year");
					fileWriter.append(",year INTEGER");
				}
				fileWriter.append("\n");
				continue;
			}
			
			if (line.startsWith("edgedef>")) {
				fields[0] = fields[0].replaceFirst("edgedef>", "");
				processingNodes = false;
				processingEdges = true;
				fileWriter.append("edgedef>");
				for (int fieldId = 0; fieldId < fields.length; fieldId++) {
					String field = fields[fieldId];
					String fieldName = field.split(" ")[0];
					String fieldType = field.split(" ")[1];
					edgeFields.add(fieldName);
					if (! fieldName.equals("year")) {
						for (String prefix : EDGE_FIELDS) {
							if (fieldName.equals(prefix)) {
								fileWriter.append(field);
								fileWriter.append(",");
							}
						}
					}
				}
				fileWriter.append("xyzTerm BOOLEAN");
				if (! edgeFields.contains("year")) {
					edgeFields.add("year");
					fileWriter.append(",year INTEGER");
				}
				fileWriter.append("\n");
				continue;
			}
			
			if (processingNodes) {
				String key = null;
				
				NodeOrEdge noe = new NodeOrEdge();
				for (int fieldId = 0; fieldId < nodeFields.size(); fieldId++) {
					String nodeFieldName = nodeFields.get(fieldId); // campos
					String nodeField = "";
					
					if (nodeFieldName.equals("year")) {
						try {
							nodeField = fields[fieldId];
							noe.properties.put(nodeFieldName, nodeField);
						} catch (ArrayIndexOutOfBoundsException e) {}
					} else {
						nodeField = fields[fieldId]; // value of nodefieldName
					}
					
					
					for (String prefix : NODE_FIELDS) {
						if (nodeFieldName.equals(prefix)) {
							if (! nodeFieldName.equals("year")) {
								noe.properties.put(nodeFieldName, nodeField);
								if(nodeFieldName.equals("name")){
									key = nodeField;
								}
								if(nodeFieldName.equals("label")){
									/*pego o autor referente ao nodeField no mapAuhtors e insiro o objeto Person retornado no map
									 * de nodeAuthor */
									Person aux = mapAuthors.get(nodeField.toLowerCase());
									nodeAuthor.put(key, aux);
									fileWriter.append("'" + nodeField + "'");
								}else{
									fileWriter.append(nodeField);
								}
								fileWriter.append(",");
							}
						}
					}
				}
				if(key != null){
					String authorY = noe.properties.get("label").toLowerCase();
					int y = authorsYear.get(authorY);
				}
				fileWriter.append("true,");

				if (! noe.properties.containsKey("year")) {
					String id = noe.properties.get("name");
					String author = noe.properties.get("label").toLowerCase();
					
					if (authorsYear.containsKey(author)) {
						int year = authorsYear.get(author);
						fileWriter.append(Integer.toString(year));
					} else {
						throw new IllegalArgumentException(author + " (" + id + ") has no paper assigned to it (or we do not know when it was published)");
					}	
				} else {
					fileWriter.append(noe.properties.get("year"));
				}
				fileWriter.append("\n");
			}
			
			if (processingEdges) {
				NodeOrEdge noe = new NodeOrEdge();
				
				for (int fieldId = 0; fieldId < edgeFields.size(); fieldId++) {
					String edgeField = "";
					String edgeFieldName = edgeFields.get(fieldId);
					if (edgeFieldName.equals("year")) {
						try {
							edgeField = fields[fieldId];
							noe.properties.put(edgeFieldName, edgeField);
						} catch (ArrayIndexOutOfBoundsException e) {}
					} else {
						edgeField = fields[fieldId]; // value of edgeFieldName
					}
					for (String prefix : EDGE_FIELDS) {
						if (edgeFieldName.equals(prefix)) {
							if (! edgeFieldName.equals("year")) {
								noe.properties.put(edgeFieldName, edgeField);
								if(edgeFieldName.equals("label")){
									fileWriter.append("'" + edgeField + "'");
								}else{
									fileWriter.append(edgeField);
								}
								fileWriter.append(",");
							}
						}
					}
				}
				fileWriter.append("true,");
				
				if (! noe.properties.containsKey("year")) {
					Integer y = null;
					String node1 = noe.properties.get("node1");
					String node2 = noe.properties.get("node2");
					
					Iterator<Publication> iterator = collection.iterator();
					while (iterator.hasNext()) {
						Publication publication = iterator.next();
						
						Person a1 = nodeAuthor.get(node1);
						Person a2 = nodeAuthor.get(node2);
						
						
						y = Integer.MAX_VALUE;
						
						/*agr consigo comparar com o metodo contains se o Person vindo do node1 esta contido na lista de authors 
						 * da publication */
						if(publication.getAuthors().contains(a1) && publication.getAuthors().contains(a2)){
							int yearOfPubl = publication.getYear();
							if(yearOfPubl < y){
								y = yearOfPubl;
								
								if(oldNode1 != node1 && oldNode1 != node1){
								System.err.println(node1 + "--" + node2 + " Year: " + y);
								fileWriter.append("year: "+ y.toString());
								
								oldNode1= node1;
								oldNode2 = node2;
								
								}
							}
						}
					}
				}
				fileWriter.append("\n");
			}
		}
		
		fileWriter.close();
		reader.close();
		
		return newGraphFile;
	}
}


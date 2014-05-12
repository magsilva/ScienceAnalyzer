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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVParser;

import com.ironiacorp.io.IoUtil;
import lode.miner.extraction.bibtex.handmade.HandmadeBibtexParser;
import lode.model.publication.Collection;
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
		"node1", "node2", "__edgeid", "visible", "width", "weight", "directed"
	};
	
	private Collection collection;


	
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
		collection = parser.parse(bibtexFile.getName(), new BufferedReader(new FileReader(bibtexFile)));
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
		baseName = IoUtil.replaceExtension(IoUtil.getExtension(baseName), ".new." + IoUtil.getExtension(baseName));
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
	public File createCoAuthoringGraph(File originalGraphFile) throws Exception
	{
		ArrayList<String> nodeFields = new ArrayList<String>();
		ArrayList<String> edgeFields = new ArrayList<String>();
		Iterator<String> iField;
		InputStreamReader isr = new InputStreamReader(new FileInputStream(originalGraphFile));
		Map<String, Integer> authorsYear = new HashMap<String, Integer>();
		boolean processingNodes = false;
		boolean processingEdges = false;
		File newGraphFile;
		Writer fileWriter;
		String line;

		String baseName = originalGraphFile.getName();
		baseName = IoUtil.replaceExtension(baseName, "new." + IoUtil.getExtension(baseName));
		newGraphFile = new File(originalGraphFile.getParent() + File.separator + baseName);
		fileWriter = new FileWriter(newGraphFile);

		Iterator<Publication> i = collection.iterator();
		while (i.hasNext()) {
			Publication publication = i.next();
			if (publication instanceof EventArticle) {
				List<Person> authors = publication.getAuthors();
				for (Person author : authors) {
					String key = author.getName().toLowerCase();
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
					for (String prefix : EDGE_FIELDS) {
						if (fieldName.equals(prefix)) {
							fileWriter.append(field);
							fileWriter.append(",");
						}
					}
				}
				fileWriter.append("xyzTerm BOOLEAN");
				fileWriter.append("\n");
				continue;
			}
			
			if (processingNodes) {
				NodeOrEdge noe = new NodeOrEdge();
				for (int fieldId = 0; fieldId < nodeFields.size(); fieldId++) {
					String nodeFieldName = nodeFields.get(fieldId);
					String nodeField = "";
					if (nodeFieldName.equals("year")) {
						try {
							nodeField = fields[fieldId];
							noe.properties.put(nodeFieldName, nodeField);
						} catch (ArrayIndexOutOfBoundsException e) {}
					} else {
						nodeField = fields[fieldId];
					}
					for (String prefix : NODE_FIELDS) {
						if (nodeFieldName.equals(prefix)) {
							if (! nodeFieldName.equals("year")) {
								noe.properties.put(nodeFieldName, nodeField);
								fileWriter.append(nodeField);
								fileWriter.append(",");
							}
						}
					}
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
					String edgeField = fields[fieldId];
					String edgeFieldName = edgeFields.get(fieldId);
					for (String prefix : EDGE_FIELDS) {
						if (edgeFieldName.equals(prefix)) {
							noe.properties.put(edgeFieldName, edgeField);
							fileWriter.append(edgeField);
							fileWriter.append(",");
						}
					}
				}
				fileWriter.append("true");
				fileWriter.append("\n");
			}
		}
		
		fileWriter.close();
		reader.close();
		
		return newGraphFile;
	}
}


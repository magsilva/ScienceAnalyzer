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

package com.ironiacorp.sysrev.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVParser;

import com.ironiacorp.io.IoUtil;
import lode.miner.extraction.bibtex.BibtexParser;
import lode.miner.extraction.bibtex.handmade.HandmadeBibtexParser;
import lode.model.publication.Collection;
import lode.model.publication.EventArticle;
import lode.model.publication.Person;
import lode.model.publication.Publication;


public class GUESSGraphPreProcessing
{
	private Collection collection;


	
	public GUESSGraphPreProcessing()
	{
	}
	
	public void readData(File bibtexFile) throws IOException
	{
		BibtexParser parser = new HandmadeBibtexParser();
		
		parser.setPreferredLanguage("en");
		collection = parser.parse(new BufferedReader(new FileReader(bibtexFile)));
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
				processingNodes = true;
				processingEdges = false;
				fileWriter.append(line);
				fileWriter.append(",year INTEGER");
				fileWriter.append("\n");
				continue;
			}
			
			if (line.startsWith("edgedef>")) {
				processingNodes = false;
				processingEdges = true;
				fileWriter.append(line);
				fileWriter.append("\n");
				continue;
			}
			
			if (processingNodes) {
				String id = fields[0];
				String author = fields[11].toLowerCase();
				fileWriter.append(line);
				fileWriter.append(",");
				if (authorsYear.containsKey(author)) {
					int year = authorsYear.get(author);
					fileWriter.append(Integer.toString(year));
					fileWriter.append("\n");
				} else {
					//if (! "labelsize integer".equals(author) && ! "14".equals(author)) {
						throw new IllegalArgumentException(author + " (" + id + ") has no paper assigned to it (or we do not know when it was published");
					//}
				}
			}
			
			if (processingEdges) {
				fileWriter.append(line);
				fileWriter.append("\n");
			}
		}
		
		fileWriter.close();
		reader.close();
		
		return newGraphFile;
	}
	
	public static void main(String[] args) throws Exception
	{
		GUESSGraphPreProcessing guess = new GUESSGraphPreProcessing();
		guess.readData(new File("/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Analysis/SBES4Visualization.bib"));
		// guess.createAuthorDocumentGraph(new File("/home/magsilva/teste.gdf"));
		guess.createCoAuthoringGraph(new File("/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Analysis/CoAuthoring/CoAuthoring-MainCollaborations.gdf"));
		
	}
}


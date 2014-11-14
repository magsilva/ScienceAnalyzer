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


public class GUESS2Gephi
{
	private File inputFile;
	
	private File outputFile;
	
	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}


	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void convert() throws Exception
	{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(inputFile));
		BufferedReader reader = new BufferedReader(isr);
		Writer fileWriter = new FileWriter(outputFile);
		CSVParser csvParser = new CSVParser(',', '\'');
		boolean processingNodes = false;
		boolean processingEdges = false;
		String line;
		String[] fields;

		
		fileWriter.append("Source,Target\n");
		
		while ((line = reader.readLine()) != null) {
			try {
				fields = csvParser.parseLine(line);
			} catch (IOException e) {
				System.out.println(line);
				throw e;
			}
			
			if (line.startsWith("nodedef>")) {
				processingNodes = true;
				processingEdges = false;
				continue;
			}
			
			if (line.startsWith("edgedef>")) {
				processingNodes = false;
				processingEdges = true;
				continue;
			}
			
			if (processingNodes) {
				String id = fields[0];
				String author = fields[11].toLowerCase();
				/*
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
				*/
			}
			
			if (processingEdges) {
				fileWriter.append(fields[0]);
				fileWriter.append(",");
				fileWriter.append(fields[1]);
				fileWriter.append("\n");
			}
		}
		
		fileWriter.close();
		reader.close();
	}
	
	public static void main(String[] args) throws Exception
	{
		GUESS2Gephi converter = new GUESS2Gephi();
		converter.setInputFile(new File("/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Analysis/CoAuthoring/CoAuthoring-MainCollaborations.new.gdf"));
		converter.setOutputFile(new File("/home/magsilva/teste.csv"));
		converter.convert();
	}
}


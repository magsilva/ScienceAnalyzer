/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.sysrev.engines.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.util.PDFTextStripper;

import net.sf.sysrev.credentials.Credential;
import net.sf.sysrev.methods.PunctuationMarkRemover;

/**
 * This is the main program that simply parses the pdf document and transforms it into text.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.14 $
 */
public class PDFTextExtractor
{
	public static final String WORD_SEPARATOR = " ";

	public static final String LINE_SEPARATOR = "\n";

	private PDFDocument document;
	
	private PunctuationMarkRemover markRemover;

	private static Map<String, String> translateChars;

	static {
		translateChars = new HashMap<String, String>();
		translateChars.put("´e", "é");
		translateChars.put("´a", "á");
		translateChars.put("´ı", "í");
		translateChars.put("´o", "ó");
		translateChars.put("a˜", "ã");
	}

	private static String translate(String str)
	{
		for (String key : translateChars.keySet()) {
			str = str.replace(key, translateChars.get(key));
		}
		return str;
	}

	public void setPDFDocument(String filename)
	{
		Credential credential = null;
		if (document.isEncrypted()) {
			if (document.decrypt(credential)) {
				throw new IllegalArgumentException("Encrypted document");
			}
		}

		try {
			PDFDocument document = new PDFDocument("/tmp/1.pdf");
			this.document = document;
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not process the request file", e);
		}

	}
	
	public void setPDFDocument(PDFDocument pdf)
	{
		this.document = pdf;
	}

	public String extractText() throws Exception
	{
		List<String> words = new ArrayList<String>();
		PDFStripper stripper = new PDFStripper();
		// stripper.setSortByPosition(true);
		String text = stripper.getText(document.getDocument());
		document.close();
		
		return text;
	}
	
	/**
	 * Infamous main method.
	 *
	 * @param args Command line arguments, should be one and a reference to a file.
	 *
	 * @throws Exception If there is an error parsing the document.
	 */
	public String[] extractWordsM1() throws Exception
	{
		List<String> words = new ArrayList<String>();
		PDFStripper stripper = new PDFStripper();
		// stripper.setSortByPosition(true);
		String text = stripper.getText(document.getDocument());
		document.close();

		String[] lines = text.split(LINE_SEPARATOR);
		for (String line : lines) {
			String[] lineWords = line.split(WORD_SEPARATOR);
			for (String word : lineWords) {
				words.add(word);
			}
		}

		return words.toArray(new String[0]);
	}

	public String[] extractWordsM2() throws Exception
	{
		List<String> words = new ArrayList<String>();
		PDFTextStripper stripper = new PDFTextStripper();
		// stripper.setSortByPosition(true);
		String text = stripper.getText(document.getDocument());
		document.close();

		String[] lines = text.split(LINE_SEPARATOR);
		for (String line : lines) {
			String[] lineWords = line.split(WORD_SEPARATOR);
			for (String word : lineWords) {
				words.add(word);
			}
		}

		return words.toArray(new String[0]);
	}
}

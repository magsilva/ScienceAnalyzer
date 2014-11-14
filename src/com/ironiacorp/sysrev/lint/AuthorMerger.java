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


package com.ironiacorp.sysrev.lint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import lode.miner.BufferComponent;
import lode.miner.extraction.bibtex.BibtexParser;
import lode.miner.extraction.bibtex.BibtexWriter;
import lode.miner.extraction.bibtex.handmade.HandmadeBibtexParser;
import lode.miner.preprocessing.text.AbbreviatedNameSorter;
import lode.miner.preprocessing.text.WordReplacer;
import lode.model.publication.Person;
import lode.model.publication.Publication;
import lode.model.text.TextResource;
import lode.model.text.UnformattedTextResource;

import com.ironiacorp.sysrev.selection.Argument;
import com.ironiacorp.sysrev.selection.PublicationSelectionStatus;
import com.ironiacorp.sysrev.selection.Reason;
import com.ironiacorp.sysrev.selection.Status;

public class AuthorMerger
{
	private AbbreviatedNameSorter sorter = new AbbreviatedNameSorter();
	
	private WordReplacer wordReplacer = new WordReplacer();

	private String knowledgeArea;
	
	public AuthorMerger()
	{
		sorter = new AbbreviatedNameSorter();
		wordReplacer = new WordReplacer();
		sorter.setConsumer(wordReplacer);
	}

	public String getKnowledgeArea()
	{
		return knowledgeArea;
	}

	public void setKnowledgeArea(String knowledgeArea)
	{
		try {
			wordReplacer.loadSynonymsFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("person/Names-" + knowledgeArea + ".txt"));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid or unknown knowledge area: " + knowledgeArea, e);
		}
		this.knowledgeArea = knowledgeArea;

	}

	public void clean(Publication publication)
	{
		List<Person> authors = publication.getAuthors();
		BufferComponent buffer = new BufferComponent();
		wordReplacer.setConsumer(buffer);

		for (Person author : authors) {
			TextResource resource = new UnformattedTextResource();
			resource.setText(author.getName());
			sorter.consume(resource);
			author.setName(buffer.getResources().get(0).toString());
			buffer.reset();
		}
	}
}

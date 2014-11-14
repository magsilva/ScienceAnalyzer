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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Iterator;

import lode.miner.extraction.bibtex.BibtexParser;
import lode.miner.extraction.bibtex.BibtexWriter;
import lode.miner.extraction.bibtex.handmade.HandmadeBibtexParser;
import lode.model.publication.Collection;
import lode.model.publication.EventArticle;
import lode.model.publication.Publication;

import com.ironiacorp.sysrev.selection.Argument;
import com.ironiacorp.sysrev.selection.PublicationSelectionStatus;
import com.ironiacorp.sysrev.selection.Reason;
import com.ironiacorp.sysrev.selection.Status;

public class BibTeXLint
{
	private AuthorMerger authorMerger;
	
	private BibtexParser parser;
	
	private Collection collection;
	
	private String knowledgeArea;
		
	private File outputFile;
	
	public BibTeXLint()
	{
		authorMerger = new AuthorMerger();
		parser = new HandmadeBibtexParser();
		collection = new Collection();
	}
	
	
	public File getOutputFile()
	{
		return outputFile;
	}


	public void setOutputFile(File outputFile)
	{
		this.outputFile = outputFile;
	}

	public String getKnowledgeArea()
	{
		return knowledgeArea;
	}


	public void setKnowledgeArea(String knowledgeArea)
	{
		this.knowledgeArea = knowledgeArea;
	}

	
	public void parse(File file)
	{
		try {
			FileInputStream fis = new FileInputStream(file);
			parser.setStreamName(file.getAbsolutePath());
			parse(fis);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public void parse(InputStream is)
	{
		Collection collectionYear = parser.parse(new BufferedReader(new InputStreamReader(is)));
		Iterator<Publication> i = collectionYear.iterator();
		authorMerger.setKnowledgeArea(knowledgeArea);
		while (i.hasNext()) {
			Publication publication = i.next();
			/*
			authorMerger.clean(publication);
			collection.add(publication);
			*/
			
			// TODO: create a pipeline processor that clean using the status field, publication type, etc.
			if (publication instanceof EventArticle) {
				String statusField = publication.getStatus();
				if (statusField != null && ! statusField.isEmpty()) {
					PublicationSelectionStatus status = new PublicationSelectionStatus(statusField);
					Argument decision = status.getDecisionArgument(); 
					if (decision.getStatus() == Status.SELECTED && decision.getReason() == Reason.FULL_TEXT) {
						authorMerger.clean(publication);
						collection.add(publication);
					}
				}
			}
		}
	}
	
	private void clean()
	{
		BibtexWriter bibtexWriter = new BibtexWriter();
		try {
			Writer fileWriter = new FileWriter(outputFile);
			bibtexWriter.setWriter(fileWriter);
			bibtexWriter.write(collection);
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		String[] files = {
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1987.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1988.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1989.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1990.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1991.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1992.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1993.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1994.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1995.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1996.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1997.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1998.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-1999.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2000.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2001.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2002.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2003.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2004.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2005.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2006.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2007.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2008.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2009.bib",
				"/home/magsilva/Dropbox/Papers/25thSBES-SoftwareTesting/Proceedings/SBES-2010.bib",
		};
		BibTeXLint lint = new BibTeXLint();
		lint.setOutputFile(new File("/home/magsilva/SBES4Visualization.bib"));
		lint.setKnowledgeArea("SoftwareTesting");
		
		for (String filename : files) {
			System.out.println(filename);
			lint.parse(new File(filename));
		}
		lint.clean();
	}


	/*
	public static void main(String[] args) throws Exception
	{
		String[] files = {
				"/home/magsilva/Submissions/19/paper/studies.bib",
		};
		BibTeXLint lint = new BibTeXLint();
		lint.setOutputFile(new File("/home/magsilva/Submissions/19/analysis/studies.bib"));
		lint.setKnowledgeArea("Education");
		
		for (String filename : files) {
			System.out.println(filename);
			lint.parse(new File(filename));
		}
		lint.clean();
	}
	*/
}


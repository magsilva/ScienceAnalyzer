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
import lode.miner.ConsumerComponent;
import lode.miner.PipelineComponent;
import lode.miner.ProducerComponent;
import lode.miner.TypicalPipelineComponent;
import lode.miner.extraction.bibtex.BibtexParser;
import lode.miner.extraction.bibtex.BibtexWriter;
import lode.miner.extraction.bibtex.handmade.HandmadeBibtexParser;
import lode.miner.preprocessing.bibtex.PageCountBasedLUCut;
import lode.miner.preprocessing.text.AbbreviatedNameSorter;
import lode.miner.preprocessing.text.WordReplacer;
import lode.model.Element;
import lode.model.publication.Collection;
import lode.model.publication.EventArticle;
import lode.model.publication.Publication;

import com.ironiacorp.sysrev.selection.Argument;
import com.ironiacorp.sysrev.selection.PublicationSelectionStatus;
import com.ironiacorp.sysrev.selection.Reason;
import com.ironiacorp.sysrev.selection.Status;


public class ShortPaperRemover
{
	private TypicalPipelineComponent component;
	
	private ConsumerComponent buffer;
	
	public ShortPaperRemover()
	{
		PageCountBasedLUCut lucutter = new PageCountBasedLUCut();
		lucutter.setLowerLimit(8);
		lucutter.setUpperLimit(-1);
		component = lucutter;
	}

	private void setConsumerComponent(ConsumerComponent consumer)
	{
		component.setConsumer(consumer);
	}

	
	public void cut(Publication publication)
	{
		component.consume(publication);
	}
	
	public static void main(String[] args) throws Exception
	{
		String[] files = {
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1987.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1988.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1989.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1990.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1991.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1992.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1993.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1994.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1995.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1996.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1997.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1998.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-1999.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2000.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2001.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2002.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2003.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2004.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2005.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2006.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2007.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2008.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2009.bib",
				"/home/magsilva/Dropbox/Project SBES/Proceedings/SBES-2010.bib",
		};
		BibtexWriter writer = new BibtexWriter();
		BibtexParser parser = new HandmadeBibtexParser();
		Collection collection = new Collection();
		Writer fileWriter = new FileWriter("/home/magsilva/teste.bib"); 
		ShortPaperRemover cutter = new ShortPaperRemover();
		BufferComponent buffer = new BufferComponent();
		cutter.setConsumerComponent(buffer);

		
		for (String filename : files) {
			System.out.println(filename);
			Collection collectionYear = parser.parse(new BufferedReader(new FileReader(filename)));
			Iterator<Publication> i = collectionYear.iterator();
			while (i.hasNext()) {
				Publication publication = i.next();
				if (publication instanceof EventArticle) {
					cutter.cut(publication);
				}
			}
		}
		
		for (Element resource : buffer.getResources()) {
			if (resource instanceof Publication) {
				collection.add((Publication) resource);
			}
		}

		writer.setWriter(fileWriter);
		writer.write(collection);
	}
}


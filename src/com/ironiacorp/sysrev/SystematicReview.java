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


package com.ironiacorp.sysrev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Iterator;

import com.ironiacorp.io.IoUtil;
import lode.miner.acquisition.filesystem.FilesystemPDFFetcher;
import lode.miner.extraction.bibtex.BibtexParser;
import lode.miner.extraction.bibtex.BibtexWriter;
import lode.miner.extraction.bibtex.handmade.HandmadeBibtexParser;
import lode.model.bibtex.BibTeX;
import lode.model.publication.Collection;
import lode.model.publication.Publication;

import com.ironiacorp.sysrev.selection.PublicationSelectionStatus;
import com.ironiacorp.sysrev.selection.Status;

public class SystematicReview
{
	private Collection collection;
	
	private File foundFilesDir;
	
	private File selectedFilesDir;
	
	public void loadFile(File file)
	{
		String extension = IoUtil.getExtension(file.getName());
		if (extension.isEmpty()) {
			throw new IllegalArgumentException("Cannot guess the type of the file");
		}
		
		if (BibTeX.FILENAME_EXTENSION.equals(extension)) {
			BibtexParser parser = new HandmadeBibtexParser();
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
				Collection newCollection = parser.parse(reader);
				if (collection == null) {
					collection = newCollection;
				} else {
					collection.merge(newCollection);
				}
			} catch (IOException e) {
				throw new IllegalArgumentException("Cannot read input file");
			}
		} else {
			throw new IllegalArgumentException("Cannot guesst the type of the file");
		}
	}
	
	public Collection prepareSelectedFiles()
	{
		if (foundFilesDir == null || selectedFilesDir == null) {
			throw new UnsupportedOperationException("The found and selected files base directory must be set before running this operation");
		}
		
		Collection selectedCollection = new Collection();
		FilesystemPDFFetcher fetcher = new FilesystemPDFFetcher();
		fetcher.setBaseDir(foundFilesDir);
		
		Iterator<Publication> i = collection.iterator();
		while (i.hasNext()) {
			Publication pub = i.next();
			PublicationSelectionStatus pubStatus = new PublicationSelectionStatus(pub.getStatus());
			if (pubStatus.getDecision() == Status.SELECTED) {
				selectedCollection.add(pub);
				// TODO: Fix: fetcher.getOrUpdate(pub);
				try {
					IoUtil.copyFile(pub.getCachedCopy(), new File(selectedFilesDir,pub.getCachedCopy().getName()));
				} catch (NullPointerException e) {
					System.err.println("There is no cached copy for publication " + pub.getTitle());
				} catch (IOException e) {
					System.err.println("Could not copy file " + pub.getCachedCopy());
				}
			}
		}
		
		return selectedCollection;
	}

	public File getFoundFilesDir() {
		return foundFilesDir;
	}

	public void setFoundFilesDir(File foundFilesDir) {
		this.foundFilesDir = foundFilesDir;
	}

	public File getSelectedFilesDir() {
		return selectedFilesDir;
	}

	public void setSelectedFilesDir(File selectedFilesDir) {
		this.selectedFilesDir = selectedFilesDir;
	}
	
	public static void main(String[] args)
	{
		SystematicReview sysrev = new SystematicReview();
		sysrev.setFoundFilesDir(new File("/home/magsilva/Publications/TechnicalReports/Learning objects requirements/Selection/Studies-Retrieved"));
		sysrev.setSelectedFilesDir(new File("/home/magsilva/leia"));
		sysrev.loadFile(new File("/home/magsilva/Publications/TechnicalReports/Learning objects requirements/Selection/Interactive Learning Objects - IEEE - Retrieved.bib"));
		// sysrev.loadFile(new File("/home/magsilva/Publications/TechnicalReports/Learning objects requirements/Selection/Interactive Learning Objects - IEEE - After.bib"));
		
		/*
		PublisherMerger merger = new PublisherMerger();
		Iterator<Publication> i = sysrev.collection.iterator();
		while (i.hasNext()) {
			Publication pub = i.next();
			merger.merge(pub);
		}
		*/
		
		
		Collection selectedCollection = sysrev.prepareSelectedFiles();
		
		BibtexWriter bibtexWriter = new BibtexWriter();
		try {
			Writer fileWriter = new FileWriter("/home/magsilva/Publications/TechnicalReports/Learning objects requirements/Selection/Interactive Learning Objects - IEEE - Selected.bib");
			bibtexWriter.setWriter(fileWriter);
			bibtexWriter.write(selectedCollection);
			fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
		}
	}

}

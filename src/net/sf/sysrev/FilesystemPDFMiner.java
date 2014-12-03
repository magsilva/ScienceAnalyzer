/*
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

Copyright (C) 2007 Marco Aurelio Graciotto Silva <magsilva@gmail.com>
*/

package net.sf.sysrev;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.sf.sysrev.engines.pdf.PDFDocument;
import net.sf.sysrev.methods.ExtractionMethod;
import net.sf.sysrev.types.Publication;

public class FilesystemPDFMiner
{
	private HashMap<String, Publication> dir;

	private List<PDFDocument> documents;

	private List<ExtractionMethod> methods;

	@SuppressWarnings("unchecked")
	public FilesystemPDFMiner()
	{
		documents = new LinkedList<PDFDocument>();
		methods = new LinkedList<ExtractionMethod>();
		dir = new HashMap<String, Publication>();

		Class<? extends ExtractionMethod>[] classes = null;

		// classes = ReflectionUtil.findClasses(ExtractionMethod.class);
		classes = new Class[2];
		classes[0] = net.sf.sysrev.methods.SoftwarePracticeAndExperience.class;
		classes[1] = net.sf.sysrev.methods.EmpiricalSoftwareEngineering.class;

		for (Class c : classes) {
			try {
				methods.add((ExtractionMethod) c.newInstance());
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		}
	}

	public void load(String[] filenames)
	{
		for (String filename : filenames) {
			PDFDocument document = null;
			try {
				document = new PDFDocument(filename);
				documents.add(document);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			} finally {
			}
		}
	}

	public void load(String filename)
	{
		String[] filenames = new String[1];
		filenames[0] = filename;
		load(filenames);
	}

	public void process()
	{
		for (PDFDocument doc : documents) {
			for (ExtractionMethod method : methods) {
				if (method.matches(doc)) {
					Publication pub = method.parse(doc);
					dir.put(doc.getFilename().getAbsolutePath(), pub);
					return;
				}
			}
		}
	}

	public Publication getDocument(String filename)
	{
		return dir.get(filename);
	}

	public static void main(String[] args)
	{
		FilesystemPDFMiner miner = new FilesystemPDFMiner();
		miner.load(args);
	}
}

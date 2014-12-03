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


package net.sf.sysrev.engines.pdf;

import java.awt.print.PageFormat;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

public class PDFMetadata
{
	public PDFMetadata(PDDocument document)
	{
	    PDDocumentInformation metadata = document.getDocumentInformation();
	    System.out.println(metadata.getAuthor());
	    System.out.println(metadata.getCreator());
	    System.out.println(metadata.getKeywords());
	    System.out.println(metadata.getProducer());
	    System.out.println(metadata.getSubject());
	    System.out.println(metadata.getTitle());
	    try {
			System.out.println(metadata.getCreationDate());
		} catch (IOException e) {
		}

	    for (int i = 1; i < document.getNumberOfPages(); i++) {
	    	System.out.println("Page " + i);
	    	PageFormat pf = document.getPageFormat(i);
	    	System.out.println(pf.getHeight());
	    	System.out.println(pf.getImageableHeight());
	    	System.out.println(pf.getWidth());
	    	System.out.println(pf.getImageableWidth());
	    	System.out.println(pf.getImageableX());
	    	System.out.println(pf.getImageableY());
	    	System.out.println(pf.getOrientation());
	    }
	}
}

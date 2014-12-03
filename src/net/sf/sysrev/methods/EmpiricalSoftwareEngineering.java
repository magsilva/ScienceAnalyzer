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

package net.sf.sysrev.methods;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ironiacorp.commons.StringUtil;

import net.sf.sysrev.engines.pdf.PDFDocument;
import net.sf.sysrev.types.Article;
import net.sf.sysrev.types.Author;
import net.sf.sysrev.types.Journal;
import net.sf.sysrev.types.PageSequence;
import net.sf.sysrev.types.Person;
import net.sf.sysrev.types.Publication;
import net.sf.sysrev.util.EncodingUtil;


public class EmpiricalSoftwareEngineering implements ExtractionMethod
{
	public static final String WORD_SEPARATOR = " "; 

	private static Pattern headerPattern = Pattern.compile("SOFTWARE.?PRACTICE AND EXPERIENCE");
	private static Pattern metadata1Pattern =
		Pattern.compile("\\# Springer Science \\+ Business Media, LLC (\\d{4})");
	private static Pattern metadata2Pattern =
		Pattern.compile("Editor: ((?:\\w+\\s?)+)");
	private static Pattern abstractStartPattern =
		Pattern.compile("Abstract\\s (.*)");
	
	public boolean matches(PDFDocument document)
	{
		String[] lines = document.getCachedTextLines();

		Matcher softwarePracticeAndExperienceHeaderMatcher =
			headerPattern.matcher(lines[0]);

		// return softwarePracticeAndExperienceHeaderMatcher.matches();
		return true;
	}

	public Publication parse(PDFDocument document)
	{
		Article pub = new Article();
		
		String[] lines = document.getCachedTextLines();
		int i = 0;
		
		Matcher headerMatcher = headerPattern.matcher(lines[0]);
		Matcher metadata1Matcher = null;
		Matcher metadata2Matcher = null;
		Matcher abstractEndMatcher = null;

		if (! matches(document)) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		while (i == 0 || lines[i].substring(0, 1).toLowerCase().equals(lines[i].substring(0, 1))) {
			sb.append(lines[i]);
			sb.append(WORD_SEPARATOR);
			i++;
		} 
		String title = sb.toString().trim();
		pub.setTitle(title);

		
		
		ArrayList<String> authorsTemp = new ArrayList<String>();
		while (! lines[i].substring(0,1).equals("#")) {
			String[] authors = lines[i].split("&");
			for (String author : authors) {
				if (! StringUtil.isEmpty(author)) {
					authorsTemp.add(EncodingUtil.translate(author.trim()));
				}
			}
			i++;
		}
		Author[] authors = new Author[authorsTemp.size()];
		for (int j = 0; j < authorsTemp.size(); j++) {
			authors[j] = new Author(authorsTemp.get(j));
		}
		pub.setAuthors(authors);

		
		int year = 0;
		metadata1Matcher = metadata1Pattern.matcher(lines[i]);
		i++;
		if (metadata1Matcher.matches()) {
			year = Integer.parseInt(metadata1Matcher.group(1));
		}
		pub.setYear(year);
		
		String publisher = "Springer";
		pub.setPublisher(publisher);


		Journal journal = new Journal();
		pub.setJournal(journal);

		String journalName = "Empirical Software Engineering";
		journal.setName(journalName);
		
		metadata2Matcher = metadata2Pattern.matcher(lines[i]);
		Person editor = null;
		if (metadata2Matcher.matches()) {
			editor = new Person(metadata2Matcher.group(1));
		}
		journal.setEditor(editor);	
		
		String abstractText = new String();
		do {
			abstractText += lines[i];
			abstractText += WORD_SEPARATOR;
			i++;
		} while (abstractEndMatcher.matches() == false);
		abstractText = abstractEndMatcher.group(1);
		pub.setAbstractText(abstractText);

		int edition = 0;
		int startPage = 0;
		int endPage = 0;
		String doi = null;
		if (metadata1Matcher.matches()) {
			year = Integer.parseInt(metadata1Matcher.group(1));
			edition = Integer.parseInt(metadata1Matcher.group(2));
			startPage = Integer.parseInt(metadata1Matcher.group(3));
			endPage = Integer.parseInt(metadata1Matcher.group(4));
		}
		if (metadata2Matcher.matches()) {
			doi = metadata2Matcher.group(4);
		}
		

		pub.setYear(year);
		pub.setEdition(edition);

		PageSequence pages = new PageSequence(startPage, endPage);
		pub.setPages(pages);

		pub.setDoi(doi);
		
		return pub;
	}

	public int getCapability()
	{
		return MethodCapability.AUTHORSHIP | MethodCapability.EDITOR | MethodCapability.PUBLISHER | MethodCapability.TITLE | MethodCapability.YEAR;
	}
}
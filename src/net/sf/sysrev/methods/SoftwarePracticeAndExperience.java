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

import net.sf.sysrev.engines.pdf.PDFDocument;
import net.sf.sysrev.types.Article;
import net.sf.sysrev.types.Author;
import net.sf.sysrev.types.Journal;
import net.sf.sysrev.types.PageSequence;
import net.sf.sysrev.types.Publication;
import net.sf.sysrev.util.EncodingUtil;


public class SoftwarePracticeAndExperience implements ExtractionMethod
{
	public static final String WORD_SEPARATOR = " ";

	private static Pattern softwarePracticeAndExperienceHeaderPattern =
		Pattern.compile("SOFTWARE.?PRACTICE AND EXPERIENCE");
	private static Pattern softwarePracticeAndExperienceMetadata1Pattern =
		Pattern.compile("Softw\\. Pract\\. Exper\\. (\\d{4}); (\\d+):(\\d+).?(\\d+)");
	private static Pattern softwarePracticeAndExperienceMetadata2Pattern =
		Pattern.compile("Published online (\\d+) (\\w+) (\\d{4}) in Wiley InterScience \\(www.interscience.wiley.com\\). DOI: (.*)$");
	private static Pattern softwarePracticeAndExperienceAbstractEndPattern =
		Pattern.compile("(.*) Copyright .?.? \\d{4} John Wiley & Sons, Ltd\\..*");


	public boolean matches(PDFDocument document)
	{
		String[] lines = document.getCachedTextLines();

		Matcher softwarePracticeAndExperienceHeaderMatcher =
			softwarePracticeAndExperienceHeaderPattern.matcher(lines[0]);

		return softwarePracticeAndExperienceHeaderMatcher.matches();
	}

	public Publication parse(PDFDocument document)
	{
		Article pub = new Article();

		String[] lines = document.getCachedTextLines();

		Matcher headerMatcher = softwarePracticeAndExperienceHeaderPattern.matcher(lines[0]);
		Matcher metadata1Matcher = softwarePracticeAndExperienceMetadata1Pattern.matcher(lines[1]);
		Matcher metadata2Matcher = softwarePracticeAndExperienceMetadata2Pattern.matcher(lines[2]);
		Matcher abstractEndMatcher = null;

		if (! matches(document)) {
			return null;
		}

		String publication = "Software - Practice and Experience";
		int year = 0;
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
		Journal journal = new Journal();
		journal.setName(publication);
		pub.setJournal(journal);

		pub.setYear(year);
		pub.setEdition(edition);

		PageSequence pages = new PageSequence(startPage, endPage);
		pub.setPages(pages);

		pub.setDoi(doi);

		StringBuffer sb = new StringBuffer();
		int i = 3;
		do {
			sb.append(lines[i]);
			sb.append(WORD_SEPARATOR);
			i++;
		} while (lines[i + 1].length() > 2);
		String title = sb.toString();
		pub.setTitle(title);

		ArrayList<String> authorsTemp = new ArrayList<String>();
		while (!lines[i + 2].equals("and")) {
			String author = lines[i];
			if (authorsTemp.size() > 0) {
				author = author.substring(2);
			}
			authorsTemp.add(EncodingUtil.translate(author));
			i += 2;
		}
		authorsTemp.add(EncodingUtil.translate(lines[i]));
		i += 3;
		authorsTemp.add(EncodingUtil.translate(lines[i]));

		Author[] authors = new Author[authorsTemp.size()];
		for (int j = 0; j < authorsTemp.size(); j++) {
			authors[j] = new Author(authorsTemp.get(j));
		}
		pub.setAuthors(authors);

		while (!lines[i].equals("SUMMARY")) {
			i++;
		}
		i++;

		String abstractText = new String();
		do {
			abstractText += lines[i];
			abstractText += WORD_SEPARATOR;
			i++;
			abstractEndMatcher =
				softwarePracticeAndExperienceAbstractEndPattern.matcher(abstractText);
		} while (abstractEndMatcher.matches() == false);
		abstractText = abstractEndMatcher.group(1);
		pub.setAbstractText(abstractText);

		return pub;
	}

	public int getCapability()
	{
		return MethodCapability.AUTHORSHIP | MethodCapability.DOI | MethodCapability.EDITOR | MethodCapability.KEYWORDS | MethodCapability.PAGES | MethodCapability.PUBLISHER | MethodCapability.TITLE | MethodCapability.YEAR;
	}

	/*
	 Pattern softwarePracticeAndExperienceHeaderPattern = Pattern.compile("SOFTWARE.?PRACTICE AND EXPERIENCE");
	 Pattern softwarePracticeAndExperienceMetadata1Pattern = Pattern.compile("Softw\\. Pract\\. Exper\\. (\\d{4}); (\\d+):(\\d+).?(\\d+)");
	 Pattern softwarePracticeAndExperienceMetadata2Pattern = Pattern.compile("Published online (\\d+) (\\w+) (\\d{4}) in Wiley InterScience \\(www.interscience.wiley.com\\). DOI: (.*)$");
	 Pattern softwarePracticeAndExperienceAbstractEndPattern = Pattern.compile("(.*) Copyright .?.? \\d{4} John Wiley & Sons, Ltd\\..*");
	 Matcher softwarePracticeAndExperienceHeaderMatcher = softwarePracticeAndExperienceHeaderPattern.matcher(lines[0]);
	 Matcher softwarePracticeAndExperienceMetadata1Matcher = softwarePracticeAndExperienceMetadata1Pattern.matcher(lines[1]);
	 Matcher softwarePracticeAndExperienceMetadata2Matcher = softwarePracticeAndExperienceMetadata2Pattern.matcher(lines[2]);
	 Matcher softwarePracticeAndExperienceAbstractEndMatcher = null;

	 if (softwarePracticeAndExperienceHeaderMatcher.matches()) {
		 String publication = "Software - Practice and Experience";
		 int year = 0;
		 int edition = 0;
		 int startPage = 0;
		 int endPage = 0;
		 String doi = null;
		 if (softwarePracticeAndExperienceMetadata1Matcher.matches()) {
			 year = Integer.parseInt(softwarePracticeAndExperienceMetadata1Matcher.group(1));
			 edition = Integer.parseInt(softwarePracticeAndExperienceMetadata1Matcher.group(2));
			 startPage = Integer.parseInt(softwarePracticeAndExperienceMetadata1Matcher.group(3));
			 endPage = Integer.parseInt(softwarePracticeAndExperienceMetadata1Matcher.group(4));
		 }
		 if (softwarePracticeAndExperienceMetadata2Matcher.matches()) {
			 doi = softwarePracticeAndExperienceMetadata2Matcher.group(4);
		 }
		 System.out.println(publication);
		 System.out.println(year);
		 System.out.println(edition);
		 System.out.println(startPage);
		 System.out.println(endPage);
		 System.out.println(doi);
		StringBuffer sb = new StringBuffer();
		int i = 3;
		do {
			sb.append(lines[i]);
			sb.append(WORD_SEPARATOR);
			i++;
		} while (lines[i + 1].length() > 2);
		String title = sb.toString();
		System.out.println(title);

		ArrayList<String> authors = new ArrayList<String>();
		while (! lines[i + 2].equals("and")) {
			String author = lines[i];
			if (authors.size() > 0) {
				author = author.substring(2);
			}
			authors.add(translate(author));
			i += 2;
		}
		authors.add(translate(lines[i]));
		i += 3;
		authors.add(translate(lines[i]));

		System.out.println(authors);

		while (! lines[i].equals("SUMMARY")) {
			i++;
		}
		i++;

		String articleAbstract = new String();
		do {
			articleAbstract += lines[i];
			articleAbstract += WORD_SEPARATOR;
			i++;
			softwarePracticeAndExperienceAbstractEndMatcher = softwarePracticeAndExperienceAbstractEndPattern.matcher(articleAbstract);
		} while (softwarePracticeAndExperienceAbstractEndMatcher.matches() == false);
		articleAbstract = softwarePracticeAndExperienceAbstractEndMatcher.group(1);
		System.out.println(articleAbstract);
	}
*/
}
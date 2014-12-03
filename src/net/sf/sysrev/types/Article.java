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

package net.sf.sysrev.types;

public class Article extends Publication
{
	private Journal journal;
	
	private String name;
	
	private int year;
	
	private int month;
	
	private int edition;
	
	private PageSequence[] pages;
	
	private String doi;
	
	private Author[] authors;
	
	private String title;
	
	private String abstractText;
	
	private String publisher;
	
	public String getPublisher()
	{
		return publisher;
	}

	public void setPublisher(String publisher)
	{
		this.publisher = publisher;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	
	public Author[] getAuthors()
	{
		return authors;
	}

	public void setAuthors(Author[] authors)
	{
		this.authors = authors;
	}

	public String getDoi()
	{
		return doi;
	}

	public void setDoi(String doi)
	{
		this.doi = doi;
	}

	public int getEdition()
	{
		return edition;
	}

	public void setEdition(int edition)
	{
		this.edition = edition;
	}

	public int getMonth()
	{
		return month;
	}

	public void setMonth(int month)
	{
		this.month = month;
	}

	public PageSequence[] getPages()
	{
		return pages;
	}

	public void setPages(PageSequence[] pages)
	{
		this.pages = pages;
	}

	public void setPages(PageSequence pages)
	{
		this.pages = new PageSequence[1];
		this.pages[0] = pages;
	}

	
	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public Journal getJournal()
	{
		return journal;
	}

	public void setJournal(Journal journal)
	{
		this.journal = journal;
	}

	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getAbstractText()
	{
		return abstractText;
	}

	public void setAbstractText(String abstractText)
	{
		this.abstractText = abstractText;
	}
	


}

package com.ironiacorp.scienceanalyzer.library;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import com.ironiacorp.scienceanalyzer.Person;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Publication
{
	@Id
	@GeneratedValue
	private int id;
	
	@Basic
	private boolean active;
	
	@Basic
	private String title;
	
	@Basic
	private Date date;
	
	@OneToMany
	private List<Person> authors;

	public int getId()
	{
		return id;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public void addAuthor(Person person)
	{
		if (authors.contains(person)) {
			throw new IllegalArgumentException("Person is already listed as author of the publication");
		}
		authors.add(person);
	}
	
	public void addAuthor(Person person, int position)
	{
		if (authors.contains(person)) {
			authors.remove(person);
		}

		authors.add(position, person);
	}
	
	public List<Person> getAuthors()
	{
		return authors;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Publication other = (Publication) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	
}

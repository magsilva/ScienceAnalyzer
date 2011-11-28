package com.ironiacorp.scienceanalyzer.library;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Journal extends Publication
{
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Issn> issn;

	@Basic
	private String name;
	
	@Basic
	private int startYear;
	
	@Basic
	private Date startDate;
	
	@Basic
	private int endYear;
	
	@Basic
	private Date endDate;
	
	@Basic
	private String address;
	
	@Basic
	private String acronym;

	public Journal()
	{
		issn = new HashSet<Issn>();
	}
	
	public String getAcronym()
	{
		return acronym;
	}

	public void setAcronym(String acronym)
	{
		this.acronym = acronym;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}



	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getStartYear()
	{
		return startYear;
	}

	public void setStartYear(int startYear)
	{
		this.startYear = startYear;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public int getEndYear()
	{
		return endYear;
	}

	public void setEndYear(int endYear)
	{
		this.endYear = endYear;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}
	
	public Set<Issn> getIssn()
	{
		return issn;
	}

	public void setIssn(Set<Issn> issn)
	{
		this.issn = issn;
	}
	
	public void addIssn(Issn issn)
	{
		this.issn.add(issn);
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(",");
		sb.append(name);
		
		return sb.toString();
	}
}

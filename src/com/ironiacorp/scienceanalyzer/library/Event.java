package com.ironiacorp.scienceanalyzer.library;

import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public class Event extends Publication
{
	@Basic
	private String name;
	
	@Basic
	private String acronym;
	
	@Basic
	private String scope;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAcronym()
	{
		return acronym;
	}

	public void setAcronym(String acronym)
	{
		this.acronym = acronym;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}
	
}

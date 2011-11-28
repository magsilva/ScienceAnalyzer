package com.ironiacorp.scienceanalyzer.library;

import javax.persistence.Id;

public class Publisher
{
	@Id
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	
}

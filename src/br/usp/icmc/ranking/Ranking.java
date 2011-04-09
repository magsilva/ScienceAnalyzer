package br.usp.icmc.ranking;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import br.usp.icmc.library.Publication;

@Entity
public abstract class Ranking
{
	@Id
	@GeneratedValue
	private int id;

	@OneToOne
	private Publication publication;

	@Basic
	private int year;
	
	@Basic
	private int validityStart;
	
	@Basic
	private int validityEnd;
	
	public int getId()
	{
		return id;
	}
	
	public Publication getPublication()
	{
		return publication;
	}

	public void setPublication(Publication publication)
	{
		this.publication = publication;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public int getValidityStart()
	{
		return validityStart;
	}

	public void setValidityStart(int validityStart)
	{
		this.validityStart = validityStart;
	}

	public int getValidityEnd()
	{
		return validityEnd;
	}

	public void setValidityEnd(int validityEnd)
	{
		this.validityEnd = validityEnd;
	}
}

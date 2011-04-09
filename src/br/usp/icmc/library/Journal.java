package br.usp.icmc.library;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.xerces.impl.xpath.regex.Match;

import au.com.bytecode.opencsv.CSVReader;

@Entity
public class Journal extends Publication
{

	private String onlineIssn;

	private String printIssn;

	private String name;
	
	private int startYear;
	
	private Date startDate;
	
	private int endYear;
	
	private Date endDate;
	
	private String address;

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}


	public String getOnlineIssn()
	{
		return onlineIssn;
	}

	public void setOnlineIssn(String onlineIssn)
	{
		this.onlineIssn = onlineIssn;
	}

	public String getPrintIssn()
	{
		return printIssn;
	}

	public void setPrintIssn(String printIssn)
	{
		this.printIssn = printIssn;
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
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(onlineIssn);
		sb.append(",");
		sb.append(printIssn);
		sb.append(",");
		sb.append(name);
		
		return sb.toString();
	}
}

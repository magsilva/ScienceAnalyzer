package com.ironiacorp.scienceanalyzer.ranking;

import java.io.File;

public abstract class CsvRankImporter implements RankImporter
{
	protected File file;
	
	protected int year;
	
	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}
	
	@Override
	public String toString()
	{
		return file.getAbsolutePath();
	}
}

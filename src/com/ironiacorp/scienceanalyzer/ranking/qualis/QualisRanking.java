package com.ironiacorp.scienceanalyzer.ranking.qualis;

import javax.persistence.Basic;
import javax.persistence.Entity;

import com.ironiacorp.scienceanalyzer.ranking.Ranking;


@Entity
public class QualisRanking extends Ranking
{
	@Basic
	private String ranking;
	
	@Basic
	private String area;

	public String getArea()
	{
		return area;
	}

	public void setArea(String area)
	{
		this.area = area;
	}

	public String getRanking()
	{
		return ranking;
	}

	public void setRanking(String ranking)
	{
		this.ranking = ranking;
	}
	
	
}

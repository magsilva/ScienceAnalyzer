package com.ironiacorp.scienceanalyzer.ranking.era;

import javax.persistence.Basic;
import javax.persistence.Entity;

import com.ironiacorp.scienceanalyzer.ranking.Ranking;


@Entity
public class EraRanking extends Ranking
{
	@Basic
	private String ranking;
	
	public String getRanking()
	{
		return ranking;
	}

	public void setRanking(String ranking)
	{
		this.ranking = ranking;
	}
}

package br.usp.icmc.ranking.era;

import javax.persistence.Basic;
import javax.persistence.Entity;

import br.usp.icmc.ranking.Ranking;

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

package br.usp.icmc.ranking.qualis;

import javax.persistence.Basic;
import javax.persistence.Entity;

import br.usp.icmc.ranking.Ranking;

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

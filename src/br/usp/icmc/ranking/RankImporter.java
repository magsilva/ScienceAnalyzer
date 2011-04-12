package br.usp.icmc.ranking;

import java.io.IOException;

public interface RankImporter
{
	int getYear();
	
	void setYear(int year);
	
	void importRankings() throws IOException;
}

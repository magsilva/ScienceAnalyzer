package br.usp.icmc.ranking;

import java.io.File;
import java.io.IOException;

public interface RankImporter
{
	int getYear();
	
	void setYear(int year);
	
	void read(File file) throws IOException;
}

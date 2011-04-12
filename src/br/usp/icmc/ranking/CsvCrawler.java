package br.usp.icmc.ranking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.ironiacorp.io.Filesystem;

import br.usp.icmc.ranking.era.EraEventsImporter;
import br.usp.icmc.ranking.era.EraJournalsImporter;
import br.usp.icmc.ranking.qualis.QualisEventsImporter;
import br.usp.icmc.ranking.qualis.QualisJournalsImporter;

public class CsvCrawler
{
	private List<RankImporter> importers;
	
	public CsvCrawler()
	{
		importers = new ArrayList<RankImporter>();
	}
	
	public void addQualisEventsImporter(File file, String area, int year)
	{
		QualisEventsImporter importer = new QualisEventsImporter();
		importer.setArea(area);
		importer.setYear(year);
		importer.setFile(file);
		importers.add(importer);
	}

	public void addQualisJournalsImporter(File file, String area, int year)
	{
		QualisJournalsImporter importer = new QualisJournalsImporter();
		importer.setArea(area);
		importer.setYear(year);
		importer.setFile(file);
		importers.add(importer);
	}

	public void addEraJournalsImporter(File file, int year)
	{
		EraJournalsImporter importer = new EraJournalsImporter();
		importer.setYear(year);
		importer.setFile(file);
		importers.add(importer);
	}

	public void addEraEventsImporter(File file, int year)
	{
		EraEventsImporter importer = new EraEventsImporter();
		importer.setYear(year);
		importer.setFile(file);
		importers.add(importer);
	}
	
	public Pattern getPattern(String name, String type)
	{
		Pattern pattern = Pattern.compile(name + "\\s+(\\d{4}\\s+)?-.*" + type + "\\.csv$", Pattern.CASE_INSENSITIVE);
		return pattern;
	}
	
	public void craw() throws IOException
	{
		for (RankImporter importer : importers) {
			System.out.println("Importing data from " + importer.toString());
			importer.importRankings();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		CsvCrawler crawler = new CsvCrawler();
		Filesystem fs = new Filesystem();
		
		for (File file : fs.find(
				new File("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Ranking/Qualis/"),
				crawler.getPattern("Qualis", "Periódicos"))
		) {
			String name = file.getName();
			String area =  name.split("-")[1].trim();
			int year = Integer.parseInt(name.split("-")[0].replaceAll("Qualis", "").trim());
			crawler.addQualisJournalsImporter(file, area, year);
		}
	
		for (File file : fs.find(
				new File("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Ranking/Qualis/"),
				crawler.getPattern("Qualis", "Eventos"))
		) {
			String name = file.getName();
			String[] fields = name.split("-");
			String area = null;
			if (fields.length == 3) {
				area = fields[1].trim();
			}
			int year = Integer.parseInt(fields[0].replaceAll("Qualis", "").trim());
			crawler.addQualisEventsImporter(file, area, year);
		}
		
		for (File file : fs.find(
				new File("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Ranking/ERA/"),
				crawler.getPattern("ERA", "Periódicos"))
		) {
			String name = file.getName();
			int year = Integer.parseInt(name.split("-")[0].replaceAll("ERA", "").trim());
			crawler.addEraJournalsImporter(file, year);
		}

	
		for (File file : fs.find(
				new File("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Ranking/ERA/"),
				crawler.getPattern("ERA", "Eventos"))
		) {
			String name = file.getName();
			int year = Integer.parseInt(name.split("-")[0].replaceAll("ERA", "").trim());
			crawler.addEraEventsImporter(file, year);
		}
		
		crawler.craw();
	}
}

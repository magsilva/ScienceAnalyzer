package br.edu.utfpr.cm.scienceanalyzer.evol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import lode.miner.extraction.bibtex.handmade.HandmadeBibtexParser;
import lode.model.publication.Collection;
import lode.model.publication.EventArticle;
import lode.model.publication.Publication;
import mariane.GUESSGraphPreProcessing;


public class ScienceEvolution
{
	private int topics;
	
	private Corpus corpus;
	
	private TopicModel dtmModel;
	
	private TopicModel influenceModel;
	
	private SocialModel coauthoringModel;
	
	private List<InputStream> inputs;
	
	private File basedir;
	
	private String corpusName;
	
	public ScienceEvolution() 
	{
		inputs = new ArrayList<InputStream>();
	}
	
	public int getTopics() {
		return topics;
	}

	public void setTopics(int topics) {
		this.topics = topics;
	}

	public File getBasedir() {
		return basedir;
	}

	
	public String getCorpusName() {
		return corpusName;
	}

	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}

	public void setBasedir(File basedir) {
		if (basedir.exists()) {
			if (! basedir.isDirectory()) {
				throw new IllegalArgumentException("It exists, but it is not a directory: " + basedir.getName());
			}
		} else {
			if (! basedir.mkdirs()) {
				throw new IllegalArgumentException("Could not create directory: " + basedir.getName());
			}
		}
		
		this.basedir = basedir;
	}

	public void addInput(InputStream is)
	{
		inputs.add(is);
	}
	

	public void run() throws IOException
	{
		/*
		BibTeX2DTM bib2dtm = new BibTeX2DTM();
		
		basedir.mkdirs();
		bib2dtm.setDefaultOutputStreams(basedir, corpusName);
		for (InputStream is : inputs) {
			bib2dtm.addInputStream(is);
		}
		bib2dtm.read();
		bib2dtm.write();
		
		corpus = bib2dtm.getCorpus();
			
		DTM dtm = new DTM();
		dtm.suggestSearchPath(new File("/home/magsilva/Dropbox/Papers/10thSBSC/resources/dtm"));
		// TODO: adicionar caminho para o diretório com o executável com o DTM no computador de vocês
		dtm.suggestSearchPath(new File("/home/magsilva/Dropbox/Papers/10thSBSC/resources/dtm"));
		dtm.setPaperCitedAfterYears(1.0);
		dtm.setPaperCitedAfterYearsStdDev(3.0);
		dtm.setTopics(topics);
		dtm.setYearsPerPeriod(1);
		dtm.setAlpha(0.05);
		dtm.setMinIterations(5);
		dtm.setMaxIterations(100);
		dtm.setCorpusPrefix(basedir.getAbsolutePath() + File.separator + corpusName);
		dtm.setResultsPrefix(basedir.getAbsolutePath() + File.separator);
		dtmModel = dtm.runFit();
		influenceModel = dtm.runInfluence();
		
		coauthoringModel = new SocialModel();
		coauthoringModel.setBasedir(basedir);
		coauthoringModel.setName(corpusName);
		GUESSGraphPreProcessing guess = new GUESSGraphPreProcessing();
		guess.readData(corpus);
		*/
		
		/*
         * TODO: compile a consolidated BibTeX and use a pretty GDF to create a brand-new GDF with year data.
         */
		GUESSGraphPreProcessing guess = new GUESSGraphPreProcessing();
		guess.readData(new File("/home/mariane/Dropbox/RS-Mariane/AutomaticEvaluation/referencias_mojo.bib"));
		try {
			guess.createCoAuthoringGraph(new File("/home/mariane/Dropbox/RS-Mariane/AutomaticEvaluation/Citation analysis/TODO/Network_of_co-authored.gdf"));
		} catch (Exception e) {
			throw new RuntimeException("Error found when updating coauthoring network", e);
		}
	}
	
	public void visualize(int topTermsPerTopic) throws IOException
	{
		Util util = new Util(corpus, dtmModel);
		util.update();
		util.printXBetterResultsPerYearPerTopic(topTermsPerTopic, 0.005);
		util.printXBetterResultsPerYearPerTopicInFile(0.005);
	}
	
	public static void main(String[] args) throws Exception {
		
		ScienceEvolution evolution = new ScienceEvolution();
		evolution.run();
		/*String files[] = {
				"SBSC-2004.bib",
				"SBSC-2005.bib", 
				"SBSC-2006.bib",
				"SBSC-2007.bib",
				"SBSC-2008.bib",
				"SBSC-2009.bib",
				"SBSC-2010.bib",
				"SBSC-2011.bib",
				"SBSC-2012.bib",
		};*/
		
		
		/*for (String file : files) {
			HandmadeBibtexParser parser = new HandmadeBibtexParser();
			ArrayList<Integer> pagesPerPaper = new ArrayList<Integer>();
			Collection collection = null;
			int quantidade, totalPaginas, minPages = 9999, maxPages = -1;
			double media, variance, averageVariance;
			
			quantidade = 0;
			totalPaginas = 0;
			parser.setPreferredLanguage("en");
			parser.setIgnoreErrors(true);
			collection = parser.parse(file, new BufferedReader(new FileReader("/home/magsilva/Dropbox/Papers/10thSBSC/" + file)));
			
			Iterator<Publication> i = collection.iterator();
			while (i.hasNext()) {
				Publication publication = i.next();
				if (publication instanceof EventArticle) {
					EventArticle article = (EventArticle) publication;
					int articleLength = article.getLength(); 
					totalPaginas += articleLength;
					pagesPerPaper.add(articleLength);
					quantidade++;
					if (articleLength < minPages) {
						minPages = articleLength;
					} else {
						if (articleLength > maxPages) {
							maxPages = articleLength;
						}
					}
					System.out.println(article.getReferenceId() + ", " + article.getYear() + ", " + articleLength);
				}
			}
			
			media = (totalPaginas * 1.0) / quantidade;
			variance= 0;
			for (int pages : pagesPerPaper) {
				variance += (media - pages) * (media - pages);
			}
			variance = Math.sqrt(variance / quantidade);
			averageVariance = ((maxPages - minPages) * 1.0) / quantidade;	
			
		}*/
	/*
		
	
		evolution.setBasedir(new File("/tmp/SBSC"));
		evolution.setCorpusName("SBSC");
		evolution.setTopics(20);
		for (String file : files) {
			InputStream is = BibTeX2DTM.class.getResourceAsStream("/" + file);
			evolution.addInput(is);
		}
		evolution.run();
	//	evolution.visualize(30);
	*/
	}
}

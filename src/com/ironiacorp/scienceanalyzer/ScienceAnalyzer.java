/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ironiacorp.scienceanalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jfree.ui.RefineryUtilities;


import au.com.bytecode.opencsv.CSVReader;

import com.ironiacorp.graph.layout.Graphviz;
import com.ironiacorp.graph.layout.GraphvizGraph;
import com.ironiacorp.graph.model.Graph;
import com.ironiacorp.scienceanalyzer.education.Degree;
import com.ironiacorp.scienceanalyzer.education.MasterDegree;
import com.ironiacorp.scienceanalyzer.education.PhdDegree;
import com.ironiacorp.scienceanalyzer.library.Dissertation;
import com.ironiacorp.scienceanalyzer.loader.CsvLoader;

/**
 *
 * @author Aretha
 */
public class ScienceAnalyzer
{
	public void generateAdvisingGraph() throws Exception
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("Alumni/ICMC/alumni-icmc-posgrad-ccmc.csv");
		CSVReader reader = new CSVReader(new InputStreamReader(is));
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    	Map<Person, Set<Person>> alumniByAdvisor = new TreeMap<Person, Set<Person>>();
    	Iterator<Person> i;
		String[] line;
		
		reader.readNext();
		while ((line = reader.readNext()) != null) {
	    	String adviseeDegree = line[0].trim();
	    	String dissertationTitle = line[1].trim();
	    	String adviseeName = line[2].trim();
	    	String advisorName = line[3].trim();
	    	String degreeDate = line[4].trim();
	    	String emails = line[8].trim();
	   	
	    	Person advisee = new Person();
	    	Person advisor = new Person();
	    	Degree degree;
	    	Dissertation dissertation = new Dissertation();
	    	
	    	dissertation.setTitle(dissertationTitle);
	    	
	    	if ("ME".equals(adviseeDegree)) {
	    		degree = new MasterDegree();
	    	} else {
	    		if ("DO".equals(adviseeDegree)) {
	    			degree = new PhdDegree();
	    		} else {
	    			throw new IllegalArgumentException("Invalid or unknown degree");
	    		}
	    	}
	    	degree.setAdvisor(advisor);
	    	degree.setDate(dateFormatter.parse(degreeDate));
	    	degree.setDissertation(dissertation);

	    	advisor.setName(advisorName);
	    	
	    	advisee.setName(adviseeName);
	    	advisee.addDegree(degree);
	    	for (String email : emails.split(",")) {
	    		advisee.addEmail(email);
	    	}
	    	
	    	if (! alumniByAdvisor.containsKey(advisor)) {
	    		alumniByAdvisor.put(advisor, new TreeSet<Person>());
	    	}
	    	alumniByAdvisor.get(advisor).add(advisee);
		}
		
		i = alumniByAdvisor.keySet().iterator();
		while (i.hasNext()) {
			Person advisor = i.next();
			Set<Person> advisees = alumniByAdvisor.get(advisor);
			if (advisees.size() > 0) {
				System.out.println(advisor.getName());
				for (Person advisee : advisees) {
					System.out.println("\t" + advisee.getName());
					
					System.out.println("\tEmails:");
					for (String email : advisee.getEmails()) {
						System.out.println("\t\t" + email);
					}
					if (advisee.getEmails().isEmpty()) {
						System.out.println("\t\t(nenhum email foi informado ou encontrado)");
					}
					
					System.out.println("\tTítulos obtidos sob sua orientação:");
					Set<Degree> degrees = advisee.getDegrees();
					for (Degree degree : degrees) {
						if (degree.getAdvisor().equals(advisor)) {
							System.out.println("\t\t" + degree.toString());
						}
					}
					System.out.println();
				}
			}
		}
	}
	
	public void generateAdvisingGraph(String name) throws IOException
	{
		AdvisingGraph advisingGraph = new AdvisingGraph();
		GraphvizGraph graphvizGraph = new GraphvizGraph(); 
		Graphviz graphviz = new Graphviz();
		Graph graph;
		String digraph;
		String[] professorsCCMC = {
				"Paulo Cesar Masiero",
				"Maria Carolina Monard",
		};
		String[] professorsMAT = {
				"Maria Aparecida Soares Ruas",
				"Hildebrando Munhoz Rodrigues",
				"Odelar Leite Linhares",
		};

		for (String professor : professorsCCMC) {
			graph = advisingGraph.getGraphFor(new FileInputStream("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Alumni/ICMC/alumni-icmc-posgrad-ccmc.csv"), professor);
			digraph = graphvizGraph.convert(graph);
			graphviz.run(digraph, Graphviz.Filter.DOT, Graphviz.OutputFormat.PNG, new File("/tmp/" + professor + ".dot.png"));
			graphviz.run(digraph, Graphviz.Filter.FDP, Graphviz.OutputFormat.PNG, new File("/tmp/" + professor + ".fdp.png"));
			graphviz.run(digraph, Graphviz.Filter.CIRCO, Graphviz.OutputFormat.PNG, new File("/tmp/" + professor + ".circo.png"));
		}	
		for (String professor : professorsMAT) {
			graph = advisingGraph.getGraphFor(new FileInputStream("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Alumni/ICMC/alumni-icmc-posgrad-mat.csv"), professor);
			digraph = graphvizGraph.convert(graph);
			graphviz.run(digraph, Graphviz.Filter.DOT, Graphviz.OutputFormat.PNG, new File("/tmp/" + professor + ".dot.png"));
			graphviz.run(digraph, Graphviz.Filter.FDP, Graphviz.OutputFormat.PNG, new File("/tmp/" + professor + ".fdp.png"));
		}
		graph = advisingGraph.getGraphFor(new FileInputStream("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Alumni/ICMC/alumni-icmc-posgrad-ccmc.csv"));
		digraph = graphvizGraph.convert(graph);
		graphviz.run(digraph, Graphviz.Filter.FDP, Graphviz.OutputFormat.PNG, new File("/tmp/CCMC.png"));
		graph = advisingGraph.getGraphFor(new FileInputStream("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Alumni/ICMC/alumni-icmc-posgrad-mat.csv"));
		digraph = graphvizGraph.convert(graph);
		graphviz.run(digraph, Graphviz.Filter.FDP, Graphviz.OutputFormat.PNG, new File("/tmp/MAT.png"));
	}
	
    /**
     * @param args the command line arguments
     */
    public static void main(TreeMap<Integer, HashMap<String, ArrayList<Person>>> defesasPorAno)
    {	
    	Graphic graphic = new Graphic("Computação", "Defesas Por Ano - CCMC");
        graphic.generateGraphic(defesasPorAno, false);
        graphic.pack();
        RefineryUtilities.centerFrameOnScreen(graphic);
        graphic.setVisible(true);

        graphic = new Graphic("Computação", "Defesas Por Ano (ME/DO) - CCMC");
        graphic.generateGraphic2(defesasPorAno, true);
        graphic.pack();
        RefineryUtilities.centerFrameOnScreen(graphic);
        graphic.setVisible(true);
        
        graphic = new Graphic("Computação", "Mestrado/Doutorado - CCMC");
        graphic.generateGraphic3(defesasPorAno, true);
        graphic.pack();
        RefineryUtilities.centerFrameOnScreen(graphic);
        graphic.setVisible(true);
        
        graphic = new Graphic("Matemática", "Defesas por Ano - Mat");
        graphic.generateGraphic(defesasPorAno,false);
        graphic.pack();
        RefineryUtilities.centerFrameOnScreen(graphic);
        graphic.setVisible(true);
        
        graphic = new Graphic("Matemática", "Defesas Por Ano (ME/DO) - Mat");
        graphic.generateGraphic2(defesasPorAno, true);
        graphic.pack();
        RefineryUtilities.centerFrameOnScreen(graphic);
        graphic.setVisible(true);
        
         graphic = new Graphic("Matemática", "Mestrado/Doutorado - Mat");
        graphic.generateGraphic3(defesasPorAno, true);
        graphic.pack();
        RefineryUtilities.centerFrameOnScreen(graphic);
        graphic.setVisible(true);
    }
}

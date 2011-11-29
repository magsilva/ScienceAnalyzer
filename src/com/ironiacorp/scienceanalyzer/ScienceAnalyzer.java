/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ironiacorp.scienceanalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jfree.ui.RefineryUtilities;


import com.ironiacorp.graph.layout.Graphviz;
import com.ironiacorp.graph.layout.GraphvizGraph;
import com.ironiacorp.graph.model.Graph;
import com.ironiacorp.scienceanalyzer.loader.CsvLoader;

/**
 *
 * @author Aretha
 */
public class ScienceAnalyzer
{
	public void generateAdvisingGraph() throws IOException
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
    public static void main(String[] args)
    {
		CsvLoader process = new CsvLoader();
      	String[] files = {"Alumni/ICMC/alumni-icmc-posgrad-ccmc.csv", "Alumni/ICMC/alumni-icmc-posgrad-mat.csv"};
    	
    	for (String filename : files) {
    	  	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
    		process.run(is);
    	}
    	
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
        
        Graphic graphic = new Graphic("Matemática", "Defesas por Ano - Mat");
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

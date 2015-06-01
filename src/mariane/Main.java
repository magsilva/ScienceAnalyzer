package mariane;

import java.io.File;
import java.io.IOException;

import br.edu.utfpr.cm.scienceanalyzer.evol.GUESSGraphPreProcessing;





public class Main {

	public static void main(String[] args) throws IOException {
		GUESSGraphPreProcessing guess = new GUESSGraphPreProcessing();
		guess.readData(new File("/home/mariane/Dropbox/RS-Mariane/AutomaticEvaluation/referencias_mojo.bib"));
		try {
			guess.createCoAuthoringGraph(new File("/home/mariane/Grafo1.gdf"));
			//guess.createCoAuthoringGraph(new File("/home/mariane/Dropbox/teste.gdf"));
		} catch (Exception e) {
			throw new RuntimeException("Error found when updating coauthoring network", e);
		}

	}

}


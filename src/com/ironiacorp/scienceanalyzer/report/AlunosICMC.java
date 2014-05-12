/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alunosicmc;

/**
 *
 * @author Aretha
 */
public class AlunosICMC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ProcessFileCCMC process = new ProcessFileCCMC("alumni-icmc-posgrad-ccmc.csv");
        process.run();

        ProcessFileMat processMat = new ProcessFileMat("alumni-icmc-posgrad-mat.csv");
        processMat.run();
    }
}

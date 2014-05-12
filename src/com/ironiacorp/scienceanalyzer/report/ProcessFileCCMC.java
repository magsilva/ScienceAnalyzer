/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alunosicmc;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Aretha
 */
public class ProcessFileCCMC {

    String filename;
    TreeMap<Integer, HashMap<String, ArrayList<Aluno>>> defesasPorAno = new TreeMap<Integer, HashMap<String, ArrayList<Aluno>>>();

    public ProcessFileCCMC(String filename) {
        this.filename = filename;
    }

    public void run() {
        int index_line = 2;
        try {
            CSVReader reader;
            String aux, dia, mes, ano, curso;
            String[] line;
            reader = new CSVReader(new FileReader(filename));


            StringTokenizer st_data;
            Aluno aluno;
            ArrayList<Aluno> alunos = new ArrayList<Aluno>();

            reader.readNext();
            while ((line = reader.readNext()) != null) {
                aluno = new Aluno();

                //curso (ME ou DO)
                curso = line[0];
                if (curso.compareToIgnoreCase("ME") == 0) {
                    aluno.setCurso(Aluno.ME);
                } else if (curso.compareToIgnoreCase("DO") == 0) {
                    aluno.setCurso(Aluno.DO);
                }

                //titulo dissertacao/tese
                aux = line[1];
                aluno.setTitulo(aux);

                //nome
                aux = line[2];
                aluno.setNome(aux);


                //orientador
                aux = line[3];
                aluno.setOrientador(aux);

                //data defesa
                aux = line[4];
                if (aux.contains("/")) {
                    st_data = new StringTokenizer(aux, "/");
                    dia = st_data.nextToken();
                    mes = st_data.nextToken();
                    ano = st_data.nextToken();
                    aluno.setDefesa(new Date(Integer.parseInt(ano), Integer.parseInt(mes), Integer.parseInt(dia)));
                } else {
                    ano = aux;
                    aluno.setDefesa(new Date(Integer.parseInt(ano), 1, 1));
                }
                if (defesasPorAno.containsKey(Integer.parseInt(ano))) {
                    defesasPorAno.get(Integer.parseInt(ano)).get(curso).add(aluno);
                } else {
                    ArrayList<Aluno> me = new ArrayList<Aluno>();
                    ArrayList<Aluno> dout = new ArrayList<Aluno>();
                    if (curso.compareToIgnoreCase("ME") == 0) {
                        me.add(aluno);
                    } else if (curso.compareToIgnoreCase("DO") == 0) {
                        dout.add(aluno);
                    }
                    HashMap<String, ArrayList<Aluno>> hashmap = new HashMap<String, ArrayList<Aluno>>();
                    hashmap.put("ME", me);
                    hashmap.put("DO", dout);
                    defesasPorAno.put(Integer.parseInt(ano), hashmap);
                }

                //nro usp
                aux = line[5];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setNroUSP(Double.parseDouble(aux));
                }

                //id lattes
                aux = line[6];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setIdLattes(aux);
                }

                //email
                aux = line[7];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setEmail(aux);
                }

                //telefone
                aux = line[8];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setTelefone(aux);
                }

                //local de trabalho
                aux = line[9];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setLocalDeTrabalho(aux);
                }

                //facebook
                aux = line[10];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setFacebook(aux);
                }

                //orkut
                aux = line[11];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setOrkut(aux);
                }

                //linkedin
                aux = line[12];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setLinkedin(aux);
                }

                //twitter
                aux = line[13];
                if (aux.compareToIgnoreCase("") != 0) {
                    aluno.setTwitter(aux);
                }

                alunos.add(aluno);
                index_line++;
            }
            System.out.println("Total de Alunos: " + alunos.size());

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

        } catch (Exception ex) {
            Logger.getLogger(ProcessFileCCMC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("index line: " + index_line);
        }
    }
}

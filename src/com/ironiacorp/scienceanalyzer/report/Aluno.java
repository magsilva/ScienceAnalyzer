/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alunosicmc;

import java.util.Date;

/**
 *
 * @author Aretha
 */
public class Aluno {

    public static final int ME = 1;
    public static final int DO = 2;
    int curso;
    String nome, titulo, orientador, id_lattes, email, telefone, local_de_trabalho, facebook, orkut, linkedin, twitter;
    double nro_USP;
    Date defesa;

    public void setCurso(int curso) {
        this.curso = curso;
    }

    public int getCurso() {
        return this.curso;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setOrientador(String orientador) {
        this.orientador = orientador;
    }

    public void setIdLattes(String id_lattes) {
        this.id_lattes = id_lattes;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setDefesa(Date defesa) {
        this.defesa = defesa;
    }

    public void setNroUSP(double nro_USP) {
        this.nro_USP = nro_USP;
    }

    public void setLocalDeTrabalho(String local_de_trabalho) {
        this.local_de_trabalho = local_de_trabalho;
    }

    public void setOrkut(String orkut) {
        this.orkut = orkut;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }
}

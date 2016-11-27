package br.com.anagnostou.publisher;

import android.text.TextUtils;

/**
 * Created by George on 26/07/2016.
 */
public class Relatorio {
    static int registros;
    //ano, mes,nome,modalidade,videos,horas,publicacoes,revisitas,estudos
    int ano;
    int mes;
    String nome;
    String modalidade;
    int videos;
    int horas;
    int publicacoes;
    int revisitas;
    int estudos;

    public Relatorio(int ano, int mes, String nome, String modalidade, int videos, int horas, int publicacoes, int revisitas, int estudos) {
        this.ano = ano;
        this.mes = mes;
        this.nome = nome;
        this.modalidade = modalidade;
        this.videos = videos;
        this.horas = horas;
        this.publicacoes = publicacoes;
        this.revisitas = revisitas;
        this.estudos = estudos;
    }

    public Relatorio (String what){
        String iano="";
        String imes="";
        String snome="";
        String smodalidade="";
        String ivideos="";
        String ihoras="";
        String ipublicacoes="";
        String irevisitas="";
        String iestudos="";

        //'\uFEFF' 65279
        // Remove the leading BOM characters.
        //what = what.replaceAll("\\uFEFF", "");
        //utfFEFF =  what.substring(0, what.indexOf(";"));
        what = getRidOfUTF(what);
        try {
            iano = what.substring(0, what.indexOf(";"));
        } catch (Exception e) {
            //L.m("UTF problems");
            iano = "2016";
        }

        what = what.substring(what.indexOf(";") + 1, what.length());

        imes = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        snome = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        smodalidade = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        ivideos = (what.substring(0, what.indexOf(";")));
        if (TextUtils.isEmpty(ivideos)) ivideos="0";
        what = what.substring(what.indexOf(";") + 1, what.length());

        ihoras = (what.substring(0, what.indexOf(";")));
        if (TextUtils.isEmpty(ihoras)) ihoras="0";
        what = what.substring(what.indexOf(";") + 1, what.length());

        ipublicacoes = (what.substring(0, what.indexOf(";")));
        if (TextUtils.isEmpty(ipublicacoes)) ipublicacoes="0";
        what = what.substring(what.indexOf(";") + 1, what.length());

        irevisitas = (what.substring(0, what.indexOf(";")));
        if (TextUtils.isEmpty(irevisitas)) irevisitas="0";

        iestudos = what.substring(what.indexOf(";") + 1, what.length());
        if (TextUtils.isEmpty(iestudos)) iestudos="0";

        this.ano = Integer.parseInt(iano);
        this.mes = Integer.parseInt(imes);
        this.nome = snome;
        this.modalidade = smodalidade;
        this.videos = Integer.parseInt(ivideos);
        this.horas = Integer.parseInt(ihoras);
        this.publicacoes = Integer.parseInt(ipublicacoes);
        this.revisitas = Integer.parseInt(irevisitas);
        this.estudos = Integer.parseInt(iestudos);
        registros++;
    }


    public String getRidOfUTF(String str){
        char[] charArray = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : charArray){
            if (c != '\uFEFF') {
                sb.append(c);
                //L.m("UTF \uFEFF");
            }
        }
        return sb.toString();
    }

    public static int getRegistros() {
        return registros;
    }

    public static void setRegistros(int registros) {
        Relatorio.registros = registros;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(int publicacoes) {
        this.publicacoes = publicacoes;
    }

    public int getRevisitas() {
        return revisitas;
    }

    public void setRevisitas(int revisitas) {
        this.revisitas = revisitas;
    }

    public int getEstudos() {
        return estudos;
    }

    public void setEstudos(int estudos) {
        this.estudos = estudos;
    }
}

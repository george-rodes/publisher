package br.com.anagnostou.publisher;

/**
 * Created by George on 26/07/2016.
 */
public class Publicador  {
    static int ativos;
    String nome;
    String familia;
    String grupo;
    String batismo;
    String nascimento;
    String fone;
    String celular;
    String rua;
    String bairro;
    String ansepu;
    String pipu;
    String sexo;

    public Publicador() {
        ativos++;
    }

    public Publicador(String nome, String familia, String grupo, String batismo,
                      String nascimento, String fone, String celular, String rua, String bairro,
                      String ansepu, String pipu, String sexo ) {
        this.nome = nome;
        this.familia = familia;
        this.grupo = grupo;
        this.batismo = batismo;
        this.nascimento = nascimento;
        this.fone = fone;
        this.celular = celular;
        this.rua = rua;
        this.bairro = bairro;
        this.ansepu = ansepu;
        this.pipu = pipu;
        this.sexo = sexo;
    }

    public Publicador(String what){
        String snome;
        String sfamilia;
        String sgrupo;
        String sbatismo;
        String snascimento;
        String sfone;
        String scelular;
        String srua;
        String sbairro;
        String sansepu;
        String spipu;
        String ssexo;
        //'\uFEFF' 65279
        // Remove the leading BOM characters.
        //what = what.replaceAll("\\uFEFF", "");

        //utfFEFF =  what.substring(0, what.indexOf(";"));
        what = getRidOfUTF(what);

        try {
            snome = what.substring(0, what.indexOf(";"));
        } catch (Exception e) {
            //L.m("UTF problems");
            snome = "Desconhecido";
        }

        what = what.substring(what.indexOf(";") + 1, what.length());

        sfamilia = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        sgrupo = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        sbatismo = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        snascimento = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        sfone = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        scelular = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        srua = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        sbairro = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        sansepu = (what.substring(0, what.indexOf(";")));
        what = what.substring(what.indexOf(";") + 1, what.length());

        spipu = (what.substring(0, what.indexOf(";")));
        ssexo = what.substring(what.indexOf(";") + 1, what.length());

        this.nome = snome;
        this.familia = sfamilia;
        this.grupo = sgrupo;
        this.batismo = sbatismo;
        this.nascimento = snascimento;
        this.fone =sfone;
        this.celular = scelular;
        this.rua = srua;
        this.bairro = sbairro;
        this.ansepu = sansepu;
        this.pipu = spipu;
        this.sexo = ssexo;
        ativos++;
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
}


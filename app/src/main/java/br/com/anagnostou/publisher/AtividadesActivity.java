package br.com.anagnostou.publisher;

import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AtividadesActivity extends AppCompatActivity {
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    TextView tvPub1, tvPub2, tvPub3, tvPub4, tvPub5, tvPub6, tvPub7, tvPub8, tvPub9,
            tvPub10, tvPub11, tvPub12, tvPub13, tvPub14, tvPubTitle, tvPubFone, tvNomePublicador;
    String nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividades);

        Toolbar toolbar = (Toolbar) findViewById(R.id.atividades_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DBAdapter(getApplicationContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();

        tvPub1 = (TextView) findViewById(R.id.tvPub1);
        tvPub2 = (TextView) findViewById(R.id.tvPub2);
        tvPub3 = (TextView) findViewById(R.id.tvPub3);
        tvPub4 = (TextView) findViewById(R.id.tvPub4);
        tvPub5 = (TextView) findViewById(R.id.tvPub5);
        tvPub6 = (TextView) findViewById(R.id.tvPub6);
        tvPub7 = (TextView) findViewById(R.id.tvPub7);
        tvPub8 = (TextView) findViewById(R.id.tvPub8);
        tvPub9 = (TextView) findViewById(R.id.tvPub9);
        tvPub10 = (TextView) findViewById(R.id.tvPub10);
        tvPub11 = (TextView) findViewById(R.id.tvPub11);
        tvPub12 = (TextView) findViewById(R.id.tvPub12);
        tvPub13 = (TextView) findViewById(R.id.tvPub13);
        tvPub14 = (TextView) findViewById(R.id.tvPub14);
        tvPubTitle = (TextView) findViewById(R.id.tvPubTitle);
        tvPubFone = (TextView) findViewById(R.id.tvPubFone);
        //tvNomePublicador = (TextView) findViewById(R.id.tvNomePublicador);
        nome = getIntent().getExtras().getString("nome", "George");
        getSupportActionBar().setTitle(nome);
        //tvNomePublicador.setText(nome);
        achaPublicador(nome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_atividades, menu);
        /** SEARCH **/
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            /** Ihave to return where I came From **/
            Intent returnIntent = new Intent();
            //returnIntent.putExtra("busca", nameSearch);
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_relatorio) {
            Intent intent = new Intent(this,CartaoActivity.class );
            intent.putExtra("nome", nome);
            startActivity(intent);

            return true;
        }
        return true;
    }

    public void achaPublicador(String nome) {
        String idade;
        String batismo;
        Double mediahoras;
        Double mediarevisitas;
        Double mediaestudos;
        Double mediasvideos;
        Double mediapublicacoes;
        String pioneiroauxiliar;
        String irregular;

        Publicador[] pub;
        pub = dbAdapter.retrievePublisherData(nome);

        if (pub.length > 0) {
            Publicador p = pub[0];
            if (!p.nascimento.isEmpty()) {
                idade = Utilidades.calculaTempoAnos(p.nascimento) + " Anos";
            } else idade = "Não Informada";

            if (!p.batismo.isEmpty()) {
                batismo = Utilidades.calculaTempoBatismo(p.batismo);
            } else {

                if (p.sexo.equals("M")) batismo = "Não Batizado";
                else batismo = "Não Batizada";

            }

            String[] resultado = dbAdapter.somaHorasMeses(p.nome);
            mediahoras = (double) Integer.parseInt(resultado[0]) / Integer.parseInt(resultado[1]);
            mediarevisitas = Double.parseDouble(resultado[2]);
            mediaestudos = Double.parseDouble(resultado[3]);
            mediasvideos = Double.parseDouble(resultado[4]);
            mediapublicacoes = Double.parseDouble(resultado[5]);
            pioneiroauxiliar = dbAdapter.contaPioneiroAuxiliar(p.nome);
            irregular = dbAdapter.deixouDeRelatar(p.nome);

            tvPub1.setText("Familia " + p.familia);
            tvPub2.setText("Grupo " + p.grupo);
            tvPub5.setText("Idade: " + idade);
            tvPub6.setText("Tempo de Batismo: " + batismo);
            tvPubTitle.setText("Médias dos últimos " + resultado[1] + " meses ");
            tvPub7.setText("Média de " + String.format("%.1f", mediahoras) + " Horas");
            tvPub8.setText("Média de " + String.format("%.1f", mediarevisitas) + " Revisitas");
            tvPub9.setText("Média de " + String.format("%.1f", mediaestudos) + " Estudos");
            tvPub13.setText("Média de " + String.format("%.1f", mediasvideos) + " Videos");
            tvPub14.setText("Média de " + String.format("%.1f", mediapublicacoes) + " Publicações");

            //Verificar se existe data de batismo
            //
            String pio;
            if (p.sexo.equals("M")) pio = "Pioneiro";
            else pio = "Pioneira";

            if (Integer.parseInt(pioneiroauxiliar) > 1) {
                tvPub10.setText("Saiu de " + pio + " Auxiliar " + pioneiroauxiliar + " vezes");

            } else if (Integer.parseInt(pioneiroauxiliar) == 0) {
                tvPub10.setText("Não saiu de " + pio + " Auxiliar");
            } else tvPub10.setText("Saiu de " + pio + " Auxiliar " + pioneiroauxiliar + " vez");

            if (p.batismo.isEmpty()) tvPub10.setText("");

            if (p.pipu.equals("Pioneiro")) {
                tvPub10.setText(pio + " Regular");
            }

            if (Integer.parseInt(irregular) > 1) {
                tvPub11.setText("Deixou de relatar " + irregular + " meses");
            } else if (Integer.parseInt(irregular) == 0) {
                tvPub11.setText("Relatou todos os meses");
            } else tvPub11.setText("Deixou de relatar " + irregular + " mês");
            if (mediahoras.isNaN()) {
                if (p.sexo.equals("M")) tvPub11.setText("Inativo");
                else tvPub11.setText("Inativa");
            }

            tvPub3.setText(p.rua);
            tvPub4.setText("Bairro " + p.bairro);
            tvPub12.setText(p.fone);
            tvPubFone.setText(p.celular);

        } else {
            L.m("No data");
        }
    }

    public void ligarFone(View v) {
        dialPhoneNumber(tvPubFone.getText().toString());
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void returnMainActivity(View v) {
        v.setSelected(true);
        // Ihave to return where I came From
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 7214);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


}

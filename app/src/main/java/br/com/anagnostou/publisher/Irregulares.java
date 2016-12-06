package br.com.anagnostou.publisher;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Irregulares extends Fragment {
    View rootView;
    ListView listView;
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    public Irregulares() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_irregulares, container, false);
        listView = (ListView) rootView.findViewById(R.id.irregularesListView);
        dbAdapter = new DBAdapter(rootView.getContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();

        /** startCalendar.get(Calendar.YEAR));
         startCalendar.get(Calendar.MONTH));
         * Ano 31/12/2016 2016
         Mes 31/12/2016: 11
         Ano 1/1/2016 2016
         Mes 1/1/2016: 0
         * */

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(Calendar.getInstance().getTime());


        String anoini, anofim, mesini, mesfim, mesini1, mesfim1;
        int ianoini, ianofim, imesini, imesfim, imesatual;

        ianofim = calendar.get(Calendar.YEAR);
        //imesfim = ;

        /**
         * 1. CASO calculo no mesmo ano, mes atual = 7 até  12, so subtrai os meses
         * SQL relatorio.ano = ano_atual, mesfim = atual - 1, mesini = mesfim - 5
         *
         * 2. CASO mes atual 1, subtrai ano e subtrai os meses
         * SQL relatorio.ano = ano_atual - 1, mesfim = 12 , mesini = 7
         *
         * 3. CASO mes atual 2 - 6, sao
         * */

        imesatual = 6; //calendar.get(Calendar.MONTH) + 1;

        //1 Caso
        //Dezembro, 2016, calculo 6/2016 - 11/2016
        if (imesatual >= 7 && imesatual <= 12) {
            anoini = Integer.toString(calendar.get(Calendar.YEAR)); //2016
            mesfim = Integer.toString(imesatual - 1); //11
            mesini = Integer.toString(imesatual - 6); //6
            cursor = dbAdapter.irregularesJaneiroDezembro(anoini, mesini, mesfim);

        } else if (imesatual == 1) {
            anoini = Integer.toString(calendar.get(Calendar.YEAR) - 1); //2015
            mesfim = "12"; //11
            mesini = "7"; //6
            cursor = dbAdapter.irregularesJaneiroDezembro(anoini, mesini, mesfim);

        } else if (imesatual >= 2 && imesatual <= 6){
            anoini = Integer.toString(calendar.get(Calendar.YEAR) - 1); //2015
            anofim = Integer.toString(calendar.get(Calendar.YEAR)); //2016
            int delta = 6 - imesatual ;
            mesini = Integer.toString( 12 - delta) ;
            mesfim = "12"; //constant
            mesini1 = "01"; //constant
            mesfim1 = Integer.toString(imesatual - 1); //distancia ate
            cursor = dbAdapter.irregularesCruzaAno(anoini, mesini, mesfim,anofim,mesini1,mesfim1);
            L.m("imesatual: " + imesatual);
            L.m("anoini: " + anoini);
            L.m("mesini " + mesini);
            L.m("mesfim " + mesfim);
            L.m("anofim: " + anofim);
            L.m("mesini1 " + mesini1);
            L.m("mesfim1 " + mesfim1);
        }

        if (cursor.getCount() > 0) {
            CursorAdapter listAdapter = new SimpleCursorAdapter(rootView.getContext(), R.layout.row,
                    cursor, new String[]{"nome", "familia"}, new int[]{R.id.nameTextView, R.id.familyTextView}, 0);
            listView.setAdapter(listAdapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                cursor.moveToPosition(position);
                Intent intent = new Intent(view.getContext(), AtividadesActivity.class);
                intent.putExtra("nome", cursor.getString(cursor.getColumnIndex("nome")));
                startActivity(intent);
            }
        });
        return rootView;
    }
}
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

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AnoBatismo extends Fragment {
    View rootView;
    ListView listView;
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    public AnoBatismo() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ano_batismo, container, false);
        listView = (ListView) rootView.findViewById(R.id.anoBatismoListView);
        dbAdapter = new DBAdapter(rootView.getContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
       /***********/
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(Calendar.getInstance().getTime());

        String anoini, anofim, mesini;
        int  imesatual;

        imesatual = calendar.get(Calendar.MONTH) + 1;
        mesini = String.format("%1$02d", imesatual); //pre
        L.m(mesini);
        anofim = Integer.toString(calendar.get(Calendar.YEAR));
        anoini = Integer.toString(calendar.get(Calendar.YEAR)-1);

        cursor = dbAdapter.menosDeUmAnoDeBatismo(anoini,mesini,anofim);

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
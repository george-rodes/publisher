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

public class Pregadores extends Fragment {
    View rootView;
    ListView pregadoresListView;
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    public Pregadores() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pregadores, container, false);

        pregadoresListView = (ListView) rootView.findViewById(R.id.pregadoresListView);
        dbAdapter = new DBAdapter(rootView.getContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        cursor = dbAdapter.cursorPublicadorPorAnsepu("Publicador");

        if (cursor.getCount() > 0) {
            CursorAdapter listAdapter = new SimpleCursorAdapter(rootView.getContext(), R.layout.row,
                    cursor, new String[]{"nome", "familia"}, new int[]{R.id.nameTextView, R.id.familyTextView}, 0);
            pregadoresListView.setAdapter(listAdapter);
        }



        pregadoresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //L.m("view.setSelected(true); " + view.toString());
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

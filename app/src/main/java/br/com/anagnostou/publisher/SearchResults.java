package br.com.anagnostou.publisher;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SearchResults extends AppCompatActivity {
    ListView resultadosListView;
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String query = "...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        resultadosListView = (ListView) findViewById(R.id.resultadosListView);
        dbAdapter = new DBAdapter(SearchResults.this);
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_results_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**SEARCH*/
        Intent searchIntent = getIntent();
        if(searchIntent.getAction().equals(Intent.ACTION_VIEW)) {
            //L.m(searchIntent.getData().getLastPathSegment());
            cursor = dbAdapter.getOnePublicador(searchIntent.getData().getLastPathSegment());
            if (cursor.moveToNext()) {
                Intent intent = new Intent(this, AtividadesActivity.class);
                intent.putExtra("nome", cursor.getString(cursor.getColumnIndex("nome")));
                startActivity(intent);
                finish();
            }
        }

        if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
            query = searchIntent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setTitle(query);
            cursor = dbAdapter.cursorPublicadorBusca(query);
            if (cursor.getCount() > 0) {
                CursorAdapter listAdapter = new SimpleCursorAdapter(SearchResults.this, R.layout.row,
                        cursor, new String[]{"nome", "familia"}, new int[]{R.id.nameTextView, R.id.familyTextView}, 0);
                resultadosListView.setAdapter(listAdapter);
            }
            resultadosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                    cursor.moveToPosition(position);
                    Intent intent = new Intent(view.getContext(), AtividadesActivity.class);
                    intent.putExtra("nome", cursor.getString(cursor.getColumnIndex("nome")));
                    startActivity(intent);
                }
            });
        }
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
        return true;
    }
}

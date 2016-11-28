package br.com.anagnostou.publisher;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Settings extends AppCompatActivity {
    private EditText etUpdate;
    private EditText etCadastro;
    private EditText etRelatorio;
    private EditText etHomepage;
    private static final String DEFAULT = "N/A";
    private final int PERM_EXT_STORAGE = 99;
    private final int PERM_PHONE = 77;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        etCadastro = (EditText) findViewById(R.id.etCadastro);
        etRelatorio = (EditText) findViewById(R.id.etRelatorio);
        etUpdate = (EditText) findViewById(R.id.etUpdate);
        etHomepage = (EditText) findViewById(R.id.etHomepage);
        sp = getSharedPreferences("myPreferences", MODE_PRIVATE);
        editor = sp.edit();

        if (sp.contains("update") && sp.contains("cadastro")
                && sp.contains("relatorio") && sp.contains("homepage")) {
            etUpdate.setText(sp.getString("update", DEFAULT));
            etCadastro.setText(sp.getString("cadastro", DEFAULT));
            etRelatorio.setText(sp.getString("relatorio", DEFAULT));
            etHomepage.setText(sp.getString("homepage", DEFAULT));
        } else factoryReset(getCurrentFocus());


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(Settings.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Settings.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(Settings.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_EXT_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Settings.this, Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.CALL_PHONE}, PERM_PHONE);
            }
        }
    }

    public void getPreferences(View v) {
        etUpdate.setText(sp.getString("update", DEFAULT));
        etCadastro.setText(sp.getString("cadastro", DEFAULT));
        etRelatorio.setText(sp.getString("relatorio", DEFAULT));
        etHomepage.setText(sp.getString("homepage", DEFAULT));
    }

    public void setPreferences(View v) {
        if (!etUpdate.getText().toString().contentEquals("")
                && !etCadastro.getText().toString().contentEquals("")
                && !etRelatorio.getText().toString().contentEquals("")
                && !etHomepage.getText().toString().contentEquals("")) {

            editor.putString("update", etUpdate.getText().toString());
            editor.putString("cadastro", etCadastro.getText().toString());
            editor.putString("relatorio", etRelatorio.getText().toString());
            editor.putString("homepage", etHomepage.getText().toString());
            editor.commit();
        } else L.t(v.getContext(), "Campos Vazios");
    }

    public void factoryReset(View v) {
        //update.txt,
        editor.putString("update", "update.txt");
        editor.putString("cadastro", "cadastro.txt");
        editor.putString("relatorio", "relatorio.txt");
        editor.putString("homepage", "http://www.anagnostou.com.br/overseer/");
        editor.commit();
        etUpdate.setText(sp.getString("update", DEFAULT));
        etCadastro.setText(sp.getString("cadastro", DEFAULT));
        etRelatorio.setText(sp.getString("relatorio", DEFAULT));
        etHomepage.setText(sp.getString("homepage", DEFAULT));
    }

    public void returnMainActivity(View v) {
        //para forcar a inicializacao dos componentes do MainActivity
        startActivity(new Intent(this, MainActivity.class));
    }
}

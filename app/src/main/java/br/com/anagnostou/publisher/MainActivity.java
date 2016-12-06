package br.com.anagnostou.publisher;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String DATABASE_NAME ;
    private String DB_FULL_PATH;
    private String spUpdate;
    private String spCadastro;
    private String spRelatorio;
    private String spHomepage;
    SharedPreferences sp;
    private static final String NA = "N/A";
    SharedPreferences.Editor editor;
    String fosPublicador;
    String fosRelatorio;
    String fosUpdate;
    String sdcard;
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    String sDataServidor;
    Boolean bancoTemDados = false;
    ConnectivityManager connMgr;
    Boolean bBackgroundJobs = false;
    Adriano adrianoFragment;
    Centro centroFragment;
    SalaoDoReino salaoFragment;
    VilaNova vilaFragment;
    Vazio vazioFragment;
    Anciaos anciaosFragment;
    Pioneiros pioneirosFragment;
    Servos servosFragment;
    Pregadores pregadoresFragment;
    VaroesBatizados  varoesBatizadosFragment;
    String nameSearch;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SecondSectionsPagerAdapter secondSectionsPagerAdapter;
    private SpecialPagerAdapter specialPagerAdapter;
    private ViewPager mViewPager;
    BroadcastReceiver broadcastReceiver;
    private Intent checkSQLServerIntent;
    private CheckSQLService checkSQLService;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;
    private boolean mStopLoop;


    private void bindService(){
        if (serviceConnection == null){
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    CheckSQLService.MyServiceBinder myServiceBinder = (CheckSQLService.MyServiceBinder)iBinder;
                    checkSQLService = myServiceBinder.getService();
                    isServiceBound = true;
                }
                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    isServiceBound = false;
                }
            };
        }
        bindService(checkSQLServerIntent,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    private void unbindService(){
        if(isServiceBound){
            unbindService(serviceConnection);
            isServiceBound=false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /******NEW DrawerLayout *********/
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /******************/

        broadcastReceiver = new PowerConnectionReceiver();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        secondSectionsPagerAdapter = new SecondSectionsPagerAdapter(getSupportFragmentManager());
        specialPagerAdapter = new SpecialPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        getSupportActionBar().setTitle(R.string.por_grupo);
        getSupportActionBar().setSubtitle(getString(R.string.atividades_da_congregacao));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /** Shared Preferences **/
        sp = getSharedPreferences("myPreferences", MODE_PRIVATE);
        if (sp.contains("update") && sp.contains("cadastro")
                && sp.contains("relatorio") && sp.contains("homepage")) {
            spUpdate = sp.getString("update", NA);
            spCadastro = sp.getString("cadastro", NA);
            spRelatorio = sp.getString("relatorio", NA);
            spHomepage = sp.getString("homepage", NA);
            //sdcard = Environment.getExternalStorageDirectory().toString();
            //sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
            //sdcard = Environment.getExternalStorageDirectory().getPath();
            fosPublicador = getString(R.string.sdcard) + spCadastro;
            fosRelatorio = getString(R.string.sdcard) + spRelatorio;
            fosUpdate = getString(R.string.sdcard) + spUpdate;

            bBackgroundJobs = false;
            dbAdapter = new DBAdapter(getApplicationContext());
            sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
            DATABASE_NAME = dbAdapter.mydbHelper.getDatabaseName();
            DB_FULL_PATH = sqLiteDatabase.getPath();
            bBackgroundJobs = false;

            if (Utilidades.existeTabela("relatorio", MainActivity.this)
                    && Utilidades.existeTabela("publicador", MainActivity.this)
                    && Utilidades.existeTabela("versao", MainActivity.this)) {
                if (Utilidades.temDadosNoBanco(MainActivity.this)) {
                    final CheckUpdateAvailable checkUpdateAvailable = new CheckUpdateAvailable(getApplicationContext());
                    checkUpdateAvailable.execute(spHomepage + spUpdate, fosUpdate);
                    bancoTemDados = true;
                }
            } else {
                atualizarBancoDeDados(getCurrentFocus());
            }
        } else startActivity(new Intent(this, Settings.class));

        /**
       carregaPreferencias();
       checkSQLServerIntent = new Intent(this,CheckSQLService.class);
       startService(checkSQLServerIntent);
       bindService();
       */
    }

    private void carregaPreferencias() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean usarDadosCelurares = sharedPreferences.getBoolean("usarDadosCelurares", false);
        //L.t(this, "usarDadosCelurares: " + usarDadosCelurares);
        //L.t(this, sharedPreferences.getString("server", ""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        /** Testes para BROADCASTRECEIVER
        IntentFilter i = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        registerReceiver(broadcastReceiver, i);
        IntentFilter i2 = new IntentFilter("android.intent.action.ACTION_POWER_DISCONNECTED");
        registerReceiver(broadcastReceiver, i2);
         IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
         registerReceiver(broadcastReceiver,iFilter);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        /** Testes para BROADCASTRECEIVER
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            L.t(this, "Nothing Registered");
        }
        */

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** try {
            unregisterReceiver(broadcastReceiver);
            unbindService();
        } catch (Exception e) {
            L.t(this, "Nothing Registered");
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /** SEARCH **/
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /** Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml. **/
        int id = item.getItemId();
        if (id == R.id.action_updateDatabase) {
            atualizarBancoDeDados(getCurrentFocus());
            return true;
        }
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, Settings.class));
            //startActivity(new Intent(this, AppPreferences.class));
            /* if(isServiceBound){
                L.t(this,"Get Randomnumber: " + checkSQLService.getmRandomNumber());

            }*/
            return true;
        }

        if (id == R.id.action_clear) {
            copyDataBaseSdCard();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0 && bancoTemDados) {
                adrianoFragment = new Adriano();
                return adrianoFragment;
            }
            if (position == 1 && bancoTemDados) {
                salaoFragment = new SalaoDoReino();
                return salaoFragment;
            }
            if (position == 2 && bancoTemDados) {
                vilaFragment = new VilaNova();
                return vilaFragment;
            }
            if (position == 3 && bancoTemDados) {
                centroFragment = new Centro();
                return centroFragment;
            }

            if (position == 4 && bancoTemDados) {
                anciaosFragment = new Anciaos();
                return anciaosFragment;
            }
            if (position == 5 && bancoTemDados) {
                servosFragment = new Servos();
                return servosFragment;
            }

            if (position == 6 && bancoTemDados) {
                pregadoresFragment = new Pregadores();
                return pregadoresFragment;
            }

            if (position == 7 && bancoTemDados) {
                pioneirosFragment = new Pioneiros();
                return pioneirosFragment;
            }

            vazioFragment = new Vazio();
            return vazioFragment;
        }

        @Override // Show x total pages.
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ADRIANO";
                case 1:
                    return "SALÃO DO REINO";
                case 2:
                    return "VILA NOVA";
                case 3:
                    return "CENTRO";

                /** TABS SCROLLABLE *******/
                case 4:
                    return "ANCIÃOS";
                case 5:
                    return "SERVOS";
                case 6:
                    return "PUBLICADORES";
                case 7:
                    return "PIONEIROS";

            }
            return null;
        }
    }

    public class SecondSectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SecondSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0 && bancoTemDados) {
                anciaosFragment = new Anciaos();
                return anciaosFragment;
            }
            if (position == 1 && bancoTemDados) {
                servosFragment = new Servos();
                return servosFragment;
            }

            if (position == 3 && bancoTemDados) {
                pregadoresFragment = new Pregadores();
                return pregadoresFragment;
            }

            if (position == 2 && bancoTemDados) {
                pioneirosFragment = new Pioneiros();
                return pioneirosFragment;
            }
            vazioFragment = new Vazio();
            return vazioFragment;
        }

        @Override // Show x total pages.
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ANCIÃOS";
                case 1:
                    return "SERVOS";
                case 3:
                    return "PUBLICADORES";
                case 2:
                    return "PIONEIROS";
            }
            return null;
        }
    }

    public class SpecialPagerAdapter extends FragmentStatePagerAdapter {
        public SpecialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //IRREGULARES
            if (position == 0 && bancoTemDados) {
                return new Irregulares();
            }
            //VARÕES BATIZADOS
            if (position == 1 && bancoTemDados) {
                return new VaroesBatizados();
            }
            if (position == 2 && bancoTemDados) {
                return new NaoBatizados();
            }
            //NÃO BATIZADOS
            if (position == 3 && bancoTemDados) {
                return new NaoBatizados();
            }

            vazioFragment = new Vazio();
            return vazioFragment;
        }

        @Override // Show x total pages.
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "IRREGULARES";
                case 1:
                    return "VARÕES BATIZADOS";
                case 2:
                    return "MENOS DE UM ANO DE BATISMO";
                case 3:
                    return "NÃO BATIZADOS";

            }
            return null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.publicadores) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
            getSupportActionBar().setTitle(getString(R.string.por_grupo));
        } else if (id == R.id.porPrivilegio) {
            mViewPager.setAdapter(secondSectionsPagerAdapter);
            getSupportActionBar().setTitle(getString(R.string.por_privilegio));
        } else if (id == R.id.pesquisasEspeciais) {
            mViewPager.setAdapter(specialPagerAdapter);
            getSupportActionBar().setTitle(getString(R.string.pesquisas_especiais));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean atualizarBancoDeDados(View v) {
        if (Utilidades.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            /** em vez de chamar a activity, chamar várias Asynctask que uma chama a outra através do onPostExecute */
            if (!bBackgroundJobs) {
                final DownloadTaskUpdate downloadTaskUpdate = new DownloadTaskUpdate(getApplicationContext());
                downloadTaskUpdate.execute(spHomepage + spUpdate);
            } else L.t(getApplicationContext(), "Background Jobs in Progress");
            return true;
        } else {
            L.t(getApplicationContext(), "Sem Conexão com a Internet");
            return false;
        }
    }

    class DownloadTaskUpdate extends AsyncTask<String, Integer, String> {
        private Context context;
        AlertDialog.Builder builder1;
        AlertDialog alert11;

        public DownloadTaskUpdate(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            bBackgroundJobs = true;

            builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Aguarde! Baixando update.txt..");
            alert11 = builder1.create();
            alert11.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) return "No HTTP ";
                input = connection.getInputStream();
                output = new FileOutputStream(fosUpdate);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                alert11.dismiss();
            } else {
                alert11.dismiss();
                final DownloadTaskRelatorio downloadRelatorioTask = new DownloadTaskRelatorio(getApplicationContext());
                downloadRelatorioTask.execute(spHomepage + spRelatorio);
            }
        }
    }

    class DownloadTaskRelatorio extends AsyncTask<String, Integer, String> {
        private Context context;
        AlertDialog.Builder builder1;
        AlertDialog alert11;

        public DownloadTaskRelatorio(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            bBackgroundJobs = true;
            builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Aguarde! Baixando Relatorios..");
            alert11 = builder1.create();
            alert11.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) return "No HTTP ";
                input = connection.getInputStream();
                output = new FileOutputStream(fosRelatorio);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                alert11.dismiss();
            } else {
                alert11.dismiss();
                if (Utilidades.findLocalFiles(spRelatorio)) {
                    final TaskRelatorio taskRelatorio = new TaskRelatorio(MainActivity.this);
                    taskRelatorio.execute("Atualizando Relatórios");
                }
            }
        }
    }

    class TaskRelatorio extends AsyncTask<String, Integer, String> {
        private Context context;
        private final ProgressDialog progressDialog;

        public TaskRelatorio(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMax(100);
            progressDialog.setMessage("Atualizando Registros");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            if (!sqLiteDatabase.isOpen())
                sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
            dbAdapter.mydbHelper.dropTableRelatorio(sqLiteDatabase);
            if (!sqLiteDatabase.isOpen())
                sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
            dbAdapter.mydbHelper.dropTableVersao(sqLiteDatabase);
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, spRelatorio);
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            List<String> myStringList = new ArrayList<String>();
            String str;
            try {
                while ((str = in.readLine()) != null) {
                    myStringList.add(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //2. Creates an Object array of Publicadores
            Relatorio rel[] = new Relatorio[myStringList.size()];
            int counter = 0;
            for (String what : myStringList) {
                rel[counter] = new Relatorio(what);
                counter++;
            }
            //4 . Populate Database
            for (Relatorio r : rel) {
                long i = dbAdapter.insertDataRelatorio(r);
                publishProgress((int) (i * 100 / counter));
            }
            /** inserir a data da ultima atualizacao insertDataVersao(String versao)
             * ler a data do arquivo e comparar
             */
            File fileU = new File(sdcard, spUpdate);
            BufferedReader inU = null;
            try {
                inU = new BufferedReader(new InputStreamReader(new FileInputStream(fileU), "UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String strU;
            String sDataServidor = dbAdapter.selectVersao();
            try {
                while ((strU = inU.readLine()) != null) {
                    sDataServidor = strU;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inU != null) {
                try {
                    inU.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            dbAdapter.insertDataVersao(sDataServidor);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                final DownloadTaskPublicador downloadPublicadorTask = new DownloadTaskPublicador(MainActivity.this);
                downloadPublicadorTask.execute(spHomepage + spCadastro);
            }
        }
    }

    class DownloadTaskPublicador extends AsyncTask<String, Integer, String> {
        private Context context;
        AlertDialog.Builder builder1;
        AlertDialog alert11;

        public DownloadTaskPublicador(Context context) {
            this.context = context;
            builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Aguarde! Baixando Cadastro de Publicadores..");
            alert11 = builder1.create();
            alert11.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(fosPublicador);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                alert11.dismiss();
            } else {
                alert11.dismiss();
                if (Utilidades.findLocalFiles(spCadastro)) {
                    final TaskPublicador taskPublicador = new TaskPublicador(MainActivity.this);
                    taskPublicador.execute("Atualizando Tabela Publicadores");
                }
            }
        }
    }

    class TaskPublicador extends AsyncTask<String, Integer, String> {
        private Context context;
        private final ProgressDialog progressDialog;

        public TaskPublicador(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMax(100);
            progressDialog.setMessage("Atualizando Registros");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            if (!sqLiteDatabase.isOpen())
                sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
            dbAdapter.mydbHelper.dropTablePublicador(sqLiteDatabase);
            //1. Loads file from external storage /sdcard, accessible via explorer
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, spCadastro);
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            List<String> myStringList = new ArrayList<String>();
            String str;
            try {
                while ((str = in.readLine()) != null) {
                    myStringList.add(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //2. Creates an Object array of Publicadores
            Publicador pub[] = new Publicador[myStringList.size()];
            int counter = 0;
            for (String what : myStringList) {
                pub[counter] = new Publicador(what);
                counter++;
            }
            //4 . Populate Database
            for (Publicador p : pub) {
                long i = dbAdapter.insertDataPublicador(p);
                publishProgress((int) (i * 100 / counter));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            bBackgroundJobs = false;
            if (result != null) {
                progressDialog.dismiss();
            } else {
                bancoTemDados = true;
                progressDialog.dismiss();
                mViewPager.setAdapter(mSectionsPagerAdapter);

            }
        }
    }

    class CheckUpdateAvailable extends AsyncTask<String, Integer, String> {
        private Context context;

        public CheckUpdateAvailable(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(sUrl[1]);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                }
                if (connection != null) connection.disconnect();
            }
            //ler a data do arquivo e comparar
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, spUpdate);
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String str;
            try {
                while ((str = in.readLine()) != null) {
                    sDataServidor = str;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
            } else {
                if (!Utilidades.comparaData(dbAdapter.selectVersao(), sDataServidor).contentEquals("mesma data")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Atualização Disponivel!\nAtualizar Banco de Dados?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            atualizarBancoDeDados(getCurrentFocus());
                        }
                    });
                    builder1.setNegativeButton("NÂO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       //super.onActivityResult(requestCode, resultCode, data);
        /** http://stackoverflow.com/questions/22083639/calling-activity-from-fragment-then-return-to-fragment
         * returns to the calling fragment */
    }

    public void copyDataBaseSdCard() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String currentDBPath = DB_FULL_PATH;
                String backupDBPath = DATABASE_NAME;
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            L.m(e.toString());
        }
    }



}

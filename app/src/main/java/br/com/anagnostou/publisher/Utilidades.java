package br.com.anagnostou.publisher;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

public class Utilidades extends AppCompatActivity {

    public static String calculaTempoBatismo(String t) {
        //transformar string em data
        //calcular a diferenca e retorna anos
        Date first;
        Date last = Calendar.getInstance().getTime();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            first = sdf.parse(t);
            Calendar startCalendar = new GregorianCalendar();
            startCalendar.setTime(first);
            Calendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(last);
            int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            //fim alternativa

            if (diffYear < 3) {
                return diffMonth + " meses";
            } else {
                return diffYear + " anos";
            }
        } catch (ParseException ex) {
            return "Nao foi possivel calcular";
        }
    }

    public static String calculaTempoAnos(String t) {
        Date first;
        Date last = Calendar.getInstance().getTime();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            first = sdf.parse(t);
            return String.valueOf(getDiffYears(first, last));
        } catch (ParseException ex) {
            return "Nao foi possivel calcular";
        }
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static boolean isOnline(ConnectivityManager connMgr) {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String comparaData(String local, String baixada) {
        //transformar string em data
        //calcular a diferenca e retorna anos
        Date dataLocal;
        Date dataBaixada;
        Date agora = Calendar.getInstance().getTime();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            dataLocal = sdf.parse(local);
            dataBaixada = sdf.parse(baixada);

            Calendar localCalendar = new GregorianCalendar();
            localCalendar.setTime(dataLocal);

            Calendar baixadaCalendar = new GregorianCalendar();
            baixadaCalendar.setTime(dataBaixada);

            if (baixadaCalendar.get(Calendar.YEAR) - localCalendar.get(Calendar.YEAR) == 0) {
                if (baixadaCalendar.get(Calendar.MONTH) - localCalendar.get(Calendar.MONTH) == 0) {
                    if (baixadaCalendar.get(Calendar.DAY_OF_MONTH) - localCalendar.get(Calendar.DAY_OF_MONTH) == 0) {
                        return "mesma data";
                    } else {
                        return "Dias Diferentes: " + (baixadaCalendar.get(Calendar.DAY_OF_MONTH) - localCalendar.get(Calendar.DAY_OF_MONTH));
                    }
                } else {
                    return "Meses Diferentes: " + (baixadaCalendar.get(Calendar.MONTH) - localCalendar.get(Calendar.MONTH));
                }
            } else {
                return "Anos Diferentes: " + (baixadaCalendar.get(Calendar.YEAR) - localCalendar.get(Calendar.YEAR));
            }

        } catch (ParseException ex) {
            return "Nao foi possivel calcular";
        }
    }

    public static boolean existeTabela(String tabela, Context context) {
        DBAdapter dbAdapter = new DBAdapter(context);
        SQLiteDatabase sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tabela + "' ", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public static boolean temDadosNoBanco(Context context) {
        DBAdapter dbAdapter;
        dbAdapter = new DBAdapter(context);

        if (dbAdapter.findFirstRelatorio().contentEquals("No Records")) {
            //L.m("No Records in RELATORIOS");
            return false;
        } else if (dbAdapter.findFirstPublicador().contentEquals("No Records")) {
            //L.m("No Records in PUBLICADORES");
            return false;
        } else {
            return true;
        }
    }

    public static boolean findLocalFiles(String name) {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, name);
        if (file.exists()) return true;
        else return false;
    }
}

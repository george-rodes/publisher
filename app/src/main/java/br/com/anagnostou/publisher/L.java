package br.com.anagnostou.publisher;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by George on 31/05/2016.
 */
public class L {
    public static void m(String message)
    {
        Log.d("HIELSEN",message);
    }
    public static void t(Context context, String message)
    {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }
}
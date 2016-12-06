package br.com.anagnostou.publisher;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Random;

public class CheckSQLService extends Service {
    private int mRandomNumber;
    private boolean isRandomGeneratorOn;

    class MyServiceBinder extends Binder {
        CheckSQLService getService() {
            return CheckSQLService.this;
        }
    }

    private IBinder mBinder = new MyServiceBinder();

    /*public CheckSQLService() {
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        L.m("onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRandomGeneratorOn = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRandomnumberGenerator();
                L.m("Random Generator Started");
            }
        }).start();


        return START_NOT_STICKY; //START_STICKY
    }

    private void startRandomnumberGenerator() {
        while (isRandomGeneratorOn) {

            try {
                Thread.sleep(10000);
                int MIN = 0;
                int MAX = 100;
                mRandomNumber = new Random().nextInt(MAX) + MIN;
                /**if (Utilidades.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    L.m("Online");
                } else L.m("Offline");
                 */
                L.m("Service Started on Thread id: " + Thread.currentThread().getName());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    public int getmRandomNumber() {
        return mRandomNumber;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        L.m("Unbinding");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRandomGeneratorOn = false;
        L.m("Destroying Service");
    }
}

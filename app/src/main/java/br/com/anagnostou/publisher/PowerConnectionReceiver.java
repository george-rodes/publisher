package br.com.anagnostou.publisher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by George on 28/11/2016.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /** int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

         L.t(context, "isCharging: " + isCharging);
         L.t(context, "usbCharge: " + usbCharge);
         L.t(context, "acCharge: " + acCharge);

         */

        String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            L.t(context, "ACTION_POWER_CONNECTED");
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            L.t(context, "ACTION_POWER_DISCONNECTED");
        }



    }
}

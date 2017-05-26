package rpk.organizer.actionbar.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

/**
 * Created by PL on 2017-05-26.
 */

public class GPSCheck extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //gps on
            Toast.makeText(context, "ON", Toast.LENGTH_LONG).show();
        }else {
            //gps off
            Toast.makeText(context, "OFF", Toast.LENGTH_LONG).show();
            BlockClickFlag.setFlagTrue();
        }
    }
}

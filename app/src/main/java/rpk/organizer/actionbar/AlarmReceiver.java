package rpk.organizer.actionbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    static Ringtone ringtone;
    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "Alarm! i od razu stop!", Toast.LENGTH_LONG).show();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();
        //TODO tutaj jaki popup i jak klinie to stop!
        ringtone.stop();
    }

}
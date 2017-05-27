package rpk.organizer.actionbar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import rpk.organizer.actionbar.Utils.EventList;

public class AlarmReceiver extends BroadcastReceiver {
    static public Ringtone ringtone;
    @Override
    public void onReceive(final Context context, Intent intent) {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        ringtone = RingtoneManager.getRingtone(context, uri);

        v.vibrate(750);
        ringtone.play();

        Intent intent2 = new Intent(context, AlarmActivity.class);
        intent2.putExtra("NotiClick",true);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle(context.getString(R.string.NotificationTitle))
                        .setContentText(EventList.getSummary())
                        .setContentIntent(pIntent);

        int mNotificationId = 1;

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}
package rpk.organizer.actionbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;


public class AlarmActivity extends Fragment {
    Context mContext;
    AlarmManager mAlarmManager;
    PendingIntent mAlarmIntent;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_alarm,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity().getApplicationContext();

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(mContext,234324243,intent,0);

        mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);


        // Pobieramy pierwsze zdarzenie z brzegu (oczywiście nie te które już się odbyły)
        // Sprwdzamy jak tam dojechać w chwili aktualnej.(pobieramy czas dojazdu)
        // ustawiamy powiadomienie kilka minut przed czasem wyjścia

        // !! jeżeli czas wystarczający to ok jak nie to informujemy o braku czasu !!

        // Czas docelowy - Czas jazdy - czas na wyjście
        int ZaIleSecAlarm = 5;

        mAlarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(ZaIleSecAlarm*1000),mAlarmIntent);

//        mAlarmManager.cancel(mAlarmIntent);

        Toast.makeText(mContext, "Alarm Start! za "+ ZaIleSecAlarm, Toast.LENGTH_LONG).show();

        //TODO aby anulować alarm, ! nie może być uruchomiony !
//        mAlarmManager.cancel(mAlarmIntent);
    }

}

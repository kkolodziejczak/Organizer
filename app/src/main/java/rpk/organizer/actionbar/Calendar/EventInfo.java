package rpk.organizer.actionbar.Calendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.model.Event;

import java.util.Arrays;
import java.util.HashMap;

import rpk.organizer.actionbar.R;

public class EventInfo extends Fragment {

    private Context mContext;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragment fragment = getFragmentManager().findFragmentByTag("yourstringtag");
        return inflater.inflate(R.layout.activity_event_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();

//        ActivityMainBinding binding = DataBindingUtil.setContentView(this, LAYOUT);
//        binding.setInfoAboutEvent(Calendar.EventToDisplay;);
        mContext = getContext();
    }

}
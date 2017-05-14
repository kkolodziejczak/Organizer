package rpk.organizer.actionbar.Calendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rpk.organizer.actionbar.R;
import rpk.organizer.actionbar.databinding.ActivityEventInfoBinding;

public class EventInfo extends Fragment {

    private Context mContext;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityEventInfoBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.activity_event_info, container, false);

        binding.setInfoAboutEvent(Calendar.EventToDisplay);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        mContext = getContext();
    }

}
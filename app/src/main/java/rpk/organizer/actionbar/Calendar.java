package rpk.organizer.actionbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import rpk.organizer.actionbar.Utils.EventList;

//Fragment
public class Calendar extends Fragment {
    private ListView EventListView;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        mContext = getContext();
        EventListView = (ListView)getActivity().findViewById(R.id.EventList);
        populateList();
    }

    private void populateList(){
        EventAdapter adapter = new EventAdapter(mContext, EventList.getEvents());
        EventListView.setAdapter(adapter);
    }
}

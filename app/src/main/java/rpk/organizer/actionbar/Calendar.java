package rpk.organizer.actionbar;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ListView;

import rpk.organizer.actionbar.Utils.EventList;

//Fragment
public class Calendar extends AppCompatActivity {
    private ListView EventListView;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mContext = this;
        EventListView = (ListView)findViewById(R.id.EventList);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        toolbar.setTitle("LUL");

        populateList();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void populateList(){
        EventAdapter adapter = new EventAdapter(mContext, EventList.getEvents());
        EventListView.setAdapter(adapter);
    }
}

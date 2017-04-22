package rpk.organizer.actionbar;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class Calendar extends AppCompatActivity {
    private List<EventInfo> ListaEventow;
    private ListView EventListView;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mContext = this;
        ListaEventow = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        toolbar.setTitle("LUL");

        populateList();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void populateList(){
        ListaEventow.add(new EventInfo("Ktos", "Meeting", "Kojama","8:00"));
        ListaEventow.add(new EventInfo("Ktos2", "Meeting23", "Kojama","8:15"));
        ListaEventow.add(new EventInfo("Ktos3", "Meeting123", "Kojama","8:45"));
        ListaEventow.add(new EventInfo("Ktos4", "Meeting1234", "Kojama","8:49"));

        EventListView = (ListView)findViewById(R.id.EventList);

        EventAdapter adapter = new EventAdapter(mContext, ListaEventow);

        EventListView.setAdapter(adapter);
    }
}

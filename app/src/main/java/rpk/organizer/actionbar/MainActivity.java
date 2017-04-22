package rpk.organizer.actionbar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import rpk.organizer.actionbar.Utils.EventList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        toolbar.setTitle("LUL");
        LoadDataToClasses();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent mIntent = null;

        switch(item.getItemId()) {
            case R.id.mapAction:
                mIntent = new Intent(this, ShortestWayActivity.class);
                break;
            case R.id.calendarAction:
                mIntent = new Intent(this, DayView.class);
                break;
            case R.id.myPlaceAction:

                break;
        }
        if (mIntent != null) {
            startActivity(mIntent);
            return true;
        }
        return false;
    }

    public void mapStartClick(View view) {
        Intent intent = new Intent(this, ShortestWayActivity.class);
        startActivity(intent);
    }

    public void calendarStartClick(View view) {
        Intent intent = new Intent(this, Calendar.class);
        startActivity(intent);
    }

    public void myplacesStartClick(View view) {

    }

    public void LoadDataToClasses(){
        for(int i =0;i<50;i++)
            EventList.addEvent(new EventInfo("Ktos","Meeting","Kojama"+i,"8:15"));
    }
}

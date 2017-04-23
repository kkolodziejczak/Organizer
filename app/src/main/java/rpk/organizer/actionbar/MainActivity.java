package rpk.organizer.actionbar;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment frag=null;
        Class fragmentClass;
        fragmentClass = Default.class;
        try {
            frag = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.frame,frag ).commit();
        LoadDataToClasses();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment frag=null;
        Class fragmentClass;
        switch(item.getItemId()) {
            case R.id.mapAction:
                fragmentClass = ShortestPathActivity.class;
                break;
            case R.id.calendarAction:
                fragmentClass = Calendar.class;
                break;
            case R.id.myPlaceAction:
                fragmentClass = Default.class;
                break;
            default:
                fragmentClass = Default.class;
        }
        try {
            frag = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, frag).commit();
        return false;
    }

    public void LoadDataToClasses(){
        for(int i =0;i<15;i++)
            EventList.addEvent(new EventInfo("Ktos","Meeting","Kojama"+i,"8:15"));
    }
}

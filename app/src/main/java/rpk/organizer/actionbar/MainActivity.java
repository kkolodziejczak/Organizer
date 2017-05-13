package rpk.organizer.actionbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Random;

import rpk.organizer.actionbar.Calendar.Calendar;
import rpk.organizer.actionbar.MyPlaces.Place;
import rpk.organizer.actionbar.Utils.PlacesHandler;

public class MainActivity extends AppCompatActivity {

    static public Class selectedFragmentClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JodaTimeAndroid.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        toolbar.setTitle("MainBar");

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment frag = null;
        Class fragmentClass;
        fragmentClass = Default.class;
        try {
            frag = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectedFragmentClass = fragmentClass;
        // nie odkladamy domyslnego fragmentu (menu) na stos fragmentow
        fragmentManager.beginTransaction().replace(R.id.frame, frag).commit();
//        LoadDataToClasses();
        RandomPlacesGenerate();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment frag = null;
        String tag = "MENU";
        Class fragmentClass;
        switch (item.getItemId()) {
            case R.id.mapAction:
                fragmentClass = ShortestPathActivity.class;
                tag = "SHORTEST_PATH";
                break;
            case R.id.calendarAction:
                fragmentClass = Calendar.class;
                tag = "CALENDAR";
                break;
            case R.id.myPlaceAction:
                fragmentClass = MyPlacesActivity.class;
                tag = "MY_PLACES";
                break;
            default:
                fragmentClass = Default.class;
                tag = "MENU";
        }
        try {
            frag = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // bedac np. na mapie nie mozemy przeladowac fragmentu map itd.
        if (selectedFragmentClass != fragmentClass) {
            getSupportFragmentManager().popBackStack();
            // jezeli jestesmy w menu to nie dodajemy do fragmentu menu, aby uniknac 2-krotnego
            // naciskania przycisku Back w celu opuszczenia aplikacji
            if (fragmentClass != Default.class) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, frag).addToBackStack(tag).commit();
            }
        }
        // po kazdym wyborze z ActionBar zapisz informacje o klasie fragmentu
        selectedFragmentClass = fragmentClass;
        return false;
    }

//    public void LoadDataToClasses() {
//        for (int i = 0; i < 15; i++)
//            EventList.addEvent(new EventInfo("Ktos", "Meeting", "Kojama" + i, "8:15"));
//    }
    public void RandomPlacesGenerate(){
                Random rnd = new Random();
                int limit=rnd.nextInt(10);
                for(int i=0;i<limit;++i){
                    PlacesHandler.addPlace(new Place(String.format("Miejsce %d",PlacesHandler.getIter()),"0:00"));
                    PlacesHandler.IterIncrement();
                }
    }

    // moze sie przydac. identyfikacja odbywa sie po tagach, ktore przypisywane sa do fragmentow w onOptionsItemSelected
    private Fragment getCurrentFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        return fragmentManager.findFragmentByTag(fragmentTag);
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
}

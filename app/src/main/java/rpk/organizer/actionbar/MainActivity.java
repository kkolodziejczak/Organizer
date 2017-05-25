package rpk.organizer.actionbar;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rpk.organizer.actionbar.Calendar.Calendar;
import rpk.organizer.actionbar.Calendar.EventsInfo;
import rpk.organizer.actionbar.MyPlaces.PlacesAdapter;
import rpk.organizer.actionbar.Utils.BazaDanych;
import rpk.organizer.actionbar.Utils.PlacesHandler;

public class MainActivity extends AppCompatActivity {

    static public Class selectedFragmentClass = null;
    static public FragmentManager fragmentManager;
    private int IsListCreated = 0;
    private int TimeBetweenCalls = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PlacesAdapter adapter = new PlacesAdapter(PlacesHandler.getPlaces(), this);
        PlacesHandler.setAdapter(adapter);
        JodaTimeAndroid.init(this);

        BazaDanych db = new BazaDanych(this);
        PlacesHandler.db = db;

        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
//                Toast.makeText(getApplicationContext(),
//                        "Minute:" + java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE),
//                        Toast.LENGTH_SHORT zmiana
//                ).show();
//
                List<EventsInfo> list = Calendar.getTodaysEventList();
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule(hourlyTask, 1, 1000 * 15);   // 1000*10*60 every 10 minut

//            TimeBetweenCalls * 60 * 1000;
        if (AlarmReceiver.ringtone != null) {
            AlarmReceiver.ringtone.stop();
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.cancelAll();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        toolbar.setTitle("MainBar");
        if (IsListCreated == 0) {
            RandomPlacesGenerate();
            IsListCreated = 1;
        }


        fragmentManager = getSupportFragmentManager();

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

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(theBroadcastReceiver);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        IntentFilter aIntentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
//        registerReceiver(theBroadcastReceiver, aIntentFilter);
//    }
//
//    protected BroadcastReceiver theBroadcastReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (null == intent) {
//                return;
//            }
//            String astrAction = intent.getAction();
//            if (Intent.ACTION_TIME_TICK.equals(astrAction)) {
//                Toast.makeText(getApplicationContext(),
//                        "Minute:" + java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE),
//                        Toast.LENGTH_SHORT
//                ).show();
//            }
//        }
//
//    };

    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        // jezeli nie mamy zadnych fragmentow odlozonych na stosie, oznacza to ze jestesmy w menu
        // menu (poczatkowy ekran jak wlacza sie aplikacja) nie jest odkladane na stos fragmentow
        if (fragmentManager.getBackStackEntryCount() == 0) {
            selectedFragmentClass = null;
        } else {    // jezeli jednak cos jest na stosie i kliknelismy na ktorakolwiek z opcji w actionbar
            // to nalezy usunac wszystkie fragmenty ze stosu i dodac nowy
        }
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
            hideKeyboard(this); // jezeli na poprzednim fragmencie byla wlaczona klawiatura, to ja chowamy
            if (fragmentManager.getBackStackEntryCount() > 0) {
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                    fragmentManager.popBackStack();
                }
            }
            //getSupportFragmentManager().popBackStack();
            // jezeli jestesmy w menu to nie dodajemy do fragmentu menu, aby uniknac 2-krotnego
            // naciskania przycisku back w celu opuszczenia aplikacji
            if (fragmentClass != Default.class) {
                fragmentManager.beginTransaction().replace(R.id.frame, frag).addToBackStack(tag).commit();
            }
        }
        // po kazdym wyborze z ActionBar zapisz informacje o klasie fragmentu
        selectedFragmentClass = fragmentClass;
        return false;
    }

    public static void AddNewFragmentOnTop(Class o, String tag) {
        Fragment frag = null;
        Class fragmentClass = o;
        try {
            frag = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (selectedFragmentClass != fragmentClass && fragmentClass != Default.class)
            fragmentManager.beginTransaction().replace(R.id.frame, frag).addToBackStack(tag).commit();

        selectedFragmentClass = fragmentClass;
    }

    public static void AddNewFragmentOnTop(Class o, String tag, String place) {
        Fragment frag = null;
        Bundle args = new Bundle();
        args.putString("PLACE", place);
        Class fragmentClass = o;
        try {
            frag = (Fragment) fragmentClass.newInstance();
            frag.setArguments(args);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (selectedFragmentClass != fragmentClass && fragmentClass != Default.class)
            fragmentManager.beginTransaction().replace(R.id.frame, frag).addToBackStack(tag).commit();

        selectedFragmentClass = fragmentClass;
    }

    public void RandomPlacesGenerate() {
        //Random rnd = new Random();
        //int limit = rnd.nextInt(10);
        // for (int i = 0; i < limit; ++i) {
        //    PlacesHandler.addPlace(new Place(String.format("Miejsce %d", PlacesHandler.getIter()), "0:00"));
        //    PlacesHandler.IterIncrement();
        // }
        PlacesHandler.db.loadAllPlacess();
    }

    // moze sie przydac. identyfikacja odbywa sie po tagach, ktore przypisywane sa do fragmentow w onOptionsItemSelected
    private Fragment getCurrentFragment() {
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
            if (count >= 2)
                selectedFragmentClass = fragmentManager.getBackStackEntryAt(count - 2).getClass();
            else
                selectedFragmentClass = fragmentManager.getBackStackEntryAt(0).getClass();
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

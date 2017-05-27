package rpk.organizer.actionbar.Calendar;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rpk.organizer.actionbar.AlarmReceiver;
import rpk.organizer.actionbar.MainActivity;
import rpk.organizer.actionbar.R;
import rpk.organizer.actionbar.ShortestPath.DirectionFinder;
import rpk.organizer.actionbar.ShortestPath.DirectionFinderListener;
import rpk.organizer.actionbar.ShortestPath.LocationAssistant;
import rpk.organizer.actionbar.ShortestPath.Route;
import rpk.organizer.actionbar.Utils.BlockClickFlag;
import rpk.organizer.actionbar.Utils.DataUtils;
import rpk.organizer.actionbar.Utils.EventList;

import static android.app.Activity.RESULT_OK;

enum Task {
    GetFirstEvents,
    GetEvents,
    GetCalendars
}

//Fragment
public class Calendar extends Fragment
        implements EasyPermissions.PermissionCallbacks, LocationAssistant.Listener, DirectionFinderListener {

    private LocationAssistant assistant;
    private ProgressDialog progressDialog;
    static public GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    public static Event EventToDisplay = null;
    public static String SelectedCalendar = null;
    public static int SelectedCalendarPosition = -1;


    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    private static ListView EventListView;
    private static Context mContext;
    private CalendarView mCalendar;
    private static DateTime timeToGet;
    private static Spinner mSpinner;
    public static HashMap<String, String> CalendarNames;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_calendar, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        assistant.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        assistant.stop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assistant = new LocationAssistant(getActivity(), this, LocationAssistant.Accuracy.HIGH, 5000, false);
        assistant.setVerbose(true);
        Bundle bundle = getArguments();
        mContext = getContext();
        EventListView = (ListView) getActivity().findViewById(R.id.EventList);
        mCalendar = (CalendarView) getActivity().findViewById(R.id.Calendar);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            mCalendar.setFirstDayOfWeek(java.util.Calendar.SUNDAY);
        }
        mSpinner = (Spinner) getActivity().findViewById(R.id.CalendarNames);

        CalendarNames = new HashMap<String, String>();

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                timeToGet = new DateTime(year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth) + "T00:00:00.000Z");
//                Toast.makeText(getContext().getApplicationContext(), year+"-"+String.format("%02d", month+1)+"-"+String.format("%02d", dayOfMonth)+"T00:00:00.000Z", Toast.LENGTH_SHORT).show();
                SelectedCalendar = (String) mSpinner.getSelectedItem();
                SelectedCalendarPosition = mSpinner.getSelectedItemPosition();
                getResultsFromApi(Task.GetEvents);
            }
        });

        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi(Task.GetCalendars);
        BlockClickFlag.setFlagTrue();
    }


    private void sendRequest(String dest, List<EventsInfo> list) {
        if (dest == null)
            return;
        Location location = assistant.getBestLocation();
        String origin = getCompleteAddressString(location.getLatitude(), location.getLongitude()); // pobiera aktualną pozycję
        String destination = dest; // Koniec
        try {
            new DirectionFinder(this, origin, destination, list).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static List<EventsInfo> getTodaysEventList() {
        if (MainActivity.EventsInfoList == null)
            return null;
        return MainActivity.EventsInfoList;
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi(Task task) {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!MainActivity.isNetworkConnected(mContext)) {
            Toast.makeText(mContext, "No network connection available.", Toast.LENGTH_SHORT).show();
        } else {
            new MakeRequestTask(mCredential).execute(task);
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                getContext(), Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getActivity().getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi(Task.GetCalendars);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi(Task.GetCalendars);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi(Task.GetCalendars);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi(Task.GetCalendars);
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Task, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;
        private Task task;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Task... params) {
            if (params.length > 0)
                try {
                    this.task = params[0];
                    switch (params[0]) {
                        case GetFirstEvents:
                            return getDataFromApi();
                        case GetEvents:
                            return getDataFromApi();
                        case GetCalendars:
                            return getCalendarListFromApi();
                        default:
                            return null;
                    }
                } catch (Exception e) {
                    mLastError = e;
                    cancel(true);
                }
            return null;
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            if (timeToGet == null)
                timeToGet = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();

            org.joda.time.DateTime dateTime = new org.joda.time.DateTime(timeToGet.toString());
            Events events = mService.events().list(CalendarNames.get(mSpinner.getSelectedItem()))
                    .setTimeMin(timeToGet)
                    .setTimeMax(new DateTime(dateTime.plusDays(1).toString()))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            EventList.Clear();
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null)
                    start = event.getStart().getDate();

                EventList.addEvent(new EventsInfo(event.getSummary(), DataUtils.toHourMin(start, ":"), event.getLocation()));
                EventList.addGoogleEvent(event);
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), DataUtils.toHourMin(start, ":")));
            }
            MainActivity.EventsInfoList = EventList.getEvents();
            return eventStrings;
        }

        private List<String> getCalendarListFromApi() {
            List<String> CalendarStrings = new ArrayList<String>();

            String pageToken = null;
            try {
                do {
                    CalendarList calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
                    List<CalendarListEntry> items = calendarList.getItems();
                    for (CalendarListEntry calendarListEntry : items) {
                        CalendarStrings.add(calendarListEntry.getSummary());
                        CalendarNames.put(calendarListEntry.getSummary(), calendarListEntry.getId());
                    }
                    pageToken = calendarList.getNextPageToken();
                } while (pageToken != null);
            } catch (Exception e) {

            }

            return CalendarStrings;
        }

        @Override
        protected void onPreExecute() {
//            mProgress.show();
            EventList.Clear();
        }

        @Override
        protected void onPostExecute(List<String> output) {
//            mProgress.hide();
            if (output == null || output.size() == 0) {
                EventAdapter adapter = new EventAdapter(mContext, new ArrayList<EventsInfo>());
                EventListView.setAdapter(adapter);
            } else {
                switch (task) {
                    case GetFirstEvents:
                        MainActivity.EventsInfoList = EventList.getEvents();
                        EventList.setID(0);
                        sendRequest(MainActivity.EventsInfoList.get(0).getPlace(), MainActivity.EventsInfoList);

                        getResultsFromApi(Task.GetEvents);
                        break;
                    case GetEvents:
                        EventAdapter adapter = new EventAdapter(mContext, EventList.getEvents());
                        int l = EventList.Count();
                        EventListView.setAdapter(adapter);
                        EventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                EventToDisplay = EventList.getEventAt(position);
                                MainActivity.AddNewFragmentOnTop(EventInfo.class, "EVENTINFO");
                            }
                        });
                        break;
                    case GetCalendars:
                        ArrayAdapter<String> gameKindArray = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, output);
                        gameKindArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinner.setAdapter(gameKindArray);

                        if (SelectedCalendar != null && SelectedCalendarPosition != -1)
                            mSpinner.setSelection(SelectedCalendarPosition);

                        getResultsFromApi(Task.GetFirstEvents);
                        break;
                }
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    @Override
    public void onNeedLocationPermission() {
        assistant.requestAndPossiblyExplainLocationPermission();
    }

    @Override
    public void onExplainLocationPermission() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.permissionExplanation)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        assistant.requestLocationPermission();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView,
                                                        DialogInterface.OnClickListener fromDialog) {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.permissionPermanentlyDeclined)
                .setPositiveButton(R.string.ok, fromDialog)
                .show();
    }

    @Override
    public void onNeedLocationSettingsChange() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.switchOnLocationShort)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        assistant.changeLocationSettings();
                    }
                })
                .show();
    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.switchOnLocationLong)
                .setPositiveButton(R.string.ok, fromDialog)
                .show();
    }

    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strAdd;
    }

    @Override
    public void onNewLocationAvailable(Location location) {
        if (location == null) return;

        if (assistant.getBestLocation() != null && MainActivity.isNetworkConnected(mContext)) {
            String geolocation = getCompleteAddressString(location.getLatitude(), location.getLongitude());

            LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());

        } else {
            Toast.makeText(mContext, R.string.NoConnectionAVB, Toast.LENGTH_SHORT).show();
        }
        BlockClickFlag.setFlagTrue();
    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
//        tvLocation.setText(getString(R.string.mockLocationMessage));
//        tvLocation.setOnClickListener(fromView);
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {
//        tvLocation.setText(getString(R.string.error));
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(mContext, "Please wait.",
                "Finding direction..!", true);
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes, List<EventsInfo> list) {
        if (MainActivity.mAlarmIntent != null){
            progressDialog.dismiss();
            return;
        }
        long j =0;
        for (int i = 0; i < 10000000; i++)
            j = j+j-j;
        progressDialog.dismiss();

        if (routes.size() == 0 || list.size() == 0)
            return;

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        MainActivity.mAlarmIntent = PendingIntent.getBroadcast(mContext, 234324243, intent, 0);
        AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        // Pobieramy pierwsze zdarzenie z brzegu (oczywiście nie te które już się odbyły)
        List<Integer> time = DataUtils.toIntList(list.get(0));

        // Sprwdzamy jak tam dojechać w chwili aktualnej.(pobieramy czas dojazdu)
        int duration = routes.get(0).duration.value;

        long millisInDay = 60 * 60 * 24 * 1000;
        long dateOnly = (new Date().getTime() / millisInDay) * millisInDay;

        // data aktualna (bez czasu), TRZEBA POTEM ZMIENIC ZEBY BRALO Z EVENTU, w formacie unix
        long clearDateUnix = new Date(dateOnly).getTime() / 1000;

        // czas interesujacego nas eventu
        long eventTimeUnix = clearDateUnix + time.get(0) * 3600 + time.get(1) * 60;


        // ustawiamy powiadomienie kilka minut przed czasem wyjscia
        long currentTimeUnix = System.currentTimeMillis() / 1000; // w sekundach

        // czas bedacy "zapasem" jaki mamy dodatkowo, poza uwzglednionym czasem podrozy
        int bufferTime = 900;

        long diffTime = eventTimeUnix - currentTimeUnix - duration - bufferTime;


        Log.d("AktualnyCzas", String.valueOf(currentTimeUnix));

        // !! jeżeli czas wystarczający to ok jak nie to informujemy o braku czasu !!
        if (diffTime < 0)
            Toast.makeText(mContext, R.string.TooLateNoti, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mContext, R.string.AddedNoti, Toast.LENGTH_SHORT).show();

        long whenToNotify = currentTimeUnix + diffTime;

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, whenToNotify, MainActivity.mAlarmIntent);
    }

}

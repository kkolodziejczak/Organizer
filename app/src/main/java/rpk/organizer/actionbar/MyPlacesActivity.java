package rpk.organizer.actionbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rpk.organizer.actionbar.MyPlaces.Place;
import rpk.organizer.actionbar.MyPlaces.PlacesAdapter;
import rpk.organizer.actionbar.ShortestPath.DirectionFinderListener;
import rpk.organizer.actionbar.ShortestPath.LocationAssistant;
import rpk.organizer.actionbar.ShortestPath.Route;
import rpk.organizer.actionbar.Utils.BlockClickFlag;
import rpk.organizer.actionbar.Utils.PlacesHandler;


public class MyPlacesActivity extends Fragment
        implements AdapterView.OnItemClickListener, LocationAssistant.Listener, DirectionFinderListener {

    private LocationAssistant assistant;
    private ListView PlacesListView;
    private Context mContext;
    private FloatingActionButton fab;
    private PlacesAdapter adapter;
    private ProgressDialog progressDialog;
    private int IsClickedFlag = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_places,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assistant = new LocationAssistant(getActivity(), this, LocationAssistant.Accuracy.HIGH, 5000, false);
        assistant.setVerbose(true);
        Bundle bundle = getArguments();
        mContext = getContext();
        adapter = new PlacesAdapter(PlacesHandler.getPlaces(), mContext);
        PlacesHandler.setAdapter(adapter);
        PlacesListView = (ListView) getActivity().findViewById(R.id.lista);
        PlacesListView.post(new Runnable() {
            @Override
            public void run() {
                PlacesListView.requestLayout();
            }
        });
        PlacesListView.setAdapter(adapter);
        PlacesListView.setOnItemClickListener(this);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fapPlace);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_addplace);
                // Custom Android Allert Dialog Title
                dialog.setTitle(R.string.dialog_title);

                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);
                // Click cancel to dismiss android custom dialog box
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Your android custom dialog ok action
                // Action for custom dialog ok button click
                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edit = (EditText) dialog.findViewById(R.id.placeName);
                        EditText edit2 = (EditText) dialog.findViewById(R.id.etDestination);
                        String czas = "0:00";
                        Place place;
                        if (edit.getText().toString().isEmpty()) {
                            if (edit2.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Place can not be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                if (!PlacesHandler.isAlreadyAdded(edit2.getText().toString())) {
                                    place = new Place(edit2.getText().toString(), edit2.getText().toString(), czas);
                                    PlacesHandler.addPlace(place);
                                    adapter.notifyDataSetChanged();
                                    PlacesHandler.db.dodaj(place);
                                } else {
                                    Toast.makeText(getContext(), "Place already exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (edit2.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Place destination can not be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                if (PlacesHandler.isAlreadyAdded(edit.getText().toString())) {
                                    place = new Place(edit.getText().toString(), edit2.getText().toString(), czas);
                                    PlacesHandler.addPlace(place);
                                    adapter.notifyDataSetChanged();
                                    PlacesHandler.db.dodaj(place);
                                } else {
                                    Toast.makeText(getContext(), "Place already exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        BlockClickFlag.setFlagTrue();
        populate();

    }

    @Override
    public void onStart() {
        super.onStart();
        IsClickedFlag = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        assistant.start();
        IsClickedFlag = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        assistant.stop();
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (IsClickedFlag == 0) {

            String place = PlacesHandler.getPlace((int) id).getPosition();
            //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            MainActivity.AddNewFragmentOnTop(ShortestPathActivity.class, "SHORTEST_PATH", place);
//            final FragmentTransaction ft = getFragmentManager().beginTransaction();
//            Class frag = ShortestPathActivity.class;
//            Fragment fragment;
//            try {
//
//                fragment = (Fragment) frag.newInstance();
//                Bundle args = new Bundle();
//                args.putString("PLACE", place);
//                fragment.setArguments(args);
//                ft.replace(R.id.activity_main, fragment, "SHORTEST_PATH");
//                ft.addToBackStack(null);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.commit();
//            } catch (java.lang.InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
        }
        IsClickedFlag = 1;
        //Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        // Then you start a new Activity via Intent
        //Intent intent = new Intent();
        //intent.setClass(this, ListItemDetail.class);
        //intent.putExtra("position", position);
        // Or / And
        //intent.putExtra("id", id);
        //startActivity(intent);
    }

    public void populate() {

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
            Toast.makeText(mContext,"No network connection available.",Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (routes.size() == 0)
            return;
        String duration = routes.get(0).duration.text;
        String distance = routes.get(0).distance.text;
    }
}

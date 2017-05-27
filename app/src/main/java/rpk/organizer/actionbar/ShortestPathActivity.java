// https://github.com/klaasnotfound/LocationAssistant
/*
 *    Copyright 2017 Klaas Klasing (klaas [at] klaasnotfound.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package rpk.organizer.actionbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rpk.organizer.actionbar.Calendar.EventsInfo;
import rpk.organizer.actionbar.MyPlaces.Place;
import rpk.organizer.actionbar.MyPlaces.PlacesAdapter;
import rpk.organizer.actionbar.ShortestPath.DirectionFinder;
import rpk.organizer.actionbar.ShortestPath.DirectionFinderListener;
import rpk.organizer.actionbar.ShortestPath.LocationAssistant;
import rpk.organizer.actionbar.ShortestPath.PlaceArrayAdapter;
import rpk.organizer.actionbar.ShortestPath.Route;
import rpk.organizer.actionbar.Utils.BlockClickFlag;
import rpk.organizer.actionbar.Utils.PlacesHandler;

public class ShortestPathActivity extends Fragment
        implements LocationAssistant.Listener, DirectionFinderListener,
        OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private TextView tvLocation;
    private TextView tvLocationAdress;
    private LocationAssistant assistant;
    private SupportMapFragment map;
    private GoogleMap mMap;

    private Marker myPositionMarker;
    private Marker myNewMarker;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;
    boolean isPathExisting = false;

    private static Context mContext;
    private static int GOOGLE_API_CLIENT_ID = 0;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final String LOG_TAG = "ShortestPathActivity";
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(48.77791275550184, 15.09521484375), new LatLng(55.02802211299252, 23.5107421875));

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shortest_path, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assistant = new LocationAssistant(getActivity(), this, LocationAssistant.Accuracy.HIGH, 5000, false);
        assistant.setVerbose(true);

        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        tvLocation.setText(getString(R.string.empty));

        tvLocationAdress = (TextView) view.findViewById(R.id.tvLocationAdress);
        tvLocationAdress.setText("");

        FragmentManager fm = getActivity().getSupportFragmentManager();
        map = (SupportMapFragment) fm.findFragmentById(R.id.map);
        map = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, map).commit();
        map.getMapAsync(this);


        mContext = getContext();

        // Google Places
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID++, this)
                .addConnectionCallbacks(this)
                .build();
        etOrigin = (AutoCompleteTextView) view.findViewById(R.id.etOrigin);
        etOrigin.setThreshold(3);
        etOrigin.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        etOrigin.setAdapter(mPlaceArrayAdapter);
        etDestination = (AutoCompleteTextView) view.findViewById(R.id.etDestination);
        etDestination.setThreshold(3);
        etDestination.setOnItemClickListener(mAutocompleteClickListener);
        etDestination.setAdapter(mPlaceArrayAdapter);

        // po nacisnieciu guzika od wyznaczenia trasy schowaj klawiature
        Button btnFindPath = (Button) view.findViewById(R.id.btnFindPath);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sendRequest();
                // Chowanie klawiatury
                MainActivity.hideKeyboard(getActivity());
            }
        });
        Bundle arg = getArguments();
        String msgStr;
        tvLocationAdress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                etOrigin.setText(tvLocationAdress.getText().toString());
            }

        });
        if(arg != null) {
            msgStr = arg.getString("PLACE");
            Toast.makeText(getContext(), msgStr, Toast.LENGTH_LONG).show();
            etDestination.setText(msgStr);
        }

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final com.google.android.gms.location.places.Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            /*
            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
            mIdTextView.setText(Html.fromHtml(place.getId() + ""));
            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
            mWebTextView.setText(place.getWebsiteUri() + "");
            if (attributions != null) {
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
            */
        }
    };

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();

        if (origin.isEmpty()) {
            Toast.makeText(getContext(), "Podaj adres początkowy", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(getContext(), "Podaj adres końcowy", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @NonNull
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (assistant.onPermissionsUpdated(requestCode, grantResults))
            tvLocation.setOnClickListener(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        assistant.onActivityResult(requestCode, resultCode);
    }

    @Override
    public void onNeedLocationPermission() {
        tvLocation.setText("Need\nPermission");
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assistant.requestLocationPermission();
            }
        });
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
                        tvLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                assistant.requestLocationPermission();
                            }
                        });
                    }
                })
                .show();
        BlockClickFlag.setFlagTrue();
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
        BlockClickFlag.setFlagTrue();
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
        if (location == null) {
            BlockClickFlag.setFlagTrue();
            return;
        }
        tvLocation.setOnClickListener(null);
        tvLocation.setText(location.getLongitude() + "; " + location.getLatitude());
        if (assistant.getBestLocation() != null && MainActivity.isNetworkConnected(mContext)) {
            // test {
            String geolocation = getCompleteAddressString(location.getLatitude(), location.getLongitude());
            tvLocationAdress.setText(geolocation);
            // }
            ///mMap.clear();
            LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(myPosition).title(geolocation);
            if(myPositionMarker == null){
                myPositionMarker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 16));
            }
            else {
                myPositionMarker.setPosition(myPosition);
            }

        } else {
            Toast.makeText(mContext,"No network connection available.",Toast.LENGTH_SHORT).show();
        }
        tvLocation.setAlpha(1.0f);
        tvLocation.animate().alpha(0.5f).setDuration(400);
       // ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        BlockClickFlag.setFlagTrue();

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        tvLocation.setText(getString(R.string.mockLocationMessage));
        tvLocation.setOnClickListener(fromView);
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {
        tvLocation.setText(getString(R.string.error));
    }

    private void mapEvents() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                if(myNewMarker == null){
                    myNewMarker = mMap.addMarker(markerOptions);
                }
                else {
                    myNewMarker.setPosition(latLng);
                    myNewMarker.hideInfoWindow();
                }

                if (MainActivity.isNetworkConnected(mContext)) {
                    String s = getCompleteAddressString(latLng.latitude, latLng.longitude);
                    String lines[] = s.split("\\r?\\n");
                    String fullAddress = "";
                    for (int i = 0; i < lines.length; i++) {
                        if (i > 0) {
                            fullAddress += ", ";
                        }
                        fullAddress += lines[i];
                    }
                    myNewMarker.setTitle(fullAddress);
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 800, null);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        mapEvents();

    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getContext(), "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (routes.size() == 0)
            return;
        String rt = "";
        int c = 0;
        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) getView().findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) getView().findViewById(R.id.tvDistance)).setText(route.distance.text);
            rt = route.distance.text;
            Log.d("distance", String.valueOf(c++));
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
                builder.include(route.points.get(i));
            }
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        //int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.1); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
        isPathExisting = true;
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes, List<EventsInfo> list) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(!PlacesHandler.isAlreadyAdded(marker.getTitle().trim())) {
            Toast.makeText(getContext(), "Place added", Toast.LENGTH_SHORT).show();
            String czas = "0:00";
            Place pl = new Place(marker.getTitle().trim(), marker.getTitle(), czas);
            PlacesHandler.addPlace(pl);
            PlacesHandler.db.dodaj(pl);
            PlacesHandler.getAdapter().notifyDataSetChanged();
        }else {
            Toast.makeText(getContext(), "Place already exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getContext(), "Google Places API connection failed with error code:" + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }
}

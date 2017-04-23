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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShortesPathActivity extends FragmentActivity implements LocationAssistant.Listener, OnMapReadyCallback {

    private TextView tvLocation;
    private LocationAssistant assistant;
    private SupportMapFragment map;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
        assistant.setVerbose(true);

        setContentView(R.layout.shortest_path);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocation.setText(getString(R.string.empty));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        assistant.start();
    }

    @Override
    protected void onPause() {
        assistant.stop();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (assistant.onPermissionsUpdated(requestCode, grantResults))
            tvLocation.setOnClickListener(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        new AlertDialog.Builder(this)
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
    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView,
                                                        DialogInterface.OnClickListener fromDialog) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permissionPermanentlyDeclined)
                .setPositiveButton(R.string.ok, fromDialog)
                .show();
    }

    @Override
    public void onNeedLocationSettingsChange() {
        new AlertDialog.Builder(this)
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
        new AlertDialog.Builder(this)
                .setMessage(R.string.switchOnLocationLong)
                .setPositiveButton(R.string.ok, fromDialog)
                .show();
    }

    @Override
    public void onNewLocationAvailable(Location location) {
        if (location == null) return;
        tvLocation.setOnClickListener(null);
        tvLocation.setText(location.getLongitude() + "\n" + location.getLatitude());
        if (assistant.getBestLocation() != null) {
            mMap.clear();
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
        tvLocation.setAlpha(1.0f);
        tvLocation.animate().alpha(0.5f).setDuration(400);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
}

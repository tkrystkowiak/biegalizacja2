package com.tomaszkrystkowiak.biegalizacja;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    private GoogleMap mMap;
    private Polyline route;
    private Location mLastKnownLocation;
    private Location penultimateLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private boolean mLocationPermissionGranted;
    private boolean isMeasuring = false;
    private Button startButton;
    private Button pauseButton;
    private TextView timeView;
    private TextView distanceView;
    private float distance;
    private Handler timerHandler = new Handler();
    private long startTime;
    private long pauseTime;
    private long resumeTime;
    private long pausedTime;
    private long totalTime;
    private Date startDate;
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            totalTime = System.currentTimeMillis() - (startTime + pausedTime);
            int seconds = (int) (totalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timeView.setText(String.format("%02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.i(TAG, "noResult");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                    Log.i(TAG, mLastKnownLocation.toString());
                    updateRoute();
                    updateDistance();
                }
            }
        };
        startButton = findViewById(R.id.startstop_button);
        startButton.setOnClickListener(new StartButtonClick());
        pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new PauseButtonClick());
        timeView = findViewById(R.id.time_view);
        distanceView = findViewById(R.id.distance_view);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.CYAN);
        polylineOptions.width(10);
        route = mMap.addPolyline(polylineOptions);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        Log.d(TAG, "Permissions granted proceeding.");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        getDeviceLocation();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Log.d(TAG, "Location found. Showing current location");
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 17));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.408333,16.934167), 15));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    protected void startLocationUpdates() {
        Log.i(TAG, "Starting updates");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "Starting updates");
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);
    }

    private void updateRoute() {
        List<LatLng> points = route.getPoints();
        points.add(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()));
        route.setPoints(points);
    }

    private void updateDistance() {
        if(penultimateLocation != null){
            float addedDistance = penultimateLocation.distanceTo(mLastKnownLocation);
            distance =distance + addedDistance;
            Log.i(TAG, "Distance: "+distance);
            distanceView.setText(Math.round(distance)+"m");
        }
        penultimateLocation = mLastKnownLocation;
    }

    private void startSummaryActivity(){
        ArrayList<LatLng> routePoints = new ArrayList<>();
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra("distance",distance);
        intent.putExtra("route", routePoints);
        intent.putExtra("time",totalTime);
        intent.putExtra("startDate", startDate);
        startActivity(intent);
    }

    private class StartButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(startButton.getText().equals("start")) {
                Calendar calendar = Calendar.getInstance();
                startDate = calendar.getTime();
                isMeasuring = true;
                startLocationUpdates();
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
                startButton.setText(R.string.button_stop);
            }
            else{
                isMeasuring = false;
                stopLocationUpdates();
                timerHandler.removeCallbacks(timerRunnable);
                startButton.setText(R.string.button_start);
                startSummaryActivity();
            }
        }
    }

    private class PauseButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(pauseButton.getText().equals("pause")) {
                pauseTime = System.currentTimeMillis();
                stopLocationUpdates();
                timerHandler.removeCallbacks(timerRunnable);
                pauseButton.setText(R.string.button_resume);
            }
            else{
                resumeTime = System.currentTimeMillis();
                pausedTime = pausedTime + resumeTime - pauseTime;
                startLocationUpdates();
                timerHandler.postDelayed(timerRunnable, 0);
                pauseButton.setText(R.string.button_pause);
            }
        }
    }

}

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RaceActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "RaceActivity";
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private Polyline ghostRoute;
    private Polyline userRoute;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private boolean mLocationPermissionGranted;
    private TextView infoView;
    private ArrayList<LatLng> routePointsList;
    private Button startButton;
    private boolean isStarted = false;
    private int checkpointIndex = 0;
    private ArrayList<Long> timestamps;
    private Handler timerHandler = new Handler();
    private long startTime;
    private long totalTime;
    private ArrayList<Location> checkpoints;
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            totalTime = System.currentTimeMillis() - startTime;
            int seconds = (int) (totalTime / 1000);
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: starting creation");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
        routePointsList = getIntent().getParcelableArrayListExtra("route");
        checkpoints = new ArrayList<Location>();
        timestamps = new ArrayList<Long>();
        Log.i(TAG, "onCreate: first etap of creation finished");
        convertTimestamps();
        createCheckpoints();
        getLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                    if(isStarted){
                        isCheckpointChecked();
                        updateRoute();
                    }
                    else {
                        if (isNearStart()) {
                            startButton.setEnabled(true);
                        } else {
                            startButton.setEnabled(false);
                        }
                    }
                }
            }
        };
        infoView = findViewById(R.id.info_view);
        infoView.setText("Go to the start point");
        startButton = findViewById(R.id.start_race_button);
        startButton.setOnClickListener(new StartButtonClick());
        startButton.setEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions polylineOptionsGhost = new PolylineOptions();
        polylineOptionsGhost.color(Color.BLACK);
        polylineOptionsGhost.width(20);
        ghostRoute = mMap.addPolyline(polylineOptionsGhost);

        PolylineOptions polylineOptionsUser = new PolylineOptions();
        polylineOptionsUser.color(Color.BLUE);
        polylineOptionsUser.width(20);
        userRoute = mMap.addPolyline(polylineOptionsUser);


        ghostRoute.setPoints(routePointsList);
        Log.i(TAG, "onMapReady: isEmpty"+ routePointsList.isEmpty());
        LatLng start = routePointsList.get(0);
        LatLng finish = routePointsList.get(routePointsList.size()-1);
        mMap.addMarker(new MarkerOptions().position(start).title("Start"));
        mMap.addMarker(new MarkerOptions().position(finish).title("Finish"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(start.latitude, start.longitude), 17));
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

    private boolean isNearStart(){
        Location start = new Location("");
        start.setLatitude(routePointsList.get(0).latitude);
        start.setLongitude(routePointsList.get(0).longitude);
        if(start.distanceTo(mLastKnownLocation)<=10){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
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
                            Log.d(TAG, "Location found. Showing current location");
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 17));
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void createCheckpoints(){
        for(int i = 0;i<routePointsList.size(); i=i+3){
            Location checkpoint = new Location("");
            checkpoint.setLatitude(routePointsList.get(i).latitude);
            checkpoint.setLongitude(routePointsList.get(i).longitude);
            checkpoints.add(checkpoint);
        }
        Location finish = new Location("");
        finish.setLatitude(routePointsList.get(routePointsList.size()-1).latitude);
        finish.setLongitude(routePointsList.get(routePointsList.size()-1).longitude);
        checkpoints.add(finish);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
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

    private void isCheckpointChecked(){
        if(checkpointIndex == checkpoints.size() - 1){
            long result = totalTime - timestamps.get(checkpointIndex);
            startRaceResultActivity(result);
        }
        else {
            if (checkpoints.get(checkpointIndex).distanceTo(mLastKnownLocation) <= 5) {
                long deltaTime = totalTime - timestamps.get(checkpointIndex);
                infoView.setText(deltaTime / 1000 + " s");
                checkpointIndex++;
            }
        }
    }

    private void convertTimestamps(){

        ArrayList<String> stringTimestamps = getIntent().getStringArrayListExtra("timestamps");
        Log.i(TAG, "convertTimestamps: this works");
        for (int i = 0;i<stringTimestamps.size();i=i+3) {
            timestamps.add(Long.valueOf(stringTimestamps.get(i)));
            Log.i(TAG, "convertTimestamps: timespam added nr"+i);
        }
        timestamps.add(Long.valueOf(stringTimestamps.get(stringTimestamps.size()-1)));
    }

    private void startRaceResultActivity(long result){
        Intent intent = new Intent(this, RaceResultActivity.class);
        intent.putExtra("result",result);
    }

    private void updateRoute() {
        List<LatLng> points = userRoute.getPoints();
        points.add(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()));
        userRoute.setPoints(points);
    }

    private class StartButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            isStarted = true;
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            startButton.setEnabled(false);
        }
    }
}

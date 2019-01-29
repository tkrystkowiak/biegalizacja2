package com.tomaszkrystkowiak.biegalizacja;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class SummaryActivity extends Activity {

    private static final String TAG = "SummaryActivity";

    private TextView distanceResultView;
    private TextView averageSpeedView;
    private TextView caloriesView;
    private Button showRouteButton;
    private Button saveRouteButton;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        distanceResultView = findViewById(R.id.distance_result_view);
        averageSpeedView = findViewById(R.id.average_speed_view);
        caloriesView = findViewById(R.id.calories_view);
        showRouteButton = findViewById(R.id.show_route_button);
        saveRouteButton = findViewById(R.id.save_route_button);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "routes").build();
        fillTextFields();
    }

    private void fillTextFields(){
        long distance = Math.round(getIntent().getFloatExtra("distance",0f));
        float distanceKm = (float)distance/1000;
        Log.i(TAG, "Distance in kiliometers: "+distanceKm);
        long time = getIntent().getLongExtra("time", 0l);
        float timeHours = (float)time/3600000;
        Log.i(TAG, "Time in Hours: "+timeHours);
        float avgSpeed = distanceKm/timeHours;
        Log.i(TAG, "Average speed: "+avgSpeed);
        distanceResultView.setText("Distance: "+ distance+"m");
        averageSpeedView.setText("Average speed: "+ Math.round(avgSpeed*100f)/100f +"km/h");
    }

    /*private Route prepareRouteToSave(){
        Route routeToSave = new Route();
        routeToSave.locations = getIntent().getParcelableArrayListExtra("route");
        routeToSave.distance = getIntent().getFloatExtra("distance",0f);
        routeToSave.date = (Date) getIntent().getSerializableExtra("date");
        return routeToSave;
    }*/

    /*private class SaveRouteButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            db.routeDao().insert(prepareRouteToSave());
        }
    }*/

}


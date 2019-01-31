package com.tomaszkrystkowiak.biegalizacja;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        showRouteButton.setOnClickListener(new ShowRouteButtonClick());
        saveRouteButton = findViewById(R.id.save_route_button);
        saveRouteButton.setOnClickListener(new SaveRouteButtonClick());
        db = Room.databaseBuilder(getApplicationContext(),
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

        float calorie = 85 * distanceKm;
        Log.i(TAG, "Calorie: "+calorie);
        caloriesView.setText("Calorie: "+Math.round(calorie*100f)/100f+" kcal");
    }

    private Route prepareRouteToSave(){
        Route routeToSave = new Route();
        if(getIntent().getParcelableArrayListExtra("route").isEmpty()){
            Log.i(TAG, "prepareRouteToSave: Route points to save are empty");
        }
        routeToSave.locations = getIntent().getParcelableArrayListExtra("route");
        routeToSave.distance = getIntent().getFloatExtra("distance",0f);
        routeToSave.date = (Date) getIntent().getSerializableExtra("startDate");
        return routeToSave;
    }

    private void showThatSaved(){
        Toast.makeText(this, "Route successfully saved", Toast.LENGTH_LONG).show();
    }

    private void startRoutePreviewActivity(){
        Intent intent = new Intent(this, RoutePreviewActivity.class);
        intent.putExtra("route",getIntent().getParcelableArrayListExtra("route"));
        startActivity(intent);
    }

    private class SaveRouteButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            DbRouteSavingAsyncTask dbRouteSavingAsyncTask = new DbRouteSavingAsyncTask();
            dbRouteSavingAsyncTask.execute();
            showThatSaved();

        }
    }

    private class ShowRouteButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            startRoutePreviewActivity();
        }
    }



    private class DbRouteSavingAsyncTask extends AsyncTask<Void, Void, Route> {


        @Override
        protected Route doInBackground(Void...voids) {

            Route toSave = prepareRouteToSave();
            db.routeDao().insert(toSave);
            return toSave;

        }

    }

}


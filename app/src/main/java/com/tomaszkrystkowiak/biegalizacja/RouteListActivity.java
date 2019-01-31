package com.tomaszkrystkowiak.biegalizacja;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import 	android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class RouteListActivity extends Activity {

    private static final String TAG = "RouteListActivity";
    private LinearLayout routesLayout;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        routesLayout = findViewById(R.id.routes_layout);
        routesLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.SPACE_CADET));
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "routes").build();
        DbRoutesAsyncTask dbRoutesAsyncTask = new DbRoutesAsyncTask();
        dbRoutesAsyncTask.execute();

    }

    private void drawRoutes(Route route,int i){
        LinearLayout subLayout = new LinearLayout(this);
        subLayout.setId(i);
        subLayout.setWeightSum(1);
        subLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams sublayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        sublayoutParams.setMargins(10,10,10,10);
        subLayout.setLayoutParams(sublayoutParams);
        subLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.rounded_corner));
        subLayout.setClipToOutline(true);
        subLayout.setGravity(Gravity.CENTER_VERTICAL);
        TextView routeView = new TextView(this);
        routeView.setId(i);
        LinearLayout.LayoutParams routeViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        routeViewParams.setMargins(30,40,10,10);
        routeView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22f);
        routeView.setLayoutParams(routeViewParams);
        routeView.setGravity(Gravity.CENTER_VERTICAL);
        Button showButton = new Button(this);
        Button raceButton = new Button(this);
        //showButton.setBackground(ContextCompat.getDrawable(this,R.drawable.ic_directions_run_black_24dp));
        showButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_map_black_24dp,0,0);
        showButton.setGravity(Gravity.CENTER);
        showButton.setText("SHOW");
        showButton.setLayoutParams(new LinearLayout.LayoutParams(200,200));


        //raceButton.setBackground(ContextCompat.getDrawable(this,R.drawable.ic_map_black_24dp));
        raceButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_directions_run_black_24dp,0,0);
        raceButton.setGravity(Gravity.CENTER);
        raceButton.setText("RACE");
        raceButton.setLayoutParams(new LinearLayout.LayoutParams(200,200));



        final Route routeToPass = route;

        routeView.append("Distance: " + Math.round(route.distance) +"m"+ "\n");
        routeView.append("Date: " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(route.date) + "\n");
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRoutePreviewActivity(routeToPass.locations);
            }
        });
        raceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRaceActivity(routeToPass.locations,routeToPass.timestamps);
            }
        });
        subLayout.addView(routeView);
        subLayout.addView(showButton);
        subLayout.addView(raceButton);
        routesLayout.addView(subLayout);
    }

    private void startRoutePreviewActivity(ArrayList<LatLng> routePoints){
        Intent intent = new Intent(this, RoutePreviewActivity.class);
        intent.putExtra("route",routePoints);
        startActivity(intent);
    }

    private void startRaceActivity(ArrayList<LatLng> routePoints, ArrayList<String> timestamps){
        Intent intent = new Intent(this, RaceActivity.class);
        intent.putExtra("route",routePoints);
        intent.putStringArrayListExtra("timestamps",timestamps);
        startActivity(intent);
    }

    private void showThatNoRoutes(){
        Toast.makeText(this, "No available routes", Toast.LENGTH_LONG).show();
    }


    private class DbRoutesAsyncTask extends AsyncTask<Void , Void, List<Route>> {


        @Override
        protected  List<Route> doInBackground(Void...voids) {

            List<Route> routeList = db.routeDao().getAll();
            if(routeList.isEmpty()){
                return null;
            }
            else {
                return routeList;
            }
        }

        @Override
        protected void onPostExecute(List<Route> routeList) {
            if(routeList != null) {
                for(int i = 0; i < routeList.size(); i++) {
                    Route route = routeList.get(i);
                    drawRoutes(route,i);

                }
            }else{
                showThatNoRoutes();
            }

        }

    }

}

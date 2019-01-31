package com.tomaszkrystkowiak.biegalizacja;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RouteListActivity extends Activity {

    private static final String TAG = "RouteListActivity";
    private TextView routeListView;
    private LinearLayout routesLayout;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        routesLayout = findViewById(R.id.routes_layout);
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
        subLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView routeView = new TextView(this);
        routeView.setId(i);
        routeView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f);
        Button showButton = new Button(this);
        showButton.setId(i);
        final Route routeToPass = route;
        if(route.locations.isEmpty()){
            Log.i(TAG, "drawRoutes: original point list is empty ");
        }
        if(routeToPass.locations.isEmpty()){
            Log.i(TAG, "drawRoutes: point list is empty ");
        }

        routeView.append("Distance: " + route.distance + "\n");
        routeView.append("Date: " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(route.date) + "\n");
        showButton.setText("Show");
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRoutePreviewActivity((ArrayList<LatLng>)routeToPass.locations);
            }
        });
        subLayout.addView(routeView);
        subLayout.addView(showButton);
        routesLayout.addView(subLayout);
    }

    private void startRoutePreviewActivity(ArrayList<LatLng> routePoints){
        Intent intent = new Intent(this, RoutePreviewActivity.class);
        intent.putExtra("route",routePoints);
        startActivity(intent);
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
                routeListView.setText("No available routes");
            }

        }

    }

}

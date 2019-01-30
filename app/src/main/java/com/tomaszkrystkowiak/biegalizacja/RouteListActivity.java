package com.tomaszkrystkowiak.biegalizacja;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class RouteListActivity extends Activity {

    private TextView routeListView;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        routeListView = findViewById(R.id.route_list);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "routes").build();
        DbRoutesAsyncTask dbRoutesAsyncTask = new DbRoutesAsyncTask();
        dbRoutesAsyncTask.execute();

    }

    private Route getRoutes(){
        List<Route> routeList = db.routeDao().getAll();
        Route sample = routeList.get(0);
        return sample;
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
                Iterator<Route> iterator = routeList.iterator();
                while(iterator.hasNext()){
                    Route route = iterator.next();
                    routeListView.append("Distance: "+ route.distance+ "\n");
                    routeListView.append("Date: "+ route.date+ "\n");
                    routeListView.append("==========================="+ "\n");
                    routeListView.append("\n");
                }
            }else{
                routeListView.setText("No available routes");
            }

        }

    }

}

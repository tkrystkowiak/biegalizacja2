package com.tomaszkrystkowiak.biegalizacja;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Route.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RouteDao routeDao();

}

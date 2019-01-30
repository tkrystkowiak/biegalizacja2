package com.tomaszkrystkowiak.biegalizacja;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RouteDao {

    @Query("SELECT * FROM route")
    List<Route> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Route route);

}

package com.tomaszkrystkowiak.biegalizacja;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.ArrayList;

@Entity
public class Route {

    @PrimaryKey(autoGenerate = true)
    public int rid;

    @ColumnInfo(name = "locations")
    public ArrayList<LatLng> locations;

    @ColumnInfo(name = "timestamps")
    public ArrayList<String> timestamps;

    @ColumnInfo(name = "distance")
    public float distance;

    @ColumnInfo(name = "date")
    public Date date;

}

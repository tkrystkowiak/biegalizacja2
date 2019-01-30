package com.tomaszkrystkowiak.biegalizacja;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<LatLng> stringToLatLngList(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<LatLng>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String LatLngListToString(List<LatLng> someObjects) {
        Gson gson = new Gson();
        return gson.toJson(someObjects);
    }


}

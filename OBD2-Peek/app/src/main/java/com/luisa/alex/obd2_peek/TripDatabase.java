package com.luisa.alex.obd2_peek;

/**
 * Created by alex on 2016-11-24.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.util.Log;

import java.util.ArrayList;

public class TripDatabase extends SQLiteOpenHelper{

    /**
     * Created by alex on 2016-11-02.
     */
    private static final String TABLE_NAME = "trips";
    private static final String DB_FILENAME = "trips.db";
    private static final int DB_VERSION = 3;

    private static final String CREATE_STATEMENT = "" +
            "create table " +
            TABLE_NAME + "(" +
            "_id integer primary key autoincrement, " +
            "date text not null," +
            "duration integer not null," +
            "origin text not null," +
            "timeDeparture text not null," +
            "destination text not null," +
            "timeArrival text not null," +
            "maxSpeed integer not null," +
            "maxRPM integer not null" +
            ")";

    private static final String DROP_STATEMENT = "drop table " + TABLE_NAME;

    public TripDatabase(Context context) {
        super(context, DB_FILENAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_STATEMENT);
        db.execSQL(CREATE_STATEMENT);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_STATEMENT);
        db.execSQL(CREATE_STATEMENT);
    }

    //---------------ADD TRIP ---------------
    public Trip addTrip(String date, Long duration, String origin, String timeDeparture,
                        String destination, String timeArrival, Integer maxSpeed, Integer maxRPM) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("duration", duration);
        values.put("origin", origin);
        values.put("timeDeparture", timeDeparture);
        values.put("destination", destination);
        values.put("timeArrival", timeArrival);
        values.put("maxSpeed", maxSpeed + "");
        values.put("maxRPM", maxRPM + "");


        long id = db.insert(TABLE_NAME, null, values);
        return new Trip(id, date, duration, origin, timeDeparture, destination, timeArrival, maxSpeed, maxRPM);
    }

    //---------------ADD TRIP ---------------
    public Trip addTrip(Trip tripMissingId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", tripMissingId.getDate());
        values.put("duration", tripMissingId.getDuration());
        values.put("origin", tripMissingId.getOrigin());
        values.put("timeDeparture", tripMissingId.getTimeDeparture());
        values.put("destination", tripMissingId.getDestination());
        values.put("timeArrival", tripMissingId.getTimeArrival());
        values.put("maxSpeed", tripMissingId.getMaxSpeed() + "");
        values.put("maxRPM", tripMissingId.getMaxRPM() + "");


        long id = db.insert(TABLE_NAME, null, values);
        tripMissingId.setId(id); //add the id
        return tripMissingId;
    }

    //---------------DELETE TRIP ---------------
    public void deleteTrip(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "_id = ?", new String[]{""+id});
    }

    //---------------GET ALL TRIPS---------------
    //Get a list of all the trips in the database
    public ArrayList<Trip> getAllTrips(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Trip> trips = new ArrayList<>();

        String[] columns = {"_id", "date", "duration", "origin", "timeDeparture", "destination", "timeArrival", "maxSpeed", "maxRPM"};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){

            //Get the components for the trip
            Long id = cursor.getLong(0);
            String date = cursor.getString(1);
            Long duration = cursor.getLong(2);
            String origin = cursor.getString(3);
            String timeDeparture = cursor.getString(4);
            String destination = cursor.getString(5);
            String timeArrival = cursor.getString(6);
            Integer maxSpeed = cursor.getInt(7);
            Integer maxRPM = cursor.getInt(8);

            //Add the trip to the overall trips array
            trips.add(new Trip(id, date, duration, origin, timeDeparture,destination, timeArrival, maxSpeed, maxRPM));

            cursor.moveToNext();
        }

        return trips;
    }

    //---------------DELETE ALL TRIPS ---------------
    public void deleteAllTrips(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
}
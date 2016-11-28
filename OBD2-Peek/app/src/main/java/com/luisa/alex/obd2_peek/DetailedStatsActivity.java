package com.luisa.alex.obd2_peek;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import info.hoang8f.widget.FButton;

//Floating menu

public class DetailedStatsActivity extends AppCompatActivity {

    private final static String TAG = "DetailedStatsActivity";
    private int position;

    private TripDatabase tripDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_stats);
        tripDatabase = new TripDatabase(this);

        getIntentExtras();
    }

    public static String getTimeDiff(long duration){
        //Takes the duration (ms) and returns a string
        //that shows the days, hours, minutes, and seconds
        String returnStr = "";


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = duration / daysInMilli;
        if(elapsedDays != 0){
            returnStr += elapsedDays + " Day";
            if(elapsedDays > 1){
                returnStr += "s";
            }
            returnStr += ", ";
        }
        duration = duration % daysInMilli;

        long elapsedHours = duration / hoursInMilli;
        if(elapsedHours != 0){
            returnStr += elapsedHours + " Hour";
            if(elapsedHours > 1){
                returnStr += "s";
            }
            returnStr += ", ";
        }
        duration = duration % hoursInMilli;

        long elapsedMinutes = duration / minutesInMilli;
        if(elapsedMinutes != 0){
            returnStr += elapsedMinutes + " Minute";
            if(elapsedMinutes > 1){
                returnStr += "s";
            }
            returnStr += ", ";
        }
        duration = duration % minutesInMilli;

        long elapsedSeconds = duration / secondsInMilli;
        returnStr += elapsedSeconds + " seconds";

        return returnStr;
    }

    public void displayTripInfo(Trip trip) {

        TextView dateText = (TextView) findViewById(R.id.date);
        TextView durationText = (TextView) findViewById(R.id.duration);
        TextView originText = (TextView) findViewById(R.id.origin);
        TextView timeDepartureText = (TextView) findViewById(R.id.time_departure);
        TextView destinationText = (TextView) findViewById(R.id.destination);
        TextView timeArrivalText = (TextView) findViewById(R.id.time_arrival);
        TextView maxSpeedText = (TextView) findViewById(R.id.maxSpeed);
        TextView maxRPMText = (TextView) findViewById(R.id.maxRPM);

        dateText.setText(trip.getDate());
        durationText.setText(getTimeDiff(trip.getDuration()));
        originText.setText(trip.getOrigin());
        timeDepartureText.setText(trip.getTimeDeparture());
        destinationText.setText(trip.getDestination());
        timeArrivalText.setText(trip.getTimeArrival());
        maxSpeedText.setText(trip.getMaxSpeed()+" km/h");
        maxRPMText.setText(trip.getMaxRPM()+" rpm");
    }

    public void getIntentExtras() {

        Intent intent = getIntent();

        String date = intent.getStringExtra("date");
        long duration = intent.getLongExtra("duration", -1);
        String origin = intent.getStringExtra("origin");
        String timeDeparture = intent.getStringExtra("timeDeparture");
        String destination = intent.getStringExtra("destination");
        String timeArrival = intent.getStringExtra("timeArrival");
        int maxSpeed = intent.getIntExtra("maxSpeed", -1);
        int maxRPM = intent.getIntExtra("maxRPM", -1);
        position = intent.getIntExtra("position", -1);

        FButton deleteButton = (FButton) findViewById(R.id.btn_delete_trip);
        if (position == -1) {
            //deleteButton.setEnabled(false);
            deleteButton.setVisibility(View.GONE);
        } else {
            deleteButton.setVisibility(View.VISIBLE);
        }

        displayTripInfo(new Trip(date, duration, origin, timeDeparture, destination, timeArrival, maxSpeed, maxRPM));
    }

    public void returnToMainClicked(View view) {
        finish();
    }

    public void deletePastTripClicked(View view) {
        Log.d("TAG", "Item to delete: " + position);

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this trip!")
                .setConfirmText("YES I\'m sure")
                .setCancelText("NO Nevermind")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        ArrayList<Trip> trips = tripDatabase.getAllTrips();
                        Trip tripToDelete = trips.get(position);
                        // delete from database
                        tripDatabase.deleteTrip(tripToDelete.getId());

                        MainActivity.showToast("Trip deleted");

                        finish();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();
    }
}
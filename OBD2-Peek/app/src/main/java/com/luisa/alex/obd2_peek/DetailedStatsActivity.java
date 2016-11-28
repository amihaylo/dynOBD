package com.luisa.alex.obd2_peek;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

//Floating menu
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

public class DetailedStatsActivity extends AppCompatActivity {

    private final static String TAG = "DetailedStatsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_stats);

        getIntentExtras();
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
        durationText.setText(""+trip.getDuration());
        originText.setText(trip.getOrigin());
        timeDepartureText.setText(trip.getTimeDeparture());
        destinationText.setText(trip.getDestination());
        timeArrivalText.setText(trip.getTimeArrival());
        maxSpeedText.setText(""+trip.getMaxSpeed());
        maxRPMText.setText(""+trip.getMaxRPM());
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

        displayTripInfo(new Trip(date, duration, origin, timeDeparture, destination, timeArrival, maxSpeed, maxRPM));
    }

    public void returnToMainClicked(View view) {
        finish();
    }
}
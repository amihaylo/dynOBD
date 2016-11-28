package com.luisa.alex.obd2_peek;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.ArrayList;

public class PastTripsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final static String TAG = "PastTripsActivity";

    private boolean init = false;
    private BoomMenuButton boomMenuButton;
    private TripDatabase tripDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_trips);
        tripDatabase = new TripDatabase(this);
        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);

        //testDatabase();

        /*
        //Get the Trips from the dataset
        ArrayList<Trip> trips = tripDatabase.getAllTrips();
        showTrips(trips);
        */
    }

    @Override
    public void onRestart() {
        ArrayList<Trip> trips = tripDatabase.getAllTrips();
        showTrips(trips);
        super.onResume();
    }

    /*
    private ArrayList<Trip> loadTripsDB(){
        TripDatabase tripDatabase = new TripDatabase(this);

        return tripDatabase.getAllTrips();
    }
    */

    /*
    private void testDatabase() {
        String METHOD = "testDatabase";
        Log.d(METHOD, "called");

        TripDatabase tripDatabase = new TripDatabase(this);
        tripDatabase.deleteAllTrips();

        //Check add function
        tripDatabase.addTrip((new Date()).toString(), new Long(1400), "Toronto, Canada", "Tokyo, Japan", 150, 4000);
        tripDatabase.addTrip((new Date()).toString(), new Long(1200), "Oshawa, Canada", "Mississauga, Japan", 250, 6000);
        tripDatabase.addTrip((new Date()).toString(), new Long(1100), "Niagara Falls, Canada", "New York, USA", 50, 2000);

        //check delete function
        Trip toDel = tripDatabase.addTrip((new Date()).toString(), new Long(0), "TO DELETE", "TO DELETE", 0, 0);
        tripDatabase.deleteTrip(toDel.getId());

        //Print the current Database
        ArrayList<Trip> trips = tripDatabase.getAllTrips();
        for(Trip trip : trips){
            System.out.println(trip);
        }

        showTrips(trips);
    }
    */

    public void showTrips(ArrayList<Trip> trips){

        TextView noPastTrips = (TextView) findViewById(R.id.empty_list);

        if (trips.size() > 0) {
            ListView listView = (ListView)findViewById(R.id.listView_pastTrips);
            listView.setAdapter(new tripArrayAdapter(this, trips));
            listView.setOnItemClickListener(this);

            noPastTrips.setVisibility(View.GONE);
        } else {
            noPastTrips.setText("There are no past trips to show.");
            noPastTrips.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Use a param to record whether the boom button has been initialized
        // Because we don't need to init it again when onResume()
        if (init)
            return;

        init = true;

        int[][] subButton1Colors = new int[1][2];
        int[][] subButton2Colors = new int[1][2];
        int[][] subButton3Colors = new int[1][2];
        int[][] subButton4Colors = new int[1][2];

        subButton1Colors[0][1] = ContextCompat.getColor(this, R.color.md_red_400); //
        subButton1Colors[0][0] = Util.getInstance().getPressedColor(subButton1Colors[0][1]);

        subButton2Colors[0][1] = ContextCompat.getColor(this, R.color.md_green_400);
        subButton2Colors[0][0] = Util.getInstance().getPressedColor(subButton2Colors[0][1]);

        subButton3Colors[0][1] = ContextCompat.getColor(this, R.color.md_amber_600);
        subButton3Colors[0][0] = Util.getInstance().getPressedColor(subButton3Colors[0][1]);

        subButton4Colors[0][1] = ContextCompat.getColor(this, R.color.md_light_blue_600);
        subButton4Colors[0][0] = Util.getInstance().getPressedColor(subButton4Colors[0][1]);

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.car), subButton1Colors[0], "About")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.where), subButton2Colors[0], "Locator")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.help), subButton3Colors[0], "Help")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.home), subButton4Colors[0], "Home")
                .button(ButtonType.CIRCLE)
                .boom(BoomType.HORIZONTAL_THROW_2)
                .place(PlaceType.CIRCLE_4_2)
                //.subButtonTextColor(Color.BLACK)
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {

                        //Prepare the intent to be returned to main
                        Intent resultIntent = new Intent();

                        switch (buttonIndex) {
                            case 0:
                                Log.d(TAG, "About was clicked");
                                setResult(MainActivity.ABOUT_REQ,resultIntent);
                                break;
                            case 1:
                                Log.d(TAG, "Locator was clicked");
                                setResult(MainActivity.LOCATION_REQ,resultIntent);
                                break;
                            case 2:
                                Log.d(TAG, "Help was clicked");
                                setResult(MainActivity.HELP_REQ,resultIntent);
                                break;
                            case 3:
                                Log.d(TAG, "Home was clicked");
                                break;
                            default:
                                Log.d(TAG, "There has been an error involving the subbuttons.");
                                break;
                        }
                        finish();
                    }
                })
                .init(boomMenuButton);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.i(TAG, "Clicked element  " + id + " at position " + position);

        ArrayList<Trip> allTrips = tripDatabase.getAllTrips();
        Trip tripClicked = allTrips.get(position);

        Intent intent = new Intent();
        intent.setClass(PastTripsActivity.this, DetailedStatsActivity.class);

        intent.putExtra("date", tripClicked.getDate());
        intent.putExtra("duration", tripClicked.getDuration());
        intent.putExtra("origin", tripClicked.getOrigin());
        intent.putExtra("timeDeparture", tripClicked.getTimeDeparture());
        intent.putExtra("destination", tripClicked.getDestination());
        intent.putExtra("timeArrival", tripClicked.getTimeArrival());
        intent.putExtra("maxSpeed", tripClicked.getMaxSpeed());
        intent.putExtra("maxRPM", tripClicked.getMaxRPM());
        intent.putExtra("position", position);
        startActivity(intent);
    }
}

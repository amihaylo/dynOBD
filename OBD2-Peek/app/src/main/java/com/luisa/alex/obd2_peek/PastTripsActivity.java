package com.luisa.alex.obd2_peek;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.ArrayList;
import java.util.Date;

public class PastTripsActivity extends AppCompatActivity {

    private final static String TAG = "PastTripsActivity";

    private boolean init = false;
    private BoomMenuButton boomMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_trips);

        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);

        testDatabase();
    }

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

        subButton1Colors[0][1] = ContextCompat.getColor(this, R.color.md_light_blue_600);
        subButton1Colors[0][0] = Util.getInstance().getPressedColor(subButton1Colors[0][1]);

        subButton2Colors[0][1] = ContextCompat.getColor(this, R.color.md_green_400);
        subButton2Colors[0][0] = Util.getInstance().getPressedColor(subButton2Colors[0][1]);

        subButton3Colors[0][1] = ContextCompat.getColor(this, R.color.md_amber_600);
        subButton3Colors[0][0] = Util.getInstance().getPressedColor(subButton3Colors[0][1]);

        subButton3Colors[0][1] = ContextCompat.getColor(this, R.color.md_red_400);
        subButton3Colors[0][0] = Util.getInstance().getPressedColor(subButton3Colors[0][1]);

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.home), subButton3Colors[0], "Home")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.where), subButton1Colors[0], "Locator")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.help), subButton2Colors[0], "Help")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.car), subButton4Colors[0], "About")
                .button(ButtonType.CIRCLE)
                .boom(BoomType.HORIZONTAL_THROW_2)
                .place(PlaceType.CIRCLE_4_2)
                //.subButtonTextColor(Color.BLACK)
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {

                        switch (buttonIndex) {
                            case 0:
                                finish();
                                Log.d(TAG, "Home was clicked");
                                break;
                            case 1:
                                //LaunchLocatorActivity();
                                Log.d(TAG, "Locator was clicked");
                                break;
                            case 2:
                                //LaunchHelpActivity();
                                Log.d(TAG, "Help was clicked");
                                break;
                            case 3:
                                //LaunchAboutCarActivity();
                                Log.d(TAG, "About was clicked");
                                break;
                            default:
                                Log.d(TAG, "There has been an error involving the subbuttons.");
                                break;
                        }
                    }
                })
                .init(boomMenuButton);
    }
}

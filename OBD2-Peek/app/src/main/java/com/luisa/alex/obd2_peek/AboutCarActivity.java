package com.luisa.alex.obd2_peek;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.List;

public class AboutCarActivity extends AppCompatActivity {

    private final static String TAG = "AboutCarActivity";

    private boolean init = false;
    private BoomMenuButton boomMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_car);

        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);

        String METHOD = "onCreate";

        //Obtain the car data from the intent
        Intent callingIntent = getIntent();
        List<String> data = callingIntent.getStringArrayListExtra("carData");

        /*  DEBUG
        for(String ele : data){
            Log.d(METHOD, ele);
        }
        */

        //Display the information to the user
        ListView listView = (ListView)findViewById(R.id.list_view_vin_data);
        listView.setAdapter(new vinArrayAdapter(this, data));

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

        subButton3Colors[0][1] = ContextCompat.getColor(this, R.color.md_deep_purple_400);
        subButton3Colors[0][0] = Util.getInstance().getPressedColor(subButton3Colors[0][1]);

        subButton4Colors[0][1] = ContextCompat.getColor(this, R.color.md_amber_600);
        subButton4Colors[0][0] = Util.getInstance().getPressedColor(subButton4Colors[0][1]);

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.home), subButton1Colors[0], "Home")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.where), subButton2Colors[0], "Locator")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.help), subButton4Colors[0], "Help")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.past), subButton3Colors[0], "Trips")
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
                                Log.d(TAG, "Home was clicked");
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
                                Log.d(TAG, "Trips was clicked");
                                setResult(MainActivity.TRIPS_REQ,resultIntent);
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
}

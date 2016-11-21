package com.luisa.alex.obd2_peek;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

//Floating menu
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

public class HelpActivity extends AppCompatActivity {

    private boolean init = false;
    private BoomMenuButton boomMenuButton;

    private final static String TAG = "HelpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);
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

        subButton1Colors[0][1] = ContextCompat.getColor(this, R.color.md_deep_purple_400);
        subButton1Colors[0][0] = Util.getInstance().getPressedColor(subButton1Colors[0][1]);

        subButton2Colors[0][1] = ContextCompat.getColor(this, R.color.md_green_400);
        subButton2Colors[0][0] = Util.getInstance().getPressedColor(subButton2Colors[0][1]);

        subButton3Colors[0][1] = ContextCompat.getColor(this, R.color.md_light_blue_600);
        subButton3Colors[0][0] = Util.getInstance().getPressedColor(subButton3Colors[0][1]);

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.home), subButton3Colors[0], "Home")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.where), subButton1Colors[0], "Trips")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.help), subButton2Colors[0], "Locator")
                .button(ButtonType.CIRCLE)
                .boom(BoomType.HORIZONTAL_THROW_2)
                .place(PlaceType.CIRCLE_3_1)
                //.subButtonTextColor(Color.BLACK)
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {

                        switch (buttonIndex) {
                            case 0:
                                finish();
                                //Log.d(TAG, "Home was clicked");
                                //break;
                            case 1:
                                //LaunchLocatorActivity();
                                Log.d(TAG, "Past Trips was clicked");
                                break;
                            case 2:
                                //LaunchHelpActivity();
                                Log.d(TAG, "Locator was clicked");
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

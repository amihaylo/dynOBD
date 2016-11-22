package com.luisa.alex.obd2_peek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

public class AboutCarActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_car);

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
        listView.setAdapter(new DisplayArrayAdapter(this, data));

    }
}

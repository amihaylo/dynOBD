package com.luisa.alex.obd2_peek;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Step1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);

    }

    public void backClicked(View view) {
        finish();
    }
}

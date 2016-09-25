package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //-------------DEBUG----------------
    public void testButton(View view){
        //Init the toast Message
        CharSequence testMsg = "Test Clicked!";


        //---------------BLUETOOTH-------------
        //get the Bluetooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if Bluetooth is supported
        if(mBluetoothAdapter == null){
            testMsg = "Bluetooth NOT Supported!";
        }else{
            testMsg = "Bluetooth is Supported!";
        }

        //Enable Bluetooth if not enabled
        if(!mBluetoothAdapter.isEnabled()){
            testMsg = "requesting to enable Bluetooth";
            //If the bluetooth is not enabled on the device
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Query all paired Devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //If there are any paired devices found
        if(pairedDevices.size() > 0){
            //ArrayAdapter mArrayAdapter = TODO
            //Loop through all the devices
            for (BluetoothDevice device : pairedDevices){
                //add to an Array adapter
                //TODO
            }
        }

        //Create and Display the Toast
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, testMsg, duration);
        toast.show();
    }
}

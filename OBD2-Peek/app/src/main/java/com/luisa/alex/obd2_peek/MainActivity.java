package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity
        extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    //************************MEMBER VARIABLES************************
    //BLUETOOTH MEMBERS
    public static final int REQUEST_ENABLE_BT = 8100;
    public ConnectBTAsync connBTAsync = null;
    public ConnectBTThread connBTThread = null;
    public BluetoothSocket commSocket;
    //public  ConnectBTThread connBTThread = null;

    //TOAST MEMBERS
    private static Toast toast;

    //****************************METHODS******************************

    //--------------------LIFECYCLE METHODS----------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the toast
        this.toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity.onDestroy() Called");
        super.onDestroy();

        //Close the Bluetooth Connection
        if(connBTAsync != null){
            connBTAsync.closeSocket();
        }
    }

    //--------------------INTENT RESULTS----------------------
    @Override
    public void onActivityResult(int requestCode,
                                 int responseCode,
                                 Intent resultIntent){

        //Response Intent once the Bluetooth has been enabled
        if(requestCode == REQUEST_ENABLE_BT && responseCode == RESULT_OK){
            Log.d(TAG, "[MainActivity.onActivityResult] Bluetooth has been Enabled");
            MainActivity.showToast("Bluetooth Enabled");
        }
    }

    //--------------------BUTTON CLICKS----------------------

    //ENABLE BLUETOOTH BUTTON
    public void enableBTBtnClick(View view){
        Log.d(TAG, "MainActivity.enableBTBtnClick() Called");

        //check if device supports bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            //Device does not support bluetooth
            Log.d(TAG, "[MainActivity.enableBTBtnClick] Device does not support Bluetooth");
            return;
        }

        //Enabled bluetooth if it is not already enabled
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            Log.d(TAG, "[MainActivity.enableBTBtnClick] Bluetooth already enabled");
            MainActivity.showToast("Bluetooth is already enabled");
        }
    }

    //CONNECT BLUETOOTH BUTTON
    public void connectBtnClick(View view){
        Log.d(TAG, "MainActivity.connectBtnClick() Called");

        //Check to see which button was clicked (async/thread)
        final Boolean isAsync;
        if(view.getId() == R.id.btn_Main_connect_async){
            isAsync = true;
        }else if(view.getId() == R.id.btn_Main_connect_thread){
            isAsync = false;
        }else{
            isAsync = false;
        }

        //Init arrays that hold devices information
        ArrayList<String> deviceStrs = new ArrayList<>(); //Holds the device names + addresses
        final ArrayList<String> devicesAddress = new ArrayList<>(); //Holds only the device addresses
        final ArrayList<BluetoothDevice> devices = new ArrayList<>(); //Holds the actual device object

        //Obtain any already Paired/Bonded Bluetooth Devices
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        //If there is more than 1 paired device
        if(pairedDevices.size() > 0){
            //Iterate through all paired devices
            for(BluetoothDevice device : pairedDevices){
                //Add the devices to the 3 respective arrays defined above
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devicesAddress.add(device.getAddress());
                devices.add(device);
            }
        }else{
            //There are no existing Paired Devices
            Log.d(TAG, "[MainActivity.connectBtnClick] No Paired Devices Found");
            MainActivity.showToast("Need to Pair with device first!");
            return;
        }

        //Set up the list of paired devices to be shown to the user
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, deviceStrs.toArray(new String[deviceStrs.size()]));
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            //Once the user clicks on the paired device they wish to connect to
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Close the dialog
                dialog.dismiss();

                //Obtain the index of the device they clicked on
                int deviceIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                //Obtain the device given the index
                BluetoothDevice device = devices.get(deviceIndex);
                String deviceAddress = devicesAddress.get(deviceIndex);
                Log.d(TAG, "deviceAddress: " + deviceAddress);

                //Finally Attempt to Initialize the connection
                //Check if the async or thread method should be used to connect
                MainActivity.showToast("Connecting...");
                if(isAsync) {
                    //ASYNC METHOD
                    connBTAsync = new ConnectBTAsync(commSocket);
                    connBTAsync.execute(device);
                }else{
                    //THREAD METHOD
                    connBTThread = new ConnectBTThread(device);
                    connBTThread.start();
                }

            }
        });

        //Display the list of paired Devices to the user
        alertDialog.setTitle("Select Bluetooth Device");
        alertDialog.show();

    }

    //DISCONNECT BUTTON
    public void disconnectBtnClick(View view){
        //Check to see which button was clicked (async/thread)
        final Boolean isAsync;
        if(view.getId() == R.id.btn_Main_disconnect_async){
            isAsync = true;
        }else if(view.getId() == R.id.btn_Main_disconnect_async){
            isAsync = false;
        }else{
            isAsync = false;
        }

        //Disconnect the Bluetooth by closing the opened socket
        Log.d(TAG, "MainActivity.disconnectBtnClick() called");
        if(isAsync){
            if(connBTAsync != null){
                //connBTThread.cancel();
                if(connBTAsync.closeSocket()){
                    //Show Success Toast
                    MainActivity.showToast("Disconnect Successful!");
                }else{
                    //Show UnSuccess Toast
                    MainActivity.showToast("Disconnect Unsuccessful!");
                }
            }
        }else{
            if(connBTThread != null){
                //connBTThread.cancel();
                if(connBTThread.closeSocket()){
                    //Show Success Toast
                    MainActivity.showToast("Disconnect Successful!");
                }else{
                    //Show UnSuccess Toast
                    MainActivity.showToast("Disconnect Unsuccessful!");
                }
            }
        }

    }

    //--------------------TOAST----------------------
    public static void showToast(String message){
        //Displays a toast given a message
        MainActivity.toast.setText(message);
        MainActivity.toast.show();
    }

    public void testBtnClick(View view){
        Log.d(TAG, "MainActivity.testBtnClick()");

    }

}

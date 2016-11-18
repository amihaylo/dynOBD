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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity
        extends AppCompatActivity
        implements ConnectionHandler {
    public static final String TAG = "MainActivity";

    //************************MEMBER VARIABLES************************
    //BLUETOOTH MEMBERS
    public static final int REQUEST_ENABLE_BT = 8100;
    public ConnectBTAsync connBTAsync = null;
    public BluetoothSocket commSocket;

    //TOAST MEMBERS
    private static Toast toast;

    //ListView
    private DisplayArrayAdapter displayArrayAdapter;

    //TEMP
    private CustomGauge gaugeSpeed;
    private TextView gaugeViewSpeed;
    private CustomGauge gaugeRPM;
    private TextView gaugeViewRPM;


    //****************************METHODS******************************

    //--------------------LIFECYCLE METHODS----------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the toast
        this.toast = Toast.makeText(this, "", Toast.LENGTH_LONG);


        //Init the ListView
        /*
        ArrayList<OBDData> data = new ArrayList<>();
        ListView dataList = (ListView) findViewById(R.id.allData);
        this.displayArrayAdapter = new DisplayArrayAdapter(this, data);
        dataList.setAdapter(this.displayArrayAdapter);
        */


        //TEMP
        this.gaugeSpeed = (CustomGauge) findViewById(R.id.gauge_speed);
        this.gaugeViewSpeed = (TextView) findViewById(R.id.gaugeView_speed);
        this.gaugeRPM = (CustomGauge) findViewById(R.id.gauge_rpm);
        this.gaugeViewRPM = (TextView) findViewById(R.id.gaugeView_rpm);




        /*

        this.lbl_speed = (TextView) findViewById(R.id.speed_result);
        this.lbl_rpm = (TextView) findViewById(R.id.rpm_result);
        this.lbl_engineLoad = (TextView) findViewById(R.id.engineLoad_result);
        */
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity.onDestroy() Called");

        //Close the Bluetooth Connection
        if(connBTAsync != null){
            connBTAsync.closeSocket();
        }

        super.onDestroy();
    }

    //--------------INTENT RESULTS FROM BLUETOOTH--------------
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


    //--------------------BT HANDLER----------------------
    //Called after bluetooth has attempted to connect, either success or failure
    //This is called by the onPostExecute method after the ConnectBTAsync has finished doInBackground
    @Override
    public void handleBTConnection(BluetoothSocket mmSocket){
        //Check if the connection is valid
        Boolean isConnected = mmSocket.isConnected();
        //Placeholder message to be displayed to user
        String toastMessage;

        //Let the user know that the connection was a success
        if(isConnected){
            toastMessage = "Connection Success!";
        }else {
            toastMessage= "Unsuccessful Connection!";
        }

        //Show Log + Toast
        Log.d(TAG, "[MainActivity.handleBTConnection]" + toastMessage);
        MainActivity.showToast(toastMessage);

        //Start communicating
        OBDCommunicator obdConnection = new OBDCommunicator(this); //
        obdConnection.execute(mmSocket);
    }

    @Override
    public void updateUI(OBDData speedOBD, OBDData rpmOBD) {
        //Update the UI Gauge elements

        //Update the speed
        String speedStr = speedOBD.getData();
        String speedStr_num= speedStr.replaceAll("[^0-9]", "");
        final Integer speedInt = Integer.parseInt(speedStr_num);
        //gaugeSpeed.setValue(speedInt);
        //gaugeViewSpeed.setText(Integer.toString(gaugeSpeed.getValue()));

        //Update the RPM
        String rpmStr = rpmOBD.getData();
        String rpmStr_num = rpmStr.replaceAll("[^0-9]", "");
        final Integer rpmInt = Integer.parseInt(rpmStr_num);
        gaugeRPM.setValue(rpmInt);
        gaugeViewRPM.setText(Integer.toString(gaugeRPM.getValue()));
    }


    @Override
    public void updateUI2(Integer speedInt, Integer rpmInt) {
        gaugeSpeed.setValue(speedInt);
        gaugeViewSpeed.setText(speedInt + "");

        gaugeRPM.setValue(rpmInt);
        gaugeViewRPM.setText(rpmInt + "");
    }

    @Override
    public void showAllData(ArrayList<OBDData> data) {
        //TEMP - get the speed OBD Data
        OBDData speedOBD = data.get(0); //TODO Uncomment
        updateGauge(speedOBD);

        //Display all the data in the ListView
        //this.displayArrayAdapter.updateDataArray(data);
    }

    @Override
    public void updateGauge(OBDData obdData) {
        //Update the gauge with the appropriate numbers

        //Get only the number from the speed
        String speedStr = obdData.getData();
        String numberStr= speedStr.replaceAll("[^0-9]", "");
        final Integer speedInt = Integer.parseInt(numberStr);
        if(speedInt % 1000 == 0) {
            gaugeSpeed.setValue(speedInt);
            gaugeViewSpeed.setText(Integer.toString(gaugeSpeed.getValue()));
        }

        /*
        new Thread() {
            public void run() {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gaugeSpeed.setValue(speedInt);
                                gaugeViewSpeed.setText(Integer.toString(gaugeSpeed.getValue()));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }.start();
        */
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

        //get a reference to the mainActivity;
        final ConnectionHandler connHandler = this;

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
                //ASYNC METHOD
                connBTAsync = new ConnectBTAsync(commSocket, connHandler);
                connBTAsync.execute(device);
            }
        });

        //Display the list of paired Devices to the user
        alertDialog.setTitle("Select Bluetooth Device");
        alertDialog.show();

    }

    //DISCONNECT BUTTON
    public void disconnectBtnClick(View view){
        //Disconnect the Bluetooth by closing the opened socket
        Log.d(TAG, "MainActivity.disconnectBtnClick() called");
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
    }

    //--------------------TOAST----------------------
    public static void showToast(String message){
        //Displays a toast given a message
        MainActivity.toast.setText(message);
        MainActivity.toast.show();
    }

    public void testBtnClick(View view){
        Log.d(TAG, "MainActivity.testBtnClick()");

        //Start communicating
        OBDCommunicator obdConnection = new OBDCommunicator(this); //
        obdConnection.execute(this.commSocket);

    }
}

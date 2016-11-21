package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

//Gauge import
import pl.pawelkleczkowski.customgauge.CustomGauge;

//Otta - Android Event Bus

import com.squareup.otto.*;

//Floating menu
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

public class MainActivity
        extends AppCompatActivity
        implements ConnectionHandler {

    //************************VARIABLES************************
    //-----------Static-------------
    public static final String TAG = "MainActivity";

    //-----------Member-------------
    //BLUETOOTH MEMBERS
    public static final int REQUEST_ENABLE_BT = 8100;
    public ConnectBTAsync connBTAsync = null;
    public BluetoothSocket commSocket;

    //TOAST
    private static Toast toast;

    //GAUGES
    private CustomGauge gaugeSpeed;
    private TextView gaugeViewSpeed;
    private CustomGauge gaugeRPM;
    private TextView gaugeViewRPM;

    //Otto - TEMP
    private Bus bus;

    private boolean init = false;
    private BoomMenuButton boomMenuButton;

    //****************************METHODS******************************

    //-----------------------LIFECYCLE METHODS-----------------------
    //-----------on Create-------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load UI elements into member variables
        initUIElements();

        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);
    }

    //-----------on Destroy-------------
    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity.onDestroy() Called");

        //Close the Bluetooth Connection
        if (connBTAsync != null) {
            connBTAsync.closeSocket();
        }
        super.onDestroy();
    }

    //-----------------------UI ELEMENT HELPERS-----------------------
    //-----------Init UI elements-------------
    private void initUIElements() {
        //Load the UI elements from the resources into the private members of this class

        //Initialize the toast
        this.toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        //Init gauge elements
        this.gaugeSpeed = (CustomGauge) findViewById(R.id.gauge_speed);
        this.gaugeViewSpeed = (TextView) findViewById(R.id.gaugeView_speed);
        this.gaugeRPM = (CustomGauge) findViewById(R.id.gauge_rpm);
        this.gaugeViewRPM = (TextView) findViewById(R.id.gaugeView_rpm);

        //Init the bus
        this.bus = new Bus(ThreadEnforcer.MAIN);
        bus.register(this);
    }

    //-----------Update Gauges via Handler-------------
    @Override
    public void updateGauges(Integer speedInt, Integer rpmInt) {
        gaugeSpeed.setValue(speedInt);
        gaugeViewSpeed.setText(speedInt + "km/h");

        gaugeRPM.setValue(rpmInt);
        gaugeViewRPM.setText(rpmInt + "RPM");
    }

    //
    @Subscribe
    public void updateSpeedUI(Integer[] carData) {
        //-----------Updating Gauges via Otto-------------
        Integer speedInt = carData[0];
        Integer rpmInt = carData[1];

        gaugeSpeed.setValue(speedInt);
        gaugeViewSpeed.setText(speedInt + "km/h");

        gaugeRPM.setValue(rpmInt);
        gaugeViewRPM.setText(rpmInt + "RPM");
    }

    //-----------------------BLUETOOTH HELPERS-----------------------
    //-----------Is Bluetooth enabled-------------
    private void enableBluetooth() {
        //check if device supports bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //Device does not support bluetooth
            Log.d(TAG, "[MainActivity.enableBTBtnClick] Device does not support Bluetooth");
            return;
        }

        //Enabled bluetooth if it is not already enabled
        if (!mBluetoothAdapter.isEnabled()) {
            //Launch intent to ask to turn bluetooth on
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d(TAG, "[MainActivity.enableBTBtnClick] Bluetooth already enabled");
            //MainActivity.showToast("Bluetooth is already enabled");

            //Connect to a paired bluetooth device
            connectToPairedDevice();
        }
    }

    //-----------Connect Bluetooth to paired device-------------
    private void connectToPairedDevice() {
        //Connects the already enabled bluetooth to a paired device
        //The user is given a list of paired devices from which
        //they can click and select the device they wish to connec to

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
        if (pairedDevices.size() > 0) {
            //Iterate through all paired devices
            for (BluetoothDevice device : pairedDevices) {
                //Add the devices to the 3 respective arrays defined above
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devicesAddress.add(device.getAddress());
                devices.add(device);
            }
        } else {
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

    //-----------Bluetooth Handler-------------
    @Override
    public void handleBTConnection(BluetoothSocket mmSocket) {
        //Called after bluetooth has attempted to connect, either success or failure
        //This is called by the onPostExecute method after the ConnectBTAsync has finished doInBackground

        //Check if the connection is valid
        Boolean isConnected = mmSocket.isConnected();
        //Placeholder message to be displayed to user
        String toastMessage;

        //Let the user know that the connection was a success
        if (isConnected) {
            toastMessage = "Connection Success!";
            //--show/hide the buttons--
            startTripButtonDisplay();
        } else {
            toastMessage = "Unsuccessful Connection!";
        }

        //Show Log + Toast
        Log.d(TAG, "[MainActivity.handleBTConnection]" + toastMessage);
        MainActivity.showToast(toastMessage);

        //Start communicating with OBD Device
        OBDCommunicator obdConnection = new OBDCommunicator(this, this.bus);
        obdConnection.execute(mmSocket);
    }

    //-----------Disconnect Bluetooth-------------
    private void disconnectFromBluetooth() {
        if (connBTAsync != null) {
            //connBTThread.cancel();
            if (connBTAsync.closeSocket()) {
                //Show Success Toast
                MainActivity.showToast("Disconnect Successful!");
            } else {
                //Show UnSuccess Toast
                MainActivity.showToast("Disconnect Unsuccessful!");
            }
        }
    }

    //-----------------------INTENT RESULTS-----------------------
    @Override
    public void onActivityResult(int requestCode,
                                 int responseCode,
                                 Intent resultIntent) {

        //-----------After enabling bluetooth-------------
        //Response Intent once the Bluetooth has been enabled
        if (requestCode == REQUEST_ENABLE_BT && responseCode == RESULT_OK) {
            Log.d(TAG, "[MainActivity.onActivityResult] Bluetooth has been Enabled");
            MainActivity.showToast("Bluetooth Enabled");

            //Connect to a paired bluetooth device
            connectToPairedDevice();
        }
    }

    //-----------------------BUTTON CLICKS-----------------------
    //-----------start trip-------------
    public void startTripClick(View view) {
        String METHOD = "startTripClick";
        Log.d(METHOD, "called");

        //Check if bluetooth is enabled and Start communicating
        enableBluetooth();
    }

    //-----------end trip-------------
    public void endTripClick(View view) {
        String METHOD = "endTripClick";
        Log.d(METHOD, "called");

        //Disconnect the bluetooth (interrupts the
        disconnectFromBluetooth();

        endTripButtonDisplay();
    }

    //-----------------------MENU-----------------------

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

        subButton3Colors[0][1] = ContextCompat.getColor(this, R.color.md_amber_600);
        subButton3Colors[0][0] = Util.getInstance().getPressedColor(subButton3Colors[0][1]);

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.past), subButton1Colors[0], "Trips")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.where), subButton2Colors[0], "Locator")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.help), subButton3Colors[0], "Help")
                .button(ButtonType.CIRCLE)
                .boom(BoomType.HORIZONTAL_THROW_2)
                .place(PlaceType.CIRCLE_3_1)
                //.subButtonTextColor(Color.BLACK)
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {

                        Log.d(TAG, "button " + buttonIndex + " was clicked!");

                        switch (buttonIndex) {
                            case 0:
                                LaunchPastTripsActivity();
                                break;
                            case 1:
                                LaunchLocatorActivity();
                                break;
                            case 2:
                                LaunchHelpActivity();
                                break;
                            default:
                                Log.d(TAG, "There has been an error involving the subbuttons.");
                                break;
                        }
                    }
                })
                .init(boomMenuButton);
    }

    private void LaunchHelpActivity() {
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    private void LaunchLocatorActivity() {
        Intent intent = new Intent(MainActivity.this, LocatorActivity.class);
        startActivity(intent);
    }

    private void LaunchPastTripsActivity() {
        Intent intent = new Intent(MainActivity.this, DetailedStatsActivity.class);
        startActivity(intent);
    }

    //-----------------------UTILITY METHODS-----------------------
    //-----------Start Trip Button Display-------------
    private void startTripButtonDisplay() {
        //Hides/Shows the necessary buttons when the trip starts

        //Get the buttons
        Button startButton = (Button) findViewById(R.id.btn_Main_startTrip);
        Button endButton = (Button) findViewById(R.id.btn_Main_endTrip);

        //Show the End Trip Button
        startButton.setVisibility(View.GONE);

        //Show the End Trip Button
        endButton.setVisibility(View.VISIBLE);

    }

    //-----------End Trip Button Display-------------
    private void endTripButtonDisplay() {
        //Hides/Shows the necessary buttons when the trip ends
        //Get the buttons
        Button startButton = (Button) findViewById(R.id.btn_Main_startTrip);
        Button endButton = (Button) findViewById(R.id.btn_Main_endTrip);

        //Hide the End Trip button
        endButton.setVisibility(View.GONE);

        //Show the Start Trip Button
        startButton.setVisibility(View.VISIBLE);
    }

    //-----------Show a Toast-------------
    public static void showToast(String message) {
        //Displays a toast given a message
        MainActivity.toast.setText(message);
        MainActivity.toast.show();
    }

    //-----------------------DEBUGGING/TESTING-----------------------
    //-----------Enable Bluetooth Button-------------
    public void enableBTBtnClick(View view) {
        Log.d(TAG, "MainActivity.enableBTBtnClick() Called");
        enableBluetooth();
    }

    //-----------Connect Bluetooth Button-------------
    public void connectBtnClick(View view) {
        Log.d(TAG, "MainActivity.connectBtnClick() Called");
        connectToPairedDevice();
    }

    //-----------Disconnect Bluetooth Button-------------
    public void disconnectBtnClick(View view) {
        //Disconnect the Bluetooth by closing the opened socket
        Log.d(TAG, "MainActivity.disconnectBtnClick() called");
        disconnectFromBluetooth();
    }

    //-----------Test Button-------------
    public void testBtnClick(View view) {
        Log.d(TAG, "MainActivity.testBtnClick()");

        //Start communicating
        enableBluetooth();
        //OBDCommunicator obdConnection = new OBDCommunicator(this, this.bus);
        //obdConnection.execute(this.commSocket);

    }
}
package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.UUID;

import static com.luisa.alex.obd2_peek.MainActivity.TAG;

/**
 * Created by alex on 2016-10-16.
 */

//Connnect to a bluetooth device given its address
class ConnectBTAsync extends AsyncTask<BluetoothDevice, Void, Boolean> {
    private final static String BT_BOARD_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
    private Exception exception = null;
    private BluetoothAdapter mmAdapter;
    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;

    public ConnectBTAsync(BluetoothSocket socket){
        this.mmSocket = socket;

        //Get the Bluetooth adapter
        this.mmAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected Boolean doInBackground(BluetoothDevice... devices){
        Log.d(TAG, "ConnectBTAsync.doInBackground() called");
        //Returns true if the connection was successful, false otherwise

        //Obtain the Bluetooth Device object
        this.mmDevice = devices[0];
        Log.d(TAG, "[ConnectBTAsync.doInBackground] Passed address: " + this.mmDevice.getAddress());

        //Get the Bluetooth socket that will be used to communicate
        //mmSocket = null;
        try{
            //Get the UUID and create the Bluetooth Socket
            UUID uuid = UUID.fromString(BT_BOARD_UUID_STRING);
            mmSocket = this.mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        }catch(IOException e){
            Log.d(TAG, "[ConnectBTAsync.doInBackground] Unable to set up UUID!");
            e.printStackTrace();
            return false;
        }

        //Attempt to connect using the create socket above
        Log.d(TAG, "[ConnectBTAsync.doInBackground] Attempting to Connect...!");
        //Cancel device Discovery to minimize resource footprint
        this.mmAdapter.cancelDiscovery();
        try{
            //Attempt to open up the connection
            this.mmSocket.connect();
        }catch(IOException connectException){
            //Unable to connect, try to close and return
            Log.d(TAG, "[ConnectBTAsync.doInBackground] Unable to connect! Attempting to close socket...");
            try{
                mmSocket.close();
                Log.d(TAG, "[ConnectBTAsync.doInBackground] Socket closed!");
                return false;
            }catch(IOException closeException){
                Log.d(TAG, "[ConnectBTAsync.doInBackground] Unable to close the socket!");
                return false;
            }
        }
        Log.d(TAG, "[ConnectBTAsync.doInBackground] Successfully Connected!");
        //TODO Mange the connected socket;
        //ObdComThread obdComm = new ObdComThread(this.mmSocket);
        //obdComm.start();

        //TODO TEMP
        try {
            Log.d(TAG, "[ConnectBTAsync.doInBackground] Initializing OBD...");
            //init the OBD Device with the following configuration commands
            new EchoOffCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] EchoOffCommand Initialized!");
            new LineFeedOffCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] LineFeedOffCommand Initialized!");
            new TimeoutCommand(125).run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] TimeoutCommand Initialized!");
            new SelectProtocolCommand(ObdProtocols.AUTO).run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] SelectProtocolCommand Initialized!");
            new AmbientAirTemperatureCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] AmbientAirTemperatureCommand Initialized!");
            Log.d(TAG, "[ConnectBTAsync.doInBackground] Initialized OBD Device with configuration commands.");

            //Declare the commands
            SpeedCommand speedCommand = new SpeedCommand();
            RPMCommand rpmCommand = new RPMCommand();
            //ConsumptionRateCommand fuelCRCommand = new ConsumptionRateCommand();

            //Start communicating
            Log.d(TAG, "[ConnectBTAsync.doInBackground] Starting the communication stream:");
            //while (!Thread.currentThread().isInterrupted()) {
            while(true){
                //Log.d()
                speedCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                rpmCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                //fuelCRCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());

                //handle commands result
                String resultSpeed = "Speed: " + speedCommand.getFormattedResult() + " km/h";
                String resultRPM = "Throttle: " + rpmCommand.getFormattedResult() + " RPM";
                //String resultFuelCR = "Throttle: " + fuelCRCommand.getFormattedResult() + " ??";

                //Log the results
                Log.d(TAG, resultSpeed);
                Log.d(TAG, resultRPM);
                //Log.d(TAG, resultFuelCR);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "[ConnectBTAsync.doInBackground] Communication stream Ended!");
        this.closeSocket();


        return true;
    }

    @Override
    protected void onPostExecute(Boolean isConnected){
        //isConnected is true if the socket was successfully opened
        String toastMessage;
        if(this.exception != null){
            exception.printStackTrace();
        }
        //Let the user know that the connection was a success
        if(isConnected){
            toastMessage = "Connection Success!";
        }else {
            toastMessage= "Unsuccessful Connection!";
        }

        //Show Log + Toast
        Log.d(TAG, "[ConnectBTAsync.onPostExecute]" + toastMessage);
        MainActivity.showToast(toastMessage);
    }

    //Closes the socket
    public Boolean closeSocket() {
        //returns true if socket successfully closes
        try{
            mmSocket.close();
            Log.d(TAG, "[ConnectBTAsync.closeSocket] Socket closed!");
            return true;
        }catch(IOException closeException){
            Log.d(TAG, "[ConnectBTAsync.closeSocket] Unable to close the socket!");
            return false;
        }
    }


}


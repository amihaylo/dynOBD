package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

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

    public ConnectBTAsync(){
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
        mmSocket = null;
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

        //TODO Mange the connected socket;
        Log.d(TAG, "[ConnectBTAsync.doInBackground] Successfully Connected!");
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


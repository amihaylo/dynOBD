package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static com.luisa.alex.obd2_peek.MainActivity.TAG;

/**
 * Created by alex on 2016-10-16.
 *
 * CURRENTLY NOT IN USE, USING AS FALLBACK AND REFERENCE MATERIAL!
 *
 */

//TEMP CLASS - ATTEMPT 1
public class ConnectBTThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mmAdapter;

    //Constructor
    public ConnectBTThread(BluetoothDevice device){
        this.mmDevice = device;
        mmAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothSocket tmp = null;
        try{
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            tmp = this.mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);

        }catch(IOException e){}
        mmSocket = tmp;
    }


    public void run(){
        //Cancel device Discovery
        Log.d(TAG, "Thread: Attempting to Connect...!");
        this.mmAdapter.cancelDiscovery();
        try{
            this.mmSocket.connect();
        }catch(IOException connectException){
            //Unable to connect, try to close and return
            Log.d(TAG, "Unable to connect! Attemping to close socket...");
            try{
                mmSocket.close();
                Log.d(TAG, "Socket closed!");
                return;
            }catch(IOException closeException){
                Log.d(TAG, "Unable to close the socket!");
                return;
            }
        }

        Log.d(TAG, "Successfully Connected!");
        //TODO Mange the connected socket;
        ObdComThread obdComm = new ObdComThread(this.mmSocket);
        obdComm.start();
    }

    public Boolean closeSocket(){
        try{
            mmSocket.close();
            Log.d(TAG, "Socket closed!");
            return true;
        }catch(IOException closeException){
            Log.d(TAG, "Unable to close the socket!");
            return false;
        }
    }
}


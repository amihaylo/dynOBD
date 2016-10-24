package com.luisa.alex.obd2_peek;

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

import static com.luisa.alex.obd2_peek.MainActivity.TAG;

/**
 * Created by alex on 2016-10-22.
 */

public class OBDCommunicator extends AsyncTask<BluetoothSocket, String, Boolean> {
    BluetoothSocket mmSocket;
    ConnectionHandler connHandler;

    public OBDCommunicator(ConnectionHandler connHandler) {
        this.connHandler = connHandler;
    }

    @Override
    protected Boolean doInBackground(BluetoothSocket... sockets) {
        Log.d(TAG, "OBDCommunicator.doInBackground called()");
        //Obtain the socket
        this.mmSocket = sockets[0];

        //The communication
        try {
            Log.d(TAG, "[OBDCommunicator.doInBackground] Initializing OBD...");
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
            ConsumptionRateCommand fuelCRCommand = new ConsumptionRateCommand();

            //Start communicating
            Log.d(TAG, "[OBDCommunicator.doInBackground] Starting the communication stream:");
            //while (!Thread.currentThread().isInterrupted()) {
            while(this.mmSocket.isConnected()){
            //while(true){
                //Log.d()
                speedCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                rpmCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                fuelCRCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());

                //handle commands result
                String resultSpeed = speedCommand.getFormattedResult();
                String resultRPM = rpmCommand.getFormattedResult();
                String resultFuelCR = fuelCRCommand.getFormattedResult();

                //Log the results
                Log.d(TAG, resultSpeed);
                Log.d(TAG, resultRPM);
                Log.d(TAG, resultFuelCR);

                publishProgress(resultSpeed, resultRPM, resultFuelCR);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "[OBDCommunicator.doInBackground] Communication stream Ended!");

        return true;
    }

    protected void onProgressUpdate(String... carData) {
        String resultSpeed = carData[0];
        String resultRPM = carData[1];
        String resultFuelCR = carData[2];

        connHandler.updateUI(resultSpeed, resultRPM, resultFuelCR);
    }

    @Override
    protected void onPostExecute(Boolean bool){

    }
}

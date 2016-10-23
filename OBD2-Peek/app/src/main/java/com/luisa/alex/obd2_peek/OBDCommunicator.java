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

public class OBDCommunicator extends AsyncTask<BluetoothSocket, Void, Boolean> {
    BluetoothSocket mmSocket;
    ConnectionHandler connHandler;

    public OBDCommunicator(ConnectionHandler connHandler) {
        this.connHandler = connHandler;
    }

    @Override
    protected Boolean doInBackground(BluetoothSocket... sockets) {
        //Obtain the socket
        this.mmSocket = sockets[0];


//The communication
        try {
            Log.d(TAG, "[OBDCommunicator.doInBackground] Initializing OBD...");
            //init the OBD Device with the following configuration commands
            new EchoOffCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[OBDCommunicator.doInBackground] EchoOffCommand Initialized!");
            new LineFeedOffCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[OBDCommunicator.doInBackground] LineFeedOffCommand Initialized!");
            new TimeoutCommand(125).run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[OBDCommunicator.doInBackground] TimeoutCommand Initialized!");
            new SelectProtocolCommand(ObdProtocols.AUTO).run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[OBDCommunicator.doInBackground] SelectProtocolCommand Initialized!");
            new AmbientAirTemperatureCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[OBDCommunicator.doInBackground] AmbientAirTemperatureCommand Initialized!");
            Log.d(TAG, "[OBDCommunicator.doInBackground] Initialized OBD Device with configuration commands.");

            //Declare the commands
            SpeedCommand speedCommand = new SpeedCommand();
            RPMCommand rpmCommand = new RPMCommand();
            //ConsumptionRateCommand fuelCRCommand = new ConsumptionRateCommand();

            //Start communicating
            Log.d(TAG, "[OBDCommunicator.doInBackground] Starting the communication stream:");
            //while (!Thread.currentThread().isInterrupted()) {
            while(this.mmSocket.isConnected()){
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

                connHandler.updateUI(resultSpeed, resultRPM);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "[OBDCommunicator.doInBackground] Communication stream Ended!");

        return true;
    }

    @Override
    protected void onPostExecute(Boolean bool){

    }
}

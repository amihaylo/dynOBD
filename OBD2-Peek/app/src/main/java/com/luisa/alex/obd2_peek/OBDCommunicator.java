package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.github.pires.obd.commands.protocol.CloseCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.protocol.ObdProtocolCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.TemperatureCommand;


import java.util.ArrayList;
import java.util.List;

import static com.luisa.alex.obd2_peek.MainActivity.TAG;

/**
 * Created by alex on 2016-10-22.
 */

public class OBDCommunicator extends AsyncTask<BluetoothSocket, List<OBDData>, Boolean> {
    BluetoothSocket mmSocket;
    ConnectionHandler connHandler;
    private List<OBDData> dataList;

    public OBDCommunicator(ConnectionHandler connHandler) {

        this.connHandler = connHandler;
    }

    @Override
    protected Boolean doInBackground(BluetoothSocket... sockets) {
        dataList = new ArrayList<>();

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
            LoadCommand loadCommand = new LoadCommand();
            //ConsumptionRateCommand fuelCRCommand = new ConsumptionRateCommand();

            //Create OBDData objects to store the values
            dataList.add(new OBDData("Speed", "N/A"));
            dataList.add(new OBDData("RPM", "N/A"));
            dataList.add(new OBDData("Engine Load", "N/A"));

            //Start communicating
            Log.d(TAG, "[OBDCommunicator.doInBackground] Starting the communication stream:");
            //while (!Thread.currentThread().isInterrupted()) {
            while(this.mmSocket.isConnected()){
            //while(true){
                //Log.d()
                speedCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                rpmCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                loadCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());

                //handle commands result
                /*
                String resultSpeed = speedCommand.getFormattedResult();
                String resultRPM = rpmCommand.getFormattedResult();
                String resultEngineLoad = loadCommand.getFormattedResult();
                */

                dataList.get(0).setData(speedCommand.getFormattedResult());
                dataList.get(1).setData(rpmCommand.getFormattedResult());
                dataList.get(2).setData(loadCommand.getFormattedResult());

                //Display the result to the UI. Calls onProgressUpdate(String... carData)
                //publishProgress(resultSpeed, resultRPM, resultEngineLoad);
                publishProgress(dataList);

                //Log the results
                /*
                Log.d(TAG, resultSpeed);
                Log.d(TAG, resultRPM);
                Log.d(TAG, resultEngineLoad);
                */
            }
            Log.d(TAG, "[OBDCommunicator.doInBackground] Communication stream Ended!");

            //Once the socket is closed then we can close all the
            Log.d(TAG, "[OBDCommunicator.doInBackground] CloseCommand Called!");
            CloseCommand closeCommand = new CloseCommand();
            closeCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());

        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    protected void onProgressUpdate(List<OBDData>... carData) {

        connHandler.showData(carData[0]);
    }

    @Override
    protected void onPostExecute(Boolean bool){

    }
}

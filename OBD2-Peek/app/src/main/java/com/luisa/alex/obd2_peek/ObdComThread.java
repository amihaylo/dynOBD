package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;
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

import static com.luisa.alex.obd2_peek.MainActivity.TAG;

/**
 * Created by alex on 2016-10-17.
 */

public class ObdComThread extends Thread{
    private BluetoothSocket mmSocket;

    public ObdComThread(BluetoothSocket commSocket){
        this.mmSocket = commSocket;
    }

    //Temporary for debugging purposes
    public void run2(){
        //Check if the socket is connected
        if(this.mmSocket.isConnected()){
            Log.d(TAG, "[ObdComThread.run] mmSocket is CONNECTED");
        }else{
            Log.d(TAG, "[ObdComThread.run] mmSocket is NOT Connected");
        }
        this.cancel();
    }

    public void run() {
        Log.d(TAG, "ObdComThread.run() Called!");

        //Check if the socket is connected
        if(this.mmSocket.isConnected()){
            Log.d(TAG, "[ObdComThread.run] mmSocket is CONNECTED");
        }else{
            Log.d(TAG, "[ObdComThread.run] mmSocket is NOT Connected");
        }

        try {
            Log.d(TAG, "[ObdComThread.run] Initializing OBD...");
            //init the OBD Device with the following configuration commands
            new EchoOffCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ObdComThread.run] EchoOffCommand Initialized!");
            new LineFeedOffCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ObdComThread.run] LineFeedOffCommand Initialized!");
            new TimeoutCommand(125).run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ObdComThread.run] TimeoutCommand Initialized!");
            new SelectProtocolCommand(ObdProtocols.AUTO).run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ObdComThread.run] SelectProtocolCommand Initialized!");
            new AmbientAirTemperatureCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ObdComThread.run] AmbientAirTemperatureCommand Initialized!");
            Log.d(TAG, "[ObdComThread.run] Initialized OBD Device with configuration commands.");

            //Declare the commands
            SpeedCommand speedCommand = new SpeedCommand();
            RPMCommand rpmCommand = new RPMCommand();
            //ConsumptionRateCommand fuelCRCommand = new ConsumptionRateCommand();

            //Start communicating
            Log.d(TAG, "[ObdComThread.run] Starting the communication stream:");
            while (!Thread.currentThread().isInterrupted()) {
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
        Log.d(TAG, "[ObdComThread.run] Communication stream Ended!");
        this.cancel();
    }

    public void cancel() {
        try{
            this.mmSocket.close();
            mmSocket.close();
            Log.d(TAG, "[ObdComThread.cancel] Socket closed!");
            return;
        }catch(IOException closeException){
            Log.d(TAG, "[ObdComThread.cancel] Unable to close the socket!");
            return;
        }
    }
}

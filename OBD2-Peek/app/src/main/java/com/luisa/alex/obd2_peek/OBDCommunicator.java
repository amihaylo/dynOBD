package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.github.pires.obd.commands.control.DistanceSinceCCCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.pressure.FuelRailPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.CloseCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
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

            DistanceSinceCCCommand distanceCommand = new DistanceSinceCCCommand();
            VinCommand vinCommand = new VinCommand();
            AbsoluteLoadCommand absLoadCommand = new AbsoluteLoadCommand();
            MassAirFlowCommand massAirFlowCommand = new MassAirFlowCommand();
            OilTempCommand oilTempCommand = new OilTempCommand();
            RuntimeCommand runtimeCommand = new RuntimeCommand();
            ThrottlePositionCommand throttlePositionCommand = new ThrottlePositionCommand();
            AirFuelRatioCommand airFuelRatioCommand = new AirFuelRatioCommand();
            ConsumptionRateCommand consumptionRateCommand = new ConsumptionRateCommand();
            FindFuelTypeCommand findFuelTypeCommand = new FindFuelTypeCommand();
            FuelLevelCommand fuelLevelCommand = new FuelLevelCommand();
            BarometricPressureCommand barometricPressureCommand = new BarometricPressureCommand();
            FuelPressureCommand fuelPressureCommand = new FuelPressureCommand();
            FuelRailPressureCommand fuelRailPressureCommand= new FuelRailPressureCommand();
            IntakeManifoldPressureCommand intakeManifoldPressureCommand = new IntakeManifoldPressureCommand();
            AirIntakeTemperatureCommand airIntakeTemperatureCommand = new  AirIntakeTemperatureCommand();
            AmbientAirTemperatureCommand ambientAirTemperatureCommand = new AmbientAirTemperatureCommand();
            EngineCoolantTemperatureCommand engineCoolantTemperatureCommand = new EngineCoolantTemperatureCommand();

            //Create OBDData objects to store the values
            dataList.add(new OBDData("Speed", "N/A"));
            dataList.add(new OBDData("RPM", "N/A"));
            dataList.add(new OBDData("Engine Load Value (Calculated)", "N/A"));

            dataList.add(new OBDData("Distance traveled since codes cleared", "N/A")); //DistanceSinceCCCommand
            dataList.add(new OBDData("VIN", "N/A")); //VinCommand
            dataList.add(new OBDData("Engine Load Value (Absolute)", "N/A")); //AbsoluteLoadCommand
            dataList.add(new OBDData("MAF air flow rate", "N/A")); //MassAirFlowCommand
            dataList.add(new OBDData("Engine oil temperature", "N/A")); //OilTempCommand
            dataList.add(new OBDData("Run time since engine start", "N/A")); //RuntimeCommand
            dataList.add(new OBDData("Throttle position", "N/A")); //ThrottlePositionCommand
            dataList.add(new OBDData("Fuel–Air commanded equivalence ratio", "N/A")); //AirFuelRatioCommand
            dataList.add(new OBDData("Engine fuel rate", "N/A")); //ConsumptionRateCommand
            dataList.add(new OBDData("Fuel type", "N/A")); //FindFuelTypeCommand
            dataList.add(new OBDData("Fuel tank level output", "N/A")); //FuelLevelCommand
            dataList.add(new OBDData("Absolute barometric pressure", "N/A")); //BarometricPressureCommand
            dataList.add(new OBDData("Fuel pressure (gauge pressure)", "N/A")); //FuelPressureCommand
            dataList.add(new OBDData("Fuel rail pressure (diesel, or gasoline direct injection)", "N/A")); //FuelRailPressureCommand
            dataList.add(new OBDData("Intake manifold absolute pressure", "N/A")); //IntakeManifoldPressureCommand
            dataList.add(new OBDData("Intake air temperature", "N/A")); //AirIntakeTemperatureCommand
            dataList.add(new OBDData("Ambient air temperature", "N/A")); //AmbientAirTemperatureCommand
            dataList.add(new OBDData("Engine coolant temperature", "N/A")); //EngineCoolantTemperatureCommand

            //Start communicating
            Log.d(TAG, "[OBDCommunicator.doInBackground] Starting the communication stream:");
            //while (!Thread.currentThread().isInterrupted()) {
            while(this.mmSocket.isConnected()){
            //while(true){
                //Log.d()
                speedCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                rpmCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                loadCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                distanceCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                vinCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                absLoadCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                massAirFlowCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                oilTempCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                runtimeCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                throttlePositionCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                airFuelRatioCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                consumptionRateCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                findFuelTypeCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                fuelLevelCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                barometricPressureCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                fuelPressureCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                fuelRailPressureCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                intakeManifoldPressureCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                airIntakeTemperatureCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                ambientAirTemperatureCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                engineCoolantTemperatureCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());

                //handle commands result
                /*
                String resultSpeed = speedCommand.getFormattedResult();
                String resultRPM = rpmCommand.getFormattedResult();
                String resultEngineLoad = loadCommand.getFormattedResult();
                */

                dataList.get(0).setData(speedCommand.getFormattedResult());
                dataList.get(1).setData(rpmCommand.getFormattedResult());
                dataList.get(2).setData(loadCommand.getFormattedResult());

                dataList.get(3).setData(distanceCommand.getFormattedResult());
                dataList.get(4).setData(vinCommand.getFormattedResult());
                dataList.get(5).setData(absLoadCommand.getFormattedResult());
                dataList.get(6).setData(massAirFlowCommand.getFormattedResult());
                dataList.get(7).setData(oilTempCommand.getFormattedResult());
                dataList.get(8).setData(runtimeCommand.getFormattedResult());
                dataList.get(9).setData(throttlePositionCommand.getFormattedResult());
                dataList.get(10).setData(airFuelRatioCommand.getFormattedResult());
                dataList.get(11).setData(consumptionRateCommand.getFormattedResult());
                dataList.get(12).setData(findFuelTypeCommand.getFormattedResult());
                dataList.get(13).setData(fuelLevelCommand.getFormattedResult());
                dataList.get(14).setData(barometricPressureCommand.getFormattedResult());
                dataList.get(15).setData(fuelPressureCommand.getFormattedResult());
                dataList.get(16).setData(fuelRailPressureCommand.getFormattedResult());
                dataList.get(17).setData(intakeManifoldPressureCommand.getFormattedResult());
                dataList.get(18).setData(airIntakeTemperatureCommand.getFormattedResult());
                dataList.get(19).setData(ambientAirTemperatureCommand.getFormattedResult());
                dataList.get(20).setData(engineCoolantTemperatureCommand.getFormattedResult());

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

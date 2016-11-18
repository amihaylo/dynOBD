package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;
import java.util.Random;

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
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;


import java.util.ArrayList;

import static com.luisa.alex.obd2_peek.MainActivity.TAG;

/**
 * Created by alex on 2016-10-22.
 */

public class OBDCommunicator extends AsyncTask<BluetoothSocket, OBDData , Boolean> {
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
        //establishOBDComm();
        testCommunication();

        return true;
    }

    private void testCommunication() {
        Log.d("testCommunication", "called");

        Random rand = new Random();
        for (int i = 0; i < 1000000; i++){

            if(i % 100 == 0) {

                OBDData speedOBD = new OBDData("Speed",i + " km/h"); //Speed
                OBDData rpmOBD = new OBDData("RPM",rand.nextInt(1000) + ""); //RPM
                OBDData loadvalCalcOBD = new OBDData("Engine Load Value (Calculated)",rand.nextInt(1000) + ""); //LOAD
                OBDData distOBD = new OBDData("Distance traveled since codes cleared",rand.nextInt(1000) + ""); //DistanceSinceCCCommad
                OBDData vinOBD = new OBDData("VIN",rand.nextInt(1000) + ""); //VinCommad
                OBDData loadValAbsOBD = new OBDData("Engine Load Value (Absolute)",rand.nextInt(1000) + ""); //AbsoluteLoadCommad
                OBDData mafOBD = new OBDData("MAF air flow rate",rand.nextInt(1000) + ""); //MassAirFlowCommad
                OBDData oilTempOBD = new OBDData("Engine oil temperature",rand.nextInt(1000) + ""); //OilTempCommad
                OBDData runtimeOBD = new OBDData("Run time since engine start",rand.nextInt(1000) + ""); //RuntimeCommad
                OBDData throttlePosOBD = new OBDData("Throttle position",rand.nextInt(1000) + ""); //ThrottlePositionCommad
                OBDData fuelAirRatioOBD = new OBDData("Fuel–Air commanded equivalence ratio",rand.nextInt(1000) + ""); //AirFuelRatioCommad
                OBDData fuelRateOBD = new OBDData("Engine fuel rate",rand.nextInt(1000) + ""); //ConsumptionRateCommad
                OBDData fuelTypeOBD = new OBDData("Fuel type",rand.nextInt(1000) + ""); //FindFuelTypeCommad
                OBDData fuelLevelOBD = new OBDData("Fuel tank level output",rand.nextInt(1000) + ""); //FuelLevelCommad
                OBDData absBPOBD = new OBDData("Absolute barometric pressure",rand.nextInt(1000) + ""); //BarometricPressureCommad
                OBDData fuelPresOBD = new OBDData("Fuel pressure (gauge pressure)",rand.nextInt(1000) + ""); //FuelPressureCommad
                OBDData fuelRailOBD = new OBDData("Fuel rail pressure (diesel, or gasoline direct injection)",rand.nextInt(1000) + ""); //FuelRailPressureCommad
                OBDData intakeManOBD = new OBDData("Intake manifold absolute pressure",rand.nextInt(1000) + ""); //IntakeManifoldPressureCommad
                OBDData intakeAirOBD = new OBDData("Intake air temperature",rand.nextInt(1000) + ""); //AirIntakeTemperatureCommad
                OBDData ambOBD = new OBDData("Ambient air temperature",rand.nextInt(1000) + ""); //AmbientAirTemperatureCommad
                OBDData engCoolOBD = new OBDData("Engine coolant temperature",rand.nextInt(1000) + ""); //EngineCoolantTemperatureCommad


                //publishProgress(randomInt);
                publishProgress(speedOBD, rpmOBD, loadvalCalcOBD, distOBD, vinOBD, loadValAbsOBD, mafOBD, oilTempOBD, runtimeOBD, throttlePosOBD,
                                fuelAirRatioOBD, fuelRateOBD, fuelTypeOBD, fuelLevelOBD, absBPOBD, fuelPresOBD, fuelRailOBD, intakeManOBD, intakeAirOBD,
                                ambOBD, engCoolOBD);
            }
        }

    }

    //Runs the OBD commands from doInBackground
    private void establishOBDComm() {
        Log.d("establishOBDComm", "called()");
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

            //Start communicating
            Log.d(TAG, "[OBDCommunicator.doInBackground] Starting the communication stream:");
            //while (!Thread.currentThread().isInterrupted()) {
            while(this.mmSocket.isConnected()){
                //while(true){
                //Obtain all the commands
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

                //Create OBDData objects for each command
                OBDData speedOBD = new OBDData("Speed",speedCommand.getFormattedResult()); //Speed
                OBDData rpmOBD = new OBDData("RPM",rpmCommand.getFormattedResult()); //RPM
                OBDData loadvalCalcOBD = new OBDData("Engine Load Value (Calculated)",loadCommand.getFormattedResult()); //LOAD
                OBDData distOBD = new OBDData("Distance traveled since codes cleared",distanceCommand.getFormattedResult()); //DistanceSinceCCCommad
                OBDData vinOBD = new OBDData("VIN",vinCommand.getFormattedResult()); //VinCommad
                OBDData loadValAbsOBD = new OBDData("Engine Load Value (Absolute)",absLoadCommand.getFormattedResult()); //AbsoluteLoadCommad
                OBDData mafOBD = new OBDData("MAF air flow rate",massAirFlowCommand.getFormattedResult()); //MassAirFlowCommad
                OBDData oilTempOBD = new OBDData("Engine oil temperature",oilTempCommand.getFormattedResult()); //OilTempCommad
                OBDData runtimeOBD = new OBDData("Run time since engine start",runtimeCommand.getFormattedResult()); //RuntimeCommad
                OBDData throttlePosOBD = new OBDData("Throttle position",throttlePositionCommand.getFormattedResult()); //ThrottlePositionCommad
                OBDData fuelAirRatioOBD = new OBDData("Fuel–Air commanded equivalence ratio",airFuelRatioCommand.getFormattedResult()); //AirFuelRatioCommad
                OBDData fuelRateOBD = new OBDData("Engine fuel rate",consumptionRateCommand.getFormattedResult()); //ConsumptionRateCommad
                OBDData fuelTypeOBD = new OBDData("Fuel type",findFuelTypeCommand.getFormattedResult()); //FindFuelTypeCommad
                OBDData fuelLevelOBD = new OBDData("Fuel tank level output",fuelLevelCommand.getFormattedResult()); //FuelLevelCommad
                OBDData absBPOBD = new OBDData("Absolute barometric pressure",barometricPressureCommand.getFormattedResult()); //BarometricPressureCommad
                OBDData fuelPresOBD = new OBDData("Fuel pressure (gauge pressure)",fuelPressureCommand.getFormattedResult()); //FuelPressureCommad
                OBDData fuelRailOBD = new OBDData("Fuel rail pressure (diesel, or gasoline direct injection)",fuelRailPressureCommand.getFormattedResult()); //FuelRailPressureCommad
                OBDData intakeManOBD = new OBDData("Intake manifold absolute pressure",intakeManifoldPressureCommand.getFormattedResult()); //IntakeManifoldPressureCommad
                OBDData intakeAirOBD = new OBDData("Intake air temperature", airIntakeTemperatureCommand.getFormattedResult()); //AirIntakeTemperatureCommad
                OBDData ambOBD = new OBDData("Ambient air temperature",ambientAirTemperatureCommand.getFormattedResult()); //AmbientAirTemperatureCommad
                OBDData engCoolOBD = new OBDData("Engine coolant temperature",engineCoolantTemperatureCommand.getFormattedResult()); //EngineCoolantTemperatureCommad

                //Display the result to the UI. Calls onProgressUpdate(String... carData)
                publishProgress(speedOBD, rpmOBD, loadvalCalcOBD, distOBD, vinOBD, loadValAbsOBD, mafOBD, oilTempOBD, runtimeOBD, throttlePosOBD,
                        fuelAirRatioOBD, fuelRateOBD, fuelTypeOBD, fuelLevelOBD, absBPOBD, fuelPresOBD, fuelRailOBD, intakeManOBD, intakeAirOBD,
                        ambOBD, engCoolOBD);


                //-------DEBUG--------- TODO: Uncomment for debugging purposes

                String resultSpeed = speedCommand.getFormattedResult();
                String resultRPM = rpmCommand.getFormattedResult();
                String resultEngineLoad = loadCommand.getFormattedResult();

                //Log the results
                Log.d(TAG, resultSpeed);
                Log.d(TAG, resultRPM);
                Log.d(TAG, resultEngineLoad);

            }
            Log.d(TAG, "[OBDCommunicator.doInBackground] Communication stream Ended!");

            //Once the socket is closed then we can close all the
            Log.d(TAG, "[OBDCommunicator.doInBackground] CloseCommand Called!");
            CloseCommand closeCommand = new CloseCommand();
            closeCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void onProgressUpdate(OBDData[] carData) {
        //Display the data in the UI
        connHandler.showAllData(new ArrayList<>(Arrays.asList(carData)));
    }


    @Override
    protected void onPostExecute(Boolean bool){

    }
}

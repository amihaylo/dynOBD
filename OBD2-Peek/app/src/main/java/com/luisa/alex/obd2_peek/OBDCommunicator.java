package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.protocol.CloseCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;

import java.util.Date;
import java.util.Random;

import static com.luisa.alex.obd2_peek.MainActivity.TAG;

/**
 * Created by alex on 2016-10-22.
 */

public class OBDCommunicator extends AsyncTask<BluetoothSocket, Integer , Trip> {
    private BluetoothSocket mmSocket;
    private ConnectionHandler connHandler;
    private Boolean queryForVin;
    private String vinNumber;

    //Used in Simulation
    final Integer MAX_SPEED = 200; //km/h
    final Integer MAX_RPM = 6000;
    final Integer SPEED_THRESHOLD = Math.round(new Float(MAX_SPEED * 0.1));
    final Integer RPM_THRESHOLD = Math.round(new Float(MAX_RPM * 0.1));
    Boolean accelerating = true;


    //---------------CONSTRUCTOR--------------
    public OBDCommunicator(ConnectionHandler connHandler, Boolean queryForVin) {
        this.connHandler = connHandler;

        //If queryForVin is TRUE then doInBackground will simply query the obd device
        //For some data and then exit without entering a whileloop stream
        this.queryForVin = queryForVin;

        //Initialize Vin to empty;
        this.vinNumber = "";
    }

    //---------------DO IN BACKGROUND--------------
    @Override
    protected Trip doInBackground(BluetoothSocket... sockets) {
        Log.d(TAG, "OBDCommunicator.doInBackground called()");
        //Obtain the socket
        this.mmSocket = sockets[0];

        //The communication
        //establishOBDComm();
        Trip tripMissingId = testCommunication();

        return tripMissingId;
    }

    //---------------TEST COMMUNICATION--------------
    private Trip testCommunication() {
        String METHOD = "testCommunication";
        //Log.d(METHOD, "called()");
        if(this.mmSocket == null){Log.d(METHOD, "mmsocket = null"); return null;}

        //SIMULATION DUMMY DATA
        String secretVinNumber = "KMHHxxxDx3Uxxxxxx";
        Integer simDataStream[] = {0,0};

        //Query the OBD for the Vin Data ONLY
        //-----------VIN QUERY----------
        //We only query for specific data and then exit
        if(this.mmSocket.isConnected() && this.queryForVin){
            //Obtain the vin
            String vinNumber = secretVinNumber;

            //Save the vin
            this.vinNumber = vinNumber.trim();

            //exit
            return null;
        }

            //Not querying for vin - Run actual data communication

        //-----------ACTUAL COMMUNICATION----------
        //Data to be collected for the Trip:
        String date = new Date().toString();
        Long duration = new Long(0);
        String origin = "";
        String destination = "";
        Integer maxSpeed = 0;
        Integer maxRPM = 0;

        while(this.mmSocket.isConnected()) {
            try {
                //Log.d("testCommunication", "i = " + i);
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            simulateData(simDataStream);

            //Update the UI with the speed and rpm values
            //publishProgress(i * 2, i * 60);
            publishProgress(simDataStream[0], simDataStream[1]);
        }

        //Dummy Data
        date = new Date().toString();
        duration = new Long(1400);
        origin = "Toronto, Canada";
        destination = "Tokyo, Japan";
        maxSpeed = 150;
        maxRPM = 4000;


        Trip tripMissingId = new Trip(date, duration, origin, destination, maxSpeed, maxRPM);
        return tripMissingId;
    }

    //---------------SIMULATE DATA--------------
    //Simulates a stream of data including the speed and RPM
    private void simulateData(Integer[] oldDataStream){
        String METHOD = "simulateData";
        //Log.d(METHOD, "called");

        //Obtain the old data
        Integer speed = oldDataStream[0];
        Integer rpm = oldDataStream[1];

        //Accelerate to roughly halfway
        if(speed < MAX_SPEED/2 - SPEED_THRESHOLD) {
            speed = (speed + 2) % MAX_SPEED;
            rpm = (rpm + 70) % MAX_RPM;
        }else{
            //fluctuate the speed between the threshold
            Random rand = new Random();
            Boolean randBool = rand.nextBoolean();

            //The random chance of increasing or decreasing the speed
            int increaseChance = 1;
            int decreaseChance = 1;
            if(accelerating && (speed < (MAX_SPEED/2 + SPEED_THRESHOLD))){
                //Log.d(METHOD, "Accelerating");
                increaseChance = 2; //give advantage
                decreaseChance = 1;
            }else if(accelerating && (speed >= (MAX_SPEED/2 + SPEED_THRESHOLD))){
                //Log.d(METHOD, "Setting accelerating = false");
                accelerating = false;
            }else if(!accelerating && (speed > (MAX_SPEED/2 - SPEED_THRESHOLD))){
                //Log.d(METHOD, "Decelerating");
                increaseChance = 1;
                decreaseChance = 2; //give advantage
            }else if(!accelerating && (speed <= (MAX_SPEED/2 - SPEED_THRESHOLD))){
                //Log.d(METHOD, "Setting accelerating = true");
                accelerating = true;
            }else{
                //Log.d(METHOD, "No conditions met!");
            }

            //Random increase or decrease the speed
            if(randBool){
                speed += rand.nextInt(increaseChance);
                rpm  += rand.nextInt(increaseChance * 10);
            }else{
                speed -= rand.nextInt(decreaseChance);
                rpm  -= rand.nextInt(decreaseChance * 10);
            }
        }


        //Set the new speed
        oldDataStream[0] = speed;
        oldDataStream[1] = rpm;
    }


    //---------------OBD CAR COMMUNICATION--------------
    //Runs the OBD commands from doInBackground
    private void establishOBDComm() {
        String METHOD = "establishOBDComm";
        Log.d(METHOD, "called");
        try {
            Log.d(TAG, "[OBDCommunicator.doInBackground] Initializing OBD...");
            //init the OBD Device with the following configuration commands
            new EchoOffCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] EchoOffCommand Initialized!");
            new LineFeedOffCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] LineFeedOffCommand Initialized!");
            new TimeoutCommand(125).run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] TimeoutCommand Initialized!");
            new SelectProtocolCommand(ObdProtocols.AUTO).run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] SelectProtocolCommand Initialized!");
            //new AmbientAirTemperatureCommand().run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream()); Log.d(TAG, "[ConnectBTAsync.doInBackground] AmbientAirTemperatureCommand Initialized!");

            Log.d(TAG, "[ConnectBTAsync.doInBackground] Initialized OBD Device with configuration commands.");

            //Check if Vin is being Queried - Query the OBD for the Vin Data ONLY
            if(this.mmSocket.isConnected() && this.queryForVin) {
                //We only query for specific data and then exit
                //Declare the vin Command
                VinCommand vinCommand = new VinCommand();
                //Run it
                vinCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                //Obtain the vin
                String vinNumber = vinCommand.getFormattedResult();

                //Save the vin
                //Truncate to the first 17 since there is a weird 18th character
                this.vinNumber = vinNumber.substring(0, 17);
                Log.d(METHOD, "Obtained vin = |" + vinNumber + "| of size " + this.vinNumber.length());
                //Log.d(METHOD, vinNumber);
                //Log.d(METHOD, vinCommand.getCalculatedResult());

                //exit
                return;
            }

            //Proceed with regular communication


            //Declare the commands
            SpeedCommand speedCommand = new SpeedCommand();
            RPMCommand rpmCommand = new RPMCommand();
            /*
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
            */

            //Start communicating
            Log.d(TAG, "[OBDCommunicator.doInBackground] Starting the communication stream:");
            //while (!Thread.currentThread().isInterrupted()) {
            while(this.mmSocket.isConnected()){
                //while(true){
                //Obtain all the commands
                speedCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                rpmCommand.run(this.mmSocket.getInputStream(), this.mmSocket.getOutputStream());
                /*
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
                */

                //Create OBDData objects for each command
                //OBDData speedOBD = new OBDData("Speed",speedCommand.getFormattedResult()); //Speed
                //OBDData rpmOBD = new OBDData("RPM",rpmCommand.getFormattedResult()); //RPM

                /*
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
                */

                //Display the result to the UI. Calls onProgressUpdate(String... carData)
                //Convert the speed and rpm into Integers
                publishProgress(speedCommand.getMetricSpeed(), rpmCommand.getRPM());


                //-------DEBUG--------- TODO: Uncomment for debugging purposes
                /*
                String resultSpeed = speedCommand.getFormattedResult();
                String resultRPM = rpmCommand.getFormattedResult();

                //Log the results
                Log.d(TAG, resultSpeed);
                Log.d(TAG, resultRPM);
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
    }

    //---------------ON PROGRESS UPDATE--------------
    protected void onProgressUpdate(Integer[] carData) {
        //[0]=Speed [1]=RPM
        connHandler.updateGauges(carData[0], carData[1]);
    }


    //---------------ON POST EXECUTE--------------
    @Override
    protected void onPostExecute(Trip tripMissingId) {
        String METHOD = "onPostExecute";
        if(this.queryForVin){
            //Send the vin back to the user
            connHandler.handleVin(this.vinNumber);
        }else{
            //set speed and rpm fields back to 0
            connHandler.resetGauges();

            //Trip has ended - prompt the user to save the trip data
            connHandler.saveTripAlert(tripMissingId);
        }
    }
}

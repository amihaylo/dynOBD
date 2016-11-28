package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.luisa.alex.obd2_peek.MainActivity.TAG;

/**
 * Created by alex on 2016-10-22.
 */

public class OBDCommunicator extends AsyncTask<BluetoothSocket, Integer , Trip> {
    private BluetoothSocket mmSocket;
    private ConnectionHandler connHandler;
    private FragmentActivity activity;
    private LocationListener locationListener;
    private Boolean queryForVin;
    private String vinNumber;
    private Trip trip;
    private Date startDate;
    private Date endDate;

    //Used in Simulation
    private final Integer MAX_SPEED = 200; //km/h
    private final Integer MAX_RPM = 6000;
    private final Integer SPEED_THRESHOLD = Math.round(new Float(MAX_SPEED * 0.1));
    private final Integer RPM_THRESHOLD = Math.round(new Float(MAX_RPM * 0.1));
    private Boolean accelerating = true;
    private Boolean simulateTrip = true;



    //---------------CONSTRUCTOR--------------
    public OBDCommunicator(ConnectionHandler connHandler,
                           FragmentActivity activity,
                           LocationListener locationListener,
                           Boolean queryForVin,
                           Boolean simulateTrip) {
        //Set the Handler, activity and location Listener
        this.connHandler = connHandler;
        this.activity = activity;
        this.locationListener = locationListener;

        //If simulateTrip is true then dummy data is used instead of real one
        this.simulateTrip = simulateTrip;

        //If queryForVin is TRUE then doInBackground will simply query the obd device
        //For some data and then exit without entering a whileloop stream
        this.queryForVin = queryForVin;

        //Initialize Vin to empty;
        this.vinNumber = "";

        //If not a vin query, initialize the trip
        if(!queryForVin){
            initTrip();
        }
    }

    private void initTrip(){
        //Get the date
        this.startDate = new Date();
        SimpleDateFormat dfDate = new SimpleDateFormat("EEE, MMM d yyyy");
        SimpleDateFormat dfTime = new SimpleDateFormat("h:mm a");
        String date = dfDate.format(this.startDate);
        String departureTime = dfTime.format(this.startDate);

        

        //Get the origin
        String origin = "N/A";
        Location currLocation = LocationHelper.getLastLocation(this.activity, this.locationListener);
        Address address = LocationHelper.locToAddress(this.activity, currLocation);
        if(address != null) {
            origin = address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2);
        }

        this.trip = new Trip(date, new Long(0), origin, departureTime, "N/A","N/A" , 0 ,0);
    }

    //---------------DO IN BACKGROUND--------------
    @Override
    protected Trip doInBackground(BluetoothSocket... sockets) {
        Log.d(TAG, "OBDCommunicator.doInBackground called()");
        //Obtain the socket
        this.mmSocket = sockets[0];

        //The communication
        if(this.simulateTrip){
            return testCommunication();
        }else{
            return establishOBDComm();
        }
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
        this.trip.setDuration(new Long(1400));
        this.trip.setMaxSpeed(150);
        this.trip.setMaxRPM(4000);

        return this.trip;
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
    private Trip establishOBDComm() {
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
                return null;
            }

            //----------------ACTUAL COMMUNICATION-------------
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


                //Display the result to the UI. Calls onProgressUpdate(String... carData)
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

            //Dummy Data
            this.trip.setDuration((new Date()).getTime() - this.startDate.getTime());
            this.trip.setMaxSpeed(150);
            this.trip.setMaxRPM(4000);

        }catch(Exception e){
            e.printStackTrace();
        }
        return this.trip;
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

            //set the Arrival Time
            SimpleDateFormat dfTime = new SimpleDateFormat("h:mm a");
            tripMissingId.setTimeArrival(dfTime.format(new Date()));

            //Set the destination
            String destination = "N/A";
            Location currLocation = LocationHelper.getLastLocation(this.activity, this.locationListener);
            Address address = LocationHelper.locToAddress(this.activity, currLocation);
            if(address != null) {
                destination = address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2);
            }
            tripMissingId.setDestination(destination);


            Log.d(METHOD, tripMissingId.toString());
            //Trip has ended - prompt the user to save the trip data
            connHandler.saveTripAlert(tripMissingId);
        }
    }
}

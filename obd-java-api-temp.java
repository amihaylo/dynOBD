
//The communication
try {
    Log.d(TAG, "[ConnectBTAsync.doInBackground] Initializing OBD...");
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
    //ConsumptionRateCommand fuelCRCommand = new ConsumptionRateCommand();

    //Start communicating
    Log.d(TAG, "[ConnectBTAsync.doInBackground] Starting the communication stream:");
    //while (!Thread.currentThread().isInterrupted()) {
    while(true){
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

    }
}catch(Exception e){
    e.printStackTrace();
}
Log.d(TAG, "[ConnectBTAsync.doInBackground] Communication stream Ended!");
this.closeSocket();

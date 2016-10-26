#CSCI 4100 Final Project
##Due: Monday, November 28, 2016
###Members:
* Luisa Rojas
* Alexandar Mihaylov

#Idea 1: OBD2 Car App
* Using [OBD2](https://en.wikipedia.org/wiki/On-board_diagnostics#OBD-II) we aim to read a statistics from a car and incorporate them into an android application.

##Specifics
* **Car Make**: 2003 Hyundai Elantra
* **OBDII Protocol**: ISO 14230-4 
* **ISO 14230-4 Supported by**: ELM323, ELM327, ELM327/L
* **OBD Device**: OBD2 ELM327 Bluetooth KW901/KW903 Car Auto Diagnostic Interface Scanner Tool KONNWEI
* **OBD2 Simulator**: [ScanTool 602201 ECUsim 2000 ECU CAN Simulator for OBD-II Development](https://www.amazon.com/ScanTool-602201-ECUsim-Simulator-Development/dp/B008NAH6WE)

##Useful Links:
* [Hyundai OBD II diagnostic interface pinout](http://pinoutsguide.com/CarElectronics/hyundai_obd_2_pinout.shtml)
* [ELM protocol support](https://www.elmelectronics.com/products/ics/obd/)
* [ELM327 AT Commands](https://www.sparkfun.com/datasheets/Widgets/ELM327_AT_Commands.pdf)
* [OBDII Commands](https://en.wikipedia.org/wiki/OBD-II_PIDs)
* [Android Bluetooth](https://developer.android.com/guide/topics/connectivity/bluetooth.html)
* [OBDII Android Tutorial](http://blog.lemberg.co.uk/how-guide-obdii-reader-app-development)
* [ODBII Java api](https://github.com/pires/obd-java-api)
* [Android OBD Reader](https://github.com/pires/android-obd-reader)
* [OBD Sim](http://icculus.org/obdgpslogger/obdsim.html)
* [OBD + GPS application](http://icculus.org/obdgpslogger/)


#Problems/Fixes
* [Bluetooth Fails to Connect](http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/18786701details)
* [Constant output issue](https://github.com/pires/obd-java-api/issues/98)

#Re-connection to bluetooth issue

	       MainActivity.disconnectBtnClick() called
	       10-24 09:16:32.237 480-1191/com.luisa.alex.obd2_peek W/System.err: java.io.IOException: bt socket closed, read return: -1
	       10-24 09:16:32.237 480-480/com.luisa.alex.obd2_peek D/MainActivity: [ConnectBTAsync.closeSocket] Socket closed!
	       10-24 09:16:32.237 480-1191/com.luisa.alex.obd2_peek W/System.err:     at android.bluetooth.BluetoothSocket.read(BluetoothSocket.java:500)
	       10-24 09:16:32.247 480-1191/com.luisa.alex.obd2_peek W/System.err:     at android.bluetooth.BluetoothInputStream.read(BluetoothInputStream.java:60)
	       10-24 09:16:32.247 480-1191/com.luisa.alex.obd2_peek W/System.err:     at com.github.pires.obd.commands.ObdCommand.readRawData(ObdCommand.java:175)
	       10-24 09:16:32.247 480-1191/com.luisa.alex.obd2_peek W/System.err:     at com.github.pires.obd.commands.ObdCommand.readResult(ObdCommand.java:127)
	       10-24 09:16:32.267 480-1191/com.luisa.alex.obd2_peek W/System.err:     at com.github.pires.obd.commands.ObdCommand.run(ObdCommand.java:77)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek W/System.err:     at com.luisa.alex.obd2_peek.OBDCommunicator.doInBackground(OBDCommunicator.java:60)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek W/System.err:     at com.luisa.alex.obd2_peek.OBDCommunicator.doInBackground(OBDCommunicator.java:23)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek W/System.err:     at android.os.AsyncTask$2.call(AsyncTask.java:288)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek W/System.err:     at java.util.concurrent.FutureTask.run(FutureTask.java:237)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek W/System.err:     at android.os.AsyncTask$SerialExecutor$1.run(AsyncTask.java:231)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek W/System.err:     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1112)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek W/System.err:     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:587)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek W/System.err:     at java.lang.Thread.run(Thread.java:841)
	       10-24 09:16:32.277 480-1191/com.luisa.alex.obd2_peek D/MainActivity: [OBDCommunicator.doInBackground] Communication stream Ended!

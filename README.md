CSCI 4100 Final Project
-----------------------------

##Due: Monday, November 28, 2016
###Members:
* Luisa Rojas
* Alexandar Mihaylov

Idea 1: OBD2 Car App
-----------------------------
* Using [OBD2](https://en.wikipedia.org/wiki/On-board_diagnostics#OBD-II) we aim to read a statistics from a car and incorporate them into an android application.

Specifics
-----------------------------
* **Car Make**: 2003 Hyundai Elantra
* **OBDII Protocol**: ISO 14230-4 
* **ISO 14230-4 Supported by**: ELM323, ELM327, ELM327/L
* **OBD Device**: OBD2 ELM327 Bluetooth KW901/KW903 Car Auto Diagnostic Interface Scanner Tool KONNWEI
* **OBD2 Simulator**: [ScanTool 602201 ECUsim 2000 ECU CAN Simulator for OBD-II Development](https://www.amazon.com/ScanTool-602201-ECUsim-Simulator-Development/dp/B008NAH6WE)

Useful Links:
-----------------------------
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


Problems/Fixes
-----------------------------
* [Bluetooth Fails to Connect](http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/18786701details)
* [Constant output issue](https://github.com/pires/obd-java-api/issues/98)

OBD-II PIDs and Java OBD Api
--------------------------------




Command Type | Mode | PID(hex) | OBD-Java-Api | Definition
-------------|------|----------|--------------|-----------
control | 01 | 21 | DistanceMILOnCommand.java | N/A
control | 01 | 31 | DistanceSinceCCCommand.java | N/A
control | 01 | 01 | DtcNumberCommand.java | N/A
control | 01 | 44 | EquivalentRatioCommand.java | N/A
control | AT | IGN | IgnitionMonitorCommand.java | N/A
control | 01 | 42 | ModuleVoltageCommand.java | N/A
control | -- | 07 | PendingTroubleCodesCommand.java | N/A
control | -- | 0A | PermanentTroubleCodesCommand.java | N/A
control | 01 | 0E | TimingAdvanceCommand.java | N/A
control | -- | 03 | TroubleCodesCommand.java | N/A
control | 09 | 02 | VinCommand.java | N/A

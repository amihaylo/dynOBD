package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2016-10-22.
 */

public interface ConnectionHandler {
    void handleBTConnection(BluetoothSocket mmSocket);
    void updateUI(OBDData speedOBD, OBDData rpmOBD);
    void updateUI2(Integer speedInt, Integer rpmInt);
    void showAllData(ArrayList<OBDData> data);
    void updateGauge(OBDData obdData);
}

package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;

import java.util.List;

/**
 * Created by alex on 2016-10-22.
 */

public interface ConnectionHandler {
    public void handleBTConnection(BluetoothSocket mmSocket);
    public void updateUI(String speed, String rpm, String fuelCR);
    public void showData(List<OBDData> data);
}

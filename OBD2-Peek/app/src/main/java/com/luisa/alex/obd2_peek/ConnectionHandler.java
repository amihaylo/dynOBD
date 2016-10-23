package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;

/**
 * Created by alex on 2016-10-22.
 */

public interface ConnectionHandler {
    public void handleBTConnection(BluetoothSocket mmSocket);
}

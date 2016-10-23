package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;

/**
 * Created by alex on 2016-10-16.
 */

public interface BluetoothSocketHandler {
    void handleSocket(BluetoothSocket socket);
}

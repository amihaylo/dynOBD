package com.luisa.alex.obd2_peek;

import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2016-10-22.
 */

public interface ConnectionHandler {
    void handleBTConnection(BluetoothSocket mmSocket);
    void updateGauges(Integer speedInt, Integer rpmInt);
}

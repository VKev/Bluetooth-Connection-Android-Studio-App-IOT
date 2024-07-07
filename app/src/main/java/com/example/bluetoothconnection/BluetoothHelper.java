package com.example.bluetoothconnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothHelper {

    private static final String TAG = "BluetoothHelper";

    public List<BluetoothDevice> getPairedDevices(BluetoothAdapter btAdapter) {
        List<BluetoothDevice> pairedDevicesList = new ArrayList<>();

        if (btAdapter == null) {
            Log.e(TAG, "BluetoothAdapter is null. Initialization error.");
            return pairedDevicesList; // Return empty list if adapter is null
        }

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesList.add(device);
                Log.d(TAG, "Paired Device: " + device.getName() + " - " + device.getAddress());
            }
        } else {
            Log.d(TAG, "No paired devices found.");
        }

        return pairedDevicesList;
    }
}


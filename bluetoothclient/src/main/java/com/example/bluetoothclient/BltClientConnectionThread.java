package com.example.bluetoothclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

public class BltClientConnectionThread extends Thread {
    
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final  UUID MY_UUID;
 
    public BltClientConnectionThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        MY_UUID = UUID.fromString("2da12563-e314-1211-7789-" + device.getAddress().replace(":", ""));

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            cancel();
            return;
        }

        // Do work to manage the connection (in a separate thread)
        BltConnectionThread bltConnectionThread = new BltConnectionThread(mmSocket);
        bltConnectionThread.start();
        EventBus.getDefault().post(bltConnectionThread);
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
        finally {

        }
    }

}

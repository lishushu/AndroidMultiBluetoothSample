package com.example.jokerlee.bluetoothsample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by jokerlee on 16-4-19.
 */
public class BltServerSocketThread extends Thread{


    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBluetoothAdapter;
    private final  UUID MY_UUID;

    public BltServerSocketThread(BluetoothAdapter bluetoothAdapter) {

        mBluetoothAdapter = bluetoothAdapter;
        MY_UUID = UUID.fromString("2da12563-e314-1211-7789-" + mBluetoothAdapter.getAddress().replace(":", ""));

        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            //get the server socket listenning the client's connection requests.
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("MyBLTServer", MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                /*Start listening for connection requests by calling accept().
                 *This is a blocking call. A connection is accepted only when a remote device has sent
                 * a connection request with a UUID matching the one registered with this listening server socket.
                 * When successful, accept() will return a connected BluetoothSocket.
                 */
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                System.out.println("Unable to accept; close the socket and get out");
                cancel();
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                BltConnectionThread bltConnectionThread = new BltConnectionThread(socket);
                bltConnectionThread.start();
                EventBus.getDefault().post(bltConnectionThread);
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}

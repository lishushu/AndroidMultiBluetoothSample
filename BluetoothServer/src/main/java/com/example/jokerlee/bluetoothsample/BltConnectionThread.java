package com.example.jokerlee.bluetoothsample;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by jokerlee on 16-4-20.
 */
public class BltConnectionThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public BltConnectionThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    /**
     * get the connected blt's mac address
     * @return
     */
    public String getClientBltMac(){
        return  mmSocket.getRemoteDevice().getAddress();
    }
    
    /**
     * get the connected blt's name
     * @return
     */
    public String getClientBltName(){
        return  mmSocket.getRemoteDevice().getName();
    }
    
    /**
     * get the blt's rssi
     * @return
     */
    public String getClientBltRssi(){
        return  "";
    }
    
    /**
     * get the blt's bondStatus
     * @return
     */
    public String getClientBltBondState() {
        switch (mmSocket.getRemoteDevice().getBondState()) {
        case BluetoothDevice.BOND_BONDED:
            return "Paired";
        case BluetoothDevice.BOND_BONDING:
            return "Bonding";
        case BluetoothDevice.BOND_NONE:
            return "New Device";
        }
        return "New Device";
    }

    public boolean isConnected(){
        return  mmSocket.isConnected();
    }

    public void run() {

        int bufferSize = 1024;
        int bytesRead = -1;
        byte[] buffer = new byte[bufferSize];

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                final StringBuilder sb = new StringBuilder();
                // Read from the InputStream
                bytesRead = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                if (bytesRead != -1) {
                    String result = "";
                    while ((bytesRead == bufferSize) && (buffer[bufferSize] != 0)) {
                        result = result + new String(buffer, 0, bytesRead);
                        bytesRead = mmInStream.read(buffer);
                    }
                    result = result + new String(buffer, 0, bytesRead);
                    sb.append(result);
                }
                EventBus.getDefault().post(new BluetoothCommunicator(sb.toString()));
            } catch (IOException e) {
                cancel();
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {

        }
        finally {
            EventBus.getDefault().post(new BltDisconnectMSG(this));
        }
    }

}

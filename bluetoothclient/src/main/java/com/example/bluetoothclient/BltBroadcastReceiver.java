package com.example.bluetoothclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jokerlee on 16-4-20.
 */
public class BltBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
            BLTInfo bltInfo = new BLTInfo(device,intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
            EventBus.getDefault().post(bltInfo);
        } else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            Toast.makeText(context,"Blt scan compeleted.", Toast.LENGTH_SHORT).show();
        } else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
            Toast.makeText(context,"Start scan bluetooth......", Toast.LENGTH_SHORT).show();
        }
    }

}

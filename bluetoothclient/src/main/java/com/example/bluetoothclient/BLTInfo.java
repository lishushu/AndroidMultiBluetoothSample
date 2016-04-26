package com.example.bluetoothclient;

import android.bluetooth.BluetoothDevice;

/**
 * Created by jokerlee on 16-4-20.
 */
public class BLTInfo {

    private final int mRSSI ;
    private final BluetoothDevice mBluetoothDevice;

    public BLTInfo(BluetoothDevice device, int rssi){
        mBluetoothDevice = device;
        mRSSI = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof BLTInfo ){
            BLTInfo bltInfo = (BLTInfo) o;
            return  bltInfo.getmBluetoothDeviceMac().equals(mBluetoothDevice.getAddress());
        }
        return super.equals(o);
    }
    
    public BluetoothDevice getRemoteDevice(){
        return mBluetoothDevice;
    }

    public String getRSSI() {
        return mRSSI + "";
    }

    public String getmBluetoothDeviceName() {
        return mBluetoothDevice.getName();
    }

    public String getmBluetoothDeviceMac() {
        return mBluetoothDevice.getAddress();
    }

    public String getmBluetoothDeviceBondStatus() {
        switch (mBluetoothDevice.getBondState() ) {
            case BluetoothDevice.BOND_BONDED:
                return "Paired";
            case BluetoothDevice.BOND_BONDING:
                return "Bonding";
            case BluetoothDevice.BOND_NONE:
                return "New Device";
        }
        return "New Device";
    }
}

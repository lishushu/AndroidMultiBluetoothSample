package com.example.jokerlee.bluetoothsample;

/**
 * Created by jokerlee on 16-4-25.
 */
public class BltDisconnectMSG {

    private BltConnectionThread mBltThread;

    public BltDisconnectMSG(BltConnectionThread thread){
        mBltThread = thread;
    }

    public BltConnectionThread getDisconnectThread() {
        return mBltThread;
    }
}

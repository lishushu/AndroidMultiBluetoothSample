package com.example.bluetoothclient;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Bind(R.id.start_blt_scan) Button startBltServer;
    
    @Bind(R.id.blt_client_list) ListView mBltListView;

    private BluetoothAdapter mBluetoothAdapter;
    private BltBroadcastReceiver mBltBroadcastReceiver;
    private BltClientListAdapter mBltClientListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //use the butterKnife lib
        ButterKnife.bind(this);

        //register eventBus
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        mBltBroadcastReceiver = new BltBroadcastReceiver();
        //register the bluetooth_found broadcast
        registerReceiver(mBltBroadcastReceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(mBltBroadcastReceiver,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(mBltBroadcastReceiver,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if( mBluetoothAdapter == null ){
            Toast.makeText(this,"Can't find bluetooth device.", Toast.LENGTH_SHORT).show();
            finish();
        } 
        
        mBltClientListAdapter = new BltClientListAdapter(this);
        mBltListView.setAdapter(mBltClientListAdapter);

        //When click the selected item to require a connection with the selected blt device.
        mBltListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                BLTInfo bltInfo = mBltInfos.get(position);
                new BltClientConnectionThread(bltInfo.getRemoteDevice()).start();
                
            }
        });
    }

    @OnClick(R.id.start_blt_scan)
    public void onServerStart() {
        //start the blt server socket listening and discovery blt
        if( !mBluetoothAdapter.isEnabled() ) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }
        
        if( !mBluetoothAdapter.isEnabled() ) return;
        
        if( mBluetoothAdapter.isDiscovering() ){
            Toast.makeText(this,"Scanning for Bluetooth......", Toast.LENGTH_SHORT).show();
            return;
        }
        mBluetoothAdapter.startDiscovery();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBltBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * be called when recive msg from server.
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBltReciveMsg(BluetoothCommunicator event){
        Toast.makeText(this, "From Server ： " + event.mMessageReceive, Toast.LENGTH_SHORT).show();
    }

    /**
     * be called when a connection set up successed.
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBltConnected(BltConnectionThread event){
        Toast.makeText(this," Bluetooth " + event.getName() + " connected. ", Toast.LENGTH_SHORT).show();
        mBltInfos.clear();
        mBltClientListAdapter.notifyDataSetChanged();
    }

    /**
     * be called when find a new blt device.
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBltFound(BLTInfo event){
        if( !mBltInfos.contains(event) ){
            mBltInfos.add(event);
        } 
        mBltClientListAdapter.notifyDataSetChanged();
    }

    private List<BLTInfo> mBltInfos = new ArrayList<BLTInfo>();

    class BltClientListAdapter extends BaseAdapter {

        private final LayoutInflater mLayoutInflater;
        BltClientListAdapter(Context context){
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mBltInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BLTInfo bltInfo = mBltInfos.get(position);
            if( convertView == null ){
                convertView = mLayoutInflater.inflate(R.layout.blt_list_item,null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.blt_mac.setText(bltInfo.getmBluetoothDeviceMac());
            viewHolder.blt_name.setText(bltInfo.getmBluetoothDeviceName());
            viewHolder.blt_rssi.setText(bltInfo.getRSSI());
            viewHolder.blt_status.setText(bltInfo.getmBluetoothDeviceBondStatus());
            return convertView;
        }
    }

    public static class ViewHolder {
        @Bind(R.id.blt_mac)
        TextView blt_mac;

        @Bind(R.id.blt_name)
        TextView blt_name;

        @Bind(R.id.blt_status)
        TextView blt_status;

        @Bind(R.id.blt_rssi)
        TextView blt_rssi;

        public ViewHolder(View container){
            ButterKnife.bind(this,container);
        }
    }

}

package com.example.jokerlee.bluetoothsample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

    @Bind(R.id.start_blt_server) Button startBltServer;
    
    @Bind(R.id.blt_client_list) ListView mBltListView;

    private BluetoothAdapter mBluetoothAdapter;
    private BltClientListAdapter mBltClientListAdapter;
    private BltServerSocketThread mBltServerSocketThread ;
    private List<BltConnectionThread> mBltConnections = new ArrayList<BltConnectionThread>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //use the butterKnife lib
        ButterKnife.bind(this);

        //register eventBus
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBltClientListAdapter = new BltClientListAdapter(this);
        mBltListView.setAdapter(mBltClientListAdapter);

        //When click select item, will send a greeting to the seleted blt.
        mBltListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                BltConnectionThread bltConnectionThread = mBltConnections.get(position);
                String greet = "Hello, I am " + bltConnectionThread.getClientBltName();
                bltConnectionThread.write(greet.getBytes());
            }
        });
        
        mBltServerSocketThread = new BltServerSocketThread(mBluetoothAdapter);
        
        if( mBluetoothAdapter == null ){
            Toast.makeText(this,"Can't find Bluetooth on this device.", Toast.LENGTH_SHORT).show();
            finish();
        } 
    }

    @OnClick(R.id.start_blt_server)
    public void onServerStart() {
        //start the blt server socket listening and discovery blt
        if( !mBluetoothAdapter.isEnabled() ) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }
        if( !mBltServerSocketThread.isAlive() ) mBltServerSocketThread.start();
        
        if( mBluetoothAdapter.isEnabled() ) {
            Intent discoverableIntent = new
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);
            startActivity(discoverableIntent);
        }
        Toast.makeText(this,"Bluetooth Server Socket listening already started. "  , Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        //release the server socket
        mBltServerSocketThread.cancel();

        //close the connected blt socket
        for( BltConnectionThread connections : mBltConnections ){
            connections.cancel();
        }
        super.onDestroy();
    }

    /**
     * When the blt device recive msg from client will be called
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBltReciveMsg(BluetoothCommunicator event){
        
    }

    /**
     * When the new blt device connected, will be called
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBltConnected(BltConnectionThread event){
        Toast.makeText(this,"Bluetooth " + event.getClientBltName() + "Connected.", Toast.LENGTH_SHORT).show();
        mBltConnections.add(event);
        mBltClientListAdapter.notifyDataSetChanged();
    }

    /**
     * When the blt device recive msg from client will be called
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBltDisconnected(BltDisconnectMSG event){
        mBltConnections.remove(event.getDisconnectThread());
        mBltClientListAdapter.notifyDataSetChanged();
        Toast.makeText(this,"Bluetooth " + event.getDisconnectThread().getClientBltName() + "disconnected.", Toast.LENGTH_SHORT).show();
    }

    class BltClientListAdapter extends BaseAdapter {

        private final LayoutInflater mLayoutInflater;
        BltClientListAdapter(Context context){
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mBltConnections.size();
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

            BltConnectionThread bltConnectionThread = mBltConnections.get(position);
            if( convertView == null ){
                convertView = mLayoutInflater.inflate(R.layout.blt_list_item,null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.blt_mac.setText(bltConnectionThread.getClientBltMac());
            viewHolder.blt_name.setText(bltConnectionThread.getClientBltName());
            viewHolder.blt_rssi.setText(bltConnectionThread.getClientBltRssi());
            viewHolder.blt_status.setText(bltConnectionThread.getClientBltBondState());
            if( bltConnectionThread.isConnected() ) {
                convertView.setBackgroundColor(Color.GREEN);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
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

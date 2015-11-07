
package com.tugba.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;


public class findActivity extends Activity {
    private BluetoothAdapter myBluetoothAdapter;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findlayout);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myListView = (ListView) findViewById(R.id.listView1);
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myListView.setAdapter(BTArrayAdapter);

        boolean isReq = requestBluetoothPerms();

        if (!isReq) find();
    }

    public boolean requestBluetoothPerms() {
        boolean isRequireRequest = !myBluetoothAdapter.isEnabled();
        if (isRequireRequest) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, MainActivity.REQUEST_ENABLE_BT);
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
        return isRequireRequest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_ENABLE_BT) {
            // BluetoothAdapter.STATE_DISCONNECTED==0 ise Bluetooth kapalı
            if (resultCode == BluetoothAdapter.STATE_DISCONNECTED) {
                Toast.makeText(getApplicationContext(), "Bluetooth disable",
                        Toast.LENGTH_LONG).show();
            } else {
                //Bluetooth açık durumu
                Toast.makeText(getApplicationContext(), "Bluetooth turned on",Toast.LENGTH_LONG).show();
                //Diğer Bluetooth cihazlarını aramasını yapan metodu cagırdık
                find();
            }
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Diğer Bluetooth cihazlar  keşif edildiğinde
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Bulunan Bluetoth cihazlarının ad ve adres gibi ozelliklerinin barındığı,  BluetoothDevice objelerini
                // çağırdık
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               //BluetoothDevice objelerini list Adapter'a ekledik
                BTArrayAdapter.clear();
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void find() {
        //Bluetoth cihazlarını keşif işlemi yapıldıktan sonra, keşfi sonlandırma
        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
        } else {
            BTArrayAdapter.clear();
            //Bluetoth cihazlarını keşif işlemi yapılmadığı durumunda, arama işlemi başlatılan bölüm
            myBluetoothAdapter.startDiscovery();
            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }


}

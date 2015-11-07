
package com.tugba.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class deviceDiscoverable extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_Turn_On_Discoverable = 3;
        Spinner spnDiscoverableDuration;
        Button btnTurnOnDiscoverable;
        TextView stateBluetooth;
        BluetoothAdapter bluetoothAdapter;

        String[] optDiscoverableDur = {"10 sec", "60 sec", "120 sec", "240 sec", "300 sec"};
        int[] valueDiscoverableDur = {10, 60, 120, 240, 300};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discoverable_layout);
        //Arayuz elemanlarını tanımladık
        spnDiscoverableDuration = (Spinner)findViewById(R.id.discoverableduration);
        btnTurnOnDiscoverable = (Button)findViewById(R.id.turnondiscoverable);
        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        CheckBlueToothState();

        btnTurnOnDiscoverable.setOnClickListener(btnTurnOnDiscoverableOnClickListener);
        //optDiscoverableDur dizisinde belirttiğimiz süreleri,Spinner a atadık
        ArrayAdapter<String> adapterDiscoverableDur = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, optDiscoverableDur);
        adapterDiscoverableDur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDiscoverableDuration.setAdapter(adapterDiscoverableDur);

        registerReceiver(ScanModeChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
     }


    //Bluetooth'un kapalı yada açık olma, cihazın Bluetooth'u desteklemediğine dair durumları
    //kullanıcıya belirtmek amaclı yazılan metod
    private void CheckBlueToothState(){
        if (bluetoothAdapter == null){
        stateBluetooth.setText("Bluetooth NOT support");
        }else{
            if (bluetoothAdapter.isEnabled()){
                if(bluetoothAdapter.isDiscovering()){
                    stateBluetooth.setText("Bluetooth is currently in device discovery process.");
                }else{
                    stateBluetooth.setText("Bluetooth is Enabled.");
                    btnTurnOnDiscoverable.setEnabled(true);
                }
            }else{
                stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        }
    //Cihazınızın, Bluetooth keşfedilebilir özelliğini açma işlemini butonunun click eventınde yazdık
    private Button.OnClickListener btnTurnOnDiscoverableOnClickListener
        = new Button.OnClickListener(){

@Override
public void onClick(View arg0) {
        Intent discoverableIntent= new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //Spinner'da secilen süreyi alıp, secilen süre kadar Bluetooth keşfedilebilir özelliğini açmak icin
        //süreyi gönderdik
        int dur = valueDiscoverableDur[(int)spnDiscoverableDuration.getSelectedItemId()];
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, dur);
        startActivityForResult(discoverableIntent, REQUEST_Turn_On_Discoverable);
        }};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            CheckBlueToothState();
        }if (requestCode == REQUEST_Turn_On_Discoverable){
            if(resultCode == RESULT_OK){
            }else if (resultCode == RESULT_CANCELED){
            Toast.makeText(deviceDiscoverable.this,"User Canceled",Toast.LENGTH_LONG).show();
            }
        }
    }
    private final BroadcastReceiver ScanModeChangedReceiver = new BroadcastReceiver(){

@Override
public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {

        int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR);
        String strMode = "";

        switch(mode){
        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
        strMode = "mode changed: SCAN_MODE_CONNECTABLE_DISCOVERABLE";
        break;
        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
        strMode = "mode changed: SCAN_MODE_CONNECTABLE";
        break;
        case BluetoothAdapter.SCAN_MODE_NONE:
        strMode = "mode changed: SCAN_MODE_NONE";
        break;
        }

        Toast.makeText(deviceDiscoverable.this,strMode, Toast.LENGTH_LONG).show();
        }
        }};
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(ScanModeChangedReceiver);
    }
}
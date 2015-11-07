
package com.tugba.bluetooth;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.Set;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final int REQUEST_ENABLE_BT = 1;
    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button findBtn;
    private Button discoverableBtn;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Arayuz elemanlarını tanımladık
        listBtn = (Button) findViewById(R.id.paired);
        findBtn = (Button) findViewById(R.id.search);
        discoverableBtn = (Button) findViewById(R.id.discoverable);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Cihazın Bluetooth özeliğini destekleyip desteklemediğini, myBluetoothAdapter degerini kontrol edek tespit ettık
        if (myBluetoothAdapter == null) {
            //Bluetooth özeliğini desteklemediği durumda arayuz butonlarımızı pasif yaptık
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",Toast.LENGTH_LONG).show();
        } else {
            text = (TextView) findViewById(R.id.text);
            onBtn = (Button) findViewById(R.id.turnOn);
            //Bluetooth Status(durumunu), belirten metodu kullanduk..
            setStatus();
            //Bluetooth'u açan butonumuzu  click metodunu yazdık..
            onBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    on(v);
                }
            });

            offBtn = (Button) findViewById(R.id.turnOff);
            //Bluetooth'u kapatan butonumuzu  click metodunu yazdık..
            offBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    off(v);
                }
            });

            //Eşleşmiş Bluetooth cihazlarını listemek icin list butonumuzu  click metodunu yazdık..
            listBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    list(v);
                }
            });
            //discoverableBtn butonumuzun  click metodu ile Cihazınızın, Bluetooth keşfedilebilir özelliğini açmayı saglayan
            // sınıfa yonlendirdik
            discoverableBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, deviceDiscoverable.class));
                }
            });
            //findBtn butonumuzun  click metodu ile diğer Bluetooth cihazlarının aramasını yapan sınıfa yonlendirdim
            findBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, findActivity.class));
                }
            });

            myListView = (ListView) findViewById(R.id.listView1);

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);
        }
    }
    //Cihazınızın Bluetooth'unu açmağı sağlayan metod
    public void on(View view) {
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            //Bluetooth açık olduğunda arayuz butonlarımızı aktif yaptık.Çünkü Bluetooth açık olmazsa bu butonların işlevini kullanamayız
            listBtn.setEnabled(true);
            findBtn.setEnabled(true);
            discoverableBtn.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Bluetooth turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        setStatus();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            setStatus();
        }
    }

    public void list(View view) {
        //Eşleşmiş Bluetooth cihazlarını  çektik ve pairedDevices değişkenine atadım
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        BTArrayAdapter.clear();
        //Eşleşmiş Bluetooth cihazlarının adını ve adresini, listeye ekledik
        for (BluetoothDevice device : pairedDevices) {
            BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        Toast.makeText(getApplicationContext(), "Show Paired Devices",Toast.LENGTH_SHORT).show();

    }

    //Cihazınızın Bluetooth'unu kapatmayı sağlayan metod
    public void off(View view) {
        //BluetoothAdapter etkisiz hale getirdik
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");
        //Bluetooth kapalı olduğunda arayuz butonlarımızı pasif yaptık
        listBtn.setEnabled(false);
        findBtn.setEnabled(false);
        discoverableBtn.setEnabled(false);
        //ve uyarıyı verdik
        Toast.makeText(getApplicationContext(), "Bluetooth turned off",Toast.LENGTH_LONG).show();
    }
    //Bluetooth'un kapalı yada açık olma durumunu kullanıcıya belirtmek amaclı, durumu kontrolu yapıp,  text arayzlerine atayan
    //Bluetooth durumuna göre  butonlarımızı aktif yada pasif yapan metod
    public void setStatus() {
        if (myBluetoothAdapter != null && text != null) {
            //Bluetooth açık
            if (myBluetoothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                //Bluetooth kapalı
                text.setText("Status: Disabled");
                listBtn.setEnabled(false);
                findBtn.setEnabled(false);
                discoverableBtn.setEnabled(false);
            }
        }
    }


}
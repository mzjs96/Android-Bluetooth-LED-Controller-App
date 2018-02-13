package com.michaelzjs.android.bluetooth3;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;


public class DeviceList extends AppCompatActivity {
    Button btnPaired;
    ListView deviceList;
    public static String EXTRA_ADDRESS = "device_address";

    //Create variables to control bluetooth.

    private BluetoothAdapter myBluetooth = null;
    private Set pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //Create variables to "Call" the widgets used to create the layout.

        btnPaired = (Button) findViewById(R.id.button);
        deviceList = (ListView) findViewById(R.id.listView);

        //To check if the device has bluetooth adapter and whether it is activated.

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG).show();

            //Finish apk if the bluetooth is not available.
            finish();
        } else {
            if (!myBluetooth.isEnabled()) {
                //Ask the user to turn on the bluetooth.
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }

        //When the button is clicked, "listen" and show the paired devices.

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
    }


    private void pairedDevicesList() {
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                //get the device's name and the address
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);

        //myListClickListener: Method called when the device from list is clicked.
        deviceList.setOnItemClickListener(myListClickListener);
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            //Get the device's MAC address, the last 17 characters in the view.

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //Make an intent to start the next activity.
            Intent i = new Intent(DeviceList.this, ledControl.class);

            //change the activity.
            i.putExtra(EXTRA_ADDRESS, address);
            //the address will be received at led control (class) Activity
            startActivity(i);
        }
    };
}



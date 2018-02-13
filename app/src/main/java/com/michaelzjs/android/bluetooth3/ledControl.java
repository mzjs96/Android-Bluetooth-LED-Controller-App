package com.michaelzjs.android.bluetooth3;

import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.util.UUID;
import android.os.Bundle;


/**
 * Created by Michael.Z on 12/29/17.
 */


public class ledControl extends AppCompatActivity {

    private ProgressDialog progress;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Button btnOn, btnOff, btnDis;
    SeekBar brightness;
    TextView lumn;
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the variable and retrieve the bluetooth device address from the DeviceList Class
        Intent newInt = getIntent();
        address = newInt.getStringExtra(DeviceList.EXTRA_ADDRESS);

        //view of the ledControl layout
        setContentView(R.layout.activity_led_control);

        //call the widgets
        btnOn = (Button) findViewById(R.id.button2);
        btnOff = (Button) findViewById(R.id.button3);
        btnDis = (Button) findViewById(R.id.button4);
        brightness = (SeekBar) findViewById(R.id.seekBar);
        lumn = (TextView)findViewById(R.id.lumn);


        //start connection.

        new ConnectBT().execute();

        btnOn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                turnOnLed(); // turn the LED on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                turnOffLed(); //turn the LED off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                Disconnect(); //disconnect bluetooth
            }
        });


        //seeker operation
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged (SeekBar seekBar,int progress, boolean fromUser) {
                if (fromUser) {
                    lumn.setText(String.valueOf(progress));
                    try {
                        btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                    } catch (IOException e) {

                    }
                }
            }
                @Override
                public void onStartTrackingTouch (SeekBar seekBar){
                }

                @Override
                public void onStopTrackingTouch (SeekBar seekBar){
                }
        });
    }

    //Create a class to start the connection:

    private class ConnectBT extends AsyncTask<Void, Void, Void> {

        //Setting true for almost connection.

        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            //Shows a progress dialog:

            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");
        }

        //While the progress dialog is shown, the connection is done background.
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    //get the mobile bluetooth device
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();

                    //connects to the device's address and checks if it's available
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address);

                    //create a RFCOMM (SPP) connection
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    //start connection
                    btSocket.connect();
                }
            } catch (IOException e) {
                //if the connection try failed, you can check the exception here.
                ConnectSuccess = false;
            }
            return null;
        }

        // after the doInBackground, it checks if everything went fine
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection failed. Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }



    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void Disconnect() {
        if (btSocket != null) //if the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout
    }

    private void turnOffLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("TF".toString().getBytes());
                Toast.makeText(getApplicationContext(), "succ,         essfully turned off", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("TO".toString().getBytes());
                Toast.makeText(getApplicationContext(), "successfully turned on", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                msg("Error");
            }
        }
    }
}




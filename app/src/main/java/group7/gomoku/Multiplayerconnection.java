package group7.gomoku;

/**
 * Created by purva on 11-Feb-15.
 */

import java.io.BufferedReader;
import java.io.IOException;
import android.os.Handler;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;
    import java.io.IOException;
    import java.util.Set;
    import java.util.UUID;
    //import org.anddev.andengine.util.Debug;
   //import org.d.andengine.util.Debug;
    import java.util.ArrayList;
    import android.app.Activity;
    import android.app.AlertDialog;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.bluetooth.BluetoothServerSocket;
    import android.bluetooth.BluetoothSocket;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.os.Bundle;
    import android.os.Debug;
import android.os.Message;
import android.view.View;
    import android.widget.AdapterView;
    import android.widget.AdapterView.OnItemClickListener;
    import android.widget.ArrayAdapter;
    import android.widget.GridView;
    import android.widget.Toast;
    import java.util.Set;
    import android.widget.ListView;
   // import static android.support.v4.app.ActivityCompat.startActivityForResult;
    import static android.widget.Toast.makeText;



    public class Multiplayerconnection extends Activity {
        private BluetoothAdapter mBluetoothAdapter;
        private Set<BluetoothDevice> pairedDevices;
        private ListView lv;
        // Intent request codes
        private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
        private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
        private static final int REQUEST_ENABLE_BT = 3;
        private ArrayAdapter<String> mArrayAdapter;
        //protected BroadcastReceiver mReceiver;
        private BluetoothAdapter BA;


        // Unique UUID for this application
        private static final UUID MY_UUID_INSECURE =
                UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

//Name for the SDP record when creating server socket

        private static final String NAME_INSECURE = "GomokuplusBluetooth";

           private static AcceptThread mAcceptThread;
        //   private static ConnectThread mConnectThread;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            CharSequence msg;
            String who = "";
            //super.onCreate(savedInstanceState);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                msg = String.format("%s Won %s", who, "Device does not support bluetooth");

            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                Toast.makeText(getApplicationContext(), "Turned on"
                        , Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), "Already on",
                        Toast.LENGTH_LONG).show();
            }}


            // Create a BroadcastReceiver for ACTION_FOUND


        public void list(View view) {
            pairedDevices = BA.getBondedDevices();

            ArrayList list = new ArrayList();
            for (BluetoothDevice bt : pairedDevices)
                list.add(bt.getName());

            Toast.makeText(getApplicationContext(), "Showing Paired Devices",
                    Toast.LENGTH_SHORT).show();
            final ArrayAdapter adapter = new ArrayAdapter
                    (this, android.R.layout.simple_list_item_1, list);
            lv.setAdapter(adapter);

        }

        /**********************************************************************************
         *
         * Inner class AcceptThread
         * Creates a thread which listens for incoming connection and passes socket to class Multiplayer_BT when socket connection is established
         *
         **********************************************************************************/
        private class AcceptThread extends Thread {
            private final BluetoothServerSocket mmServerSocket;

            public AcceptThread() {
                // Use a temporary object that is later assigned to mmServerSocket,
                // because mmServerSocket is final
                BluetoothServerSocket tmp = null;
                try {
                    // MY_UUID is the app's UUID string, also used by the client code
                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
                } catch (IOException e) { }
                mmServerSocket = tmp;
            }

            public void run() {
                BluetoothSocket socket = null;
                // Keep listening until exception occurs or a socket is returned
                while (true) {
                    try {
                        socket = mmServerSocket.accept();
                        Multiplayer.setBluetoothSocket(socket);
                        Intent i=new Intent(Multiplayerconnection.this,Multiplayer.class);
                        finish();
                        startActivity(i);
                    } catch (IOException e) {
                        break;
                    }
                    // If a connection was accepted
                    if (socket != null) {
                        // Do work to manage the connection (in a separate thread)
                        //manageConnectedSocket(socket);
                        try {
                            mmServerSocket.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }

            /** Will cancel the listening socket, and cause the thread to finish */
            public void cancel() {
                try {
                    mmServerSocket.close();
                } catch (IOException e) { }
            }
        }


    }





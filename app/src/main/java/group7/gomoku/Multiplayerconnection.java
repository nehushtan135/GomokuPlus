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
import java.util.List;
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
import android.widget.TextView;
import android.widget.Toast;
    import java.util.Set;
    import android.widget.ListView;
   // import static android.support.v4.app.ActivityCompat.startActivityForResult;
    import static android.widget.Toast.makeText;


    public class Multiplayerconnection extends Activity {
        public BluetoothAdapter mBluetoothAdapter;
        private ListView lv;
        // Intent request codes
        private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
        private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
        private static final int REQUEST_ENABLE_BT = 3;
        private ArrayAdapter<String> mArrayAdapter;
        //protected BroadcastReceiver mReceiver;
        private BluetoothAdapter BA;
        private boolean YesClient;
        private String str;

        // Unique UUID for this application
        private static final UUID MY_UUID_INSECURE =
                UUID.fromString("f12aeaf8-b5af-11e4-a71e-12e3f512a338");

        private static final String NAME_INSECURE = "GomokuplusBluetooth";
        private static AcceptThread mAcceptThread;
        private static ConnectThread mConnectThread;

        //  private static AcceptThread mAcceptThread;
        //   private static ConnectThread mConnectThread;
        private ArrayAdapter<String> btArrayAdapter;
        private ListView listDevicesFound;
        private Set<BluetoothDevice> pairedDevices;

        private final BroadcastReceiver myBluetoothReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Found
                    Toast.makeText(getApplicationContext(), "BT Found", Toast.LENGTH_SHORT).show();
                }
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    // Conected
                    Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
                }
                if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    // Disconnected
                    Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
                }

            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_multiplayerconnection);
            CharSequence msg;
            String who = "";



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
            }



            //DEVICES NEED TO BE PAIRED BEFORE THIS
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            btArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview);
            listDevicesFound = (ListView) findViewById(R.id.listView1);
            listDevicesFound.setAdapter(btArrayAdapter);

            for (BluetoothDevice bt : pairedDevices) {
                // ** this
                //btArrayAdapter.add(bt.getName() + "\n" + bt.getAddress());
                // Connections tied to BT names. Can be tied to BT address too
                btArrayAdapter.add(bt.getName());
            }

            listDevicesFound.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    str = ((TextView) view).getText().toString();

                    // Do not start Server on both devices. Need to show dialog box to start server or not here

                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Multiplayerconnection.this);

                    // Setting Dialog Title
                    alertDialog2.setTitle("Confirm Server");

                    // Setting Dialog Message
                    alertDialog2.setMessage("Run server?");

                    // Setting Icon to Dialog
                    alertDialog2.setIcon(R.drawable.ic_launcher);

                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Start Server
                                    mAcceptThread = new AcceptThread();
                                    mAcceptThread.start();
                                    Toast.makeText(getApplicationContext(),
                                            "Starting Server listener, Will block until client connects", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                    // Setting Negative "NO" Btn
                    alertDialog2.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Set Client
                                    // YesClient = true;
                                    pairedDevices = mBluetoothAdapter.getBondedDevices();
                                    for (BluetoothDevice bt : pairedDevices) {
                                        if (bt.getName().contentEquals(str)) {
                                            registerReceiver(myBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                                            registerReceiver(myBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                                            registerReceiver(myBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                                            // Start the connection to this device. I am client
                                            mConnectThread = new ConnectThread(bt);
                                            mConnectThread.start();

                                        }
                                    }
                                    Toast.makeText(getApplicationContext(),
                                            "Connecting as client to Server"+ str, Toast.LENGTH_SHORT)
                                            .show();
                                    dialog.cancel();
                                }
                            });

                    // Showing Alert Dialog
                    alertDialog2.show();


                }
            });
        }

        // Server Thread
        private class AcceptThread extends Thread {
            private final BluetoothServerSocket mmServerSocket;

            public AcceptThread() {
                // Use a temporary object that is later assigned to mmServerSocket,
                // because mmServerSocket is final
                BluetoothServerSocket tmp = null;
                try {
                    // MY_UUID is the app's UUID string, also used by the client code
                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
                } catch (IOException e) {
                }
                mmServerSocket = tmp;
            }

            public void run() {
                BluetoothSocket socket = null;
                // Keep listening until exception occurs or a socket is returned
                while (true) {
                    try {
                        socket = mmServerSocket.accept();
                        Multiplayer.setBluetoothSocket(socket);
                        Intent i = new Intent(Multiplayerconnection.this, Multiplayer.class);
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


            public void cancel() {
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                }
            }
        }

        // Client Thread
        private class ConnectThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final BluetoothDevice mmDevice;

            public ConnectThread(BluetoothDevice device) {
                // Use a temporary object that is later assigned to mmSocket,
                // because mmSocket is final
                BluetoothSocket tmp = null;
                mmDevice = device;

                // Get a BluetoothSocket to connect with the given BluetoothDevice
                try {
                    // MY_UUID is the app's UUID string, also used by the server code
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                } catch (IOException e) { }
                mmSocket = tmp;
            }

            public void run() {
                // Cancel discovery because it will slow down the connection
                mBluetoothAdapter.cancelDiscovery();

                try {
                    // Connect the device through the socket. This will block
                    // until it succeeds or throws an exception


                    mmSocket.connect();

                    Multiplayer.setBluetoothSocket(mmSocket);
                    Intent i=new Intent(Multiplayerconnection.this,Multiplayer.class);
                    finish();
                    //Debug.d("BLUETOOTH SOCKET CONNECTED");
                    startActivity(i);


                } catch (IOException connectException) {
                    // Unable to connect; close the socket and get out
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) { }
                    return;
                }

                // Do work to manage the connection (in a separate thread)
                // manageConnectedSocket(mmSocket);
            }

            /** Will cancel an in-progress connection, and close the socket */
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) { }
            }
        }

    }




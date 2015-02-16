package group7.gomoku;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by purva on 12-Feb-15.
 */
public class Multiplayer extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private static BluetoothSocket mSocket;
    public static void setBluetoothSocket(BluetoothSocket Socket)
    {
        mSocket=Socket;
        // You have the socket for send and receive of messages
        // Remaining code for multiplayer game goes here
    }
}

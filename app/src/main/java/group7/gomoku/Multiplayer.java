package group7.gomoku;

import android.bluetooth.BluetoothSocket;

/**
 * Created by purva on 12-Feb-15.
 */
public class Multiplayer
{
    private static BluetoothSocket mSocket;
    public static void setBluetoothSocket(BluetoothSocket _BTSocket)
    {
        mSocket=_BTSocket;
    }
}

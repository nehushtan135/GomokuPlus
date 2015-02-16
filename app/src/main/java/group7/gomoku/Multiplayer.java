package group7.gomoku;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;

/**
 * Created by purva on 12-Feb-15.
 *
 * https://github.com/dipenpradhan/bot-wars/blob/master/src/botwars/main/
 */
public class Multiplayer extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private static BluetoothSocket mSocket;
    public Boolean isRunning = false;
    private static BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private PrintWriter mPrintWriterOut;
    private BufferedReader mBufferedReader;

    public static void setBluetoothSocket(BluetoothSocket Socket)
    {
        mSocket=Socket;
        // You have the socket for send and receive of messages
        // Remaining code for multiplayer game goes here
    }

    Multiplayer() {
        try {
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mOutputStream = mSocket.getOutputStream();
            mPrintWriterOut = new PrintWriter(mOutputStream);
        } catch (UnknownHostException e) {
            System.out.println("Unknown Server Address");
        } catch (IOException e) {
            System.out.println("Error Creating socket");
        }
        startReceivingThread();
        sendMessage ("Do,you,see");

    }

    public void sendMessage(String str){
        mPrintWriterOut.println(str);
        mPrintWriterOut.flush();
    }

    public String receiveMessage() {
        String receivedMessage ="";
        try {
            receivedMessage = new String (mBufferedReader.readLine()+"\n");
            //receivedMessage.trim();
            return receivedMessage;
        } catch (IOException e) {
            System.out.println ("error reading stream.\n");
            isRunning = false;
            //endGame();
        }
        return receivedMessage;
    }

    private void startReceivingThread() {
        isRunning = true;
        (new Thread(){
            public void run() {
                while (true) {
                    if (isRunning) {
                        String[] msgArray;
                        msgArray = receiveMessage().split(",",3);
                        handleReceivedMessage(msgArray);
                    }
                    else
                        // isRunning = false';
                        break;
                }
            }
        }).start();
    }

    // Parse the message into proper type then should be able to update the board.
    private void handleReceivedMessage(String[] msgArray) {
        String msg = String.format("Received: %s %s %s\n", msgArray[0], msgArray[1], msgArray[2]);

        System.out.printf ("%s", msg);

        sendMessage("I,got,it");
    }


}

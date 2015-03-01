package group7.gomoku;

import android.app.FragmentManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
public class Multiplayer extends MainActivity implements SurfaceHolder.Callback, PauseFragment.PauseCom
{
    public Boolean isRunning = false;
    private static BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private PrintWriter mPrintWriterOut;
    private BufferedReader mBufferedReader;

    // Server: 1 (White)
    // Client: 2 (Black)
    private static int who;

    Button btnPass;
    ImageButton btnPause;
    GameMultiplayer mGameMulti;
    private SharedPreferences sharedPrefs;
    String sizeServer="";
    String sizeClient="";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView);

        // Set up messaging streams
        try {
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mOutputStream = mSocket.getOutputStream();
            mPrintWriterOut = new PrintWriter(mOutputStream);
        } catch (UnknownHostException e) {
            System.out.println("Unknown Server Address");
        } catch (IOException e) {
            System.out.println("Error Creating socket");
        }

        // The server's preference setting determines the board size
        if (who == 1) {
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            sizeServer = sharedPrefs.getString("pref_boardsize", "15");
            String mmsg = String.format("size,%s", sizeServer);
            sendMessage(mmsg);
            GameMultiplayer.setBluetoothSocket(mSocket);
            mGameMulti = new GameMultiplayer(this, sv, Integer.parseInt(sizeServer), 0, 0, 1);
        }
        // Client tries to get the board size from the server.
        else if (who == 2) {
            while (sizeClient.isEmpty()) {
                receiveBoardSize();
                System.out.print ("looping to get sizeClient.\n");
            }
            GameMultiplayer.setBluetoothSocket(mSocket);
            mGameMulti = new GameMultiplayer(this, sv, Integer.parseInt(sizeClient), 0, 0, 2);
        }

        sv.getHolder().addCallback(this);

        btnPass = (Button) findViewById(R.id.btnPass);
        btnPause = (ImageButton) findViewById(R.id.btn_pause);



        //btnPass.setBackgroundColor(BLUE);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fM = getFragmentManager();
                PauseFragment pF = new PauseFragment();
                pF.show(fM,"PauseFragment");


            }

        } );
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameMulti.changeTurn();
            }
        });


    }
    @Override
    public void onDialogResume() {

    }

    @Override
    public void onDialogExit() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_new_game, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setBluetoothSocket(BluetoothSocket Socket)
    {
        mSocket=Socket;
    }

    // 1: Server
    // 2: Client
    public static void setWho (int i) {
        who = i;
    }

    public void sendMessage(String str){
        mPrintWriterOut.println(str);
        mPrintWriterOut.flush();
        System.out.printf("Sent: %s\n", str);

    }

    public boolean receiveBoardSize() {
        String receivedMessage ="";
        String[] msgArray;
        try {
            //receivedMessage = new String (mBufferedReader.readLine()+"\n");
            receivedMessage = new String (mBufferedReader.readLine());
            receivedMessage.trim();
            msgArray = receivedMessage.split(",",2);
            if (msgArray[0].equals("size")) {
                sizeClient = msgArray[1];
                System.out.printf("set sizeClient!!! %s \n", sizeClient);
                return true;
            }
        } catch (IOException e) {
            System.out.println ("error reading stream.\n");
            //isRunning = false;
            //endGame();
        }
        return false;
    }

    private void startReceivingThread() {
        isRunning = true;
        (new Thread(){
            public void run() {
                while (true) {
                    if (isRunning) {
                    //    String[] msgArray;
                    //    msgArray = receiveMessage().split(",",3);
                    //    handleReceived(msgArray);
                    }
                    else
                        // isRunning = false';
                        break;
                }
            }
        }).start();
    }

    // Parse the message into proper type then should be able to update the board.
    private void handleReceived(String[] msgArray) {
        if (msgArray[0].equals("size"))
            sizeClient = msgArray[1];

        String msg = String.format("Received: %s %s\n", msgArray[0], msgArray[1]);

        System.out.printf ("%s", msg);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mGameMulti.draw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
        mGameMulti.draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

}

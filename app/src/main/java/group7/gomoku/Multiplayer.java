package group7.gomoku;

import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
public class Multiplayer extends MainActivity implements SurfaceHolder.Callback
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
    TextView textViewTime;
    String cTime;
    GamePlus mGameMulti;
    private SharedPreferences sharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String size = sharedPrefs.getString("pref_boardsize", "15");

        mGameMulti = new GamePlus(this, sv, Integer.parseInt(size), 0, 0);
        sv.getHolder().addCallback(this);

        btnPass = (Button) findViewById(R.id.btnPass);
        btnPause = (ImageButton) findViewById(R.id.btn_pause);
        textViewTime = (TextView) findViewById(R.id.textViewTime);

        textViewTime.setText("03:00");
        /*
        final CounterClass timer = new CounterClass(180000,1000);

        timer.start();
        //btnPass.setBackgroundColor(BLUE);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                FragmentManager fM = getFragmentManager();
                PauseFragment pF = new PauseFragment();
                pF.show(fM,"PauseFragment");


            }

        } );
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.start();
                mGame.changeTurn();
            }
        })

        */

        try {
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mOutputStream = mSocket.getOutputStream();
            mPrintWriterOut = new PrintWriter(mOutputStream);
        } catch (UnknownHostException e) {
            System.out.println("Unknown Server Address");
        } catch (IOException e) {
            System.out.println("Error Creating socket");
        }

        if (who == 1)
            sendMessage ("1,2,3");

        startReceivingThread();

        if (who == 2)
            sendMessage("4,5,6");
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

    public String receiveMessage() {
        String receivedMessage ="";
        try {
            receivedMessage = new String (mBufferedReader.readLine()+"\n");
            receivedMessage.trim();
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

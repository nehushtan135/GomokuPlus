package group7.gomoku;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class NewGame extends MainActivity implements SurfaceHolder.Callback{

    Button btnPass;
    ImageButton btnPause;
    TextView textViewTime;
    String cTime;
    GamePlus mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        btnPass = (Button) findViewById(R.id.btnPass);
        btnPause = (ImageButton) findViewById(R.id.btn_pause);
        textViewTime = (TextView) findViewById(R.id.textViewTime);

        textViewTime.setText("03:00");
        final CounterClass timer = new CounterClass(180000,1000);

        timer.start();

        SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView);
        mGame = new GamePlus(this, sv, 10);
        sv.getHolder().addCallback(this);

        //btnPass.setBackgroundColor(BLUE);
        btnPause.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               timer.cancel();
               FragmentManager fM = getFragmentManager();
               PauseFragment pF = new PauseFragment();
               pF.show(fM,"Pause");

           }

            } );
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.start();
                mGame.changeTurn();
            }
        });



    }


    public void showDialog(View v) {
        FragmentManager fM = getFragmentManager();
        PauseFragment pF = new PauseFragment();
        pF.show(fM,"Pause");
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


    public class CounterClass extends CountDownTimer {
        public CounterClass (long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick (long millisUntilFinished) {
            long millis = millisUntilFinished;
            String ms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            //cTime is storing the current time left on the clock into a variable that pauseButton onclicklistener can then store to recover on resume.
            cTime = ms;


            //System.out.println(ms);
            textViewTime.setText(ms);
        }

        @Override
        public void onFinish() {

            textViewTime.setText("DONE.");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mGame.draw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
        mGame.draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

}

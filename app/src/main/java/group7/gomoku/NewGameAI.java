package group7.gomoku;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class NewGameAI extends MainActivity implements SurfaceHolder.Callback, PauseFragment.PauseCom{

    private SharedPreferences sharedPrefs;
    ImageButton btnPause;
    GamePlusAI mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_game_ai);
        SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String size = sharedPrefs.getString("pref_boardsize", "15");

        mGame = new GamePlusAI(this, sv, Integer.parseInt(size), 0, 0);
        sv.getHolder().addCallback(this);

        btnPause = (ImageButton) findViewById(R.id.btn_pause);

        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fM = getFragmentManager();
                PauseFragment pF = new PauseFragment();
                pF.show(fM,"PauseFragment");


            }

        } );
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

    @Override
    public void onDialogResume() {

    }

    @Override
    public void onDialogExit() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /* not using timer anymore but don't want to delete it completely
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
        */
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

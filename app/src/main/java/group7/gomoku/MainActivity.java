package group7.gomoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends Activity {

    boolean muteAudio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupOnNewGameClick();

        setupOnPlayAiClick();
        setupOnMultiplayerClick();
        setupOnExitClick();
        playMusic();
    }

    @Override
    protected void onDestroy() {
        stopMusic();
        super.onDestroy();
    }
    private void setupOnPlayAiClick() {
        Button seButton = (Button) findViewById(R.id.PlayAiButton);
        seButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PUT AI CODE HERE
                //startActivity(new Intent(MainActivity.this, Settings.class));
            }
        });
    }


    public void setupOnNewGameClick() {
        Button ngButton = (Button) findViewById(R.id.newGameButton);
        ngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewGame.class));
            }

        });
    }
    public void setupOnMultiplayerClick() {
        Button heButton = (Button) findViewById(R.id.MultiplayerButton);
        heButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(MainActivity.this, Multiplayerconnection.class));
            }

        });
    }

    public void setupOnExitClick() {
        Button exButton = (Button) findViewById(R.id.exitButton);
        exButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
                finish();
                System.exit(0);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.newgame:
                startActivity(new Intent(MainActivity.this, NewGame.class));
                return true;
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.help:
                startActivity(new Intent(MainActivity.this, Help.class));
                return true;
            case R.id.about:
                startActivity(new Intent(MainActivity.this, About.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean MuteAudio(View view){
        /**
         * Enable and disable the sound of the app, default state is enable
         * @param View view
         * @return true if success
         */
        muteAudio = !muteAudio;

        ImageButton btn_mute = (ImageButton)findViewById(R.id.btn_mute);

        if(true == muteAudio){
            btn_mute.setImageResource(R.drawable.mute);
            stopMusic();
        }else{
            btn_mute.setImageResource(R.drawable.unmute);
            playMusic();
        }

        return true;

    }

    //start audio play service and play music
    public void playMusic(){
        Intent intentPlayMusic = new Intent(this, group7.gomoku.ServiceAudioPlay.class);
        startService(intentPlayMusic);
    }

    // stop playing music via stop audio play service
    public void stopMusic(){
        Intent intentPlayMusic = new Intent(this, group7.gomoku.ServiceAudioPlay.class);
        stopService(intentPlayMusic);
    }

    //Release UI memory

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}

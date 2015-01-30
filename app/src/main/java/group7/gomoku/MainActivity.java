package group7.gomoku;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends Activity {

    boolean muteAudio = false;
    // declare and init various needed id's

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupOnNewGameClick();
        setupOnSettingClick();
        setupOnHelpClick();
        setupOnAboutClick();
        setupOnExitClick();
        playMusic();
    }

    @Override
    protected void onDestroy() {
        stopMusic();
        super.onDestroy();
    }

    private void setupOnSettingClick() {
        Button seButton = (Button) findViewById(R.id.settingsButton);
        seButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Settings.class));
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
    public void setupOnHelpClick() {
        Button heButton = (Button) findViewById(R.id.helpButton);
        heButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Help.class));
            }

        });
    }
    public void setupOnAboutClick() {
        Button abButton = (Button) findViewById(R.id.aboutButton);
        abButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, About.class));
            }

        });
    }
    public void setupOnExitClick() {
        Button exButton = (Button) findViewById(R.id.exitButton);
        exButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        Intent intentPlayMusic = new Intent(this, com.example.vi.gomokuplus.ServiceAudioPlay.class);
        startService(intentPlayMusic);
    }

    // stop playing music via stop audio play service
    public void stopMusic(){
        Intent intentPlayMusic = new Intent(this, com.example.vi.gomokuplus.ServiceAudioPlay.class);
        stopService(intentPlayMusic);
    }
}

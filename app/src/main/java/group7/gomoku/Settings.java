package group7.gomoku;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class Settings extends MainActivity {

    boolean muteAudio = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_settings, menu);
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

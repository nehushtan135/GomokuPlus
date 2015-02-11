package group7.gomoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class PauseFragment extends DialogFragment implements View.OnClickListener {
    Button toResume, toExit;
    ImageButton muteSound;
    PauseCom pauseCom;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        pauseCom = (PauseCom) activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = 0;
        theme = R.style.customDialog;
        setStyle(style,theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Pause Menu");
        View view = inflater.inflate(R.layout.fragment_pause,container,false);

        muteSound = (ImageButton) view.findViewById(R.id.pauseBtn_mute);
        toResume = (Button) view.findViewById(R.id.pauseResume);
        toExit = (Button) view.findViewById(R.id.pauseExit);

        muteSound.setOnClickListener(this);
        toResume.setOnClickListener(this);
        toExit.setOnClickListener(this);

        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.pauseResume){
            //reset timer to players time
            pauseCom.onDialogResume();

            //start timer again

            //get out of the dialog
            dismiss();


        }
        else if(view.getId() == R.id.pauseExit) {
            // return to main menu

            //get destroy dialog
            dismiss();

        }
        // if the mute button is pushed
        else {
            dismiss();
            //AudioManager am = (AudioManager)this. (Context.AUDIO_SERVICE);

            // stop or stop the sound, depending on current setting

            //change image that is displayed again to the opposite of the current setting

            //wait for further button clicks

        }

    }
     interface PauseCom {
         public void onDialogResume();
     }



}

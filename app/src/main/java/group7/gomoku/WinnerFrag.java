package group7.gomoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class WinnerFrag extends DialogFragment implements View.OnClickListener {

    public static WinnerFrag newInstance(String winner,int wScore, int bScore) {
        WinnerFrag winFrag = new WinnerFrag();
        Bundle args = new Bundle();
        args.putString("title", winner);
        args.putInt("whiteScore",wScore);
        args.putInt("blackScore",bScore);
        winFrag.setArguments(args);
        return winFrag;
    }


    Button toNewGame, toQuit;

    //ImageButton muteSound;
    WinCom winCom;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String title = getArguments().getString("title");
        CharSequence fScore;
        int wScore = getArguments().getInt("whiteScore");
        int bScore = getArguments().getInt("blackScore");
        fScore = String.format("White  %s     Black %s",wScore,bScore);
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(fScore)
                .setPositiveButton(R.string.winReset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((WinCom)getActivity()).doOnPositiveClick();

                    }

                })
                .setNegativeButton(R.string.winExit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((WinCom)getActivity()).doOnNegativeClick();

                    }
                })
                .create();


    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        winCom = (WinCom) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        int style = DialogFragment.STYLE_NORMAL;
        int theme = 0;
        theme = R.style.customDialog;
        setStyle(style, theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Winner!");
        View view = inflater.inflate(R.layout.activity_winner_frag, container, false);



        toNewGame = (Button) view.findViewById(R.id.winReset);
        toQuit = (Button) view.findViewById(R.id.pauseExit);

        toNewGame.setOnClickListener(this);
        toQuit.setOnClickListener(this);

        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View v) {


    }

    interface WinCom {
        public void doOnPositiveClick();
        public void doOnNegativeClick();
    }


}

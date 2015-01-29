package group7.gomoku;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.PipedOutputStream;

/**
 * 10*10 Gomoku Game
 * Created by Lai Xu on 2015/1/26.
 */
public class Game {

    public class Position{
        int x;
        int y;
        int occupy; //0:empty, 1:white, 2:black
        ImageView imageStone;

        public Position(){
            x = 0;
            y = 0;
            occupy = 0;
            imageStone = new ImageView(mContext);
        }

        int getDistance(int x, int y){
            return (int)Math.pow(x-this.x, 2)+(int)Math.pow(y-this.y, 2);
        }

    }

    //bitmaps of stones
    Bitmap mStoneWhite, mStoneBlack;
    Bitmap mStoneWhiteScale, mStoneBlackScale;

    //image of board
    ImageView mBoard;

    //the activitity will be used
    Context mContext;

    //the position  of each cross of board
    Position[][] mPosition;

    //the the layout for the stones
    RelativeLayout mLayout;

    int curParty;

    Game(Context context){
        this.mContext = context;
        mPosition = new Position[11][11];
        curParty = 1; //default white first
        int i, j;
        int x = 7, y = 85;
        int step  = 60;

        for(i=0; i<11; i++){
            for(j=0; j<11; j++){
                mPosition[i][j] = new Position();
                mPosition[i][j].x = x;
                mPosition[i][j].y = y;
                x += step;
            }
            x = 7;
            y += step;
        }

        mStoneBlack = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.stoneblack);
        mStoneBlackScale = Bitmap.createScaledBitmap(mStoneBlack, 60, 60, true);
        mStoneWhite = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.stonewhite);
        mStoneWhiteScale = Bitmap.createScaledBitmap(mStoneWhite,60, 60, true);

        /*
        for(i=0; i<11; i++){
            for(j=0; j<11; j++){

                if ((i+j)%3 == 0) {
                    PutStone(j % 2 + 1, mPosition[i][j]);
                }
            }
        }
        */
        mLayout = (RelativeLayout)(((Activity)mContext).findViewById(R.id.boardLayout));
        mBoard = (ImageView) (((Activity)mContext).findViewById(R.id.imageBoard));
        addListenerOnBoard();

    }

    public void PutStone(int flag, Position position){
        if(1 == flag) {
            position.imageStone.setImageBitmap(mStoneWhiteScale);
        }else if (2 == flag)
        {
            position.imageStone.setImageBitmap(mStoneBlackScale);
        }
        else{
            return;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.leftMargin=position.x;
        lp.topMargin=position.y;
        position.imageStone.setId(position.x*10+position.y);
        position.imageStone.setLayoutParams(lp);
        mLayout.addView(position.imageStone);

        position.occupy = flag;
    }

    public void addListenerOnBoard() {

        mBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PutStoneByTouch(curParty, (int)event.getX(), (int)event.getY());
                }
                return true;
            }
        });
    }

    public boolean PutStoneByTouch(int flag, int x, int y){
        //find right position based on the minimum distance
        Position position = mPosition[0][0];
        int minDistance = mPosition[0][0].getDistance(x, y);
        int curDistance = 0;

        int i, j;
        for(i=0; i<11; i++){
            for(j=0; j<11; j++){
                    curDistance = mPosition[i][j].getDistance(x, y);
                    if (minDistance > curDistance){
                        position = mPosition[i][j];
                        minDistance = curDistance;
                }
            }
        }

        if (position.occupy != 0){
            return false;
        }

        PutStone(flag, position);

        if(curParty == 1){
            curParty = 2;
        }else if(curParty == 2){
            curParty = 1;
        }else{
            //error here
        }

        return true;
    }

}

package group7.gomoku;

import android.app.Activity;
import android.app.Dialog;
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
 * Created by Lai Xu on Jan.01.2015
 * Modified by Lai Xu on Feb.05.2015
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
    public void changeTurn() {
        if(curParty == 1){
            curParty = 2;
        }else if(curParty == 2){
            curParty = 1;
        }else{
            //error here
        }
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

        changeTurn();

        return true;
    }

    // caller should ensure that nextPosition is guaranteed to be within the board
    public Position getNextPosition (Position p, int direction) {
        Position nextPosition = new Position();

        switch (direction) {
            case 0:
                // direction 0: moveRight
                nextPosition = mPosition[(p.x)++][p.y];
                break;
            case 1:
                // direction 1: moveDownRight
                nextPosition = mPosition[(p.x)++][(p.y)--];
                break;
            case 2:
                // direction 2: moveDown
                nextPosition = mPosition[p.x][(p.y)--];
                break;
            case 3:
                // direction 3: moveDownLeft
                nextPosition = mPosition[(p.x)--][(p.y)--];
                break;
            case 4:
                // direction 4: moveLeft
                nextPosition = mPosition[(p.x)--][p.y];
                break;
            case 5:
                // direction 5: moveUpLeft
                nextPosition = mPosition[(p.x)--][(p.y)++];
                break;
            case 6:
                // direction 6: moveUp
                nextPosition = mPosition[p.x][(p.y)++];
                break;
            case 7:
                // direction 7: moveUpRight
                nextPosition = mPosition[(p.x)++][(p.y)++];
                break;
            default:
                break;
        }
        return nextPosition;
    }

    // flag: 1 White
    // flag: 2 Black
    public void checkForWinner (Position p, int flag) {
        int i;
        int[] tmp = new int[10];
        Position curPosition = new Position();
        curPosition = p;

        // Horizontal wins:
        tmp[5] = p.occupy;
        // fill the right half of tmp buffer |x|x|x|x|x|*|6|7|8|9|10|
        for (i = 6; i <= 10; i++) {
            curPosition = getNextPosition(curPosition, 0);
            tmp[i] = curPosition.occupy;
        }
        // fill the left half of tmp buffer |0|1|2|3|4|x|x|x|x|x|x|
        curPosition = p;
        for (i = 4; i >= 0; i--) {
            curPosition = getNextPosition(curPosition, 4);
            tmp[i] = curPosition.occupy;
        }
        if (isWinner(tmp) != 0) {
            //We have a winner... EndGame here
        }

        // Vertical wins:
        curPosition = p;
        for (i = 6; i <= 10; i++){
            curPosition = getNextPosition(curPosition, 6);
            tmp[i] = curPosition.occupy;
        }
        curPosition = p;
        for (i = 4; i >= 0; i--){
            curPosition = getNextPosition(curPosition, 2);
            tmp[i] = curPosition.occupy;
        }
        if (isWinner(tmp) != 0) {
            //We have a winner... EndGame here
        }

        // Diagonal Down Wins
        curPosition = p;
        for (i = 6; i <= 10; i++){
            curPosition = getNextPosition(curPosition, 5);
            tmp[i] = curPosition.occupy;
        }
        curPosition = p;
        for (i = 4; i >= 0; i--){
            curPosition = getNextPosition(curPosition, 7);
            tmp[i] = curPosition.occupy;
        }
        if (isWinner(tmp) != 0) {
            //We have a winner... EndGame here
        }

        // Diagonal Up Wins
        curPosition = p;
        for (i = 6; i <= 10; i++){
            curPosition = getNextPosition(curPosition, 3);
            tmp[i] = curPosition.occupy;
        }
        curPosition = p;
        for (i = 4; i >= 0; i--){
            curPosition = getNextPosition(curPosition, 1);
            tmp[i] = curPosition.occupy;
        }
        if (isWinner(tmp) != 0) {
            //We have a winner... EndGame here
        }
    }

    // return 1 if white win!!
    // return 2 if black win!!
    // return 0 if no winner!
    public int isWinner (int []arr) {
        int i;
        int same = 1;
        int winner = 0;

        // look for 5 consecutive color in tmp buffer
        for (i = 0; i < 10; i++) {
            if (arr[i] == arr[i + 1])
                same++;
            if (same == 5)
                break;
            else
                same = 1;
        }
        // potential win, need to check for the 6th stone
        if (same == 5) {
            if (arr[i] == arr[i + 1])
                return 0;
            else {
                // Report who won
                /*Dialog dialog = new Dialog(this);
                //dialog.setTitle(getString(R.string.app_name));
                dialog.setContentView(this);
                dialog.setCancelable(true);
                dialog.show();*/
                return arr[i]; // should be 1 or 2!
            }
        }
        return 0;
    }

    public void endGame(){
        // need a way to end game. Exit or reset the board
    }

    public void resetGame(){
        // reset for a new game
    }

    public void pauseGame(){

    }
}

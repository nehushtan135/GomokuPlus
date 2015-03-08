package group7.gomoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Lai Xu on 15-2-6.
 */
public class GamePlusAI extends MainActivity implements Runnable {
    Context context;
    SurfaceView sv;
    int curParty;
    int wScore, bScore;
    Position[][] posMatrix;
    Bitmap mStoneWhiteScale;
    Bitmap mStoneBlackScale;
    float gridSize;
    int boardType;
    int maxNumStone;
    int stoneCounter;
    boolean exitGame;
    //the the layout for the stones
    RelativeLayout mLayout;

    protected GameAI gameAI;


    GamePlusAI(Context context, SurfaceView sv, int boardType, int wScore, int bScore) {
        this.context = context;
        this.sv = sv;
        this.boardType = boardType;
        this.wScore = wScore;
        this.bScore = bScore;
        mStoneBlackScale = null;
        mStoneWhiteScale = null;
        curParty = 1;
        exitGame = false;
        maxNumStone = (boardType+1) * (boardType+1);
        stoneCounter = 0;
        mLayout = (RelativeLayout) (((Activity) context).findViewById(R.id.boardLayout));
        displayScore();
        addListenerOnBoard();

        gameAI = new GameAI(boardType, 2);
    }

    public void newGame() {
        draw();
    }

    public class Position {
        float x;
        float y;
        int occupy; //0:empty, 1:white, 2:black
        ImageView imageStone;

        public Position() {
            x = 0;
            y = 0;
            occupy = 0;
            imageStone = null;
        }

        float getSquareDistance(float x, float y) {
            return (float) Math.pow(x - this.x, 2) + (int) Math.pow(y - this.y, 2);
        }
    }

    private void drawBoard(final Canvas canvas) {
        final int height = sv.getHeight();
        final int width = sv.getWidth();

        posMatrix = new Position[boardType + 1][boardType + 1];

        //Calculating the size and position of the board
        float boundWidth = 0;
        float posLeft = boundWidth;
        float posRight = width - posLeft;
        float posTop = (height - width) / 2;
        float posBottom = posTop + (posRight - posLeft);

        //Draw background
        canvas.drawRGB(20, 66, 72);

        //Draw gomuko board
        Paint p = new Paint();
        p.setARGB(255, 220, 179, 92);
        canvas.drawRect(posLeft, posTop, posRight, posBottom, p);


        p.setStrokeWidth(width / 150);
        p.setARGB(255, 47, 35, 8);

        //Draw out boundary of the board
        p.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(posLeft, posTop, posRight, posTop, p);
        canvas.drawLine(posLeft, posBottom, posRight, posBottom, p);
        canvas.drawLine(posLeft, posTop, posLeft, posBottom, p);
        canvas.drawLine(posRight, posTop, posRight, posBottom, p);

        //Draw inner boundary of the board
        p.setStrokeWidth(width / 200);
        float innerBoundWidth = width / (boardType * 2);
        float innerPosLeft = posLeft + innerBoundWidth;
        float innerPosRight = posRight - innerBoundWidth;
        float innerPosTop = posTop + innerBoundWidth;
        float innerPosBottom = posBottom - innerBoundWidth;


        gridSize = (innerPosRight - innerPosLeft) / boardType;


        //Draw inner bound
        canvas.drawLine(innerPosLeft, innerPosTop, innerPosRight, innerPosTop, p);
        canvas.drawLine(innerPosLeft, innerPosBottom, innerPosRight, innerPosBottom, p);
        canvas.drawLine(innerPosLeft, innerPosTop, innerPosLeft, innerPosBottom, p);
        canvas.drawLine(innerPosRight, innerPosTop, innerPosRight, innerPosBottom, p);


        //Draw lines of the board
        p.setStrokeWidth(width / (boardType * 30));


        int i = 0;
        int j = 0;
        float posOffset = 0;
        while (i < boardType + 1) {
            posOffset = gridSize * i;
            canvas.drawLine(innerPosLeft + posOffset, innerPosTop, innerPosLeft + posOffset, innerPosBottom, p);
            canvas.drawLine(innerPosLeft, innerPosTop + posOffset, innerPosRight, innerPosTop + posOffset, p);


            while (j < boardType + 1) {

                if (posMatrix[i][j] == null) {
                    posMatrix[i][j] = new Position();
                }

                if (posMatrix[j][i] == null) {
                    posMatrix[j][i] = new Position();
                }

                posMatrix[j][i].x = innerPosLeft + posOffset;
                posMatrix[i][j].y = innerPosTop + posOffset;
                j++;
            }

            j = 0;
            i++;
        }

        //Draw the small mark points of the board
        int step = boardType / 5;
        canvas.drawCircle(posMatrix[step][step].x, posMatrix[step][step].y, gridSize / 10, p);
        canvas.drawCircle(posMatrix[step][boardType - step].x, posMatrix[step][boardType - step].y, gridSize / 10, p);
        canvas.drawCircle(posMatrix[boardType - step][step].x, posMatrix[boardType - step][step].y, gridSize / 10, p);
        canvas.drawCircle(posMatrix[boardType - step][boardType - step].x, posMatrix[boardType - step][boardType - step].y, gridSize / 10, p);

        if (null == mStoneBlackScale) {
            Bitmap mStoneBlack = BitmapFactory.decodeResource(context.getResources(), R.drawable.stoneblack);
            mStoneBlackScale = Bitmap.createScaledBitmap(mStoneBlack, (int) gridSize, (int) gridSize, true);
        }
        if (null == mStoneWhiteScale) {

            Bitmap mStoneWhite = BitmapFactory.decodeResource(context.getResources(), R.drawable.stonewhite);
            mStoneWhiteScale = Bitmap.createScaledBitmap(mStoneWhite, (int) gridSize, (int) gridSize, true);
        }
    }

    public void draw() {

        SurfaceHolder holder = sv.getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
        } else {
            drawBoard(canvas);
            holder.unlockCanvasAndPost(canvas);
        }

    }

    public void addListenerOnBoard() {

        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PutStoneByTouch(curParty, event.getX(), event.getY());
                }
                return true;
            }
        });
    }

    public boolean PutStoneByTouch(int flag, float x, float y) {


        //find right position based on the minimum distance
        Position position = posMatrix[0][0];
        float minDistance = posMatrix[0][0].getSquareDistance(x, y);
        float curDistance = 0;

        int i, j, row = 0, col = 0;
        for (i = 0; i < boardType + 1; i++) {
            for (j = 0; j < boardType + 1; j++) {
                curDistance = posMatrix[i][j].getSquareDistance(x, y);
                if (minDistance > curDistance) {
                    position = posMatrix[i][j];
                    col = i;
                    row = j;
                    minDistance = curDistance;
                }
            }
        }

        //if (position.occupy != 0) {
        if (posMatrix[col][row].occupy != 0) {
            System.out.print("Failed to put stone\n");
            return false;
        }

        PutStone(flag, position);
        posMatrix[col][row].occupy = flag;
        stoneCounter++;
        checkForWinner(col, row);
        //if (true == checkForWinner(col, row)) {
        //    resetGame();
        //    return true;
        //}

        changeTurn();

        //AI
        gameAI.putOpStone(col, row);
        //gameAI.PrintMatrix();

        while (true) {

            GameAI.StonePos pos = gameAI.GetPos2();
            if (null != pos) {
                if (posMatrix[pos.x][pos.y].occupy == 0) {

                    position = posMatrix[pos.x][pos.y];
                    PutStone(curParty, position);
                    posMatrix[pos.x][pos.y].occupy = curParty;
                    gameAI.putMyStone(pos.x, pos.y);
                    //gameAI.PrintMatrix();
                    stoneCounter++;
                    checkForWinner(pos.x, pos.y);

                    // we have a tie if the board is completely filled.
                    if (stoneCounter == maxNumStone)
                        displayWinner(-1, "This Game.");

                    changeTurn();
                    break;

                } else {
                    System.out.print("Failed to put stone AI\n");
                    continue;
                }
            }

            return false;


        }

        return true;
    }


    public void PutStone(int flag, Position position) {

        position.imageStone = new ImageView(context);

        if (1 == flag) {
            position.imageStone.setImageBitmap(mStoneWhiteScale);
        } else if (2 == flag) {
            position.imageStone.setImageBitmap(mStoneBlackScale);
        } else {
            return;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.leftMargin = (int) (position.x - gridSize / 2 + 0.5f);
        lp.topMargin = (int) (position.y - gridSize / 2 + 0.5f);
        position.imageStone.setId((int) (position.x * 10 + position.y));
        position.imageStone.setLayoutParams(lp);
        mLayout.addView(position.imageStone);
        mLayout.invalidate();

        //position.occupy = flag;
    }

    public void changeTurn() {
        if (curParty == 1) {
            curParty = 2;
           // iv.setImageResource(R.drawable.stoneblack);
        }else if(curParty == 2){
            //iv.setImageResource(R.drawable.stonewhite);
            curParty = 1;
        } else {
        }
    }

    @Override
    public void run() {
    }

    public void resetGame() {

        //remove the view from the relative layout and reset the PosMatrix
        int i, j;
        for (i = 0; i <= boardType; i++) {
            for (j = 0; j <= boardType; j++) {
                if (posMatrix != null) {
                    if (posMatrix[i][j].imageStone != null) {
                        ((RelativeLayout) posMatrix[i][j].imageStone.getParent()).removeView(posMatrix[i][j].imageStone);
                        posMatrix[i][j].imageStone = null;
                    }
                    posMatrix[i][j].occupy = 0;
                }

            }
        }
        displayScore();
        stoneCounter = 0;
        curParty = 1;

        gameAI.reset();
    }
    private void displayScore() {
        TextView whiteView = (TextView)((Activity) context).findViewById(R.id.whiteScore);
        whiteView.setText("White: "+wScore);
        TextView blackView = (TextView)((Activity) context).findViewById(R.id.blackScore);
        blackView.setText("Black: " +bScore);
    }
    public Position getNextPosition(int col, int row, int direction) {
        Position nextPosition = new Position();
        switch (direction) {
            case 0:
                // direction 0: moveRight
                nextPosition = posMatrix[col++][row];
                break;
            case 1:
                // direction 1: moveDownRight
                nextPosition = posMatrix[col++][row--];
                break;
            case 2:
                // direction 2: moveDown
                nextPosition = posMatrix[col][row--];
                break;
            case 3:
                // direction 3: moveDownLeft
                nextPosition = posMatrix[col--][row--];
                break;
            case 4:
                // direction 4: moveLeft
                nextPosition = posMatrix[col--][row];
                break;
            case 5:
                // direction 5: moveUpLeft
                nextPosition = posMatrix[col--][row++];
                break;
            case 6:
                // direction 6: moveUp
                nextPosition = posMatrix[col][row++];
                break;
            case 7:
                // direction 7: moveUpRight
                nextPosition = posMatrix[col++][row++];
                break;
            default:
                break;
        }
        return nextPosition;
    }

    // flag: 1 White
    // flag: 2 Black
    public boolean checkForWinner(int col, int row) {
        int i, winner;
        int nextc = col;
        int nextr = row;
        int[] tmp = new int[11];
        Position curPosition = new Position();
        curPosition = posMatrix[col][row];

        // Vertical wins:
        tmp[5] = curPosition.occupy;
        // fill the right half of tmp buffer |x|x|x|x|x|*|6|7|8|9|10|
        for (i = 6; i <= 10; i++) {
            nextc++;
            if (nextc <= boardType) {
                curPosition = getNextPosition(nextc, row, 0);
                tmp[i] = curPosition.occupy;
            } else
                tmp[i] = -1;
        }
        // fill the left half of tmp buffer |0|1|2|3|4|x|x|x|x|x|x|
        nextc = col;
        for (i = 4; i >= 0; i--) {
            nextc--;
            if (nextc >= 0) {
                curPosition = getNextPosition(nextc, row, 4);
                tmp[i] = curPosition.occupy;
            } else // Since we  have to get 4 positions, we have to put -1
                // when it's outside the board.
                tmp[i] = -1;
        }
        winner = isWinner(tmp);
        if (winner != 0) {
            displayWinner(winner, "Vertically!!!");
            return true;
        }

        // Horizontal wins:
        nextr = row;
        for (i = 6; i <= 10; i++) {
            nextr++;
            if (nextr <= boardType) {
                curPosition = getNextPosition(col, nextr, 6);
                tmp[i] = curPosition.occupy;
            } else
                tmp[i] = -1;
        }
        nextr = row;
        for (i = 4; i >= 0; i--) {
            nextr--;
            if (nextr >= 0) {
                curPosition = getNextPosition(col, nextr, 2);
                tmp[i] = curPosition.occupy;
            } else
                tmp[i] = -1;
        }
        winner = isWinner(tmp);
        if (winner != 0) {
            displayWinner(winner, "Horizontally!!!");
            return true;
        }

        // Diagonal Down Wins
        nextc = col;
        nextr = row;
        for (i = 6; i <= 10; i++) {
            nextr++;
            nextc++;
            if ((nextc <= boardType) && (nextr <= boardType)) {
                curPosition = getNextPosition(nextc, nextr, 5);
                tmp[i] = curPosition.occupy;
            } else
                tmp[i] = -1;
        }
        nextr = row;
        nextc = col;
        for (i = 4; i >= 0; i--) {
            nextr--;
            nextc--;
            if ((nextr >= 0) && (nextc >= 0)) {
                curPosition = getNextPosition(nextc, nextr, 7);
                tmp[i] = curPosition.occupy;
            } else
                tmp[i] = -1;
        }
        winner = isWinner(tmp);
        if (winner != 0) {
            displayWinner(winner, "Diagonally Down!!!");
            return true;
        }

        // Diagonal Up Wins
        nextc = col;
        nextr = row;
        for (i = 6; i <= 10; i++) {
            nextc++;
            nextr--;
            if ((nextc <= boardType) && (nextr >= 0)) {
                curPosition = getNextPosition(nextc, nextr, 3);
                tmp[i] = curPosition.occupy;
            } else
                tmp[i] = -1;
        }
        nextc = col;
        nextr = row;
        for (i = 4; i >= 0; i--) {
            nextr++;
            nextc--;
            if ((nextr <= boardType) && (nextc >= 0)) {
                curPosition = getNextPosition(nextc, nextr, 1);
                tmp[i] = curPosition.occupy;
            } else
                tmp[i] = -1;
        }
        winner = isWinner(tmp);
        if (winner != 0) {
            displayWinner(winner, "Diagonally Up!!!");
            return true;
        }

        return false;
    }

    // return 1 if white win!!
    // return 2 if black win!!
    // return 0 if no winner!
    public int isWinner(int[] arr) {
        int i;
        int same = 1;
        //boolean lookBack = false;
        /*
        System.out.print ("isWinner arr: ");
        for (int j = 0; j <= 10; j++)
            System.out.printf ("%d ", arr[j]);
        System.out.print ("\n");
        */
        // look for 5 consecutive color in tmp buffer
        for (i = 0; i < 10; i++) {
            if (arr[i] == arr[i + 1]) {
                // count the same only for 1 or 2, not 0 or -1.
                if ((arr[i] == 1) || (arr[i] == 2))
                    same++;
                //if ((arr [i] == 0 ) || (arr[i] == -1))
                //    lookBack = false;
            } else {
                same = 1;
            }
            // if this true, then i >= 4;
            if (same == 5) {
                i++;
                break;
            }
        }

        // potential win when 5 stones are the same. Do some more checking.
        if ((same == 5) && (i <= 10) && (i >= 5)) {
            if (i < 10) { // don't go out of arr bound.
                // If right is empty or up to the right end of the board, win.
                if ((arr[i + 1] == 0) || (arr[i + 1] == -1)) {
                    //System.out.printf ("1.Winner: %d\n", arr[i]);
                    return arr[i];
                }
                // if right is blocked by the same num, no win.
                else if (arr[i + 1] == arr[i]) {
                    //System.out.print ("2.No Winner\n");
                    return 0;
                }

                // If you're Here: the right is blocked.
                // so we need to look back left [i-5].
                // We can only win if [i -5] is  0 or -1.

                // if left is blocked by 1 or 2, no win!
                else if ((arr[i - 5] == 1) || (arr[i - 5] == 2)) {
                    //System.out.print ("3.No Winner\n");
                    return 0;
                }
                // up to the left end of the board or empty
                if ((arr[i - 5] == -1) || (arr[i - 5] == 0)) {
                    //System.out.printf ("2.Winner: %d\n", arr[i]);
                    return arr[i];
                }

            } else if (i == 10) {
                //System.out.printf ("3.Winner: %d\n", arr[i]);
                return arr[i];
            }
        }
        //System.out.print ("4.No Winner!\n");
        return 0;
    }

    public void displayWinner(int winner, String dir) {
        Toast toast = new Toast(context);
        String who = "";
        CharSequence msg, msg1;
        if (winner == 1) {
            wScore +=1;
            who = "White";
        }
        else if (winner == 2) {
            bScore +=1;
            who = "Black";
        }
        else if (winner == -1) {
            who = "No One";
        }
        CharSequence fScore = String.format("White %s\nBlack %s",wScore,bScore);
        displayScore();

        msg1 =String.format("%s Scored!!", who);
        msg = String.format("%s Won!!", who);
        //change for testing to change number of scores needed to win.
        //todo make this a changable preference
        if(wScore >= 3 || bScore >=3) {
            ContextThemeWrapper ctw = new ContextThemeWrapper(context,R.style.customDialog);
            AlertDialog.Builder winDialog = new AlertDialog.Builder(ctw);
            winDialog.setCancelable(false);
            winDialog.setTitle(msg);
            winDialog.setMessage(fScore);
            winDialog.setPositiveButton(R.string.winReset, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    wScore = 0;
                    bScore = 0;
                    resetGame();
                }
            });
            winDialog.setNegativeButton(R.string.winExit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });
            winDialog.create().show();
        }
        else{

            ContextThemeWrapper mctw = new ContextThemeWrapper(context, R.style.customDialog);
            AlertDialog.Builder singWinDialog1 = new AlertDialog.Builder(mctw);
            singWinDialog1.setCancelable(false);
            singWinDialog1.setTitle(msg1);
            singWinDialog1.setMessage(fScore);
            singWinDialog1.setNeutralButton(R.string.winContinue, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetGame();
                }
            });
            Dialog dlg = singWinDialog1.create();
            Window window = dlg.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.TOP;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            dlg.show();
        }
    }
}

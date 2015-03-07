package group7.gomoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;

/**
 * Created by dany on 2/19/2015.
 */
public class GameMultiplayer extends MainActivity implements Runnable {
    Context context;
    SurfaceView sv;
    ImageView iv;
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

    private static BluetoothSocket gameSocket;
    private OutputStream gameOutputStream;
    private static PrintWriter gamePrintWriterOut;
    private BufferedReader gameBufferedReader;
    private static int who;


    GameMultiplayer(Context context, SurfaceView sv, int boardType, int wScore, int bScore, int who) {
        setupIOStream();
        this.context = context;
        this.sv = sv;
        this.boardType = boardType;
        this.wScore = wScore;
        this.bScore = bScore;
        this.iv = (ImageView) ((Activity) context).findViewById(R.id.turnIndicate);
        mStoneBlackScale = null;
        mStoneWhiteScale = null;
        maxNumStone = (boardType+1) * (boardType+1);
        stoneCounter = 0;
        curParty = 1;

        // 1: Server
        // 2: Client
        this.who = who;

        exitGame = false;
        mLayout = (RelativeLayout) (((Activity) context).findViewById(R.id.boardLayout));
        displayScore();

        // hack to display current turn.
        changeTurn();
        changeTurn();

        addListenerOnBoard();

        startReceivingThread();
    }

    public static void setBluetoothSocket(BluetoothSocket Socket) {
        gameSocket = Socket;
    }

    public void setupIOStream() {
        // Set up messaging streams
        try {
            gameBufferedReader = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
            gameOutputStream = gameSocket.getOutputStream();
            gamePrintWriterOut = new PrintWriter(gameOutputStream);
        } catch (UnknownHostException e) {
            System.out.println("Unknown Server Address");
        } catch (IOException e) {
            System.out.println("Error Creating socket");
        }
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

                posMatrix[i][j].x = innerPosLeft + posOffset;
                posMatrix[j][i].y = innerPosTop + posOffset;
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
                if ((event.getAction() == MotionEvent.ACTION_DOWN) && (curParty == who)) {
                    PutStoneByTouch(curParty, event.getX(), event.getY());
                }
                return true;
            }
        });
    }

    // Only if it's your turn
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
                    row = i;
                    col = j;
                    minDistance = curDistance;
                }
            }
        }

        //if (position.occupy != 0) {
        if (posMatrix[col][row].occupy != 0) {
            System.out.printf("Failed to put stone at (%d,%d)\n", col, row);
            return false;
        }

        PutStone(flag, position, col, row);
        posMatrix[col][row].occupy = flag;
        stoneCounter++;

        checkForWinner(col, row);

        // we have a tie if the board is completely filled.
        if (stoneCounter == maxNumStone)
            displayWinner(-1, "This Game.");

        changeTurn();
        return true;
    }

    //only startReceivingThread will call this function.
    public void updateBoard(int flag, int col, int row) {
        posMatrix[col][row].imageStone = new ImageView(context);

        if (flag == 1)
            posMatrix[col][row].imageStone.setImageBitmap(mStoneWhiteScale);
        else if (flag == 2)
            posMatrix[col][row].imageStone.setImageBitmap(mStoneBlackScale);
        else
            return;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.leftMargin = (int) (posMatrix[col][row].x - gridSize / 2 + 0.5f);
        lp.topMargin = (int) (posMatrix[col][row].y - gridSize / 2 + 0.5f);
        posMatrix[col][row].imageStone.setId((int) (posMatrix[col][row].x * 10 + posMatrix[col][row].y));
        posMatrix[col][row].imageStone.setLayoutParams(lp);

        mLayout.addView(posMatrix[col][row].imageStone);

        // the row and col is confusing!!!! keep this the way it is.
        posMatrix[row][col].occupy = flag;
        stoneCounter++;

        checkForWinner(row, col);

        /*for (int j = 0; j <= boardType; j++) {
            for (int k = 0; k <= boardType; k++)
                System.out.printf("%d, ", posMatrix[j][k].occupy);
            System.out.print("\n");
        }*/

        // we have a tie if the board is completely filled.
        if (stoneCounter == maxNumStone)
            displayWinner(-1, "This Game.");

        changeTurn();
    }

    public void PutStone(int flag, Position position, int col, int row) {

        String moveMsg;
        position.imageStone = new ImageView(context);
        final ImageView image;

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

        // send a message
        if (curParty == who) {
            moveMsg = String.format("updateBoard,%d,%d,%d", flag, row, col);
            sendMessage(moveMsg);
            System.out.printf("%d sending %s\n", flag, moveMsg);
        }

        //position.occupy = flag;
    }

    public void changeTurn() {
        if (curParty == 1) {
            curParty = 2;
            iv.setImageResource(R.drawable.stoneblack);
        } else if (curParty == 2) {
            iv.setImageResource(R.drawable.stonewhite);
            curParty = 1;
        } else {
        }
    }

    public int getCurrentTurn() {
        return curParty;
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
        // White goes first again!
        curParty = 1;
    }

    private void displayScore() {
        TextView whiteView = (TextView) ((Activity) context).findViewById(R.id.whiteScore);
        whiteView.setText("White: " + wScore);
        TextView blackView = (TextView) ((Activity) context).findViewById(R.id.blackScore);
        blackView.setText("Black: " + bScore);
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
    public void checkForWinner(int col, int row) {
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
        }
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
            wScore += 1;
            who = "White";
        } else if (winner == 2) {
            bScore += 1;
            who = "Black";
        } else if (winner == -1) {
            who = "No One";
        }
        CharSequence fScore = String.format("White %s\nBlack %s", wScore, bScore);
        displayScore();

        msg1 = String.format("%s Scored!!", who);
        msg = String.format("%s Won!!", who);
        //change for testing to change number of scores needed to win.
        if (wScore >= 3 || bScore >= 3) {
            ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.customDialog);
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
        } else {

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

    public static void sendMessage(String str) {
        gamePrintWriterOut.println(str);
        gamePrintWriterOut.flush();
        // System.out.printf("Sent: %s\n", str);

    }

    private void startReceivingThread() {
        //isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final String[] msgArray;
                    msgArray = receiveMessage().split(",", 4);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleReceived(msgArray);
                        }
                    });
                }
            }
        }).start();
    }

    private String receiveMessage() {
        String receivedMessage = "";
        try {
            receivedMessage = new String(gameBufferedReader.readLine());
            receivedMessage.trim();
            System.out.printf("Received: %s\n", receivedMessage);
            return receivedMessage;
        } catch (IOException e) {
            System.out.print("error reading stream.");
        }
        return receivedMessage;
    }

    private void handleReceived(String[] msgArray) {
        int occupy, row, col;
        if (msgArray[0].equals("updateBoard")) {
            occupy = Integer.parseInt(msgArray[1]);
            col = Integer.parseInt(msgArray[2]);
            row = Integer.parseInt(msgArray[3]);
            updateBoard(occupy, col, row);
        } else if (msgArray[0].equals("disconnect")) {
            try {
                if (gameSocket != null) {
                    gameSocket.close();
                    System.exit(0);
                }
            } catch (IOException closeException) {
            }
        } else
            System.out.print("handleReceived: Not expecting this msg.\n");
    }
}
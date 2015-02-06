package group7.gomoku;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Lai Xu on 15-2-6.
 */
public class GamePlus implements Runnable {
    Context context;
    SurfaceView sv;
    int curParty;
    Position[][] posMatrix;
    Bitmap mStoneWhiteScale;
    Bitmap mStoneBlackScale;
    float gridSize;
    int boardType;
    //the the layout for the stones
    RelativeLayout mLayout;


    GamePlus(Context context, SurfaceView sv, int boardType) {
        this.context = context;
        this.sv = sv;
        this.boardType = boardType;
        mStoneBlackScale = null;
        mStoneWhiteScale = null;
        curParty = 1;
        mLayout = (RelativeLayout) (((Activity) context).findViewById(R.id.boardLayout));
        addListenerOnBoard();


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

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PutStoneByTouch(curParty, event.getX(), event.getY());
                }
                return true;
            }
        });
    }

    public boolean PutStoneByTouch(int flag, float x, float y) {
        TextView textView = (TextView)((Activity) context).findViewById(R.id.test);
        textView.setText("x="+x+" y="+y+"\n");

        //find right position based on the minimum distance
        Position position = posMatrix[0][0];
        float minDistance = posMatrix[0][0].getSquareDistance(x, y);
        float curDistance = 0;

        int i, j;
        for (i = 0; i < boardType + 1; i++) {
            for (j = 0; j < boardType + 1; j++) {
                curDistance = posMatrix[i][j].getSquareDistance(x, y);
                if (minDistance > curDistance) {
                    position = posMatrix[i][j];
                    minDistance = curDistance;
                }
            }
        }

        if (position.occupy != 0) {
            return false;
        }


        PutStone(flag, position);

        changeTurn();


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
        lp.leftMargin = (int)(position.x - gridSize /2 + 0.5f);
        lp.topMargin = (int)(position.y - gridSize /2 + 0.5f);
        position.imageStone.setId((int)(position.x * 10 + position.y));
        position.imageStone.setLayoutParams(lp);
        mLayout.addView(position.imageStone);

        position.occupy = flag;
    }

    public void changeTurn() {
        if(curParty == 1){
            curParty = 2;
        }else if(curParty == 2){
            curParty = 1;
        }else{
        }
    }

    @Override
    public void run() {

        //Setup

    }
}

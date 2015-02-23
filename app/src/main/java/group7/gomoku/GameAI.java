package group7.gomoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Lai Xu on Feb.6.2015.
 */
public class GameAI {

    int mBoardSize;   //10, 15, 20
    int mStoneColor;  //1: white, 2:black
    int mOpStoneColor; //1: white, 2:black
    int mAICharacter; //0:Offensive, 1:Defensive, 2:Smart
    int[][] mCurBoardMatrix; //0:empty, -1:white, -2:black
    int[][] mOffensiveMatrix;
    int[][] mDefensiveMatrix; //0: safety, 1:There is a stone

    public class StonePos{
        int x;
        int y;

        public StonePos(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public GameAI(int boardSize, int stoneColor) {
        this.mBoardSize = boardSize;
        this.mStoneColor = stoneColor;

        if(stoneColor == 1){
            mOpStoneColor = 2;
        }else{
            mOpStoneColor = 1;
        }

        init();
    }

    public void init() {

        mCurBoardMatrix = new int[mBoardSize + 1][mBoardSize + 1];
        mOffensiveMatrix = new int[mBoardSize + 1][mBoardSize + 1];
        mDefensiveMatrix = new int[mBoardSize + 1][mBoardSize + 1];

        int i, j;
        for (i = 0; i <= mBoardSize; i++) {
            for (j = 0; j <= mBoardSize; j++) {
                mCurBoardMatrix[i][j] = 0;
                mOffensiveMatrix[i][j] = 0;
                mDefensiveMatrix[i][j] = 0;
            }
        }
    }

    public void reset(){
        init();
    }

    public boolean putMyStone(int x, int y) {

        int x1, x2, y1, y2, i, j, w;

        mCurBoardMatrix[x][y]  = mStoneColor;
        mOffensiveMatrix[x][y] = -1 * mStoneColor;
        mDefensiveMatrix[x][y] = -1 * mStoneColor;

        //Updating Vertically
        y1 = y-4;
        if (y1 < 0) {
            y1 = 0;
        }

        y2 = y+4;
        if (y2 > mBoardSize) {
            y2 = mBoardSize;
        }

        j = y-1;
        w = 4;
        while (j >= y1) {

            if (mOffensiveMatrix[x][j] == -1*mOpStoneColor){
                break;
            }

            if (mOffensiveMatrix[x][j] >= 0) {
                mOffensiveMatrix[x][j] += w;
            }

            w--;
            j--;
        }

        j = y - 1;
        w = 4;
        while (j >= y1) {
            if(mDefensiveMatrix[x][j] == -1*mOpStoneColor){
                break;
            }

            if (mDefensiveMatrix[x][j] > 0) {
                mDefensiveMatrix[x][j]-= w;
                if(mDefensiveMatrix[x][j]<0){
                    mDefensiveMatrix[x][j]=0;
                }
            }

            w--;
            j--;
        }

        j = y + 1;
        w = 4;
        while (j <= y2) {
            if (mOffensiveMatrix[x][j] == -1*mOpStoneColor){
                break;
            }

            if (mOffensiveMatrix[x][j] >= 0) {
                mOffensiveMatrix[x][j] += w;
            }

            w--;
            j++;
        }

        j = y + 1;
        w = 4;
        while (j <= y2) {

            if(mDefensiveMatrix[x][j] == -1*mOpStoneColor){
                break;
            }

            if (mDefensiveMatrix[x][j] > 0) {
                mDefensiveMatrix[x][j]-= w;
                if(mDefensiveMatrix[x][j]<0){
                    mDefensiveMatrix[x][j]=0;
                }
            }

            w--;
            j++;
        }


        //Horizontally
        x1 = x - 4;
        if (x1 < 0) {
            x1 = 0;
        }

        x2 = x + 4;
        if (x2 > mBoardSize) {
            x2 = mBoardSize;
        }

        i = x - 1;
        w = 4;

        while (i >= x1) {
            if (mOffensiveMatrix[i][y] == -1*mOpStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][y] >= 0) {
                mOffensiveMatrix[i][y] +=w ;
            }
            w--;
            i--;
        }

        i = x - 1;
        w = 4;
        while (i >= x1) {
            if(mDefensiveMatrix[i][y] == -1*mOpStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][y] > 0) {
                mDefensiveMatrix[i][y] -= w;
                if(mDefensiveMatrix[i][y]<0){
                    mDefensiveMatrix[i][y]=0;
                }
            }
            w--;
            i--;
        }

        i = x + 1;
        w=4;
        while (i <= x2) {
            if (mOffensiveMatrix[i][y] == -1*mOpStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][y] >= 0) {
                mOffensiveMatrix[i][y]+= w;
            }

            w--;
            i++;
        }

        i = x + 1;
        w = 4;
        while (i <= x2) {
            if(mDefensiveMatrix[i][y] == -1*mOpStoneColor){
                break;
            }
            if (mDefensiveMatrix[i][y] > 0) {
                mDefensiveMatrix[i][y]-= w;
                if(mDefensiveMatrix[i][y]<0){
                    mDefensiveMatrix[i][y]=0;
                }
            }

            w--;
            i++;
        }


        //Diagonally Down
        i = x - 1;
        j = y - 1;
        w = 4;
        while (i >= x1 && j>=y1) {
            if (mOffensiveMatrix[i][j] == -1*mOpStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][j] >= 0) {
                mOffensiveMatrix[i][j]+=w;
            }

            i--;
            j--;
            w--;
        }

        i = x - 1;
        j = y - 1;
        w = 4;
        while (i >= x1 && j>=y1) {
            if(mDefensiveMatrix[i][j] == -1*mOpStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][j] > 0) {
                mDefensiveMatrix[i][j]-=w;
                if(mDefensiveMatrix[i][j]<0){
                    mDefensiveMatrix[i][j]=0;
                }
            }

            i--;
            j--;
            w--;
        }

        i = x + 1;
        j = y + 1;
        w = 4;
        while (i <= x2 && j<=y2) {
            if (mOffensiveMatrix[i][j] == -1*mOpStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][j] >= 0) {
                mOffensiveMatrix[i][j]+=w;
            }


            i++;
            j++;
            w--;
        }

        i = x + 1;
        j = y + 1;
        w = 4;
        while (i <= x2 && j<=y2) {
            if(mDefensiveMatrix[i][j] == -1*mOpStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][j] > 0) {
                mDefensiveMatrix[i][j]-=w;
                if(mDefensiveMatrix[i][j]<0){
                    mDefensiveMatrix[i][j]=0;
                }
            }

            i++;
            j++;
            w--;
        }

        //Diagonally Up
        i = x - 1;
        j = y + 1;
        w = 4;
        while (i >= x1 && j<=y2) {
            if (mOffensiveMatrix[i][j] == -1*mOpStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][j] >= 0) {
                mOffensiveMatrix[i][j]+=w;
            }

            i--;
            j++;
            w--;
        }

        i = x - 1;
        j = y + 1;
        w = 4;
        while (i >= x1 && j<=y2) {
            if(mDefensiveMatrix[i][j] == -1*mOpStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][j] > 0) {
                mDefensiveMatrix[i][j]-=w;
                if(mDefensiveMatrix[i][j]<0){
                    mDefensiveMatrix[i][j]=0;
                }
            }

            i--;
            j++;
            w--;
        }

        i = x + 1;
        j = y - 1;
        w = 4;
        while (i <= x2 && j>=y1) {
            if (mOffensiveMatrix[i][j] == -1*mOpStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][j] >= 0) {
                mOffensiveMatrix[i][j]+=w;
            }

            i++;
            j--;
            w--;
        }

        i = x + 1;
        j = y - 1;
        w = 4;
        while (i <= x2 && j>=y1) {
            if(mDefensiveMatrix[i][j] == -1*mOpStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][j] > 0) {
                mDefensiveMatrix[i][j]-=w;
                if(mDefensiveMatrix[i][j]<0){
                    mDefensiveMatrix[i][j]=0;
                }
            }

            i++;
            j--;
            w--;
        }

        return true;
    }


    public boolean putOpStone(int x, int y) {

        int x1, x2, y1, y2, i, j, w;

        mCurBoardMatrix[x][y]  =  mOpStoneColor;
        mOffensiveMatrix[x][y] = -1 * mOpStoneColor;
        mDefensiveMatrix[x][y] = -1 * mOpStoneColor;

        //Updating Vertically
        y1 = y - 4;
        if (y1 < 0) {
            y1 = 0;
        }

        y2 = y + 4;
        if (y2 > mBoardSize) {
            y2 = mBoardSize;
        }

        j = y-1;
        while (j >= y1) {
            if (mDefensiveMatrix[x][j] == -1*mStoneColor){
                break;
            }

            if (mDefensiveMatrix[x][j] >= 0) {
                mDefensiveMatrix[x][j]++;
            }

            j--;
        }

        j = y - 1;
        while (j >= y1) {
            if (mOffensiveMatrix[x][j] == -1*mStoneColor){
                break;
            }

            if (mOffensiveMatrix[x][j] > 0) {
                mOffensiveMatrix[x][j]--;
            }

            j--;
        }

        j = y + 1;
        while (j <= y2) {
            if (mDefensiveMatrix[x][j] == -1*mStoneColor){
                break;
            }

            if (mDefensiveMatrix[x][j] >= 0) {
                mDefensiveMatrix[x][j]++;
            }

            j++;
        }

        j = y + 1;
        while (j <= y2) {

            if (mOffensiveMatrix[x][j] == -1*mStoneColor){
                break;
            }

            if (mOffensiveMatrix[x][j] > 0) {
                mOffensiveMatrix[x][j]--;
            }

            j++;
        }


        //Horizontally
        x1 = x-4;
        if (x1 < 0) {
            x1 = 0;
        }

        x2 = x + 4;
        if (x2 > mBoardSize) {
            x2 = mBoardSize;
        }

        i = x-1;
        while (i >= x1) {
            if (mDefensiveMatrix[i][y] == -1*mStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][y] >= 0) {
                mDefensiveMatrix[i][y]++;
            }

            i--;
        }

        i = x-1;
        while (i >= x1) {
            if (mOffensiveMatrix[i][y] == -1*mStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][y] > 0) {
                mOffensiveMatrix[i][y]--;
            }

            i--;
        }

        i = x + 1;
        while (i <= x2) {
            if (mDefensiveMatrix[i][y] == -1*mStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][y] >= 0) {
                mDefensiveMatrix[i][y]++;
            }

            i++;
        }

        i = x + 1;
        while (i <= x2) {
            if (mOffensiveMatrix[i][y] == -1*mStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][y] > 0) {
                mOffensiveMatrix[i][y]--;
            }

            i++;
        }


        //Diagonally Down
        i = x - 1;
        j = y - 1;
        while (i >= x1 && j>=y1) {
            if (mDefensiveMatrix[x][j] == -1*mStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][j] >= 0) {
                mDefensiveMatrix[i][j]++;
            }

            i--;
            j--;
        }

        i = x - 1;
        j = y - 1;
        while (i >= x1 && j>=y1) {
            if (mOffensiveMatrix[i][j] == -1*mStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][j] > 0) {
                mOffensiveMatrix[i][j]--;
            }

            i--;
            j--;
        }

        i = x + 1;
        j = y + 1;
        while (i <= x2 && j<=y2) {
            if (mDefensiveMatrix[i][j] == -1*mStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][j] >= 0) {
                mDefensiveMatrix[i][j]++;
            }

            i++;
            j++;
        }

        i = x + 1;
        j = y + 1;
        while (i <= x2 && j<=y2) {
            if (mOffensiveMatrix[i][j] == -1*mStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][j] > 0) {
                mOffensiveMatrix[i][j]--;
            }

            i++;
            j++;
        }

        //Diagonally Up
        i = x - 1;
        j = y + 1;
        while (i >= x1 && j<=y2) {
            if (mDefensiveMatrix[i][j] == -1*mStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][j] >= 0) {
                mDefensiveMatrix[i][j]++;
            }

            i--;
            j++;
        }

        i = x - 1;
        j = y + 1;
        while (i >= x1 && j<=y2) {
            if (mOffensiveMatrix[i][j] == -1*mStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][j] > 0) {
                mOffensiveMatrix[i][j]--;
            }

            i--;
            j++;
        }

        i = x + 1;
        j = y - 1;
        while (i <= x2 && j>=y1) {
            if (mDefensiveMatrix[i][j] == -1*mStoneColor){
                break;
            }

            if (mDefensiveMatrix[i][j] >= 0) {
                mDefensiveMatrix[i][j]++;
            }

            i++;
            j--;
        }

        i = x + 1;
        j = y - 1;
        while (i <= x2 && j>=y1) {
            if (mOffensiveMatrix[i][j] == -1*mStoneColor){
                break;
            }

            if (mOffensiveMatrix[i][j] > 0) {
                mOffensiveMatrix[i][j]--;
            }

            i++;
            j--;
        }

        return true;
    }


    public StonePos GetPos(){

        int maxOffensive = 0;
        int maxDefensive = 0;

        List candidatePos = new ArrayList<StonePos>();
        Random randomGenerator = new Random();

        int i, j, x, y;

        for(i=0; i<=mBoardSize; i++){
            for(j=0; j<=mBoardSize; j++){
                if(mOffensiveMatrix[i][j]>maxOffensive){
                    maxOffensive = mOffensiveMatrix[i][j];
                }
            }
        }

        for(i=0; i<=mBoardSize; i++){
            for(j=0; j<=mBoardSize; j++){
                if(mDefensiveMatrix[i][j]>maxDefensive){
                    maxDefensive = mDefensiveMatrix[i][j];
                }
            }
        }

        //The opponent current cannot threat us, so we attack
        if(maxDefensive < 3){

            if(maxOffensive == 0){
                do{
                    i = randomGenerator.nextInt(mBoardSize+1);
                    j = randomGenerator.nextInt(mBoardSize+1);
                }while(mOffensiveMatrix[i][j]!=0);

                return (new StonePos(i,j));
            }

            for(i=0; i<=mBoardSize; i++){
                for(j=0; j<=mBoardSize; j++){
                    if(mOffensiveMatrix[i][j] == maxOffensive){
                        StonePos pos = new StonePos(i,j);

                        if(checkForWinner(i, j, mStoneColor)){
                            return pos;
                        }

                        candidatePos.add(pos);
                    }
                }
            }

            if(!candidatePos.isEmpty()){
                i = randomGenerator.nextInt(candidatePos.size());

                return (StonePos)(candidatePos.get(i));
            }

        }else{

            for(i=0; i<=mBoardSize; i++){
                for(j=0; j<=mBoardSize; j++){
                    if(mDefensiveMatrix[i][j] == maxDefensive){
                        StonePos pos = new StonePos(i,j);

                        if(checkForWinner(i, j, mOpStoneColor)){
                            return pos;
                        }

                        candidatePos.add(pos);
                    }
                }
            }

            if(!candidatePos.isEmpty()){
                i = randomGenerator.nextInt(candidatePos.size());

                return (StonePos)(candidatePos.get(i));
            }

        }

        return null;

    }

    public int getNextPosition (int col, int row, int direction) {
        int nextPosition = 0;
        switch (direction) {
            case 0:
                // direction 0: moveRight
                nextPosition = mCurBoardMatrix[col++][row];
                break;
            case 1:
                // direction 1: moveDownRight
                nextPosition = mCurBoardMatrix[col++][row--];
                break;
            case 2:
                // direction 2: moveDown
                nextPosition = mCurBoardMatrix[col][row--];
                break;
            case 3:
                // direction 3: moveDownLeft
                nextPosition = mCurBoardMatrix[col--][row--];
                break;
            case 4:
                // direction 4: moveLeft
                nextPosition = mCurBoardMatrix[col--][row];
                break;
            case 5:
                // direction 5: moveUpLeft
                nextPosition = mCurBoardMatrix[col--][row++];
                break;
            case 6:
                // direction 6: moveUp
                nextPosition = mCurBoardMatrix[col][row++];
                break;
            case 7:
                // direction 7: moveUpRight
                nextPosition = mCurBoardMatrix[col++][row++];
                break;
            default:
                break;
        }
        return nextPosition;
    }

    // flag: 1 White
    // flag: 2 Black
    public boolean checkForWinner (int col, int row, int flag) {
        int i, winner;
        int  nextc = col;
        int  nextr = row;
        int[] tmp = new int[11];
        int curPosition = 0;
        curPosition = flag;

        // Vertical wins:
        tmp[5] = curPosition;
        // fill the right half of tmp buffer |x|x|x|x|x|*|6|7|8|9|10|
        for (i = 6; i <= 10; i++) {
            nextc++;
            if (nextc <= mBoardSize) {
                curPosition = getNextPosition(nextc, row, 0);
                tmp[i] = curPosition;
            }
            else
                tmp[i] = -1;
        }
        // fill the left half of tmp buffer |0|1|2|3|4|x|x|x|x|x|x|
        nextc = col;
        for (i = 4; i >= 0; i--) {
            nextc--;
            if (nextc >=  0) {
                curPosition = getNextPosition(nextc, row, 4);
                tmp[i] = curPosition;
            }
            else // Since we  have to get 4 positions, we have to put -1
                // when it's outside the board.
                tmp[i] = -1;
        }
        winner = isWinner(tmp);
        if (winner != 0) {
            //displayWinner(winner, "Vertically!!!");
            return true;
        }

        // Horizontal wins:
        nextr = row;
        for (i = 6; i <= 10; i++) {
            nextr++;
            if (nextr <= mBoardSize) {
                curPosition = getNextPosition(col, nextr, 6);
                tmp[i] = curPosition;
            } else
                tmp[i] = -1;
        }
        nextr = row;
        for (i = 4; i >= 0; i--) {
            nextr--;
            if (nextr >= 0) {
                curPosition = getNextPosition(col, nextr, 2);
                tmp[i] = curPosition;
            } else
                tmp[i] = -1;
        }
        winner = isWinner(tmp);
        if (winner != 0) {
            //displayWinner(winner, "Horizontally!!!");
            return true;
        }

        // Diagonal Down Wins
        nextc = col;
        nextr = row;
        for (i = 6; i <= 10; i++) {
            nextr++;
            nextc++;
            if ((nextc <= mBoardSize) && (nextr <= mBoardSize)) {
                curPosition = getNextPosition(nextc, nextr, 5);
                tmp[i] = curPosition;
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
                tmp[i] = curPosition;
            } else
                tmp[i] = -1;
        }
        winner = isWinner(tmp);
        if (winner != 0) {
            //displayWinner(winner, "Diagonally Down!!!");
            return true;
        }

        // Diagonal Up Wins
        nextc = col;
        nextr = row;
        for (i = 6; i <= 10; i++) {
            nextc++;
            nextr--;
            if ((nextc <= mBoardSize) && (nextr >= 0)) {
                curPosition = getNextPosition(nextc, nextr, 3);
                tmp[i] = curPosition;
            } else
                tmp[i] = -1;
        }
        nextc = col;
        nextr = row;
        for (i = 4; i >= 0; i--) {
            nextr++;
            nextc--;
            if ((nextr <= mBoardSize) && (nextc >= 0)) {
                curPosition = getNextPosition(nextc, nextr, 1);
                tmp[i] = curPosition;
            } else
                tmp[i] = -1;
        }
        winner = isWinner(tmp);
        if (winner != 0) {
            //displayWinner(winner, "Diagonally Up!!!");
            return true;
        }

        return false;
    }

    // return 1 if white win!!
    // return 2 if black win!!
    // return 0 if no winner!
    public int isWinner (int []arr) {
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
                if ((arr [i] == 1 ) || (arr[i] == 2))
                    same++;
                //if ((arr [i] == 0 ) || (arr[i] == -1))
                //    lookBack = false;
            }
            else {
                same = 1;
            }
            // if this true, then i >= 4;
            if (same == 5) {
                i++;
                break;
            }
        }

        // potential win when 5 stones are the same. Do some more checking.
        if ((same == 5) && (i <= 10) && (i >=5)) {
            if (i < 10) { // don't go out of arr bound.
                // If right is empty or up to the right end of the board, win.
                if ((arr[i+1] == 0) || (arr[i+1] == -1)) {
                    //System.out.printf ("1.Winner: %d\n", arr[i]);
                    return arr[i];
                }
                // if right is blocked by the same num, no win.
                else if (arr[i+1] == arr[i]) {
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
                if ((arr[i-5] == -1) || (arr[i-5] == 0)) {
                    //System.out.printf ("2.Winner: %d\n", arr[i]);
                    return arr[i];
                }

            }
            else if (i == 10) {
                //System.out.printf ("3.Winner: %d\n", arr[i]);
                return arr[i];
            }
        }
        //System.out.print ("4.No Winner!\n");
        return 0;
    }

    public void PrintMatrix() {
        int i, j;
        /*
        System.out.print("--------");
        for (i = 0; i <= mBoardSize; i++) {
            for (j = 0; j <= mBoardSize; j++) {
                System.out.print(mCurBoardMatrix[i][j] + " ");
            }
            System.out.print("\n");
        }
        */

        System.out.print("--------Offensive Matrix---------\n");

        for (i = 0; i <= mBoardSize; i++) {
            for (j = 0; j <= mBoardSize; j++) {
                //System.out.print(mOffensiveMatrix[i][j] + " ");
                System.out.format("%02d ", mOffensiveMatrix[i][j]);
            }
            System.out.print("\n");
        }

        System.out.print("--------Defensive Matrix---------\n");
        for (i = 0; i <= mBoardSize; i++) {
            for (j = 0; j <= mBoardSize; j++) {
                //System.out.print(mDefensiveMatrix[i][j] + " ");
                System.out.format("%02d ", mDefensiveMatrix[i][j]);
            }
            System.out.print("\n");
        }

        System.out.print("\n");
    }

}

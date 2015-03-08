package group7.gomoku;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Feb.6.2015. created by Lai Xu
 * Mar.3.2015, modified by Lai Xu for integrating biran0079's alpha beta search algorithm to replace
 *                         previous naive one. The brian0079's original code you can find there
 *                         https://code.google.com/p/br-gomoku/source/browse/trunk/src/player/AlphaBetaSearch.java
 */
public class GameAI {

    int mBoardSize;   //10, 15, 20
    int mStoneColor;  //1: white, 2:black
    int mOpStoneColor; //1: white, 2:black
    int[][] mCurBoardMatrix; //0:empty, 1:white, 2:black

    private int maxDepth = 1;
    private int paddingDis = 4;

    final int BLACK_PIECE = 2;
    final int WHITE_PIECE = 1;
    final int NOTHING = 0;


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

        int i, j;
        for (i = 0; i <= mBoardSize; i++) {
            for (j = 0; j <= mBoardSize; j++) {
                mCurBoardMatrix[i][j] = 0;
            }
        }
    }

    public void reset(){
        init();
    }



    class Node {
        StonePos p;
        int f;

        Node(StonePos p, int f) {
            this.p = p;
            this.f = f;
        }
    }


    public StonePos GetPos2() {

        Node res;
        if (mStoneColor == BLACK_PIECE) {
            res = maxValue(mCurBoardMatrix, -10000000, 10000000, 0);
        } else {
            res = minValue(mCurBoardMatrix, -10000000, 10000000, 0);
        }

        return res.p;
    }

    private boolean validPosition(int i, int j){
        int R = mBoardSize+1, C = mBoardSize+1;

        if (i>=0 && i< R &&
                j>=0 && j< C){
            return true;
        }

        return false;
    }

    private int eval(int[][] board) {
        double Ba2 = numOfActive(board, BLACK_PIECE, 2),
                Wa2 = numOfActive(board, WHITE_PIECE, 2),
                Ba3 = numOfActive(board, BLACK_PIECE, 3),
                Wa3 = numOfActive(board, WHITE_PIECE, 3),
                Ba4 = numOfActive(board, BLACK_PIECE, 4),
                Wa4 = numOfActive(board, WHITE_PIECE, 4);
        return (int)(10 * (Ba2 - Wa2) + 100 * (Ba3 - Wa3) + 400 * (Ba4 - Wa4));
    }

    private double numOfActive(int[][] board, int piece, int n) {
        int R = mBoardSize+1, C = mBoardSize+1;
        double res = 0;
        int deadEnd,l;
        int[][] d = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { -1, 0 },
                { -1, -1 }, { 0, -1 }, { 1, -1 } };
        for (int i = 1; i < R - 1; i++)
            for (int j = 1; j < C - 1; j++) {
                for (int k = 0; k < 8; k++) {
                    if (validPosition(i + n * d[k][0], j + n * d[k][1])){
                        deadEnd=2;
                        if(board[i - d[k][0]][j - d[k][1]] != NOTHING)deadEnd--;
                        if(board[i + n * d[k][0]][j + n * d[k][1]] == NOTHING)deadEnd--;
                        if(deadEnd==2)continue;
                        for (l = 0; l < n; l++)
                            if (board[i + l * d[k][0]][j + l * d[k][1]] != piece)
                                break;
                        if (l == n){
                            res+=deadEnd==0?1:0.8;
                        }
                    }
                }
            }
        return res;
    }

    private void bfs(int[][] board, int[][] dis) {
        int R = mBoardSize+1, C = mBoardSize+1;
        int[][] d = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 },
                { -1, -1 }, { 0, -1 }, { 1, -1 } };
        Queue<StonePos> q = new LinkedList<StonePos>();
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++)
                if (dis[i][j] == 0)
                    q.add(new StonePos(i, j));
        if(q.isEmpty())dis[R/2][C/2]=1;
        while (!q.isEmpty()) {
            StonePos t = q.poll();
            int i, j;
            for (int k = 0; k < 8; k++) {
                i = t.x + d[k][0];
                j = t.y + d[k][1];
                if (validPosition(i, j) && dis[i][j] == -1) {
                    dis[i][j] = dis[t.x][t.y] + 1;
                    if (dis[i][j] < paddingDis)
                        q.add(new StonePos(i, j));
                }
            }
        }
    }


    private Node maxValue(int[][] board, int a, int b, int dep) {
        if (playerWins(board, BLACK_PIECE)) {
            //System.out.print("maxValue Black WIN "+a+" "+b+"\n");
            return new Node(null, 100000);
        } else if (playerWins(board, WHITE_PIECE)) {
            //System.out.print("maxValue White WIN "+a+" "+b+"\n");
            return new Node(null, -100000);
        } else if (dep > maxDepth) {
            return new Node(null, eval(board));
        }

        int R = mBoardSize+1, C = mBoardSize+1;
        int[][] dis = new int[R][C];
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++)
                dis[i][j] = (board[i][j] == NOTHING ? -1 : 0);
        bfs(board, dis);
        int f = -10000000, t;
        StonePos p = new StonePos(-1, -1);
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++) {
                if (dis[i][j] > 0) {
                    board[i][j] = BLACK_PIECE;
                    t = minValue(board, a, b, dep + 1).f;
                    //System.out.print("maxValue minValue\n");
                    if (t > f) {
                        f = t;
                        p.x = i;
                        p.y = j;
                    }
                    board[i][j] = NOTHING;
                    if (f >= b)	return new Node(p, f);
                    a = Math.max(a, f);
                }
            }
        return new Node(p, f);
    }

    private Node minValue(int[][] board, int a, int b, int dep) {
        if (playerWins(board, BLACK_PIECE)) {
            //System.out.print("minValue Black WIN\n");
            return new Node(null, 100000);
        } else if (playerWins(board, WHITE_PIECE)) {
            //System.out.print("minValue White WIN\n");
            return new Node(null, -100000);
        } else if (dep > maxDepth) {
            return new Node(null, eval(board));
        }

        int R = mBoardSize+1, C = mBoardSize+1;
        int[][] dis = new int[R][C];
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++)
                dis[i][j] = (board[i][j] == NOTHING ? -1 : 0);
        bfs(board, dis);
        int f = 10000000, t;
        StonePos p = new StonePos(-1, -1);
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++) {
                if (dis[i][j] > 0) {
                    board[i][j] = WHITE_PIECE;
                    t = maxValue(board, a, b, dep + 1).f;
                    //System.out.print("minValue maxValue\n");
                    if (t < f) {
                        f = t;
                        p.x = i;
                        p.y = j;
                    }
                    board[i][j] = NOTHING;
                    if (f <= a) return new Node(p, f);
                    b = Math.min(b, f);
                }
            }
        return new Node(p, f);
    }


    public boolean putMyStone(int x, int y) {

        mCurBoardMatrix[x][y]  = mStoneColor;


        return true;
    }


    public boolean putOpStone(int x, int y) {

        mCurBoardMatrix[x][y]  =  mOpStoneColor;

        return true;
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

        for (i = 0; i <= mBoardSize; i++) {
            for (j = 0; j <= mBoardSize; j++) {
                System.out.print(mCurBoardMatrix[i][j] + " ");
            }
            System.out.print("\n");
        }


        System.out.print("\n");
    }




    private boolean playerWins(int[][]board, int piece){
        int R = mBoardSize+1, C = mBoardSize+1;

        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++){
                if(board[i][j] == piece){
                    if(checkForWinner(i, j, piece)){
                        return true;
                    }
                }

            }

        return false;
    }

}

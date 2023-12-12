package gomoku;

import java.util.Arrays;

public class Engine1 extends AbstractEngine{
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        bestMove = null;
        quickMove();

    }
    private void quickMove(){
        int[][] heuristic = new int[Settings.size][Settings.size];
        for (int[] row : heuristic)
            Arrays.fill(row, -1);


    }
}

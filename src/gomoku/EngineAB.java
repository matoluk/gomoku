package gomoku;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class EngineAB extends AbstractEngine{
    @Override
    public void setUp(Game game, int id, Lock lock, Condition condition) {
        super.setUp(game, id, lock, condition);
        turn = 0;
    }
    private int turn = 0;
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        turn++;
        if (turn < 3) {
            long startTime = System.currentTimeMillis();
            bestMove = EngineTS.quickMove(board);
            //System.out.println("Engine"+ (getCell(board, new Move(7,7)) != opponentStone ? "0" : "1") + " " + (System.currentTimeMillis() - startTime) + " ms");
            stop();
        }else
            new EngineABSearch(board, 4, this, id); //4
    }
}
package gomoku;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class EngineAB extends AbstractEngine{
    private final int heuristic;
    Object[] memo = {new HashMap<ArrayInt, PositionScore>(), new HashMap<ArrayInt, PositionScore>()};
    EngineAB(int heuristic){
        this.heuristic = heuristic;
    }
    @Override
    public void setUp(Game game, int id, Lock lock, Condition condition) {
        super.setUp(game, id, lock, condition);
        turn = 0;
    }
    private int turn = 0;
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        turn++;
        if (turn < 0/* && heuristic == 1*/) {
            bestMove = EngineTS.quickMove(board);
            stop();
        }else
            new EngineABSearch(board, 4, this, id, heuristic); //4
    }

    @Override
    public Object[] getMemory() {
        return memo;
    }
}
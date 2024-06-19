package gomoku;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class EngineAB extends AbstractEngine{
    private final int heuristic;
    Object[] memo = {new HashMap<ArrayInt, PositionScore>(), new HashMap<ArrayInt, PositionScore>()};
    private Set<Move> relatedSquares;
    EngineAB(int heuristic){
        this.heuristic = heuristic;
    }
    @Override
    public void setUp(Game game, int id, Lock lock, Condition condition) {
        super.setUp(game, id, lock, condition);
        relatedSquares = new HashSet<>();
        relatedSquares.add(new Move(7,7));
        turn = 0;
    }
    private int turn = 0;
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        turn++;
        if (turn < 2 && heuristic != 3) {
            bestMove = EngineTS.quickMove(board);
            stop();
        }else {
            int deep = 4;
            if (heuristic == 3)
                deep = 6;
            new EngineABSearch(board, deep, this, id, heuristic); //4
        }
    }

    @Override
    void setCell(Move move, int stone) {
        super.setCell(move, stone);

        relatedSquares.remove(move);
        for (Move relSq : move.getRelatedMoves())
            if (getCell(board, relSq) == empty)
                relatedSquares.add(relSq);
    }

    @Override
    public Object[] getMemory() {
        return memo;
    }
    @Override
    public Set<Move> getRelatedSquares() {
        return relatedSquares;
    }
}
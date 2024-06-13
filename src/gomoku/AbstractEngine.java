package gomoku;

import jdk.jshell.spi.ExecutionControl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public abstract class AbstractEngine implements Engine, SetBestMove{
    private Game game;
    int id;
    private Lock lock;
    private Condition condition;
    volatile Move bestMove;
    int[] board;
    public static final int cellBitSize = 2;
    public static final int empty = 0;
    public static final int myStone = 1;
    public static final int opponentStone = 2;
    public static final int mask = 3;
    public static int getCell(int[] board, Move move){
        return ((board[move.x] >> (move.y * cellBitSize)) & mask);
    }
    private void setCell(Move move, int stone){
        board[move.x] |= stone << (move.y * cellBitSize);
    }

    @Override
    public void setUp(Game game, int id, Lock lock, Condition condition) {
        board = new int[Settings.size];
        for (int i = 0; i < Settings.size; i++)
            board[i] = 0;
        this.game = game;
        this.id = id;
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void opponentMove(Move move) {
        assert (getCell(board, move) == empty);
        setCell(move, opponentStone);
    }

    @Override
    public Object[] getMemory() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setBestMove(Move move) {
        bestMove = move;
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            game.bestMove(id, bestMove);
            condition.signal();
        } finally {
            lock.unlock();
        }
        setCell(bestMove, myStone);
    }

    @Override
    public void quit() {

    }
}

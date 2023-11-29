package gomoku;

import java.util.Random;
import static gomoku.EngineRandom.*;

public class EngineRandomSearch implements Runnable{
    private int[] board;
    private SetBestMove engine;
    private Move bestMove;
    EngineRandomSearch(int[] board, SetBestMove engine){
        this.board = board;
        this.engine = engine;
        Thread search = new Thread(this);
        search.start();
    }
    private int getCell(int x, int y){
        return ((board[x] >> (y * cellBitSize)) & mask);
    }
    @Override
    public void run() {
        Random random = new Random();
        int x = random.nextInt(Settings.size);
        int y = random.nextInt(Settings.size);
        while (getCell(x, y) != empty) {
            x = random.nextInt(Settings.size);
            y = random.nextInt(Settings.size);
        }
        bestMove = new Move(x, y);
        engine.setBestMove(bestMove);
    }
}

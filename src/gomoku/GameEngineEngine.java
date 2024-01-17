package gomoku;

import java.awt.event.WindowEvent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameEngineEngine implements Game{
    private final Engine[] engines;
    private final Graphic graphic;
    private final Board board = new Board();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile MoveResult moveResult = MoveResult.NORMAL;
    private volatile int engineOnTurn;
    private int moveTime = 50;
    private Data data = Data.getInstance();
    GameEngineEngine(Engine[] engines){
        this.engines = engines;
        for (int i = 0; i < engines.length; i++)
            engines[i].setUp(this, i, lock, condition);
        graphic = new Graphic(this, lock, condition, false);
    }
    public void start(int engineStarts) {
        engineOnTurn = engineStarts;
        while (moveResult == MoveResult.NORMAL) {
            long start = System.currentTimeMillis();

            int onTurn = engineOnTurn;
            engines[onTurn].go(-1, -1, -1);
            try {
                lock.lock();
                try {
                    while (onTurn == engineOnTurn) {
                        condition.await();
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            data.plTurns[onTurn]++;
            data.plDur[onTurn] += System.currentTimeMillis() - start;
        }
        if (data.plTurns[0] > data.plTurns[1]) {
            data.win1++;
            for (int i = 0; i <= 1; i++){
                data.win1Dur[i] += data.plDur[i];
                data.win1Turns[i] += data.plTurns[i];
            }
        } else{
            data.win2++;
            for (int i = 0; i <= 1; i++){
                data.win2Dur[i] += data.plDur[i];
                data.win2Turns[i] += data.plTurns[i];
            }
        }
        //graphic.dispose();
    }
    @Override
    public void bestMove(int id, Move move) {
        if (id < 0 || id > 1 || moveResult == MoveResult.WIN)
            return;
        if (id != engineOnTurn)
            throw new RuntimeException();

        Cell player = (id == 0) ? Cell.PLAYER1 : Cell.PLAYER2;
        moveResult = board.move(move, player);
        if (moveResult == MoveResult.DENIED){
            throw new IllegalArgumentException();
        }

        graphic.move(move, player, moveResult);
        engines[1-id].opponentMove(move);
        if(moveResult == MoveResult.WIN) {
            for (int i = 0; i < 5; i++) {
                int x = board.getWinCells().from.x + i*board.getWinCells().xDirection;
                int y = board.getWinCells().from.y + i*board.getWinCells().yDirection;
                graphic.move(new Move(x, y), player, moveResult);
            }
            engineOnTurn = -1;
        }
        else
            engineOnTurn = 1 - engineOnTurn;
    }
}

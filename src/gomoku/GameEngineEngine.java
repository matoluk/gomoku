package gomoku;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameEngineEngine implements Game{
    private final Engine engine;
    //private final Graphic graphic;
    private final Board board = new Board();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private MoveResult moveResult = MoveResult.NORMAL;
    private volatile boolean humanTurn;
    private int moveTime = 50;
    GameEngineEngine(Engine engine){
        this.engine = engine;
        engine.setUp(this, 1, lock, condition);
        //graphic = new Graphic(this, lock, condition);
    }
    public void start(boolean humanStarts){
        humanTurn = humanStarts;
        while(moveResult == MoveResult.NORMAL) {
            if (humanTurn) {
                try {
                    lock.lock();
                    try {
                        while (humanTurn) {
                            condition.await();
                        }
                    } finally {
                        lock.unlock();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } //while (humanTurn) condition.await();
            else {
                engine.go(-1, -1, -1);
                try {
                    lock.lock();
                    try {
                        while (!humanTurn) {
                            condition.await();
                        }
                    } finally {
                        lock.unlock();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                /*try {
                    TimeUnit.MILLISECONDS.sleep(moveTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //engine.stop();*/
            }
        }
    }
    @Override
    public void bestMove(int id, Move move) {
        if (id < 0 || id > 1 || moveResult == MoveResult.WIN)
            return;
        if (id == 0 && !humanTurn)
            return;
        if (id == 1 && humanTurn)
            throw new RuntimeException();

        Cell player = (id == 0) ? Cell.PLAYER1 : Cell.PLAYER2;
        moveResult = board.move(move, player);
        if (moveResult == MoveResult.DENIED){
            if (id == 0)
                return;
            else
                throw new RuntimeException();
        }

        //graphic.move(move, player, moveResult);
        if (id == 0)
            engine.opponentMove(move);
        if(moveResult == MoveResult.WIN) {
            for (int i = 0; i < 5; i++) {
                int x = board.getWinCells().from.x + i*board.getWinCells().xDirection;
                int y = board.getWinCells().from.y + i*board.getWinCells().yDirection;
                //graphic.move(new Move(x, y), player, moveResult);
            }
        }
        else
            humanTurn = !humanTurn;
    }
}

package gomoku;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface Engine {
    void setUp(Game game, int id, Lock lock, Condition condition);

    void opponentMove(Move move);
    void go(int time, int opponentTime, int moveTime);  //milliseconds
    void stop();
    void quit();
}

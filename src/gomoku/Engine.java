package gomoku;

public interface Engine {
    void setUp(Game game, int id);
    void opponentMove(Move move);
    void go(int time, int opponentTime, int moveTime);  //milliseconds
    void stop();
    void quit();
}

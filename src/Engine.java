public interface Engine {
    void opponentMove(Move move);
    void go(int time, int opponentTime, int moveTime);  //milliseconds
    void stop();
    void quit();
}

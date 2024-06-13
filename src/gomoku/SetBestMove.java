package gomoku;

public interface SetBestMove {
    Object[] getMemory();
    void setBestMove(Move move);
    void stop();
}

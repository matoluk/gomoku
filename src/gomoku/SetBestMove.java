package gomoku;

import java.util.Set;

public interface SetBestMove {
    Object[] getMemory();
    Set<Move> getRelatedSquares();
    void setBestMove(Move move);
    void stop();
}

package gomoku;

import java.util.HashMap;
import java.util.Map;

public class EngineMCTS extends AbstractEngine{
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        new EngineMCTSSearch(board, this, id);
    }
}

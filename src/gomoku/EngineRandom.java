package gomoku;

public class EngineRandom extends AbstractEngine{
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        bestMove = null;
        if (getCell(new Move(7,7)) == empty)
            bestMove = new Move(7,7);
        else
            new EngineRandomSearch(board, this);
    }
}

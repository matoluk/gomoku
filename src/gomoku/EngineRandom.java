package gomoku;

public class EngineRandom extends AbstractEngine{
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        bestMove = null;
        if (getCell(board, new Move(7,7)) == empty) {
            bestMove = new Move(7, 7);
            stop();
        }
        else
            new EngineRandomSearch(board, this);
    }
}

package gomoku;

public abstract class AbstractEngine implements Engine, SetBestMove{
    private Game game;
    private int id;
    volatile Move bestMove;
    final int[] board;
    public static final int cellBitSize = 2;
    public static final int empty = 0;
    public static final int myStone = 1;
    public static final int opponentStone = 2;
    public static final int mask = 3;
    AbstractEngine(){
        board = new int[Settings.size];
        for (int i = 0; i < Settings.size; i++)
            board[i] = 0;
    }
    int getCell(Move move){
        return ((board[move.x] >> (move.y * cellBitSize)) & mask);
    }
    private void setCell(Move move, int stone){
        board[move.x] |= stone << (move.y * cellBitSize);
    }

    @Override
    public void setUp(Game game, int id) {
        this.game = game;
        this.id = id;
    }

    @Override
    public void opponentMove(Move move) {
        assert (getCell(move) == empty);
        setCell(move, opponentStone);
    }

    @Override
    public void setBestMove(Move move) {
        bestMove = move;
    }

    @Override
    public void stop() {
        while (bestMove == null)
            Thread.onSpinWait();
        game.bestMove(id, bestMove);
        setCell(bestMove, myStone);
    }

    @Override
    public void quit() {

    }
}

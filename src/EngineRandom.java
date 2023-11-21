import java.util.Random;

public class EngineRandom implements Engine {
    private Game game;
    private Move bestMove;
    private int[] board;
    private final int cellBitSize = 2;
    private final int empty = 0;
    private final int myStone = 1;
    private final int opponentStone = 2;
    private final int mask = 3;
    EngineRandom(){
        board = new int[Settings.size];
        for (int i = 0; i < Settings.size; i++)
            board[i] = 0;
    }
    private int getCell(Move move){
        return (board[move.x] & (mask << (move.y * cellBitSize)));
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void opponentMove(Move move) {
        assert (getCell(move) == empty);
        board[move.x] += opponentStone << (move.y * cellBitSize);
    }

    @Override
    public void go(int time, int opponentTime, int moveTime) {
        Random random = new Random();
        Move move = new Move(random.nextInt(Settings.size), random.nextInt(Settings.size));
        while (getCell(move) != empty)
            move = new Move(random.nextInt(Settings.size), random.nextInt(Settings.size));
        bestMove = move;
    }

    @Override
    public void stop() {
        game.bestMove(bestMove);
    }

    @Override
    public void quit() {

    }
}

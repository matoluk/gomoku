package gomoku;

import java.util.concurrent.TimeUnit;

public class GameHumanEngine implements Game{
    private Engine engine;
    private Graphic graphic;
    private Board board = new Board();
    private MoveResult moveResult = MoveResult.NORMAL;
    private volatile boolean humanTurn;
    private int moveTime = 50;
    GameHumanEngine(Engine engine){
        this.engine = engine;
        engine.setUp(this, 1);
        graphic = new Graphic(this);
    }
    public void start(boolean humanStarts){
        humanTurn = humanStarts;
        while(moveResult == MoveResult.NORMAL) {
            if (humanTurn)
                while (humanTurn) Thread.onSpinWait();
            else {
                engine.go(-1, -1, -1);
                try {
                    TimeUnit.MILLISECONDS.sleep(moveTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                engine.stop();
            }
        }
    }
    @Override
    public void bestMove(int id, Move move) {
        if (id < 0 || id > 1 || moveResult == MoveResult.WIN)
            return;
        if (id == 0 && !humanTurn)
            return;
        if (id == 1 && humanTurn)
            throw new RuntimeException();

        Cell player = (id == 0) ? Cell.PLAYER1 : Cell.PLAYER2;
        moveResult = board.move(move, player);
        if (moveResult == MoveResult.DENIED){
            if (id == 0)
                return;
            else
                throw new RuntimeException();
        }

        graphic.move(move, player, moveResult);
        if (id == 0)
            engine.opponentMove(move);
        if(moveResult == MoveResult.WIN) {
            for (int i = 0; i < 5; i++) {
                int x = board.getWinCells().from.x + i*board.getWinCells().xDirection;
                int y = board.getWinCells().from.y + i*board.getWinCells().yDirection;
                graphic.move(new Move(x, y), player, moveResult);
            }
        }
        else
            humanTurn = !humanTurn;
    }
}

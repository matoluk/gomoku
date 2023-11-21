import java.util.Scanner;
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
        engine.setGame(this);
        graphic = new Graphic(this);
    }
    public void start(boolean humanStarts){
        humanTurn = humanStarts;
        while(moveResult == MoveResult.NORMAL) {
            if (!humanTurn) {
                engine.go(-1, -1, -1);
                try {
                    TimeUnit.MILLISECONDS.sleep(moveTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                engine.stop();
            }
            while (humanTurn) Thread.onSpinWait();
        }
    }
    public void humanMove(Move move){
        if(humanTurn)
            if ((moveResult = board.move(move, Cell.PLAYER2)) != MoveResult.DENIED){
                graphic.move(move, Cell.PLAYER1);
                humanTurn = false;
            }
    }
    @Override
    public void bestMove(Move move) {
        if(humanTurn)
            throw new RuntimeException("Engine tried to play on human's turn.");
        moveResult = board.move(move, Cell.PLAYER2);
        if(moveResult != MoveResult.DENIED) {
            graphic.move(move, Cell.PLAYER2);
            humanTurn = true;
        }
    }
}

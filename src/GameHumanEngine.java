import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GameHumanEngine implements Game{
    private Engine engine;
    private Board board = new Board();
    private MoveResult moveResult = MoveResult.NORMAL;
    private boolean humanTurn;
    private int moveTime = 50;
    GameHumanEngine(Engine engine){
        this.engine = engine;
        engine.setGame(this);
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
            System.out.println(board.getBoard());
            if (humanTurn){
                moveResult = board.move(humanMove(), Cell.PLAYER1);
                humanTurn = false;
            }
        }
    }
    private Move humanMove(){
        Scanner scanner = new Scanner(System.in);
        return new Move(scanner.nextInt(), scanner.nextInt());
    }
    @Override
    public void bestMove(Move move) {
        if(humanTurn)
            throw new RuntimeException("Engine tried to play on human's turn.");
        moveResult = board.move(move, Cell.PLAYER2);
        humanTurn = true;
    }
}

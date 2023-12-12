package gomoku;

enum Cell{
    EMPTY,
    PLAYER1,
    PLAYER2
}
enum MoveResult{
    NORMAL,
    DENIED,
    WIN
}
class LineOfSquares {
    public Move from;
    public int xDirection, yDirection;
    public int values;
    LineOfSquares(int x, int y, int xDirection, int yDirection){
        from = new Move(x, y);
        this.xDirection = xDirection;
        this.yDirection = yDirection;
    }
}
public class Board {
    private Cell[][] board = new Cell[Settings.size][Settings.size];
    private LineOfSquares winCells;
    Board(){
        for (int i = 0; i < Settings.size; i++)
            for (int j = 0; j < Settings.size; j++)
                board[i][j] = Cell.EMPTY;
    }
    public LineOfSquares getWinCells() {
        return winCells;
    }
    public MoveResult move(Move move, Cell player){
        if (board[move.x][move.y] != Cell.EMPTY || player == Cell.EMPTY)
            return MoveResult.DENIED;
        board[move.x][move.y] = player;
        if(checkRow(move, player) || checkColumn(move, player) || checkDiagonal1(move, player) || checkDiagonal2(move, player))
            return MoveResult.WIN;
        return MoveResult.NORMAL;
    }
    private boolean checkRow(Move move, Cell player){
        int x = move.x - Settings.inRow + 1;
        int y = move.y;
        int count = 0;
        while(x < move.x + Settings.inRow){
            if (x >= 0 && x < Settings.size) {
                count = (board[x][y] == player ? count + 1 : 0);
                if (count >= Settings.inRow) {
                    winCells = new LineOfSquares(x, y, -1, 0);
                    return true;
                }
            }
            x++;
        }
        return false;
    }
    private boolean checkColumn(Move move, Cell player){
        int x = move.x;
        int y = move.y - Settings.inRow + 1;
        int count = 0;
        while(y < move.y + Settings.inRow){
            if (y >= 0 && y < Settings.size) {
                count = (board[x][y] == player ? count + 1 : 0);
                if (count >= Settings.inRow) {
                    winCells = new LineOfSquares(x, y, 0, -1);
                    return true;
                }
            }
            y++;
        }
        return false;
    }
    private boolean checkDiagonal1(Move move, Cell player){
        int x = move.x - Settings.inRow + 1;
        int y = move.y - Settings.inRow + 1;
        int count = 0;
        while(x < move.x + Settings.inRow){
            if (x >= 0 && x < Settings.size && y >= 0 && y < Settings.size) {
                count = (board[x][y] == player ? count + 1 : 0);
                if (count >= Settings.inRow) {
                    winCells = new LineOfSquares(x, y, -1, -1);
                    return true;
                }
            }
            x++;
            y++;
        }
        return false;
    }
    private boolean checkDiagonal2(Move move, Cell player){
        int x = move.x - Settings.inRow + 1;
        int y = move.y + Settings.inRow - 1;
        int count = 0;
        while(x < move.x + Settings.inRow){
            if (x >= 0 && x < Settings.size && y >= 0 && y < Settings.size) {
                count = (board[x][y] == player ? count + 1 : 0);
                if (count >= Settings.inRow) {
                    winCells = new LineOfSquares(x, y, -1, 1);
                    return true;
                }
            }
            x++;
            y--;
        }
        return false;
    }
}
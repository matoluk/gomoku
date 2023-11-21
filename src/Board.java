public class Board {
    public enum Cell{
        EMPTY,
        PLAYER1,
        PLAYER2
    }
    public enum MoveResult{
        NORMAL,
        DENIED,
        WIN
    }
    private Cell[][] board = new Cell[Settings.size][Settings.size];
    Board(){
        for (int i = 0; i < Settings.size; i++)
            for (int j = 0; j < Settings.size; j++)
                board[i][j] = Cell.EMPTY;
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
                if (count >= Settings.inRow)
                    return true;
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
                if (count >= Settings.inRow)
                    return true;
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
                if (count >= Settings.inRow)
                    return true;
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
                if (count >= Settings.inRow)
                    return true;
            }
            x++;
            y--;
        }
        return false;
    }
}
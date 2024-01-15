package gomoku;

public class LineOfSquares {
    public Move from;
    public int xDirection, yDirection;
    public int values;
    LineOfSquares(int x, int y, int xDirection, int yDirection){
        from = new Move(x, y);
        this.xDirection = xDirection;
        this.yDirection = yDirection;
    }
}
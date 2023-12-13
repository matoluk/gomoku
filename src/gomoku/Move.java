package gomoku;

public class Move {
    public int x;
    public int y;
    Move(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move))
            return false;
        Move o = (Move) obj;
        return x == o.x && y == o.y;
    }
}
package gomoku;

import java.util.ArrayList;
import java.util.Objects;

public class Move {
    public int x;
    public int y;
    Move(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move o))
            return false;
        return x == o.x && y == o.y;
    }
    public int hashCode() {
        return Objects.hash(x, y);
    }
    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

    public ArrayList<Move> getRelatedMoves() {
        ArrayList<Move> relMoves = new ArrayList<>();
        for (int d = 1; d <= 2; d++) {
            if (x >= d) {
                if (y >= d)
                    relMoves.add(new Move(x - d, y - d));
                relMoves.add(new Move(x - d, y));
                if (y < Settings.size - d)
                    relMoves.add(new Move(x - d, y + d));
            }

            if (y >= d)
                relMoves.add(new Move(x, y - d));
            if (y < Settings.size - d)
                relMoves.add(new Move(x, y + d));

            if (x < Settings.size - d) {
                if (y >= d)
                    relMoves.add(new Move(x + d, y - d));
                relMoves.add(new Move(x + d, y));
                if (y < Settings.size - d)
                    relMoves.add(new Move(x + d, y + d));
            }
        }
        return relMoves;
    }
}
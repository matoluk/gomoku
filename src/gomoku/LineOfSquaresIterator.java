package gomoku;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineOfSquaresIterator implements Iterator<LineOfSquares> {
    private int[] board;
    private int length;
    private int values;
    private LineOfSquares line;
    private int x, y;
    private int xDirection, yDirection;

    public LineOfSquaresIterator(int[] board, int length) {
        this.board = board;
        this.length = length;
        x = 0;
        y = 0;
        xDirection = 0;
        yDirection = 1;
        values = board[x];
    }

    @Override
    public boolean hasNext() {
        return xDirection != -1;
    }

    @Override
    public LineOfSquares next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        LineOfSquares next = new LineOfSquares(x, y, xDirection, yDirection);
        next.values = values % (length * AbstractEngine.cellBitSize);

        if (xDirection == 0 && yDirection == 1){
            if (y + length < Settings.size){
                y++;
                values >>= AbstractEngine.cellBitSize;
                return next;
            }
            if (x < Settings.size - 1){
                y = 0;
                x++;
                values = board[x];
                return next;
            }
            xDirection = 1;
            yDirection = 0;
            x = 0;
            y = 0;
            values = 0;
            for (int i = 0; i < length; i++){
                values |= (board[i] & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == 0){
            if (x + length < Settings.size){
                x++;
                values >>= AbstractEngine.cellBitSize;
                values |= ((board[x+length-1] >> (y * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << ((length-1) * AbstractEngine.cellBitSize);
                return next;
            }
            if (y < Settings.size - 1){
                x = 0;
                y++;
                values = 0;
                for (int i = 0; i < length; i++){
                    values |= ((board[i] >> (y * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            yDirection = 1;
            x = 0;
            y = Settings.size-length;
            values = 0;
            for (int i = 0; i < length; i++){
                values |= ((board[i] >> ((y+i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == 1){
            if (x + length < Settings.size && y + length < Settings.size){
                x++;
                y++;
                values >>= AbstractEngine.cellBitSize;
                values |= ((board[x+length-1] >> ((y+length-1) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << ((length-1) * AbstractEngine.cellBitSize);
                return next;
            }
            if (y > x){
                y = y - x - 1;
                x = 0;
                values = 0;
                for (int i = 0; i < length; i++){
                    values |= ((board[i] >> ((y+i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            if (y > 0){
                x = x - y + 1;
                y = 0;
                values = 0;
                for (int i = 0; i < length; i++){
                    values |= ((board[x+i] >> (i * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            yDirection = -1;
            x = 0;
            y = length - 1;
            values = 0;
            for (int i = 0; i < length; i++){
                values |= ((board[i] >> ((y-i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == -1){
            if (x + length < Settings.size && y - length >= 0){
                x++;
                y--;
                values >>= AbstractEngine.cellBitSize;
                values |= ((board[x+length-1] >> ((y-length+1) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << ((length-1) * AbstractEngine.cellBitSize);
                return next;
            }
            if (y < length && x < Settings.size - length){
                y = y + x + 1;
                x = 0;
                values = 0;
                for (int i = 0; i < length; i++){
                    values |= ((board[i] >> ((y-i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            if (y < Settings.size - 1){
                x = x + y - Settings.size + 2;
                y = Settings.size - 1;
                values = 0;
                for (int i = 0; i < length; i++){
                    values |= ((board[x+i] >> ((y-i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            xDirection = -1;
        }
        return next;
    }
    public static void main(String[] args) {
        Settings.size = 3;

        int[] board = {0, 11, 1};
        LineOfSquaresIterator it = new LineOfSquaresIterator(board, 2);

        while (it.hasNext()) {
            System.out.print(Integer.toBinaryString(it.next().values) + " ");
        }
    }
}
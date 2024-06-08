package gomoku;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineOfSqVariableLengthIt implements Iterator<LineOfSquares> {
    private int[] board;
    private int minLength;
    private int maxLength;
    private int values;
    private int x, y;
    private int xDirection, yDirection;

    public LineOfSqVariableLengthIt(int[] board, int minLength) {
        this.board = board;
        this.minLength = minLength;
        x = 0;
        y = 0;
        xDirection = 0;
        yDirection = 1;
        maxLength = Settings.size - y;
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

        LineOfSquares next = new LineOfSquares(x, y, xDirection, yDirection, maxLength, values);
        if (maxLength > minLength) {
            x += xDirection;
            y += yDirection;
            values >>= AbstractEngine.cellBitSize;
            maxLength--;
            return next;
        }

        if (xDirection == 0 && yDirection == 1){
            if (x < Settings.size - 1){
                y = 0;
                x++;
                values = board[x];
                maxLength = Settings.size;
                return next;
            }
            xDirection = 1;
            yDirection = 0;
            x = 0;
            y = 0;
            values = 0;
            maxLength = Settings.size;
            for (int i = 0; i < maxLength; i++){
                values |= (board[i] & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == 0){
            if (y < Settings.size - 1){
                x = 0;
                y++;
                values = 0;
                maxLength = Settings.size;
                for (int i = 0; i < maxLength; i++){
                    values |= ((board[i] >> (y * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            yDirection = 1;
            x = 0;
            y = Settings.size-minLength;
            values = 0;
            maxLength = minLength;
            for (int i = 0; i < maxLength; i++){
                values |= ((board[i] >> ((y+i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == 1){
            if (y > x){
                y = y - x - 1;
                x = 0;
                values = 0;
                maxLength = Settings.size - y;
                for (int i = 0; i < maxLength; i++){
                    values |= ((board[i] >> ((y+i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            if (y > 0){
                x = x - y + 1;
                y = 0;
                values = 0;
                maxLength = Settings.size - x;
                for (int i = 0; i < maxLength; i++){
                    values |= ((board[x+i] >> (i * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            yDirection = -1;
            x = 0;
            y = minLength - 1;
            values = 0;
            maxLength = minLength;
            for (int i = 0; i < maxLength; i++){
                values |= ((board[i] >> ((y-i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == -1){
            if (x < Settings.size - minLength){
                y = y + x + 1;
                x = 0;
                values = 0;
                maxLength = y + 1;
                for (int i = 0; i < maxLength; i++){
                    values |= ((board[i] >> ((y-i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            if (y < Settings.size - 1){
                x = x + y - Settings.size + 2;
                y = Settings.size - 1;
                values = 0;
                maxLength = Settings.size - x;
                for (int i = 0; i < minLength; i++){
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

        int[] board = {63, 11, 1};
        gomoku.LineOfSqVariableLengthIt it = new gomoku.LineOfSqVariableLengthIt(board, 2);

        while (it.hasNext()) {
            System.out.print(Integer.toBinaryString(it.next().values) + " ");
        }
    }
}
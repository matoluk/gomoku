package gomoku;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineIterator implements Iterator<LineOfSquares> {
    private int[] board;
    private int maxLength;
    private int values;
    private int x, y;
    private int xDirection, yDirection;

    public LineIterator(int[] board) {
        this.board = board;
        x = 0;
        y = 0;
        xDirection = 0;
        yDirection = 1;
        maxLength = Settings.size;
        values = board[x];
    }

    @Override
    public boolean hasNext() {
        return xDirection != -1;
    }

    @Override
    public LineOfSquares next() {
        if (!hasNext())
            throw new NoSuchElementException();

        LineOfSquares next = new LineOfSquares(x, y, xDirection, yDirection, maxLength, values);
        if (xDirection == 0 && yDirection == 1){
            if (x < Settings.size - 1){
                x++;
                values = board[x];
                return next;
            }
            xDirection = 1;
            yDirection = 0;
            x = 0;
            values = 0;
            for (int i = 0; i < maxLength; i++){
                values |= (board[i] & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == 0){
            if (y < Settings.size - 1){
                y++;
                values = 0;
                for (int i = 0; i < maxLength; i++){
                    values |= ((board[i] >> (y * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            yDirection = 1;
            maxLength = Settings.inRow;
            y = Settings.size - maxLength;
            values = 0;
            for (int i = 0; i < maxLength; i++){
                values |= ((board[i] >> ((y+i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == 1){
            if (y > 0){
                y--;
                maxLength++;
                values = 0;
                for (int i = 0; i < maxLength; i++){
                    values |= ((board[i] >> ((y+i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            if (maxLength > Settings.inRow){
                x++;
                maxLength--;
                values = 0;
                for (int i = 0; i < maxLength; i++){
                    values |= ((board[x+i] >> (i * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            yDirection = -1;
            x = 0;
            maxLength = Settings.inRow;
            y = maxLength - 1;
            values = 0;
            for (int i = 0; i < maxLength; i++){
                values |= ((board[i] >> ((y-i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
            }
            return next;
        }

        if (xDirection == 1 && yDirection == -1){
            if (y < Settings.size - 1){
                y++;
                maxLength++;
                values = 0;
                for (int i = 0; i < maxLength; i++){
                    values |= ((board[i] >> ((y-i) * AbstractEngine.cellBitSize)) & AbstractEngine.mask) << (i * AbstractEngine.cellBitSize);
                }
                return next;
            }
            if (maxLength > Settings.inRow){
                x++;
                maxLength--;
                values = 0;
                for (int i = 0; i < maxLength; i++){
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
        Settings.inRow = 2;

        //11 11 11
        //00 10 11
        //00 00 01
        int[] board = {63, 11, 1};
        LineIterator it = new LineIterator(board);

        while (it.hasNext()) {
            System.out.print(Integer.toBinaryString(it.next().values) + " ");
        }
    }
}
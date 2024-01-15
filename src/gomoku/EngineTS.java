package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class EngineTS extends AbstractEngine{
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        bestMove = quickMove(board);

        int deep = 10;
        new WinningThreatSequenceSearch(board, deep, this);
    }
    static public int[][] cellsRating(int[] board, int player){
        int[][] heuristic = new int[Settings.size][Settings.size];
        for (int[] row : heuristic)
            Arrays.fill(row, 0);
        final int three2 = 4;
        final int three3 = 3;
        final int brokenThree = 2;
        final int openTwo = 2;
        final int brokenTwo = 1;

        assert empty == 0 && cellBitSize == 2;
        LineOfSquaresIterator it = new LineOfSquaresIterator(board, 6);
        while (it.hasNext()) {
            LineOfSquares line = it.next();
            if (line.values % player != 0)
                continue;
            switch (line.values / player) {
                case 20 -> {    //___oo_
                    heuristic[line.from.x + line.xDirection * 3][line.from.y + line.yDirection * 3] += three3;
                    heuristic[line.from.x + line.xDirection * 4][line.from.y + line.yDirection * 4] += brokenThree;
                }
                case 68 -> {    //__o_o_
                    heuristic[line.from.x + line.xDirection * 2][line.from.y + line.yDirection * 2] += three3;
                    heuristic[line.from.x + line.xDirection * 4][line.from.y + line.yDirection * 4] += brokenThree;
                }
                case 80 -> {    //__oo__
                    heuristic[line.from.x + line.xDirection * 1][line.from.y + line.yDirection * 1] += three3;
                    heuristic[line.from.x + line.xDirection * 4][line.from.y + line.yDirection * 4] += three3;
                }
                case 272 -> {   //_o_o__
                    heuristic[line.from.x + line.xDirection * 1][line.from.y + line.yDirection * 1] += brokenThree;
                    heuristic[line.from.x + line.xDirection * 3][line.from.y + line.yDirection * 3] += three3;
                }
                case 320 -> {   //_oo___
                    heuristic[line.from.x + line.xDirection * 1][line.from.y + line.yDirection * 1] += brokenThree;
                    heuristic[line.from.x + line.xDirection * 2][line.from.y + line.yDirection * 2] += three3;
                }
                case 260 -> {   //_o__o_
                    heuristic[line.from.x + line.xDirection * 2][line.from.y + line.yDirection * 2] += brokenThree;
                    heuristic[line.from.x + line.xDirection * 3][line.from.y + line.yDirection * 3] += brokenThree;
                }
                case 16 ->   //___o__
                        heuristic[line.from.x + line.xDirection * 3][line.from.y + line.yDirection * 3] += openTwo;
                case 64 ->   //__o___
                        heuristic[line.from.x + line.xDirection * 2][line.from.y + line.yDirection * 2] += openTwo;
            }
        }

        it = new LineOfSquaresIterator(board, 7);
        while (it.hasNext()) {
            LineOfSquares line = it.next();
            if (line.values % player != 0)
                continue;
            switch (line.values / player){
                case 80:    //___oo__
                    heuristic[line.from.x + line.xDirection * 4][line.from.y + line.yDirection * 4] += three2 - 2*three3;
                    break;
                case 272:   //__o_o__
                    heuristic[line.from.x + line.xDirection * 3][line.from.y + line.yDirection * 3] += three2 - 2*three3;
                    break;
                case 320:   //__oo___
                    heuristic[line.from.x + line.xDirection * 2][line.from.y + line.yDirection * 2] += three2 - 2*three3;
                    break;
                case 16:    //____o__
                    heuristic[line.from.x + line.xDirection * 4][line.from.y + line.yDirection * 4] += brokenTwo;
                    break;
                case 256:   //__o____
                    heuristic[line.from.x + line.xDirection * 2][line.from.y + line.yDirection * 2] += brokenTwo;
                    break;
            }
        }

        return heuristic;
    }
    static public Move quickMove(int[] board){
        int[][] cellsRating = cellsRating(board, myStone);

        int max = 1;
        ArrayList<Move> bestMoves = new ArrayList<>();
        for (int x = 0; x < cellsRating.length; x++)
            for (int y = 0; y < cellsRating[x].length; y++){
                if (cellsRating[x][y] > max){
                    max = cellsRating[x][y];
                    bestMoves.clear();
                }
                if (cellsRating[x][y] == max)
                    bestMoves.add(new Move(x, y));
            }

        Random random = new Random();
        if (bestMoves.isEmpty() && getCell(board, new Move(7, 7)) == empty)
            bestMoves.add(new Move(7, 7));
        while (bestMoves.isEmpty()){
            int x = random.nextInt(Settings.size - 2) + 1;
            int y = random.nextInt(Settings.size - 2) + 1;
            if (getCell(board, new Move(x, y)) == empty
                    &&(getCell(board, new Move(x-1, y-1)) != empty
                    || getCell(board, new Move(x-1, y+1)) != empty
                    || getCell(board, new Move(x+1, y-1)) != empty
                    || getCell(board, new Move(x+1, y+1)) != empty
                    || getCell(board, new Move(x-1, y)) != empty
                    || getCell(board, new Move(x+1, y)) != empty
                    || getCell(board, new Move(x, y-1)) != empty
                    || getCell(board, new Move(x, y+1)) != empty))
                bestMoves.add(new Move(x, y));
        }
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }
}

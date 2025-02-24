package gomoku;

import java.util.*;

import static gomoku.AbstractEngine.*;

public class EngineABSearch implements Runnable{
    private final int[] board;
    private final SetBestMove engine;
    private final int deep;
    private final int id;
    private final int heuristic;
    Map<ArrayInt, PositionScore> memo;
    Map<ArrayInt, PositionScore> oldMemo;
    private final Data data = Data.getInstance();
    EngineABSearch(int[] board, int deep, SetBestMove engine, int idEngine, int heuristic){
        this.id = idEngine;
        this.board = board;
        this.engine = engine;
        this.deep = deep;
        this.heuristic = heuristic;

        Object[] memory = engine.getMemory();
        memory[1] = memory[0];
        memory[0] = new HashMap<ArrayInt, PositionScore>();
        memo = (Map<ArrayInt, PositionScore>) memory[0];
        oldMemo = (Map<ArrayInt, PositionScore>) memory[1];

        Thread search = new Thread(this);
        search.start();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        int[] bestMove = bestMove(board, deep);
        for (int i = deep - 1; bestMove == null && i > 0; i--) {
            System.out.println("deep: " + i);
            bestMove = bestMove(board, i);
        }

        engine.setBestMove(getMove(board, bestMove));
        engine.stop();

        //int[] fakeBoard = new int[16];
        //System.arraycopy(bestMove, 0, fakeBoard, 0, 15);
        //float[] pl = PositionEvaluate.getPosScore(fakeBoard).pl;
        //System.out.println("  SCORE red: " + pl[0] + " blue: " +pl[1]);

        data.turns[id]++;
        //data.plDur[id] += System.currentTimeMillis() - startTime;
        //data.printABsearch(id);
        //data.printHeuristic(id);
        //data.printTurnLength(id);
        //data.printTurn();
        data.printSort(id);
        //    data.printMemo();
        data.printAvgHeur(id);
    }
    private Move getMove(int[] board, int[] newBoard){
        for (int x = 0; x < board.length; x++){
            int row = newBoard[x] - board[x];
            if (row > 0){
                int y = 0;
                for (row >>= cellBitSize; row > 0; row >>= cellBitSize)
                    y++;
                return new Move(x,y);
            }
        }
        return null;
    }
    private Set<Move> newRelSq(Set<Move> oldRelSq, int[] oldBoard, Move move) {
        Set<Move> newRelSq = new HashSet<>(oldRelSq);
        newRelSq.remove(move);
        for (Move relSq : move.getRelatedMoves())
            if (getCell(oldBoard, relSq) == empty)
                newRelSq.add(relSq);
        return newRelSq;
    }
    private int[] newBoard(int[] oldBoard, Move move, int stone) {
        assert getCell(oldBoard, move) == empty;
        int[] newBoard = Arrays.copyOf(oldBoard, oldBoard.length);
        newBoard[move.x] |= stone << (move.y * cellBitSize);
        return newBoard;
    }
    private int[] bestMove(int[] board, int deep){
        float myMax = -1;
        ArrayList<int[]> bestMove = new ArrayList<>();
        Set<Move> relatedSquares = engine.getRelatedSquares();
        Iterator it;
        switch (heuristic) {
            case 1, 2 -> it = new MoveIterator(board, myStone);
            case 3 -> it = new MoveIterator2(board, myStone, relatedSquares);
            default -> throw new IllegalArgumentException();
        }
        while (it.hasNext() && myMax != 1){
            int[] newBoard;
            Set<Move> newRelSq = null;
            switch (heuristic) {
                case 1, 2 -> newBoard = (int[]) it.next();
                case 3 -> {
                    Move m = (Move) it.next();
                    newRelSq = newRelSq(relatedSquares, board, m);
                    newBoard = newBoard(board, m, myStone);
                }
                default -> throw new IllegalArgumentException();
            }
            float score = abSearch(newBoard, deep-1, opponentStone, myMax, 1, newRelSq);
            if (score > myMax) {
                myMax = score;
                bestMove.clear();
            }
            if (score == myMax)
                bestMove.add(newBoard);
        }

        if (myMax == -1 || bestMove.isEmpty())
            return null;

        Random random = new Random();
        for (int[] bm : bestMove)
            System.out.print(getMove(board, bm));
        System.out.println(myMax);
        return bestMove.get(random.nextInt(bestMove.size()));
    }
    private float abSearch(int[] board, int deep, int player, float max, float min, Set<Move> relatedSquares){
        data.abSearch[id]++;
        //System.out.print(deep + "," + max + "," + min + "|");

        long start = System.nanoTime();
        float score;
        switch (heuristic) {
            case 1 -> score = heuristic(board);
            case 2, 3 -> score = heuristic2(board);
            default -> throw new IllegalArgumentException();
        }
        data.heuristicCount[id]++;
        data.heuristicTime[id] += System.nanoTime() - start;

        if (deep <= 0 || score == 1 || score == -1)
            return score;

        float myMax = -1;
        float myMin = 1;
        Iterator it;
        switch (heuristic) {
            case 1, 2 -> it = new MoveIterator(board, player);
            case 3 -> it = new MoveIterator2(board, player, relatedSquares);
            default -> throw new IllegalArgumentException();
        }
        while (it.hasNext()) {
            int[] newBoard;
            Set<Move> newRelSq = null;
            switch (heuristic) {
                case 1, 2 -> newBoard = (int[]) it.next();
                case 3 -> {
                    Move m = (Move) it.next();
                    newRelSq = newRelSq(relatedSquares, board, m);
                    newBoard = newBoard(board, m, player);
                }
                default -> throw new IllegalArgumentException();
            }
            if (player == myStone) {
                float next = abSearch(newBoard, deep - 1, 3 - player, Float.max(max, myMax), min, newRelSq);
                if (next > myMax)
                    myMax = next;
                if (myMax > min || myMax == 1)    // less or equal
                    return myMax;
            }
            if (player == opponentStone) {
                float next = abSearch(newBoard, deep - 1, 3 - player, max, Float.min(min, myMin), newRelSq);
                if (next < myMin)
                    myMin = next;
                if (myMin < max || myMin == -1)    // less or equal
                    return myMin;
            }
        }
        if (player == myStone)
            return myMax;
        if (player == opponentStone)
            return myMin;

        throw new IllegalArgumentException("player should be 1 or 2");
    }
    private float heuristic2(int[] board){
        /*ArrayInt arrBoard = new ArrayInt(board);
        if (memo.containsKey(arrBoard)) {
            data.memo++;
            return memo.get(arrBoard).score;
        }*/
        PositionScore posScore;
        /*if (oldMemo.containsKey(arrBoard)) {
            data.oldMemo++;
            posScore = oldMemo.get(arrBoard);
        }
        else {
            data.eval++;*/
        posScore = PositionEvaluate.getPosScore(board);
        //}
        //memo.put(arrBoard, posScore);
        return posScore.score;
    }
    private float heuristic(int[] board){
        //long start = System.nanoTime();
        //data.heuristicCount++;

        /*ArrayInt boardMemo = new ArrayInt(board);
        if (data.heuristicMemo.containsKey(boardMemo))
            return data.heuristicMemo.get(boardMemo);
        boardMemo.copy();*/

        LineOfSquaresIterator it = new LineOfSquaresIterator(board, 5);
        while (it.hasNext()) {
            int value = it.next().values;
            if (value == 341) {
                //data.heuristicTime += System.nanoTime() - start;
                //data.heuristicMemo.put(boardMemo, (float) 1);
                return 1;
            }if (value == 682) {
                //data.heuristicTime += System.nanoTime() - start;
                //data.heuristicMemo.put(boardMemo, (float) -1);
                return -1;
            }
        }
        float myThreat0 = WinningThreatSequenceSearch.getThreats(board, myStone,0).size();
        float opThreat0 = WinningThreatSequenceSearch.getThreats(board, opponentStone,0).size();
        float myThreat1 = WinningThreatSequenceSearch.getThreats(board, myStone,1).size() - myThreat0;
        float opThreat1 = WinningThreatSequenceSearch.getThreats(board, opponentStone,1).size() - opThreat0;
        float myThreat2 = WinningThreatSequenceSearch.getThreats(board, myStone,2).size() - myThreat1 - myThreat0;
        float opThreat2 = WinningThreatSequenceSearch.getThreats(board, opponentStone,2).size() - opThreat1 - opThreat0;
        float myScore = myThreat2 + 10*myThreat1 + 100*myThreat0;
        float opScore = opThreat2 + 10*opThreat1 + 100*opThreat0;

        float score = (myScore - opScore) / (myScore + opScore + 1);

        //float score = PositionEvaluate.getPosScore(board).score;
        //data.heuristicTime += System.nanoTime() - start;
        //data.heuristicMemo.put(boardMemo, score);
        return score;
    }

    class MoveIterator2 implements Iterator<Move> {
        private final TreeMap<Integer, Stack<Move>> moves = new TreeMap<>(Comparator.reverseOrder()); //reverse order?
        public MoveIterator2(int[] board, int player, Set<Move> relSq) {
            int[][] points = new int[Settings.size][Settings.size];

            long start = System.nanoTime();

            LineIterator it = new LineIterator(board);
            while (it.hasNext()) {
                LineOfSquares line = it.next();
                int my = 0;
                int op = 0;
                for (int i = 0; i < line.maxLength; i++) {
                    int stone = line.getValue(i);
                    if (stone == player)
                        my++;
                    else if (stone == 3 - player)
                        op++;

                    if (i >= Settings.inRow - 1) {
                        if (op == 0 && my > 0)
                            for (int j = i - Settings.inRow + 1; j <= i; j++)
                                points[line.from.x + line.xDirection * j][line.from.y + line.yDirection * j] += (3 << my) >> 2;
                        if (op > 0 && my == 0)
                            for (int j = i - Settings.inRow + 1; j <= i; j++)
                                points[line.from.x + line.xDirection * j][line.from.y + line.yDirection * j] += 1 << (op - 1);

                        stone = line.getValue(i - Settings.inRow + 1);
                        if (stone == player)
                            my--;
                        else if (stone == 3 - player)
                            op--;
                    }
                }
            }

            for(Move move : relSq) {
                int score = points[move.x][move.y];
                if (!moves.containsKey(score))
                    moves.put(score, new Stack<>());
                moves.get(score).push(move);
            }

            data.heuristicTimePart2[id] += System.nanoTime() - start;
            data.nearCells[id] += relSq.size();
            data.moveIterator[id]++;
        }
        @Override
        public boolean hasNext() {
            return !moves.isEmpty();
        }

        @Override
        public Move next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Stack<Move> nextMoves = moves.get(moves.firstKey());
            Move next = nextMoves.pop();
            if (nextMoves.isEmpty())
                moves.remove(moves.firstKey());
            return next;
        }
    }

    class MoveIterator implements Iterator<int[]> {
        private TreeMap<Float, ArrayList<int[]>> moves;
        //private final int id;
        public MoveIterator(int[] board, int player) {
            if (player == myStone)
                moves = new TreeMap<>(Comparator.reverseOrder());
            if (player == opponentStone)
                moves = new TreeMap<>();
            assert moves != null;

            boolean win = false;
            for(int x = 0; x < board.length && !win; x++)
                for(int y = 0; y < board.length && !win; y++)
                    if (((board[x] >> (y * cellBitSize)) & mask) == empty){
                        int[] newBoard = Arrays.copyOf(board, board.length);
                        newBoard[x] |= player << (y * cellBitSize);
                        long start = System.nanoTime();
                        //float score = heuristic(newBoard);
                        float score = PositionEvaluate.getPosScore(newBoard).score;
                        data.heuristicTimePart2[id] += System.nanoTime() - start;
                        if (score == (1.5 - player) * 2){
                            win = true;
                            moves.clear();
                        }
                        if (!moves.containsKey(score))
                            moves.put(score, new ArrayList<>());
                        moves.get(score).add(newBoard);
                    }

            if (moves.size() == 1){
                ArrayList<int[]> newArray = new ArrayList<>();
                newArray.add(moves.get(moves.firstKey()).get(0));
                moves.clear();
                moves.put((float) 1, newArray);
                moves.put((float) -1, newArray);
            }

            int near = 0;
            for (float score: moves.keySet())
                if (score != moves.lastKey()){
                    near += moves.get(score).size();
                }
            data.nearCells[id] += near;
            data.moveIterator[id]++;
        }
        @Override
        public boolean hasNext() {
            return moves.size() > 1;
        }

        @Override
        public int[] next() {
            if (!hasNext())
                throw new NoSuchElementException();

            ArrayList<int[]> nextMoves = moves.get(moves.firstKey());
            int[] next = nextMoves.remove(nextMoves.size() - 1);
            if (nextMoves.isEmpty())
                moves.remove(moves.firstKey());
            return next;
        }
    }
}

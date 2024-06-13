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
        if (bestMove == null) {
            //System.out.println("well played!");
            bestMove = bestMove(board, 2);
            if (bestMove == null) {
                //System.out.println("bye!");
                bestMove = bestMove(board, 1);
            }
        }

        engine.setBestMove(getMove(board, bestMove));
        engine.stop();

        //int[] fakeBoard = new int[16];
        //System.arraycopy(bestMove, 0, fakeBoard, 0, 15);
        //float[] pl = PositionEvaluate.getPosScore(fakeBoard).pl;
        //System.out.println("  SCORE red: " + pl[0] + " blue: " +pl[1]);

        data.turns++;
        //data.plDur[id] += System.currentTimeMillis() - startTime;
        data.printABsearch(id);
        //data.printHeuristic();
        //data.printTurnLength();
        //data.printTurn();
        data.printMemo();
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
    private int[] bestMove(int[] board, int deep){
        float myMax = -1;
        int[] bestMove = null;
        MoveIterator it = new MoveIterator(board, myStone);
        while (it.hasNext()){
            int[] move = it.next();
            if (deep > 2) {
                //System.out.print(getMove(board, move)+" ");
            }
            float score = abSearch(move, deep-1, opponentStone, myMax, 1);
            if (score > myMax) {
                myMax = score;
                bestMove = move;
            }
        }
        return bestMove;
    }
    private float abSearch(int[] board, int deep, int player, float max, float min){
        data.abSearch[id]++;
        //System.out.print(deep + "," + max + "," + min + "|");

        //long start = System.nanoTime();
        float score;
        switch (heuristic) {
            case 1 -> score = heuristic(board);
            case 2 -> score = heuristic2(board);
            default -> throw new IllegalArgumentException();
        }
        //data.heuristicCount++;
        //data.heuristicTime += System.nanoTime() - start;

        if (deep <= 0 || score == 1 || score == -1)
            return score;

        if (player == myStone){
            float myMax = -1;
            MoveIterator it = new MoveIterator(board, player);
            while (it.hasNext()){
                float next = abSearch(it.next(), deep-1, 3-player, Float.max(max, myMax), min);
                if (next > myMax)
                    myMax = next;
                if (myMax >= min || myMax == 1)    // less or equal
                    return myMax;
            }
            return myMax;
        }
        if (player == opponentStone){
            float myMin = 1;
            MoveIterator it = new MoveIterator(board, player);
            while (it.hasNext()){
                float next = abSearch(it.next(), deep-1, 3-player, max, Float.min(min, myMin));
                if (next < myMin)
                    myMin = next;
                if (myMin <= max || myMin == -1)    // less or equal
                    return myMin;
            }
            return myMin;
        }

        throw new IllegalArgumentException("player should be 1 or 2");
    }
    private float heuristic2(int[] board){
        ArrayInt arrBoard = new ArrayInt(board);
        if (memo.containsKey(arrBoard)) {
            data.memo++;
            return memo.get(arrBoard).score;
        }
        PositionScore posScore;
        if (oldMemo.containsKey(arrBoard)) {
            data.oldMemo++;
            posScore = oldMemo.get(arrBoard);
        }
        else
            posScore = PositionEvaluate.getPosScore(board);
        memo.put(arrBoard, posScore);
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

    //private int global = 0;
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
                        data.heuristicTimePart2 += System.nanoTime() - start;
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
            //id = global++;
            if (id == 0) {
                //System.out.print("It start: ");
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
            if (id == 0 && moves.size() <= 1) {
                //System.out.println("End");
            }
            return moves.size() > 1;
        }

        @Override
        public int[] next() {
            if (!hasNext())
                throw new NoSuchElementException();
            if (id == 0) {
                //System.out.print(moves.size());
            }

            ArrayList<int[]> nextMoves = moves.get(moves.firstKey());
            int[] next = nextMoves.remove(nextMoves.size() - 1);
            if (nextMoves.isEmpty())
                moves.remove(moves.firstKey());
            return next;
        }
    }
}

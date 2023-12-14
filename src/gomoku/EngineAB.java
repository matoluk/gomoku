package gomoku;

import java.util.*;

import static gomoku.AbstractEngine.*;

public class EngineAB extends AbstractEngine{
    private int turn = 0;
    @Override
    public void go(int time, int opponentTime, int moveTime) {
        turn++;
        if (turn < 3) {
            long startTime = System.currentTimeMillis();
            bestMove = Engine1.quickMove(board);
            System.out.println("RED  ab search " + (System.currentTimeMillis() - startTime) + " ms");
            stop();
        }else
            new EngineABSearch(board, 4, this);
    }
}
class EngineABSearch implements Runnable{
    private final int[] board;
    private final SetBestMove engine;
    private int deep;
    EngineABSearch(int[] board, int deep, SetBestMove engine){
        this.board = board;
        this.engine = engine;
        this.deep = deep;
        Thread search = new Thread(this);
        search.start();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        float myMax = -1;
        int[] bestMove = null;
        MoveIterator it = new MoveIterator(board, myStone);
        while (it.hasNext()){
            int[] move = it.next();
            float next = abSearch(move, deep-1, opponentStone, myMax, 1);
            if (next > myMax) {
                myMax = next;
                bestMove = move;
            }
        }

        if (bestMove == null) {
            new EngineRandomSearch(board, engine);
            System.out.println(System.currentTimeMillis() - startTime + " ms (Resigned)");
            engine.stop();
            return;
        }

        for (int x = 0; x < bestMove.length; x++){
            int row = bestMove[x] - board[x];
            if (row > 0){
                int y = 0;
                for (row >>= cellBitSize; row > 0; row >>= cellBitSize)
                    y++;
                engine.setBestMove(new Move(x, y));
                System.out.println("RED ab search  " + (System.currentTimeMillis() - startTime) + " ms");
                engine.stop();
                return;
            }
        }
    }
    private float abSearch(int[] board, int deep, int player, float max, float min){
        if (deep <= 0)
            return heuristic(board);

        if (player == myStone){
            float myMax = -1;
            MoveIterator it = new MoveIterator(board, player);
            while (it.hasNext()){
                float next = abSearch(it.next(), deep-1, 3-player, Float.max(max, myMax), min);
                if (next > myMax)
                    myMax = next;
                if (myMax > min || myMax == 1)
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
                if (myMin < max || myMin == -1)
                    return myMin;
            }
            return myMin;
        }

        throw new IllegalArgumentException("player should be 1 or 2");
    }
    private float heuristic(int[] board){
        LineOfSquaresIterator it = new LineOfSquaresIterator(board, 5);
        while (it.hasNext()) {
            int value = it.next().values;
            if (value == 341)
                return 1;
            if (value == 682)
                return -1;
        }
        float myThreat0 = WinningThreatSequenceSearch.getThreats(board, myStone,0).size();
        float opThreat0 = WinningThreatSequenceSearch.getThreats(board, opponentStone,0).size();
        float myThreat1 = WinningThreatSequenceSearch.getThreats(board, myStone,1).size() - myThreat0;
        float opThreat1 = WinningThreatSequenceSearch.getThreats(board, opponentStone,1).size() - opThreat0;
        float myThreat2 = WinningThreatSequenceSearch.getThreats(board, myStone,2).size() - myThreat1 - myThreat0;
        float opThreat2 = WinningThreatSequenceSearch.getThreats(board, opponentStone,2).size() - opThreat1 - opThreat0;
        float myScore = myThreat2 + 10*myThreat1 + 100*myThreat0;
        float opScore = opThreat2 + 10*opThreat1 + 100*opThreat0;
        if (myScore == opScore)
            return 0;
        return (myScore - opScore) / (myScore + opScore + 1);
    }

    class MoveIterator implements Iterator<int[]> {
        private TreeMap<Float, ArrayList<int[]>> moves = new TreeMap<>(Comparator.reverseOrder());
        private int deep = 0;
        public MoveIterator(int[] board, int player) {
            if (player == opponentStone)
                moves = new TreeMap<>();
            for(int x = 0; x < board.length; x++)
                for(int y = 0; y < board.length; y++)
                    if (((board[x] >> (y * cellBitSize)) & mask) == empty){
                        int[] newBoard = Arrays.copyOf(board, board.length);
                        newBoard[x] |= player << (y * cellBitSize);
                        float score = heuristic(newBoard);
                        if (!moves.containsKey(score))
                            moves.put(score, new ArrayList<>());
                        moves.get(score).add(newBoard);
                    }
            //deep = moves.size() / 2; //faster & weaker
        }
        @Override
        public boolean hasNext() {
            return moves.size() > deep;
        }

        @Override
        public int[] next() {
            ArrayList<int[]> nextMoves = moves.get(moves.firstKey());
            int[] next = nextMoves.remove(nextMoves.size() - 1);
            if (nextMoves.isEmpty())
                moves.remove(moves.firstKey());
            return next;
        }
    }
}
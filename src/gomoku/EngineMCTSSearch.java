package gomoku;

import java.security.KeyPair;
import java.util.*;
import java.util.stream.Collectors;

import static gomoku.AbstractEngine.*;

public class EngineMCTSSearch implements Runnable {
    private final int[] board;
    private final SetBestMove engine;
    private final int id;
    private static final int SIMULATIONS = 100000;
    private static final double EXPLORATION_PARAM = 1; //Math.sqrt(2);
    private static final Random rand = new Random();
    private final Data data = Data.getInstance();

    EngineMCTSSearch(int[] board, SetBestMove engine, int myId) {
        this.id = myId;
        this.board = board;
        this.engine = engine;

        Thread search = new Thread(this);
        search.start();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        Move bestMove = mctsSearch();

        data.plDur[id] += System.currentTimeMillis() - startTime;
        System.out.println("move time: " + data.plDur[id]);
        System.out.println("evaluateBoard time: " + data.heuristicTime[id]);

        engine.setBestMove(bestMove);
        engine.stop();
    }

    private Move mctsSearch() {
        Map<Move, Integer> result = new HashMap<>();
        Map<Move, Integer> plays = new HashMap<>();
        List<Move> possibleMoves = getPossibleMoves(board);

        for (int i = 0; i < SIMULATIONS; i++) {
            Move move = selectMoveUCT(result, plays, i+1, possibleMoves);

            plays.put(move, plays.getOrDefault(move, 0) + 1);
            result.put(move, result.getOrDefault(move, 0) + simulate(board, move, myStone, possibleMoves));
        }

        // Only print:
        for (int y = 0; y < Settings.size; y++) {
            for (int x = 0; x < Settings.size; x++) {
                Move move = new Move(x, y);
                String out = "";
                if (possibleMoves.contains(move)) {
                    double winrt = (double) result.getOrDefault(move, 0) / plays.getOrDefault(move, 1);
                    out = plays.getOrDefault(move, 0) + ", " + String.format("%.2f", winrt);
                }
                System.out.printf("%-14s", out);
            }
            System.out.println();
        }

        return possibleMoves.stream()
                .max(Comparator.comparingDouble(m -> result.getOrDefault(m, 0) / (double) plays.getOrDefault(m, 1)))
                .orElse(new Move(0, 0));
    }

    private Move selectMoveUCT(Map<Move, Integer> result, Map<Move, Integer> plays, int totalSimulations, List<Move> possibleMoves) {
        double maxValue = Double.NEGATIVE_INFINITY;
        List<Move> bestMoves = new ArrayList<>();

        for (Move m : possibleMoves) {
            int wi = result.getOrDefault(m, 0);
            int ni = plays.getOrDefault(m, 1);
            double value = (double) wi / ni + EXPLORATION_PARAM * Math.sqrt(Math.log(totalSimulations) / ni);

            if (value > maxValue) {
                maxValue = value;
                bestMoves.clear();
                bestMoves.add(m);
            } else if (value == maxValue) {
                bestMoves.add(m);
            }
        }

        return bestMoves.isEmpty() ? possibleMoves.get(rand.nextInt(possibleMoves.size())) : bestMoves.get(rand.nextInt(bestMoves.size()));
    }


    private int simulate(int[] board, Move move, int player, List<Move> allMoves) {
        int[] simBoard = newBoard(board, move, player);
        List<Move> moves = new ArrayList<>(allMoves);
        int indexToRemove = moves.indexOf(move);
        moves.set(indexToRemove, moves.get(moves.size() - 1));
        moves.remove(moves.size() - 1);
        int currentPlayer = myStone + opponentStone - player;

        int result;

        long start = System.currentTimeMillis(); //////////////////
        for (result = evaluateBoard(simBoard); result == 2; result = evaluateBoard(simBoard)) {
            data.heuristicTime[id] += System.currentTimeMillis() - start; //////////////////

            int randomIdx = rand.nextInt(moves.size());
            Move randomMove = moves.get(randomIdx);
            moves.set(randomIdx, moves.get(moves.size() - 1));
            moves.remove(moves.size() - 1);
            simBoard[randomMove.x] |= currentPlayer << (randomMove.y * cellBitSize);
            currentPlayer = myStone + opponentStone - currentPlayer;

            start = System.currentTimeMillis(); //////////////////
        }
        return result;
    }

    private int[] newBoard(int[] oldBoard, Move move, int stone) {
        assert getCell(oldBoard, move) == empty;
        int[] newBoard = Arrays.copyOf(oldBoard, oldBoard.length);
        newBoard[move.x] |= stone << (move.y * cellBitSize);
        return newBoard;
    }

    private List<Move> getPossibleMoves(int[] board) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < Settings.size; x++) {
            for (int y = 0; y < Settings.size; y++) {
                Move move = new Move(x, y);
                if (getCell(board, move) == empty) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    private int evaluateBoard(int[] board) {
        LineOfSquaresIterator it = new LineOfSquaresIterator(board, 5);
        while (it.hasNext()) {
            int value = it.next().values;
            if (value == 341)
                return 1; //win
            if (value == 682)
                return -1; //lose
        }
        for (int y = 0; y < Settings.size; y++) {
            for (int x = 0; x < Settings.size; x++) {
                if (getCell(board, new Move(x, y)) == empty)
                    return 2; //game not over
            }
        }
        return 0; //draw
    }
}

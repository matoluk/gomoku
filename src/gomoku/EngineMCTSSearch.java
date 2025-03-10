package gomoku;

import java.util.*;

import static gomoku.AbstractEngine.*;

public class EngineMCTSSearch implements Runnable {
    private final int[] board;
    private final SetBestMove engine;
    private final int id;
    private static final int SIMULATIONS = 300000;
    private static final double EXPLORATION_PARAM = 1; //Math.sqrt(2);
    private static final Random rand = new Random();
    private final Data data = Data.getInstance();
    private final Node root;

    EngineMCTSSearch(int[] board, SetBestMove engine, int myId) {
        this.id = myId;
        this.board = board;
        this.engine = engine;
        this.root = new Node(null, null, board, opponentStone, false);

        Thread search = new Thread(this);
        search.start();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        Move bestMove = mctsSearch();

        data.plDur[id] = System.currentTimeMillis() - startTime;
        //data.turns[id]++;
        System.out.println("move time: " + data.plDur[id]);
        //System.out.println("evaluateBoard time: " + data.heuristicTime[id]);

        engine.setBestMove(bestMove);
        engine.stop();
    }
    void printTree(Node node, int deep) {
        if (node.visits < 2000)
            return;
        for (int i = 0; i < deep; i++)
            System.out.print(" ");
        System.out.println(node);
        for (Node child : node.children)
            printTree(child, deep + 1);
    }

    private Move mctsSearch() {
        for (int i = 0; i < SIMULATIONS; i++) {
            Node node = selectNode(root);
            int result = node.score == 2 ? simulate(node.board, node.player) : node.score;
            backPropagate(node, result);
        }
        printTree(root, 0);
        Move best = root.getBestMove();
        for (Node n : root.children)
            if (n.move == best)
                System.out.println("*"+n);
        return best;
    }
    private Node selectNode(Node node) {
        while (!node.children.isEmpty()) {
            node = node.selectBestChild();
        }
        if (node.visits == 1) {
            node.expand();
            if (!node.children.isEmpty()) {
                node = node.children.get(rand.nextInt(node.children.size()));
            }
        }
        return node;
    }

    private void backPropagate(Node node, int result) {
        while (node != null) {
            node.visits++;
            node.wins += node.player == myStone ? result : -result;
            node = node.parent;
        }
    }

    private int simulate(int[] board, int player) {
        int[] simBoard = Arrays.copyOf(board, board.length);
        List<Move> moves = getPossibleMoves(simBoard);

        while (!moves.isEmpty()) {
            player = myStone + opponentStone - player;
            int randomIdx = rand.nextInt(moves.size());
            Move randomMove = moves.get(randomIdx);
            moves.set(randomIdx, moves.get(moves.size() - 1));
            moves.remove(moves.size() - 1);
            makeMove(simBoard, randomMove, player);

            if (isGameEnd(simBoard, randomMove))
                return player == myStone ? 1 : -1;
        }
        return 0;
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

    private boolean isGameEnd(int[] board, Move lastMove) {
        int player = getCell(board, lastMove);
        int[] xDir = {1, 0, 1, 1};
        int[] yDir = {0, 1, 1, -1};
        for (int dir = 0; dir < 4; dir++) {
            int count = 0;
            for (Move pos = new Move(lastMove.x - 5 * xDir[dir], lastMove.y - 5 * yDir[dir]);
                 pos.x != lastMove.x + 4 * xDir[dir] || pos.y != lastMove.y + 4 * yDir[dir]; ) {
                pos.x += xDir[dir];
                pos.y += yDir[dir];
                if (pos.x < 0 || pos.x >= Settings.size || pos.y < 0 || pos.y >= Settings.size)
                    continue;
                if (getCell(board, pos) == player)
                    count++;
                else
                    count = 0;
                if (count >= 5)
                    return true;
            }
        }
        return false;
    }

    private class Node {
        Node parent;
        Move move;
        int[] board;
        int player;
        int wins = 0;
        int visits = 0;
        int score = 2; //2=progres, 1=win, 0=draw, -1=loss
        List<Node> children = new ArrayList<>();

        Node(Node parent, Move move, int[] board, int player, boolean leaf) {
            this.parent = parent;
            this.move = move;
            this.board = Arrays.copyOf(board, board.length);
            this.player = player;

            if (move != null && isGameEnd(board, move))
                score = player == myStone ? 1 : -1;
            else if (leaf)
                score = 0;
        }

        void expand() {
            if (score != 2)
                return;
            List<Move> moves = getPossibleMoves(board);
            for (Move m : moves) {
                int[] newBoard = newBoard(board, m, myStone + opponentStone - player);
                children.add(new Node(this, m, newBoard, myStone + opponentStone - player, moves.size() == 1));
            }
        }

        Node selectBestChild() {
            return children.stream()
                    .max(Comparator.comparingDouble(n -> (double) n.wins / (n.visits + 1) +
                            EXPLORATION_PARAM * Math.sqrt(Math.log(visits + 1) / (n.visits + 1))))
                    .orElse(children.get(rand.nextInt(children.size())));
        }

        Move getBestMove() {
            return children.stream()
                    .max(Comparator.comparingDouble(n -> (double) n.wins / (n.visits + 1)))
                    .map(n -> n.move)
                    .orElse(new Move(0, 0));
        }
        @Override
        public String toString() {
            return "Node{" +
                    "move=" + move +
                    ", wins=" + wins +
                    ", visits=" + visits +
                    ", children=" + children.size() +
                    '}';
        }
    }
}

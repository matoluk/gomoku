package gomoku;

import java.util.ArrayList;
import static gomoku.AbstractEngine.*;

class Threat {
    public LineOfSquares position;
    public Move attackerMove;
    public Move[] replayMoves;
    public int category;
    Threat(LineOfSquares position, Move attackerMove, Move[] replayMoves, int category){
        this.position = position;
        this.attackerMove = attackerMove;
        this.replayMoves = replayMoves;
        this.category = category;
    }
}

public class WinningThreatSequenceSearch implements Runnable{
    private final int[] board;
    private final SetBestMove engine;
    private int deep;
    WinningThreatSequenceSearch(int[] board, int deep, SetBestMove engine){
        this.board = board;
        this.engine = engine;
        this.deep = deep;
        Thread search = new Thread(this);
        search.start();
    }
    @Override
    public void run() {
        System.out.println(getThreats(myStone, 2).size());/*
        ArrayList<Threat> threats = getThreats(myStone, 2);
        for (Threat threat : threats)
            System.out.println(threat.attackerMove.x + " " + threat.attackerMove.y);*/
    }

    private int getCell(int x, int y){
        return ((board[x] >> (y * cellBitSize)) & mask);
    }

    private ArrayList<Threat> getThreats(int attacker, int maxCategory){
        ArrayList<Threat> threats = new ArrayList<>();
        int defender = 3 - attacker;

        LineOfSquaresIterator it = new LineOfSquaresIterator(board, 5);
        while (it.hasNext()) {
            LineOfSquares line = it.next();
            Move attackerMove;
            Move[] replayMoves = {};
            if (line.values % attacker != 0)
                continue;
            switch (line.values / attacker) {
                case 85: //_oooo
                    attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                    threats.add(new Threat(line, attackerMove, replayMoves, 0));
                    break;
                case 277: //o_ooo
                    attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                    threats.add(new Threat(line, attackerMove, replayMoves, 0));
                    break;
                case 325: //oo_oo
                    attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                    threats.add(new Threat(line, attackerMove, replayMoves, 0));
                    break;
                case 337: //ooo_o
                    attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                    threats.add(new Threat(line, attackerMove, replayMoves, 0));
                    break;
                case 340: //oooo_
                    attackerMove = new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0);
                    threats.add(new Threat(line, attackerMove, replayMoves, 0));
                    break;
            }
            if (maxCategory == 0)
                continue;
            switch (line.values / attacker) {
                case 21: //__ooo
                    if (line.from.x - line.xDirection < 0 || line.from.y - line.yDirection < 0 || line.from.y - line.yDirection >= Settings.size
                            || getCell(line.from.x - line.xDirection, line.from.y - line.yDirection) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    }
                    attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 69: //_o_oo
                    if (line.from.x - line.xDirection < 0 || line.from.y - line.yDirection < 0 || line.from.y - line.yDirection >= Settings.size
                            || getCell(line.from.x - line.xDirection, line.from.y - line.yDirection) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    }
                    attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 81: //_oo_o
                    if (line.from.x - line.xDirection < 0 || line.from.y - line.yDirection < 0 || line.from.y - line.yDirection >= Settings.size
                            || getCell(line.from.x - line.xDirection, line.from.y - line.yDirection) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    }
                    attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 84: //_ooo_
                    if (line.from.x - line.xDirection < 0 || line.from.y - line.yDirection < 0 || line.from.y - line.yDirection >= Settings.size
                            || getCell(line.from.x - line.xDirection, line.from.y - line.yDirection) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    }
                    if (line.from.x + line.xDirection * 5 >= Settings.size || line.from.y + line.yDirection * 5 < 0 || line.from.y + line.yDirection * 5 >= Settings.size
                            || getCell(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    }
                    break;
                case 261: //o__oo
                    attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 273: //o_o_o
                    attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 276: //o_oo_
                    attackerMove = new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    if (line.from.x + line.xDirection * 5 >= Settings.size || line.from.y + line.yDirection * 5 < 0 || line.from.y + line.yDirection * 5 >= Settings.size
                            || getCell(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    }
                    break;
                case 321: //oo__o
                    attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 324: //oo_o_
                    attackerMove = new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    if (line.from.x + line.xDirection * 5 >= Settings.size || line.from.y + line.yDirection * 5 < 0 || line.from.y + line.yDirection * 5 >= Settings.size
                            || getCell(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    }
                    break;
                case 336: //ooo__
                    attackerMove = new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    if (line.from.x + line.xDirection * 5 >= Settings.size || line.from.y + line.yDirection * 5 < 0 || line.from.y + line.yDirection * 5 >= Settings.size
                            || getCell(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    }
                    break;
            }
        }

        if (maxCategory == 0)
            return threats;

        it = new LineOfSquaresIterator(board, 6);
        while (it.hasNext()) {
            LineOfSquares line = it.next();
            Move attackerMove;
            Move[] replayMoves = {};
            if (line.values % attacker != 0)
                continue;
            switch (line.values / attacker) {
                case 84: //__ooo_
                    attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 276: //_o_oo_
                    attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 324: //_oo_o_
                    attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
                case 336: //_ooo__
                    attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                    threats.add(new Threat(line, attackerMove, replayMoves, 1));
                    break;
            }
            if (maxCategory == 1)
                continue;
            switch (line.values / attacker) {
                case 20: //___oo_
                    if (line.from.x - line.xDirection < 0 || line.from.y - line.yDirection < 0 || line.from.y - line.yDirection >= Settings.size
                            || getCell(line.from.x - line.xDirection, line.from.y - line.yDirection) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4),
                                new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                                new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    }
                    attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3),
                            new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                            new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    break;
                case 68: //__o_o_
                    if (line.from.x - line.xDirection < 0 || line.from.y - line.yDirection < 0 || line.from.y - line.yDirection >= Settings.size
                            || getCell(line.from.x - line.xDirection, line.from.y - line.yDirection) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4),
                                new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                                new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    }
                    attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2),
                            new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                            new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    break;
                case 80: //__oo__
                    if (line.from.x - line.xDirection < 0 || line.from.y - line.yDirection < 0 || line.from.y - line.yDirection >= Settings.size
                            || getCell(line.from.x - line.xDirection, line.from.y - line.yDirection) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4),
                                new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                                new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    }
                    if (line.from.x + line.xDirection * 6 >= Settings.size || line.from.y + line.yDirection * 6 < 0 || line.from.y + line.yDirection * 6 >= Settings.size
                            || getCell(line.from.x + line.xDirection * 6, line.from.y + line.yDirection * 6) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1),
                                new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                                new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    }
                    break;
                case 260: //_o__o_
                    attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3),
                            new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                            new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2),
                            new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                            new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    break;
                case 272: //_o_o__
                    attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3),
                            new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                            new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    if (line.from.x + line.xDirection * 6 >= Settings.size || line.from.y + line.yDirection * 6 < 0 || line.from.y + line.yDirection * 6 >= Settings.size
                            || getCell(line.from.x + line.xDirection * 6, line.from.y + line.yDirection * 6) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1),
                                new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                                new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    }
                    break;
                case 320: //_oo___
                    attackerMove = new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1);
                    replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2),
                            new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                            new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    if (line.from.x + line.xDirection * 6 >= Settings.size || line.from.y + line.yDirection * 6 < 0 || line.from.y + line.yDirection * 6 >= Settings.size
                            || getCell(line.from.x + line.xDirection * 6, line.from.y + line.yDirection * 6) == defender) {
                        attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                        replayMoves = new Move[]{new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1),
                                new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                                new Move(line.from.x + line.xDirection * 0, line.from.y + line.yDirection * 0)};
                        threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    }
                    break;
            }
        }

        if (maxCategory == 1)
            return threats;

        it = new LineOfSquaresIterator(board, 7);
        while (it.hasNext()) {
            LineOfSquares line = it.next();
            Move attackerMove;
            Move[] replayMoves = new Move[]{
                    new Move(line.from.x + line.xDirection * 5, line.from.y + line.yDirection * 5),
                    new Move(line.from.x + line.xDirection * 1, line.from.y + line.yDirection * 1)};
            if (line.values % attacker != 0)
                continue;
            switch (line.values / attacker) {
                case 80: //___oo__
                    attackerMove = new Move(line.from.x + line.xDirection * 4, line.from.y + line.yDirection * 4);
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    break;
                case 272: //__o_o__
                    attackerMove = new Move(line.from.x + line.xDirection * 3, line.from.y + line.yDirection * 3);
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    break;
                case 320: //__oo___
                    attackerMove = new Move(line.from.x + line.xDirection * 2, line.from.y + line.yDirection * 2);
                    threats.add(new Threat(line, attackerMove, replayMoves, 2));
                    break;
            }
        }
        return threats;
    }
}

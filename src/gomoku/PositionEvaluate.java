package gomoku;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.Input;
import java.io.FileOutputStream;
import java.io.IOException;

public class PositionEvaluate {
    private static final int four = 25;
    private static final int _three__ = 10;
    private static final int _broken_three_ = 8;
    private static final int __three__ = 12;
    private static final int three__ = 6;
    private static final int __two__ = 4;
    private static final int _two___ = 3;
    private static final int _broken_two__ = 3;
    private static final int _broken__two_ = 2;
    private static final int __broken_two__ = 4;
    private static final int __two___ = 5;
    private static final int two___ = 0; // 1
    private static final int one = 0; // 1
    private static Map<Integer, PositionScore>[] maps = null;
    private static int code(int number, int from, int to) {
        int ans = 0;
        for (int pow = 1; number != 0; pow *= to) {
            ans += (number % from) * pow;
            number /= from;
        }
        return ans;
    }

    static public PositionScore getPosScore2(int[] board) {
        PositionScore score = new PositionScore();

        LineOfSqVariableLengthIt it = new LineOfSqVariableLengthIt(board, 5);
        while (it.hasNext()) {
            LineOfSquares line = it.next();
            if (line.values == 0)
                continue;
            int val5 = code(line.values % (1 << AbstractEngine.cellBitSize * 5),
                    1 << AbstractEngine.cellBitSize, 10);

            int p = 0;
            for (int i = 0; i < 5; i++) {
                int stone = line.getValue(i);
                if (stone == 0)
                    continue;
                if (p != 0 && stone != p) {
                    p = 0;
                    break;
                }
                p = stone;
            }
            if (p == 0)
                continue;
            assert p == 1 || p == 2;

            val5 /= p;

            if (board.length != 15) {///////////////////////////////////////////////////////////////////////////////////
                //System.out.println(line + "\t\t" + val5);
            }

            if (val5 == 11111) {
                score.score = (p == 1) ? 1 : -1;
                score.set(p, 1);
                score.set(3-p, (float) (-0.5));
                return score;
            }
            if (val5 == 1111 || val5 == 10111 || val5 == 11011 || val5 == 11101 || val5 == 11110)
                score.add(p, four);
            if (val5 == 11100 || val5 == 11010 || val5 == 11001 || val5 == 10110 || val5 == 10101 ||
                    val5 == 10011 || val5 == 1110 || val5 == 1101 || val5 == 1011 || val5 == 111)
                score.add(p, three__);
            if (val5 == 11000 || val5 == 11)
                score.add(p, two___);

            if (line.maxLength == 5 || line.getValue(0) != 0 || line.getValue(5) != 0)
                continue;

            if (val5 == 11100 || val5 == 1110)
                score.add(p, _three__ - 2 * three__);
            if (val5 == 11010 || val5 == 10110)
                score.add(p, _broken_three_ - 2 * three__);
            if (val5 == 1100)
                score.add(p, __two__);
            if (val5 == 11000 || val5 == 110)
                score.add(p, _two___ - two___);
            if (val5 == 10100 || val5 == 1010)
                score.add(p, _broken_two__);
            if (val5 == 10010)
                score.add(p, _broken__two_);
            if (val5 == 1000 || val5 == 100)
                score.add(p, one);

            if (line.maxLength == 6)
                continue;

            if (line.getValue(1) == 0 && line.getValue(6) == 0) {
                if (val5 == 11100)
                    score.add(p, __three__ - 2 * _three__ + three__);
                if (val5 == 10100)
                    score.add(p, __broken_two__ - 2 * _broken_two__);
                if (val5 == 11000 || val5 == 1100)
                    score.add(p, __two___ - __two__ - _two___);
                if (val5 == 1000)
                    score.add(p, -one);
            }

            if (line.maxLength == 7 || line.getValue(7) != 0)
                continue;

            if (val5 == 1011010)
                score.add(p, __three__ - 2 * _broken_three_);
            if (val5 == 11000)
                score.add(p, __two__ - __two___);
        }

        score.eval();
        return score;
    }

    static public PositionScore getPosScore(int[] board) {
        loadMaps();
        PositionScore score = new PositionScore();

        LineIterator it = new LineIterator(board);
        while (it.hasNext()) {
            LineOfSquares line = it.next();
            PositionScore lineScore = maps[line.maxLength].get(line.values);
            if (lineScore == null)
                continue;
            if (lineScore.score == 1 || lineScore.score == -1)
                return lineScore;
            score.add(lineScore);
        }

        score.eval();
        return score;
    }

    private static void loadMaps() {
        if (maps == null) {
            Kryo kryo = new Kryo();
            kryo.register(Map[].class);
            kryo.register(HashMap.class);
            kryo.register(PositionScore.class);
            kryo.register(float[].class);

            try (Input input = new Input(new FileInputStream("map.kryo"))) {
                maps = kryo.readObject(input, Map[].class);
                System.out.println("Mapy úspešne načítané.");
            } catch (IOException e) {
                System.out.println("Chyba pri načítaní mapy: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        int origSize = Settings.size;
        Map<Integer, PositionScore>[] maps = new Map[origSize + 1];
        for (Settings.size = Settings.inRow; Settings.size <= origSize; Settings.size++) {
            int[] board = new int[Settings.size];
            maps[Settings.size] = new HashMap<>();

            int max = (int) Math.pow(3, Settings.size);
            int percent = max/100;
            for (int i = 1; i < max; i++) {
                if ((i % 100000) == 0) { // % percent
                    System.out.println((i / percent) + "%");
                }
                int n = code(i, 3, 4);
                board[0] = n;
                PositionScore score = getPosScore(board);
                if (score.score != 0)
                    maps[Settings.size].put(n, score);
            }
            System.out.println("Map["+Settings.size+"].size: " + maps[Settings.size].size());
        }
        Settings.size = origSize;

        Kryo kryo = new Kryo();
        kryo.register(Map[].class);
        kryo.register(HashMap.class);
        kryo.register(PositionScore.class);
        kryo.register(float[].class);
        try (Output output = new Output(new FileOutputStream("map.kryo"))) {
            kryo.writeObject(output, maps);
            System.out.println("Zapis: " + maps[Settings.size].size());
        } catch (IOException e) {
            System.out.println("Chyba pri zápise mapy: " + e.getMessage());
        }
    }
}

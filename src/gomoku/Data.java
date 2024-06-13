package gomoku;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Data implements Serializable {
    private static final Data data = new Data();
    private Data() {
    }
    public static Data getInstance() {
        return data;
    }
    public Map<ArrayInt, Float> heuristicMemo = new HashMap<>(1 << 30);
    public int[] plTurns = {0, 0};
    public long[] plDur = {0, 0};
    public int win1 = 0;
    public int[] win1Turns = {0, 0};
    public long[] win1Dur = {0, 0};
    public int win2 = 0;
    public int[] win2Turns = {0, 0};
    public long[] win2Dur =  {0, 0};

    public void printAvgTurnDuration(){
        System.out.println("FirstWin "+win1+"times. Avg durations:");
        if (win1 > 0) {
            System.out.println("1. " + (win1Dur[0] / win1Turns[0]));
            System.out.println("2. " + (win1Dur[1] / win1Turns[1]));
        }
        System.out.println("SecondWin "+win2+"times. Avg durations:");
        if (win2 > 0) {
            System.out.println("1. " + (win2Dur[0] / win2Turns[0]));
            System.out.println("2. " + (win2Dur[1] / win2Turns[1]));
        }
    }
    public int turns = 0;
    public int[] moveIterator = {0,0};
    public int[] nearCells = {0,0};
    public int[] abSearch = {0,0};
    public void printABsearch(int id){
        System.out.println("Considered " + nearCells[id] + "\tSearched " + abSearch[id] + "\t= " + (abSearch[id]*100/nearCells[id]) + "%");
        System.out.println("Avg " + (nearCells[id] / turns) + "\t" + (abSearch[id] / turns));
    }
    public long heuristicTime = 0;
    public long heuristicTimePart2 = 0;
    public int heuristicCount = 0;
    public void printHeuristic(){
        System.out.println("Heuristic time per turn: " + (heuristicTime/1000000/turns) + "ms\tCount: " + heuristicCount + "\tMemoized: " + heuristicMemo.keySet().size());
    }
    public void printTurnLength(){
        System.out.println("Avg turn length: "+ (plDur[0] / turns));

    }
    long lastTurn = 0;
    long lastHeur = 0;
    long lastHeur2 = 0;
    public void printTurn(){
        System.out.println("Last turn: "+ (plDur[0] - lastTurn) + "\t(Heuristic: " + (heuristicTime - lastHeur)/1000000 + ")\t(Turn sort: " + (heuristicTimePart2 - lastHeur2)/1000000 + ")");
        lastTurn = plDur[0];
        lastHeur = heuristicTime;
        lastHeur2 = heuristicTimePart2;
    }
    int memo = 0, oldMemo = 0;
    void printMemo(){
        System.out.println("Memo: "+memo+"\toldMemo: "+oldMemo);
    }
}

package gomoku;

import java.io.Serializable;

public class PositionScore implements Serializable {
    public final float[] pl = {0, 0};
    public float score = 0;
    public float eval(){
        return score = (pl[0] - pl[1]) / (pl[0] + pl[1] + 1);
    }
    public void add(int player, float points){
        pl[player - 1] += points;
    }
    public void add(PositionScore lineScore) {
        pl[0] += lineScore.pl[0];
        pl[1] += lineScore.pl[1];
    }
    public void set(int player, float points){
        pl[player - 1] = points;
    }

    @Override
    public String toString() {
        return "("+(int)pl[0]+":"+(int)pl[1]+")";
    }
}

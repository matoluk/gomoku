package gomoku;

public class PositionScore {
    public final float[] pl = {0, 0};
    public float score = 0;
    public float eval(){
        return score = (pl[0] - pl[1]) / (pl[0] + pl[1] + 1);
    }
    public void add(int player, float points){
        if (points == 0)
            return;
        pl[player - 1] += points;
        //System.out.println("add("+player+", "+points+")");
    }
}

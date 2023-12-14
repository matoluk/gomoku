package gomoku;

import javax.swing.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;

public class Graphic extends JFrame {
    BoardPanel panel;
    Graphic(Game game, Lock lock, Condition condition, boolean humanPlays){
        panel = new BoardPanel(game, lock, condition, humanPlays);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    public void move(Move move, Cell player, MoveResult type){
        panel.move(move, player, type);
    }
}

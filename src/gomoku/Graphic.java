package gomoku;

import javax.swing.*;

public class Graphic extends JFrame {
    BoardPanel panel;
    Graphic(GameHumanEngine game){
        panel = new BoardPanel(game);
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

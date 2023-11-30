package gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class BoardPanel extends JPanel {
    private final Lock lock;
    private final Condition condition;
    private List<Shape> blue = new ArrayList<>();
    private List<Shape> red = new ArrayList<>();
    private List<Shape> darkBlue = new ArrayList<>();
    private List<Shape> darkRed = new ArrayList<>();
    private GameHumanEngine game;
    BoardPanel(GameHumanEngine game, Lock lock, Condition condition){
        this.game = game;
        this.lock = lock;
        this.condition = condition;
        int boardSize = Settings.size * Settings.cellSize + Settings.lineWidth;
        this.setPreferredSize(new Dimension(boardSize, boardSize));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = (e.getX() - Settings.lineWidth/2) / Settings.cellSize;
                int y = (e.getY() - Settings.lineWidth/2) / Settings.cellSize;
                if (x >= 0 && y >= 0 && x < Settings.size && y < Settings.size) {
                    lock.lock();
                    try {
                        game.bestMove(0, new Move(x, y));
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        });
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;
        g2D.setStroke(new BasicStroke(Settings.lineWidth));
        for (int i = 0; i < Settings.size; i++)
            for (int j = 0; j < Settings.size; j++) {
                int x = Settings.lineWidth / 2 + i * Settings.cellSize;
                int y = Settings.lineWidth / 2 + j * Settings.cellSize;
                g2D.drawRect(x, y, Settings.cellSize, Settings.cellSize);
            }
        g2D.setPaint(Color.blue);
        for (Shape shape : blue)
            g2D.draw(shape);
        g2D.setPaint(Color.red);
        for (Shape shape : red)
            g2D.draw(shape);
        g2D.setPaint(new Color(0,0,127));
        for (Shape shape : darkBlue)
            g2D.draw(shape);
        g2D.setPaint(new Color(127, 0, 0));
        for (Shape shape : darkRed)
            g2D.draw(shape);
    }
    public void move(Move move, Cell player, MoveResult type){
        int x = Settings.lineWidth*3/2 + move.x * Settings.cellSize;
        int y = Settings.lineWidth*3/2 + move.y * Settings.cellSize;
        int width = Settings.cellSize - 2 * Settings.lineWidth;

        if (player == Cell.PLAYER1){
            if (type == MoveResult.NORMAL) {
                blue.add(new Line2D.Double(x, y, x + width, y + width));
                blue.add(new Line2D.Double(x, y + width, x + width, y));
            }if (type == MoveResult.WIN) {
                darkBlue.add(new Line2D.Double(x, y, x + width, y + width));
                darkBlue.add(new Line2D.Double(x, y + width, x + width, y));
            }
        }
        if (player == Cell.PLAYER2) {
            if (type == MoveResult.NORMAL)
                red.add(new Ellipse2D.Double(x, y, width, width));
            if (type == MoveResult.WIN)
                darkRed.add(new Ellipse2D.Double(x, y, width, width));
        }

        repaint();
    }
}

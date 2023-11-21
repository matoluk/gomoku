import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {
    private List<Shape> stones = new ArrayList<>();
    private GameHumanEngine game;
    BoardPanel(GameHumanEngine game){
        this.game = game;
        int boardSize = Settings.size * Settings.cellSize + Settings.lineWidth;
        this.setPreferredSize(new Dimension(boardSize, boardSize));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = (e.getX() - Settings.lineWidth/2) / Settings.cellSize;
                int y = (e.getY() - Settings.lineWidth/2) / Settings.cellSize;
                game.humanMove(new Move(x,y));
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
        for (Shape shape : stones) {
            if (shape instanceof Line2D)
                g2D.setPaint(Color.blue);
            else if (shape instanceof Ellipse2D)
                g2D.setPaint(Color.red);
            else
                g2D.setPaint(Color.black);
            g2D.draw(shape);
        }
    }
    public void move(Move move, Cell player){
        int x = Settings.lineWidth*3/2 + move.x * Settings.cellSize;
        int y = Settings.lineWidth*3/2 + move.y * Settings.cellSize;
        int width = Settings.cellSize - 2 * Settings.lineWidth;

        if (player == Cell.PLAYER1){
            stones.add(new Line2D.Double(x, y, x + width, y + width));
            stones.add(new Line2D.Double(x, y + width, x + width, y));
        }
        if (player == Cell.PLAYER2)
            stones.add(new Ellipse2D.Double(x, y, width, width));

        repaint();
    }
}

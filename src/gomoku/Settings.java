package gomoku;

import java.awt.*;

public class Settings {
    public static int size = 15;
    public static int inRow = 5;
    static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static final int minSize = Integer.min(screenSize.height, screenSize.width);
    public static int cellSize = minSize / 17;
    public static int lineWidth = minSize / 300;
}

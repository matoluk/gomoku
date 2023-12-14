package gomoku;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose game mode (1-HumanEngine, 2-EngineEngine): ");
        assert (scanner.hasNextInt());
        int mode = scanner.nextInt();
        if (mode == 1){
            Engine engine = chooseEngine(scanner);
            if (engine == null)
                return;
            GameHumanEngine game = new GameHumanEngine(engine);
            game.start(true);
        } else if (mode == 2) {
            Engine[] engines = {chooseEngine(scanner), chooseEngine(scanner)};
            if (engines[0] == null || engines[1] == null)
                return;
            GameEngineEngine game = new GameEngineEngine(engines);
            game.start(0);
        }
    }
    private static Engine chooseEngine(Scanner scanner){
        System.out.println("Choose engine (1-Random), (2-Engine1), (3-EngineAB): ");
        assert (scanner.hasNextInt());
        int type = scanner.nextInt();
        if (type == 1)
            return new EngineRandom();
        if (type == 2)
            return new Engine1();
        if (type == 3)
            return new EngineAB();
        return null;
    }
    private static void human(){

    }
    private static void engine(){

    }
}

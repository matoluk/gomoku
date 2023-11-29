package gomoku;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose game mode (1-HumanEngine, 2-EngineEngine): ");
        assert (scanner.hasNextInt());
        int mode = scanner.nextInt();
        if (mode == 1){
            Engine engine;
            System.out.println("Choose engine (1-Random): ");
            assert (scanner.hasNextInt());
            int type = scanner.nextInt();
            if (type == 1){
                engine = new EngineRandom();
            } else
                return;
            GameHumanEngine game = new GameHumanEngine(engine);
            game.start(true);
        } else if (mode == 2) {
            Engine engines[] = new Engine[2];
            for (int i = 0; i < 2; i++) {
                System.out.println("Choose engine"+(i+1)+" (1-Random): ");
                assert (scanner.hasNextInt());
                int type = scanner.nextInt();
                if (type == 1) {
                    engines[1] = new EngineRandom();
                } else
                    return;
            }
            //GameEngineEngine game = new GameEngineEngine(engines);
            //game.start(true);
        }
    }
    private static void human(){

    }
    private static void engine(){

    }
}

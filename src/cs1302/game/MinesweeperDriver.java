package cs1302.game;

import cs1302.game.MinesweeperGame;
import java.util.Scanner;
import java.io.File;

/**
 * The main class for the MinesweeperGame. This class is responsible for reading
 * Standard input user commands to play the MinesweeperGame. This class is also
 * responsible for accepting the seed file path necessarry to set the dimensions
 * for an instance of a minesweeper game.
 * @param args an array of command line arguments to redirect input from a file.
 */
public class MinesweeperDriver {

    public static void main(String[] args) {
        Scanner stdIn = new Scanner(System.in);
        String seedPath = args[0];
        if (args.length != 1) {
            System.err.println();
            System.err.println("Usage: MinesweeperDriver SEED_FILE_PATH");
            System.exit(1);
        }
        MinesweeperGame msInstance = new MinesweeperGame(stdIn, seedPath);
        msInstance.play();
    }
}

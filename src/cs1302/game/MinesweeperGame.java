package cs1302.game;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;

/**
 * This class represents an instance of Minesweeper Alpha. Each
 * Minesweeper game object is characterized by the size of the grid,
 * the number of mines on the grid, and a performance score based on
 * rounds played and the dimensions of the rows and columns.
 */
public class MinesweeperGame {
    private String [][] myGrid;
    private boolean [][] mineGrid;
    private int row, col, round, numOfMines;
    private double score;
    private final Scanner stdIn;
    private String seedFileName;
    private Scanner inputParser;

    /**
     * Constructs an instance of a Minesweeper Game object. Initalizes the
     * instance variables.
     * Generates the game board with a two dimensional String array. Fills
     * the String array with whitespaces to resemble undiscovered squares
     * on the grid. Calls the readSeed method to instantiate rows, columns,
     * number of mines, and the location of mines.
     *
     * @param stdIn the only scanner object in this class to accept standard input.
     * @param seedFileName the String to read input from a seed file.
     */
    public MinesweeperGame (Scanner stdIn,String seedFileName) {
        this.stdIn = stdIn;
        this.seedFileName = seedFileName;
        this.round = 0;
        readSeed();
        this.myGrid = new String[this.row][this.col];
        for (int r = 0;r < this.row; r++) {
            for (int c = 0;c < this.col;c++) {
                myGrid[r][c] = " ";
            }
        }
    }

    /**
     * Reads input from a seed file via a Scanner object. Parses the file to
     * set the dimensions of the Minesweeper game grid. Checks for appropriate
     * dimensions of rows, columns, number of mines, and location of mines.
     * Ensures that the program can properly handle incorrect input. Provides
     * user with error message regarding the seed file.
     */
    public void readSeed() {
        try {
            File file = new File(this.seedFileName);
            Scanner input = new Scanner(file);
            this.row = input.nextInt();
            this.col = input.nextInt();
            //Rows & Columns must be greater than 5.
            if ((this.row < 5) || (this.col < 5)) {
                String underInputErrMsg = "Cannot create a mine field" +
                    " with that number of rows and/or columns!";
                System.err.println("\nSeed File Malformed Error: " + underInputErrMsg);
                System.exit(3);
            }
            //Rows & Columns must be less than 11.
            if ((this.row > 10) || (this.col > 10)) {
                String overInputErrMsg = "Cannot create a mine field" +
                    " with that many rows and/or columns!";
                System.err.println("\nSeed File Malformed Error: " + overInputErrMsg);
                System.exit(3);
            }
            this.numOfMines = input.nextInt();
            //Number of Mines must be greater than 1 and less than row*col-1.
            if ((this.numOfMines < 1) || (this.numOfMines > (this.row * this.col - 1))) {
                String numMinesErrMsg = "Invalid mine count";
                System.err.println("\nSeed File Malformed Error: " + numMinesErrMsg);
                System.exit(3);
            }
            //Create boolean array to keep track of mine locations.
            this.mineGrid = new boolean[this.row][this.col];
            for (int i = 0; i < this.row; i++) {
                for (int j = 0; j < this.col; j++) {
                    mineGrid[i][j] = false;
                }
            }
            //Check index of mines in seed file.
            for (int k = 0; k < numOfMines; k++) {
                String bombIndexErr = "Mine Coordinate Out of Bounds!";
                int bombHereRow = input.nextInt();
                if ((bombHereRow < 0) || (bombHereRow > this.row - 1)) {
                    System.out.println("\nSeed File Malformed Error: " + bombIndexErr);
                    System.exit(3);
                }
                int bombHereCol = input.nextInt();
                if ((bombHereCol < 0) || (bombHereCol > this.col - 1)) {
                    System.err.println("\nSeed File Malformed Error: " + bombIndexErr);
                    System.exit(3);
                }
                mineGrid[bombHereRow][bombHereCol] = true;
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("\nSeed File Not Found Error: " + fnfe.getMessage());
            System.exit(2);
        } catch (InputMismatchException ime) {
            System.err.println("\nSeed File Malformed Error: Incorrect Datatype Detected");
            System.exit(3);
        } catch (NoSuchElementException nsee) {
            System.err.println("\nSeed File Malformed Error: Missing Value");
            System.exit(3);
        }
    } //readSeed

    /**
     * Displays the welcome banner to standard output.
     */
    public void printWelcome() {
        System.out.println("        _");
        System.out.println("  /\\/\\ (F)_ __   ___  _____      _____  ___ _ __   ___ _ __");
        System.out.println(" /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ /" +
                           " / _ \\/ _ \\ '_ \\ / _ \\ '__|");
        System.out.println("/ /\\/\\ \\ | | | |  __/\\__ \\  V  V /  __/  __/ |_) |  __/ |   ");
        System.out.println("\\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_| ");
        System.out.println("                             ALPHA EDITION |_| v2021.fa");
        printMineField();
    } //printWelcome

    /**
     * Displays the number of rounds played and the current state of the
     * Minesweeper grid to standard output.
     */
    public void printMineField() {
        System.out.println();
        System.out.println(" Rounds completed: " + this.round);
        for (int r = 0;r < this.row; r++) {
            System.out.println();
            System.out.print(" " + r + " ");
            for (int c = 0; c < this.col; c++) {
                System.out.print("| " + this.myGrid[r][c] + " ");
                if (c >= this.col - 1) {
                    System.out.print("|");
                }
            }
        }
        System.out.println();
        System.out.print("     0");
        for (int colIndex = 1; colIndex < this.col; colIndex++) {
            System.out.print("   " + colIndex);
        }
        System.out.println("\n");
    } //printMineField

    /**
     * Prints the game prompt to the user via standard output. Accepts
     * and interprets user commands via standard input. Modifies
     * the game grid based on the user-input commands. Handles
     * inappropriate input and ensures the program doesn't crash abruptly.
     */
    public void promptUser() {
        System.out.print("minesweeper-alpha: ");
        String fullCommand = stdIn.nextLine();
        Scanner inputParser = new Scanner(fullCommand);
        this.inputParser = inputParser;
        String command = inputParser.next();
        //Remove white spaces to help with parsing string.
        String trimFullCommand = fullCommand.replace(" ", "");
        try {
            if ((command.equals("r")) || (command.equals("reveal"))) {
                reveal();
                //If the reveal command does not win the game, print the minefield.
                if (isWon() == false) {
                    this.round++;
                    printMineField();
                } else {
                    this.round++;
                }
            }  else if ((command.equals("m")) || (command.equals("mark"))) {
                mark();
            }   else if ((command.equals("g")) || (command.equals("guess"))) {
                guess();
            }   else if (command.equals("nofog")) {
                nofog();
            } else if ((command.equals("h")) || (command.equals("help"))) {
                help();
            } else if ((command.equals("q")) || (command.equals("quit"))) {
                quit();
            }   else {
                System.err.println();
                System.err.println("Invalid Command: Command not recognized!");
                printMineField();
            }
        } catch (IndexOutOfBoundsException ioobe) {
            System.err.println("Invalid Command: " + ioobe.getMessage());
            printMineField();
        } catch (NoSuchElementException nsee) {
            System.err.println("Invalid Command: " + nsee.getMessage());
            printMineField();
        } catch (NumberFormatException nfe) {
            System.err.println("Invalid Command: " + nfe.getMessage());
            printMineField();
        }
    } //promptUser

/**
 * Helper method for the promptUser() command: reveal. Checks all
 * adjacent squares to the revealed square and displays the number
 * of mines located in adjacent squares.
 */
    public void reveal() {
        String parseRow = inputParser.next();
        int revealRow = Integer.parseInt(parseRow);
        String parseCol = inputParser.next();
        int revealCol = Integer.parseInt(parseCol);
        int mineCount = 0;
        if (inputParser.hasNext()) {
            System.err.println("Invalid Command: Command not recognized!");
            printMineField();
        } else {
            if (this.mineGrid[revealRow][revealCol] == true) {
                printLoss();
            } //if mine revealed, end game.
            if ((revealCol < this.col - 1) &&
                (this.mineGrid[revealRow][revealCol + 1] == true)) {
                mineCount++;
                this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
            } //if mine in square to the right of revealed square.
            if ((revealCol > 0) && (this.mineGrid[revealRow][revealCol - 1] == true)) {
                mineCount++;
                this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
            } //if mine in square to the left of revealed square.
            if ((revealRow > 0) && (revealCol > 0)) {
                if (this.mineGrid[revealRow - 1][revealCol - 1] == true) {
                    mineCount++;
                    this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
                } //if mine in square up and left of revealed square.
            }
            if ((revealRow > 0) && (this.mineGrid[revealRow - 1][revealCol] == true)) {
                mineCount++;
                this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
            } //if mine is in square above revealed square.
            if ((revealRow > 0) && (revealCol < this.col - 1)) {
                if (this.mineGrid[revealRow - 1][revealCol + 1] == true) {
                    mineCount++;
                    this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
                } //if mine is up and right of revealed square.
            }
            if ((revealRow < this.row - 1) && (revealCol > 0)) {
                if (this.mineGrid[revealRow + 1][revealCol - 1] == true) {
                    mineCount++;
                    this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
                } //if mine is below and left of revealed square.
            }
            if ((revealRow < this.row - 1) &&
                (this.mineGrid[revealRow + 1][revealCol] == true)) {
                mineCount++;
                this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
            } //if mine is below the revealed square.
            if ((revealRow < this.row - 1) && (revealCol < this.col - 1)) {
                if (this.mineGrid[revealRow + 1][revealCol + 1] == true) {
                    mineCount++;
                    this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
                } //if mine is below and right of the revealed square.
            }
            this.myGrid[revealRow][revealCol] = Integer.toString(mineCount);
        }
    }

/**
 * Helper method for promptUser() command: mark. Adds an "F" to the
 * user specified square on the grid.
 */
    public void mark() {
        String parseRow = inputParser.next();
        int markRow = Integer.parseInt(parseRow);
        String parseCol = inputParser.next();
        int markCol = Integer.parseInt(parseCol);
        if (inputParser.hasNext()) {
            System.err.println("Invalid Command: Command not recognized!");
            printMineField();
        } else {
            this.myGrid[markRow][markCol] = "F";
            this.round++;
            printMineField();
        }
    }

/**
 * Helper method for promptUser() command: guess. Adds a "?" to the
 * user specified square on the grid.
 */
    public void guess() {
        String parseRow = inputParser.next();
        int guessRow = Integer.parseInt(parseRow);
        String parseCol = inputParser.next();
        int guessCol = Integer.parseInt(parseCol);
        if (inputParser.hasNext()) {
            System.err.println("Invalid Command: Command not recognizd!");
            printMineField();
        }   else {
            this.myGrid[guessRow][guessCol] = "?";
            this.round++;
            printMineField();
        }
    }

    /**
     * Helper method for promptUser() command: quit. Displays quit message
     * to standard output and exits the program gracefully.
     */
    public void quit() {
        if (inputParser.hasNext()) {
            System.err.println("Invalid Command: Command not recognized!");
            printMineField();
        } else {
            System.out.println();
            System.out.println("Quitting the game...");
            System.out.println("Bye!");
            System.exit(0);
        }
    }

    /**
     * Helper method for promptUser() command: nofog. Displays the location
     * of mines on the grid.
     */
    public void nofog() {
        if (inputParser.hasNext()) {
            System.err.println("Invalid Command: Command not recognized!");
            printMineField();
        }        else {
            System.out.println();
            round++;
            System.out.println("Rounds completed: " + round);
            //Prints the original mineGrid but replaces white spaces with "<>"
            for (int r = 0; r < this.row; r++) {
                System.out.println();
                System.out.print(" " + r + " ");
                for (int c = 0; c < this.col; c++) {
                    if (this.mineGrid[r][c] == true) {
                        System.out.print("|<" + this.myGrid[r][c] + ">");
                    } else {
                        System.out.print("| " + this.myGrid[r][c] + " ");
                    }
                    if (c >= this.col - 1) {
                        System.out.print("|");
                    }
                }
            }
            System.out.println();
            System.out.print("     0");
            for (int colIndex = 1; colIndex < this.col; colIndex++) {
                System.out.print("   " + colIndex);
            }
            System.out.println("\n");
        }
    }

    /**
     * Helper method for promptUser() command: help. Displays five
     * options to standard output.
     */
    public void help() {
        if (inputParser.hasNext()) {
            System.err.println("Invalid Command: Command not recoginized!");
            printMineField();
        } else {
            System.out.println();
            System.out.println("Commands Available...");
            System.out.println(" - Reveal: r/reveal row col");
            System.out.println(" -   Mark: m/mark   row col" );
            System.out.println(" -  Guess: g/guess  row col" );
            System.out.println(" -   Help: h/help");
            System.out.println(" -   Quit: q/quit" );
            this.round++;
            printMineField();
        }
    }

    /**
     * Determines if all squares on the minefield grid that do not contain a mine
     * have been revealed.
     *
     * @return allSquaresRevealed returns true if all squares not containing a mine
     * have been revealed. Otherwise, returns false.
     */
    public boolean allSquaresRevealed() {
        boolean allSquaresRevealed = true;
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.row; j++) {
                //Checks the contents of each square on the grid not containing a mine.
                if ((this.mineGrid[i][j] == false) && (this.myGrid[i][j].equals(" "))) {
                    allSquaresRevealed = false;
                    break;
                } else if ((this.mineGrid[i][j] == false) && (this.myGrid[i][j].equals("?"))) {
                    allSquaresRevealed = false;
                    break;
                } else if ((this.mineGrid[i][j] == false) && (this.myGrid[i][j].equals("F"))) {
                    allSquaresRevealed = false;
                    break;
                }
            }
        }
        return allSquaresRevealed;
    } //allSquaresRevealed

    /**
     * Determines if all squares containing a mine have been marked as definitely
     * containing a mine.
     *
     * @return minesRevealed true if all mine-containing squares have been
     * marked. Otherwise, returns false.
     */
    public boolean minesRevealed() {
        boolean minesRevealed = true;
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                //if the square contains a mine, but has not been revealed yet: false.
                if ((this.mineGrid[i][j] == true) && (!this.myGrid[i][j].equals("F"))) {
                    minesRevealed = false;
                    break;
                }
            }
        }
        return minesRevealed;
    } //minesRevealed

    /**
     * Determines if the user has satisfied both conditions required to win
     * instance of a Minesweeper Alpha game.
     *
     * @return isWon true if the user has revealed all squares not containing a
     * mine and marked all squares containing a mine. Otherwise, returns false.
     */
    public boolean isWon () {
        boolean isWon = false;
        if ((allSquaresRevealed() == true) && (minesRevealed() == true)) {
            isWon = true;
        }
        return isWon;
    } //isWon

    /**
     * Calculates the user's score.
     * @return score  The users score.
     */
    public double getScore() {
        double score;
        //Cast all ints to a double to ensure score holds a double value.
        score = 100.00 * (double)this.row * (double)this.col / (double)this.round;
        if (score >= 99.999) {
            score = 100.00;
        }
        //Round score to a double value with two decimal places.
        double roundScore = Math.round(score * 100.0) / 100.0;
        score = roundScore;
        return score;
    } //getScore

/**
 * Displays the winning banner and the user's score to standard output.
 * Exits the program gracefully afterwards.
 */
    public void printWin() {
        //DecimalFormat object to ensure two decimal places in score print line.
        DecimalFormat dfScore = new DecimalFormat("##.00");
        System.out.println();
        System.out.println("░░░░░░░░░▄░░░░░░░░░░░░░░▄░░░░ \"So Doge\"");
        System.out.println("░░░░░░░░▌▒█░░░░░░░░░░░▄▀▒▌░░░");
        System.out.println("░░░░░░░░▌▒▒█░░░░░░░░▄▀▒▒▒▐░░░ \"Such Score\"");
        System.out.println("░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░░");
        System.out.println("░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐░░░ \"Much Minesweeping\"");
        System.out.println("░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░░");
        System.out.println("░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒▌░░ \"Wow\"");
        System.out.println("░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░░");
        System.out.println("░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄▌░");
        System.out.println("░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒▌░");
        System.out.println("▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒▐░");
        System.out.println("▐▒▒▐▀▐▀▒░▄▄▒▄▒▒▒▒▒▒░▒░▒░▒▒▒▒▌");
        System.out.println("▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒▒▒░▒░▒░▒▒▐░");
        System.out.println("░▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒░▒░▒░▒░▒▒▒▌░");
        System.out.println("░▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▒▄▒▒▐░░");
        System.out.println("░░▀▄▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▄▒▒▒▒▌░░");
        System.out.println("░░░░▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀░░░ CONGRATULATIONS!");
        System.out.println("░░░░░░▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀░░░░░ YOU HAVE WON!");
        System.out.println("░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▀▀░░░░░░░░ SCORE: " + dfScore.format(getScore()));
        System.out.println();
        System.exit(0);
    } //printWin

/**
 * Displays a loss message and the gameover banner to standard output.
 * Exits the program gracefully.
 */
    public static void printLoss() {
        System.out.println();
        System.out.println(" Oh no.... You revealed a mine!");
        System.out.println("  __ _  __ _ _ __ ___   ___    _____   _____ _ __");
        System.out.println(" / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|");
        System.out.println("| (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |");
        System.out.println(" \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|");
        System.out.println(" |___/");
        System.out.println();
        System.exit(0);
    } //printLoss

/**
 * Provides the main game loop for an instance of a Minesweeper Alpha game.
 */
    public void play() {
        printWelcome();
        do {
            promptUser();
            //Continue to promptthe user if the game is not completed.
        } while (isWon() == false);

        if (isWon()) {
            printWin();
        }
    } //play
} //MinesweeperGame

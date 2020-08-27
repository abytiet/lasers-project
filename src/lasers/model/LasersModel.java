package lasers.model;

import lasers.backtracking.Backtracker;
import lasers.backtracking.Configuration;
import lasers.backtracking.SafeConfig;
import lasers.ptui.LasersPTUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputFilter;
import java.util.*;

/**
 * The model of the lasers safe.  You are free to change this class however
 * you wish, but it should still follow the MVC architecture.
 *
 * @author RIT CS
 * @author Aby Tiet
 * @author Annie Tiet
 */
public class LasersModel {
    public final static String EMPTY = "."; // empty tile
    public final static String LASER = "L"; // a laser
    public final static String BEAM = "*"; // a laser beam
    public final static String FREE_PILLAR = "X"; // any or no adjacent lasers
    public final static String ZERO = "0"; // 0 adjacent laser pillar
    public final static String ONE = "1"; // 1 adjacent laser pillar
    public final static String TWO = "2"; // 2 adjacent lasers pillar
    public final static String THREE = "3"; // 3 adjacent lasers pillar
    public final static String FOUR = "4"; // 4 adjacent lasers pillar
    public static HashSet<String> pillars;
    public static HashSet<String> lasers;


    private int ROWS;
    private int COLS;
    private String[][] board;
    private String[][] defaultBoard;
    private Scanner in;
    private String fileName;
    /**
     * the observers who are registered with this model
     */
    private List<Observer<LasersModel, ModelData>> observers;
    private String status;

    public LasersModel(String filename) {
        this.fileName = filename;
        try {
            this.observers = new LinkedList<>();
            pillars = new HashSet<>();
            lasers = new HashSet<>();
            createBoard(filename);
            pillars.add(FREE_PILLAR);
            pillars.add(ZERO);
            pillars.add(ONE);
            pillars.add(TWO);
            pillars.add(THREE);
            pillars.add(FOUR);
        } catch (FileNotFoundException fnfe) {
            fnfe.getMessage();
            System.out.println(filename + " The system cannot find the file specified");
        }

    }

    /**
     * Creates a board from file
     * @param fileName name of file
     * @throws FileNotFoundException error
     */
    public void createBoard(String fileName) throws FileNotFoundException {
        this.in = new Scanner(new File(fileName));
        this.status = fileName + " loaded";
        String line = in.nextLine();
        String[] dims = line.split(" ");
        this.ROWS = Integer.parseInt(dims[0]);
        this.COLS = Integer.parseInt(dims[1]);
        this.board = new String[ROWS][COLS];
        this.defaultBoard = new String[ROWS][COLS];
        for (int i = 0; i < this.ROWS; i++) {
            line = in.nextLine();
            String[] tiles = line.split(" ");
            for (int j = 0; j < this.COLS; j++) {
                this.board[i][j] = tiles[j];
                this.defaultBoard[i][j] = tiles[j];
            }
        }
    }

    /**
     * Sets to beginning board
     */
    public void setDefaultBoard() {
        String[][] reset = new String[getROWS()][getCOLS()];
        for (int i = 0; i < getROWS(); i++) {
            reset[i] = Arrays.copyOf(this.defaultBoard[i], getCOLS());
        }
        this.board = reset;
    }

    /**
     * reads in the files from the 2nd in line argument
     * splits the lines and turns them into commands for the program to process
     *
     * @param fileName filename of the file with inputs
     */
    public void fileRead(String fileName) {
        File inputs = new File(fileName);
        try {
            this.in = new Scanner(inputs);
            while (in.hasNextLine()) {
                String msg = in.nextLine();
                System.out.println(msg);
                String[] cmd = msg.split(" ");
                commandProcess(cmd);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads commands to execute respective methods to make action happen
     * @param cmd command and coordinates if required.
     */
    public void commandProcess(String[] cmd) {
        switch (cmd[0]) {
            case "add":
            case "a":
                if (cmd.length != 3) {
                    System.out.println("Incorrect coordinates");
                } else if (Integer.parseInt(cmd[1]) > this.ROWS ||
                        Integer.parseInt(cmd[2]) > this.COLS) {
                    System.out.println("Error adding laser at: (" + cmd[1] + ", " + cmd[2] + ")");
                } else {
                    addTile(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), LASER);
                }
                break;
            case "display":
            case "d":
                System.out.println(this.toString());
                break;
            case "help":
            case "h":
                System.out.println("a|add r c: Add laser to (r,c)\n" +
                        "d|display: Display safe\n" +
                        "h|help: Print this help message\n" +
                        "q|quit: Exit program\n" +
                        "r|remove r c: Remove laser from (r,c)\n" +
                        "v|verify: Verify safe correctness");
                break;
            case "quit":
            case "q":
                try {
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case "remove":
            case "r":
                if (cmd.length != 3) {
                    System.out.println("Incorrect coordinates");
                } else if (Integer.parseInt(cmd[1]) > this.ROWS ||
                        Integer.parseInt(cmd[2]) > this.COLS) {
                    System.out.println("Error removing laser at: (" + cmd[1] + ", " + cmd[2] + ")");
                } else {
                    removeTile(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), EMPTY);
                }
                break;
            case "verify":
            case "v":
                verifyBoard();
                break;
            default:
                System.out.println("Unrecognized command: " + cmd[0]);
                break;
        }
    }

    /**
     * the main loop that handles user input
     */
    public void go() {
        System.out.println("Now accepting user input: ");
        System.out.println(this.toString());
        while (true) {
            this.in = new Scanner(System.in);
            System.out.print("> ");
            String msg = in.nextLine();
            String[] cmd = msg.split(" ");
            commandProcess(cmd);
        }
    }
    /**
     * Add a new observer.
     *
     * @param observer the new observer
     */
    public void addObserver(Observer<LasersModel, ModelData> observer) {
        this.observers.add(observer);
    }

    /**
     * Notify observers the model has changed.
     *
     * @param data optional data the model can send to the view
     */
    private void notifyObservers(ModelData data) {
        for (Observer<LasersModel, ModelData> observer : observers) {
            observer.update(this, data);
        }
    }

    public int getROWS() {
        return this.ROWS;
    }

    public int getCOLS() {
        return this.COLS;
    }

    /**
     * gets the board
     *
     * @return board in String format
     */
    public String[][] getBoard() {
        return this.board;
    }


    public String getStatus() {
        return this.status;
    }

    /**
     * changes a value in the safe
     *
     * @param row row coordinate
     * @param col column coordinate
     * @param val value changed to
     */
    public void addTile(int row, int col, String val) {
        if ((row < 0 || row > getROWS()) || (col < 0 || col > getCOLS())) {
            this.status = "Invalid coordinates: (" + row + ", " + col + ")";
            System.out.println("Invalid coordinates: (" + row + ", " + col + ")");
            notifyObservers(new ModelData(row, col, null));
        } else if (pillars.contains(this.board[row][col])) {
            this.status = "Error adding laser at: (" + row + ", " + col + ")";
            System.out.println("Error adding laser at: (" + row + ", " + col + ")");
            notifyObservers(new ModelData(row, col, null));
        } else {
            this.board[row][col] = val;
            this.status = "Laser added at: (" + row + ", " + col + ")";
            lasers.add(row + " " + col);
            makeBeam(row, col);
            notifyObservers(new ModelData(row, col, LASER));

        }
    }

    /**
     * removes a laser in the safe
     *
     * @param row row coordinate
     * @param col column coordinate
     * @param val value changed to
     */
    public void removeTile(int row, int col, String val) {
        if ((row < 0 || row > getROWS()) || (col < 0 || col > getCOLS())) {
            this.status = "Invalid coordinates: (" + row + ", " + col + ")";
            System.out.println("Invalid coordinates: (" + row + ", " + col + ")");
            notifyObservers(new ModelData(row, col, LASER));
        } else if (!this.board[row][col].equals(LASER)) {
            this.status = "Error removing laser at: (" + row + ", " + col + ")";
            System.out.println("Error removing laser at: (" + row + ", " + col + ")");
            notifyObservers(new ModelData(row, col, LASER));
        } else {
            this.board[row][col] = val;
            this.status = "Laser removed at: (" + row + ", " + col + ")";
            lasers.remove(row + " " + col);
            killBeam(row, col);
            notifyObservers(new ModelData(row, col, EMPTY));
        }
    }

    /**
     * makes the beam of the laser in all directions
     *
     * @param row row that the laser is placed
     * @param col col laser placed
     */
    public void makeBeam(int row, int col) {
        makeBeamRight(row, col);
        makeBeamDown(row, col);
        makeBeamLeft(row, col);
        makeBeamUp(row, col);

    }

    /**
     * makes beam in right direction
     *
     * @param row row coordinate of beam
     * @param col col coordinate
     */
    public void makeBeamRight(int row, int col) {
        //build beam right
        for (int i = col; i < this.COLS; i++) {
            if (this.board[row][i].equals(EMPTY)) {
                this.board[row][i] = BEAM;
                notifyObservers(new ModelData(row, i, BEAM));
            }
            if (pillars.contains(this.board[row][i])) {
                break;
            }
        }
    }

    /**
     * makes beam in downward direction
     *
     * @param row row coordinate of beam
     * @param col col coordinate
     */
    public void makeBeamDown(int row, int col) {
        //build beam down
        for (int j = row; j < this.ROWS; j++) {
            if (this.board[j][col].equals(EMPTY)) {
                this.board[j][col] = BEAM;
                notifyObservers(new ModelData(j, col, BEAM));
            }
            if (pillars.contains(this.board[j][col])) {
                break;
            }
        }
    }

    /**
     * makes beam in left direction
     *
     * @param row row coordinate of beam
     * @param col col coordinate
     */
    public void makeBeamLeft(int row, int col) {
        //build beam left
        for (int k = col; k >= 0; k--) {
            if (this.board[row][k].equals(EMPTY)) {
                this.board[row][k] = BEAM;
                notifyObservers(new ModelData(row, k, BEAM));
            }
            if (pillars.contains(this.board[row][k])) {
                break;
            }
        }
    }

    /**
     * makes beam in upward direction
     *
     * @param row row coordinate of beam
     * @param col col coordinate
     */
    public void makeBeamUp(int row, int col) {
        //build beam up
        for (int l = row; l >= 0; l--) {
            if (this.board[l][col].equals(EMPTY)) {
                this.board[l][col] = BEAM;
                notifyObservers(new ModelData(l, col, BEAM));
            }
            if (pillars.contains(this.board[l][col])) {
                break;
            }
        }

    }

    /**
     * removes the beam of the laser in all directions
     *
     * @param row row that the laser is being removed at
     * @param col col laser being removed at
     */
    public void killBeam(int row, int col) {
        for (int i = col; i < this.COLS; i++) {
            if (this.board[row][i].equals(BEAM)) {
                this.board[row][i] = EMPTY;
                notifyObservers(new ModelData(row, i, EMPTY, true));
            }
            if (pillars.contains(this.board[row][i])) {
                break;
            }
        }
        for (int j = row; j < this.ROWS; j++) {
            if (this.board[j][col].equals(BEAM)) {
                this.board[j][col] = EMPTY;
                notifyObservers(new ModelData(j, col, EMPTY, true));
            }
            if (pillars.contains(this.board[j][col])) {
                break;
            }
        }
        //build beam left
        for (int k = col; k >= 0; k--) {
            if (this.board[row][k].equals(BEAM)) {
                this.board[row][k] = EMPTY;
                notifyObservers(new ModelData(row, k, EMPTY, true));
            }
            if (pillars.contains(this.board[row][k])) {
                break;
            }
        }
        //build beam up
        for (int l = row; l >= 0; l--) {
            if (this.board[l][col].equals(BEAM)) {
                this.board[l][col] = EMPTY;
                notifyObservers(new ModelData(l, col, EMPTY, true));
            }
            if (pillars.contains(this.board[l][col])) {
                break;
            }
        }
        updateLasers();
    }

    /**
     * reads the beams for all the current lasers
     */
    public void updateLasers() {
        for (String s : lasers) {
            String[] coords = s.split(" ");
            makeBeam(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
        }
    }

    /**
     * returns an ArrayList of string with "row col" of neighboring spots of a tile
     *
     * @param row row of tile checked
     * @param col col of tile checked
     * @return ArrayList of neighbor coordinates
     */
    public ArrayList getNeighbors(int row, int col) {
        ArrayList<String> neighbors = new ArrayList();
        if (col == this.COLS - 1) // right side
        {
            if (row == this.ROWS - 1) // SE corner: left & top
            {
                neighbors.add(row + " " + (col - 1));
                neighbors.add((row - 1) + " " + col);
            } else if (row == 0) { //NE corner: bottom & left
                neighbors.add((row + 1) + " " + col);
                neighbors.add(row + " " + (col - 1));
            } else { //left, top, bottom
                neighbors.add(row + " " + (col - 1));
                neighbors.add((row - 1) + " " + col);
                neighbors.add((row + 1) + " " + col);
            }
        } else if (col == 0) { // left side
            if (row == this.ROWS - 1) { //SW corner: right & top
                neighbors.add(row + " " + (col + 1));
                neighbors.add((row - 1) + " " + col);
            } else if (row == 0) { //NW corner: bottom & right
                neighbors.add((row + 1) + " " + col);
                neighbors.add(row + " " + (col + 1));
            } else //right top bottom
            {
                neighbors.add(row + " " + (col + 1));
                neighbors.add((row - 1) + " " + col);
                neighbors.add((row + 1) + " " + col);
            }
        } else if (row == 0) //top
        {
            //bottom, left, right
            neighbors.add((row + 1) + " " + col);
            neighbors.add(row + " " + (col - 1));
            neighbors.add(row + " " + (col + 1));
        } else if (row == this.ROWS - 1) //bottom
        {
            //top, left, right
            neighbors.add((row - 1) + " " + col);
            neighbors.add(row + " " + (col - 1));
            neighbors.add(row + " " + (col + 1));
        } else //in the middle
        {
            neighbors.add(row + " " + (col + 1));
            neighbors.add(row + " " + (col - 1));
            neighbors.add((row - 1) + " " + col);
            neighbors.add((row + 1) + " " + col);

        }
        return neighbors;
    }

    /**
     * Checks if lasers are facing directly at other lasers illegal!
     *
     * @param row row coordinate
     * @param col column coordinate
     * @return false if invalid lasers facing each other
     */
    public boolean checkLasersBuddies(int row, int col) {
        //check the right of the laser
        for (int i = col + 1; i < this.COLS; i++) {
            if (this.board[row][i].equals(LASER)) {
                this.status = "Error verifying at: (" + i + " , " + row + ")";
                System.out.println("Error verifying at: (" + i + " , " + row + ")");
                notifyObservers(new ModelData(row, col, "error"));
                return false;
            }
            if (pillars.contains(this.board[row][i])) {
                break;
            }
        }
        //check under laser
        for (int j = row + 1; j < this.ROWS; j++) {
            if (this.board[j][col].equals(LASER)) {
                this.status = "Error verifying at: (" + col + ", " + j + ")";
                System.out.println("Error verifying at: (" + col + ", " + j + ")");
                notifyObservers(new ModelData(row, col, "error"));
                return false;
            }
            if (pillars.contains(this.board[j][col])) {
                break;
            }
        }
        //check the left of the laser
        for (int k = col - 1; k >= 0; k--) {
            if (this.board[row][k].equals(LASER)) {
                this.status = "Error verifying at: (" + k + " , " + row + ")";
                System.out.println("Error verifying at: (" + k + " , " + row + ")");
                notifyObservers(new ModelData(row, col, "error"));
                return false;
            }
            if (pillars.contains(this.board[row][k])) {
                break;
            }
        }
        //check above laser
        for (int l = row - 1; l >= 0; l--) {
            if (this.board[l][col].equals(LASER)) {
                this.status = "Error verifying at: (" + col + ", " + l + ")";
                System.out.println("Error verifying at: (" + col + ", " + l + ")");
                notifyObservers(new ModelData(row, col, "error"));
                return false;
            }
            if (pillars.contains(this.board[l][col])) {
                break;
            }
        }
        return true;
    }


    /**
     * check if theres the correct amount of lasers at a pillar
     *
     * @param row row pillar located
     * @param col col pillar located
     * @return true if correct amount of pillars, false otherwise
     */
    public boolean checkPillars(int row, int col) {
        String pillar = board[row][col];
        boolean noError = true;
        ArrayList<String> neighbors = getNeighbors(row, col);
        int num = 0;
        int count = 0;
        switch (pillar) {
            case FREE_PILLAR:
                return true;
            case FOUR:
                num = 4;
                break;
            case THREE:
                num = 3;
                break;
            case TWO:
                num = 2;
                break;
            case ONE:
                num = 1;
                break;
            case ZERO:
                num = 0;
                break;
        }
        for (String neighbor : neighbors) {
            String[] coordinate = neighbor.split(" ");
            int r = Integer.parseInt(coordinate[0]);
            int c = Integer.parseInt(coordinate[1]);
            String buddy = board[r][c];
            if (buddy.equals(LASER)) {
                count++;
            }
        }
        if (count != num) {
            this.status = "Error verifying at: (" + col + " , " + row + ")";
            System.out.println("Error verifying at: (" + col + " , " + row + ")");
            noError = false;
            notifyObservers(new ModelData(row, col, "error"));
        }
        return noError;
    }

    /**
     * Checks if an empty tile is in the safe
     * @return True if empty tile exists, false otherwise.
     */
    public boolean isEmpty(){
        for(int r = 0; r < this.ROWS; r++){
            for(int c = 0; c < this.COLS; c++){
                if(this.board[r][c].equals(EMPTY)){
                    this.status = "Error verifying at: (" + c + " , " + r + ")";
                    System.out.println("Error verifying at: (" + c + " , " + r + ")");
                    notifyObservers(new ModelData(r, c, "errorEmpty"));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if the board is right
     * laserBuddyCheck will check if theres a laser facing each other
     * laserValid checks if the laser is placed at a valid location (next to a pillar)
     * pillarCheck will check if there's enough lasers next to it
     * successfully valid board will print a message
     */
    public void verifyBoard() {
        boolean noLasersFacing; //true  = continue
        boolean pillarLasersAmount; //true = continue
        for (int r = 0; r < getROWS(); r++) {
            for (int c = 0; c < getCOLS(); c++) {
                if (getBoard()[r][c].equals(LASER)) {
                    noLasersFacing = checkLasersBuddies(r, c);
                    if (!noLasersFacing) {
                        return;
                    }

                }
                if (pillars.contains(getBoard()[r][c])) {
                    pillarLasersAmount = checkPillars(r, c);
                    if (!pillarLasersAmount) {
                        return;
                    }
                }
            }
        }
        if(this.isEmpty()){
            return;
        } else {
            this.status = "This safe is fully verified!";
            System.out.println("This safe is fully verified!");
        }
    }

    /**
     * Runs solve to find a solution and returns it as a board.
     * @return Solution board
     */
    public String[][] getFromSolve(){
        SafeConfig start = null;
        try {
            start = new SafeConfig(this.fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Backtracker b = new Backtracker(false);
        Optional<Configuration> solution = b.solve(start);
        if(solution.isEmpty())
        {
            return null;
        }
        return ((SafeConfig)solution.get()).getBoard();
    }

    /**
     * Gets the solution board, if it exists.
     * @return returns true if solution exists, false otherwise.
     */
    public boolean getSolution()
    {
        String[][] sol = getFromSolve();
        if(sol == null)
        {
            return false;
        }
        else {
            this.board = sol;
            return true;
        }
    }

    /**
     * string representation of the safe, for example a
     * 4x4 game that is just underway.
     * <p>
     * 0 1 2 3
     * 0|. . . 0
     * 1|. X . .
     * 2|. . 1 .
     * 3|1 . . .
     *
     * @return the board as a string
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // build the top row of indices
        str.append("  ");
        for (int col = 0; col < this.COLS; ++col) {
            str.append(col + " ");
        }
        str.append("\n");
        // build each row of the safe
        for (int row = 0; row < this.ROWS; ++row) {
            str.append(row).append("|");
            // build the columns of the safe
            for (int col = 0; col < this.COLS; ++col) {
                str.append(this.board[row][col] + " ");
            }
            str.append("\n");
        }
        return str.toString();
    }


}


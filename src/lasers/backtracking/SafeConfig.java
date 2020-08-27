package lasers.backtracking;

import lasers.Lasers;
import lasers.model.LasersModel;
import lasers.model.ModelData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the lasers.model
 * package and/or incorporate it into another class.
 *
 * @author RIT CS
 * @author Aby Tiet
 * @author Annie Tiet
 */
public class SafeConfig implements Configuration {
        private int ROWS;
        private int COLS;
        private String[][] board;
        private int cursor;


    public SafeConfig(String filename) throws FileNotFoundException {
        //create scanner for input file
        Scanner in = new Scanner(new File(filename));
        //read dimensions
        String dims = in.nextLine();
        String dim[] = dims.split(" ");
        this.ROWS = Integer.parseInt(dim[0]);
        this.COLS = Integer.parseInt(dim[1]);
        //create board
        this.board = new String[this.ROWS][this.COLS];
        for(int r = 0; r < this.ROWS; ++r){
            String line = in.nextLine();
            String[] tiles = line.split(" ");
            for(int c = 0; c < this.COLS; ++c){
                board[r][c] = tiles[c];
            }
        }
        in.close();
        this.cursor = -1; //starting cursor at invalid
    }

    public SafeConfig(SafeConfig other){
        this.ROWS = other.ROWS;
        this.COLS = other.COLS;
        this.board = new String[this.ROWS][this.COLS];
        for(int r = 0; r < this.ROWS; r++){
            for(int c = 0; c < this.COLS; c++){
                board[r][c] = other.board[r][c];
            }
        }
        this.cursor = other.cursor + 1;
    }

    /**
     * returns the board of the safe config
     * @return
     */
    public String[][] getBoard()
    {
        return this.board;
    }

    /**
     * adds a laser to the board
     * @param row row coordinate
     * @param col column coordinate
     */
    private void addLaser(int row, int col){
        this.board[row][col] = LasersModel.LASER;
        addBeams(row,col);
    }

    /**
     * checks if this spot is a pillar
     * @param row row coordinate
     * @param col column coordinate
     * @return
     */
    private boolean isPillar(int row, int col){
        switch (this.board[row][col]) {
            case LasersModel.FREE_PILLAR:
            case LasersModel.ZERO:
            case LasersModel.ONE:
            case LasersModel.TWO:
            case LasersModel.THREE:
            case LasersModel.FOUR:
                return true;
            default:
                return false;
        }
    }

    /**
     * adds beams based on where the laser was placed
     * @param row row coordinate
     * @param col column coordinate
     */
    public void addBeams(int row, int col){
        for (int i = col; i < this.COLS; i++) {
            if (this.board[row][i].equals(LasersModel.EMPTY)) {
                this.board[row][i] = LasersModel.BEAM;
            }
            if (isPillar(row,i)) {
                break;
            }
        }
        //build beam down
        for (int j = row; j < this.ROWS; j++) {
            if (this.board[j][col].equals(LasersModel.EMPTY)) {
                this.board[j][col] = LasersModel.BEAM;

            }
            if (isPillar(j,col)) {
                break;
            }
        }
        //build beam left
        for (int k = col; k >= 0; k--) {
            if (this.board[row][k].equals(LasersModel.EMPTY)) {
                this.board[row][k] = LasersModel.BEAM;
            }
            if (isPillar(row,k)) {
                break;
            }
        }
        //build beam up
        for (int l = row; l >= 0; l--) {
            if (this.board[l][col].equals(LasersModel.EMPTY)) {
                this.board[l][col] = LasersModel.BEAM;

            }
            if (isPillar(l,col)) {
                break;
            }
        }
    }

    /**
     * Two successors from a valid config: If tile isn't a pillar,
     * we can make it a LASER or EMPTY.
     * @return the successors
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        Collection<Configuration> successors = new ArrayList<>();
        SafeConfig succ1 = new SafeConfig(this);

        //if the cursor of succ1 points to empty tile, put a laser on it
        int row = succ1.cursor / COLS ;
        int col = succ1.cursor % COLS ;
        if(row == this.ROWS){
            return successors;
        }
        if(board[row][col].equals(".")) {
            succ1.addLaser(row, col);
            successors.add(succ1);
        }

        SafeConfig succ2 = new SafeConfig(this);
        successors.add(succ2);

        return successors;
    }

    /**
     * does this interfere with lasers already placed? if cursor pointing
     * to the last cell, checks for adequate number of lasers and then if
     * any of the cells are empty.
     * @return false if invalid config. true otherwise.
     */
    @Override
    public boolean isValid() {
        // check empty spaces & places that need laser & lasers near each other
        // else return true
        // used in solvewithpath to check each successor
        int row = this.cursor / COLS;
        int col = this.cursor % COLS;
        //check for lasers in path if a laser is placed
        if (this.board[row][col].equals(LasersModel.LASER)){
            if(!lasersBuddies(row,col)){
                return false;
            }
        }
        //if its a pillar, check if there are too many lasers put next to it
        for(int r = 0; r < this.ROWS; r++){
            for(int c= 0; c < this.COLS; c++) {
                if (isPillar(r, c)) {
                    boolean flag = checkPillarsTooMany(r,c);
                    if (!flag) {
                        return false;
                    }

                }
            }
        }
        return true;
    }

    /**
     * checks if the cursor points to the last cell in the board
     * @return boolean if this is a goal solution or not, false if not
     */
    @Override
    public boolean isGoal() {
        if(cursor == ROWS*COLS-1){
            for(int r = 0; r < this.ROWS; r++){
                for(int c= 0; c < this.COLS; c++) {
                    if (isPillar(r, c)) {
                        boolean flag = checkPillars(r, c);
                        if (!flag) {
                            return false;
                        }
                    }
                }
            }
            return !isEmpty();
        }
        return false;
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
     * check if there's the correct amount of lasers at a pillar
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
            case LasersModel.FREE_PILLAR:
                return true;
            case LasersModel.FOUR:
                num = 4;
                break;
            case LasersModel.THREE:
                num = 3;
                break;
            case LasersModel.TWO:
                num = 2;
                break;
            case LasersModel.ONE:
                num = 1;
                break;
            case LasersModel.ZERO:
                num = 0;
                break;
        }
        for (String neighbor : neighbors) {
            String[] coordinate = neighbor.split(" ");
            int r = Integer.parseInt(coordinate[0]);
            int c = Integer.parseInt(coordinate[1]);
            String buddy = board[r][c];
            if (buddy.equals(LasersModel.LASER)) {
                count++;
            }
        }
        if (count != num) {
            noError = false;
        }
        return noError;
    }

    /**
     * checks if there are too many lasers placed next to a pillar
     * @param row row coordinate of the pillar
     * @param col column coordinate of the pillar
     * @return true if there are too many pillars, false if ok
     */
    public boolean checkPillarsTooMany(int row, int col) {
        String pillar = board[row][col];
        boolean noError = true;
        ArrayList<String> neighbors = getNeighbors(row, col);
        int num = 0;
        int count = 0;
        switch (pillar) {
            case LasersModel.FREE_PILLAR:
                return true;
            case LasersModel.FOUR:
                num = 4;
                break;
            case LasersModel.THREE:
                num = 3;
                break;
            case LasersModel.TWO:
                num = 2;
                break;
            case LasersModel.ONE:
                num = 1;
                break;
            case LasersModel.ZERO:
                num = 0;
                break;
        }
        for (String neighbor : neighbors) {
            String[] coordinate = neighbor.split(" ");
            int r = Integer.parseInt(coordinate[0]);
            int c = Integer.parseInt(coordinate[1]);
            String buddy = board[r][c];
            if (buddy.equals(LasersModel.LASER)) {
                count++;
            }
        }
        if (count > num) {
            noError = false;
        }
        return noError;
    }

    /**
     * Checks the laser at this position if it interferes with another
     * laser above, below, or next to it.
     * @param rowCur row coordinate
     * @param colCur column coordinate
     * @return true if no lasers interfering, false if there is a laser in another's path
     */
    public boolean lasersBuddies(int rowCur, int colCur){
        //check the right of the laser
        for (int i = colCur + 1; i < this.COLS; i++) {
            if (this.board[rowCur][i].equals(LasersModel.LASER)) {
                return false;
            }
            if (isPillar(rowCur,i)) {
                break;
            }
        }

        //check under laser
        for (int j = rowCur + 1; j < this.ROWS; j++) {
            if (this.board[j][colCur].equals(LasersModel.LASER)) {
                return false;
            }
            if (isPillar(j,colCur)) {
                break;
            }
        }
        //check the left of the laser
        for (int k = colCur - 1; k >= 0; k--) {
            if (this.board[rowCur][k].equals(LasersModel.LASER)) {
                return false;
            }
            if (isPillar(rowCur,k)) {
                break;
            }
        }
        //check above laser
        for (int l = rowCur - 1; l >= 0; l--) {
            if (this.board[l][colCur].equals(LasersModel.LASER)) {
                return false;
            }
            if (isPillar(l,colCur)) {
                break;
            }
        }

        return true;
    }


    /**
     * is there an empty tile on the board?
     * @return true if empty, false otherwise
     */
    public boolean isEmpty(){
        for(int r = 0; r < this.ROWS; r++){
            for(int c = 0; c < this.COLS; c++){
                if(this.board[r][c].equals(".")){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * string representation of the safe, for example a
     * 4x4 game that is just underway.
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
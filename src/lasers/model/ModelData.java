package lasers.model;

/**
 * Use this class to customize the data you wish to send from the model
 * to the view when the model changes state.
 *
 * @author RIT CS
 * @author Aby Tiet
 * @author Annie Tiet
 */

public class ModelData {
    /** the row */
    private final int row;
    /** the column */
    private final int col;
    private final String val;
    private boolean isBeam;

    /**
     * Create a new update card.
     *
     * @param row the row
     * @param col the column
     * @param val the value
     */
    public ModelData(int row, int col, String val) {
        this.row = row;
        this.col = col;
        this.val = val;
    }

    /**
     * Create a new update card.
     *
     * @param row the row
     * @param col the column
     * @param val the value
     */
    public ModelData(int row, int col, String val, boolean isBeam) {
        this.row = row;
        this.col = col;
        this.val = val;
        this.isBeam = isBeam;
    }
    /**
     * Get the row.
     *
     * @return the row
     */
    public int getRow() { return this.row; }
    /**
     * Get the column.
     *
     * @return the column
     */
    public int getCol() { return this.col; }
    /**
     * Get the value.
     *
     * @return the value
     */
    public String getVal() { return this.val; }


    /**
     * is this tile a beam
     * @return
     */
    public boolean getIsBeam()
    {
        return this.isBeam;
    }

}


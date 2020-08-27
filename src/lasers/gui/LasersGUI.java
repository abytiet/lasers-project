package lasers.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javafx.stage.Window;
import lasers.model.*;
import lasers.ptui.ControllerPTUI;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the lasers.lasers.model
 * and receives updates from it.
 *
 * @author RIT CS
 * @author Aby Tiet
 * @author Annie Tiet
 */
public class LasersGUI extends Application implements Observer<LasersModel, ModelData> {
    /**
     * The UI's connection to the lasers.lasers.model
     */
    private LasersModel model;
    /**
     * this can be removed - it is used to demonstrates the button toggle
     */
    private static boolean status = true;
    private static boolean status2 = true;
    private Scene scene;
    private BorderPane borderPane;
    private String filename;
    private Button lastButton = new Button();
    private Button lastButton2 = new Button();

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
        new Image( getClass().getResource("resources/" + bgImgName).toExternalForm()),
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
        BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * The initialization of all GUI component happens here.
     */
    @Override
    public void init() throws Exception, FileNotFoundException {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        Parameters params = getParameters();
        String filename = params.getRaw().get(0);
        this.filename = filename;
        this.model = new LasersModel(filename);
        this.model.addObserver(this);
    }


    /**
     * makes a GridPane with buttons
     * @return GridPane
     */
    private GridPane makeGridPane() {
        GridPane gridPane = new GridPane();
        for (int rows = 0; rows < this.model.getROWS(); rows++) {
            for (int cols = 0; cols < this.model.getCOLS(); cols++) {
                Button b = new Button();
                final int x = rows;
                final int y = cols;
                initButton(b, x, y);
                setButtonBackground(b, "yellow.png");
                gridPane.add(b, rows, cols);
            }
        }
        return gridPane;
    }

    /**
     * initializes an image and an on action event to a button
     * @param b Button
     * @param x x coordinate of button
     * @param y y coordinate of button
     */
    private void initButton(Button b, int x, int y) {
        String tile = this.model.getBoard()[x][y];
        switch (tile) {
            case LasersModel.LASER:
                Image laser = new Image(getClass().getResourceAsStream("resources/laser.png"));
                b.setGraphic(new ImageView(laser));
                b.setOnAction(actionEvent -> this.model.removeTile(x, y, LasersModel.EMPTY));
                break;
            case LasersModel.EMPTY:
                Image white = new Image(getClass().getResourceAsStream("resources/white.png"));
                b.setGraphic(new ImageView(white));
                b.setOnAction(actionEvent -> this.model.addTile(x, y, LasersModel.LASER));
                break;
            case LasersModel.BEAM:
                Image beam = new Image(getClass().getResourceAsStream("resources/beam.png"));
                b.setGraphic(new ImageView(beam));
                b.setOnAction(actionEvent -> this.model.addTile(x, y, LasersModel.LASER));
                break;
            case LasersModel.FREE_PILLAR:
                Image free = new Image(getClass().getResourceAsStream("resources/pillarX.png"));
                b.setGraphic(new ImageView(free));
                b.setOnAction(actionEvent -> this.model.addTile(x, y, LasersModel.LASER));
                break;
            case LasersModel.ZERO: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar0.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(x, y, LasersModel.LASER));
                break;
            }
            case LasersModel.ONE: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar1.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(x, y, LasersModel.LASER));
                break;
            }
            case LasersModel.TWO: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar2.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(x, y, LasersModel.LASER));
                break;
            }
            case LasersModel.THREE: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar3.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(x, y, LasersModel.LASER));
                break;
            }
            case LasersModel.FOUR: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar4.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(x, y, LasersModel.LASER));
                break;
            }
        }

    }

    /**
     * resets the board
     */
    private void restartBoard()
    {
        this.model.setDefaultBoard();
        for (int rows = 0; rows < this.model.getROWS(); rows++) {
            for (int cols = 0; cols < this.model.getCOLS(); cols++) {
                GridPane gridPane = (GridPane) this.borderPane.getCenter();
                Button b = (Button) gridPane.getChildren().get(this.model.getROWS() * rows + cols);
                final int x = rows;
                final int y = cols;
                initButton(b, x, y);
            }
        }
    }

    /**
     * allows user to pick a file and returns it as a string
     * @param stage current stage we are on
     * @return String of file chosen
     */
    private String fileChooser(Stage stage)
    {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(stage);
        if(file != null) {
            this.filename = file.toString();
            System.out.println(filename);
            return file.toString();
        }
        else
        {
            return " ";
        }
    }


    /**
     * loads a new board based on file chosen
     * @param stage current stage we are working on
     */
    private void load(Stage stage)
    {
        try {
            String fileName = fileChooser(stage);
            if(!fileName.equals(" ")) {
                model.createBoard(fileChooser(stage));
            }
            else
            {
                return;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int rows = 0; rows < this.model.getROWS(); rows++) {
            for (int cols = 0; cols < this.model.getCOLS(); cols++) {
                GridPane gridPane = makeGridPane();
                this.borderPane.setCenter(gridPane);
                Button b = (Button) gridPane.getChildren().get(this.model.getROWS() * rows + cols);
                final int x = rows;
                final int y = cols;
                initButton(b, x, y);
            }
        }
        refreshLabel();
    }

    /**
     * makes the buttons for check, solve, restart, and load
     * @param stage current stage
     * @return HBox object
     */
    private HBox makeButtonsBottom(Stage stage)
    {
        Button check =  new Button("Check");
        check.setOnAction(actionEvent -> this.model.verifyBoard());
        Button solve = new Button("Solve");
        solve.setOnAction(actionEvent -> solutionGridPane());
        Button restart = new Button("Restart");
        restart.setOnAction(actionEvent -> restartBoard());
        Button load = new Button("Load");
        load.setOnAction(actionEvent -> {
            load(stage);
        });
        HBox hBox;
        hBox = new HBox(check, solve, restart, load);
        return hBox;
    }

    /**
     * solves the current safe
     */
    public void solutionGridPane()
    {
        BorderPane labels = (BorderPane) this.borderPane.getTop();
        Label status = (Label) labels.getCenter();
        status.setText("Calculating solution...");
        boolean solution = this.model.getSolution();
        if(solution){
            GridPane sol = makeGridPane();
            this.borderPane.setCenter(sol);
            status.setText(this.filename + " solved!");
        } else {
            status.setText(this.filename + " has no solution!");
        }

    }

    /**
     * sets the stage, creating BorderPane, GridPane, label, etc.
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        this.borderPane = new BorderPane();
        GridPane gridPane = makeGridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        this.borderPane.setCenter(gridPane);
        HBox hBox = makeButtonsBottom(stage);
        this.borderPane.setBottom(hBox);
        Label label = new Label(this.filename+ " loaded");
        BorderPane labelTop = new BorderPane();
        labelTop.setCenter(label);
        this.borderPane.setTop(labelTop);
        this.scene = new Scene(borderPane);
        stage.setResizable(true);
        stage.setTitle("Lasers GUI");
        stage.setScene(scene);
        stage.show();
    }


    /**
     * helper method to update card information
     *
     * @param data ModelData object with info on tile to update
     */
    private void refresh(ModelData data) {
        GridPane gridPane = (GridPane) this.borderPane.getCenter();
        Button b = (Button) gridPane.getChildren().get(this.model.getROWS() * data.getRow() + data.getCol());
        String value = data.getVal();
        if(status) {
            setButtonBackground(lastButton, "yellow.png");
        }
        if(status2){
            Image white = new Image(getClass().getResourceAsStream("resources/white.png"));
            lastButton2.setGraphic(new ImageView(white));
        }
        switch (value) {
            case LasersModel.LASER:
                Image laser = new Image(getClass().getResourceAsStream("resources/laser.png"));
                b.setGraphic(new ImageView(laser));
                b.setOnAction(actionEvent -> this.model.removeTile(data.getRow(), data.getCol(), LasersModel.EMPTY));
                break;
            case LasersModel.EMPTY:
                Image white = new Image(getClass().getResourceAsStream("resources/white.png"));
                b.setGraphic(new ImageView(white));
                b.setOnAction(actionEvent -> this.model.addTile(data.getRow(), data.getCol(), LasersModel.LASER));
                break;
            case LasersModel.BEAM:
                Image beam = new Image(getClass().getResourceAsStream("resources/beam.png"));
                b.setGraphic(new ImageView(beam));
                b.setOnAction(actionEvent -> this.model.addTile(data.getRow(), data.getCol(), LasersModel.LASER));
                break;
            case LasersModel.FREE_PILLAR:
                Image free = new Image(getClass().getResourceAsStream("resources/pillarX.png"));
                b.setGraphic(new ImageView(free));
                b.setOnAction(actionEvent -> this.model.addTile(data.getRow(), data.getCol(), LasersModel.LASER));
                break;
            case LasersModel.ZERO: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar0.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(data.getRow(), data.getCol(), LasersModel.LASER));
                break;
            }
            case LasersModel.ONE: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar1.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(data.getRow(), data.getCol(), LasersModel.LASER));
                break;
            }
            case LasersModel.TWO: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar2.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(data.getRow(), data.getCol(), LasersModel.LASER));
                break;
            }
            case LasersModel.THREE: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar3.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(data.getRow(), data.getCol(), LasersModel.LASER));
                break;
            }
            case LasersModel.FOUR: {
                Image pillar = new Image(getClass().getResourceAsStream("resources/pillar4.png"));
                b.setGraphic(new ImageView(pillar));
                b.setOnAction(actionEvent -> this.model.addTile(data.getRow(), data.getCol(), LasersModel.LASER));
                break;
            }
            case "error":
                status = false;
                if (!status) {
                    setButtonBackground(b, "red.png");
                }
                status = true;
                lastButton = b;
                break;
            case "errorEmpty":
                status2 = false;
                if (!status2) {
                    Image error = new Image(getClass().getResourceAsStream("resources/red.png"));
                    b.setGraphic(new ImageView(error));
                }
                status2 = true;
                lastButton2 = b;
                break;
        }
    }


    /**
     * refreshes the label at the top of the grid
     */
    public void refreshLabel(){
        BorderPane labels = (BorderPane) this.borderPane.getTop();
        Label status = (Label) labels.getCenter();
        status.setText(this.model.getStatus());
    }

    /**
     * updates a spot on the grid based on what the model tols it to
     * @param model the model
     * @param data optional data the server.model can send to the observer
     */
    @Override
    public void update(LasersModel model, ModelData data) {
        if(data.getVal() != null) {
            Platform.runLater(() -> refresh(data));
        } else {

        }
        Platform.runLater(this::refreshLabel);
    }
}


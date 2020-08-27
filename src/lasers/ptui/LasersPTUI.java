package lasers.ptui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import lasers.model.LasersModel;
import lasers.model.ModelData;
import lasers.model.Observer;

/**
 * This class represents the view portion of the plain text UI.  It
 * is initialized first, followed by the controller (ControllerPTUI).
 * You should create the model here, and then implement the update method.
 *
 * @author Sean Strout @ RIT CS
 * @author Aby Tiet
 * @author Annie Tiet
 */
public class LasersPTUI implements Observer<LasersModel, ModelData> {
    /** The UI's connection to the model */
    private LasersModel model;
    private ControllerPTUI controller;
    private Scanner in;

    /**
     * Construct the PTUI.  Create the lasers.lasers.model and initialize the view.
     * @param filename the safe file name
     * @throws FileNotFoundException if file not found
     */
    public LasersPTUI(String filename) throws FileNotFoundException {
        this.model = new LasersModel(filename);
        this.model.addObserver(this);
        this.controller = new ControllerPTUI(this.model);
    }

    /**
     * Accessor for the model the PTUI create.
     *
     * @return the model
     */
    public LasersModel getModel() { return this.model; }

    /**
     * updates the board
     * @param model
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(LasersModel model, ModelData data) {
        if(data.getVal().equals(LasersModel.LASER) ||  data.getVal().equals(LasersModel.EMPTY))
        {
            if(data.getIsBeam())
            {
                return;
            }
            System.out.println(model);
        }
    }

    /**
     * closes scanner and exits program
     */
    public void close() {
        this.in.close();
        System.exit(0);
    }
}

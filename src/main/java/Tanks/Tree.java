package Tanks;

import processing.core.PImage;

/**
 * Tree class, mainly stores information related to trees
 */
public class Tree {
    private float height; // Height
    private int acc_X;  // x-coordinate
    private final int CELLSIZE = 32; // Size of the grid

    private final PImage treeImg;
    private final PImage defaultTreeImg;

    public Tree(PImage treeImg, PImage defaultTreeImg) {
        this.treeImg = treeImg;
        this.defaultTreeImg = defaultTreeImg;
    }

    /**
     * Method for drawing trees.
     *
     * @param app The application instance
     */
    public void draw(App app) {
        try {
            app.image(treeImg, acc_X - 0.5F * CELLSIZE, height * CELLSIZE, 30, 30);
        } catch (Exception e) {
            app.image(defaultTreeImg, acc_X - 0.5F * CELLSIZE, height * CELLSIZE, 30, 30);
        }
    }

    /**
     * Getter and setter methods for variables:
     *
     *  float  height
     *  int    acc_X
     */
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getAcc_X() {
        return acc_X;
    }

    public void setAcc_X(int acc_X) {
        this.acc_X = acc_X;
    }

}

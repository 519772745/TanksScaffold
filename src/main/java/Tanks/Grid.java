package Tanks;

import Tools.MathTools;

import java.util.*;

/**
 * Class representing the map, storing information about the current scene
 */
public class Grid {

    private char[][] chessboard; // Representation of the scene in text
    private float[] peak; // Rough curve of the mountains read in text, used to calculate the real mountain curve
    private float[] mountainCurve; // Mountain curve, size 32*27. Each point represents the height of the mountain at position i

    private int wind; // Current wind speed

    private HashMap<String, Tank> tanks; // List of tanks, including dead ones for future features
    private HashMap<Integer, Tree> trees; // List of trees

    private int height; // 20
    private int width; // 32

    /**
     * Constructor, used to initialize the chessboard
     */
    public Grid(int height, int width) {
        this.height = height;
        this.width = width;

        this.peak = new float[width]; // Store the rough peak curve
        this.mountainCurve = new float[width * 32];

        this.wind = new Random().nextInt(71) - 35; // Set initial wind speed between -35 and 35

        this.tanks = new HashMap<>();
        this.trees = new HashMap<>();
    }

    /**
     * Add a tank to the tank HashMap.
     *
     * @param name Player name
     * @param tank Tank object
     */
    public void addTanks(String name, Tank tank) {
        this.tanks.put(name, tank);
    }

    /**
     * Add a tree to the tree HashMap.
     *
     * @param index Tree's x coordinate
     * @param t     Tree object
     */
    public void addTrees(int index, Tree t) {
        this.trees.put(index, t);
    }

    /**
     * Traverse the text map, identify the height of each mountain peak, and store it in Peak.
     */
    private void storePeak() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (chessboard[i][j] == 'X') {
                    peak[i] = j; // Store the peak value
                    break;
                }
            }
        }
    }

    /**
     * Extend the Peak of length 27 to a 27*32 array, and store it in mountainCurve.
     */
    private void storeCurve() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < 32; j++) {
                mountainCurve[32 * i + j] = peak[i];
            }
        }
    }

    /**
     * Function to change wind speed.
     * ±5 based on the original
     *
     * @return Current wind speed
     */
    public int changeWind() {
        this.wind += new Random().nextInt(11) - 5;
        return this.wind;
    }

    /**
     * Get a tank from the HashMap based on the tank name.
     */
    public Tank getTank(String name) {
        return tanks.get(name);
    }

    /**
     * Get a tree from the HashMap based on the tree's position.
     */
    public Tree getTree(int index) {
        return trees.get(index);
    }

    /**
     * Getter and setter methods for variables
     */
    public char[][] getChessboard() {
        return chessboard;
    }

    /**
     * Method called after reading the text chessboard.
     * Automatically generates a smooth mountain peak curve.
     */
    public void setChessboard(char[][] chessboard) {
        this.chessboard = chessboard;
        this.storePeak();
        this.storeCurve();

        this.mountainCurve = MathTools.calculateSMA(mountainCurve, 20);
        this.mountainCurve = MathTools.calculateSMA(mountainCurve, 20);
    }

    public float[] getMountainCurve() {
        return mountainCurve;
    }

    public HashMap<String, Tank> getTanks() {
        return tanks;
    }

    public HashMap<Integer, Tree> getTrees() {
        return trees;
    }

    public void setTrees(HashMap<Integer, Tree> trees) {
        this.trees = trees;
    }

    public int getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }
}

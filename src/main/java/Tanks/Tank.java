package Tanks;

import processing.core.PImage;

import java.util.Random;

/**
 * Tank class, stores information related to tanks
 */
public class Tank {
    private final char player; // Current player

    private int acc_X; // Precise X-coordinate
    private float height;

    private float HP;
    private int fuel;
    private float power;

    // Tank color
    private int r;
    private int g;
    private int b;

    /* Turret */
    float turretDegree; // Angle of turret rotation
    int turretLength; // Length of the turret
    float endTurretX; // X and Y coordinates of the turret endpoint, where the projectile will be fired
    float endTurretY;

    /* Projectile */
    int largerProjectile; // Whether there is a large projectile

    private int parachutes_num; // Number of parachutes owned
    private boolean using_parachutes; // Whether parachutes are currently being used
    private float target_height; // The coordinate to descend to
    private float drop_height; // Actual height of descent, calculated when descent is occurring
    private char drop_maker; // Who caused the fall

    private final int CELLSIZE = 32;


    /**
     * Constructor
     *
     * @param name  Player's name
     * @param color Tank's color, if the value does not match the format (r,g,b), it will automatically use random colors
     */
    public Tank(char name, String color) {
        this.player = name;
        if (color.equals("random")) {
            generateRandomColor();
        } else {
            String[] rgbArray = color.split(",");
            if (rgbArray.length == 3) {
                this.r = Integer.parseInt(rgbArray[0]);
                this.g = Integer.parseInt(rgbArray[1]);
                this.b = Integer.parseInt(rgbArray[2]);

            } else {
                generateRandomColor();
            }
        }

        // Basic information
        this.HP = 100;
        this.fuel = 250;
        this.power = 50;

        // Turret and projectile
        this.turretDegree = 0;
        this.turretLength = 15;
        this.target_height = -1;
        this.largerProjectile = 0;

        // Parachutes
        this.parachutes_num = 3;
        this.using_parachutes = false;
    }

    /**
     * Method for drawing tanks
     *
     * @param app The application instance
     */
    public void draw(App app) {
        // Draw the tank body
        app.fill(r, g, b);
        app.rect(0.14F * CELLSIZE + acc_X - 0.5F * CELLSIZE, 0.6F * CELLSIZE + (height) * CELLSIZE, CELLSIZE * 0.75F, CELLSIZE / 4, 20);
        app.rect(acc_X - 0.5F * CELLSIZE, 0.75F * CELLSIZE + height * CELLSIZE, CELLSIZE, CELLSIZE / 4, 20);
        app.noStroke();

        // Draw the turret
        app.stroke(0);
        float coreX = acc_X;
        float coreY = height * CELLSIZE + 0.6F * CELLSIZE;
        double theta = Math.toRadians(turretDegree);
        this.endTurretX = (float) (coreX + turretLength * Math.cos(theta));
        this.endTurretY = (float) (coreY + turretLength * Math.sin(theta));

        app.line(coreX, coreY, endTurretX, endTurretY);
        app.noStroke();

        // Draw the parachute
        if (isUsing_parachutes()) {
            PImage parachuteImg = app.loadImage("Tanks/parachute.png");
            app.image(parachuteImg, acc_X - 0.5F * CELLSIZE, height * CELLSIZE - 0.3F * CELLSIZE, 30, 30);
        }
    }

    /**
     * Method to update HP, ensuring HP will not be negative
     *
     * @param damage Amount of damage received
     */
    public void updateHP(float damage) {
        this.HP -= damage;
        if (this.HP < 0)
            this.HP = 0;
    }

    /**
     * Method to set a random color. RGB values are between 0-255.
     */
    public void generateRandomColor() {
        Random random = new Random();
        this.r = random.nextInt(256); // Generate a random integer between 0 and 255 as the red component
        this.g = random.nextInt(256); // Generate a random integer between 0 and 255 as the green component
        this.b = random.nextInt(256); // Generate a random integer between 0 and 255 as the blue component
    }

    /**
     * Method to use parachutes
     * Decreases the number of parachutes and sets using_parachutes to true
     */
    public void useParachutes() {
        this.parachutes_num--;
        this.using_parachutes = true;
    }

    /**
     * Getter and setter methods for variables
     */
    public char getPlayer() {
        return player;
    }

    public int getAcc_X() {
        return acc_X;
    }

    public void setAcc_X(int acc_X) {
        this.acc_X = acc_X;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHP() {
        return HP;
    }

    public void setHP(float HP) {
        this.HP = HP;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getTurretDegree() {
        return turretDegree;
    }

    public void setTurretDegree(float turretDegree) {
        this.turretDegree = turretDegree;
    }

    public int getTurretLength() {
        return turretLength;
    }

    public void setTurretLength(int turretLength) {
        this.turretLength = turretLength;
    }

    public void setLargerProjectile(int largerProjectile) {
        this.largerProjectile = largerProjectile;
    }

    public float getEndTurretX() {
        return endTurretX;
    }

    public float getEndTurretY() {
        return endTurretY;
    }

    public int getLargerProjectile() {
        return largerProjectile;
    }

    public int getParachutes_num() {
        return parachutes_num;
    }

    public void setParachutes_num(int parachutes_num) {
        this.parachutes_num = parachutes_num;


    }

    public boolean isUsing_parachutes() {
        return using_parachutes;
    }

    public void setUsing_parachutes(boolean using_parachutes) {
        this.using_parachutes = using_parachutes;
    }

    public float getTarget_height() {
        return target_height;
    }

    public void setTarget_height(float target_height, char maker) {
        this.target_height = target_height;
        this.drop_height = Math.abs(this.target_height - this.height);
        this.drop_maker = maker;
    }

    public float getDrop_height() {
        return drop_height;
    }

    public void setDrop_height(float drop_height) {
        this.drop_height = drop_height;
    }

    public char getDrop_maker() {
        return drop_maker;
    }

    public void setDrop_maker(char drop_maker) {
        this.drop_maker = drop_maker;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}
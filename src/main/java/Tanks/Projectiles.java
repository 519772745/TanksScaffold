package Tanks;

import java.util.ArrayList;

/**
 * Class related to explosives, used for tank explosions and ammunition
 */
public class Projectiles {

    private float x;
    private float y;

    private final char owner; // The person who caused the explosion or fired the shell

    // Shell
    private final float degree; // Angle of shell launch
    float initialVelocity; // Initial velocity of the shell when fired
    private final float gravity; // Gravity
    private final int startTime; // Time when the shell is fired

    private float parabolaX; // Current X coordinate of the shell, affected by wind
    private final ArrayList<Float> parabolaY; // Y coordinates of the shell calculated when just fired, affected by gravity

    // Explosion
    private boolean exploded; // Whether it has exploded
    private int exploded_timer; // How long the explosion has occurred
    float boom_radius; // Explosion radius
    private float size; // Degree of explosion; boom_radius = 3 * size;

    /**
     * Constructor
     *
     * @param x            Starting x coordinate
     * @param y            Starting y coordinate
     * @param degree       Launch angle of the shell; 0 when exploding
     * @param initialVelocity Initial velocity when fired; 0 when exploding
     * @param size         Degree of explosion
     * @param owner        The person who caused this explosion or fired the shell
     * @param startTime    Time when this event occurs
     */
    public Projectiles(float x, float y, float degree, float initialVelocity, float size, char owner, int startTime) {
        this.x = x;
        this.y = y;

        this.owner = owner;
        this.degree = -degree;

        this.initialVelocity = initialVelocity / 2; // This is set separately for demonstration purposes
        this.size = size;
        this.boom_radius = 3 * size;

        this.gravity = 36F; // This is set separately for demonstration purposes

        this.startTime = startTime;

        this.exploded = false;
        this.exploded_timer = 0;

        parabolaY = new ArrayList<>();
        this.parabolaY(); // Calculate all Y coordinates when initialized
    }

    /**
     * Method for drawing shells
     *
     * Draw the shell and calculate the next position of the shell based on the current situation
     */
    public void draw(App app, int currentTime) {

        // Explosion center is black, outer circle is yellow
        app.fill(0);
        app.stroke(255, 255, 0);

        // Draw the shell
        float radius = 3;
        app.ellipse(x, y, radius * 2, radius * 2);

        // Calculate the next position
        int index = Math.min(parabolaY.size() - 1, currentTime - startTime); // Prevents index out of bounds
        this.y = parabolaY.get(index);
    }

    /**
     * Method for drawing explosions
     */
    public void drawExploded(App app) {
        app.noStroke();

        // Draw circles
        if (exploded_timer >= 12) { // Show red part after 12 frames
            app.fill(255, 0, 0); // Red
            app.ellipse(x, y, boom_radius * 10, boom_radius * 10);
        }
        if (exploded_timer >= 6) { // Show orange part after 6 frames
            app.fill(255, 150, 0); // Orange
            app.ellipse(x, y, boom_radius * 5, boom_radius * 5);
        }
        app.fill(255, 255, 0); // Yellow
        app.ellipse(x, y, boom_radius * 2, boom_radius * 2);
    }

    /**
     * Method to calculate X position based on wind speed
     */
    public void parabolaX(int windStrength) {
        double angleRadians = Math.toRadians(this.degree);
        double initialVelocityX = initialVelocity * Math.cos(angleRadians);
        this.x = (float) (this.x + initialVelocityX * 0.033F + windStrength * 0.03);
    }

    /**
     * Method to calculate Y coordinate of parabola based on gravity, initial velocity, and angle
     */
    private void parabolaY() {
        // Convert angle to radians
        double angleRadians = Math.toRadians(this.degree);
        double initialVelocityY = initialVelocity * Math.sin(angleRadians); // Initial velocity in the Y direction

        float time = 0;
        while (true) {
            float y = (float) (this.y - initialVelocityY * time + 0.5F * this.gravity * time * time); // Parabola
            this.parabolaY.add(y);

            // If the position is below the ground, stop simulating
            if (y > 32 * 20) {
                break;
            }

            // Increase time
            time += 0.033; // Increase by 0.033 seconds each time; 0.33 = 1/30 (frame)
        }

    }

    /**
     * Change state to exploded
     * Explosion time is increased by 1 based on the original
     */
    public void explode() {
        this.exploded = true;
        this.exploded_timer++;
    }

    /**
     * Getter and setter methods for variables
     **/
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public char getOwner() {
        return owner;
    }

    public boolean isExploded() {
        return exploded;
    }

    public int getExploded_timer() {
        return exploded_timer;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setBoom_radius(float boom_radius) {
        this.boom_radius = boom_radius;
    }

    public float getBoom_radius() {
        return boom_radius * 10; // Because there are three circles, the size of the red circle needs to be multiplied by 10
    }
}

package Tanks;

import Tools.MathTools;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class App extends PApplet {


    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    /*------------------
     * BASIC DATA
     * -----------------*/
    public int currentLevel;
    public int max_level;
    private JSONArray levels_setting;
    private JSONObject player_colours;

    private static final int normal_drop_speed=120;
    private static final int slow_drop_speed=60;
    public Grid grid;
    private float[] mountain;

    /*------------------
     * Dynamic DATA
     * -----------------*/
    public HashMap<String,Integer> score;
    public ArrayList<Character> currentAlivePlayer;
    public HashSet<Character> prepareToMove;
    public char currentPlayer;

    private int arrowTimer;
    private int resultTimer=0;
    private int currentTime;

    public ArrayList<Projectiles> projectiles;

    public boolean gameOver=false;
    /*------------------
     * Dynamic Control
     * -----------------*/
    public boolean WPressed;
    public boolean SPressed;
    public boolean LeftPressed;
    public boolean RightPressed;
    public boolean DownPressed;
    public boolean UpPressed;


    /*------------------
    * UI
    * -----------------*/

    private PImage backgroundImg;
    private PImage defaultBackgroundImg;
    private PImage treeImg;
    private PImage defaultTreeImg;
    private PImage windImg;
    private PImage windImg1;
    private PImage fuelImg;
    private PImage parachuteImg;

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        JSONObject gameConfig = loadJSONObject("config.json");

        // Set initial settings based on the JSON data
        // Read the current level
        this.currentLevel = gameConfig.getInt("default_start_level");
        this.levels_setting = gameConfig.getJSONArray("levels");
        this.max_level = this.levels_setting.size() - 1;

        // Set default initialization images
        this.defaultBackgroundImg = loadImage("Tanks/basic.png");
        this.defaultTreeImg = loadImage("Tanks/tree1.png");

        this.windImg = loadImage("Tanks/wind.png");
        this.windImg1 = loadImage("Tanks/wind-1.png");

        this.fuelImg = loadImage("Tanks/fuel.png");
        this.parachuteImg = loadImage("Tanks/parachute.png");

        this.player_colours = gameConfig.getJSONObject("player_colours");

        /* Initialization */
        setNewLevelConfig();

        /* Initialize scores */
        this.score = new HashMap<>();
        for (char p : currentAlivePlayer) {
            this.score.put(String.valueOf(p), 0);
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (!gameOver) {
            Tank currentTank = grid.getTank(String.valueOf(currentPlayer));
            if (keyCode == 32) {
                // Fire
                float initialVelocity = 60 + ((540 - 60) / 100F * currentTank.getPower()); // Initialize initial velocity
                // Create a projectile with the current projectile's starting coordinates, angle, shooter, and time
                int size = 1;
                if (currentTank.getLargerProjectile() >= 1) {
                    currentTank.setLargerProjectile(0);
                    size = size * 2;
                }
                projectiles.add(new Projectiles(currentTank.getEndTurretX(), currentTank.getEndTurretY(),
                        currentTank.getTurretDegree(), initialVelocity, size, currentPlayer, currentTime));

                // Pressed spacebar, execute the next round of operations
                this.grid.changeWind();

                // Calculate whose turn is next
                int player_length = currentAlivePlayer.size();
                int current_index = currentAlivePlayer.indexOf(currentPlayer);
                currentPlayer = currentAlivePlayer.get((current_index + 1) % player_length);

                this.arrowTimer = 0;
            } else if (keyCode == 87) {
                WPressed = true; // Pressed W key
            } else if (keyCode == 83) {
                SPressed = true;
            } else if (keyCode == LEFT) {
                LeftPressed = true;
            } else if (keyCode == RIGHT) {
                RightPressed = true;
            } else if (keyCode == DOWN) {
                DownPressed = true;
            } else if (keyCode == UP) {
                UpPressed = true;
            } else if (keyCode == 80) { // P
                // Buy parachute
                int player_score = this.score.get(String.valueOf(currentPlayer));
                if (player_score >= 15) {
                    currentTank.setParachutes_num(currentTank.getParachutes_num() + 1);
                    player_score -= 15;
                    this.score.put(String.valueOf(currentPlayer), player_score);
                }
            } else if (keyCode == 88) { // X
                int player_score = this.score.get(String.valueOf(currentPlayer));
                if (player_score >= 20) {
                    // Larger projectile
                    currentTank.setLargerProjectile(currentTank.getLargerProjectile() + 1);
                    player_score -= 20;
                    this.score.put(String.valueOf(currentPlayer), player_score);
                }
            }else if (keyCode == 70){ //F
                int player_score = this.score.get(String.valueOf(currentPlayer));
                if (player_score >= 10) {
                    currentTank.setFuel(currentTank.getFuel() + 200);
                    player_score -= 10;
                    this.score.put(String.valueOf(currentPlayer), player_score);
                }
            }else if (keyCode == 82){ //R
                int player_score = this.score.get(String.valueOf(currentPlayer));
                if (player_score >= 20) {
                    if (currentTank.getHP()<100){
                        float tempHealth=min(100,currentTank.getHP()+20);
                        currentTank.setHP(tempHealth);
                        player_score -= 20;
                        this.score.put(String.valueOf(currentPlayer), player_score);
                    }
                }
            }
        }else {
            if(keyCode == 82) { // R
                resultTimer = 0;
                gameOver = false;
                changeLevel(0); // Restart from the beginning if it's the last level, otherwise restart the current level
            }
        }
        draw();
    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased(KeyEvent event) {
        if (keyCode == 87) {
            WPressed = false;
        } else if (keyCode == 83) {
            SPressed = false;
        } else if (keyCode == LEFT) {
            LeftPressed = false;
        } else if (keyCode == RIGHT) {
            RightPressed = false;
        } else if (keyCode == DOWN) {
            DownPressed = false;
            // Add operations you want to execute here
        } else if (keyCode == UP) {
            UpPressed = false;
            // Add operations you want to execute here
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        prepareToMove = new HashSet<>(); // A list to be deleted in this frame

        //----------------------------------
        // Draw background images, mountains, tanks, and trees
        //----------------------------------

        // 1. Draw background images, use default images if not set
        try {
            image(backgroundImg, 0, 0);
        } catch (Exception e) {
            image(defaultBackgroundImg, 0, 0);
        }

        mountain = grid.getMountainCurve(); // Get the curve of the mountain for mountain drawing

        // 2. Draw mountains
        for (int i = 0; i < WIDTH; i++) {
            stroke(255);
            fill(255);
            rect(i, (20 - mountain[i]) * 32, 1, mountain[i] * 32); // Fill mountains
        }

        noStroke();

        // 3. Draw all tanks still alive
        for (char name : currentAlivePlayer) {
            grid.getTank(String.valueOf(name)).draw(this);
        }

        // 4. Draw trees
        for (int index : grid.getTrees().keySet()) {
            grid.getTree(index).draw(this);
        }


        //----------------------------------
        // Draw UI
        //----------------------------------

        // 1. Draw rounds
        fill(0);
        textSize(11.5F);
        // Set the number of rounds and changes
        text("Current:" + currentPlayer + "'s turn", 10, 22);

        // 2. Draw wind and wind power
        if (grid.getWind() > 0) {
            image(windImg, 23 * CELLSIZE, 3);
        } else if (grid.getWind() < 0) {
            image(windImg1, 23 * CELLSIZE, 3);
        }

        text("Current", 25 * CELLSIZE, 25);
        text("Wind", 25 * CELLSIZE, 37);
        text(grid.getWind(), 25 * CELLSIZE, 51);

        // 3. Draw health
        text("Health: ", 9 * CELLSIZE, 25);

        Tank currentTank = grid.getTank(String.valueOf(currentPlayer));
        float currentHP = currentTank.getHP();
        // Health bar
        fill(0, 0, 255);
        rect(10.5F * CELLSIZE, 10, (float) (160 * currentHP) / 100, 20); // Divider

        // Outer frame
        noFill(); // Below 50 HP
        strokeWeight(5);
        stroke(80);
        rect(10.5F * CELLSIZE, 11, 80, 18);
        strokeWeight(3);

        stroke(0); // Above 50 HP
        rect(10.5F * CELLSIZE + 80, 10, 80, 20);
        strokeWeight(1);
        stroke(255, 0, 0); // Set line color to black
        rect(10.5F * CELLSIZE + 80F, 5, 0.2F, 30); // Divider

        fill(0);
        text(currentHP, 15.8F * CELLSIZE, 25); // Display HP

        // 4. Show current power
        float currentPower = currentTank.getPower();
        currentTank.setPower(min(currentHP, currentPower)); // Take the maximum value of HP and HP

        if (WPressed) { // If the current frame is in keyboard operation, adjust the corresponding power
            currentPower += 36F / FPS;
            currentPower = min(currentHP, currentPower);
            currentTank.setPower(currentPower);
        } else if (SPressed) {
            currentPower -= 36F / FPS;
            currentPower = max(0, currentPower);
            currentTank.setPower(currentPower);
        }
        text("Power:", 9 * CELLSIZE, 50);
        text(currentPower, 10.5F * CELLSIZE, 50);

        // 5. Show remaining fuel
        image(fuelImg, 4F * CELLSIZE, 5, 20, 20);
        text(currentTank.getFuel(), 4.8F * CELLSIZE, 22);

        // 6. Show the number of parachutes
        image(parachuteImg, 6F * CELLSIZE, 5, 20, 20);
        text(currentTank.getParachutes_num(), 6.8F * CELLSIZE, 22);

        // 7. Show whether there is a larger projectile
        if (currentTank.getLargerProjectile() >= 1) {
            // Draw a circle
            fill(255, 0, 0); // Red
            ellipse(9.4F * CELLSIZE, 80, 2.5F * 10, 2.5F * 10);
            fill(255, 150, 0); // Orange
            ellipse(9.4F * CELLSIZE, 80, 2.5F * 5, 2.5F * 5);
            fill(255, 255, 0); // Yellow
            ellipse(9.4F * CELLSIZE, 80, 2.5F * 2, 2.5F * 2);
            fill(0);
            text("Larger Projectile", 10.1F * CELLSIZE, 85);
        }

        //----------------------------------
        // Scoreboard
        //----------------------------------

         // 1. Outer frame
        textSize(14);
        text("Scores", 22.5F * CELLSIZE, 89);
        stroke(0); // Set line color to black
        noFill(); // Remove filling
        rect((float) (22.3 * CELLSIZE), 70, 145, textAscent() + textDescent() + 10); // Draw border
        rect((float) (22.3 * CELLSIZE), 70, 145, score.keySet().size() * 19 + 20); // Draw border

        // 2. Set player health
        int temp_index = 0;
        for (String player : score.keySet()) {
            fill(grid.getTank(player).getR(), grid.getTank(player).getG(), grid.getTank(player).getB());
            text("player " + player, 22.5F * CELLSIZE, 113 + 15 * temp_index);
            fill(0);
            text(score.get(player), 26 * CELLSIZE, 113 + 15 * temp_index);
            temp_index++;
        }

        strokeWeight(3);


        //----------------------------------
        // Arrow indicator for current tank
        //----------------------------------

        if (arrowTimer <= 2 * FPS) { // Timer, records 2 seconds. The arrow is not displayed after 2 seconds.

            // Set the position and size of the arrow
            float arrowX = currentTank.getAcc_X();
            float arrowY = currentTank.getHeight() * CELLSIZE - 35;
            float arrowSize = 25;

            // Stem
            strokeWeight(3);
            line(arrowX, arrowY, arrowX, arrowY + arrowSize);

            // Arrowhead
            beginShape();
            vertex(arrowX - arrowSize / 3, arrowY + arrowSize); // Lower left corner
            vertex(arrowX, (float) (arrowY + arrowSize * 1.5)); // Tip
            vertex(arrowX + arrowSize / 3, arrowY + arrowSize); // Lower right corner
            endShape(CLOSE);

            this.arrowTimer++;
        }

        //----------------------------------
        // User control (because calculations are performed every frame, control code is placed here)
        //----------------------------------
        // 1. Tank movement, each movement consumes 1 unit of fuel
        if (LeftPressed) {
            if (currentTank.getFuel() > 0) {
                int moveTo = currentTank.getAcc_X() - 60 / FPS;
                if (moveTo >= 0) { // Boundary control, to prevent overflow
                    currentTank.setAcc_X(moveTo);
                    currentTank.setHeight(20 - mountain[moveTo] - 1);
                    currentTank.setFuel(currentTank.getFuel() - 2);
                } else {
                    moveTo = 0;
                }
            }
        } else if (RightPressed) {
            if (currentTank.getFuel() > 0) {
                int moveTo = currentTank.getAcc_X() + 60 / FPS;
                if (moveTo < 27 * CELLSIZE) {
                    currentTank.setAcc_X(moveTo);
                    currentTank.setHeight(20 - mountain[moveTo] - 1);
                    currentTank.setFuel(currentTank.getFuel() - 2);
                } else {
                    moveTo = 27 * CELLSIZE - 1;
                }
            }
        }

        // 2. Rotate the turret
        if (DownPressed) {
            float tempDegree = currentTank.getTurretDegree() + 3F / FPS;
            currentTank.setTurretDegree(tempDegree);
        } else if (UpPressed) {
            float tempDegree = currentTank.getTurretDegree() - 3F / FPS;
            currentTank.setTurretDegree(tempDegree);
        }


        //----------------------------------
        // Draw firing scene
        //----------------------------------
        if (!projectiles.isEmpty()) { // If there are projectiles on the scene
            for (Projectiles p : projectiles) {
                // Actual height
                float mountainHeightY = (20 - mountain[(int) p.getX()]) * CELLSIZE;

                // If exploded, play the explosion animation. But in this frame, two conditions need to be judged to see if the explosion has just occurred. Therefore, there are two judgment conditions, Y and the height of the mountain, or the explosion has already started.
                if (p.getY() >= mountainHeightY || p.isExploded()) {
                    p.explode(); // exploded sets this value to true, and also increments time, so this call is to save one if condition

                    // Draw explosion animation
                    p.drawExploded(this);

                    // If the point has exploded for 18 frames, start to cause damage.
                    if (p.getExploded_timer() > 18) {

                        makeDamage(p); // Inflict damage on terrain, tanks, trees, etc.
                        projectiles.remove(p); // Explosion is complete, remove the explosion object
                    }
                    break;
                }
                // If not exploded, play the non-explosion animation
                else {
                    p.draw(this, currentTime);
                    // Since the wind changes each time the spacebar is pressed, the projectile may be affected by the wind. Therefore, the x coordinate is calculated separately every frame
                    p.parabolaX(grid.getWind());

                    // If the projectile flies off the screen, recycle it
                    if (p.getX() > 27 * CELLSIZE || p.getX() < 0 || p.getY() < 0 || p.getY() > 20 * CELLSIZE) {
                        projectiles.remove(p);
                        break;
                    }
                }
            }
        }


        //----------------------------------
        // Calculation of tank position change due to dropping
        //----------------------------------

        for (char s : currentAlivePlayer) {

            Tank tempTank = grid.getTank(String.valueOf(s));

            // If dropping
            if (tempTank.getTarget_height() > tempTank.getHeight()) {
                // No parachute situation (120/30)/32 = 0.125
                float speed = (float) (this.normal_drop_speed / 30) / 32;

                // If a parachute is set
                if (tempTank.isUsing_parachutes()) {
                    speed = (float) (this.slow_drop_speed / 30) / 32;
                }

                tempTank.setHeight(tempTank.getHeight() + speed); // If there is a parachute, it's another story
            }
            // If dropping and has dropped to the bottom
            else if (tempTank.getTarget_height() != -1) {

                if (!tempTank.isUsing_parachutes()) {
                    // After landing at the bottom, start calculating additional damage
                    float temp_damage = tempTank.getDrop_height() * CELLSIZE * 1;
                    temp_damage = Math.min(tempTank.getHP(), temp_damage);
                    tempTank.updateHP(temp_damage);

                    // Players who cause landing get points
                    if (s != tempTank.getDrop_maker()) {
                        int temp_score = score.get(String.valueOf(tempTank.getDrop_maker())) + (int) temp_damage;
                        score.put(String.valueOf(tempTank.getDrop_maker()), temp_score);
                    }
                }

                // Restore dropping control values
                tempTank.setTarget_height(-1, ' ');
                tempTank.setUsing_parachutes(false);

                // If the tank's HP is 0
                if (tempTank.getHP() <= 0) {
                    prepareToMove.add(s);
                }
            }
        }

        //----------------------------------
        // Whether there is a tank touching the ground, if so, self-destruct
        //----------------------------------
        for (char s : currentAlivePlayer) {
            Tank tempTank = grid.getTank(String.valueOf(s));

            if (tempTank.getHeight() * CELLSIZE + 30 >= 20 * CELLSIZE) {
                tempTank.setHP(0); // Directly dead if fallen to the ground
                prepareToMove.add(s);
            }
        }

        //----------------------------------
        // Set up explosion after tank is destroyed
        //----------------------------------
        for (char c : prepareToMove) {
            Tank tempTank = grid.getTank(String.valueOf(c));

            float size = 0.5F;
            if (tempTank.getHeight() * CELLSIZE + 20 >= 20 * CELLSIZE) // If touching the ground
                size = 1;

            float boom_height = max(tempTank.getHeight(), tempTank.getTarget_height());
            Projectiles p = new Projectiles(tempTank.getAcc_X(), boom_height * CELLSIZE + 25, 0,
                    0, size, c, currentTime);
            p.explode();
            projectiles.add(p);
        }

        //----------------------------------
        // Clear objects eliminated in this frame. If the current player is eliminated, pass control to the next player without pressing space.
        //----------------------------------
        for (char c : prepareToMove) {
            int index = currentAlivePlayer.indexOf(c);
            if (index >= 0) {
                // Force a switch
                if (currentTank.getPlayer() == currentAlivePlayer.get(index)) {
                    this.grid.changeWind();

                    // Calculate who the next player's turn is
                    int player_length = currentAlivePlayer.size();
                    int current_index = currentAlivePlayer.indexOf(currentPlayer);
                    currentPlayer = currentAlivePlayer.get((current_index + 1) % player_length);

                    this.arrowTimer = 0;
                }

                currentAlivePlayer.remove(index);
            }
        }

        //----------------------------------
        // Check if the game is over
        //----------------------------------
        if (currentAlivePlayer.size() <= 1  && arrowTimer>=18) {
            // If the game is over
            if (currentLevel == max_level) {
                gameOver = true;

                this.sortScore(); // Sort the HashMap

                String winner = score.keySet().iterator().next(); // Read the winner

                // Set the background box
                Tank tempTank = grid.getTank(winner);
                fill(tempTank.getR(), tempTank.getG(), tempTank.getB(), 128);
                rect(6 * CELLSIZE, 5 * CELLSIZE, 14F * CELLSIZE, 10 * CELLSIZE);

                // Set text prompt
                fill(255);
                textSize(26);
                text("GAME OVER", 10.5F * CELLSIZE, 6.5F * CELLSIZE);

                textSize(20);
                text("Player " + winner + " wins!", 10.7F * CELLSIZE, 7.5F * CELLSIZE);

                // Display results with a 0.7s interval
                int tempIndex = 0; // Record the current player index
                int player_length = Math.min(resultTimer / 21, score.keySet().size() - 1); // Show results for X players at the moment

                for (Map.Entry<String, Integer> entry : score.entrySet()) {
                    String name = entry.getKey();
                    if (tempIndex > player_length)
                        break;
                    text("Player " + name, 7.5F * CELLSIZE, 9F * CELLSIZE + tempIndex * 1.6F * CELLSIZE);
                    text(entry.getValue(), 17.5F * CELLSIZE, 9F * CELLSIZE + tempIndex * 1.6F * CELLSIZE);

                    tempIndex++;
                }
                resultTimer++;
            } else {
                delay(1000); // Next level after 1 second
                changeLevel(currentLevel + 1);
                this.setNewLevelConfig();
            }
        }

        currentTime++;
    }


    /**
     * Check if the item is a tank.
     *
     * Use regular expressions to match when the value is '0' - '9' or a letter, excluding 'X' and 'T'.
     *
     * @param c The name of the item
     * @return True if the item is a tank, false otherwise
     */
    public boolean checkIsTank(char c) {
        if (c == 'T' || c == 'X') {
            return false;
        }
        Pattern pattern = Pattern.compile("[A-Z0-9]");
        return pattern.matcher(String.valueOf(c)).matches();
    }

    /**
     * Method to calculate damage caused by projectiles and tank self-destruction.
     *
     * <p> 1. If a tank is hit by an explosion, it will suffer damage. The damage is determined by the proximity to the explosion center.
     *      Overall, the damage follows the formula y=-2x+60;
     *    2. If the explosion hits a mountain peak, it will affect the mountain's height. If the mountain is lowered due to the explosion,
     *       - Plants: They will fall straight down but still remain on the slope.
     *       - Tanks: Record the target location of the fall and gradually fall frame by frame. Damage is calculated only when it reaches the bottom.
     *                If a parachute is available, it will be automatically used to slow the fall and avoid damage.
     * </p>
     *
     * @param p The projectile causing the explosion
     */
    public void makeDamage(Projectiles p) {
        // Calculate the explosion center and radius
        int core_x = (int) p.getX();
        float core_y = p.getY();
        int boom_radius = (int) p.getBoom_radius();

        // Calculate damage to players caused by the explosion
        for (Character c : currentAlivePlayer) {
            Tank tempTank = grid.getTank(String.valueOf(c));
            float distance = MathTools.getDistance(tempTank.getAcc_X(), tempTank.getHeight() * CELLSIZE + 0.6F * CELLSIZE, core_x, core_y);

            // If within the damage range, calculate the damage based on proximity
            if (distance < boom_radius) {
                float damage = MathTools.Linear(distance, -2, 60); // y=-2x+60
                damage = Math.min(tempTank.getHP(), damage); // Limit damage to remaining HP

                tempTank.updateHP(damage);

                // If not caused by the same player, grant them score
                if (p.getOwner() != tempTank.getPlayer()) {
                    int temp_score = score.get(String.valueOf(p.getOwner())) + (int) damage;
                    score.put(String.valueOf(p.getOwner()), temp_score);
                }
                // If HP reaches zero, add to the list for destruction
                if (tempTank.getHP() <= 0) {
                    prepareToMove.add(c);
                    break;
                }
            }
        }

        // Impact on the mountain, trees, and tanks falling due to the explosion
        for (int i = core_x - boom_radius; i < core_x + boom_radius; i++) {
            // Calculate the coordinates of the points on the circle in the coordinate system
            double y_min = Math.sqrt(Math.pow(boom_radius, 2) - Math.pow((i - core_x), 2)) + core_y;

            // If the coordinate i is within bounds, update
            try {
                float temp_mountainHeightY = (20 - mountain[i]) * CELLSIZE;
                if (temp_mountainHeightY < y_min) {

                    mountain[i] = (float) (20 - (y_min / CELLSIZE)); // Lower the height of the mountain

                    // Lower the trees
                    if (this.grid.getTree(i) != null) {
                        Tree t = this.grid.getTree(i);
                        t.setHeight(20 - mountain[i] - 1);
                    }

                    // Set the target height for tanks to fall to and whether to use a parachute
                    for (Character c : currentAlivePlayer) {
                        Tank tempTank = grid.getTank(String.valueOf(c));
                        if (tempTank.getAcc_X() == i) {

                            if (tempTank.getParachutes_num() > 0) { // Use a parachute if available
                                tempTank.useParachutes();
                            }

                            tempTank.setTarget_height(20 - mountain[i] - 1, p.getOwner()); // Target position for tank to fall
                        }
                    }
                }
            } catch (Exception e) {
                continue; // Skip if out of bounds
            }
        }
    }

    /**
     * Method to change the current level.
     *
     * <p> Only effective when the value is valid, otherwise no action is taken.
     * </p>
     *
     * @param level The new level to change to
     */
    public void changeLevel(int level) {
        if (level <= max_level && level >= 0) {
            this.currentLevel = level;
            setNewLevelConfig();
        }
    }
    /**
     * Method to read new level configurations.
     *
     * <p>
     *     Includes terrain, tank positions, trees, and resetting related attributes.
     * </p>
     */
    public void setNewLevelConfig() {
        // Set initial images and default initialization images
        String backgroundImg_url = this.levels_setting.getJSONObject(currentLevel).getString("background").toLowerCase();
        String tree_url = this.levels_setting.getJSONObject(currentLevel).getString("trees").toLowerCase();

        this.backgroundImg = loadImage("Tanks/" + backgroundImg_url);
        this.treeImg = loadImage("Tanks/" + tree_url);

        /* Initialize scores */
        this.currentAlivePlayer = new ArrayList<Character>();
        /* Read map script */
        this.grid = new Grid(BOARD_HEIGHT, BOARD_WIDTH);

        // Read script
        String layout_url = this.levels_setting.getJSONObject(currentLevel).getString("layout").toLowerCase();

        /* Read map */
        char[][] map = new char[BOARD_WIDTH][BOARD_HEIGHT]; // Text map body
        // Traverse the map and record information
        try (BufferedReader br = new BufferedReader(new FileReader(layout_url))) {
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                String line = br.readLine();
                try {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        map[j][20 - i] = line.charAt(j); // Store map data
                        // If it's a tank, store tank data
                        if (checkIsTank(line.charAt(j))) {
                            String name = String.valueOf(line.charAt(j)); // Record the tank's name
                            String color = player_colours.getString(name); // Record the tank's color

                            // Create a tank
                            Tank tempTank = new Tank(name.charAt(0), color);
                            tempTank.setAcc_X(j * 32);  // Initialize X
                            //tempTank.setY(20-i-1); Y initialization not necessary

                            this.grid.addTanks(name, tempTank);// key-value
                            //this.score.put(name,0);
                            this.currentAlivePlayer.add(name.charAt(0)); // List of tanks currently available
                        } else if (line.charAt(j) == 'T') {
                            // If it's a tree
                            Tree tempT = new Tree(treeImg, defaultTreeImg);
                            tempT.setAcc_X(j * 32);

                            this.grid.addTrees(j * 32, tempT);
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (IOException e) {
        }


        // Store map data, automatically generate the curve of the mountain when storing
        this.grid.setChessboard(map);

        // Store the real Y coordinates of the tanks
        for (Character s : currentAlivePlayer) {
            Tank tempTank = this.grid.getTank(String.valueOf(s));
            int temp_index = tempTank.getAcc_X();
            float temp_height = 20 - this.grid.getMountainCurve()[temp_index] - 1;
            tempTank.setHeight(temp_height);
        }
        // Store the real coordinates of the trees
        for (int index : grid.getTrees().keySet()) {
            int temp_index = this.grid.getTree(index).getAcc_X();
            float temp_height = 20 - this.grid.getMountainCurve()[temp_index] - 1;
            this.grid.getTree(index).setHeight(temp_height);
        }
        // Initialization complete
        this.projectiles = new ArrayList<>();
        this.arrowTimer = 0;
        this.currentPlayer = this.currentAlivePlayer.get(0);
        currentTime = 0;
    }

    /**
     * Utility method to sort a HashMap by value.
     */
    public void sortScore() {
        // Sort by Map's value
        stroke(0);
        this.score = score.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );
    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}

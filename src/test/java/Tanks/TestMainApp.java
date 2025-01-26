package Tanks;

import org.checkerframework.checker.units.qual.Current;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TestMainApp class contains unit tests for the main functionalities of the Tanks game.
 */
public class TestMainApp {
    App app = new App();

    /**
     * Entry point for testing the APP class. All test methods will be called here.
     */
    @Test
    public void testApp(){

        // Initialize the test window
        PApplet.runSketch(new String[]{"App"}, app);
        app.delay(1000);

        // Load configuration file
        app.setup();
        app.delay(1000);

        /*-----------------------
          Start testing
         -----------------------*/

        // Test wind-related functionalities. The movement speed of projectiles is affected by wind, causing horizontal offset.
        // Since the offset distance depends on time, this functionality is tested first, setting the default start time to 1.
        this.testWind(app);
        app.delay(1000);

        // Test keyboard control-related functionalities,
        // including W and S keys for turret power control, arrow keys for movement.
        this.testKeyboard(app);
        app.delay(1000);

        // Test functionalities related to hitting the terrain,
        // including terrain changes, tank HP changes, and score changes.
        this.testAttack(app);
        app.delay(1000);

        // Test functionalities after pressing the spacebar,
        // including firing projectiles and switching turns.
        this.testFire(app);
        app.delay(1000);

        // Test functionalities related to falling,
        // including descent speed, damage, and score changes.
        this.testFall(app);
        app.delay(1000);

        // Test functionalities related to purchasing and using items.
        this.testPurchase(app);
        app.delay(1000);

        // Test whether the program correctly removes the player when they are eliminated.
        this.testRemovePlayer(app);
        app.delay(1000);

        // Test the level change functionality.
        this.testChangeLevel(app);
        app.delay(1000);

        // Test the game restart functionality.
        this.testRestartGame(app);
        app.delay(1000);

        // Test the game over functionality.
        this.testGameOver(app);

    }


    /**
     * Tests keyboard controls for player actions.
     *
     * <p>This method sequentially tests W, S, and arrow keys.
     * W and S are used to control the turret power, W increases power and S decreases power.
     * Arrow keys are used to rotate the turret and control the tank's movement.
     * Fuel is consumed when moving, and movement is not possible without fuel.
     *
     * @param app The application instance.
     */
    public void testKeyboard(App app){
        Tank currentTank =app.grid.getTank(String.valueOf(app.currentPlayer));

        KeyEvent keyEventPressed = new KeyEvent(null, 0, 0, 0, ' ', 87);


        /*-------------------
         * W & S (Power)
         * ------------------*/
        app.keyCode = 87;   //W
        float beforePower=currentTank.getPower();
        app.keyPressed(keyEventPressed);
        assertTrue(app.WPressed);   // Check if W is pressed
        app.delay(300);
        float currentPower=currentTank.getPower();
        assertTrue(beforePower-currentPower<=0); // Check if the power is increased

        app.keyReleased(keyEventPressed);
        assertFalse(app.WPressed);  // Check if W is released
        app.delay(100);
        beforePower=currentTank.getPower();
        app.delay(100);
        currentPower=currentTank.getPower();
        assertEquals(beforePower,currentPower);  // Check if the power remains unchanged after releasing

        app.keyCode = 83;   //S
        beforePower=currentTank.getPower();
        app.keyPressed(keyEventPressed); // Check if S is pressed
        currentPower=currentTank.getPower();
        assertTrue(app.SPressed);
        assertTrue(beforePower-currentPower>=0); // Check if the power is decreased

        app.keyReleased(keyEventPressed);
        assertFalse(app.SPressed); // Check if S is released
        beforePower=currentTank.getPower();
        currentPower=currentTank.getPower();
        assertEquals(beforePower,currentPower);  // Check if the power remains unchanged after releasing

        /*-------------------
         * L & R (X)
         * ------------------*/
        app.keyCode = 37; // Left key
        int preFuel=currentTank.getFuel();
        int preX=currentTank.getAcc_X();
        app.keyPressed(keyEventPressed);
        assertTrue(app.LeftPressed); // Check if the left key is pressed
        app.delay(300);

        int currentFuel=currentTank.getFuel();
        int currentX=currentTank.getAcc_X();
        assertNotEquals(preX,currentX); // Check if the tank moves
        assertNotEquals(preFuel,currentFuel);// Check if fuel is consumed while moving


        app.keyReleased(keyEventPressed);
        assertFalse(app.LeftPressed); // Check if the left key is released
        app.delay(100);
        preX=currentTank.getAcc_X();
        app.delay(100);
        currentX=currentTank.getAcc_X();
        assertEquals(preX,currentX);  // Check if the tank stops moving after releasing the left key


        app.keyCode = 39; // Right key
        app.keyPressed(keyEventPressed); // Check if the right key is pressed
        assertTrue(app.RightPressed);
        app.keyReleased(keyEventPressed);
        assertFalse(app.RightPressed); // Check if the right key is released

        app.delay(1000);


        currentTank.setFuel(0); // Test the case without fuel, the tank should not move even if the keys are pressed
        app.keyCode = 39;
        preX=currentTank.getAcc_X();
        app.keyPressed(keyEventPressed);
        assertTrue(app.RightPressed); // Check if the right key is pressed
        app.delay(1000);
        app.keyReleased(keyEventPressed);
        assertFalse(app.RightPressed);  // Check if the right key is released
        app.delay(100);
        currentX=currentTank.getAcc_X();
        assertEquals(preX,currentX); // Check if the tank does not move while the right key is pressed


        /*-------------------
         * U & D (Turret)
         * ------------------*/

        app.delay(1500);
        app.keyCode = 38;
        float preDegree=currentTank.getTurretDegree();
        app.keyPressed(keyEventPressed);
        assertTrue(app.UpPressed); // Check if the up key is pressed
        app.delay(2500);
        float currentDegree=currentTank.getTurretDegree();
        assertNotEquals(preDegree,currentDegree);  // Check if the turret rotates

        app.keyReleased(keyEventPressed);
        assertFalse(app.UpPressed);  // Check if the up key is released

        app.delay(1000);
        preDegree=currentTank.getTurretDegree();
        app.delay(1000);
        currentDegree=currentTank.getTurretDegree();
        assertEquals( preDegree,currentDegree);  // Check if the turret remains unchanged within 2 seconds after releasing

        app.keyCode = 40; // Down key
        app.keyPressed(keyEventPressed);
        assertTrue(app.DownPressed); // Check if the down key is pressed
        app.keyReleased(keyEventPressed);
        assertFalse(app.DownPressed); // Check if the down key is released
    }

    /**
     * Test whether firing after pressing the space bar works correctly.
     *
     * @param app The application instance.
     */
    public void testFire(App app){
        Tank currentTank =app.grid.getTank(String.valueOf(app.currentPlayer));
        KeyEvent keyEventPressed = new KeyEvent(null, 0, 0, 0, ' ', 87);

        int preProjectilesSize=app.projectiles.size();
        app.keyCode = 32;
        app.keyPressed(keyEventPressed);

        app.delay(200);

        // Check if a projectile is generated
        assertEquals(preProjectilesSize+1,app.projectiles.size());

    }

    /**
     * Test whether the purchase functionality works correctly [COMP9003]
     *
     * <p>1. Purchase parachute: need to consume 15 scores
     * 2. Purchase larger projectiles: need to consume 20 scores
     * 3. Purchase Repair kit: need to consume 20 scores
     * 4. Purchase Additional Fuel: need to consume 10 scores
     * @param app The application instance.
     */
    public void testPurchase(App app){
        Tank currentTank =app.grid.getTank(String.valueOf(app.currentPlayer));
        KeyEvent keyEventPressed = new KeyEvent(null, 0, 0, 0, ' ', 87);


        /*---------------------------
         *  Case: Insufficient scores
         * ---------------------------*/
        app.score.put(String.valueOf(app.currentPlayer),0); // Now you're broke

        app.keyCode = 80; // Purchase parachute key code is 80
        int preParachutesNum=currentTank.getParachutes_num();
        app.keyPressed(keyEventPressed);
        app.delay(100);
        int currentParachutesNum=currentTank.getParachutes_num();
        assertEquals(preParachutesNum, currentParachutesNum); // Test if the purchase cannot be made
        app.delay(100);

        app.keyCode = 88; // Larger projectiles key code is 88
        app.keyPressed(keyEventPressed);
        app.delay(100);
        assertEquals(currentTank.getLargerProjectile(),0); // Test if the purchase cannot be made
        app.delay(100);

        app.keyCode=82;
        float preHealth=currentTank.getHP();
        app.keyPressed(keyEventPressed);
        app.delay(100);
        assertEquals(preParachutesNum, currentTank.getParachutes_num());
        assertEquals(preHealth, currentTank.getHP());
        app.delay(100);

        app.keyCode=70;
        float preFuel=currentTank.getFuel();
        app.keyPressed(keyEventPressed);
        app.delay(100);
        assertEquals(preParachutesNum, currentTank.getParachutes_num());
        assertEquals(preFuel, currentTank.getFuel());
        app.delay(100);

        /*---------------------------
         *  Case: Sufficient scores
         * ---------------------------*/
        app.score.put(String.valueOf(app.currentPlayer),9999);  // Yes, I'm cheating

        app.delay(100);
        app.keyCode = 80;// Purchase parachute key code is 80
        preParachutesNum=currentTank.getParachutes_num();
        app.keyPressed(keyEventPressed);
        app.delay(100);
        currentParachutesNum=currentTank.getParachutes_num();
        assertEquals(app.score.get(String.valueOf(app.currentPlayer)),9999-15); // Test if scores are correctly deducted
        assertEquals(preParachutesNum + 1, currentParachutesNum); // Test if the item count increases

        app.delay(100);
        app.keyCode = 88; // Larger projectiles key code is 80
        app.keyPressed(keyEventPressed);
        app.delay(100);
        assertEquals(app.score.get(String.valueOf(app.currentPlayer)),9999-15-20); // Test if scores are correctly deducted
        assertTrue(currentTank.getLargerProjectile()>0); // Test if the item count increases

        app.delay(100);
        app.keyCode=70;
        preFuel=currentTank.getFuel();
        app.keyPressed(keyEventPressed);
        app.delay(100);
        assertEquals(preFuel+200, currentTank.getFuel());
        assertEquals(app.score.get(String.valueOf(app.currentPlayer)), 9999-15-20-10);
        app.delay(100);

        app.delay(100);
        app.keyCode=82;
        currentTank.setHP(80);
        app.keyPressed(keyEventPressed);
        app.delay(100);
        assertEquals(100,currentTank.getHP());
        assertEquals(app.score.get(String.valueOf(app.currentPlayer)), 9999-15-20-10-20);

        app.delay(100);
        currentTank.setHP(90);
        app.keyPressed(keyEventPressed);
        assertEquals(100,currentTank.getHP());

        app.delay(100);
        currentTank.setHP(100);
        app.keyPressed(keyEventPressed);
        assertEquals(100,currentTank.getHP());
        app.delay(100);

        app.keyCode=32; // Fire, to test if the projectile radius increases next time
        app.keyPressed(keyEventPressed);
        app.delay(500);

        assertEquals(app.projectiles.get(app.projectiles.size()-1).getSize(),2); // Test if the radius increases to 2 times

    }


    /**
     * Test whether falling is correctly determined
     *
     * <p>1. Destroyed when falling to the bottom. See @testRemovePlayer
     * 2. The descent speed is different with and without parachutes, and without parachutes, HP is deducted
     * 3. Falling caused by others, the opponent can get points
     * @param app The application instance.
     */
    public void testFall(App app){
        Tank currentTank =app.grid.getTank(String.valueOf(app.currentPlayer));
        float preHeight =currentTank.getHeight(); // Record the height and HP before falling
        float preHP=currentTank.getHP();

        // Case: With parachute, no HP deduction
        currentTank.setParachutes_num(3);
        currentTank.setUsing_parachutes(true);
        app.delay(100);
        currentTank.setTarget_height(preHeight+1,'A');

        app.delay(1000);
        assertEquals(currentTank.getHP(),preHP); // Test if HP is not deducted

        // Case: Self-caused fall, without parachute, HP deduction
        currentTank.setParachutes_num(0);
        currentTank.setUsing_parachutes(false);
        app.delay(100);
        currentTank.setHeight(preHeight);
        currentTank.setTarget_height(preHeight+1,'C');

        app.delay(1000);
        assertTrue(currentTank.getHP()<preHP);  // Test if HP is correctly deducted
        assertEquals(0, (int) app.score.get("C"));  // Test if the score does not increase

        // Case: Falling caused by others, without parachute
        currentTank.setParachutes_num(0);
        currentTank.setUsing_parachutes(false);
        app.delay(100);
        currentTank.setHeight(preHeight);
        currentTank.setTarget_height(preHeight+1,'A');

        app.delay(1000);
        assertTrue(app.score.get("A")>0);  // Test if player D gets points
    }


    /**
     * Test the effect of wind on projectile trajectory
     *
     * <p>When firing projectiles at the same position, due to different wind strengths at different times, the x coordinates should be different after the same time Δt.
     *
     * @param app The application instance.
     */
    public void testWind(App app){
        Projectiles p1 =new Projectiles(100,100,
                0,60, 1,'A',1); // Projectile 1 is launched at the first second

        app.projectiles.add(p1);
        app.delay(1000);
        float case1X =p1.getX(); // Record the coordinates of projectile 1 after 1 second

        app.grid.changeWind();
        Projectiles p2 =new Projectiles(100,100,
                0,60, 1,'A',2); // Projectile 2 is launched at the second second

        app.projectiles.add(p2); // Record the coordinates of projectile 2 after 1 second
        app.delay(1000);
        float case2X =p2.getX();

        app.projectiles.remove(p1); // Recycle the test projectiles to avoid subsequent effects
        app.projectiles.remove(p2);

        assertNotEquals(case1X,case2X); // Compare whether the x coordinates are different when the wind strengths are different
    }


    /**
     * Test the effect of attacks
     *
     * <p>After attacking, the position of the terrain should change
     * and the person causing damage will get the opponent's HP drop amount as points
     *
     * @param app The application instance.
     */
    public void testAttack(App app){
        Tank currentTank =app.grid.getTank(String.valueOf(app.currentPlayer)); // Victim
        currentTank.setHP(100); // Victim's HP is restored to full

        Projectiles p =new Projectiles(currentTank.getAcc_X(),currentTank.getHeight()*32+25,
                0,0, 1,'A',4); // A projectile is set above the victim's head

        app.projectiles.add(p);
        app.delay(500);

        assertEquals((int)app.score.get("A"),(int)(100-currentTank.getHP())); // Test if the score obtained is the opponent's lost HP

        // Recycle unused projectiles and scores to avoid subsequent effects
        app.delay(500);
        app.score.put("A",0);
        app.projectiles.remove(p);
    }


    /**
     * Test whether players can be removed correctly
     *
     * @param app The application instance.
     */
    public void testRemovePlayer(App app){

        Tank currentTank =app.grid.getTank(String.valueOf(app.currentPlayer));
        int preSize=app.currentAlivePlayer.size();

        currentTank.setHeight(19.5F); // Set the tank to self-destruct

        app.delay(1000);

        assertNotEquals(app.currentAlivePlayer.size(),preSize); // Check if the tank is still in the list of living players
    }

    /**
     * Test whether level switching works correctly
     *
     * @param app The application instance.
     */
    public void testChangeLevel(App app){
        app.changeLevel(1);
        assertEquals(app.currentLevel,1);

        app.changeLevel(-1);
        assertEquals(app.currentLevel,1);  // Below the boundary

        app.changeLevel(5);
        assertEquals(app.currentLevel,1);  // Beyond the boundary
    }


    /**
     * Test whether the game ends correctly
     *
     * @param app The application instance.
     */
    public void testGameOver(App app){
        ArrayList<Character> fakeAlivePlayer=new ArrayList<>();
        fakeAlivePlayer.add(app.currentAlivePlayer.get(0));
        app.currentAlivePlayer=fakeAlivePlayer;

        app.currentLevel=0;

        app.delay(1500);
        assertEquals(1,app.currentLevel); // The game will not end when not reaching the last level


        fakeAlivePlayer=new ArrayList<>();
        fakeAlivePlayer.add(app.currentAlivePlayer.get(0));
        app.currentAlivePlayer=fakeAlivePlayer;
        app.currentLevel= app.max_level;

        app.delay(1500);
        assertTrue(app.gameOver);  // The game will end when reaching the last level
    }

    /**
     * Test whether the game can restart correctly
     *
     * @param app The application instance.
     */
    public void testRestartGame(App app){
        KeyEvent keyEventPressed = new KeyEvent(null, 0, 0, 0, ' ', 87);
        app.gameOver=true;
        app.keyCode = 82;
        app.keyPressed(keyEventPressed);
        app.delay(800);
        assertEquals(app.currentLevel,0); // Reset
    }
}

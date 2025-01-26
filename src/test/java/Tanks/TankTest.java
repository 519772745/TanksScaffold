package Tanks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test functionality related to the Tank class.
 */
public class TankTest {

    /**
     * Test class for learning purposes.
     */
    @Test
    public void tankCreateTest() {
        Tank tank = new Tank('A', "random");
        assertEquals('A', tank.getPlayer()); //name
        assertEquals(100, tank.getHP()); //HP
        assertEquals(250, tank.getFuel()); //Fuel
        assertEquals(50, tank.getPower()); //Power

        assertTrue(tank.getR() >= 0 && tank.getR() <= 255);
        assertTrue(tank.getG() >= 0 && tank.getG() <= 255);
        assertTrue(tank.getB() >= 0 && tank.getB() <= 255);

        assertEquals(3, tank.getParachutes_num());
        assertFalse(tank.isUsing_parachutes());
        assertEquals(3, tank.getParachutes_num());
        assertEquals(0, tank.getLargerProjectile());


        assertEquals(15, tank.getTurretLength());
        assertEquals(0, tank.getTurretDegree());

        assertEquals(-1, tank.getTarget_height());
    }

    /**
     * Complete the set and get methods not tested in TestMainApp.
     */
    @Test
    public void tankParams(){
        Tank tank = new Tank('A', "255,0,0");

        tank.setAcc_X(2);
        assertEquals(2,  tank.getAcc_X());
        tank.setHeight(1.2F);
        assertEquals(1.2F,  tank.getHeight());

        /*-----------------
          HP
        -----------------*/
        tank.updateHP(101);
        assertEquals(0,  tank.getHP());
        tank.setHP(100);
        tank.updateHP(1);
        assertEquals(99,  tank.getHP());

         /*-----------------
          Fuel
        -----------------*/
        tank.setFuel(100);
        assertEquals(100,  tank.getFuel());
        /*-----------------
          Power
        -----------------*/
        tank.setPower(10);
        assertEquals(10,  tank.getPower());

        /*-----------------
          Tool
        -----------------*/
        tank.useParachutes();
        assertEquals(2,  tank.getParachutes_num());
        tank.setUsing_parachutes(true);
        assertTrue(tank.isUsing_parachutes());
        tank.setParachutes_num(1);
        assertEquals(1,  tank.getParachutes_num());

        tank.setLargerProjectile(2);
        assertEquals(2,  tank.getLargerProjectile());

        /*-----------------
          Drop
        -----------------*/
        tank.setDrop_height(10);
        assertEquals(10,  tank.getDrop_height());
        tank.setDrop_maker('c');
        assertEquals('c',  tank.getDrop_maker());
        tank.setTarget_height(10F,'c');
        assertEquals('c',  tank.getDrop_maker());

        /*-----------------
          Turret
        -----------------*/
        tank.setTurretDegree(1.5F);
        assertEquals(1.5F,  tank.getTurretDegree());
        tank.setTurretLength(1);
        assertEquals(1,  tank.getTurretLength());

    }

    /**
     * Test whether a tank can be generated with the given correct color.
     */
    @Test
    public void tankCreateWithGivenColor(){
        Tank tank = new Tank('A', "255,0,0");
        assertTrue(tank.getR() >= 0 && tank.getR() <= 255);
        assertTrue(tank.getG() >= 0 && tank.getG() <= 255);
        assertTrue(tank.getB() >= 0 && tank.getB() <= 255);
    }

    /**
     * Test whether a tank can be generated with the given incorrect color.
     */
    @Test
    public void tankCreateWithWrongColor(){
        Tank tank = new Tank('A', "255,0,0,0");
        assertTrue(tank.getR() >= 0 && tank.getR() <= 255);
        assertTrue(tank.getG() >= 0 && tank.getG() <= 255);
        assertTrue(tank.getB() >= 0 && tank.getB() <= 255);
    }
}

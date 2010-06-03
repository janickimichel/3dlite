package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Controller;
import au.com.zonski.space.domain.Body;
import au.com.zonski.space.invaders.core.Game;
import au.com.zonski.math.FixedMath;
import au.com.zonski.shape3d.Shape;
import au.com.zonski.shape3d.Composite;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 3/05/2005
 * Time: 14:13:08
 */
public class PlayerController extends BaseController implements Controller
{
    public static final byte LEFT   = 0x01 << 0;
    public static final byte RIGHT  = 0x01 << 1;
    public static final byte SHOOT  = 0x01 << 2;

    private static final byte DEFAULT_INVULNERABLE = 100;
    private static final byte FLASH_AMOUNT = 4;

    public static final int MAX_Z_ROTATION = FixedMath.toFixed(55);
    public static final int DIVISOR = 4;

    private static final Composite EMPTY = new Composite(0);

    private byte inputs;
    private byte read;

    private int zRotation;
    public Vector lives;
    private Ship currentLife;
    private byte invulnerable;
    private Shape originalShape;

    public PlayerController()
    {
        this.lives = new Vector(3);
    }

    public Ship getCurrentLife()
    {
        return currentLife;
    }

    public void setCurrentLife(Ship currentLife)
    {
        this.currentLife = currentLife;
    }

    public void update(Body body, int elapsedTime)
    {
        int zRotation = this.zRotation;
        Ship ship = (Ship)body;
        Level level = ship.getLevel();
        boolean left = isDown(LEFT);
        boolean right = isDown(RIGHT);
        boolean shoot = isDown(SHOOT);
        int speed = ship.speed;
        int[] center = ship.center;
        int radius = ship.shape.getMaxRadius();
        if(left && !right)
        {
            center[0] = Math.max(center[0] - FixedMath.multiply(speed, elapsedTime), level.getMinx() + radius);
            zRotation = Math.max(zRotation - (MAX_Z_ROTATION + zRotation)/DIVISOR,  -MAX_Z_ROTATION);
        }else if(right && !left){
            center[0] = Math.min(center[0] + FixedMath.multiply(speed, elapsedTime), level.getMaxx() - radius);
            zRotation = Math.min(zRotation + (MAX_Z_ROTATION - zRotation)/DIVISOR,  MAX_Z_ROTATION);
        }else{
            // gravitate to center
            if(zRotation != 0)
            {
                zRotation = zRotation - (zRotation/DIVISOR);
                if(zRotation > 0 && zRotation < FixedMath.DEFAULT_ONE)
                {
                    zRotation = 0;
                }else if(zRotation < 0 && zRotation > -FixedMath.DEFAULT_ONE){
                    zRotation = 0;
                }
            }
        }
        int y = center[1];
        int z = center[2];
        if(y != 0)
        {
            // zero in
            int diff;
            if(y < 0)
            {
                diff = Math.min(speed, -y);
            }else{
                diff = Math.max(-speed, -y);
            }
            center[1] += diff;
        }
        if(z != 0)
        {
            // zero in
            int diff;
            if(z < 0)
            {
                diff = Math.min(speed, -z);
            }else{
                diff = Math.max(-speed, -z);
            }
            center[2] += diff;
        }
        if(shoot)
        {
            ship.weapon.shoot(ship, ship.rotation);
        }
        if(this.zRotation != zRotation)
        {
            this.zRotation = zRotation;
            ship.rotation[0] = FixedMath.cos(zRotation/2);
            ship.rotation[1] = 0;
            ship.rotation[2] = 0;
            ship.rotation[3] = FixedMath.sin(zRotation/2);
        }
        if(this.invulnerable > 0)
        {
            this.invulnerable--;
            if(this.invulnerable % FLASH_AMOUNT > FLASH_AMOUNT/2)
            {
                // hide the ship
                ship.shape = EMPTY;
            }else{
                // show the ship
                ship.shape = this.originalShape;
            }
        }
    }

    public void addLife(Body life)
    {
        this.lives.addElement(life);
    }

    public void collision(Body controlled, Body with)
    {
        if(with.controller instanceof UpgradeController)
        {
            // upgrade our ship
            Ship upgradeShip = (Ship)with;
            Ship ship = (Ship)controlled;
            if(upgradeShip.getWeapon().equals(ship.getWeapon()))
            {
                ship.setPower(ship.getPower() + (2 * Ship.POWER_PER_UPGRADE) - (ship.getPower() % Ship.POWER_PER_UPGRADE+1));
            }else{
                ship.setWeapon(upgradeShip.getWeapon());
            }
        }else if(this.invulnerable <= 0){
            super.collision(controlled, with);
            if(this.currentLife.isRemove())
            {
                this.currentLife.controller = null;
                costLife(this.currentLife.getLevel(), this.currentLife.getSide());
            }
        }
    }

    public boolean peek(byte input)
    {
        return (this.inputs & input) == input || (this.read & input) == input;
    }

    public boolean isDown(byte input)
    {
        boolean result;
        if((this.read & input) != 0)
        {
            this.read = (byte)(this.read & ~input);
            result = true;
        }else{
            result = ((this.inputs & input) != 0);
        }
        return result;
    }

    public void setDown(byte input)
    {
        this.inputs |= input;
        this.read |= input;
    }

    public void setUp(byte input)
    {
        this.inputs = (byte)(inputs & ~input);
    }

    public Ship costLife(Level target, byte side)
    {
        Ship life;
        if(this.lives.size() > 0)
        {
            life = (Ship)this.lives.elementAt(0);
            this.lives.removeElementAt(0);
            target.addBody(life, side);
            life.controller = this;
            this.zRotation = 0;
            if(this.currentLife != null)
            {
                life.center[0] = this.currentLife.center[0];
            }
            //life.center[1] = target.getHeight()/2;
            life.center[2] = target.getHeight()/2;
            this.currentLife = life;
            this.invulnerable = DEFAULT_INVULNERABLE;
            this.originalShape = life.shape;
        }else{
            life = null;
        }
        return life;
    }

    public Controller copy()
    {
        //return new InputController();
        PlayerController controller = new PlayerController();
        for(int i=this.lives.size(); i>0; )
        {
            i--;
            Body life = (Body)this.lives.elementAt(i);
            Body copy = life.copy();
            controller.addLife(copy);
        }
        return controller;
    }
}

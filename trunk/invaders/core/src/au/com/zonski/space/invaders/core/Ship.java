package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.Body;
import au.com.zonski.math.FixedMath;
import au.com.zonski.shape3d.Shape;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 3/05/2005
 * Time: 14:13:30
 */
public class Ship extends Body
{
    public static final int POWER_PER_UPGRADE = 100;

    int speed;
    byte health;
    byte preCollisionHealth;
    byte side;
    Weapon weapon;
    Level level;
    int power;
    byte typeId;

    public Ship()
    {
        this(null, new int[3], new int[]{FixedMath.DEFAULT_ONE, 0, 0, 0, FixedMath.DEFAULT_PRECISION}, 0);
    }

    public Ship(Shape shape, int[] center, int[] rotation, int speed)
    {
        super(shape, center, rotation);
        this.speed = speed;
    }

    public void update(int multiplier)
    {
        super.update(multiplier);
        if(this.weapon != null)
        {
            this.weapon.update(multiplier);
        }
    }

    public Level getLevel()
    {
        return level;
    }

    public void setLevel(Level level)
    {
        this.level = level;
    }

    public byte getTypeId()
    {
        return typeId;
    }

    public void setTypeId(byte typeId)
    {
        this.typeId = typeId;
    }

    public Weapon getWeapon()
    {
        return this.weapon;
    }

    public void setWeapon(Weapon weapon)
    {
        this.weapon = weapon;
    }

    public int getPower()
    {
        return power;
    }

    public void setPower(int power)
    {
        this.power = Math.max(0, power);
    }

    public int getUpgrades()
    {
        return this.power / POWER_PER_UPGRADE;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public byte getHealth()
    {
        return health;
    }

    public void setHealth(byte health)
    {
        this.health = health;
    }

    public byte getSide()
    {
        return side;
    }

    public void setSide(byte side)
    {
        this.side = side;
    }

    public Body copy()
    {
        Ship ship = new Ship();
        super.copyInto(ship);
        if(this.weapon != null)
        {
            ship.weapon = this.weapon.copy();
        }
        ship.speed = this.speed;
        ship.health = this.health;
        ship.power = this.power;
        ship.typeId = this.typeId;
        return ship;
    }
}

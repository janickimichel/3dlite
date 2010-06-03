package au.com.zonski.space.invaders.core;

import au.com.zonski.space.domain.SolarSystem;
import au.com.zonski.space.domain.Body;
import au.com.zonski.space.invaders.core.Ship;
import au.com.zonski.shape3d.Mesh;
import au.com.zonski.math.MatrixMath;
import au.com.zonski.math.FixedMath;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 3/05/2005
 * Time: 14:14:42
 */
public class Weapon
{
    protected Body projectile;
    private int timeBetweenShots;
    private int timeRemaining;
    protected byte maxUpgrades;
    protected byte cost;
    protected byte upgradeTypeId;

    public Weapon()
    {

    }

    public byte getMaxUpgrades()
    {
        return maxUpgrades;
    }

    public void setMaxUpgrades(byte maxUpgrades)
    {
        this.maxUpgrades = maxUpgrades;
    }

    public byte getUpgradeTypeId()
    {
        return upgradeTypeId;
    }

    public void setUpgradeTypeId(byte upgradeTypeId)
    {
        this.upgradeTypeId = upgradeTypeId;
    }

    public byte getCost()
    {
        return cost;
    }

    public void setCost(byte cost)
    {
        this.cost = cost;
    }

    public int getTimeBetweenShots()
    {
        return timeBetweenShots;
    }

    public void setTimeBetweenShots(int timeBetweenShots)
    {
        this.timeBetweenShots = timeBetweenShots;
    }

    public Body getProjectile()
    {
        return projectile;
    }

    public void setProjectile(Body projectile)
    {
        this.projectile = projectile;
    }

    public boolean canShoot()
    {
        return this.timeRemaining <= 0;
    }

    public void shoot(Ship owner, int[] direction)
    {
        if(canShoot())
        {
            this.timeRemaining = this.timeBetweenShots;
            int result = doShoot(owner, owner.getUpgrades(), direction);
            owner.setPower(owner.getPower() - result);
        }
    }

    protected int doShoot(Ship owner, int upgrades, int[] direction)
    {
        Ship shot = (Ship)this.projectile.copy();
        //this.timeRemaining = this.timeBetweenShots / upgrades;
        return doShoot(owner, upgrades, direction, shot);
    }

    protected int doShoot(Ship owner, int upgrades, int[] direction, Ship shot)
    {
        Level level = owner.getLevel();
        // place the shot the max-radius away from the owner facing in the same direction as the owner
        int[] center = owner.center;
        int maxradius = owner.shape.getMaxRadius() + shot.shape.getMaxRadius();
        int[] points = new int[]{0, 0, -maxradius};
        int[][] rotationMatrix = new int[3][3];
        MatrixMath.getRotationMatrix(rotationMatrix, direction);
        MatrixMath.multiplyVector(shot.center, points, rotationMatrix);

        shot.center[0] += center[0];
        shot.center[1] += center[1];
        shot.center[2] += center[2];

        System.arraycopy(direction,  0, shot.rotation, 0, owner.rotation.length);

        level.addBody(shot, owner.getSide());
        return this.cost;
    }

    public void update(int elapsedTime)
    {
        this.timeRemaining -= FixedMath.multiply(elapsedTime, FixedMath.DEFAULT_ONE);
    }

    public Weapon copy()
    {
        Weapon weapon = new Weapon();
        copyInto(weapon);
        return weapon;
    }

    public void copyInto(Weapon weapon)
    {
        weapon.projectile = this.projectile;
        weapon.timeBetweenShots = this.timeBetweenShots;
        weapon.upgradeTypeId = this.upgradeTypeId;
        weapon.setMaxUpgrades(this.maxUpgrades);
        weapon.setCost(this.cost);
    }

    public boolean equals(Object o)
    {
        boolean b;
        if(o != null && o.getClass().equals(this.getClass()))
        {
            b = equals((Weapon)o);
        }else{
            b = false;
        }
        return b;
    }

    public boolean equals(Weapon w)
    {
        return w.projectile == this.projectile;
    }

    public int hashCode()
    {
        return this.projectile.hashCode();
    }
}

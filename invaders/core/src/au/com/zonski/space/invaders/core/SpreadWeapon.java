package au.com.zonski.space.invaders.core;

import au.com.zonski.math.FixedMath;
import au.com.zonski.math.MatrixMath;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 18/05/2005
 * Time: 15:15:58
 */
public class SpreadWeapon extends Weapon
{
    private int spreadAngle;

    public SpreadWeapon()
    {

    }

    public int getSpreadAngle()
    {
        return spreadAngle;
    }

    public void setSpreadAngle(int spreadAngle)
    {
        this.spreadAngle = spreadAngle;
    }

    public void doUpgrade()
    {
    }

    protected int doShoot(Ship owner, int upgrades, int[] direction)
    {
        int cost = 0;
        int projectiles = Math.min(upgrades, this.maxUpgrades) + 1;
//        if(projectiles % 2 == 0)
//        {
            // add in two forward facing projectiles
//            cost += doShoot(owner, upgrades, direction, 2);
//            projectiles -= 2;
//        }
        int startAngle = - (this.spreadAngle * (projectiles - 1))/2;
        for(int i=projectiles; i>0; )
        {
            i--;
            int angle = startAngle + (i * this.spreadAngle);
            int[] rotation = new int[]{FixedMath.cos(angle/2), 0, FixedMath.sin(angle/2), 0, FixedMath.DEFAULT_PRECISION};
            MatrixMath.multiplyQuaternion(rotation, direction, rotation);

            Ship shot = (Ship)this.projectile.copy();
            // place the shot the max-radius away from the owner facing in the same direction as the owner
//            int[] center = owner.center;
//            int maxradius = owner.shape.getMaxRadius() + shot.shape.getMaxRadius();
//            int[] points = new int[]{0, 0, -maxradius};
//            int[][] rotationMatrix = new int[3][3];
//            MatrixMath.getRotationMatrix(rotationMatrix, rotation);
//            MatrixMath.multiplyVector(shot.center, points, rotationMatrix);
//
//            shot.center[0] += center[0];
//            shot.center[1] += center[1];
//            shot.center[2] += center[2];
//
//            System.arraycopy(rotation,  0, shot.rotation, 0, owner.rotation.length);
//
//            level.addBody(shot, owner.getSide());
            cost += doShoot(owner, upgrades, rotation, shot);
        }
        return cost;
    }

    public Weapon copy()
    {
        SpreadWeapon weapon = new SpreadWeapon();
        copyInto(weapon);
        weapon.setSpreadAngle(this.spreadAngle);
        return weapon;
    }
}

package au.com.zonski.space.invaders.core;

import au.com.zonski.math.MatrixMath;
import au.com.zonski.math.FixedMath;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 18/05/2005
 * Time: 14:44:56
 */
public class MultiWeapon extends Weapon
{
    public MultiWeapon()
    {
    }

    public void doUpgrade()
    {
    }

    protected int doShoot(Ship owner, int upgrades, int[] direction)
    {
        int projectiles = Math.min(upgrades, this.maxUpgrades) + 1;
        return doShoot(owner, upgrades, direction, projectiles);
    }

    protected int doShoot(Ship owner, int upgrades, int[] direction, int projectiles)
    {
        Level level = owner.getLevel();
        int adj;
        if(projectiles == 2)
        {
            adj = 0;
        }else{
            adj = -90;
        }
        for(int i=projectiles; i>0; )
        {
            i--;
            Ship shot = (Ship)this.projectile.copy();
            // place the shot the max-radius away from the owner facing in the same direction as the owner
            int[] center = owner.center;
            int maxradius = owner.shape.getMaxRadius() + shot.shape.getMaxRadius();
            int minradius = owner.shape.getMaxRadius();

            int dx, dy;
            if(projectiles != 1)
            {
                int angle = FixedMath.toFixed((i*360) / projectiles + adj);
                dx = FixedMath.multiply(FixedMath.cos(angle), minradius);
                dy = FixedMath.multiply(FixedMath.sin(angle), minradius);
            }else{
                dx = 0;
                dy = 0;
            }

            int[] points = new int[]{dx, dy, -maxradius};
            int[][] rotationMatrix = new int[3][3];
            MatrixMath.getRotationMatrix(rotationMatrix, direction);
            MatrixMath.multiplyVector(shot.center, points, rotationMatrix);

            shot.center[0] += center[0];
            shot.center[1] += center[1];
            shot.center[2] += center[2];

            System.arraycopy(direction,  0, shot.rotation, 0, owner.rotation.length);

            level.addBody(shot, owner.getSide());
        }
        return projectiles * this.getCost();
    }

    public Weapon copy()
    {
        MultiWeapon multi = new MultiWeapon();
        copyInto(multi);
        return multi;
    }
}

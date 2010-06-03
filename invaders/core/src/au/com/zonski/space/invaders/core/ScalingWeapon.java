package au.com.zonski.space.invaders.core;

import au.com.zonski.shape3d.Composite;
import au.com.zonski.shape3d.Mesh;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 18/05/2005
 * Time: 20:04:08
 */
public class ScalingWeapon extends Weapon
{
    public ScalingWeapon()
    {
    }

    protected int doShoot(Ship owner, int upgrades, int[] direction)
    {
        Ship shot = (Ship)this.projectile.copy();
        // TODO : handle more shape types
        // scale the size of the mesh
        Composite composite = (Composite)shot.getShape();
        Composite scaled = new Composite(composite.shapes.size());
        int scale = Math.min(upgrades + 1,  this.maxUpgrades);
        scaled.maxradius = composite.maxradius * scale;
        for(int i=0; i<composite.shapes.size(); i++)
        {
            Mesh mesh = (Mesh)composite.shapes.elementAt(i);
            Mesh copy = mesh.copy();
            for(int j=copy.baseMatrix.length; j>0; )
            {
                j--;
                int[] points = copy.baseMatrix[j];
                for(int k=points.length; k>0; )
                {
                    k--;
                    points[k] *= scale;
                }
            }
            scaled.shapes.addElement(copy);
        }
        shot.setShape(scaled);

        shot.setHealth((byte)(shot.getHealth() * scale));
        shot.setSpeed(shot.getSpeed() * scale);
        return scale * doShoot(owner, upgrades, direction, shot);
    }

    public Weapon copy()
    {
        ScalingWeapon weapon = new ScalingWeapon();
        copyInto(weapon);
        return weapon;
    }
}

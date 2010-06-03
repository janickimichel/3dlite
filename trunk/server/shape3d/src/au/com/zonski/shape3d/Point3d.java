package au.com.zonski.shape3d;

import au.com.zonski.math.FixedMath;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 5/05/2005
 * Time: 15:28:34
 */
public class Point3d
{
    private int x;
    private int y;
    private int z;

    public Point3d()
    {
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getZ()
    {
        return z;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    public int[] toArray()
    {
        return new int[]{FixedMath.toFixed(this.x), FixedMath.toFixed(this.y), FixedMath.toFixed(this.z)};
    }
}

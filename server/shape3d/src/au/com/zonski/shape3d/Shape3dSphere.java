package au.com.zonski.shape3d;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 5/05/2005
 * Time: 14:34:33
 */
public class Shape3dSphere extends Sphere
{
    public Shape3dSphere()
    {
        this(0);
    }

    public Shape3dSphere(int radius)
    {
        super(radius);
    }

    public int getMaxRadius()
    {
        return this.radius;
    }

    public int getRadius()
    {
        return this.radius;
    }

    public void setRadius(int radius)
    {
        this.radius = radius;
    }
}

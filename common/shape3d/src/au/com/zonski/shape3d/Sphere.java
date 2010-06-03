package au.com.zonski.shape3d;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 29/03/2005
 * Time: 10:49:44
 */
public class Sphere implements Shape
{
    public int radius;

    public Sphere(int radius)
    {
        this.radius = radius;
    }

    public int getMaxRadius()
    {
        return this.radius;
    }
}

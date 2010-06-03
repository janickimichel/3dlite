package au.com.zonski.shape3d;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 5/05/2005
 * Time: 14:36:46
 * <p>
 * A shape containing other shapes
 */
public class Composite implements Shape
{
    public Vector shapes;
    public int maxradius;

    public Composite(int size)
    {
        this.shapes = new Vector(size);
    }

    public int getMaxRadius()
    {
        return this.maxradius;
    }
}
